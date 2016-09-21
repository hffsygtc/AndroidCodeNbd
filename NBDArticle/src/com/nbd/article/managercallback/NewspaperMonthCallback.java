package com.nbd.article.managercallback;

import java.util.List;

import com.nbd.article.bean.NewspaperMonthBean;

public interface NewspaperMonthCallback {

	void onMonthPaperCallback(List<NewspaperMonthBean> papers);
}
