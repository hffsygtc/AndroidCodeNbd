package com.nbd.article.database;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

import com.google.gson.reflect.TypeToken;
import com.nbd.article.article.ArticleType;
import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleDisplayMode;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.ChildrenArticlesInfo;
import com.nbd.article.bean.FeatureInfo;
import com.nbd.article.bean.Gallery;
import com.nbd.article.bean.NewspaperDailyBean;
import com.nbd.article.bean.NewspaperImage;
import com.nbd.article.bean.NewspaperMonthBean;
import com.nbd.article.utility.JsonUtil;
import com.nbd.network.bean.RequestType;

import android.content.ContentValues;
import android.content.Context;
import android.content.Loader;
import android.util.Log;

/**
 * 数据库的数据存储，不同表数据的关联存取封装
 * 
 * @author riche
 * 
 */
public class DatabaseManager {
	private Context mContext;

	public DatabaseManager(Context context) {
		this.mContext = context;
		initDatabase();
	}

	/**
	 * 初始化数据库
	 */
	private void initDatabase() {

	}

	/**
	 * 从数据库获取对应的文章信息
	 * 
	 * @param config
	 *            数据请求方的条件
	 * @return 文章的数据对象
	 */
	public List<ArticleInfo> getArticleFromDb(ArticleConfig config) {
		List<ArticleInfo> infos = new ArrayList<ArticleInfo>();
		switch (config.getType()) {
		// TODO 主要区别是否需要查询 图集子表或者推广链接表
		case KX:
			// 需要将子子文章的数据也一并取出来
			infos = DatabaseUtils.findConditionData(375, "article_id", 15, 0);
			for (int i = 0; i < infos.size(); i++) {
				List<ChildrenArticlesInfo> child = infos.get(i)
						.getChildrenArticlesData();
				if (child != null) {
					infos.get(i).setChildren_articles(child);
				}
				infos.get(i).setList_show_control(
						infos.get(i).getArticleDispalyMode());
			}
			break;
		case ZXLB:
			infos = DatabaseUtils.findConditionData(455, "article_id", 3, 0);
			for (int i = 0; i < infos.size(); i++) {
				if (infos.get(i).getGallery_id() > 0) {
					Gallery gallery = getGalleryFromDb(infos.get(i)
							.getGallery_id());
					List<ArticleImages> dbImages = gallery.getImagesData();
					gallery.setImages(dbImages);
					infos.get(i).setGallery(gallery);
				}
				// List<ArticleImages> dbImages = infos.get(i).getImagesData();
				// if (dbImages != null) {
				// infos.get(i).setImages(dbImages);
				// }
				infos.get(i).setList_show_control(
						infos.get(i).getArticleDispalyMode());
			}

			break;
		case ZX:
			infos = DatabaseUtils.findConditionData(435, "article_id", 15, 0);
			for (int i = 0; i < infos.size(); i++) {
				if (infos.get(i).getGallery_id() > 0) {
					Gallery gallery = getGalleryFromDb(infos.get(i)
							.getGallery_id());
					List<ArticleImages> dbImages = gallery.getImagesData();
					gallery.setImages(dbImages);
					infos.get(i).setGallery(gallery);
				}
				// List<ArticleImages> dbImages = infos.get(i).getImagesData();
				// if (dbImages != null) {
				// infos.get(i).setImages(dbImages);
				// }
				infos.get(i).setList_show_control(
						infos.get(i).getArticleDispalyMode());
			}
			break;
		case GSLB:
			infos = DatabaseUtils.findConditionData(456, "article_id", 3, 0);
			for (int i = 0; i < infos.size(); i++) {
				if (infos.get(i).getGallery_id() > 0) {
					Gallery gallery = getGalleryFromDb(infos.get(i)
							.getGallery_id());
					List<ArticleImages> dbImages = gallery.getImagesData();
					gallery.setImages(dbImages);
					infos.get(i).setGallery(gallery);
				}
				// List<ArticleImages> dbImages = infos.get(i).getImagesData();
				// if (dbImages != null) {
				// infos.get(i).setImages(dbImages);
				// }
				infos.get(i).setList_show_control(
						infos.get(i).getArticleDispalyMode());
			}
			break;
		case GSZJZ:
			infos = DatabaseUtils.findConditionData(457, "article_id", 15, 0);
			for (int i = 0; i < infos.size(); i++) {
				if (infos.get(i).getGallery_id() > 0) {
					Gallery gallery = getGalleryFromDb(infos.get(i)
							.getGallery_id());
					List<ArticleImages> dbImages = gallery.getImagesData();
					gallery.setImages(dbImages);
					infos.get(i).setGallery(gallery);
				}
				// List<ArticleImages> dbImages = infos.get(i).getImagesData();
				// if (dbImages != null) {
				// infos.get(i).setImages(dbImages);
				// }
				infos.get(i).setList_show_control(
						infos.get(i).getArticleDispalyMode());
			}
			break;
		case GSFJZ:
			infos = DatabaseUtils.findConditionData(458, "article_id", 15, 0);
			for (int i = 0; i < infos.size(); i++) {
				if (infos.get(i).getGallery_id() > 0) {
					Gallery gallery = getGalleryFromDb(infos.get(i)
							.getGallery_id());
					List<ArticleImages> dbImages = gallery.getImagesData();
					gallery.setImages(dbImages);
					infos.get(i).setGallery(gallery);
				}
				// List<ArticleImages> dbImages = infos.get(i).getImagesData();
				// if (dbImages != null) {
				// infos.get(i).setImages(dbImages);
				// }
				infos.get(i).setList_show_control(
						infos.get(i).getArticleDispalyMode());
			}
			break;
		case LCLB:
			infos = DatabaseUtils.findConditionData(459, "article_id", 3, 0);
			for (int i = 0; i < infos.size(); i++) {
				if (infos.get(i).getGallery_id() > 0) {
					Gallery gallery = getGalleryFromDb(infos.get(i)
							.getGallery_id());
					List<ArticleImages> dbImages = gallery.getImagesData();
					gallery.setImages(dbImages);
					infos.get(i).setGallery(gallery);
				}
				// List<ArticleImages> dbImages = infos.get(i).getImagesData();
				// if (dbImages != null) {
				// infos.get(i).setImages(dbImages);
				// }
				infos.get(i).setList_show_control(
						infos.get(i).getArticleDispalyMode());
			}
			break;
		case LCYW:
			infos = DatabaseUtils.findConditionData(460, "article_id", 15, 0);
			for (int i = 0; i < infos.size(); i++) {
				if (infos.get(i).getGallery_id() > 0) {
					Gallery gallery = getGalleryFromDb(infos.get(i)
							.getGallery_id());
					List<ArticleImages> dbImages = gallery.getImagesData();
					gallery.setImages(dbImages);
					infos.get(i).setGallery(gallery);
				}
				// List<ArticleImages> dbImages = infos.get(i).getImagesData();
				// if (dbImages != null) {
				// infos.get(i).setImages(dbImages);
				// }
				infos.get(i).setList_show_control(
						infos.get(i).getArticleDispalyMode());
			}
			break;
		case IPO:
			infos = DatabaseUtils.findConditionData(461, "article_id", 15, 0);
			for (int i = 0; i < infos.size(); i++) {
				if (infos.get(i).getGallery_id() > 0) {
					Gallery gallery = getGalleryFromDb(infos.get(i)
							.getGallery_id());
					List<ArticleImages> dbImages = gallery.getImagesData();
					gallery.setImages(dbImages);
					infos.get(i).setGallery(gallery);
				}
				// List<ArticleImages> dbImages = infos.get(i).getImagesData();
				// if (dbImages != null) {
				// infos.get(i).setImages(dbImages);
				// }
				infos.get(i).setList_show_control(
						infos.get(i).getArticleDispalyMode());
			}
			break;
		case VIDEO:
			infos = DatabaseUtils.findConditionData(453, "article_id", 15, 0);
			for (int i = 0; i < infos.size(); i++) {
				if (infos.get(i).getGallery_id() > 0) {
					Gallery gallery = getGalleryFromDb(infos.get(i)
							.getGallery_id());
					List<ArticleImages> dbImages = gallery.getImagesData();
					gallery.setImages(dbImages);
					infos.get(i).setGallery(gallery);
				}
				// List<ArticleImages> dbImages = infos.get(i).getImagesData();
				// if (dbImages != null) {
				// infos.get(i).setImages(dbImages);
				// }
				infos.get(i).setList_show_control(
						infos.get(i).getArticleDispalyMode());
			}
			break;
		default:
			break;
		}

		return infos;

	}

