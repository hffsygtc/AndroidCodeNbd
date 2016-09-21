package cn.com.nbd.nbdmobile.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.ArticleHandleType;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.view.NbdProgressBar;
import cn.com.nbd.nbdmobile.webview.NBDJsNative;
import cn.com.nbd.nbdmobile.webview.NBDWebView;
import cn.com.nbd.nbdmobile.webview.NBDWebViewClient;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;
import cn.com.nbd.nbdmobile.widget.WebviewHandleMoreDialog;
import cn.com.nbd.nbdmobile.widget.WebviewHandleMoreDialog.onDialogBtnClick;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.mob.tools.MobLog;
import com.nbd.article.bean.ArticleInfo;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("SetJavaScriptEnabled")
public class AdvertiseWebviewActivity extends Activity {
	
	private NBDWebView mWebview;
	private NBDJsNative mJsNative;
	private WebSettings mWebSettings;

	private TextView mTitleText;
	private ImageView mBackBtn;

	private RelativeLayout mMoreHandleImg;

	// private NbdProgressBar mProgressBar;

	private String deatilHtml;
	private String mActivityTitle;

	private ArticleInfo mArticleInfo;

	private String urlString;
	private String titleString;

	private WebviewHandleMoreDialog mDialog;

	private SharedPreferences mConfigSharedPreferences;
	private SharedPreferences.Editor mEditor;

	private SharedPreferences mNativeShare;
	private boolean isDayTheme;
	private LoadingDialog mloading;
	private boolean isLoading;

	private boolean isNight;
	private boolean isStore;
	private int mTextSize;
	private String mAccessToken;
	private String mShareImage;

	private boolean isStart; // 是否是从启动页面广告跳转进来的

	public final static int LOAD_DATABASE_COMPLETE = 0;
	public final static int LOAD_INTERNET_COMPLETE = 1;
	public final static int LOADING_PROGRESS = 2;

	private enum fontSize {
		BIG, MID, SMALL
	}

