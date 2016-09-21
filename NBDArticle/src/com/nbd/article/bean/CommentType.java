package com.nbd.article.bean;

public enum CommentType {
	//第一个父评论，用于控制显示标志性的评论类型
	PARENT_HEAD_VALUE,
	PARENT_NORAML_VALUE,
	PARENT_HEAD_HOT,
	PARENT_NORAML_HOT,
	PARENT_HEAD_NEW,
	PARENT_NORAML_NEW,
	CHILD_HEAD,
	CHILD_BODY,
	CHILD_TAIL,
	CHILD_SINGLE

}
