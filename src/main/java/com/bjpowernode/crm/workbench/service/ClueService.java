package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.pojo.Clue;

import java.util.List;
import java.util.Map;

public interface ClueService {
    /**
     * 分页查询线索
     */
    List<Clue> queryClueForPage(Map<String,Object> map);

    /**
     * 查询总线索数
     */
    int queryClueNums(Map<String, Object> map);

    /**
     * 创建一条市场活动
     */
    int saveClue(Clue clue);

    /**
     * 根据id批量删除线索
     */
    int deleteByIds(String[] id);

    /**
     * 根据id修改线索
     */
    int updateById(Clue clue);

    /**
     * 根据id查询线索(不联表)
     */
    Clue queryClueById(String id);

    /**
     * 根据id修改线索(联表)
     */
    Clue queryByIdUnion(String id);

    /**
     * 转换线索
     */
    void saveConvert(Map<String,Object> map);
}
