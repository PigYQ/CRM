package com.bjpowernode.crm.workbench.service;

import com.bjpowernode.crm.workbench.pojo.DictionaryValue;

import java.util.List;

public interface DicValueService {

    /**
     * 根据类型查询数据字典值
     */
    List<DictionaryValue> queryDicValueByTypeCode(String typeCode);
}
