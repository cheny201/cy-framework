package com.cy.framework.mvc.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SimpleDateFormatSerializer;
import com.cy.framework.annotation.RequestMethod;
import com.cy.framework.mvc.MVCRegister;
import com.cy.framework.mvc.RegisterInfo;
import com.cy.framework.mvc.web.view.ActionView;
import com.cy.framework.mvc.web.view.JsonView;
import com.cy.framework.mvc.web.view.JspView;
import com.cy.framework.mvc.web.view.View;

public class DispatcherServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	
	private static Map<String,RegisterInfo> mappingMap = new HashMap<String,RegisterInfo>();
	private static String CONTEXT_PATH;
	private static String ENCODING = "UTF-8";

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("hashCode:"+this.hashCode());
		String requestURI = req.getRequestURI().substring(CONTEXT_PATH.length());
		int idx = requestURI.indexOf("?");
		String path = null;
		if(idx != -1){
			path = requestURI.substring(0,idx);
		}else{
			path = requestURI;
		}
		System.out.println("-------------------------------------------------------------");
		if(!mappingMap.containsKey(path)){
			System.err.println("未找到映射["+path+"]");
			return;
		}
		RegisterInfo info = mappingMap.get(path);
		if(RequestMethod.ALL.equals(info.getRequestType()) || info.getRequestType().equalsIgnoreCase(req.getMethod())){
			try {
				req.setCharacterEncoding(ENCODING);
				resp.setCharacterEncoding(ENCODING);
				Object returnObj = doRequest(req,resp,info);
				doResponse(req,resp,returnObj);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}else{
			System.err.println("未找到映射["+path+","+req.getMethod()+"]");
		}
		System.out.println("-------------------------------------------------------------");
	}
	
	private Object doRequest(HttpServletRequest req, HttpServletResponse resp,RegisterInfo info) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException{
		Object obj = info.getClazz().newInstance();
		Object[] params = addParams(req,resp,info.getParams());
		Method m = obj.getClass().getMethod(info.getMethod(), info.getParams());
		return m.invoke(obj,params);
	}
	
	private Object[] addParams(HttpServletRequest req, HttpServletResponse resp,Class<?>[] params1){
		Object[] params2 = new Object[params1.length];
		for (int i = 0; i < params1.length; i++) {
			if(params1[i].isInstance(req)){
				params2[i] = req;
			}else if(params1[i].isInstance(resp)){
				params2[i] = resp;
			}else{
				params2[i] = null;
			}
		}
		return params2;
	}
	
	private void doResponse(HttpServletRequest req, HttpServletResponse resp,Object returnObj) throws IOException, ServletException{
		if(returnObj != null && returnObj instanceof View){
			if(returnObj instanceof JsonView){
				JsonView jsonView = (JsonView) returnObj;
				resp.setCharacterEncoding(ENCODING);// 设置编码
				resp.setHeader("Cache-Control", "no-cache");
				resp.setContentType("text/json;charset="+ENCODING);
				String dateFormat = (String) jsonView.get("data_date_format");
				if(dateFormat == null){
					dateFormat = "yyyy-MM-dd HH:mm:ss";
				}
				SerializeConfig config = new SerializeConfig();
				config.put(Date.class, new SimpleDateFormatSerializer(dateFormat));
				Map<String,Object> param = jsonView.getParams();
				String json = JSONObject.toJSONString(param, config);
				resp.getWriter().write(json);
			}else if(returnObj instanceof JspView){
				JspView jspView = (JspView) returnObj;
				Map<String,Object> params = jspView.getParams();
				Iterator<String> it = params.keySet().iterator();
				while(it.hasNext()){
					String key = it.next();
					req.setAttribute(key, params.get(key));
				}
				req.getRequestDispatcher(jspView.getUrl()).forward(req, resp);
			}else if(returnObj instanceof ActionView){
				ActionView actionView = (ActionView) returnObj;
				Map<String,Object> params = actionView.getParams();
				Iterator<String> it = params.keySet().iterator();
				while(it.hasNext()){
					String key = it.next();
					req.setAttribute(key, params.get(key));
				}
				req.getRequestDispatcher(actionView.getUrl()).forward(req, resp);
			}
		}
	}
	

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		CONTEXT_PATH = config.getServletContext().getContextPath();
		String suffix = config.getInitParameter("suffix");
		try {
			mappingMap = MVCRegister.register("com.cy","com.cy.framework.mvc.util",suffix);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
