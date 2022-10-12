package com.bjpowernode.crm.commons.utils;

import java.util.UUID;

public class UUIDUtils {
    /**
     * 获取id
     * @return
     */
    public static String getId(){
        return UUID.randomUUID().toString().replaceAll("-","");
    }
}
