package com.bjpowernode.crm.workbench.service.impl;

import com.bjpowernode.crm.workbench.mapper.DictionaryValueMapper;
import com.bjpowernode.crm.workbench.pojo.DictionaryValue;
import com.bjpowernode.crm.workbench.service.DicValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DicValueServiceImpl implements DicValueService {

    @Autowired
    private DictionaryValueMapper dictionaryValueMapper;

    @Override
    public List<DictionaryValue> queryDicValueByTypeCode(String typeCode) {
        return dictionaryValueMapper.selectDicValueByTypeCode(typeCode);
    }
}
