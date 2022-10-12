package com.bjpowernode.crm.settings.web.interceptor;

import com.bjpowernode.crm.commons.utils.Constant;
import com.bjpowernode.crm.settings.pojo.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //获取session对象
        HttpSession session = httpServletRequest.getSession();
        //若session域中没有User信息，则拦截，重定向到登录页面
        User user = (User) session.getAttribute(Constant.USER_INFO);
        if (user == null){
            httpServletResponse.sendRedirect(httpServletRequest.getContextPath());
            return false;
        }
        //若session域中有User信息，则放行
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
