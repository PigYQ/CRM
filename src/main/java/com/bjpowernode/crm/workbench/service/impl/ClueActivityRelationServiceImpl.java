package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.workbench.mapper.ClueActivityRelationMapper;
import com.bjpowernode.crm.workbench.pojo.ClueActivityRelation;
import com.bjpowernode.crm.workbench.service.ClueActivityRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClueActivityRelationServiceImpl implements ClueActivityRelationService {

    @Autowired
    private ClueActivityRelationMapper clueActivityRelationMapper;


    @Override
    public int saveRelation(List<ClueActivityRelation> clueActivityRelations) {
        return clueActivityRelationMapper.insertRelation(clueActivityRelations);
    }

    @Override
    public int deleteByClueIdAndActivityId(String activityId, String clueId) {
        return clueActivityRelationMapper.deleteByClueIdAndActivityId(activityId,clueId);
    }
}