	/**
	 * 从数据库取出图集的数据
	 * 
	 * @param galleryId
	 * @return
	 */
	public static Gallery getGalleryFromDb(int galleryId) {
		List<Gallery> list = DataSupport
				.where("gallery_id = ?", galleryId + "").find(Gallery.class);
		if (list != null && list.size() > 0) {
			List<ArticleImages> images = list.get(0).getImagesData();
			list.get(0).setImages(images);
			return list.get(0);
		}
		return null;
	}

	/**
	 * 获取数据库文章详情的内容
	 */
	public static ArticleInfo getArticleDetailFromDb(long ArticleId) {
		ArticleInfo info = DatabaseUtils.findArticleDetailData(ArticleId);

		if (info != null) {
			return info;
		}

		return null;
	}

	/**
	 * 获取数据库的报纸数据
	 * 
	 * @param config
	 * @return
	 */
	public List<NewspaperMonthBean> getPaperFromDb(ArticleConfig config) {
		List<NewspaperMonthBean> mDbMonthData = new ArrayList<NewspaperMonthBean>();
		mDbMonthData = DatabaseUtils.findConditionPaperData("2015-12-52");

		return null;

	}

	/**
	 * 存储文章类型数据到数据库中
	 * 
	 * @param infos
	 */
	public void savaArticleToDb(List<ArticleInfo> infos) {
		for (int i = 0; i < infos.size(); i++) {
			ArticleInfo info = infos.get(i);
//			if (info.getList_show_control() != null
//					&& (info.getList_show_control().getDisplay_form() == 8
//					|| info.getList_show_control().getDisplay_form() == 9
//					|| info.getList_show_control().getDisplay_form() == 10)) {
//				Log.d("DATABASE", "广告数据不存储");
//
//			}else {
//				
//			}

			if (info.getType() != null && (info.getType().equals(ArticleType.AD_LG)
					|| info.getType().equals(ArticleType.AD_MD)
					|| info.getType().equals(ArticleType.AD_SM)
					|| info.getType().equals(ArticleType.AD_VIDEO))) {

				Log.d("DATABASE", "广告数据不存储");
			} else {
				// 查询存储的数据是否存在本地，如果存在就直接删除替换
				ArrayList<ArticleInfo> a = (ArrayList<ArticleInfo>) DataSupport
						.where("article_id = ?", info.getArticle_id() + "")
						.find(ArticleInfo.class);
				if (a != null && a.size() > 0) {
					Log.d("DATABASE", "服务器的数据本地已经有了");
					if ((a.get(0).getContent() == null || a.get(0).getContent()
							.equals(""))
							&& info.getContent() != null
							&& !info.getContent().equals("")) {
						Log.d("DATABASE", "更新详情内容");
						DatabaseUtils.updataArticleInfo(info, a.get(0).getId());
					} else if (info.getUpdated_at().equals(
							a.get(0).getUpdated_at())) {
						Log.d("DATABASE", "更新阅读数和点击数");
						DatabaseUtils.updataChangeArticleInfo(info, a.get(0)
								.getId());
					} else {
						DatabaseUtils.updataArticleInfo(info, a.get(0).getId());
					}

				} else {
					List<ChildrenArticlesInfo> children = info
							.getChildren_articles();
					DataSupport.saveAll(children);

					ArticleDisplayMode playMode = info.getArticleDispalyMode();
					if (playMode != null) {
						playMode.save();
						info.setList_show_control(playMode);
					}

					info.getChildren_articles().addAll(children);
					info.save();

					if (info.getType() != null
							&& info.getType().equals(ArticleType.IMAGE)) {
						Gallery gallery = new Gallery();
						gallery.setGallery_id(info.getGallery_id());
						gallery.setTitle(info.getTitle());
						gallery.setType(info.getType());
						gallery.setDesc(info.getDesc());
						gallery.setUrl(info.getUrl());
						gallery.setImage(info.getImage());
						gallery.setReview_count((int) info.getReview_count());
						gallery.setAllow_review(info.isAllow_review());

						List<ArticleImages> svImages = info.getImages();
						if (svImages != null) {
							for (int j = 0; j < svImages.size(); j++) {
								svImages.get(j).valueGalleryId();
							}
						}
						DataSupport.saveAll(svImages);
						gallery.getImages().addAll(svImages);
						gallery.save();
					}
				}
			}
		}
	}

