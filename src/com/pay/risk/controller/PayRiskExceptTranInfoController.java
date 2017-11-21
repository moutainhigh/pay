package com.pay.risk.controller;

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
import com.pay.risk.service.PayRiskExceptTranInfoService;
import com.pay.risk.dao.PayRiskExceptTranInfo;
@Controller
public class PayRiskExceptTranInfoController {
    @RequestMapping("payRiskExceptTranInfo")
    public String getPayRiskExceptTranInfoList(HttpServletRequest request,HttpServletResponse response,
            @ModelAttribute("payRiskExceptTranInfo") PayRiskExceptTranInfo payRiskExceptTranInfo) {
        JWebUser user = (JWebUser)request.getSession().getAttribute("user");
        //登录和权限判断
        if(!JWebUserService.checkUser(user,request))return "/jsp/jweb/jump_login.jsp";
        //处理标识，空：跳转、0：列表
        String flag = request.getParameter("flag");
        OutputStream os = null;
        Blog log = null;
        try {
        	log=new Blog(this.getClass().getSimpleName()+".payRiskExceptTranInfo","取得PayRiskExceptTranInfo列表",user.id,request);
            if(flag == null)return "/jsp/pay/risk/pay_risk_except_tran_info.jsp";
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
            os.write(new PayRiskExceptTranInfoService().getPayRiskExceptTranInfoList(payRiskExceptTranInfo,page,rows,
                request.getParameter("sort"),request.getParameter("order")).getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            if(log!=null)log.info("查询列表失败,"+e.getMessage());
        } finally {
            if(os != null)try {os.close();} catch (IOException e1) {}
        }
        return null;
    }
    
    @RequestMapping("payRiskExceptTranInfoOperationStatus")
    public String payRiskExceptTranInfoOperationStatus(HttpServletRequest request,HttpServletResponse response,
    		@ModelAttribute("payRiskExceptTranInfo") PayRiskExceptTranInfo payRiskExceptTranInfo) {
   		 JWebUser user = (JWebUser)request.getSession().getAttribute("user");
           OutputStream os = null;
           Blog log = null;
           String info = null;
           try {
               os = response.getOutputStream();
               request.setCharacterEncoding("UTF-8");
               response.setContentType("text/html;charset=UTF-8");
               //登录和权限判断
               if(!JWebUserService.checkUser(user,request)){
                  os.write(((String)request.getSession().getAttribute("LOGIN_INFO")).getBytes());
                  return null;
               }
               info = "1".equals(payRiskExceptTranInfo.exceptTranFlag) ? "释放":"确认";
               log = new Blog(this.getClass().getSimpleName()+".payRiskExceptTranInfoOperationStatus",info+"PayRiskExceptRule",user.id,request);
               PayRiskExceptTranInfo detailPayRiskExceptTranInfo = new PayRiskExceptTranInfoService().detailPayRiskExceptTranInfo(payRiskExceptTranInfo.id);
               new PayRiskExceptTranInfoService().payRiskExceptTranInfoOperationStatus(detailPayRiskExceptTranInfo,payRiskExceptTranInfo);
               os.write(JWebConstant.OK.getBytes());
           } catch (Exception e) {
               e.printStackTrace();
               try {os.write(e.getMessage().getBytes("utf-8"));} catch (Exception e1) {}
               if(log!=null)log.info(info+"失败,"+e.getMessage());
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