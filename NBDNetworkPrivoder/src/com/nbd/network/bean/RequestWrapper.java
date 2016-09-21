package com.nbd.network.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 网络请求数据的封装 可扩展需要的传入参数
 * 
 * @author riche
 * 
 */
public class RequestWrapper {
	/** 请求类型 */
	final RequestType requestType;
	/** 资讯详情页文章的ID */
	private long zxxqId;

	private int count;
	private int page;

	private long maxId;

	private String newspaper_month;
	private String newspaper_date;

	private String login_token;
	private String login_openid;

	private Map<String, String> postRequest = new HashMap<String, String>();

	private String phoneNum;
	private String registerCode;
	private String registerPassword;

	private String userId;
	private String password;
	private String newPassword;

	private boolean isArrayReturn; // 返回的data是否是数组的格式
	private boolean commenFlag;

	private int commentType;
	private String commenString;
	private CommentHandleType handleType;
	private int commentId;
	
	private CrashBean mCrashBean;

	public CrashBean getmCrashBean() {
		return mCrashBean;
	}

	public void setmCrashBean(CrashBean mCrashBean) {
		this.mCrashBean = mCrashBean;
	}

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public CommentHandleType getHandleType() {
		return handleType;
	}

	public void setHandleType(CommentHandleType handleType) {
		this.handleType = handleType;
	}

	public String getCommenString() {
		return commenString;
	}

	public void setCommenString(String commenString) {
		this.commenString = commenString;
	}

	public int getCommentType() {
		return commentType;
	}

	public void setCommentType(int commentType) {
		this.commentType = commentType;
	}

	public boolean isCommenFlag() {
		return commenFlag;
	}

	public void setCommenFlag(boolean commenFlag) {
		this.commenFlag = commenFlag;
	}

	public boolean isArrayReturn() {
		return isArrayReturn;
	}

	public void setArrayReturn(boolean isArrayReturn) {
		this.isArrayReturn = isArrayReturn;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	private String accessToken;

	// 阅读收藏文章是是收藏还是取消收藏的标志
	private String read_collect_state;

	public String getRead_collect_state() {
		return read_collect_state;
	}

	public void setRead_collect_state(String read_collect_state) {
		this.read_collect_state = read_collect_state;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getRegisterCode() {
		return registerCode;
	}

	public void setRegisterCode(String registerCode) {
		this.registerCode = registerCode;
	}

	public String getRegisterPassword() {
		return registerPassword;
	}

	public void setRegisterPassword(String registerPassword) {
		this.registerPassword = registerPassword;
	}

	public String getPhoneNum() {
		return phoneNum;
	}

	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}

	public Map<String, String> getPostRequest() {
		return postRequest;
	}

	public void setPostRequest(Map<String, String> postRequest) {
		this.postRequest = postRequest;
	}

	public String getLogin_token() {
		return login_token;
	}

	public void setLogin_token(String login_token) {
		this.login_token = login_token;
	}

	public String getLogin_openid() {
		return login_openid;
	}

	public void setLogin_openid(String login_openid) {
		this.login_openid = login_openid;
	}

	public String getNewspaper_month() {
		return newspaper_month;
	}

	public void setNewspaper_month(String newspaper_month) {
		this.newspaper_month = newspaper_month;
	}

	public String getNewspaper_date() {
		return newspaper_date;
	}

	public void setNewspaper_date(String newspaper_date) {
		this.newspaper_date = newspaper_date;
	}

	public long getMaxId() {
		return maxId;
	}

	public void setMaxId(long maxId) {
		this.maxId = maxId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public long getZxxqId() {
		return zxxqId;
	}

	public void setZxxqId(long zxxqId) {
		this.zxxqId = zxxqId;
	}

	/** 获取评论时传入值....举个栗子，并添加set get 方法 */
	private long articleId;

	private String url;

	public long getArticleId() {
		return articleId;
	}

	public void setArticleId(long articleId) {
		this.articleId = articleId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RequestWrapper(RequestType requestType) {
		this.requestType = requestType;
	}

	public RequestType getRequestType() {
		return requestType;
	}

}