	/**
	 * 存储文字详情页内容到数据库
	 */
	public void saveArtileDetailToDb(Long articleId, String articleDetail) {

		ArrayList<ArticleInfo> article = (ArrayList<ArticleInfo>) DataSupport
				.where("article_id = ?", articleId + "")
				.find(ArticleInfo.class);
		if (article != null && article.size() > 0) {
			DatabaseUtils.saveArticleDetail(articleDetail, article.get(0)
					.getId());
		}

	}

	/**
	 * 存储报纸在数据库
	 * 
	 * @param infos
	 */
	public void savaNewspaperToDb(List<NewspaperMonthBean> infos) {
		for (int i = 0; i < infos.size(); i++) {
			NewspaperMonthBean info = infos.get(i);

			// 查询存储的数据是否存在本地，如果存在就直接删除替换
			ArrayList<NewspaperMonthBean> a = (ArrayList<NewspaperMonthBean>) DataSupport
					.where("n_index = ?", info.getN_index()).find(
							NewspaperMonthBean.class);
			if (a != null && a.size() != 0) {
				Log.d("DATABASE", "服务器的数据本地已经有了");
				// DatabaseUtils.updataArticleInfo(info, a.get(0).getId());
			} else {
				List<NewspaperImage> children = info.getSections();
				for (int j = 0; j < children.size(); j++) {
					children.get(j).setN_index(info.getN_index());
				}
				DataSupport.saveAll(children);
				// info.getSections().addAll(children);
				info.save();
				// }
			}
		}
	}

