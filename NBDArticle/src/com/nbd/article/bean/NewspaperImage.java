package com.nbd.article.bean;

import org.litepal.crud.DataSupport;

public class NewspaperImage extends DataSupport {

	private String page;
	private String section;
	private String section_avatar;
	
	//报纸的时间 用于查询和判断数据库
	private String n_index;
	
	

	public String getN_index() {
		return n_index;
	}

	public void setN_index(String n_index) {
		this.n_index = n_index;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public String getSection_avatar() {
		return section_avatar;
	}

	public void setSection_avatar(String section_avatar) {
		this.section_avatar = section_avatar;
	}

}
