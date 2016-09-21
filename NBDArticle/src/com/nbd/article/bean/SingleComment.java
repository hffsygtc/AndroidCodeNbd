package com.nbd.article.bean;

import java.util.List;

public class SingleComment {

	private int id;

	private String body;

	private String type;

	private long type_id;

	private int support_num;

	private String parent_id;

	private boolean recommend;

	private long created_at;

	private String user_name;

	private String user_avatar_url;

	private String date_format;
	
	private long article_id;
	private String article_title;
	

	public long getArticle_id() {
		return article_id;
	}

	public void setArticle_id(long article_id) {
		this.article_id = article_id;
	}

	public String getArticle_title() {
		return article_title;
	}

	public void setArticle_title(String article_title) {
		this.article_title = article_title;
	}

	private String location;
	private boolean supported;
	
	private List<SingleComment> ref;
	
	private CommentType commentType;
	
	private boolean hasChild;
	private int childPosition;
	
	/** 是否显示父类下方的回复 分享 布局*/
	private boolean isOpen; 
	
	private String mShortContent;
	
	public String getmShortContent() {
		return mShortContent;
	}

	public void setmShortContent(String mShortContent) {
		this.mShortContent = mShortContent;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public boolean isHasChild() {
		return hasChild;
	}

	public void setHasChild(boolean hasChild) {
		this.hasChild = hasChild;
	}

	public int getChildPosition() {
		return childPosition;
	}

	public void setChildPosition(int childPosition) {
		this.childPosition = childPosition;
	}

	public CommentType getCommentType() {
		return commentType;
	}

	public void setCommentType(CommentType commentType) {
		this.commentType = commentType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getType_id() {
		return type_id;
	}

	public void setType_id(long type_id) {
		this.type_id = type_id;
	}

	public int getSupport_num() {
		return support_num;
	}

	public void setSupport_num(int support_num) {
		this.support_num = support_num;
	}

	public String getParent_id() {
		return parent_id;
	}

	public void setParent_id(String parent_id) {
		this.parent_id = parent_id;
	}

	public boolean isRecommend() {
		return recommend;
	}

	public void setRecommend(boolean recommend) {
		this.recommend = recommend;
	}

	public long getCreated_at() {
		return created_at;
	}

	public void setCreated_at(long created_at) {
		this.created_at = created_at;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getUser_avatar_url() {
		return user_avatar_url;
	}

	public void setUser_avatar_url(String user_avatar_url) {
		this.user_avatar_url = user_avatar_url;
	}

	public String getDate_format() {
		return date_format;
	}

	public void setDate_format(String date_format) {
		this.date_format = date_format;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public boolean isSupported() {
		return supported;
	}

	public void setSupported(boolean supported) {
		this.supported = supported;
	}

	public List<SingleComment> getRef() {
		return ref;
	}

	public void setRef(List<SingleComment> ref) {
		this.ref = ref;
	}
	
	

}