	/**
	 * 处理文章数据回调的事件
	 * 
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case LOAD_DATABASE_COMPLETE:
				mWebview.loadData(deatilHtml, "text/html; charset=UTF-8", null);
				break;
			case LOAD_INTERNET_COMPLETE:
				if (mloading != null && mloading.isShowing()) {
					mloading.dismiss();
				}
				break;
			case LOADING_PROGRESS:
				if (!isLoading) {
					mloading.showFullDialog();
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mNativeShare = this.getSharedPreferences("NativeSetting",
				this.MODE_PRIVATE);

		isDayTheme = mNativeShare.getBoolean("theme", true);

		if (isDayTheme) {
			setTheme(R.style.DayTheme);
		} else {
			setTheme(R.style.NightTheme);
		}

		setContentView(R.layout.activity_webview_for_link);

		Intent i = getIntent();
		Bundle b = i.getBundleExtra("urlbundle");
		mActivityTitle = b.getString("title");
		urlString = b.getString("link");
		titleString = b.getString("title");
		isStart = b.getBoolean("isStart", false);
		mShareImage = b.getString("image");
		// mArticleId = b.getLong("url");

		mConfigSharedPreferences = this.getSharedPreferences("AppConfig",
				this.MODE_PRIVATE);
		mEditor = mConfigSharedPreferences.edit();
		mTextSize = mConfigSharedPreferences.getInt("ArticleTextSize", 1);
		mAccessToken = mConfigSharedPreferences.getString("accessToken", null);

		mloading = new LoadingDialog(AdvertiseWebviewActivity.this,
				R.style.loading_dialog, "加载中...");

		initUi();

		setListener();

		mWebSettings = mWebview.getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setSupportZoom(true);
		mWebSettings.setDomStorageEnabled(true);  

		// 扩大比例的缩放
		mWebSettings.setUseWideViewPort(true);
		mWebSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		// 自适应屏幕
		mWebSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mWebSettings.setLoadWithOverviewMode(true);
		// mWebview.setWebChromeClient(new WebChromeClient() {
		// @Override
		// public void onProgressChanged(WebView view, int newProgress) {
		//
		// Log.e("WEB PROGRESS", "" + newProgress);
		// Message msg = new Message();
		// msg.what = LOADING_PROGRESS;
		// msg.arg1 = newProgress;
		//
		// handler.sendMessage(msg);
		//
		// super.onProgressChanged(view, newProgress);
		// }
		//
		//
		// });
		//
		mWebview.setWebViewClient(new NBDWebViewClient() {

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				handler.obtainMessage(LOADING_PROGRESS).sendToTarget();
				super.onPageStarted(view, url, favicon);
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				Log.e("PAGE LOAD-->", "finish-->");
				handler.obtainMessage(LOAD_INTERNET_COMPLETE).sendToTarget();
				super.onPageFinished(view, url);
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				view.loadUrl(url);
				return true;
			}
			
			@Override
			public void onReceivedError(WebView view, int errorCode,
					String description, String failingUrl) {
				// TODO Auto-generated method stub
				super.onReceivedError(view, errorCode, description, failingUrl);
			}

		});

		mJsNative = new NBDJsNative(this, mWebview, mArticleInfo);
		mWebview.addJavascriptInterface(mJsNative, "nbd");

		Log.e("ADV URL==>", urlString);
		
		mWebview.loadUrl(urlString);
//		 mWebview.loadUrl("http://mobile2.itanzi.com/api2/a?NewsId=373774&pos=2&pg=0");

	}

	private void setListener() {
		mBackBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				if (isStart) {
//					Intent i = new Intent(AdvertiseWebviewActivity.this,
//							MainActivity.class);
//					startActivity(i);
//					AdvertiseWebviewActivity.this.finish();
//					if (mWebview != null) {
//						mWebview.destroy();
//					}
//				} else {
//					AdvertiseWebviewActivity.this.finish();
//					if (mWebview != null) {
//						mWebview.destroy();
//					}
//				}
				
				if (isStart) {
					if (mWebview != null) {
						int position = mWebview.copyBackForwardList()
								.getCurrentIndex();
						if (position > 0) {
							mWebview.goBack();

						} else {
							Intent i = new Intent(AdvertiseWebviewActivity.this,
									MainActivity.class);
							startActivity(i);
							AdvertiseWebviewActivity.this.finish();
							mWebview.destroy();
						}
					} else {
						Intent i = new Intent(AdvertiseWebviewActivity.this,
								MainActivity.class);
						startActivity(i);
						AdvertiseWebviewActivity.this.finish();
					}
				} else {
					if (mWebview != null) {
						int position = mWebview.copyBackForwardList()
								.getCurrentIndex();
						Log.e("POSITION==HISTOREY==", ""+position);
						if (position > 0) {
							mWebview.goBack();
						} else {
							AdvertiseWebviewActivity.this.finish();
							mWebview.destroy();
						}
					} else {
						AdvertiseWebviewActivity.this.finish();
					}
				}
			}
		});

		mMoreHandleImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog = new WebviewHandleMoreDialog(
						AdvertiseWebviewActivity.this, R.style.headdialog,
						false, isStore, mTextSize);
				mDialog.setOnBtnClickListener(new onDialogBtnClick() {

					@Override
					public void onModeTypeClick(ArticleHandleType type) {
						handleDialogMode(type);

					}
				});
				mDialog.show();
				WindowManager windowManager = getWindowManager();
				Display display = windowManager.getDefaultDisplay();
				WindowManager.LayoutParams lp = mDialog.getWindow()
						.getAttributes();
				lp.width = (int) (display.getWidth()); // 设置宽度
				mDialog.getWindow().setAttributes(lp);
			}
		});

	}

	private void initUi() {
		mWebview = (NBDWebView) findViewById(R.id.news_detail_webview);
		mTitleText = (TextView) findViewById(R.id.news_detail_title_txt);
		mBackBtn = (ImageView) findViewById(R.id.news_detail_back_arrow);
		// mProgressBar = (NbdProgressBar)
		// findViewById(R.id.news_detail_progressbar);

		mMoreHandleImg = (RelativeLayout) findViewById(R.id.news_detail_more_layout);

		mTitleText.setText(titleString);

	}

	private void showShare(ArticleInfo shArticleInfo, ArticleHandleType type) {

		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();

		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(titleString);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(urlString);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(titleString);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//		if (mShareImage == null || mShareImage.equals("")) {
			oks.setImageUrl("http://static.nbd.com.cn/images/nbd_icon.png");
//		}else {
//			oks.setImageUrl(mShareImage);
//		}
		// oks.setImageUrl(shArticleInfo.getImage());
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(urlString);
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(urlString);

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
		// oks.setPlatform(QQ.NAME);

		// 启动分享GUI
		oks.show(this);
		
		MobclickAgent.onEvent(AdvertiseWebviewActivity.this, UmenAnalytics.SHARE_WEB_PAGE);
	}

	/**
	 * 处理选择的更多操作中的各种操作
	 * 
	 * @param type
	 */
	private void handleDialogMode(ArticleHandleType type) {
		switch (type) {
		case WEIXIN:
			showShare(mArticleInfo, ArticleHandleType.WEIXIN);
			break;
		case WEIXIN_CIRCLE:
			showShare(mArticleInfo, ArticleHandleType.WEIXIN_CIRCLE);
			break;
		case SINA:
			showShare(mArticleInfo, ArticleHandleType.SINA);
			break;
		case QQ:
			showShare(mArticleInfo, ArticleHandleType.QQ);
			break;
		case QZONE:
			showShare(mArticleInfo, ArticleHandleType.QZONE);
			break;
		case REFRESH:
			if (mWebview != null) {
				mWebview.reload();
			}
			break;
		case NATIVE:
			Intent browserIntent = new Intent(Intent.ACTION_VIEW,
					Uri.parse(urlString));
			startActivity(browserIntent);
			break;
		case CANCLE:
			break;

		default:
			break;
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isStart) {
				if (mWebview != null) {
					int position = mWebview.copyBackForwardList()
							.getCurrentIndex();
					if (position > 0) {
						mWebview.goBack();

					} else {
						Intent i = new Intent(AdvertiseWebviewActivity.this,
								MainActivity.class);
						startActivity(i);
						AdvertiseWebviewActivity.this.finish();
						mWebview.destroy();
					}
				} else {
					Intent i = new Intent(AdvertiseWebviewActivity.this,
							MainActivity.class);
					startActivity(i);
					AdvertiseWebviewActivity.this.finish();
				}
			} else {
				if (mWebview != null) {
					int position = mWebview.copyBackForwardList()
							.getCurrentIndex();
					Log.e("POSITION==HISTOREY==", ""+position);
					if (position > 0) {
						mWebview.goBack();
					} else {
						AdvertiseWebviewActivity.this.finish();
						mWebview.destroy();
					}
				} else {
					AdvertiseWebviewActivity.this.finish();
				}
			}
		}

		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

}
