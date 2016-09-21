package com.nbd.article.managercallback;

import java.util.List;

import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.FeatureInfo;
import com.nbd.network.bean.RequestType;

public interface FeatureInfoCallback {
	
	void onArticleInfoCallback(List<FeatureInfo> infos,RequestType type); 

}
