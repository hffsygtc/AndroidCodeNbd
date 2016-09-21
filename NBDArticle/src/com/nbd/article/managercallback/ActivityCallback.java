package com.nbd.article.managercallback;

import java.util.List;

import com.nbd.article.bean.ActivityListBean;

public interface ActivityCallback {
	
	void onActivityCallback(List<ActivityListBean> activities,String s);

}
