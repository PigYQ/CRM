package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.mapper.ActivityRemarkMapper;
import com.bjpowernode.crm.workbench.pojo.ActivityRemark;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public interface ActivityRemarkService {
    /**
     * 根据市场活动id查询备注
     */
    List<ActivityRemark> queryActivityRemarkByActivityId(String activityId);

    /**
     * 保存创建的市场活动
     */
    int saveCreateActivityRemark(ActivityRemark activityRemark);

    /**
     * 根据id删除市场活动备注
     */
    int deleteActivityRemarkById(String id);

    /**
     * 根据id修改市场活动备注
     */
    int saveActivityRemarkById(ActivityRemark activityRemark);
}
