package com.nbd.network.utility;

/**
 * 各个网络请求的URL静态地址数据
 * 
 * @author riche
 *
 */
public class Cst {
	/**快讯*/
	public final static String KX = "http://api.nbd.com.cn/3/columns/375/articles";
	/**快讯详情*/
	public final static String KXXQ = "http://api.nbd.com.cn/3/articles/";
	/**资讯列表*/
	public final static String ZX = "http://api.nbd.com.cn/3/columns/435/articles";
	/**资讯轮播*/
	public final static String ZXLB = "http://api.nbd.com.cn/3/columns/455/articles";
	/**资讯指数*/
	public final static String ZXZS = "http://api.nbd.com.cn/3/specials/stock_index";
	/**公司轮播*/
	public final static String GSLB = "http://api.nbd.com.cn/3/columns/456/articles";
	/**公司正价值*/
	public final static String GSZJZ = "http://api.nbd.com.cn/3/columns/457/articles";
	/**公司负价值*/
	public final static String GSFJZ = "http://api.nbd.com.cn/3/columns/458/articles";
	/**理财轮播*/
	public final static String LCLB = "http://api.nbd.com.cn/3/columns/459/articles";
	/**理财要闻*/
	public final static String LCYW = "http://api.nbd.com.cn/3/columns/460/articles";
	/**其实是 新三板的 地址*/
	public final static String IPO = "http://api.nbd.com.cn/3/columns/461/articles";
	/**视频*/
	public final static String VIDEO = "http://api.nbd.com.cn/3/columns/453/articles";
	/**加入app_key*/
	public final static String APP_KEY = "?app_key=ae1bd0a8b32505a86c0b20187f5093ec";

	/**查看具体文章的详情页接口*/
	public final static String ARTICLE_DETAIL =  "http://api.nbd.com.cn/3/articles/";
	/**获取月度报纸*/
	public final static String NEWSPAPER =  "http://api.nbd.com.cn/3/newspapers/";
	/**获取当天的报纸*/
	public final static String NEWSPAPER_MONTH =  "/month_list";
	/**获取当天的报纸*/
	public final static String NEWSPAPER_DAILY =  "/sections";
	/**专题页面数据接口*/
	public final static String FEATURE =  "http://api.nbd.com.cn/3/app_features";
	
	/**手机登陆*/
	public final static String LOGIN =  "http://api.nbd.com.cn/1/user/sign_in";
	/**微博登陆*/
	public final static String LOGIN_WEIBO =  "http://api.nbd.com.cn/3/auth/weibo/callback";
	/**微信登陆*/
	public final static String LOGIN_WEIXIN =  "http://api.nbd.com.cn/3/auth/wechat/callback";
	/**QQ登陆*/
	public final static String LOGIN_QQ =  "http://api.nbd.com.cn/3/auth/qq_connect/callback";
	/**手机注册获取验证码*/
	public final static String REGISTER_PHONE_GET_CODE =  "http://api.nbd.com.cn/3/specials/sms_captcha";
	/**根据验证码的注册方式*/
	public final static String REGISTER_BY_CODE =  "http://api.nbd.com.cn/3/user/sign_up";
	/**个人相关消息*/
	public final static String  MY_MESSAGE =  "http://api.nbd.com.cn/3/user/related_notification.json";
	/**个人相关通知 */
	public final static String SYS_MESSAGE =  "http://api.nbd.com.cn/3/user/system_notification.json";
	
	/**搜索热门词*/
	public final static String HOT_TAGS =  "http://api.nbd.com.cn/3/articles/hot_tags";
	/**根据关键字搜索文章*/
	public final static String SEARCH =  "http://api.nbd.com.cn/3/articles/search";
	/**收藏文章*/
	public final static String COLLECTION =  "http://api.nbd.com.cn/3/user/collection_articles";
	/**阅读文章文章*/
	public final static String READ =  "http://api.nbd.com.cn/3/user/reading_articles";
	
	/**修改密码，登录状态*/
	public final static String RESET_PASSWORD =  "http://api.nbd.com.cn/3/passwords/reset";
	/**上传头像*/
	public final static String UPLOAD_HEAD =  "http://api.nbd.com.cn/3/user/upload_avatar";
	/** 修改昵称*/
	public final static String UPDATE_NAME =  "http://api.nbd.com.cn/3/user/update_info";
	/**  获取评论  */
	public final static String GET_COMMENT =  "http://api.nbd.com.cn/3/";
	/**  开机广告  */
	public final static String OPEN_ADV =  "http://api.nbd.com.cn/3/ads/start_page";
	/**  意见反馈  */
	public final static String FEEDBACKS =  "http://api.nbd.com.cn/3/user/feedbacks";
	/**  创建评论  */
	public final static String COMMENT =  "http://api.nbd.com.cn/3/";
	/**  个人评论  */
	public final static String SELF_COMMENT =  "http://api.nbd.com.cn/3/user/my_reviews.json";
	/**  报名中心  */
	public final static String SIGN_CENTER =  "http://api.nbd.com.cn/3/app_activities/get_activity_lists";
	/**  清空消息  */
	public final static String CLEAR_MESSAGE =  "http://api.nbd.com.cn/3/user/";
	/**  新闻阅读点击  */
	public final static String NEWS_CLICK =  "http://api.nbd.com.cn/3/columns/mobile_click_count";
	/** 单个图集*/
	public final static String GALLERY =  "http://api.nbd.com.cn/3/galleries/";
	public final static String CLEAR_DELETE =  "http://api.nbd.com.cn/3/articles/delete_article_ids";
	
	
	
}
