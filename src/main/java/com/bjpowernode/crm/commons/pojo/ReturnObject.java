package com.bjpowernode.crm.commons.pojo;

import com.bjpowernode.crm.commons.utils.Constant;

public class ReturnObject {
    private String code = Constant.RETURN_OBJECT_CODE_FAIL;
    private String msg;
    private Object obj;

    @Override
    public String toString() {
        return "ReturnObject{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", obj=" + obj +
                '}';
    }

    public ReturnObject() {
    }

    public ReturnObject(String code, String msg, Object obj) {
        this.code = code;
        this.msg = msg;
        this.obj = obj;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
