package com.nbd.article.managercallback;

import java.util.List;

import com.nbd.article.bean.MyMessageBean;

public interface MessageInfoCallback {

	void onMyMessageCallback(List<MyMessageBean> msgs);
	
}
