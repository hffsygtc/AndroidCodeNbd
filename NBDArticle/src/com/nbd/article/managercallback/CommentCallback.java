package com.nbd.article.managercallback;

import java.util.List;

import com.nbd.article.bean.commentBean;

public interface CommentCallback {

	void onCommentAllCallback(List<commentBean> comments);
}
