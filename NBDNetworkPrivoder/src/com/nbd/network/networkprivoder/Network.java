package com.nbd.network.networkprivoder;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ApplicationErrorReport.CrashInfo;
import android.content.Context;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nbd.network.bean.CrashBean;
import com.nbd.network.bean.RequestType;
import com.nbd.network.bean.RequestWrapper;
import com.nbd.network.bean.ResponseWrapper;
import com.nbd.network.utility.Cst;
import com.nbd.network.utility.NetUtil;

/**
 * 网络数据请求
 * 
 * @author riche
 * 
 */
public class Network implements NetworkBase {

	private Handler networkHandler;

	private final String RESPONSE_WRAPER = "resopnse_wraper";
	private static final String TAG = "Network>>";

	private RequestQueue mRequestQueue;
	private Context mContext;
	private String mVersionCode;

	String url = ""; // 请求的地址
	private Map<String, String> postMap = new HashMap<String, String>(); // POST请求的参数

	public Network(Context context, String versionCode) {
		this.mContext = context;
		this.mRequestQueue = Volley.newRequestQueue(mContext);

		this.mVersionCode = versionCode;
		Log.e(TAG, "version-->" + mVersionCode);
		initHandler();
	}

	/**
	 * 网络数据请求 返回Article的数据走GET/POST方法 其他数据新开方法
	 */
	@Override
	public void requestData(RequestWrapper request,
			final RequestListener listener) {
		/** 不同类型的网络请求参数的封装 */
		switch (request.getRequestType()) {
		case LOGIN:
			login(request, listener);
			break;
		case LOGIN_WEIBO:
			login(request, listener);
			break;
		case LOGIN_WEIXIN:
			login(request, listener);
			break;
		case LOGIN_QQ:
			login(request, listener);
			break;
		case KX:
			url = Cst.KX + Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case KXXQ:
			url = Cst.KXXQ + request.getZxxqId() + "/article_content";
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case ZX:
			url = Cst.ZX + Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case ZXLB:
			url = Cst.ZXLB + Cst.APP_KEY;
			url = url + "&count=3";
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case ZXZS:
			url = Cst.ZXZS + Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case GSLB:
			url = Cst.GSLB + Cst.APP_KEY;
			url = url + "&count=3";
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case GSZJZ:
			url = Cst.GSZJZ + Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case GSFJZ:
			url = Cst.GSFJZ + Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case LCLB:
			url = Cst.LCLB + Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case LCYW:
			url = Cst.LCYW + Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case IPO:
			url = Cst.IPO + Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case SEARCH:
			url = Cst.SEARCH + Cst.APP_KEY + "&keyword="
					+ request.getAccessToken() + "&page=" + request.getPage();
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case TEST_ACCOUNT:
			url = "http://api.nbd.com.cn/3/columns/" + request.getCount()
					+ "/articles" + Cst.APP_KEY + "&page=" + request.getPage()
					+ "&access_token" + request.getAccessToken();
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case ARTICLE_DETAIL:
			url = Cst.ARTICLE_DETAIL + request.getArticleId()
					+ "/article_content"
					+ "?column_id=435&app_key=ae1bd0a8b32505a86c0b20187f5093ec";
			if (request.getCommenString() != null
					&& !request.getCommenString().equals("")) {
				url = url + "&updated_at=" + request.getCommenString();
			}
			request.setUrl(url);
			doGetDetailRequest(request, listener);
			break;
		case NEWSPAPER_MONTH:
			url = Cst.NEWSPAPER + request.getNewspaper_month()
					+ Cst.NEWSPAPER_MONTH + Cst.APP_KEY + "&count=1"
					+ "&max_id=" + request.getNewspaper_date();
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case NEWSPAPER_DAILY:
			url = Cst.NEWSPAPER + request.getNewspaper_date()
					+ Cst.NEWSPAPER_DAILY + Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case FEATURE:
			url = Cst.FEATURE + ".json" + Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case FEATURE_DETAIL:
			url = Cst.FEATURE + "/" + request.getArticleId() + ".json"
					+ Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case VIDEO:
			url = Cst.VIDEO + Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case REGISTER_PHONE_GET_CODE:
			if (request.isCommenFlag()) { // 注册的时候获取验证码
				url = Cst.REGISTER_PHONE_GET_CODE + Cst.APP_KEY + "&phone_no="
						+ request.getPhoneNum();
			} else {
				url = Cst.REGISTER_PHONE_GET_CODE + Cst.APP_KEY + "&phone_no="
						+ request.getPhoneNum() + "&opt=binding_phone";
			}
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case REGISTER_BY_CODE:
			url = Cst.REGISTER_BY_CODE;
			request.setUrl(url);
			Map<String, String> registerMap = new HashMap<String, String>();
			registerMap.put("app_key", "ae1bd0a8b32505a86c0b20187f5093ec");
			registerMap.put("phone_no", request.getPhoneNum());
			registerMap.put("verify_code", request.getRegisterCode());
			registerMap.put("password", request.getRegisterPassword());
			registerMap.put("app_version_name", mVersionCode);
			request.setPostRequest(registerMap);
			doPostRequest(request, listener);
			break;
		case MY_MESSAGE:
			url = Cst.MY_MESSAGE + Cst.APP_KEY + "&access_token="
					+ request.getAccessToken() + "&count=15&page="
					+ request.getPage();
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case SYS_MESSAGE:
			url = Cst.SYS_MESSAGE + Cst.APP_KEY + "&access_token="
					+ request.getAccessToken() + "&count=15&page="
					+ request.getPage();
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case HOT_TAGS:
			url = Cst.HOT_TAGS + Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case READING:
			url = Cst.READ;
			request.setUrl(url);
			Map<String, String> readMap = new HashMap<String, String>();
			readMap.put("app_key", "ae1bd0a8b32505a86c0b20187f5093ec");
			readMap.put("access_token", request.getAccessToken());
			readMap.put("app_version_name", mVersionCode);
			if (request.getMaxId() > 0) {
				readMap.put("max_id", "" + request.getMaxId());
			}
			if (request.isCommenFlag()) {
				readMap.put("oper", "get");
				readMap.put("content_date", request.getNewspaper_date());
				request.setArrayReturn(true);

			} else {
				readMap.put("ids", "" + request.getArticleId());
				readMap.put("oper", "add");
			}
			request.setPostRequest(readMap);
			doPostRequest(request, listener);
			break;
		case COLLECTION:
			url = Cst.COLLECTION;
			request.setUrl(url);
			Map<String, String> collectMap = new HashMap<String, String>();
			collectMap.put("app_key", "ae1bd0a8b32505a86c0b20187f5093ec");
			collectMap.put("access_token", request.getAccessToken());
			collectMap.put("oper", request.getRead_collect_state());
			collectMap.put("app_version_name", mVersionCode);
			if (request.getMaxId() > 0) {
				collectMap.put("max_id", "" + request.getMaxId());
			}
			if (request.getRead_collect_state().equals("get")) {
				if (request.isCommenFlag()) { // 标志位标准取图片收藏
					collectMap.put("content_type", "picture");
				} else { // 普通文章的收藏
					collectMap.put("content_type", "article");
				}
				collectMap.put("ids", "");
				request.setArrayReturn(true);
			} else {
				if (request.getCommenString() != null
						&& !request.getCommenString().equals("")) {
					collectMap.put("ids", "" + request.getCommenString());
				} else {
					collectMap.put("ids", "" + request.getArticleId());
				}
			}
			request.setPostRequest(collectMap);
			doPostRequest(request, listener);
			break;
		case RESET_PASSWORD:
			url = Cst.RESET_PASSWORD;
			request.setUrl(url);
			Map<String, String> resetMap = new HashMap<String, String>();
			resetMap.put("app_key", "ae1bd0a8b32505a86c0b20187f5093ec");
			resetMap.put("app_version_name", mVersionCode);

			if (request.getPhoneNum() == null
					|| request.getPhoneNum().equals("")) { // 当电话号码是空的时候是修改密码
				resetMap.put("access_token", request.getAccessToken());
				resetMap.put("old_password", request.getPassword());
				resetMap.put("new_password", request.getNewPassword());
			} else { // 电话号码不为空，为忘记密码的情况
				Log.e("FORGET-PASSWORD==>",
						request.getPhoneNum() + "==" + request.getPassword()
								+ "==" + request.getNewPassword());
				resetMap.put("phone_no", request.getPhoneNum());
				resetMap.put("new_password", request.getPassword());
				resetMap.put("verify_code", request.getNewPassword());
			}
			request.setPostRequest(resetMap);
			doPostRequest(request, listener);
			break;
		case UPDATE_NAME:
			url = Cst.UPDATE_NAME;
			request.setUrl(url);
			Map<String, String> nameMap = new HashMap<String, String>();
			nameMap.put("app_key", "ae1bd0a8b32505a86c0b20187f5093ec");
			nameMap.put("access_token", request.getAccessToken());
			nameMap.put("user_name", request.getPassword());
			nameMap.put("app_version_name", mVersionCode);
			request.setPostRequest(nameMap);
			doPostRequest(request, listener);
			break;
		case GET_COMMENT:
			url = Cst.GET_COMMENT;
			if (request.isCommenFlag()) {
				url = url + "galleries/";
			} else {
				url = url + "articles/";
			}
			url = url + request.getArticleId() + "/reviews" + Cst.APP_KEY
					+ "&access_token=" + request.getAccessToken();
			if (request.getCommentType() == 0) {
				url = url + "&filter_type=all";
			} else if (request.getCommentType() == 1) {
				url = url + "&filter_type=new";
			}
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case OPEN_ADV:
			url = Cst.OPEN_ADV + Cst.APP_KEY;
			request.setUrl(url);
			doGetAdvRequest(request, listener);
			break;
		case FEEDBACKS:
			url = Cst.FEEDBACKS;
			request.setUrl(url);
			Map<String, String> backMap = new HashMap<String, String>();
			backMap.put("app_key", "ae1bd0a8b32505a86c0b20187f5093ec");
			backMap.put("access_token", request.getAccessToken());
			backMap.put("body", request.getCommenString());
			backMap.put("app_version_name", mVersionCode);
			request.setPostRequest(backMap);
			doPostRequest(request, listener);
			break;
		case COMMENT:
			handleComment(request, listener);
			break;
		case SELF_COMMENT:
			url = Cst.SELF_COMMENT + Cst.APP_KEY + "&page=" + request.getPage()
					+ "&access_token=" + request.getAccessToken();
			request.setUrl(url);
			doGetJsonRequest(request, listener);
			break;
		case SIGN_CENTER:
			if (request.getAccessToken() != null) {
				url = Cst.SIGN_CENTER + Cst.APP_KEY + "&access_token="
						+ request.getAccessToken();
			} else {
				url = Cst.SIGN_CENTER + Cst.APP_KEY;
			}
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case CLEAR_NOTICE:
			if (request.isCommenFlag()) {
				url = Cst.CLEAR_MESSAGE + "related_notification_read";
			} else {
				url = Cst.CLEAR_MESSAGE + "system_notification_read";
			}
			request.setUrl(url);
			Map<String, String> clearMap = new HashMap<String, String>();
			clearMap.put("app_key", "ae1bd0a8b32505a86c0b20187f5093ec");
			clearMap.put("access_token", request.getAccessToken());
			clearMap.put("opt", "empty");
			clearMap.put("app_version_name", mVersionCode);
			request.setPostRequest(clearMap);
			doPostRequest(request, listener);
			break;
		case ADD_CLICK_COUNT:
			url = Cst.NEWS_CLICK;
			request.setUrl(url);
			Map<String, String> clickMap = new HashMap<String, String>();
			clickMap.put("app_key", "ae1bd0a8b32505a86c0b20187f5093ec");
			// clickMap.put("access_token", request.getAccessToken());
			clickMap.put("ids", request.getArticleId() + "");
			clickMap.put("app_version_name", mVersionCode);
			request.setPostRequest(clickMap);
			doPostRequest(request, listener);
			break;
		case GALLERY:
			url = Cst.GALLERY + request.getPage() + Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case CLEAR_DELETE:
			url = Cst.CLEAR_DELETE + Cst.APP_KEY;
			request.setUrl(url);
			doGetRequest(request, listener);
			break;
		case UPLADO_CRASH:
			uploadCrashInfo(request);
			break;
		default:
			break;
		}
	}

