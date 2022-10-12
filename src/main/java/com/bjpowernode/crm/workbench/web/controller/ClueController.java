package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.pojo.ReturnObject;
import com.bjpowernode.crm.commons.utils.Constant;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.pojo.User;
import com.bjpowernode.crm.settings.service.UserService;
import com.bjpowernode.crm.workbench.pojo.*;
import com.bjpowernode.crm.workbench.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class ClueController {

    @Autowired
    private UserService userService;

    @Autowired
    private DicValueService dicValueService;

    @Autowired
    private ClueService clueService;

    @Autowired
    private ClueRemarkService clueRemarkService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ClueActivityRelationService clueActivityRelationService;

    @RequestMapping("/workbench/clue/index.do")
    public String index(Model model){
        //查询数据
        List<User> userList = userService.queryAllUser();
        List<DictionaryValue> appellation = dicValueService.queryDicValueByTypeCode("appellation");
        List<DictionaryValue> clueState = dicValueService.queryDicValueByTypeCode("clueState");
        List<DictionaryValue> source = dicValueService.queryDicValueByTypeCode("source");
        //将数据放到域中
        model.addAttribute("userList",userList);
        model.addAttribute("appellation",appellation);
        model.addAttribute("clueState",clueState);
        model.addAttribute("source",source);
        return "workbench/clue/index";
    }

    @RequestMapping("/workbench/clue/queryClue.do")
    @ResponseBody
    public Object queryClue(Clue clue, int pageNo, int pageSize){
        //封装参数
        Map<String,Object> map = new HashMap<>();
        String fullName = clue.getFullname();
        String company = clue.getCompany();
        String phone = clue.getPhone();
        String source = clue.getSource();
        String owner = clue.getOwner();
        String mphone = clue.getMphone();
        String state = clue.getState();
        map.put("state",state);
        map.put("mphone",mphone);
        map.put("owner",owner);
        map.put("source",source);
        map.put("phone",phone);
        map.put("company",company);
        map.put("fullname",fullName);
        map.put("beginNo",(pageNo-1)*pageSize);
        map.put("pageSize",pageSize);

        //查询线索集
        List<Clue> clueList = clueService.queryClueForPage(map);
        System.out.println("==========================================================");
        System.out.println(clueList);
        //查询总条数
        int totalRows = clueService.queryClueNums(map);

        //响应给前端的对象
        Map<String,Object> retMap = new HashMap<>();
        //放入线索集
        retMap.put("clueList",clueList);
        //放入总线索数
        retMap.put("totalRows",totalRows);
        return retMap;
    }

    @RequestMapping("/workbench/clue/createClue.do")
    @ResponseBody
    public Object createClue(Clue clue, HttpSession session){
        ReturnObject returnObject = new ReturnObject();
        //封装参数
        //id
        clue.setId(UUIDUtils.getId());
        //创建时间
        clue.setCreateTime(DateUtils.formatDateTime(new Date()));
        //创建人
        User user = (User) session.getAttribute(Constant.USER_INFO);
        clue.setCreateBy(user.getId());

        //调用service
        try {
            int res = clueService.saveClue(clue);
            if (res>0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setObj(clue);
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

    @RequestMapping("/workbench/clue/deleteClueByIds.do")
    @ResponseBody
    public Object deleteClueByIds(String[] id){
        ReturnObject returnObject = new ReturnObject();
        System.out.println("==============================");
        System.out.println("ids:\n"+Arrays.toString(id));
        try {
            //调用service删除线索
            int res = clueService.deleteByIds(id);
            //返回结果
            if (res>0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setMsg("系统繁忙，请稍后重试");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setMsg("系统繁忙，请稍后重试");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/clue/queryClueById.do")
    @ResponseBody
    public Object queryClueById(String id){
        return clueService.queryClueById(id);
    }

    @RequestMapping("/workbench/clue/updateClueById.do")
    @ResponseBody
    public Object updateClueById(Clue clue,HttpSession session){
        ReturnObject returnObject = new ReturnObject();
        //封装参数
        //修改时间
        clue.setEditTime(DateUtils.formatDateTime(new Date()));
        //修改人
        User user = (User) session.getAttribute(Constant.USER_INFO);
        clue.setEditBy(user.getId());
        try {
            //调用service修改
            int res = clueService.updateById(clue);
            if (res>0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setMsg("系统繁忙，请稍后再试");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setMsg("系统繁忙，请稍后再试");
        }
        //响应结果
        return returnObject;
    }

    @RequestMapping("/workbench/clue/toDetail.do")
    public String toDetail(String id,Model model){
        //根据id查询线索详情
        Clue clue = clueService.queryByIdUnion(id);
        //备注
        List<ClueRemark> clueRemarkList = clueRemarkService.queryClueRemarkByClueId(id);
        //关联的市场活动
        List<Activity> activityList = activityService.queryActivityByClueId(id);
        //响应到前端
        model.addAttribute("clue",clue);
        model.addAttribute("clueRemarkList",clueRemarkList);
        model.addAttribute("activityList",activityList);
        return "workbench/clue/detail";
    }

    @RequestMapping("/workbench/clue/saveClueRemark.do")
    @ResponseBody
    public Object saveClueRemark(ClueRemark remark,HttpSession session){
        User user = (User)session.getAttribute(Constant.USER_INFO);
        ReturnObject returnObject = new ReturnObject();
        //封装参数
        remark.setId(UUIDUtils.getId());
        remark.setCreateBy(user.getId());
        remark.setCreateTime(DateUtils.formatDateTime(new Date()));
        remark.setEditFlag(Constant.ACTIVITY_REMARK_EDITED_NO);
        try{
            //调用业务
            int res = clueRemarkService.saveClueRemark(remark);
            if (res>0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setObj(remark);
            }else {
                returnObject.setMsg("系统繁忙，请稍后再试");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setMsg("系统繁忙，请稍后再试");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/clue/queryActivityByClueIdAndActivityName.do")
    @ResponseBody
    public Object queryActivityByClueIdAndActivityName(String activityName,String clueId){
        Map<String,Object> map = new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);
        return activityService.queryActivityByClueIdAndActivityName(map);
    }

    @RequestMapping("/workbench/clue/saveRelation.do")
    @ResponseBody
    public Object saveRelation(String[] activityId,String clueId){
        ReturnObject returnObject = new ReturnObject();
        ClueActivityRelation relation = null;
        List<ClueActivityRelation> list = new ArrayList<>();
        for (String id : activityId) {
            relation = new ClueActivityRelation();
            relation.setId(UUIDUtils.getId());
            relation.setClueId(clueId);
            relation.setActivityId(id);
            list.add(relation);
        }
        try {
            int res = clueActivityRelationService.saveRelation(list);
            //关联的市场活动
            List<Activity> activityList = activityService.queryActivityByClueId(clueId);
            if (res>0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setObj(activityList);
            }else {
                returnObject.setMsg("系统繁忙，请稍后再试");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setMsg("系统繁忙，请稍后再试");
        }
        return returnObject;
    }

    @RequestMapping("/workbench/clue/deleteByClueIdAndActivityId.do")
    @ResponseBody
    public Object deleteByClueIdAndActivityId(String activityId,String clueId){
        ReturnObject returnObject = new ReturnObject();
        try {
            int res = clueActivityRelationService.deleteByClueIdAndActivityId(activityId, clueId);
            if (res>0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
            }else {
                returnObject.setMsg("系统繁忙，请稍后尝试");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setMsg("系统繁忙，请稍后尝试");
        }
        //System.out.println(returnObject.getMsg()+"/n"+returnObject.getCode());
        return returnObject;
    }

    @RequestMapping("/workbench/clue/toConvert.do")
    public String toConvert(String clueId,Model model){
        //根据线索id查询线索详情
        Clue clue = clueService.queryByIdUnion(clueId);
        List<DictionaryValue> stageList = dicValueService.queryDicValueByTypeCode("stage");
        model.addAttribute("clue",clue);
        model.addAttribute("stageList",stageList);
        return "workbench/clue/convert";
    }

    @RequestMapping("/workbench/clue/queryHaveActivityByClueIdAndActivityName.do")
    @ResponseBody
    public Object queryHaveActivityByClueIdAndActivityName(String activityName,String clueId){
        Map<String,Object> map = new HashMap<>();
        map.put("activityName",activityName);
        map.put("clueId",clueId);
        return activityService.queryHaveActivityByClueIdAndActivityName(map);
    }

    @RequestMapping("/workbench/clue/convertClue.do")
    @ResponseBody
    public Object convertClue(String clueId,String money,String name,String expectedDate,String stage,String activityId,String isCreate,HttpSession session){
        System.err.println("线索id:"+clueId+"金额："+money+"名称:"+name+"成功时间："+expectedDate+"状态："+stage+"市场活动id："+activityId+"是否创建交易："+isCreate);
        Map<String,Object> map = new HashMap<>();
        User user = (User)session.getAttribute(Constant.USER_INFO);
        map.put("clueId",clueId);
        map.put("money",money);
        map.put("name",name);
        map.put("expectedDate",expectedDate);
        map.put("stage",stage);
        map.put("activityId",activityId);
        map.put("isCreate",isCreate);
        map.put(Constant.USER_INFO,user);

        ReturnObject returnObject = new ReturnObject();
        try {
            clueService.saveConvert(map);
            returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setMsg("系统繁忙，请稍后重试");
        }
        return returnObject;
    }
}
