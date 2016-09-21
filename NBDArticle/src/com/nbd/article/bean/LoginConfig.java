package com.nbd.article.bean;

/**
 * 不同登陆方式的方式请求参数
 * @author riche
 *
 */
public class LoginConfig {

	/**第三方登陆的时候从sharesdk获取的token*/
	private String token;
	/**第三方登陆获取的openid*/
	private String openId;
	/**账号登陆的账号*/
	private String userId;
	/**账号登陆的密码*/
	private String password;
	
	private String userToken;
	
	private boolean isBlindAccount;
	
	public String getUserToken() {
		return userToken;
	}
	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	public boolean isBlindAccount() {
		return isBlindAccount;
	}
	public void setBlindAccount(boolean isBlindAccount) {
		this.isBlindAccount = isBlindAccount;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
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
	
	
}