	/**
	 * 存储报纸文章详情内容到数据库
	 * 
	 * @param infos
	 */
	public void savaNewspaperDailyToDb(List<NewspaperDailyBean> infos,
			String n_index) {
		for (int i = 0; i < infos.size(); i++) {
			NewspaperDailyBean info = infos.get(i);
			info.setN_index(n_index);

			List<ArticleInfo> dailyArticles = info.getArticles();
			if (dailyArticles != null) { // 当天某一页的文章不为空，进行存储操作
				for (int j = 0; j < dailyArticles.size(); j++) {
					ArticleInfo storeInfo = dailyArticles.get(j);
					ArrayList<ArticleInfo> a = (ArrayList<ArticleInfo>) DataSupport
							.where("article_id = ?",
									storeInfo.getArticle_id() + "").find(
									ArticleInfo.class);
					if (a != null && a.size() > 0) { // 如果对应的文章本地已经存在，则更新加上日期和页码
						ContentValues values = new ContentValues();
						values.put("n_index", n_index);
						values.put("page", info.getPage());
						DataSupport.update(ArticleInfo.class, values, a.get(0)
								.getId());
					} else { // 不存在，给数据加上日期页码，存储在数据库
						storeInfo.setN_index(n_index);
						storeInfo.setPage(info.getPage());
						storeInfo.save();
					}
				}
			}
			info.save();
		}
	}

	/**
	 * 获取本地的每日报纸的文章
	 * 
	 * @param dailyDate
	 * @return
	 */
	public List<NewspaperDailyBean> getDailyNewsPaper(String dailyDate) {

		// ArrayList<ArticleInfo> a = (ArrayList<ArticleInfo>) DataSupport
		// .where("article_id = ?", storeInfo.getArticle_id() + "")
		// .find(ArticleInfo.class);

		return null;
	}

	/**
	 * 存储专题列表的信息到数据库
	 */
	public void saveFeatureToDb(List<FeatureInfo> features) {
		for (int i = 0; i < features.size(); i++) {
			FeatureInfo info = features.get(i);

			// 查询存储的数据是否存在本地，如果存在就直接删除替换
			ArrayList<FeatureInfo> a = (ArrayList<FeatureInfo>) DataSupport
					.where("feature_id = ?", info.getFeature_id() + "").find(
							FeatureInfo.class);

			if (a != null && a.size() != 0) {
				Log.d("DATABASE", "服务器的数据本地已经有了");
				// DatabaseUtils.updataArticleInfo(info, a.get(0).getId());
			} else {
				info.save();
			}
		}
	}

	/**
	 * 获取专题列表从数据库中
	 * 
	 * @return
	 */
	public List<FeatureInfo> getFeatureFromDb() {
		List<FeatureInfo> list = DataSupport.order("feature_id" + " desc")
				.limit(10).find(FeatureInfo.class);
		return list;
	}

	/**
	 * 存储文章的收藏状态到数据库
	 */
	public static void saveArtileCollectionglToDb(Long articleId,
			boolean isCollection) {

		ArrayList<ArticleInfo> article = (ArrayList<ArticleInfo>) DataSupport
				.where("article_id = ?", articleId + "")
				.find(ArticleInfo.class);
		if (article != null && article.size() > 0) {

			ContentValues values = new ContentValues();
			values.put("isCollection", isCollection);
			DataSupport.update(ArticleInfo.class, values, article.get(0)
					.getId());

		}
	}

