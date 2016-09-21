package com.nbd.article.bean;

import java.util.List;

import android.R.integer;

/**
 * 专题里面的标签，关键字
 * 
 * @author he
 *
 */
public class FeatureLable {

	private String name;
	private int pos;
	private int label_id;
	private List<ArticleInfo> articles;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public int getLabel_id() {
		return label_id;
	}
	public void setLabel_id(int label_id) {
		this.label_id = label_id;
	}
	public List<ArticleInfo> getArticles() {
		return articles;
	}
	public void setArticles(List<ArticleInfo> articles) {
		this.articles = articles;
	}
	
	
}
