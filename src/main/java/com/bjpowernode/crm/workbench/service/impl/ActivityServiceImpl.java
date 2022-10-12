package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.workbench.mapper.ActivityMapper;
import com.bjpowernode.crm.workbench.pojo.Activity;
import com.bjpowernode.crm.workbench.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class ActivityServiceImpl implements ActivityService {

    @Autowired
    private ActivityMapper activityMapper;

    @Override
    public int saveCreateActivity(Activity activity) {
        return activityMapper.insertActivity(activity);
    }

    @Override
    public List<Activity> queryActivityByConditionForPage(Map<String, Object> map) {
        return activityMapper.selectByConditionForPages(map);
    }

    @Override
    public int deleteByIds(String[] ids) {
        return activityMapper.deleteByIds(ids);
    }

    @Override
    public Activity queryActivityById(String id) {
        return activityMapper.selectActivityById(id);
    }

    @Override
    public Activity queryActivityDetailById(String id) {
        return activityMapper.selectActivityDetailById(id);
    }

    @Override
    public List<Activity> queryActivityByClueId(String clueId) {
        return activityMapper.selectActivityByClueId(clueId);
    }

    @Override
    public List<Activity> queryActivityByClueIdAndActivityName(Map<String, Object> map) {
        return activityMapper.selectActivityByClueIdAndActivityName(map);
    }

    @Override
    public List<Activity> queryHaveActivityByClueIdAndActivityName(Map<String, Object> map) {
        return activityMapper.selectHaveActivityByClueIdAndActivityName(map);
    }

    @Override
    public int saveImportActivity(List<Activity> activityList) {
        return activityMapper.insertActivityByList(activityList);
    }

    @Override
    public List<Activity> queryByIds(String[] ids) {
        return activityMapper.selectByIds(ids);
    }

    @Override
    public List<Activity> queryAllActivity() {
        return activityMapper.selectAllActivity();
    }

    @Override
    public int updateActivityById(Activity activity) {
        return activityMapper.updateActivityById(activity);
    }

    @Override
    public int queryActivityNums(Map<String, Object> map) {
        return activityMapper.selectActivityNums(map);
    }
}
