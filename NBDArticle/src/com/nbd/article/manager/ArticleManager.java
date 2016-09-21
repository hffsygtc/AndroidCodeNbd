package com.nbd.article.manager;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.nbd.article.article.ArticleType;
import com.nbd.article.article.LoginType;
import com.nbd.article.bean.ActivityListBean;
import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.FeatureDetail;
import com.nbd.article.bean.FeatureInfo;
import com.nbd.article.bean.Gallery;
import com.nbd.article.bean.LoginConfig;
import com.nbd.article.bean.MyMessageBean;
import com.nbd.article.bean.NewspaperDailyBean;
import com.nbd.article.bean.NewspaperMonthBean;
import com.nbd.article.bean.OpenAdvBean;
import com.nbd.article.bean.SingleComment;
import com.nbd.article.bean.StockIndex;
import com.nbd.article.bean.UserInfo;
import com.nbd.article.bean.commentBean;
import com.nbd.article.database.DatabaseManager;
import com.nbd.article.managercallback.ActivityCallback;
import com.nbd.article.managercallback.ArticleInfoCallback;
import com.nbd.article.managercallback.CommentCallback;
import com.nbd.article.managercallback.FeatureDetailCallback;
import com.nbd.article.managercallback.FeatureInfoCallback;
import com.nbd.article.managercallback.GalleryInfoCallback;
import com.nbd.article.managercallback.MessageInfoCallback;
import com.nbd.article.managercallback.NewspaperDailyCallback;
import com.nbd.article.managercallback.NewspaperMonthCallback;
import com.nbd.article.managercallback.OpenAdvCallback;
import com.nbd.article.managercallback.SelfCommentCallback;
import com.nbd.article.managercallback.StockInfoCallback;
import com.nbd.article.managercallback.StringInfoCallback;
import com.nbd.article.managercallback.UserInfoCallback;
import com.nbd.article.utility.JsonUtil;
import com.nbd.network.bean.CrashBean;
import com.nbd.network.bean.RequestType;
import com.nbd.network.bean.RequestWrapper;
import com.nbd.network.bean.ResponseWrapper;
import com.nbd.network.networkprivoder.Network;
import com.nbd.network.networkprivoder.NetworkBase;
import com.nbd.network.networkprivoder.RequestListener;

/**
 * 数据管理类，沟通视图层，为视图层所用 内部分本地数据库数据获取和网络数据获取
 * 
 * @author riche
 * 
 */
public class ArticleManager {
	private final String TAG = "ArticleManager";
	private NetworkBase network; // 网络对象，连接网络层
	private DatabaseManager dbManager; // 数据库数据处理对象
	static private ArticleManager instance;// 此对象沟通视图层,为视图层所用
	private Context context;
	private String versionCode = "3.1.0";

