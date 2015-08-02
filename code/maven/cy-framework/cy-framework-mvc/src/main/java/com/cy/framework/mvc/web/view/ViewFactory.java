package com.cy.framework.mvc.web.view;

public class ViewFactory {
	
	public static JsonView jsonView(){
		return new JsonView();
	}
	
	public static JsonView jsonView(String key,Object value){
		JsonView json = jsonView();
		json.put(key, value);
		return json;
	}
	
	public static JspView jspView(String url){
		return new JspView(url);
	}
	
	public static JspView jspView(String url,String key,Object value){
		JspView jsp = jspView(url);
		jsp.put(key, value);
		return jsp;
	}
	
	public static ActionView actionView(String url){
		return new ActionView(url);
	}
	
	public static ActionView actionView(String url,String key,Object value){
		ActionView action = actionView(url);
		action.put(key, value);
		return action;
	}

}