	/**
	 * 处理评论相关的信息
	 * 
	 * @param request
	 * @param listener
	 */
	private void handleComment(RequestWrapper request, RequestListener listener) {
		String url = Cst.COMMENT;
		if (request.isCommenFlag()) { // 图集的相关评论
			url = url + "galleries/";
		} else {
			url = url + "articles/";
		}
		url = url + request.getArticleId() + "/reviews";
		Map<String, String> commentMap = new HashMap<String, String>();
		commentMap.put("app_key", "ae1bd0a8b32505a86c0b20187f5093ec");
		commentMap.put("app_version_name", mVersionCode);
		if (request.getAccessToken() != null
				&& !request.getAccessToken().equals("")) {
			commentMap.put("access_token", request.getAccessToken());
			request.setCommenFlag(true);
		} else {
			request.setCommenFlag(false);
		}
		switch (request.getHandleType()) {
		case REPLY:
			commentMap.put("body", request.getCommenString());
			commentMap.put("parent_id", "");
			break;
		case CHILD_REPLY:
			commentMap.put("body", request.getCommenString());
			commentMap.put("parent_id", request.getCommentId() + "");
			break;
		case SUPPORT:
			url = url + "/" + request.getCommentId() + "/support";
			break;
		case UN_SUPPORT:
			url = url + "/" + request.getCommentId() + "/un_support";
			break;
		case REPORT:
			url = url + "/" + request.getCommentId() + "/report";
			break;
		default:
			break;
		}
		request.setPostRequest(commentMap);
		request.setUrl(url);
		doCommentPostRequest(request, listener);

	}

