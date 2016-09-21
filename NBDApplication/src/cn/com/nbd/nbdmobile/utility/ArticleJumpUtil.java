package cn.com.nbd.nbdmobile.utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.nbd.nbdmobile.activity.AdvertiseWebviewActivity;
import cn.com.nbd.nbdmobile.activity.FeatureDetailActivity;
import cn.com.nbd.nbdmobile.activity.ImagesGalleryActivity;
import cn.com.nbd.nbdmobile.activity.WebviewContentActivity;

import com.nbd.article.article.ArticleType;
import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.SelfMessage;
import com.nbd.article.bean.UserInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.network.bean.RequestType;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

/**
 * 各个页面文章跳转的文章不同情况的处理情况
 * 
 * @author he
 * 
 */
public class ArticleJumpUtil {

	/**
	 * 根据不同的文章类型和文章详情进行文章跳转的处理
	 * 
	 * @param activity
	 *            当前文章列表的页面
	 * @param article
	 *            点击被选中的文章对象
	 * @param showTitle
	 *            跳转的文章详情页面显示的标题
	 */
	public static void jumpToArticleDetal(Activity activity,
			ArticleInfo article, String showTitle,boolean isScroll) {

		if (article != null) {

			Log.i("[>>News Click<<]", "{type>>" + article.getType() + "<<}"
					+ "{id>>" + article.getArticle_id() + "<<}" + "{title>>"
					+ article.getTitle() + "<<}");

			/**
			 * 如果有"redirect_to"字段，就优先跳转到对应的这个页面
			 */
			if (article.getRedirect_to() != null
					&& !article.getRedirect_to().equals("")) {
				Log.i("[>>News Click<<]", "CASE_____>>[redirect_to]<<");
				Intent i = new Intent(activity, AdvertiseWebviewActivity.class);
				Bundle b = new Bundle();
				b.putString("link", article.getRedirect_to());
				b.putString("title", article.getTitle());
				i.putExtra("urlbundle", b);
				activity.startActivity(i);
			} else {
				if (article.getList_show_control() != null) {
					switch (article.getList_show_control().getDisplay_form()) {
					case 7:
					case 9:
					case 10:
						Log.i("[>>News Click<<]", "display_____>>[WEB URL]<<");
						if (article.getUrl() != null
								&& !article.getUrl().equals("")) {
							Intent i = new Intent(activity,
									AdvertiseWebviewActivity.class);
							Bundle b = new Bundle();
							b.putString("link", article.getUrl());
							b.putString("title", article.getTitle());
							i.putExtra("urlbundle", b);
							activity.startActivity(i);
						} else {
							Log.i("[>>News Click<<]",
									"display___>>[NULL URL]<<");
						}
						break;
					case 4:
						Log.i("[>>News Click<<]", "display_____>>[GALLERY]<<");
						Intent i = new Intent(activity,
								ImagesGalleryActivity.class);
						Bundle b = new Bundle();
						b.putLong("articleId", article.getArticle_id());
						b.putInt("galleryId", article.getGallery_id());
						b.putLong("reviewCount", article.getReview_count());
						b.putBoolean("allowReview", article.isAllow_review());
						b.putBoolean("isCollection", article.isCollection());
						if (article.getType().equals(ArticleType.IMAGE)) {
							b.putInt("fromType", 0);
						} else {
							b.putInt("fromType", 1);
						}
						i.putExtra("urlbundle", b);
						activity.startActivity(i);
						break;
					case 6:
						Log.i("[>>News Click<<]", "display____>>[FEATURE]<<");
						int fearureId = Integer.parseInt(article
								.getFeature_id());
						Intent intent = new Intent(activity,
								FeatureDetailActivity.class);
						intent.putExtra("featureId", fearureId);
						activity.startActivity(intent);
						break;
					default:
						Log.i("[>>News Click<<]", "display____>>[NORMAL]<<");
						Intent it = new Intent(activity,
								WebviewContentActivity.class);
						Bundle bundle = new Bundle();
						bundle.putLong("url", article.getArticle_id());
						bundle.putString("title", showTitle);
						bundle.putBoolean("comment", article.isAllow_review());
						bundle.putLong("commentNum", article.getReview_count());
						bundle.putBoolean("Jpush", false);
						it.putExtra("urlbundle", bundle);
						activity.startActivity(it);
						break;
					}
				} else {
					if (article.getType().equals(ArticleType.AD_LG)
							|| article.getType().equals(ArticleType.AD_MD)
							|| article.getType().equals(ArticleType.AD_SM)
							|| article.getType().equals(ArticleType.MARKET)
							|| article.getType().equals(ArticleType.VIDEO)
							|| article.getType().equals(ArticleType.LIVE)) {
						Log.i("[>>News Click<<]", "CASE_____>>[AD URL]<<");
						if (article.getUrl() != null
								&& !article.getUrl().equals("")) {
							Intent i = new Intent(activity,
									AdvertiseWebviewActivity.class);
							Bundle b = new Bundle();
							b.putString("link", article.getUrl());
							b.putString("title", article.getTitle());
							i.putExtra("urlbundle", b);
							activity.startActivity(i);
						} else {
							Log.i("[>>News Click<<]",
									"CASE_____>>[AD URL null]<<");
						}
					} else if (article.getType().equals(ArticleType.IMAGE)
							|| article.getType().equals(ArticleType.GALLERY)) { // 图集的情况详情
						Log.i("[>>News Click<<]", "CASE_____>>[IMAGE]<<");
						Intent i = new Intent(activity,
								ImagesGalleryActivity.class);
						Bundle b = new Bundle();
						b.putLong("articleId", article.getArticle_id());
						b.putInt("galleryId", article.getGallery_id());
						b.putLong("reviewCount", article.getReview_count());
						b.putBoolean("allowReview", article.isAllow_review());
						b.putBoolean("isCollection", article.isCollection());
						if (article.getType().equals(ArticleType.IMAGE)) {
							b.putInt("fromType", 0);
						} else {
							b.putInt("fromType", 1);
						}
						i.putExtra("urlbundle", b);
						activity.startActivity(i);

					} else if (article.getType().equals(ArticleType.FEATURE)) {
						Log.e("jumpToFeature==>", article.getFeature_id());
						int fearureId = Integer.parseInt(article
								.getFeature_id());
						Intent i = new Intent(activity,
								FeatureDetailActivity.class);
						i.putExtra("featureId", fearureId);
						activity.startActivity(i);
					} else {
						Log.i("[>>News Click<<]", "CASE_____>>[NORMAL]<<");
						Intent i = new Intent(activity,
								WebviewContentActivity.class);
						Bundle b = new Bundle();
						b.putLong("url", article.getArticle_id());
						b.putString("title", showTitle);
						b.putBoolean("comment", article.isAllow_review());
						b.putLong("commentNum", article.getReview_count());
						b.putBoolean("Jpush", false);
						i.putExtra("urlbundle", b);
						activity.startActivity(i);
					}
				}
			}

			Date now = new Date();
			long nowTime = now.getTime();
			if (article.getArticle_id() > 0) {
				if (ArticleManager.getInstence().storeClickTime(nowTime,
						article.getArticle_id(),isScroll)) {
					ArticleConfig config = new ArticleConfig();
					config.setType(RequestType.ADD_CLICK_COUNT);
					// config.setAccessToken(accessToken);
					config.setArticleId(article.getArticle_id());
					ArticleManager.getInstence().addClickCount(config);
				}
			}
		}

	}

	public static void addClickCount(long articleId, String accessToken,boolean isScroll) {
		Date now = new Date();
		long nowTime = now.getTime();
		if (articleId > 0) {
			if (ArticleManager.getInstence().storeClickTime(nowTime, articleId,isScroll)) {
				ArticleConfig config = new ArticleConfig();
				config.setType(RequestType.ADD_CLICK_COUNT);
				config.setAccessToken(accessToken);
				config.setArticleId(articleId);
				ArticleManager.getInstence().addClickCount(config);
			}
		}
	}

}
