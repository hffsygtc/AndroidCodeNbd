package com.nbd.article.bean;

import java.util.List;

import org.litepal.crud.DataSupport;

public class NewspaperMonthBean extends DataSupport {
	
	private int id;
	
	//日期的时间
	private String n_index;
	
	private List<NewspaperImage> sections;
	
	public List<NewspaperImage> getSectionImages() {
		return DataSupport.where("n_index = ?", n_index)
				.find(NewspaperImage.class);
	}
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getN_index() {
		return n_index;
	}

	public void setN_index(String n_index) {
		this.n_index = n_index;
	}

	public List<NewspaperImage> getSections() {
		return sections;
	}

	public void setSections(List<NewspaperImage> sections) {
		this.sections = sections;
	}
	
	

}