	public static List<ArticleInfo> getCollectionArticle(Boolean isImage) {

		if (isImage) {
			ArrayList<ArticleInfo> article = (ArrayList<ArticleInfo>) DataSupport
					.where("isCollection = ? and  type = ?", 1 + "", "Image")
					.find(ArticleInfo.class);
			for (int i = 0; i < article.size(); i++) {
				if (article.get(i).getGallery_id() > 0) {
					Gallery gallery = getGalleryFromDb(article.get(i)
							.getGallery_id());
					List<ArticleImages> dbImages = gallery.getImagesData();
					gallery.setImages(dbImages);
					article.get(i).setGallery(gallery);
				}
			}
			return article;
		} else {
			ArrayList<ArticleInfo> article = (ArrayList<ArticleInfo>) DataSupport
					.where("isCollection = ? and  type <> ?", 1 + "", "Image")
					.find(ArticleInfo.class);
			return article;
		}
	}

	public static List<NewspaperMonthBean> getNewsPaperPages(String date) {
		ArrayList<NewspaperMonthBean> paper = (ArrayList<NewspaperMonthBean>) DataSupport
				.where("n_index = ? ", date).find(NewspaperMonthBean.class);
		for (int i = 0; i < paper.size(); i++) {
			List<NewspaperImage> dbImages = paper.get(i).getSectionImages();
			if (dbImages != null) {
				paper.get(i).setSections(dbImages);
			}
		}
		return paper;
	}

	/**
	 * 获取本地收藏的所有文章
	 * 
	 * @return
	 */
	public static List<ArticleInfo> getCollectionsAll() {
		ArrayList<ArticleInfo> article = (ArrayList<ArticleInfo>) DataSupport
				.where("isCollection = ? ", 1 + "").find(ArticleInfo.class);
		return article;
	}

	/**
	 * 发送点击事件时对比时间是否超过30分钟
	 * 
	 * @param articleId
	 * @param time
	 * @param isScroll
	 *            是否是滚动新闻
	 * @return
	 */
	public static boolean storeClickTime(long articleId, long time,
			boolean isScroll) {
		ArrayList<ArticleInfo> article = (ArrayList<ArticleInfo>) DataSupport
				.where("article_id = ? ", articleId + "").find(
						ArticleInfo.class);
		if (article != null && article.size() > 0) {
			ArticleInfo info = article.get(0);
			long lastTime = info.getClick_time();
			long disCount = time - lastTime;
			long minCount = disCount / 1000 / 60;

			if ((isScroll && minCount > 10) || (!isScroll && minCount > 5)) {
				ContentValues values = new ContentValues();
				values.put("click_time", time);
				DataSupport.update(ArticleInfo.class, values, info.getId());
				return true;
			}
			// if (minCount > 30) { // 相差30分钟以上
			// ContentValues values = new ContentValues();
			// values.put("click_time", time);
			// DataSupport.update(ArticleInfo.class, values, info.getId());
			// return true;
			// }

			Log.e("CLICK ACTION not SEND==", "NOW TIME==" + minCount);
		}
		return false;
	}

	/**
	 * 存储图集数据到数据库
	 * 
	 * @param gallery
	 */
	public void saveGalleries(Gallery gallery) {
		ArrayList<Gallery> dataGallery = (ArrayList<Gallery>) DataSupport
				.where("gallery_id = ? ", gallery.getGallery_id() + "").find(
						Gallery.class);

		if (dataGallery != null && dataGallery.size() > 0) { // GALLERY存在了，更新评论数
			Gallery updateGallery = new Gallery();
			updateGallery.setAllow_review(gallery.isAllow_review());
			updateGallery.setReview_count(gallery.getReview_count());
			updateGallery.update(dataGallery.get(0).getId());
		} else {
			List<ArticleImages> svImages = gallery.getImages();
			if (svImages != null) {
				for (int j = 0; j < svImages.size(); j++) {
					svImages.get(j).valueGalleryId();
				}
			}
			DataSupport.saveAll(svImages);
			gallery.getImages().addAll(svImages);
			gallery.save();
		}
	}

	public void deleteNetDelete(List<Long> ids) {
		if (ids != null) {
			for (int i = 0; i < ids.size(); i++) {
				DataSupport.deleteAll(ArticleInfo.class, "article_id = ?",
						ids.get(i) + "");
			}
		}
	}

}
