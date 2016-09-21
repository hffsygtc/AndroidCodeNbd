package com.nbd.article.bean;

public class UserInfo {

	// "user_id":233451,
	// "nickname":"\u6bcf\u7ecf\u7f51\u53cb233451",
	// "email":"",
	// "gold":0,
	// "push":true,
	// "collection":0,
	// "reading":0,
	// "review":0,
	// "phone_no":"13458543291",
	// "avatar":"http://static.nbd.com.cn/images/app/avatar.png",
	// "access_token":"7e424792e5447e614b813e00933dccb0",
	// "third_account":{},"notifications":{"sys_msg":0,"my_msg":0}}}

	private long user_id;
	private String nickname;
	private String email;
	private int gold;
	private boolean push;
	private int collection; // 收藏
	private int reading; // 阅读数
	private int review; // 评论数
	private String phone_no;
	private String avatar;
	private String access_token;
	private SelfMessage notifications;
	private ThirdAccount third_account;
	
	private testAccountInfo test_account_info;
	
	public testAccountInfo getTest_account_info() {
		return test_account_info;
	}

	public void setTest_account_info(testAccountInfo test_account_info) {
		this.test_account_info = test_account_info;
	}

	private String qqName;
	private String weiboName;
	private String weixinName;

	public String getQqName() {
		return qqName;
	}

	public void setQqName(String qqName) {
		this.qqName = qqName;
	}

	public String getWeiboName() {
		return weiboName;
	}

	public void setWeiboName(String weiboName) {
		this.weiboName = weiboName;
	}

	public String getWeixinName() {
		return weixinName;
	}

	public void setWeixinName(String weixinName) {
		this.weixinName = weixinName;
	}

	public ThirdAccount getThird_account() {
		return third_account;
	}

	public void setThird_account(ThirdAccount third_account) {
		this.third_account = third_account;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public boolean isPush() {
		return push;
	}

	public void setPush(boolean push) {
		this.push = push;
	}

	public int getCollection() {
		return collection;
	}

	public void setCollection(int collection) {
		this.collection = collection;
	}

	public int getReading() {
		return reading;
	}

	public void setReading(int reading) {
		this.reading = reading;
	}

	public int getReview() {
		return review;
	}

	public void setReview(int review) {
		this.review = review;
	}

	public String getPhone_no() {
		return phone_no;
	}

	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public SelfMessage getNotifications() {
		return notifications;
	}

	public void setNotifications(SelfMessage notifications) {
		this.notifications = notifications;
	}
	

}
