package com.nbd.network.bean;

/**
 * 网络请求数据类型
 * 
 * @author riche
 * 
 */
public enum RequestType {
	/** 登陆 */
	LOGIN,
	/** 快讯 */
	KX,
	/** 快讯详情 */
	KXXQ,
	/** 资讯列表 */
	ZX,
	/** 资讯轮播 */
	ZXLB,
	/** 资讯指数 */
	ZXZS,
	/** 公司轮播 */
	GSLB,
	/** 公司正价值 */
	GSZJZ,
	/** 公司负价值 */
	GSFJZ,
	/** 理财轮播 */
	LCLB,
	/** 理财要闻 */
	LCYW,
	/** 公司IPO */
	IPO,
	/** 新三版 */
	XSB,
	/** 文章详情 */
	ARTICLE_DETAIL,
	/** 原创报纸月数据 */
	NEWSPAPER_MONTH,
	/** 原创报纸日数据 */
	NEWSPAPER_DAILY,
	/** 活动专题页面 */
	FEATURE,
	/** 活动专题的详情 */
	FEATURE_DETAIL,
	/** 视频内容页面 */
	VIDEO,
	/** 微博登陆 */
	LOGIN_WEIBO,
	/** qq登陆 */
	LOGIN_QQ,
	/** 微信登陆 */
	LOGIN_WEIXIN,
	/** 手机注册获取验证码 */
	REGISTER_PHONE_GET_CODE,
	/** 根据手机注册账号 */
	REGISTER_BY_CODE,
	/** 个人相关消息 */
	MY_MESSAGE,
	/** 个人相关通知 */
	SYS_MESSAGE,
	/** 搜索热门词汇 */
	HOT_TAGS,
	/** 根据热门词汇搜索文章 */
	SEARCH,
	/** 文章被阅读了 */
	READING,
	/** 文章被收藏了 */
	COLLECTION,
	/** 修改密码 */
	RESET_PASSWORD,
	/** 修改昵称 */
	UPDATE_NAME,
	/** 获取评论列表 */
	GET_COMMENT,
	/** 开机广告 */
	OPEN_ADV,
	/** 意见反馈 */
	FEEDBACKS,
	/** 评论 */
	COMMENT,
	/**个人评论*/
	SELF_COMMENT,
	/**报名中心*/
	SIGN_CENTER,
	/**相关消息已读*/
	CLEAR_NOTICE,
	/**增加点击数*/
	ADD_CLICK_COUNT,
	/**图集ID获取图集的信息*/
	GALLERY,
	/**清除最近的50篇文章*/
	CLEAR_DELETE,
	/**上传错误日志*/
	UPLADO_CRASH,
	/** 编辑测试数据*/
	TEST_ACCOUNT

}
