package com.nbd.article.bean;

import java.util.List;

public class commentBean {
	
	private String type;
	
	private List<SingleComment> comments;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<SingleComment> getComments() {
		return comments;
	}

	public void setComments(List<SingleComment> comments) {
		this.comments = comments;
	}
	
	
	

}
