//package com.nbd.article.article;
//
//import com.nbd.article.bean.ArticleInfo;
//import com.nbd.article.view.ThreePicArticle;
//
//import android.content.Context;
//
///**
// * 创建不同类型文章实体的工厂类
// * 
// * @author riche
// *
// */
//public class ArticleFactory {
//
//	public static ArticleBase getArticle(ArticleInfo info, Context context) {
//		ArticleBase article = null;
//		if (info.getType() == null)
//			return article;
//		switch (info.getType()) {
//		case THREEPIC:
//			instance3Pic(info, context);
//			break;
//		default:
//			break;
//		}
//		return article;
//	}
//
//	static ArticleBase instance3Pic(ArticleInfo info, Context context) {
//		ArticleBase base = new ThreePicArticle(context);
//		base.initArticle(info);
//		return base;
//	}
//}
