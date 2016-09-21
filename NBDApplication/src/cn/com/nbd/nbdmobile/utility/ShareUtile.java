package cn.com.nbd.nbdmobile.utility;

import java.util.HashMap;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.activity.LoginActivity;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.nbd.article.bean.ArticleInfo;
import com.umeng.analytics.MobclickAgent;

public class ShareUtile {
	
	public static void showShare(final Activity activity,ArticleInfo shArticleInfo,ArticleHandleType type) {

		MobclickAgent.onEvent(activity, UmenAnalytics.SHARE_ARTICLE);
		ShareSDK.initSDK(activity);
		OnekeyShare oks = new OnekeyShare();

		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(shArticleInfo.getTitle());
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(shArticleInfo.getUrl());
		// text是分享文本，所有平台都需要这个字段
		oks.setText(shArticleInfo.getDigest());
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		if (shArticleInfo.getImage() == null || shArticleInfo.getImage().equals("")) {
			oks.setImageUrl("http://static.nbd.com.cn/images/nbd_icon.png");
		}else {
			oks.setImageUrl(shArticleInfo.getImage());
		}
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(shArticleInfo.getUrl());
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
//		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(shArticleInfo.getUrl());

		if (type != null) {
			switch (type) {
			case WEIXIN:
				oks.setPlatform(Wechat.NAME);
				break;
			case WEIXIN_CIRCLE:
				oks.setPlatform(WechatMoments.NAME);
				break;
			case SINA:
				oks.setPlatform(SinaWeibo.NAME);
				break;
			case QQ:
				oks.setPlatform(QQ.NAME);
				break;
			case QZONE:
				oks.setPlatform(QZone.NAME);
				break;
				
				
			default:
				break;
			}
		}
		
		oks.setCallback(new PlatformActionListener() {
			@Override
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				Log.e("SHARE==>", "ERROR" + arg2.getMessage());

			}
			
			@Override
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				// TODO Auto-generated method stub
				
				Log.e("SHARE==>", "分享成功"+arg0.getName());
				
			}
			
			@Override
			public void onCancel(Platform arg0, int arg1) {
				Log.e("SHARE==>", "取消"+arg0.getName());
				
			}
		});

		// 启动分享GUI
		oks.show(activity);
	}

}
