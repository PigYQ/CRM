package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.settings.pojo.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    /**
     * 根据用户名和密码查询用户
     */
    public User selectUserByLoginActAndPwd(Map<String,Object> map);

    /**
     * 查询所有用户
     */
    public List<User> queryAllUser();
}
