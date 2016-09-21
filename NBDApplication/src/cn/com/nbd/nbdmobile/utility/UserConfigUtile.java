package cn.com.nbd.nbdmobile.utility;

import com.nbd.article.bean.SelfMessage;
import com.nbd.article.bean.ThirdAccount;
import com.nbd.article.bean.ThirdAccountDetail;
import com.nbd.article.bean.UserInfo;
import com.nbd.article.bean.testAccountInfo;

import android.content.SharedPreferences;

/**
 * 本地个人登陆的配置信息存储
 * 
 * @author he
 * 
 */
public class UserConfigUtile {

	/**
	 * 存储用户的个人信息到本地sharepreference存储
	 * 
	 * @param editor
	 *            share编辑器
	 * @param user
	 *            个人信息
	 */
	public static void storeSelfConfigToNative(
			SharedPreferences.Editor mEditor, UserInfo info) {
		mEditor.putLong("uesrId", info.getUser_id()); // 用户id
		mEditor.putString("nickName", info.getNickname()); // 用户昵称
		mEditor.putString("uesrImg", info.getAvatar());// 头像地址
		mEditor.putString("accessToken", info.getAccess_token()); // 用户登陆token
		mEditor.putString("phoneNum", info.getPhone_no()); // 电话号码
		mEditor.putBoolean("push", info.isPush()); // 推送开关
		mEditor.putInt("colletion", info.getCollection()); // 收藏数量
		mEditor.putInt("reading", info.getReading()); // 阅读数
		mEditor.putInt("review", info.getReview()); // 评论数
		mEditor.putInt("sysmsg", info.getNotifications().getSys_msg()); // 系统消息
		mEditor.putInt("myMsg", info.getNotifications().getMy_msg()); // 个人消息
	
		if (info.getThird_account().getQq_connect() != null) { //第三方账号有QQ的授权信息
			mEditor.putString("qq", info.getThird_account().getQq_connect().getNickname());
		}
		if (info.getThird_account().getWeibo() != null) { //第三方账号有QQ的授权信息
			mEditor.putString("weibo", info.getThird_account().getWeibo().getNickname());
		}
		if (info.getThird_account().getWechat() != null) { //第三方账号有QQ的授权信息
			mEditor.putString("weixin", info.getThird_account().getWechat().getNickname());
		}
		
		if (info.getTest_account_info() != null) {
			mEditor.putBoolean("testAccount", info.getTest_account_info().isIs_test_account());
			mEditor.putInt("testUrl", info.getTest_account_info().getColumn_id());
		}
		mEditor.commit();
	}

	/**
	 * 获取个人用户信息从本地的share
	 * @param share
	 * @return
	 */
	public static UserInfo getUserinfoFormNative(SharedPreferences share) {
		UserInfo info = new UserInfo();

		info.setUser_id(share.getLong("uesrId", -1)); // 用户id
		info.setNickname(share.getString("nickName", null)); // 用户昵称
		info.setAvatar(share.getString("uesrImg", null));// 头像地址
		info.setAccess_token(share.getString("accessToken", null)); // 用户登陆token
		info.setPhone_no(share.getString("phoneNum", null)); // 电话号码
		info.setPush(share.getBoolean("push", false)); // 推送开关
		info.setCollection(share.getInt("colletion", 0)); // 收藏数量
		info.setReading(share.getInt("reading", 0)); // 阅读数
		info.setReview(share.getInt("review", 0)); // 评论数
		SelfMessage msg = new SelfMessage();
		msg.setSys_msg(share.getInt("sysmsg", 0));
		msg.setMy_msg(share.getInt("myMsg", 0));
		info.setNotifications(msg);
		
		String qq = share.getString("qq", null);
		String weibo = share.getString("weibo", null);
		String weixin = share.getString("weixin", null);
		
		info.setQqName(qq);
		info.setWeiboName(weibo);
		info.setWeixinName(weixin);
		
		testAccountInfo testAccountInfo = new testAccountInfo();
		if (share.getBoolean("testAccount", false)) {
			testAccountInfo.setIs_test_account(true);
			testAccountInfo.setColumn_id(share.getInt("testUrl", 0));
		}else {
			testAccountInfo = null;
		}
		info.setTest_account_info(testAccountInfo);
		return info;
	}

}
