package com.nbd.article.bean;

import org.litepal.crud.DataSupport;

public class ChildrenArticlesInfo extends DataSupport {
	
	/** 文章子标签ID */
	private long child_id;
	/** 文章子标签的标签 */
	private String child_label;
	
	public long getChild_id() {
		return child_id;
	}
	public void setChild_id(long child_id) {
		this.child_id = child_id;
	}
	public String getChild_label() {
		return child_label;
	}
	public void setChild_label(String child_label) {
		this.child_label = child_label;
	}


}
