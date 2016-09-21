package com.nbd.article.bean;

import android.R.integer;

import com.nbd.network.bean.CommentHandleType;
import com.nbd.network.bean.RequestType;

/**
 * 本地获取文章数据的配置条件
 * 
 * @author riche
 *
 */
public class ArticleConfig {

	/** 文章的类别 */
	private RequestType type;
	/** 是否从本地获取 */
	private boolean isLocalArticle;
	/** 获取单页的数量 */
	private int pageCount;
	/** 获取第几页的数据 */
	private int pagePositon;
	/** 传入的当前最后一个文章的ID*/
	private long maxId;
	/**文章的ID 用于请求详情*/
	private long articleId;
	
	private String paperMonth;
	private String paperDate;
	
	private String accessToken;
	
	private boolean isCollection;
	
	private String password;
	private String newPassword;
	
	private boolean customFlag; 
	private String customString;
	
	private CommentHandleType handleType;
	private int commentId;

	
	public CommentHandleType getHandleType() {
		return handleType;
	}

	public void setHandleType(CommentHandleType handleType) {
		this.handleType = handleType;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public String getCustomString() {
		return customString;
	}

	public void setCustomString(String customString) {
		this.customString = customString;
	}

	/** 评论的类型 0：全部 1：最新 2：最热 3：精华*/
	private int commentType;
	

	public int getCommentType() {
		return commentType;
	}

	public void setCommentType(int commentType) {
		this.commentType = commentType;
	}

	public boolean isCustomFlag() {
		return customFlag;
	}

	public void setCustomFlag(boolean customFlag) {
		this.customFlag = customFlag;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public boolean isCollection() {
		return isCollection;
	}

	public void setCollection(boolean isCollection) {
		this.isCollection = isCollection;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getPaperMonth() {
		return paperMonth;
	}

	public void setPaperMonth(String paperMonth) {
		this.paperMonth = paperMonth;
	}

	public String getPaperDate() {
		return paperDate;
	}

	public void setPaperDate(String paperDate) {
		this.paperDate = paperDate;
	}

	public long getArticleId() {
		return articleId;
	}

	public void setArticleId(long articleId) {
		this.articleId = articleId;
	}

	public long getMaxId() {
		return maxId;
	}

	public void setMaxId(long maxId) {
		this.maxId = maxId;
	}

	public RequestType getType() {
		return type;
	}

	public void setType(RequestType type) {
		this.type = type;
	}

	public boolean isLocalArticle() {
		return isLocalArticle;
	}

	public void setLocalArticle(boolean isLocalArticle) {
		this.isLocalArticle = isLocalArticle;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}

	public int getPagePositon() {
		return pagePositon;
	}

	public void setPagePositon(int pagePositon) {
		this.pagePositon = pagePositon;
	}

}
