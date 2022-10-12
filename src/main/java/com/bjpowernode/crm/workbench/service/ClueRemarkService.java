package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.pojo.ClueRemark;

import java.util.List;

public interface ClueRemarkService {

    /**
     * 根据线索id查询备注
     */
    List<ClueRemark> queryClueRemarkByClueId(String id);

    /**
     * 保存创建的线索备注
     */
    int saveClueRemark(ClueRemark remark);
}
