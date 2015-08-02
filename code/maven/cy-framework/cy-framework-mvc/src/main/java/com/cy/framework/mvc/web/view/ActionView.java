package com.cy.framework.mvc.web.view;

public class ActionView extends View{
	
	private String url;
	
	public ActionView(String url){
		this.url = url;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
