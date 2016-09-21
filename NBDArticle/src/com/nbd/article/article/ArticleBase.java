package com.nbd.article.article;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.nbd.article.bean.ArticleInfo;

public abstract class ArticleBase extends RelativeLayout {

	/** 是否是夜间模式 */
	private boolean isNightTheme;
	/** 文章的数据 */
	private ArticleInfo articleinfo;
	/** 是否被阅读过 */
	private boolean isRead;

	/**
	 * 构造函数
	 * @param context
	 */
	public ArticleBase(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public ArticleBase(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	// /**APP的主题，设备跟着改变布局*/
	// private enum appTheme{
	// NIGHT,
	// DAYLIGHT
	// }

	/**
	 * 初始化方法，必须调用
	 * 
	 * @param info
	 */
	protected void initArticle(ArticleInfo info) {
		articleinfo = info;
	}


	/**
	 * 子类获取新闻内容的方法
	 * @return
	 */
	protected ArticleInfo getArticleInfo() {
		return articleinfo;
	}

	public boolean getTheme() {
		return isNightTheme;
	}

	public void setTheme(boolean nightTheme) {
		this.isNightTheme = nightTheme;
	}

}
