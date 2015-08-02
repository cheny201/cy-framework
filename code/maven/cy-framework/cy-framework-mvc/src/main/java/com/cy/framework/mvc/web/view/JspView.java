package com.cy.framework.mvc.web.view;

public class JspView extends View{
	
	private String url;
	
	public JspView(String url){
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
