package com.nbd.article.managercallback;

import com.nbd.article.bean.UserInfo;

public interface UserInfoCallback {

	void onUserinfoCallback(UserInfo info,String failMsg);
}
