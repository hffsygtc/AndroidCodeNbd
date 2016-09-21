package com.nbd.article.bean;

import org.litepal.crud.DataSupport;

/**
 *  文章广告里面的标签数据
 * @author riche
 *
 */
public class ArticleTag extends DataSupport {
	
	private String value;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	

}
