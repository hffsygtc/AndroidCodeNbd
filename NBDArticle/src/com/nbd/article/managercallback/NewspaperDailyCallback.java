package com.nbd.article.managercallback;

import java.util.List;

import com.nbd.article.bean.NewspaperDailyBean;

public interface NewspaperDailyCallback {

	void onDailyNewsCallback(List<NewspaperDailyBean> dailyNews);
}
