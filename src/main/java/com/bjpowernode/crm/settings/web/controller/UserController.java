package com.bjpowernode.crm.settings.web.controller;

import com.bjpowernode.crm.commons.pojo.ReturnObject;
import com.bjpowernode.crm.commons.utils.Constant;
import com.bjpowernode.crm.commons.utils.DateUtils;
import com.bjpowernode.crm.settings.pojo.User;
import com.bjpowernode.crm.settings.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @RequestMapping("/settings/qx/user/tologin.do")
    public String toLogin(){
        return "settings/qx/user/login";
    }

    @RequestMapping("/settings/qx/user/login.do")
    @ResponseBody
    public Object doLogin(String loginAct, String loginPwd, String isRemPwd, HttpServletRequest request, HttpSession session, HttpServletResponse response){
        //封装参数
        Map<String,Object> map = new HashMap<>();
        map.put("loginAct",loginAct);
        map.put("loginPwd",loginPwd);
        map.put("isRemPwd",isRemPwd);

        //调用service层查询用户
        User user = userService.selectUserByLoginActAndPwd(map);

        //判断用户是否合法
        ReturnObject returnObject = new ReturnObject();
        if (user == null){
            //登录失败，账户或密码错误
            returnObject.setMsg("用户名或密码错误");
        }else{
            //进一步判断是否能够登录
            String nowTime = DateUtils.formatDateTime(new Date());
            if (nowTime.compareTo(user.getExpireTime()) > 0){
                //登录失败，账户过期
                returnObject.setMsg("账户已过期");
            }else if ("0".equals(user.getLockState())){
                //登录失败，账户锁定
                returnObject.setMsg("账户已过期");
            }else if (!user.getAllowIps().contains(request.getRemoteAddr())){
                //登录失败，id受限
                returnObject.setMsg("该ip无法登录");
            }else {
                //登录成功
                returnObject.setCode(Constant.RETURN_OBJECT_CODE_SUCCESS);
                session.setAttribute(Constant.USER_INFO,user);
                returnObject.setMsg("登陆成功");
                if ("true".equals(isRemPwd)){
                    //十天免登录
                    Cookie cookie1 = new Cookie("loginAct",loginAct);
                    cookie1.setMaxAge(60*60*24*10);
                    response.addCookie(cookie1);

                    Cookie cookie2 = new Cookie("loginPwd",loginPwd);
                    cookie1.setMaxAge(60*60*24*10);
                    response.addCookie(cookie2);
                }else {
                    Cookie cookie1 = new Cookie("loginAct","1");
                    cookie1.setMaxAge(0);
                    response.addCookie(cookie1);

                    Cookie cookie2 = new Cookie("loginPwd","1");
                    cookie2.setMaxAge(0);
                    response.addCookie(cookie2);
                }
            }
        }
        return returnObject;
    }

    @RequestMapping("/settings/qx/user/logout.do")
    public String logout(HttpServletResponse response,HttpSession session){
        //销毁cookie
        Cookie cookie1 = new Cookie("loginAct","1");
        cookie1.setMaxAge(0);
        response.addCookie(cookie1);

        Cookie cookie2 = new Cookie("loginPwd","1");
        cookie2.setMaxAge(0);
        response.addCookie(cookie2);
        //销毁session
        session.invalidate();
        return "redirect:/";
    }
}
