package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.pojo.ClueActivityRelation;

import java.util.List;

public interface ClueActivityRelationService {

    int saveRelation(List<ClueActivityRelation> clueActivityRelations);

    /**
     * 根据线索id和市场活动id删除关联
     */
    int deleteByClueIdAndActivityId(String activityId,String clueId);
}
