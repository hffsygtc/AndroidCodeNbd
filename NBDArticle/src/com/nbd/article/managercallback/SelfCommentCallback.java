package com.nbd.article.managercallback;

import java.util.List;

import com.nbd.article.bean.SingleComment;

public interface SelfCommentCallback {
	void onSelfCommentCallback(List<SingleComment> comments,String error);

}
