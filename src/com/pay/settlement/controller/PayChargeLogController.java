package com.pay.settlement.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import sun.beans.editors.DoubleEditor;
import sun.beans.editors.FloatEditor;
import sun.beans.editors.IntegerEditor;
import sun.beans.editors.LongEditor;
import util.JWebConstant;
import com.jweb.dao.Blog;
import com.jweb.dao.JWebUser;
import com.jweb.service.JWebUserService;
import com.pay.settlement.service.PayChargeLogService;
import com.pay.settlement.dao.PayChargeLog;
@Controller
public class PayChargeLogController {
    @RequestMapping("payChargeLog")
    public String getPayChargeLogList(HttpServletRequest request,HttpServletResponse response,
            @ModelAttribute("payChargeLog") PayChargeLog payChargeLog) {
        JWebUser user = (JWebUser)request.getSession().getAttribute("user");
        //登录和权限判断
        if(!JWebUserService.checkUser(user,request))return "/jsp/jweb/jump_login.jsp";
        //处理标识，空：跳转、0：列表
        String flag = request.getParameter("flag");
        new Blog(this.getClass().getSimpleName()+".getPayChargeLogList","取得PayChargeLog列表",user.id,request);
        OutputStream os = null;
        try {
            if(flag == null)return "/jsp/pay/settlement/pay_charge_log.jsp";
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            os = response.getOutputStream();
            int page = 1;
            int rows = JWebConstant.WEB_RECORD_COUNT_PER_PAGE;
            try {
                rows = Integer.parseInt((String)JWebConstant.SYS_CONFIG.get("WEB_RECORD_COUNT_PER_PAGE"));//系统配置的每页记录条数
            } catch (Exception e) {}
            try {
                page = Integer.parseInt(request.getParameter("page"));
            } catch (Exception e1) {}
            try {
                rows = Integer.parseInt(request.getParameter("rows"));//用户设置的每页记录条数
            } catch (Exception e1) {}
            os.write(new PayChargeLogService().getPayChargeLogList(payChargeLog,page,rows,
                request.getParameter("sort"),request.getParameter("order")).getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(os != null)try {os.close();} catch (IOException e1) {}
        }
        return null;
    }
    @RequestMapping("addPayChargeLog")
    public String addPayChargeLog(HttpServletRequest request,HttpServletResponse response,
            @ModelAttribute("payChargeLog") PayChargeLog payChargeLog) {
        JWebUser user = (JWebUser)request.getSession().getAttribute("user");
        OutputStream os = null;
        Blog log = null;
        try {
            os = response.getOutputStream();
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            //登录和权限判断
            if(!JWebUserService.checkUser(user,request)){
               os.write(((String)request.getSession().getAttribute("LOGIN_INFO")).getBytes());
               return null;
            }
            log = new Blog(this.getClass().getSimpleName()+".addPayChargeLog","添加PayChargeLog",user.id,request);
            new PayChargeLogService().addPayChargeLog(payChargeLog);
            os.write(JWebConstant.OK.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            try {os.write(e.getMessage().getBytes("utf-8"));} catch (Exception e1) {}
            if(log!=null)log.info("添加失败,"+e.getMessage());
        } finally {
            if(os != null)try {os.close();} catch (IOException e1) {}
        }
        return null;
    }
    @RequestMapping("detailPayChargeLog")
    public String detailPayChargeLog(HttpServletRequest request,HttpServletResponse response) {
        JWebUser user = (JWebUser)request.getSession().getAttribute("user");
        Blog log = null;
        //登录和权限判断
        if(!JWebUserService.checkUser(user,request))return "/jsp/jweb/jump_login.jsp";
        log = new Blog(this.getClass().getSimpleName()+".detailPayChargeLog","查看PayChargeLog",user.id,request);
        try {
            request.setAttribute("payChargeLog",
                new PayChargeLogService().detailPayChargeLog(request.getParameter("id")));
        } catch (Exception e) {
            e.printStackTrace();
            if(log!=null)log.info("查看失败,"+e.getMessage());
        } 
        return "/jsp/pay/settlement/pay_charge_log_detail.jsp";
    }
    @RequestMapping("removePayChargeLog")
    public String removePayChargeLog(HttpServletRequest request,HttpServletResponse response) {
        JWebUser user = (JWebUser)request.getSession().getAttribute("user");
        OutputStream os = null;
        Blog log = null;
        try {
            os = response.getOutputStream();
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            //登录和权限判断
            if(!JWebUserService.checkUser(user,request)){
               os.write(((String)request.getSession().getAttribute("LOGIN_INFO")).getBytes());
               return null;
            }
            log = new Blog(this.getClass().getSimpleName()+".removePayChargeLog","删除PayChargeLog",user.id,request);
            new PayChargeLogService().removePayChargeLog(request.getParameter("id"));
            os.write(JWebConstant.OK.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            try {os.write(e.getMessage().getBytes("utf-8"));} catch (Exception e1) {}
            if(log!=null)log.info("删除失败,"+e.getMessage());
        } finally {
            if(os != null)try {os.close();} catch (IOException e1) {}
        }
        return null;
    }
    @RequestMapping("updatePayChargeLog")
    public String updatePayChargeLog(HttpServletRequest request,HttpServletResponse response,
            @ModelAttribute("payChargeLog") PayChargeLog payChargeLog) {
        JWebUser user = (JWebUser)request.getSession().getAttribute("user");
        OutputStream os = null;
        Blog log = null;
        try {
            //处理标识,show跳转,其他更新
            if("show".equals(request.getParameter("flag"))){
                log = new Blog(this.getClass().getSimpleName()+".updatePayChargeLog","取得PayChargeLog",user.id,request);
                request.setAttribute("payChargeLogUpdate",
                    new PayChargeLogService().detailPayChargeLog(request.getParameter("id")));
                return "/jsp/pay/settlement/pay_charge_log_update.jsp";
            }
            os = response.getOutputStream();
            request.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=UTF-8");
            //登录和权限判断
            if(!JWebUserService.checkUser(user,request)){
               os.write(((String)request.getSession().getAttribute("LOGIN_INFO")).getBytes());
               return null;
            }
            log = new Blog(this.getClass().getSimpleName()+".updatePayChargeLog","修改PayChargeLog",user.id,request);
            new PayChargeLogService().updatePayChargeLog(payChargeLog);
            os.write(JWebConstant.OK.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
            try {os.write(e.getMessage().getBytes("utf-8"));} catch (Exception e1) {}
            if(log!=null)log.info("修改失败,"+e.getMessage());
        } finally {
            if(os != null)try {os.close();} catch (IOException e1) {}
        }
        return null;
    }
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(int.class, new IntegerEditor());
        binder.registerCustomEditor(long.class, new LongEditor());
        binder.registerCustomEditor(float.class, new FloatEditor());
        binder.registerCustomEditor(double.class, new DoubleEditor());
        binder.registerCustomEditor(Date.class, new CustomDateEditor(null, true) {
            @Override  
            public void setAsText(String text) throws IllegalArgumentException {
                if(text != null && text.length()>0)
                try {
                    setValue(new SimpleDateFormat("yyyy-MM-dd").parse(text));
                } catch (Exception e) {
                    try {setValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(text));} catch (Exception e1) {}
                }
            }
        });
    }

}