package com.nbd.article.bean;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.util.Log;

/**
 * 文章的数据结构 可数据库操作
 * 
 * @author riche
 * 
 */
public class ArticleInfo extends DataSupport {

	/** 是否是滚动新闻 */
	private int is_rolling_news;
	/** 位置 */
	private long pos;
	/** 高亮否 7 RED */
	private int special;
	/** 标题 */
	private String title;
	/** 核心提示 */
	private String digest;
	/** 详情页面 */
	private String url;
	/** 创建时间 */
	private String created_at;
	/** 数据的类型分类 */
	private long column_id;
	/** 列组合号？ */
	private long columnist_id;
	/** 文章ID */
	private long id;
	/** 文章正文 */
	private String content;
	/** 文章头图 */
	private String image;
	/** 文章类型 */
	private String type;
	/** 文章点击数 */
	private long mobile_click_count;
	/** 评论数量 */
	private long review_count;
	/** 文章ID */
	private long article_id;
	/** 子标签的列表 */
	private List<ChildrenArticlesInfo> children_articles = new ArrayList<ChildrenArticlesInfo>();
	/** 图集的列表 */
	private List<ArticleImages> images = new ArrayList<ArticleImages>();

	/** 广告的位置 */
	private long ad_position;
	/** 广告的宽 */
	private String width;
	/** 广告的高 */
	private String height;
	/** 广告的简要概述 */
	private String desc;
	/** 广告的标签 */
	private ArticleTag tag;

	/** 视频ID */
	private String video_id;
	/** 视频图片 */
	private String thumbnail;
	/** 是否允许评论 */
	private boolean allow_review;

	/** 更新的时间 */
	private String updated_at;

	/** 专题活动文章的小标题 */
	private String list_title;
	/** 专题活动文章的自标题 */
	private String sub_title;
	/** 专题活动的作者 */
	private String ori_author;
	/** 第三作者 */
	private String third_author;
	/** 信息源 */
	private String ori_source;
	/** 作者 */
	private String author;
	/** 版权 */
	private int copyright;

	/** 收藏的标志 */
	private boolean isCollection;

	/** 如果是专题文章的话，有专题的id */
	private String feature_id;
	/** 文章跳转的类型，有跳转就优先跳转 */
	private String redirect_to;

	/** 存储点击发送点击事件的时间 */
	private long click_time;

	/** 报纸文章的日期和页码 */
	private String n_index;
	private String page;

	/** 图集的Id */
	private int gallery_id;

	private Gallery gallery;
	
	private ArticleDisplayMode list_show_control;
	
	public ArticleDisplayMode getList_show_control() {
		return list_show_control;
	}

	public void setList_show_control(ArticleDisplayMode list_show_control) {
		this.list_show_control = list_show_control;
	}

