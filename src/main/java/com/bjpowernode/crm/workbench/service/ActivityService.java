package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.pojo.Activity;

import java.util.List;
import java.util.Map;

public interface ActivityService {

    //保存创建的市场活动
    int saveCreateActivity(Activity activity);

    //根据条件分页查询市场活动
    List<Activity> queryActivityByConditionForPage(Map<String,Object> map);

    //根据条件查询市场活动条数
    int queryActivityNums(Map<String,Object> map);

    //根据id删除市场活动
    int deleteByIds(String[] ids);

    //根据id查询市场活动，不需联表
    Activity queryActivityById(String id);

    //更新市场活动
    int updateActivityById(Activity activity);

    //查询所有市场活动
    List<Activity> queryAllActivity();

    //根据id集查询市场活动
    List<Activity> queryByIds(String[] ids);

    //导入市场活动
    int saveImportActivity(List<Activity> activityList);

    //根据id查询市场活动，需联表
    Activity queryActivityDetailById(String id);

    //根据线索id查询市场活动
    List<Activity> queryActivityByClueId(String clueId);

    //根据线索id和市场活动名字查询未与之关联的市场活动
    List<Activity> queryActivityByClueIdAndActivityName(Map<String,Object> map);

    //根据线索id和市场活动名字查询已经与之关联的市场活动
    List<Activity> queryHaveActivityByClueIdAndActivityName(Map<String,Object> map);
}
