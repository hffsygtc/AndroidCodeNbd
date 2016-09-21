package com.nbd.article.bean;

import java.util.List;

public class ActivityListBean {

	private String category;
	
	private List<ActivityBean> lists;

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public List<ActivityBean> getLists() {
		return lists;
	}

	public void setLists(List<ActivityBean> lists) {
		this.lists = lists;
	}
	
	
}