	public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}

	public int getGallery_id() {
		return gallery_id;
	}

	public void setGallery_id(int gallery_id) {
		this.gallery_id = gallery_id;
	}

	public String getN_index() {
		return n_index;
	}

	public void setN_index(String n_index) {
		this.n_index = n_index;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public long getClick_time() {
		return click_time;
	}

	public void setClick_time(long click_time) {
		this.click_time = click_time;
	}

	public String getFeature_id() {
		return feature_id;
	}

	public void setFeature_id(String feature_id) {
		this.feature_id = feature_id;
	}

	public String getRedirect_to() {
		return redirect_to;
	}

	public void setRedirect_to(String redirect_to) {
		this.redirect_to = redirect_to;
	}

	public boolean isCollection() {
		return isCollection;
	}

	public void setCollection(boolean isCollection) {
		this.isCollection = isCollection;
	}

	public String getList_title() {
		return list_title;
	}

	public void setList_title(String list_title) {
		this.list_title = list_title;
	}

	public String getSub_title() {
		return sub_title;
	}

	public void setSub_title(String sub_title) {
		this.sub_title = sub_title;
	}

	public String getOri_author() {
		return ori_author;
	}

	public void setOri_author(String ori_author) {
		this.ori_author = ori_author;
	}

	public String getThird_author() {
		return third_author;
	}

	public void setThird_author(String third_author) {
		this.third_author = third_author;
	}

	public String getOri_source() {
		return ori_source;
	}

	public void setOri_source(String ori_source) {
		this.ori_source = ori_source;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public int getCopyright() {
		return copyright;
	}

	public void setCopyright(int copyright) {
		this.copyright = copyright;
	}

	public String getVideo_id() {
		return video_id;
	}

	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public boolean isAllow_review() {
		return allow_review;
	}

	public void setAllow_review(boolean allow_review) {
		this.allow_review = allow_review;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public long getAd_position() {
		return ad_position;
	}

	public void setAd_position(long ad_position) {
		this.ad_position = ad_position;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public ArticleTag getTag() {
		return tag;
	}

	public void setTag(ArticleTag tag) {
		this.tag = tag;
	}

	private boolean isOpen;

	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	/**
	 * 获取关联表中的图集内容
	 * 
	 * @return 图集信息的列表
	 */
	public List<ArticleImages> getImagesData() {
		return DataSupport.where("articleinfo_id = ?", String.valueOf(id))
				.find(ArticleImages.class);
	}

	/**
	 * 根据文章的类型，当需要查询关联表的时候，获取关联表中的信息
	 * 
	 * @return 子文章数据类型的列表
	 */
	public List<ChildrenArticlesInfo> getChildrenArticlesData() {
		return DataSupport.where("articleinfo_id = ?", String.valueOf(id))
				.find(ChildrenArticlesInfo.class);
	}
	
	public ArticleDisplayMode getArticleDispalyMode(){
		List<ArticleDisplayMode> modes = DataSupport.where("articleinfo_id = ?", String.valueOf(id))
				.find(ArticleDisplayMode.class);
		
		if (modes != null && modes.size()>0) {
			return modes.get(0);
		}
		return null;
	}

	public List<ChildrenArticlesInfo> getChildren_articles() {
		return children_articles;
	}

	public List<ArticleImages> getImages() {
		return images;
	}

	public int getIs_rolling_news() {
		return is_rolling_news;
	}

	public void setIs_rolling_news(int is_rolling_news) {
		this.is_rolling_news = is_rolling_news;
	}

	public long getPos() {
		return pos;
	}

	public void setPos(long pos) {
		this.pos = pos;
	}

	public int getSpecial() {
		return special;
	}

	public void setSpecial(int special) {
		this.special = special;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public long getColumn_id() {
		return column_id;
	}

	public void setColumn_id(long column_id) {
		this.column_id = column_id;
	}

	public long getColumnist_id() {
		return columnist_id;
	}

	public void setColumnist_id(long columnist_id) {
		this.columnist_id = columnist_id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getMobile_click_count() {
		return mobile_click_count;
	}

	public void setMobile_click_count(long mobile_click_count) {
		this.mobile_click_count = mobile_click_count;
	}

	public long getReview_count() {
		return review_count;
	}

	public void setReview_count(long review_count) {
		this.review_count = review_count;
	}

	public long getArticle_id() {
		return article_id;
	}

	public void setArticle_id(long article_id) {
		this.article_id = article_id;
	}

	public void setChildren_articles(
			List<ChildrenArticlesInfo> children_articles) {
		this.children_articles = children_articles;
	}

	public void setImages(List<ArticleImages> images) {
		this.images = images;
	}

	@Override
	public String toString() {
		Log.d("ARTICLE_INFO", "is_rolling_news：" + is_rolling_news + "pos"
				+ pos + "specle:" + special + "title" + title + "digest"
				+ digest + "url" + url + "created_at" + created_at
				+ "column_id" + column_id + "digest" + digest + "digest" + id
				+ "content" + content + "image" + image + "type" + type
				+ "mobile_click_count" + mobile_click_count + "review_count"
				+ review_count + "article_id" + article_id);
		return super.toString();
	}

	public Gallery creatGalleryString() {
		Gallery gallery = new Gallery();
		gallery.setGallery_id(gallery_id);
		gallery.setType(type);
		gallery.setDesc(desc);
		gallery.setReview_count((int) review_count);
		gallery.setAllow_review(allow_review);
		gallery.setImages(images);
		return gallery;
	}

}
