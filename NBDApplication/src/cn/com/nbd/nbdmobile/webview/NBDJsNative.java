package cn.com.nbd.nbdmobile.webview;


import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.activity.AdvertiseWebviewActivity;
import cn.com.nbd.nbdmobile.activity.CommentActivity;
import cn.com.nbd.nbdmobile.activity.ImagesGalleryActivity;
import cn.com.nbd.nbdmobile.activity.WebviewContentActivity;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.nbd.article.bean.ArticleInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;


public class NBDJsNative {
	private NBDWebView nativeWebView;
	private Activity baseActivity;
	private ArticleInfo mArticleInfo;
	
	public interface onArticleClickListener{
		void onArticleClick(long id);
	}
	
	private onArticleClickListener mListener;
	
	public void setOnArticleClickListener(onArticleClickListener listener){
		this.mListener = listener;
	}
	
	private enum shareType{
		WEIXIN,
		WEIXIN_CIRCLE,
		WEIBO
	}
	
	
	public NBDJsNative(Activity activity,NBDWebView webView,ArticleInfo info) {
		this.baseActivity = activity;
		this.nativeWebView = webView;
		this.mArticleInfo = info;
	}
	
	@JavascriptInterface
	public void nbdShow(String content){
		Toast.makeText(baseActivity, content, Toast.LENGTH_SHORT).show();
	}
	
	
	@JavascriptInterface
	public void nbdShare(String shareMethod){
		
		if (shareMethod.equals("weibo")) {
			showShare(mArticleInfo,shareType.WEIBO);
		}else if (shareMethod.equals("wechat_session")) {
			showShare(mArticleInfo,shareType.WEIXIN);
		}else {
			showShare(mArticleInfo,shareType.WEIXIN_CIRCLE);
		}
	}
	
	private void showShare(ArticleInfo shArticleInfo,shareType type) {

		ShareSDK.initSDK(baseActivity);
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
//		 oks.setImageUrl(shArticleInfo.getImage());
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
		oks.setSite(baseActivity.getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(shArticleInfo.getUrl());
		
		switch (type) {
		case WEIBO:
		oks.setPlatform(SinaWeibo.NAME);
			break;
		case WEIXIN:
		oks.setPlatform(Wechat.NAME);
			break;
		case WEIXIN_CIRCLE:
		oks.setPlatform(WechatMoments.NAME);
			break;

		default:
			break;
		}
		
//		oks.setPlatform(QQ.NAME);
		
		// 启动分享GUI
		oks.show(baseActivity);
	}
	
	@JavascriptInterface
	public void nbdRelatedNews(long content){
		Log.e("article--id", content+"");
		
		if (mListener != null) {
			mListener.onArticleClick(content);
		}else {
			Intent i = new Intent(baseActivity, WebviewContentActivity.class);
			Bundle b = new Bundle();
			b.putLong("url", content);
			b.putString("title", "相关文章");
			b.putBoolean("Jpush", false);
			i.putExtra("urlbundle", b);
			baseActivity.startActivity(i);
		}
	}

	@JavascriptInterface
	public void nbdGalleryClick(int galleryId){
		Log.e("gallery--id", galleryId+"");
		
			Intent i = new Intent(baseActivity, ImagesGalleryActivity.class);
			Bundle b = new Bundle();
			b.putInt("galleryId", galleryId);
			b.putInt("fromType", 1);
			i.putExtra("urlbundle", b);
			baseActivity.startActivity(i);
	}
	
	@JavascriptInterface
	public void nbdAdClick(String title,String url){
		if (url != null && !url.equals("")) {
			Intent i = new Intent(baseActivity,
					AdvertiseWebviewActivity.class);
			Bundle b = new Bundle();
			b.putString("link", url);
			b.putString("title", title);
			i.putExtra("urlbundle", b);
			baseActivity.startActivity(i);
		}
	}
	
	
	@JavascriptInterface
	public void nbdReviewClick(){
		if (mArticleInfo.isAllow_review()) {
			Intent i = new Intent(baseActivity,
					CommentActivity.class);
			i.putExtra("article_id", mArticleInfo.getArticle_id());
			i.putExtra("comment_num", mArticleInfo.getReview_count());
			baseActivity.startActivity(i);
		}
	}

}
