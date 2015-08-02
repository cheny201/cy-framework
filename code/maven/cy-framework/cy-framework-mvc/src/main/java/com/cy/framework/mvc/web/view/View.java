package com.cy.framework.mvc.web.view;

import java.util.HashMap;
import java.util.Map;

public abstract class View {
	
	protected Map<String,Object> params = new HashMap<String,Object>();
	
	public void put(String key,Object value){
		params.put(key, value);
	};
	
	public void remove(String key){
		params.remove(key);
	};
	
	public Object get(String key){
		return params.get(key);
	};
	
	public Map<String,Object> getParams(){
		return params;
	}
	
}
