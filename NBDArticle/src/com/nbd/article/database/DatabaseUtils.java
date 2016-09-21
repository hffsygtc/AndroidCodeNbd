package com.nbd.article.database;

import java.util.List;

import org.litepal.crud.DataSupport;

import android.content.ContentValues;
import android.util.Log;

import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.Gallery;
import com.nbd.article.bean.NewspaperMonthBean;

/**
 * 数据库基本操作方法库 基于litepal的再封装
 * 
 * @author riche
 *
 */
public class DatabaseUtils {

	/**
	 * 数据库数据的存储
	 * 
	 * @param info
	 *            直接存储数据库对象就可以了
	 */
	public static void saveData(ArticleInfo info) {
		info.save();
	}

	/**
	 * 数据库数据的批量存储
	 * 
	 * @param infos
	 *            数据对象的列表
	 */
	public static void saveAllData(List<ArticleInfo> infos) {
		DataSupport.saveAll(infos);

	}

	/**
	 * 删除单条数据库数据
	 * 
	 * @param position
	 */
	public static void deleteData(int position) {
		DataSupport.delete(ArticleInfo.class, position);
	}

	/**
	 * 删除指定条件的数据库数据
	 * 
	 * @param conditions
	 *            条件语句
	 */
	public static void deleteAll(String... conditions) {
		DataSupport.deleteAll(ArticleInfo.class, conditions);
	}

	/**
	 * 修改单条数据
	 * 
	 * @param values
	 *            修改属性的键值对
	 * @param id
	 *            数据库ID
	 */
	public static void updateData(ContentValues values, long id) {
		DataSupport.update(ArticleInfo.class, values, id);
	}

	/**
	 * 批量修改符合条件的数据库数据
	 * 
	 * @param values
	 *            修改属性的键值对
	 * @param conditions
	 *            条件语句
	 */
	public static void updateAllData(ContentValues values, String... conditions) {
		DataSupport.updateAll(ArticleInfo.class, values, conditions);
	}

	/**
	 * 查询数据库单条数据
	 * 
	 * @param position
	 *            查询数据的ID
	 * @return 文章的数据对象
	 */
	public static ArticleInfo findData(int position) {
		return DataSupport.find(ArticleInfo.class, position);
	}
	

	/**
	 * 查询ID数组中的数据列表
	 * 
	 * @param ids
	 *            数据ID的数组
	 * @return
	 */
	public static List<ArticleInfo> findListData(long[] ids) {
		return DataSupport.findAll(ArticleInfo.class, ids);
	}

	/**
	 * 查询指定类型的数据
	 * 
	 * @param type
	 *            咨询，快讯等栏目分类
	 * @param order
	 *            排序的字段
	 * @param count
	 *            一次查询数据量
	 * @param position
	 *            数据的偏移量
	 * @return
	 */
	public static List<ArticleInfo> findConditionData(long type,
			String order, int count, int position) {
		List<ArticleInfo> list = DataSupport
				.where("column_id = ?", type+"").order(order + " desc").limit(count)
				.offset(count * position).find(ArticleInfo.class);
		return list;

	}

	/**
	 * 查询指定日期的数据库数据
	 * @param day
	 * @return
	 */
	public static List<NewspaperMonthBean> findConditionPaperData(String day) {
		List<NewspaperMonthBean> list = DataSupport
				.where("n_index = ?", day).find(NewspaperMonthBean.class);
		if (list != null) {
			return list;
		}
		return null;
	}
	
	/**
	 *  查询指定ID的文章的内容
	 */
	public static ArticleInfo findArticleDetailData(long ArticleId) {
		
		List<ArticleInfo> list = DataSupport
				.where("article_id = ?", ArticleId+"").find(ArticleInfo.class);
		if (list != null && list.size()>0) {
			ArticleInfo a = list.get(0);
			return a;
		}
		return null;

	}
	
	
	/**
	 *  更新数据库里面的数据，保持ID不变
	 * @param info
	 * @param id
	 */
	public static void updataArticleInfo(ArticleInfo info,long id){
		ArticleInfo updateInfo = new ArticleInfo();
		updateInfo.setAd_position(info.getAd_position());
		updateInfo.setArticle_id(info.getArticle_id());
		updateInfo.setChildren_articles(info.getChildren_articles());
		updateInfo.setColumn_id(info.getColumn_id());
		updateInfo.setColumnist_id(info.getColumnist_id());
		updateInfo.setContent(info.getContent());
		updateInfo.setCreated_at(info.getCreated_at());
		updateInfo.setDesc(info.getDesc());
		updateInfo.setDigest(info.getDigest());
		updateInfo.setHeight(info.getHeight());
		updateInfo.setImage(info.getImage());
//		updateInfo.setImages(info.getImages());
		updateInfo.setIs_rolling_news(info.getIs_rolling_news());
		updateInfo.setMobile_click_count(info.getMobile_click_count());
		updateInfo.setOpen(info.isOpen());
		updateInfo.setPos(info.getPos());
		updateInfo.setReview_count(info.getReview_count());
		updateInfo.setSpecial(info.getSpecial());
		updateInfo.setTag(info.getTag());
		updateInfo.setTitle(info.getTitle());
		updateInfo.setType(info.getType());
		updateInfo.setUrl(info.getUrl());
		updateInfo.setWidth(info.getWidth());
		updateInfo.setGallery_id(info.getGallery_id());
		if (info.getList_show_control() != null) {
			updateInfo.setList_show_control(info.getList_show_control());
		}
		
		updateInfo.update(id);
	}

	/**
	 *  更新动态变化的字段，如阅读数，评论数等
	 * @param info
	 * @param id
	 */
	public static void updataChangeArticleInfo(ArticleInfo info,long id){
		ArticleInfo updateInfo = new ArticleInfo();
		updateInfo.setArticle_id(info.getArticle_id());
		updateInfo.setMobile_click_count(info.getMobile_click_count());
		updateInfo.setReview_count(info.getReview_count());
		updateInfo.setAllow_review(info.isAllow_review());
		if (info.getList_show_control() != null) {
			updateInfo.setList_show_control(info.getList_show_control());
		}
		updateInfo.update(id);
	}
	
	/**
	 *  更新数据库里面的数据，保持ID不变
	 * @param info
	 * @param id
	 */
	public static void saveArticleDetail(String content,long id){
		
		Log.e("SAVE TO DB", id+content);
		
		ContentValues values = new ContentValues();  
		values.put("content", content);  
		DataSupport.update(ArticleInfo.class, values, id);  
	}
	
}