	private void uploadCrashInfo(RequestWrapper request) {
		CrashBean crashBean = request.getmCrashBean();
		url = "http://api.nbd.com.cn/3/error_logs/upload_error_report";
		request.setUrl(url);
		Map<String, String> crashMap = new HashMap<String, String>();
		if (crashBean != null) {
			crashMap.put("app_key", "ae1bd0a8b32505a86c0b20187f5093ec");
			crashMap.put("app_name", "nbd-android");
			crashMap.put("version_name", crashBean.getVersionName());
			crashMap.put("version_code", crashBean.getVersionCode());
			crashMap.put("system_version", crashBean.getSystemVersion());
			crashMap.put("device_model", crashBean.getDeviceModel());
			crashMap.put("exception_time", crashBean.getTime());
			crashMap.put("exception_content", crashBean.getException());
			request.setPostRequest(crashMap);
			doPostRequest(request, null);
		}
	}

	/**
	 * GET方式网络请求
	 * 
	 * @param request
	 * @param listener
	 */
	private void doGetRequest(final RequestWrapper request,
			final RequestListener listener) {
		String requestUrl = request.getUrl();
		if (request.getMaxId() > 0) {
			requestUrl = requestUrl + "&max_id=" + request.getMaxId();

		}
		requestUrl = requestUrl + "&app_version_name=" + mVersionCode;
		System.out.println(requestUrl);
		StringRequest stringRequest = new StringRequest(requestUrl,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, "response==>" + response);
						try {
							JSONObject result = new JSONObject(response);
							if (request.getRequestType() == RequestType.REGISTER_PHONE_GET_CODE
									|| request.getRequestType() == RequestType.FEATURE_DETAIL
									|| request.getRequestType() == RequestType.RESET_PASSWORD
									|| request.getRequestType() == RequestType.GET_COMMENT
									|| request.getRequestType() == RequestType.OPEN_ADV
									|| request.getRequestType() == RequestType.GALLERY) {
								JSONObject resultData = new JSONObject();
								resultData = result.getJSONObject("data");
								if (result.getInt("status_code") == 0) { // 请求有错误，附带错误信息显示
									ResponseWrapper wrapper = new ResponseWrapper(
											request.getRequestType(), true);
									wrapper.setResponseData(result
											.getString("msg"));
									listener.onResponse(wrapper);
								} else { // 请求成功
									ResponseWrapper wrapper = new ResponseWrapper(
											request.getRequestType(), false);
									if (resultData != null) {
										wrapper.setResponseData(resultData
												.toString());
										listener.onResponse(wrapper);
									} else {
										listener.onErrorResponse();
									}
								}
							} else {
								if (result.getInt("status_code") == 0) { // 请求有错误，附带错误信息显示
									ResponseWrapper wrapper = new ResponseWrapper(
											request.getRequestType(), true);
									wrapper.setResponseData(result
											.getString("msg"));
									listener.onResponse(wrapper);
								} else {
									JSONArray articleData = new JSONArray();
									ResponseWrapper wrapper = new ResponseWrapper(
											request.getRequestType(), false);
									articleData = result.getJSONArray("data");
									wrapper.setResponseData(articleData
											.toString());
									listener.onResponse(wrapper);
								}
							}
						} catch (JSONException e) {
							listener.onErrorResponse();
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, error.getMessage(), error);
						listener.onErrorResponse();
					}
				});

		stringRequest.setTag(request.getRequestType());
		mRequestQueue.cancelAll(request.getRequestType());// 取消同类请求，防止重复返回
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mRequestQueue.add(stringRequest);

	}

	private void doGetAdvRequest(final RequestWrapper request,
			final RequestListener listener) {
		String requestUrl = request.getUrl();
		if (request.getMaxId() > 0) {
			requestUrl = requestUrl + "&max_id=" + request.getMaxId();

		}
		requestUrl = requestUrl + "&app_version_name=" + mVersionCode;
		System.out.println(requestUrl);
		StringRequest stringRequest = new StringRequest(requestUrl,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, "response==>" + response);
						try {
							JSONObject result = new JSONObject(response);
							JSONObject resultData = new JSONObject();
							resultData = result.getJSONObject("data");
							if (result.getInt("status_code") == 0) { // 请求有错误，附带错误信息显示
								ResponseWrapper wrapper = new ResponseWrapper(
										request.getRequestType(), true);
								wrapper.setResponseData(result.getString("msg"));
								listener.onResponse(wrapper);
							} else { // 请求成功
								ResponseWrapper wrapper = new ResponseWrapper(
										request.getRequestType(), false);
								if (resultData != null) {
									wrapper.setResponseData(resultData
											.toString());
									listener.onResponse(wrapper);
								} else {
									listener.onErrorResponse();
								}
							}
						} catch (JSONException e) {
							listener.onErrorResponse();
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, error.getMessage(), error);
						listener.onErrorResponse();
					}
				});

		stringRequest.setTag(request.getRequestType());
		mRequestQueue.cancelAll(request.getRequestType());// 取消同类请求，防止重复返回
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mRequestQueue.add(stringRequest);

	}

	/**
	 * GET方式网络请求非JSON数据
	 * 
	 * @param request
	 * @param listener
	 */
	private void doGetDetailRequest(final RequestWrapper request,
			final RequestListener listener) {
		String requestUrl = request.getUrl();
		requestUrl = requestUrl + "&app_version_name=" + mVersionCode;
		System.out.println(request.getUrl());
		StringRequest stringRequest = new StringRequest(requestUrl,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d(TAG, "response==>" + response);
						try {
							JSONObject result = new JSONObject(response);

							if (result.getInt("status_code") == 0) { // 请求有错误，附带错误信息显示
								ResponseWrapper wrapper = new ResponseWrapper(
										request.getRequestType(), true);
								wrapper.setResponseData(result.getString("msg"));
								listener.onResponse(wrapper);
							} else { // 请求成功

								JSONArray articleData = new JSONArray();
								ResponseWrapper wrapper = new ResponseWrapper(
										request.getRequestType(), false);
								articleData = result.getJSONArray("data");

								// JSONObject contentObj = articleData
								// .getJSONObject(0);
								// if (articleData.length() != 0) {
								// Log.d(TAG, "data==>" + articleData.length()
								// + articleData.toString());
								wrapper.setResponseData(articleData.toString());
								listener.onResponse(wrapper);
								// } else {
								// listener.onErrorResponse();
								// }

							}
						} catch (JSONException e) {
							Log.e(TAG, "detail catch");
							// TODO Auto-generated catch block
							listener.onErrorResponse();
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, error.getMessage(), error);
						listener.onErrorResponse();
					}
				});

		stringRequest.setTag(request.getRequestType());
		mRequestQueue.cancelAll(request.getRequestType());// 取消同类请求，防止重复返回
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mRequestQueue.add(stringRequest);

	}

	/**
	 * POST方式网络请求
	 * 
	 * @param request
	 *            请求参数，里面带有POST请求中的键值对Map数组
	 * @param listener
	 */
	private void doPostRequest(final RequestWrapper request,
			final RequestListener listener) {
		final Map<String, String> params = request.getPostRequest();
		System.out.println("post==>" + request.getUrl());
		System.out.println("post parameter==>" + params);
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				request.getUrl(), new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d("NET RESULT==>", response);
						if (request.getRequestType() != RequestType.ADD_CLICK_COUNT
								&& request.getRequestType() != RequestType.UPLADO_CRASH) {
							try {
								JSONObject result = new JSONObject(response);
								if (result.getInt("status_code") == 0) { // 请求有错误，附带错误信息显示
									ResponseWrapper wrapper = new ResponseWrapper(
											request.getRequestType(), true);
									wrapper.setResponseData(result
											.getString("msg"));
									listener.onResponse(wrapper);
								} else { // 请求成功
									if (request.isArrayReturn()) {
										JSONArray arrayData = result
												.getJSONArray("data");
										ResponseWrapper wrapper = new ResponseWrapper(
												request.getRequestType(), false);
										wrapper.setResponseData(arrayData
												.toString());
										listener.onResponse(wrapper);
									} else {
										JSONObject contentData = result
												.getJSONObject("data");
										ResponseWrapper wrapper = new ResponseWrapper(
												request.getRequestType(), false);
										wrapper.setResponseData(contentData
												.toString());
										listener.onResponse(wrapper);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("hff", error.getMessage(), error);
						listener.onErrorResponse();
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return params;
			}

		};

		stringRequest.setTag(request.getRequestType());
		mRequestQueue.cancelAll(request.getRequestType());// 取消同类请求，防止重复返回
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mRequestQueue.add(stringRequest);

	}

	/**
	 * 处理评论相关的返回
	 * 
	 * @param request
	 * @param listener
	 */
	private void doCommentPostRequest(final RequestWrapper request,
			final RequestListener listener) {
		final Map<String, String> params = request.getPostRequest();
		System.out.println("post==>" + request.getUrl());
		System.out.println("post parameter==>" + params);
		StringRequest stringRequest = new StringRequest(Request.Method.POST,
				request.getUrl(), new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						Log.d("NET RESULT==>", response);
						if (request.isCommenFlag()) {
							try {
								JSONObject result = new JSONObject(response);
								if (result.getInt("status_code") == 0) { // 请求有错误，附带错误信息显示
									ResponseWrapper wrapper = new ResponseWrapper(
											request.getRequestType(), true);
									wrapper.setResponseData(result
											.getString("msg"));
									listener.onResponse(wrapper);
								} else { // 请求成功
									if (request.isArrayReturn()) {
										JSONArray arrayData = result
												.getJSONArray("data");
										ResponseWrapper wrapper = new ResponseWrapper(
												request.getRequestType(), false);
										wrapper.setResponseData(arrayData
												.toString());
										listener.onResponse(wrapper);
									} else {
										JSONObject contentData = result
												.getJSONObject("data");
										ResponseWrapper wrapper = new ResponseWrapper(
												request.getRequestType(), false);
										wrapper.setResponseData(contentData
												.toString());
										listener.onResponse(wrapper);
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else { // 未登录状态下的评论返回
							try {
								JSONObject result = new JSONObject(response);
								if (result.getInt("status_code") == 0) {
									ResponseWrapper wrapper = new ResponseWrapper(
											request.getRequestType(), true);
									wrapper.setResponseData(result
											.getString("msg"));
									listener.onResponse(wrapper);
								} else {
									ResponseWrapper wrapper = new ResponseWrapper(
											request.getRequestType(), false);
									wrapper.setResponseData(null);
									listener.onResponse(wrapper);

								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("hff", error.getMessage(), error);
						listener.onErrorResponse();
					}
				}) {
			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				return params;
			}

		};

		stringRequest.setTag(request.getRequestType());
		mRequestQueue.cancelAll(request.getRequestType());// 取消同类请求，防止重复返回
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mRequestQueue.add(stringRequest);

	}

	private void doGetJsonRequest(final RequestWrapper request,
			final RequestListener listener) {
		final RequestType requestType = request.getRequestType();

		String requestUrl = request.getUrl();
		if (request.getMaxId() > 0) {
			requestUrl = requestUrl + "&max_id=" + request.getMaxId();
		}
		requestUrl = requestUrl + "&app_version_name=" + mVersionCode;
		System.out.println(requestUrl);

		JsonObjectRequest jsonRequest = new JsonObjectRequest(request.getUrl(),
				null, new Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.d(TAG, "response==>" + response.toString());

						try {
							if (response.getInt("status_code") == 0) { // 请求有错误，附带错误信息显示
								ResponseWrapper wrapper = new ResponseWrapper(
										request.getRequestType(), true);
								wrapper.setResponseData(response
										.getString("msg"));
								listener.onResponse(wrapper);
							} else {
								JSONArray articleData = new JSONArray();
								ResponseWrapper wrapper = new ResponseWrapper(
										request.getRequestType(), false);
								articleData = response.getJSONArray("data");
								wrapper.setResponseData(articleData.toString());
								listener.onResponse(wrapper);
							}
						} catch (JSONException e) {
							listener.onErrorResponse();
							e.printStackTrace();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e(TAG, error.getMessage(), error);
						listener.onErrorResponse();
					}
				});
		// mRequestQueue.add(jsonRequest);
		jsonRequest.setTag(request.getRequestType());
		mRequestQueue.cancelAll(request.getRequestType());// 取消同类请求，防止重复返回
		jsonRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mRequestQueue.add(jsonRequest);
	};

	/**
	 * 获取资讯的数据 含解读子数据
	 * 
	 * @param request
	 * @param listener
	 */
	private void getZxInfo(final RequestWrapper request,
			final RequestListener listener) {
		StringRequest stringRequest = new StringRequest(request.getUrl(),
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {
						try {
							JSONObject result = new JSONObject(response);
							JSONArray articleData = result.getJSONArray("data");
							ResponseWrapper wrapper = new ResponseWrapper(
									request.getRequestType(), false);
							wrapper.setResponseData(articleData.toString());
							listener.onResponse(wrapper);

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("hff", error.getMessage(), error);
					}
				});

		stringRequest.setTag(request.getRequestType());
		mRequestQueue.cancelAll(request.getRequestType());// 取消同类请求，防止重复返回
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		mRequestQueue.add(stringRequest);

	}

	/**
	 * 登陆 普通登录及第三方登录
	 * 
	 * @param request
	 * @param listener
	 */
	private void login(final RequestWrapper request,
			final RequestListener listener) {

		Map<String, String> loginMap = new HashMap<String, String>();
		loginMap.put("app_key", "ae1bd0a8b32505a86c0b20187f5093ec");
		loginMap.put("app_version_name", mVersionCode);

		switch (request.getRequestType()) {
		case LOGIN:
			url = Cst.LOGIN;
			request.setUrl(url);
			loginMap.put("nickname", request.getUserId());
			loginMap.put("password", request.getPassword());
			break;
		case LOGIN_WEIBO:
			url = Cst.LOGIN_WEIBO;
			request.setUrl(url);
			if (request.isCommenFlag()) {
				loginMap.put("access_token", request.getAccessToken());
			}
			loginMap.put("third_access_token", request.getLogin_token());
			loginMap.put("openid", request.getLogin_openid());
			break;
		case LOGIN_WEIXIN:
			url = Cst.LOGIN_WEIXIN;
			request.setUrl(url);
			if (request.isCommenFlag()) {
				loginMap.put("access_token", request.getAccessToken());
			}
			loginMap.put("third_access_token", request.getLogin_token());
			loginMap.put("openid", request.getLogin_openid());
			break;
		case LOGIN_QQ:
			url = Cst.LOGIN_QQ;
			request.setUrl(url);
			if (request.isCommenFlag()) {
				loginMap.put("access_token", request.getAccessToken());
			}
			loginMap.put("third_access_token", request.getLogin_token());
			loginMap.put("openid", request.getLogin_openid());
			break;
		default:
			break;
		}
		request.setPostRequest(loginMap);
		doPostRequest(request, listener);
	}

	@Override
	public void registerRequestListener(RequestListener listener,
			RequestType type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void unRegisterRequestListener(RequestListener listener,
			RequestType type) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param response
	 * @param listener
	 */
	private void sendMessage(final ResponseWrapper response,
			final RequestListener listener) {
		Message msg = networkHandler.obtainMessage();
		msg.obj = listener;
		Bundle data = new Bundle();
		data.putSerializable(RESPONSE_WRAPER, response);
		msg.setData(data);
		msg.sendToTarget();
	}

	/**
	 * 初始化网络消息处理器Handler
	 */
	private void initHandler() {
		networkHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				RequestListener listener = (RequestListener) msg.obj;

				if (null != msg.getData()
						&& null != msg.getData().getSerializable(
								RESPONSE_WRAPER)) {
					ResponseWrapper response = (ResponseWrapper) msg.getData()
							.getSerializable(RESPONSE_WRAPER);
					if (null != listener) {
						listener.onResponse(response);
					}
				}
			}
		};
	}

	Runnable uploadImageRunnable = new Runnable() {
		@Override
		public void run() {

			Map<String, String> textParams = new HashMap<String, String>();
			Map<String, File> fileparams = new HashMap<String, File>();

			try {
				// 创建一个URL对象
				URL url = new URL(Cst.APP_KEY);
				textParams = new HashMap<String, String>();
				fileparams = new HashMap<String, File>();
				// 要上传的图片对象
				// Bitmap map = BitmapFactory.decodeByteArray("", 0, 0);
				// 利用HttpURLConnection对象从网络中获取网页数据
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				// 设置连接超时（记得设置连接超时,如果网络不好,Android系统在超过默认时间会收回资源中断操作）
				conn.setConnectTimeout(60000);
				// 设置允许输出（发送POST请求必须设置允许输出）
				conn.setDoOutput(true);
				// 设置使用POST的方式发送
				conn.setRequestMethod("POST");
				// 设置不使用缓存（容易出现问题）
				conn.setUseCaches(false);
				// 在开始用HttpURLConnection对象的setRequestProperty()设置,就是生成HTML文件头
				conn.setRequestProperty("ser-Agent", "Fiddler");
				// 设置contentType
				conn.setRequestProperty("Content-Type",
						"multipart/form-data; boundary=" + NetUtil.BOUNDARY);
				OutputStream os = conn.getOutputStream();
				DataOutputStream ds = new DataOutputStream(os);
				NetUtil.writeStringParams(textParams, ds);
				// NetUtil.writeFileParams(fileparams, ds);
				NetUtil.paramsEnd(ds);
				// 对文件流操作完,要记得及时关闭
				os.close();
				// 服务器返回的响应吗
				int code = conn.getResponseCode(); // 从Internet获取网页,发送请求,将网页以流的形式读回来
				// 对响应码进行判断
				if (code == 200) {// 返回的响应码200,是成功
					// 得到网络返回的输入流
					InputStream is = conn.getInputStream();
					// resultStr = NetUtil.readString(is);
				} else {
					Toast.makeText(mContext, "����URLʧ�ܣ�", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// handler.sendEmptyMessage(0);// ִ执行耗时的方法之后发送消给handler
		}
	};

}
