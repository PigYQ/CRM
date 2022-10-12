package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.pojo.ReturnObject;
import com.bjpowernode.crm.commons.utils.Constant;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.HSSFUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.mapper.UserMapper;
import com.bjpowernode.crm.settings.pojo.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.pojo.Activity;
import com.bjpowernode.crm.workbench.pojo.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import com.bjpowernode.crm.workbench.service.ActivityService;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFCellUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

@Controller
public class ActivityController {

    @Autowired
    private UserService userService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ActivityRemarkService activityRemarkService;

    @RequestMapping("/workbench/activity/index.do")
    public String index(HttpServletRequest request){
        List<User> userList = userService.queryAllUser();
        request.setAttribute("userList",userList);
        return "workbench/activity/index";
    }

    @RequestMapping("/workbench/activity/saveCreateActivity.do")
    @ResponseBody
    public Object saveCreateActivity(Activity activity, HttpSession session){
        //封装数据
        activity.setId(UUIDUtils.getId());
        activity.setCreateTime(DateUtils.formatDateTime(new Date()));
        User user = (User) session.getAttribute(Constant.USER_INFO);
        activity.setCreateBy(user.getId());

        ReturnObject returnObject = new ReturnObject();
        //调用业务层方法
        try{
            int i = activityService.saveCreateActivity(activity);
            if (i>0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setMsg("添加成功");
            }else {
                returnObject.setMsg("系统繁忙，请稍后重试");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setMsg("系统繁忙，请稍后重试");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/queryActivityNums.do")
    @ResponseBody
    public Object queryActivityNums(String owner,String name,String startDate,String endDate,
                                    int pageNo,int pageSize){
        //封装参数
        Map<String,Object> map = new HashMap<>();
        map.put("owner",owner);
        map.put("name",name);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("beginNo",(pageNo-1)*pageSize);
        map.put("pageSize",pageSize);
        //调service层方法
        int totalRows = activityService.queryActivityNums(map);
        List<Activity> activities = activityService.queryActivityByConditionForPage(map);

        //返回响应信息
        Map<String,Object> retMap = new HashMap<>();
        retMap.put("totalRows",totalRows);
        retMap.put("activityList",activities);
        return retMap;
    }

    @RequestMapping("/workbench/activity/deleteActivityByIds.do")
    @ResponseBody
    public Object deleteActivityByIds(String[] id){
        System.out.println(Arrays.toString(id));
        ReturnObject returnObject = new ReturnObject();
        try {
            int res = activityService.deleteByIds(id);
            if (res>0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMsg("系统繁忙，请稍后重试");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMsg("系统繁忙，请稍后重试");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/queryActivityById.do")
    @ResponseBody
    public Activity queryActivityById(String id){
        //调用service，返回结果
        return activityService.queryActivityById(id);
    }

    @RequestMapping("/workbench/activity/updateActivityById.do")
    @ResponseBody
    public Object updateActivityById(Activity activity,HttpSession session){
        ReturnObject returnObject = new ReturnObject();
        //封装参数
        String editTime = DateUtils.formatDateTime(new Date());
        User user = (User) session.getAttribute(Constant.USER_INFO);
        String editBy = user.getId();
        activity.setEditBy(editBy);
        activity.setEditTime(editTime);
        try {
            int res = activityService.updateActivityById(activity);
            if (res>0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
                returnObject.setMsg("系统繁忙，请稍后重试");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMsg("系统繁忙，请稍后重试");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/activity/exportAllActivity.do")
    public void exportAllActivity(HttpServletResponse response) throws IOException {
        //查询所有市场活动
        List<Activity> activities = activityService.queryAllActivity();
        //生成excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("市场活动列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("所有者");
        cell = row.createCell(2);
        cell.setCellValue("名称");
        cell = row.createCell(3);
        cell.setCellValue("开始时间");
        cell = row.createCell(4);
        cell.setCellValue("结束时间");
        cell = row.createCell(5);
        cell.setCellValue("成本");
        cell = row.createCell(6);
        cell.setCellValue("描述");
        cell = row.createCell(7);
        cell.setCellValue("创建时间");
        cell = row.createCell(8);
        cell.setCellValue("创建人");
        cell = row.createCell(9);
        cell.setCellValue("修改时间");
        cell = row.createCell(10);
        cell.setCellValue("修改者");

        if (activities!=null && activities.size()>0){
            Activity activity = null;
            for (int i=1;i<=activities.size();i++){
                activity = activities.get(i-1);
                //创建行
                row = sheet.createRow(i);
                //每行11列
                cell = row.createCell(0);
                cell.setCellValue(activity.getId());
                cell = row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell = row.createCell(2);
                cell.setCellValue(activity.getName());
                cell = row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell = row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell = row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell = row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell = row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell = row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell = row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell = row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }
        }
        //设置响应信息格式和响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition","attachment;filename=activities.xls");
        //获取输出流
        OutputStream os = response.getOutputStream();
        //将excel文件写到输出流上
        workbook.write(os);
        //关闭资源
        workbook.close();
        os.flush();
    }

    @RequestMapping("/workbench/activity/exportActivityByIds.do")
    public void exportActivityByIds(String[] id,HttpServletResponse response) throws IOException {
        //查询所有市场活动
        List<Activity> activities = activityService.queryByIds(id);
        //生成excel文件
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet("市场活动列表");
        HSSFRow row = sheet.createRow(0);
        HSSFCell cell = row.createCell(0);
        cell.setCellValue("ID");
        cell = row.createCell(1);
        cell.setCellValue("所有者");
        cell = row.createCell(2);
        cell.setCellValue("名称");
        cell = row.createCell(3);
        cell.setCellValue("开始时间");
        cell = row.createCell(4);
        cell.setCellValue("结束时间");
        cell = row.createCell(5);
        cell.setCellValue("成本");
        cell = row.createCell(6);
        cell.setCellValue("描述");
        cell = row.createCell(7);
        cell.setCellValue("创建时间");
        cell = row.createCell(8);
        cell.setCellValue("创建人");
        cell = row.createCell(9);
        cell.setCellValue("修改时间");
        cell = row.createCell(10);
        cell.setCellValue("修改者");

        if (activities!=null && activities.size()>0){
            Activity activity = null;
            for (int i=1;i<=activities.size();i++){
                activity = activities.get(i-1);
                //创建行
                row = sheet.createRow(i);
                //每行11列
                cell = row.createCell(0);
                cell.setCellValue(activity.getId());
                cell = row.createCell(1);
                cell.setCellValue(activity.getOwner());
                cell = row.createCell(2);
                cell.setCellValue(activity.getName());
                cell = row.createCell(3);
                cell.setCellValue(activity.getStartDate());
                cell = row.createCell(4);
                cell.setCellValue(activity.getEndDate());
                cell = row.createCell(5);
                cell.setCellValue(activity.getCost());
                cell = row.createCell(6);
                cell.setCellValue(activity.getDescription());
                cell = row.createCell(7);
                cell.setCellValue(activity.getCreateTime());
                cell = row.createCell(8);
                cell.setCellValue(activity.getCreateBy());
                cell = row.createCell(9);
                cell.setCellValue(activity.getEditTime());
                cell = row.createCell(10);
                cell.setCellValue(activity.getEditBy());
            }
        }
        //设置响应信息格式和响应头
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition","attachment;filename=activities.xls");
        //获取输出流
        OutputStream os = response.getOutputStream();
        //将excel文件写到输出流上
        workbook.write(os);
        //关闭资源
        workbook.close();
        os.flush();
    }

    @RequestMapping("/workbench/activity/importActivity.do")
    @ResponseBody
    public Object importActivity(MultipartFile activityFile,HttpSession session) throws IOException {
        User user = (User) session.getAttribute(Constant.USER_INFO);
        ReturnObject returnObject = new ReturnObject();
        try {
            //解析excel文件
            InputStream is = activityFile.getInputStream();
            HSSFWorkbook workbook = new HSSFWorkbook(is);
            HSSFSheet sheet = workbook.getSheetAt(0);
            HSSFRow row = null;
            HSSFCell cell = null;
            Activity activity = null;
            List<Activity> activityList = new ArrayList<>();
            for(int i=1;i<=sheet.getLastRowNum();i++){
                row = sheet.getRow(i);
                activity = new Activity();
                activity.setId(UUIDUtils.getId());
                activity.setOwner(user.getId());
                activity.setCreateTime(DateUtils.formatDateTime(new Date()));
                activity.setCreateBy(user.getId());
                for (int j=0;j<row.getLastCellNum();j++){
                    cell = row.getCell(j);
                    String cellValue = HSSFUtils.getCellValue(cell);
                    if (j==0){
                        activity.setName(cellValue);
                    }else if (j==1){
                        activity.setStartDate(cellValue);
                    }else if (j==2){
                        activity.setEndDate(cellValue);
                    }else if (j==3){
                        activity.setCost(cellValue);
                    }else if (j==4){
                        activity.setDescription(cellValue);
                    }
                }
                activityList.add(activity);
            }
            int res = activityService.saveImportActivity(activityList);
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            returnObject.setMsg("成功导入"+res+"条记录");
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_FAIL);
            returnObject.setMsg("系统繁忙，请稍后重试");
        }
        return returnObject;
    }

    @RequestMapping("workbench/activity/detailActivity.do")
    public String detailActivity(String id, Model model){
        //查询数据
        Activity activity = activityService.queryActivityDetailById(id);
        List<ActivityRemark> activityRemarks = activityRemarkService.queryActivityRemarkByActivityId(id);
        //将数据保存到model中
        model.addAttribute("activity",activity);
        model.addAttribute("activityRemarks",activityRemarks);
        return "workbench/activity/detail";
    }

}
