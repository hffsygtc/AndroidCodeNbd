package com.nbd.article.bean;

import java.util.List;

import org.litepal.crud.DataSupport;

public class NewspaperDailyBean extends DataSupport {

	private String n_index;
	private String page;
	private List<ArticleInfo> articles;

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public List<ArticleInfo> getArticles() {
		return articles;
	}

	public void setArticles(List<ArticleInfo> articles) {
		this.articles = articles;
	}

	public String getN_index() {
		return n_index;
	}

	public void setN_index(String n_index) {
		this.n_index = n_index;
	}

	/**
	 * 根据文章的类型，当需要查询关联表的时候，获取关联表中的信息
	 * 
	 * @return 子文章数据类型的列表
	 */
	public List<ArticleInfo> getDailyArticle() {
		return DataSupport.where("n_index = ? and page = ?", n_index, page)
				.find(ArticleInfo.class);
	}

}