	private ArticleManager(Context context) {
		this.context = context;
		PackageManager pm = context.getPackageManager();// 获得包管理器
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				versionCode = pi.versionName == null ? "3.1.0" : pi.versionName;
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}// 得到该应用的信息，即主Activity
		network = new Network(context, versionCode);
		dbManager = new DatabaseManager(context);

	}

	public static ArticleManager getInstence() {
		return instance;
	}

	public static void init(Context context) {
		if (null == instance) {
			instance = new ArticleManager(context);
		}
	}

	/**
	 * 因为文章返回的数据结构是一样的，所有开一个获取文章信息的方法
	 * 
	 * @param config
	 *            请求的基本参数
	 * @param dataLocal
	 *            请求的数据来源 本地或者网络
	 * @param callback
	 *            原始数据的返回
	 */
	public void getArticleInfo(final ArticleConfig config,
			final ArticleInfoCallback callback) {

		if (config.isLocalArticle()) { // 获取本地数据库数据
			Log.d(TAG,
					"--------->ArticleInfo LOCAL<----------" + config.getType());

			List<ArticleInfo> infos = dbManager.getArticleFromDb(config);
			callback.onArticleInfoCallback(infos, config.getType());

		} else { // 请求网络数据
			RequestWrapper request = new RequestWrapper(config.getType());
			if (config.getMaxId() > 0) {
				request.setMaxId(config.getMaxId());
			}
			if (config.getType().equals(RequestType.TEST_ACCOUNT)) {
				request.setAccessToken(config.getAccessToken());
				request.setPage(config.getPageCount());
				request.setCount(config.getPagePositon());
			}
			network.requestData(request, new RequestListener() {
				@Override
				public void onResponse(ResponseWrapper response) {
					Log.d(TAG,
							"--------->ArticleInfo NET<----------"
									+ response.getRequestType()
									+ response.getResponseData());

					if (response.isError()) { // 请求有错误
						callback.onArticleInfoCallback(null, config.getType());
					} else {
						List<ArticleInfo> infos = new ArrayList<ArticleInfo>();
						List<ArticleInfo> controls = JsonUtil.fromJson(
								response.getResponseData(),
								new TypeToken<List<ArticleInfo>>() {
								}.getType());

						if (controls != null) {
							for (int i = 0; i < controls.size(); i++) {
								if (controls.get(i).getType() == null
										|| controls.get(i).getType().equals("")) {
									controls.remove(i);
								} else {
									if (controls.get(i).getType()
											.equals(ArticleType.IMAGE)) {
										Gallery gallery = controls.get(i)
												.creatGalleryString();
										controls.get(i).setGallery(gallery);
									}
									
									if (controls.get(i).getList_show_control() != null) {
										List<String> images = controls.get(i).getList_show_control().getImages();
										if (images != null) {
											switch (images.size()) {
											case 0:
												controls.get(i).getList_show_control().setImageOne("");
												controls.get(i).getList_show_control().setImageTwo("");
												controls.get(i).getList_show_control().setImageThree("");
												break;
											case 1:
												controls.get(i).getList_show_control().setImageOne(images.get(0) != null ?  images.get(0) : "");
												controls.get(i).getList_show_control().setImageTwo("");
												controls.get(i).getList_show_control().setImageThree("");
												break;
											case 2:
												controls.get(i).getList_show_control().setImageOne(images.get(0) != null ?  images.get(0) : "");
												controls.get(i).getList_show_control().setImageTwo(images.get(1) != null ?  images.get(1) : "");
												controls.get(i).getList_show_control().setImageThree("");
												break;
											default:
												controls.get(i).getList_show_control().setImageOne(images.get(0) != null ?  images.get(0) : "");
												controls.get(i).getList_show_control().setImageTwo(images.get(1) != null ?  images.get(1) : "");
												controls.get(i).getList_show_control().setImageThree(images.get(2) != null ?  images.get(2) : "");
												break;
											}
										}
									}
								}
							}

							dbManager.savaArticleToDb(controls);
							callback.onArticleInfoCallback(controls,
									config.getType());
						}
					}
				}

				@Override
				public void onErrorResponse() {
					callback.onArticleInfoCallback(null, config.getType());
				}
			});
		}
	}

	/**
	 * 获取证券信息的方法，证券信息的返回的数据结构和文章不一样
	 * 
	 * @param config
	 *            请求的基本参数
	 * @param dataLocal
	 *            请求的数据来源 本地或者网络
	 * @param callback
	 *            原始数据的返回
	 */
	public void getStockInfo(final ArticleConfig config,
			final StockInfoCallback callback) {

		if (config.isLocalArticle()) { // 获取本地数据库数据

			// TODO 证券取实时数据，不取数据库

		} else { // 请求网络数据

			RequestWrapper request = new RequestWrapper(config.getType());

			network.requestData(request, new RequestListener() {

				@Override
				public void onResponse(ResponseWrapper response) {
					Log.d(TAG, "Artivle>>>>>>" + response.getRequestType()
							+ response.getResponseData());
					List<StockIndex> infos = new ArrayList<StockIndex>();

					List<StockIndex> controls = JsonUtil.fromJson(
							response.getResponseData(),
							new TypeToken<List<StockIndex>>() {
							}.getType());

					callback.onStockInfoCallback(controls, config.getType());
				}

				@Override
				public void onErrorResponse() {
					callback.onStockInfoCallback(null, config.getType());

				}
			});
		}
	}

	/**
	 * 获取文章详情内容方法
	 * 
	 * @param config
	 *            请求的基本参数
	 * @param dataLocal
	 *            请求的数据来源 本地或者网络
	 * @param callback
	 *            原始数据的返回
	 */
	public void getArticleDetail(final ArticleConfig config,
			final ArticleInfoCallback callback) {

		// 取数据库内的本地数据
		if (config.isLocalArticle()) {
			ArticleInfo art = DatabaseManager.getArticleDetailFromDb(config
					.getArticleId());
			List<ArticleInfo> returnArticles = new ArrayList<ArticleInfo>();
			if (art != null) {
				if (art.getContent() != null && !art.getContent().equals("")) {
					Log.d(TAG,
							"--------->ArticleDetail  LOCAL Ok with Content<----------");
					returnArticles.add(art);
					callback.onArticleInfoCallback(returnArticles,
							RequestType.ARTICLE_DETAIL);
				} else {
					Log.d(TAG,
							"--------->ArticleDetail  LOCAL Ok without Content<----------");
					callback.onArticleInfoCallback(null,
							RequestType.ARTICLE_DETAIL);
				}
			} else {
				Log.d(TAG, "--------->ArticleDetail  LOCAL Null<----------");
				callback.onArticleInfoCallback(null, RequestType.ARTICLE_DETAIL);
			}
		} else {

			RequestWrapper request = new RequestWrapper(config.getType());
			request.setArticleId(config.getArticleId());
			request.setCommenString(config.getCustomString());
			network.requestData(request, new RequestListener() {

				@Override
				public void onResponse(ResponseWrapper response) {
					Log.d(TAG,
							"--------->ArticleDetail  NET <----------"
									+ response.getRequestType()
									+ response.getResponseData());

					if (response.isError()) { // 有错误码的情况
						callback.onArticleInfoCallback(null,
								RequestType.ARTICLE_DETAIL);
					} else { // 请求成功，看解析对象是否为空判断是否有更新

						List<ArticleInfo> infos = new ArrayList<ArticleInfo>();
						List<ArticleInfo> controls = JsonUtil.fromJson(
								response.getResponseData(),
								new TypeToken<List<ArticleInfo>>() {
								}.getType());
						if (controls != null && controls.size() > 0) {
							callback.onArticleInfoCallback(controls,
									RequestType.ARTICLE_DETAIL);
							// TODO 本地数据化处理
							dbManager.savaArticleToDb(controls);
						} else {
							callback.onArticleInfoCallback(null,
									RequestType.ARTICLE_DETAIL);
						}
					}
				}

				@Override
				public void onErrorResponse() {
					// TODO Fail()
					callback.onArticleInfoCallback(null,
							RequestType.ARTICLE_DETAIL);
				}
			});
		}
	}

	/**
	 * 获取月度报纸新闻图片的方法
	 * 
	 * @param config
	 *            请求的基本参数
	 * @param dataLocal
	 *            请求的数据来源 本地或者网络
	 * @param callback
	 *            原始数据的返回
	 */
	public void getMonthNewspaper(final ArticleConfig config,
			final NewspaperMonthCallback callback) {

		if (config.isLocalArticle()) { // 存取本地的数据库

			List<NewspaperMonthBean> infos = DatabaseManager
					.getNewsPaperPages(config.getPaperDate());
			if (infos != null) {
				Log.d(TAG, "--------->MonthPaper  Local Ok <----------");
				callback.onMonthPaperCallback(infos);
			} else {
				Log.d(TAG, "--------->MonthPaper  Local Null <----------");
				callback.onMonthPaperCallback(null);
			}

		} else { // 网络请求
			RequestWrapper request = new RequestWrapper(config.getType());
			request.setNewspaper_month(config.getPaperMonth());
			request.setNewspaper_date(config.getPaperDate());

			network.requestData(request, new RequestListener() {

				@Override
				public void onResponse(ResponseWrapper response) {
					Log.d(TAG,
							"--------->MonthPaper Net Return <----------"
									+ response.getRequestType()
									+ response.getResponseData());

					if (response.isError()) {
						callback.onMonthPaperCallback(null);
					} else {
						List<NewspaperMonthBean> infos = new ArrayList<NewspaperMonthBean>();

						List<NewspaperMonthBean> controls = JsonUtil.fromJson(
								response.getResponseData(),
								new TypeToken<List<NewspaperMonthBean>>() {
								}.getType());

						if (controls != null) {
							for (int i = 0; i < controls.size(); i++) {
								if (controls.get(i).getN_index() == null
										|| controls.get(i).getN_index()
												.equals("")) {
									controls.remove(i);
								}
							}
							callback.onMonthPaperCallback(controls);

							dbManager.savaNewspaperToDb(controls);
						}
					}
					// TODO Success()
					// TODO 本地数据化处理
					// callback.onArticleInfoCallback(infos);

				}

				@Override
				public void onErrorResponse() {
					// TODO Fail()
					callback.onMonthPaperCallback(null);
					// callback.onArticleDetail(null);

				}
			});
		}
	}

	/**
	 * 获取单天的报纸的数据
	 * 
	 * @param config
	 *            请求的基本参数
	 * @param dataLocal
	 *            请求的数据来源 本地或者网络
	 * @param callback
	 *            原始数据的返回
	 */
	public void getDailyNewspaper(final ArticleConfig config,
			final NewspaperDailyCallback callback) {

		final RequestWrapper request = new RequestWrapper(config.getType());
		request.setNewspaper_date(config.getPaperDate());

		network.requestData(request, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				Log.d(TAG,
						"--------->DailyPaper Net Return <----------"
								+ response.getRequestType()
								+ response.getResponseData());
				if (response.isError()) {
					callback.onDailyNewsCallback(null);
				} else {
					List<NewspaperDailyBean> infos = new ArrayList<NewspaperDailyBean>();

					List<NewspaperDailyBean> controls = JsonUtil.fromJson(
							response.getResponseData(),
							new TypeToken<List<NewspaperDailyBean>>() {
							}.getType());

					callback.onDailyNewsCallback(controls);
					dbManager.savaNewspaperDailyToDb(controls,
							request.getNewspaper_date());
				}
				// dbManager.savaNewspaperToDb(controls);

			}

			@Override
			public void onErrorResponse() {
				callback.onDailyNewsCallback(null);
				// TODO Fail()
				// callback.onArticleDetail(null);

			}
		});
	}

	/**
	 * 获取活动专题数据的接口
	 * 
	 * @param config
	 *            请求的基本参数
	 * @param dataLocal
	 *            请求的数据来源 本地或者网络
	 * @param callback
	 *            原始数据的返回
	 */
	public void getFeatureList(final ArticleConfig config,
			final FeatureInfoCallback callback) {

		// 取数据库内的本地数据
		if (config.isLocalArticle()) {
			List<FeatureInfo> featureInfos = dbManager.getFeatureFromDb();
			if (featureInfos != null) {
				Log.d(TAG, "--------->Feature Locak Ok <----------");
				callback.onArticleInfoCallback(featureInfos, config.getType());
			} else {
				Log.d(TAG, "--------->Feature Locak null <----------");
				callback.onArticleInfoCallback(null, config.getType());
			}

		} else {
			RequestWrapper request = new RequestWrapper(config.getType());

			if (config.getMaxId() > 0) {
				request.setMaxId(config.getMaxId());
			}

			network.requestData(request, new RequestListener() {

				@Override
				public void onResponse(ResponseWrapper response) {
					Log.d(TAG,
							"--------->Feature Net Return <----------"
									+ response.getRequestType()
									+ response.getResponseData());

					if (response.isError()) {
						callback.onArticleInfoCallback(null, config.getType());
					} else {
						List<FeatureInfo> infos = new ArrayList<FeatureInfo>();

						List<FeatureInfo> controls = JsonUtil.fromJson(
								response.getResponseData(),
								new TypeToken<List<FeatureInfo>>() {
								}.getType());

						callback.onArticleInfoCallback(controls,
								response.getRequestType());

						dbManager.saveFeatureToDb(controls);
					}

					// dbManager.saveToDb(response.getRequestType(), controls);
					// TODO Success()
					// TODO 本地数据化处理
					// callback.onArticleInfoCallback(infos);

				}

				@Override
				public void onErrorResponse() {
					// TODO Fail()
					callback.onArticleInfoCallback(null, RequestType.FEATURE);

				}
			});
		}
	}

	/**
	 * 获取活动专题的详情数据
	 * 
	 * @param config
	 */
	public void getFeatureDetail(ArticleConfig config,
			final FeatureDetailCallback callback) {

		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setArticleId(config.getArticleId());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				Log.d(TAG,
						"--------->FeatureDetail Net Return <----------"
								+ response.getRequestType()
								+ response.getResponseData());

				if (response.isError()) {
					callback.onFeatureDetailCallback(null);
				} else {
					if (response.getResponseData() != null) {
						FeatureDetail detail = JsonUtil.fromJson(
								response.getResponseData(),
								new TypeToken<FeatureDetail>() {
								}.getType());
						if (detail != null) {
							callback.onFeatureDetailCallback(detail);
						}
					}
				}
			}

			@Override
			public void onErrorResponse() {
				callback.onFeatureDetailCallback(null);
				// TODO Auto-generated method stub
			}
		});
	}

	/**
	 * 获取单个文章数据
	 * 
	 * @param articleId
	 * @return
	 */
	public ArticleInfo getShareArticleInfo(long articleId) {

		return DatabaseManager.getArticleDetailFromDb(articleId);

	}

	/**
	 * 第三方登陆的服务器
	 * 
	 * @param token
	 * @param openid
	 * @param type
	 */
	public void Login(LoginConfig config, LoginType type,
			final UserInfoCallback callback) {

		switch (type) {
		case WEIBO:
			RequestWrapper weiboRequest = new RequestWrapper(
					RequestType.LOGIN_WEIBO);
			weiboRequest.setLogin_token(config.getToken());
			weiboRequest.setLogin_openid(config.getOpenId());
			weiboRequest.setCommenFlag(config.isBlindAccount());
			weiboRequest.setAccessToken(config.getUserToken());
			network.requestData(weiboRequest, new RequestListener() {

				@Override
				public void onResponse(ResponseWrapper response) {

					if (response.isError()) {
						Log.d(TAG,
								"--------->Login Net Return Error <----------"
										+ response.getResponseData());
						callback.onUserinfoCallback(null,
								response.getResponseData());
					} else {
						Log.d(TAG,
								"--------->Login Net Return Ok <----------"
										+ response.getRequestType()
										+ response.getResponseData());
						UserInfo user = JsonUtil.fromJson(
								response.getResponseData(),
								new TypeToken<UserInfo>() {
								}.getType());

						callback.onUserinfoCallback(user, null);

					}

				}

				@Override
				public void onErrorResponse() {

					callback.onUserinfoCallback(null, null);
				}
			});
			break;
		case QQ:
			RequestWrapper qqRequest = new RequestWrapper(RequestType.LOGIN_QQ);
			qqRequest.setLogin_token(config.getToken());
			qqRequest.setLogin_openid(config.getOpenId());
			qqRequest.setCommenFlag(config.isBlindAccount());
			qqRequest.setAccessToken(config.getUserToken());
			network.requestData(qqRequest, new RequestListener() {

				@Override
				public void onResponse(ResponseWrapper response) {

					if (response.isError()) {
						Log.d(TAG,
								"--------->Login Net Return Error <----------"
										+ response.getResponseData());
						callback.onUserinfoCallback(null,
								response.getResponseData());
					} else {
						Log.d(TAG,
								"--------->Login Net Return Ok <----------"
										+ response.getRequestType()
										+ response.getResponseData());
						UserInfo user = JsonUtil.fromJson(
								response.getResponseData(),
								new TypeToken<UserInfo>() {
								}.getType());

						callback.onUserinfoCallback(user, null);

					}

				}

				@Override
				public void onErrorResponse() {
					callback.onUserinfoCallback(null, null);
				}
			});
			break;
		case WEIXIN:
			RequestWrapper weixinRequest = new RequestWrapper(
					RequestType.LOGIN_WEIXIN);
			weixinRequest.setLogin_token(config.getToken());
			weixinRequest.setLogin_openid(config.getOpenId());
			weixinRequest.setCommenFlag(config.isBlindAccount());
			weixinRequest.setAccessToken(config.getUserToken());
			network.requestData(weixinRequest, new RequestListener() {

				@Override
				public void onResponse(ResponseWrapper response) {

					if (response.isError()) {
						Log.d(TAG,
								"--------->Login Net Return Error <----------"
										+ response.getResponseData());
						callback.onUserinfoCallback(null,
								response.getResponseData());
					} else {
						Log.d(TAG,
								"--------->Login Net Return Ok <----------"
										+ response.getRequestType()
										+ response.getResponseData());
						UserInfo user = JsonUtil.fromJson(
								response.getResponseData(),
								new TypeToken<UserInfo>() {
								}.getType());

						callback.onUserinfoCallback(user, null);

					}

				}

				@Override
				public void onErrorResponse() {
					callback.onUserinfoCallback(null, null);
				}
			});
			break;
		case PHONE:
			RequestWrapper phoneRequest = new RequestWrapper(RequestType.LOGIN);
			phoneRequest.setUserId(config.getUserId());
			phoneRequest.setPassword(config.getPassword());
			network.requestData(phoneRequest, new RequestListener() {

				@Override
				public void onResponse(ResponseWrapper response) {

					if (response.isError()) {
						Log.d(TAG,
								"--------->Login Net Return Error <----------"
										+ response.getResponseData());
						callback.onUserinfoCallback(null,
								response.getResponseData());
					} else {
						Log.d(TAG,
								"--------->Login Net Return Ok <----------"
										+ response.getRequestType()
										+ response.getResponseData());
						UserInfo user = JsonUtil.fromJson(
								response.getResponseData(),
								new TypeToken<UserInfo>() {
								}.getType());

						callback.onUserinfoCallback(user, null);
					}
				}

				@Override
				public void onErrorResponse() {
					callback.onUserinfoCallback(null, null);

				}
			});

			break;
		default:
			break;
		}

	}

	/**
	 * 获取注册的时候手机的验证码
	 * 
	 * @param phoneNum
	 * @param callback
	 */
	public void getPhoneCode(String phoneNum, boolean isRegister,
			final StringInfoCallback callback) {

		RequestWrapper phoneRequest = new RequestWrapper(
				RequestType.REGISTER_PHONE_GET_CODE);
		phoneRequest.setPhoneNum(phoneNum);
		phoneRequest.setCommenFlag(isRegister);
		network.requestData(phoneRequest, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {

				if (response != null) {
					if (response.isError()) {
						Log.d(TAG,
								"--------->PhoneCode Net Return Error <----------"
										+ response.getResponseData());
						callback.onStringDataCallback(
								response.getResponseData(), false);
					} else {
						Log.d(TAG,
								"--------->PhoneCode Net Return Ok <----------"
										+ response.getRequestType()
										+ response.getResponseData());
						try {
							JSONObject codeJson = new JSONObject(response
									.getResponseData());
							// {"smsid":"353331224","sms_captcha":"621819"}
							String code = codeJson.getString("sms_captcha");
							callback.onStringDataCallback(code, true);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}

			@Override
			public void onErrorResponse() {
				callback.onStringDataCallback(null, false);
			}
		});

	}

	/**
	 * 注册的时候获取注册返回的初始用户信息
	 * 
	 * @param phoneNum
	 * @param code
	 * @param password
	 * @param callback
	 */
	public void getRegisterResult(String phoneNum, String code,
			String password, final UserInfoCallback callback) {
		RequestWrapper registerRequest = new RequestWrapper(
				RequestType.REGISTER_BY_CODE);
		registerRequest.setPhoneNum(phoneNum);
		registerRequest.setRegisterCode(code);
		registerRequest.setRegisterPassword(password);
		network.requestData(registerRequest, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				if (response.isError()) {
					Log.d(TAG, "--------->Register Net Return Error<----------"
							+ response.getResponseData());
					callback.onUserinfoCallback(null,
							response.getResponseData());
				} else {
					Log.d(TAG,
							"--------->Register Net Return Ok <----------"
									+ response.getRequestType()
									+ response.getResponseData());
					UserInfo user = JsonUtil.fromJson(
							response.getResponseData(),
							new TypeToken<UserInfo>() {
							}.getType());

					callback.onUserinfoCallback(user, null);
				}
			}

			@Override
			public void onErrorResponse() {
				callback.onUserinfoCallback(null, null);

			}
		});
	}

	/**
	 * 忘记密码是重置密码
	 * 
	 * @param config
	 * @param callback
	 */
	public void resetPassword(ArticleConfig config,
			final StringInfoCallback callback) {
		if (config.isLocalArticle()) { // 用这个字段区别是注册时还是忘记密码时
			RequestWrapper resetRequest = new RequestWrapper(
					RequestType.RESET_PASSWORD);
			resetRequest.setPassword(config.getPassword());
			resetRequest.setNewPassword(config.getNewPassword());
			resetRequest.setPhoneNum(config.getAccessToken());
			network.requestData(resetRequest, new RequestListener() {

				@Override
				public void onResponse(ResponseWrapper response) {

					if (response.isError()) {
						Log.d(TAG,
								"--------->SetPwd Net Return Error <----------"
										+ response.getResponseData());
						callback.onStringDataCallback(
								response.getResponseData(), false);
					} else {
						Log.d(TAG, "--------->SetPwd Net Return Ok <----------"
								+ response.getRequestType());
						callback.onStringDataCallback(null, true);
					}

				}

				@Override
				public void onErrorResponse() {
					callback.onStringDataCallback(null, false);
				}
			});

		} else {
			RequestWrapper resetRequest = new RequestWrapper(
					RequestType.RESET_PASSWORD);
			resetRequest.setPassword(config.getPassword());
			resetRequest.setNewPassword(config.getNewPassword());
			resetRequest.setAccessToken(config.getAccessToken());
			network.requestData(resetRequest, new RequestListener() {

				@Override
				public void onResponse(ResponseWrapper response) {

					if (response.isError()) {
						Log.d(TAG,
								"--------->ResetPwd Net Return Error <----------"
										+ response.getResponseData());
						callback.onStringDataCallback(
								response.getResponseData(), false);
					} else {
						Log.d(TAG,
								"--------->ResetPwd Net Return Ok <----------"
										+ response.getRequestType());
						callback.onStringDataCallback(null, true);
					}

				}

				@Override
				public void onErrorResponse() {
					callback.onStringDataCallback(null, false);
				}
			});
		}
	}

	/**
	 * 获取系统消息
	 * 
	 * @param config
	 * @param callback
	 */
	public void getRelativeMessage(ArticleConfig config,
			final MessageInfoCallback callback) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setAccessToken(config.getAccessToken());
		wrapper.setPage(config.getPageCount());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				Log.d(TAG,
						"--------->ReletiveMsg Net Return Ok <----------"
								+ response.getRequestType()
								+ response.getResponseData());
				if (response.isError()) {
					callback.onMyMessageCallback(null);
				} else {
					List<MyMessageBean> msg = JsonUtil.fromJson(
							response.getResponseData(),
							new TypeToken<List<MyMessageBean>>() {
							}.getType());

					callback.onMyMessageCallback(msg);
				}

			}

			@Override
			public void onErrorResponse() {
				callback.onMyMessageCallback(null);

			}
		});

	}

	/**
	 * 获取个人消息
	 * 
	 * @param config
	 * @param callback
	 */
	public void getRelativeComment(ArticleConfig config,
			final SelfCommentCallback callback) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setAccessToken(config.getAccessToken());
		wrapper.setPage(config.getPageCount());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				Log.d(TAG,
						"--------->SelfComment Net Return Ok <----------"
								+ response.getRequestType()
								+ response.getResponseData());

				if (response.isError()) {
					callback.onSelfCommentCallback(null,
							response.getResponseData());

				} else {
					List<SingleComment> comments = JsonUtil.fromJson(
							response.getResponseData(),
							new TypeToken<List<SingleComment>>() {
							}.getType());

					callback.onSelfCommentCallback(comments, null);
				}
			}

			@Override
			public void onErrorResponse() {
				callback.onSelfCommentCallback(null, null);
			}
		});

	}

	/**
	 * 获取搜索的热门词汇标签
	 */
	public void getHotTags(ArticleConfig config,
			final StringInfoCallback callback) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {

				Log.d(TAG,
						"--------->HotTags Net Return Ok <----------"
								+ response.getRequestType()
								+ response.getResponseData());
				if (response.getResponseData() != null) {
					callback.onStringDataCallback(response.getResponseData(),
							true);
				} else {
					callback.onStringDataCallback(null, true);
				}
			}

			@Override
			public void onErrorResponse() {
				callback.onStringDataCallback("ERROR", false);

			}
		});
	}

	/**
	 * 将文章读阅读操作上传到服务器
	 */
	public void setArticleRead(ArticleConfig config,
			final UserInfoCallback callback) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setArticleId(config.getArticleId());
		wrapper.setAccessToken(config.getAccessToken());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				Log.d(TAG,
						"--------->ArticleRead Net Return Ok <----------"
								+ response.getRequestType()
								+ response.getResponseData());
				if (response.isError()) {
					callback.onUserinfoCallback(null,
							response.getResponseData());

				} else {
					UserInfo user = JsonUtil.fromJson(
							response.getResponseData(),
							new TypeToken<UserInfo>() {
							}.getType());

					callback.onUserinfoCallback(user, null);
				}

			}

			@Override
			public void onErrorResponse() {
				callback.onUserinfoCallback(null, null);
			}
		});

	}

	/**
	 * 将文章收藏操作上传到服务器
	 */
	public void setArticleCollection(final ArticleConfig config,
			final UserInfoCallback callback) {
		if (config.isLocalArticle()) { // 本地操作,未登录的状态
			if (config.isCollection()) {
				DatabaseManager.saveArtileCollectionglToDb(
						config.getArticleId(), true);
			} else {
				DatabaseManager.saveArtileCollectionglToDb(
						config.getArticleId(), false);
			}
		} else {
			RequestWrapper wrapper = new RequestWrapper(config.getType());
			wrapper.setArticleId(config.getArticleId());
			wrapper.setAccessToken(config.getAccessToken());
			wrapper.setCommenString(config.getCustomString());
			if (config.isCollection()) {
				wrapper.setRead_collect_state("add");
			} else {
				wrapper.setRead_collect_state("del");
			}

			network.requestData(wrapper, new RequestListener() {

				@Override
				public void onResponse(ResponseWrapper response) {

					if (response.isError()) {
						Log.d(TAG,
								"--------->Collections Net Return Error<----------"
										+ response.getResponseData());
						callback.onUserinfoCallback(null,
								response.getResponseData());
					} else {
						Log.d(TAG,
								"--------->Collections Net Return Ok <----------"
										+ response.getRequestType()
										+ response.getResponseData());
						UserInfo user = JsonUtil.fromJson(
								response.getResponseData(),
								new TypeToken<UserInfo>() {
								}.getType());

						callback.onUserinfoCallback(user, null);
						DatabaseManager.saveArtileCollectionglToDb(
								config.getArticleId(), true);
					}
				}

				@Override
				public void onErrorResponse() {
					callback.onUserinfoCallback(null, null);
				}
			});
		}
	}

	/**
	 * 获取收藏的文章
	 * 
	 * @param config
	 * @param callback
	 */
	public void getArticleCollection(ArticleConfig config,
			final ArticleInfoCallback callback) {
		if (config.isLocalArticle()) { // 本地操作,未登录的状态
			List<ArticleInfo> articleInfos = new ArrayList<ArticleInfo>();
			if (config.isCustomFlag()) {
				articleInfos = DatabaseManager.getCollectionArticle(true);
			} else {
				articleInfos = DatabaseManager.getCollectionArticle(false);
			}
			Log.d(TAG, "--------->GetCollections Local Return <----------"
					+ "SIZE==>" + articleInfos.size());
			callback.onArticleInfoCallback(articleInfos, RequestType.COLLECTION);
		} else {
			RequestWrapper wrapper = new RequestWrapper(config.getType());
			wrapper.setAccessToken(config.getAccessToken());
			wrapper.setRead_collect_state("get");
			wrapper.setCommenFlag(config.isCustomFlag());
			wrapper.setMaxId(config.getMaxId());
			network.requestData(wrapper, new RequestListener() {

				@Override
				public void onResponse(ResponseWrapper response) {
					Log.d(TAG,
							"--------->GetCollections Net Return Ok <----------"
									+ response.getRequestType()
									+ response.getResponseData());

					if (response.isError()) {
						callback.onArticleInfoCallback(null,
								RequestType.COLLECTION);
					} else {
						List<ArticleInfo> infos = new ArrayList<ArticleInfo>();

						List<ArticleInfo> controls = JsonUtil.fromJson(
								response.getResponseData(),
								new TypeToken<List<ArticleInfo>>() {
								}.getType());

						if (controls != null) {
							for (int i = 0; i < controls.size(); i++) {
								if (controls.get(i).getType() == null
										|| controls.get(i).getType().equals("")) {
									controls.remove(i);
								} else if (controls.get(i).getType()
										.equals(ArticleType.IMAGE)) {
									Gallery gallery = controls.get(i)
											.creatGalleryString();
									controls.get(i).setGallery(gallery);
								}
								controls.get(i).setCollection(true);
							}

							callback.onArticleInfoCallback(controls,
									RequestType.COLLECTION);
							dbManager.savaArticleToDb(controls);
						}
					}
				}

				@Override
				public void onErrorResponse() {
					// TODO Auto-generated method stub
					callback.onArticleInfoCallback(null, RequestType.COLLECTION);

				}
			});

		}
	}

	/**
	 * 获取和关键字相关联的文章
	 * 
	 * @param config
	 * @param callback
	 */
	public void getSearchArticle(final ArticleConfig config,
			final ArticleInfoCallback callback) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setAccessToken(config.getAccessToken());
		wrapper.setPage(config.getPageCount());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				Log.d(TAG,
						"--------->SearchArticle Net Return Ok <----------"
								+ response.getRequestType()
								+ response.getResponseData());
				if (response.isError()) {
					callback.onArticleInfoCallback(null, config.getType());
				} else {
					List<ArticleInfo> infos = new ArrayList<ArticleInfo>();

					List<ArticleInfo> controls = JsonUtil.fromJson(
							response.getResponseData(),
							new TypeToken<List<ArticleInfo>>() {
							}.getType());

					callback.onArticleInfoCallback(controls, config.getType());
				}
			}

			@Override
			public void onErrorResponse() {
				callback.onArticleInfoCallback(null, config.getType());
			}
		});

	}

	/**
	 * 获取我的阅读文章数据
	 * 
	 * @param config
	 * @param callback
	 */
	public void getReadingArticle(final ArticleConfig config,
			final ArticleInfoCallback callback) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setAccessToken(config.getAccessToken());
		if (config.getMaxId() > 0) {
			wrapper.setMaxId(config.getMaxId());
		}
		wrapper.setNewspaper_date(config.getPaperDate());
		wrapper.setCommenFlag(true);

		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {

				Log.d(TAG,
						"--------->ReadingArticles Net Return Ok <----------"
								+ response.getRequestType()
								+ response.getResponseData());
				if (response.isError()) {
					callback.onArticleInfoCallback(null, config.getType());
				} else {
					List<ArticleInfo> infos = new ArrayList<ArticleInfo>();

					List<ArticleInfo> controls = JsonUtil.fromJson(
							response.getResponseData(),
							new TypeToken<List<ArticleInfo>>() {
							}.getType());

					callback.onArticleInfoCallback(controls, config.getType());
				}
			}

			@Override
			public void onErrorResponse() {
				callback.onArticleInfoCallback(null, config.getType());

			}
		});
	}

	/**
	 * 修改用户名称
	 * 
	 * @param config
	 * @param callback
	 */
	public void updateNickName(ArticleConfig config,
			final UserInfoCallback callback) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setAccessToken(config.getAccessToken());
		wrapper.setPassword(config.getPassword());

		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				if (response.isError()) {
					Log.d(TAG,
							"--------->EditUserName Net Return Error <----------"
									+ response.getRequestType()
									+ response.getResponseData());
					callback.onUserinfoCallback(null,
							response.getResponseData());
				} else {
					Log.d(TAG,
							"--------->EditUserName Net Return Ok <----------"
									+ response.getRequestType()
									+ response.getResponseData());

					UserInfo user = JsonUtil.fromJson(
							response.getResponseData(),
							new TypeToken<UserInfo>() {
							}.getType());

					callback.onUserinfoCallback(user, null);
				}
			}

			@Override
			public void onErrorResponse() {
				callback.onUserinfoCallback(null, null);
			}
		});

	}

	/**
	 * 获取评论的接口 数据处理
	 * 
	 * @param config
	 * @param callback
	 */
	public void getComment(ArticleConfig config, final CommentCallback callback) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setArticleId(config.getArticleId());
		wrapper.setCommentType(config.getCommentType());
		wrapper.setMaxId(config.getMaxId());
		wrapper.setAccessToken(config.getAccessToken());
		wrapper.setCommenFlag(config.isCustomFlag());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				Log.d(TAG,
						"--------->CommentArticle Net Return Ok <----------"
								+ response.getRequestType()
								+ response.getResponseData());
				if (response.isError()) {
					callback.onCommentAllCallback(null);
				} else {
					try {
						JSONObject commentJson = new JSONObject(response
								.getResponseData());
						int count = commentJson.getInt("review_count");
						String comment = commentJson.getString("reviews");

						List<commentBean> commentBeans = JsonUtil.fromJson(
								comment, new TypeToken<List<commentBean>>() {
								}.getType());

						if (commentBeans != null) {
							callback.onCommentAllCallback(commentBeans);
						} else {
							callback.onCommentAllCallback(null);
						}

					} catch (JSONException e) {
						callback.onCommentAllCallback(null);
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onErrorResponse() {
				callback.onCommentAllCallback(null);

			}
		});

	}

	/**
	 * 获取开机动画
	 * 
	 * @param config
	 * @param callback
	 */
	public void getStartAdv(ArticleConfig config, final OpenAdvCallback callback) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				Log.d(TAG,
						"--------->StartAdv Net Return Ok <----------"
								+ response.getRequestType()
								+ response.getResponseData());

				if (response.isError()) {

				} else {
					OpenAdvBean openBean = JsonUtil.fromJson(
							response.getResponseData(),
							new TypeToken<OpenAdvBean>() {
							}.getType());
					if (openBean != null) {
						callback.onStartAdvInfoCallback(openBean);
					} else {
						callback.onStartAdvInfoCallback(null);
					}
				}

			}

			@Override
			public void onErrorResponse() {

			}
		});
	}

	/**
	 * 意见与反馈功能
	 * 
	 * @param config
	 * @param callback
	 */
	public void postFeedbacks(ArticleConfig config,
			final StringInfoCallback callback) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setAccessToken(config.getAccessToken());
		wrapper.setCommenString(config.getCustomString());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				Log.d(TAG,
						"--------->Feedbacks Net Return Ok <----------"
								+ response.getRequestType()
								+ response.getResponseData());

				if (response.isError()) { // 有错误的信息
					callback.onStringDataCallback(response.getResponseData(),
							false);
				} else {
					callback.onStringDataCallback(response.getResponseData(),
							true);
				}
			}

			@Override
			public void onErrorResponse() {
				callback.onStringDataCallback(null, false);
			}
		});
	}

	/**
	 * 操作评论
	 * 
	 * @param 里面区别了点赞
	 *            、取消点赞、举报
	 */
	public void handleComment(ArticleConfig config,
			final StringInfoCallback callback) {

		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setArticleId(config.getArticleId());
		wrapper.setHandleType(config.getHandleType());
		wrapper.setCommenString(config.getCustomString());
		wrapper.setCommentId(config.getCommentId());
		wrapper.setAccessToken(config.getAccessToken());
		wrapper.setCommenFlag(config.isCustomFlag());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				Log.d(TAG,
						"--------->HandleComment Net Return Ok <----------"
								+ response.getRequestType()
								+ response.getResponseData());

				if (response.isError()) { // 请求错误，网络有返回具体信息
					callback.onStringDataCallback(response.getResponseData(),
							false);
				} else {
					callback.onStringDataCallback(response.getResponseData(),
							true);
				}
			}

			@Override
			public void onErrorResponse() {
				callback.onStringDataCallback(null, false);
			}
		});
	}

	/**
	 * 创建评论
	 */
	public void newComment(ArticleConfig config, final UserInfoCallback callback) {

		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setArticleId(config.getArticleId());
		wrapper.setHandleType(config.getHandleType());
		wrapper.setCommenString(config.getCustomString());
		wrapper.setCommentId(config.getCommentId());
		wrapper.setAccessToken(config.getAccessToken());
		wrapper.setCommenFlag(config.isCustomFlag());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				Log.d(TAG,
						"--------->CreatComment Net Return Ok <----------"
								+ response.getRequestType()
								+ response.getResponseData());

				if (response.isError()) {
					callback.onUserinfoCallback(null,
							response.getResponseData());
				} else {
					if (response.getResponseData() != null) {
						UserInfo user = JsonUtil.fromJson(
								response.getResponseData(),
								new TypeToken<UserInfo>() {
								}.getType());

						callback.onUserinfoCallback(user, null);
					} else { // 未登录的状态下的返回
						UserInfo info = new UserInfo();
						info.setAccess_token(null);
						callback.onUserinfoCallback(info, null);
					}
				}
			}

			@Override
			public void onErrorResponse() {
				callback.onUserinfoCallback(null, null);
			}
		});
	}

	/**
	 * 获取个人评论
	 * 
	 * @param config
	 * @param callback
	 */
	public void getSelfComment(ArticleConfig config,
			final SelfCommentCallback callback) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setPage(config.getPageCount());
		wrapper.setAccessToken(config.getAccessToken());
		wrapper.setCommenFlag(config.isCustomFlag());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				Log.d(TAG,
						"--------->SelfComment Net Return Ok <----------"
								+ response.getRequestType()
								+ response.getResponseData());

				if (response.isError()) {
					callback.onSelfCommentCallback(null,
							response.getResponseData());

				} else {
					List<SingleComment> comments = JsonUtil.fromJson(
							response.getResponseData(),
							new TypeToken<List<SingleComment>>() {
							}.getType());

					callback.onSelfCommentCallback(comments, null);
				}
			}

			@Override
			public void onErrorResponse() {
				callback.onSelfCommentCallback(null, null);

			}
		});
	}

	/**
	 * 获取报名中心的数据
	 * 
	 * @param config
	 */
	public void getSignCenterData(ArticleConfig config,
			final ActivityCallback callback) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setAccessToken(config.getAccessToken());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				Log.d(TAG,
						"--------->SignCenter Net Return Ok <----------"
								+ response.getRequestType()
								+ response.getResponseData());

				if (response.isError()) {
					callback.onActivityCallback(null,
							response.getResponseData());
				} else {
					List<ActivityListBean> activities = JsonUtil.fromJson(
							response.getResponseData(),
							new TypeToken<List<ActivityListBean>>() {
							}.getType());

					if (activities != null) {
						callback.onActivityCallback(activities, null);
					} else {
						callback.onActivityCallback(null, null);
					}
				}
			}

			@Override
			public void onErrorResponse() {
				callback.onActivityCallback(null, null);
			}
		});
	}

	/**
	 * 获取本地收藏文章的IDS
	 * 
	 * @return
	 */
	public String getLocalCollections() {
		String idsString = "";
		List<ArticleInfo> collections = DatabaseManager.getCollectionsAll();
		if (collections != null) {
			for (int i = 0; i < collections.size(); i++) {
				Log.d("collection", "" + collections.get(i).getArticle_id());
				if (i == 0) {
					idsString = idsString + collections.get(i).getArticle_id();
				} else {
					idsString = idsString + ","
							+ collections.get(i).getArticle_id();
				}
			}
		}

		return idsString;
	}

	/**
	 * 清理相关消息 Flag true为相关评论，false为系统通知
	 * 
	 * @param config
	 */
	public void clearMessageNotice(ArticleConfig config,
			final UserInfoCallback callback) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setAccessToken(config.getAccessToken());
		wrapper.setCommenFlag(config.isCustomFlag());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {

				if (response.isError()) {
					Log.d(TAG,
							"--------->ClearMsg Net Return Error <----------"
									+ response.getResponseData());
					callback.onUserinfoCallback(null,
							response.getResponseData());
				} else {
					Log.d(TAG,
							"--------->ClearMsg Net Return Ok <----------"
									+ response.getRequestType()
									+ response.getResponseData());
					UserInfo user = JsonUtil.fromJson(
							response.getResponseData(),
							new TypeToken<UserInfo>() {
							}.getType());

					callback.onUserinfoCallback(user, null);

				}
			}

			@Override
			public void onErrorResponse() {
				callback.onUserinfoCallback(null, null);

			}
		});

	}

	/**
	 * 获取点击时是否能发送点击事件
	 * 
	 * @param time
	 * @param articleId
	 * @return
	 */
	public boolean storeClickTime(long time, long articleId,boolean isScroll) {

		return DatabaseManager.storeClickTime(articleId, time,isScroll);
	}

	public void addClickCount(ArticleConfig config) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		wrapper.setAccessToken(config.getAccessToken());
		wrapper.setArticleId(config.getArticleId());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {

			}

			@Override
			public void onErrorResponse() {

			}
		});
	}

	/**
	 * 将服务器获取的收藏文章状态同步到本地
	 * 
	 * @param ids
	 */
	public void setNetCollectionsToLacal(List<Long> ids) {
		if (ids != null) {
			for (int i = 0; i < ids.size(); i++) {
				DatabaseManager.saveArtileCollectionglToDb(ids.get(i), true);
			}
		}
	}

	public void getGalleries(ArticleConfig config,
			final GalleryInfoCallback callback) {
		if (config.isLocalArticle()) { // 取本地的数据库
			int galleryId = config.getPageCount();
			Gallery gallery = dbManager.getGalleryFromDb(galleryId);

			callback.onGalleryCallback(gallery);

		} else {
			RequestWrapper wrapper = new RequestWrapper(config.getType());
			wrapper.setPage(config.getPageCount());
			network.requestData(wrapper, new RequestListener() {

				@Override
				public void onResponse(ResponseWrapper response) {
					Log.d(TAG,
							"--------->Gallery Net Return Ok <----------"
									+ response.getRequestType()
									+ response.getResponseData());
					if (response.isError()) {
						callback.onGalleryCallback(null);
					} else {
						Gallery gallery = JsonUtil.fromJson(
								response.getResponseData(),
								new TypeToken<Gallery>() {
								}.getType());
						if (gallery != null) {
							callback.onGalleryCallback(gallery);
							dbManager.saveGalleries(gallery);
						} else {
							callback.onGalleryCallback(null);
						}
					}
				}

				@Override
				public void onErrorResponse() {
					callback.onGalleryCallback(null);

				}
			});
		}
	}

	/**
	 * 清除最近的删除额文章
	 */
	public void clearDatabaseByArticleDelete(ArticleConfig config) {
		RequestWrapper wrapper = new RequestWrapper(config.getType());
		network.requestData(wrapper, new RequestListener() {

			@Override
			public void onResponse(ResponseWrapper response) {
				Log.d(TAG,
						"--------->clearDelete Net Return Ok <----------"
								+ response.getRequestType()
								+ response.getResponseData());
				if (!response.isError()) {
					try {
						JSONArray idsArray = new JSONArray(response
								.getResponseData());
						List<Long> ids = new ArrayList<Long>();
						for (int i = 0; i < idsArray.length(); i++) {
							ids.add(idsArray.getLong(i));
						}
						dbManager.deleteNetDelete(ids);

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onErrorResponse() {
			}
		});

	}

	public void uploadCrashInfo(CrashBean crashBean) {
		RequestWrapper request = new RequestWrapper(RequestType.UPLADO_CRASH);
		request.setmCrashBean(crashBean);
		network.requestData(request, null);
	}
}
