package com.nbd.article.managercallback;

import java.util.List;

import com.nbd.article.bean.ArticleInfo;
import com.nbd.network.bean.RequestType;

public interface ArticleInfoCallback {
	
	void onArticleInfoCallback(List<ArticleInfo> infos,RequestType type); 

}
