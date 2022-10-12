package com.bjpowernode.crm.workbench.web.controller;

import com.bjpowernode.crm.commons.pojo.ReturnObject;
import com.bjpowernode.crm.commons.utils.Constant;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.commons.utils.UUIDUtils;
import com.bjpowernode.crm.settings.pojo.User;
import com.bjpowernode.crm.workbench.pojo.ActivityRemark;
import com.bjpowernode.crm.workbench.service.ActivityRemarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.Date;

@Controller
public class ActivityRemarkController {

    @Autowired
    private ActivityRemarkService activityRemarkService;


    @RequestMapping("/workbench/activity/saveCreateActivityRemark.do")
    @ResponseBody
    public Object saveCreateActivityRemark(ActivityRemark remark, HttpSession session){
        ReturnObject returnObject = new ReturnObject();
        User user = (User) session.getAttribute(Constant.USER_INFO);
        //封装参数
        remark.setId(UUIDUtils.getId());
        remark.setCreateBy(user.getId());
        remark.setCreateTime(DateUtils.formatDateTime(new Date()));
        remark.setEditFlag(Constant.ACTIVITY_REMARK_EDITED_NO);
        try {
            int res = activityRemarkService.saveCreateActivityRemark(remark);
            if (res>0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setObj(remark);
            }else {
                returnObject.setMsg("系统繁忙，请稍后重试");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setMsg("系统繁忙，请稍后重试");
        }
        ActivityRemark remark1 = (ActivityRemark) returnObject.getObj();
        System.out.println(remark1.getCreateTime());
        return returnObject;
    }

    @RequestMapping("/workbench/activity/deleteActivityRemarkById.do")
    @ResponseBody
    public Object deleteActivityRemarkById(String id){
        ReturnObject returnObject = new ReturnObject();
        try{
            int res = activityRemarkService.deleteActivityRemarkById(id);
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

    @RequestMapping("/workbench/activity/saveActivityRemarkById.do")
    @ResponseBody
    public Object saveActivityRemarkById(ActivityRemark activityRemark,HttpSession session){
        ReturnObject returnObject = new ReturnObject();
        User user= (User)session.getAttribute(Constant.USER_INFO);
        try {
            //封装参数
            activityRemark.setEditTime(DateUtils.formatDateTime(new Date()));
            activityRemark.setEditBy(user.getId());
            activityRemark.setEditFlag(Constant.ACTIVITY_REMARK_EDITED_YES);
            //调service
            int res = activityRemarkService.saveActivityRemarkById(activityRemark);
            if (res>0){
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                returnObject.setObj(activityRemark);
            }else {
                returnObject.setMsg("系统繁忙，请稍后重试");
            }
        }catch (Exception e){
            e.printStackTrace();
            returnObject.setMsg("系统繁忙，请稍后重试");
        }
        return returnObject;
    }

}
