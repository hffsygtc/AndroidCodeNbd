package com.nbd.article.article;

/**
 * 文章的类型
 * @author riche
 *
 */
public class ArticleType {
	/** 普通文章 根据是否有 "redirect_to" 字段解析跳转的方式 */
	public final static String ARTICLE = "Article";
	/** 广告大图 */
	public final static String AD_LG = "Ad_lg";
	/** 广告中图 和普通文章普通高度一样 */
	public final static String AD_MD = "Ad_md";
	/** 广告小图 和普通文章样式一样，带广告图标 */
	public final static String AD_SM = "Ad_sm";
	/**广告视频*/
	public final static String AD_VIDEO = "Ad_video";
	/** 视频文章 样式和普通文章一样，带视频图标 */
	public final static String VIDEO = "Video";
	/** 图片文章 三张图片的样式 */
	public final static String IMAGE = "Image";
	/** 图片文章 三张图片的样式 */
	public final static String GALLERY = "Gallery";
	/** 专题文章 普通文章样式，带专题图标 */
	public final static String FEATURE = "Feature";
	/** 推广文章 普通文章样式，带推广图标 */
	public final static String MARKET = "Market";
	/** 直播文章 普通文章样式，带直播图标 */
	public final static String LIVE = "Live";
	/** 专题图书馆里面的大图样式 */
	public final static String BOOK = "BigThumbnail";

}
