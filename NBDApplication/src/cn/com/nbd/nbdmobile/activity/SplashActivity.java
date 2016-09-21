package cn.com.nbd.nbdmobile.activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.Options;
import cn.com.nbd.nbdmobile.utility.UserConfigUtile;
import cn.com.nbd.nbdmobile.view.CustomListViewUnused;
import cn.com.nbd.nbdmobile.view.PullDownViewNLogo;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;
import cn.jpush.android.api.JPushInterface;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.OpenAdvBean;
import com.nbd.article.bean.UserInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.OpenAdvCallback;
import com.nbd.article.managercallback.UserInfoCallback;
import com.nbd.network.bean.RequestType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;

/**
 * 金币界面 目前未开发，就一个图片和主题区分
 * 
 * @author riche
 * 
 */

public class SplashActivity extends Activity implements OnClickListener {

	private final String mPageName = "SplashPage";
	private ImageView mContent;
	private TextView mSimpleText;
	
	private RelativeLayout testLayout;

	private SharedPreferences mNativeShare;
	private SharedPreferences.Editor mEditor;

	private final int SPLASH_DISPLAY_LENGTH = 3000;
	private final int SPLASH_NONE_LENGTH = 1500;
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	private String mMd5;
	private String mUrl;
	private String mLink;
	private String mTitle;

	private Runnable mRunnable;
	private Context mContext;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			mContent.setEnabled(true);

			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mNativeShare = this.getSharedPreferences("NativeSetting",
				this.MODE_PRIVATE);
		mEditor = mNativeShare.edit();

		setContentView(R.layout.activity_splash);

		mContext = this;

		MobclickAgent.setDebugMode(true);
		// SDK在统计Fragment时，需要关闭Activity自带的页面统计，
		// 然后在每个页面中重新集成页面统计的代码(包括调用了 onResume 和 onPause 的Activity)。
		MobclickAgent.openActivityDurationTrack(false);
		// MobclickAgent.setAutoLocation(true);
		// MobclickAgent.setSessionContinueMillis(1000);
		// MobclickAgent.startWithConfigure(
		// new UMAnalyticsConfig(mContext, "4f83c5d852701564c0000011", "Umeng",
		// EScenarioType.E_UM_NORMAL));
		MobclickAgent.setScenarioType(mContext, EScenarioType.E_UM_NORMAL);

		mMd5 = mNativeShare.getString("startMd5", "");
		mUrl = mNativeShare.getString("startUrl", "");
		mLink = mNativeShare.getString("startLink", "");
		mTitle = mNativeShare.getString("startTittle", "");

		options = Options.getSplashOptions(this);

		initUi();
		setListener();
		initData();

	}

	private void initData() {

		showAdv();

		ArticleConfig delconfig = new ArticleConfig();
		delconfig.setType(RequestType.CLEAR_DELETE);
		ArticleManager.getInstence().clearDatabaseByArticleDelete(delconfig);
	}

	/**
	 * 显示广告图片
	 */
	private void showAdv() {

		if (mUrl == null || mUrl.equals("")) {
			// Intent i = new Intent(SplashActivity.this, MainActivity.class);
			// startActivity(i);
			// SplashActivity.this.finish();
			handler.postDelayed(mRunnable, SPLASH_NONE_LENGTH);
		} else {
			Log.e("OPEN ADV URL", ""+mUrl);
			mSimpleText.setVisibility(View.VISIBLE);
			mContent.setEnabled(true);
			imageLoader.displayImage(mUrl, mContent, options);

			handler.postDelayed(mRunnable, SPLASH_DISPLAY_LENGTH);
		}

	}

	private void initUi() {

		mContent = (ImageView) findViewById(R.id.splash_img);
		mSimpleText = (TextView) findViewById(R.id.splash_button);

		// mContent.setVisibility(View.GONE);
		mContent.setEnabled(false);
		mSimpleText.setVisibility(View.INVISIBLE);
		
//		testLayout = (RelativeLayout) findViewById(R.id.splash_test_layout);
//		
//		View v = new PullDownViewNLogo(this);
//		testLayout.addView(v);

	}

	private void setListener() {

		mContent.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mLink != null && !mLink.equals("")) {
					Intent i = new Intent(SplashActivity.this,
							AdvertiseWebviewActivity.class);
					Bundle b = new Bundle();
					b.putString("link", mLink);
					b.putString("title", mTitle);
					b.putBoolean("isStart", true);
					i.putExtra("urlbundle", b);
					startActivity(i);
					SplashActivity.this.finish();
					if (mRunnable != null) {
						handler.removeCallbacks(mRunnable);

					}
				}
			}
		});

		mSimpleText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(i);
				SplashActivity.this.finish();
				if (mRunnable != null) {
					handler.removeCallbacks(mRunnable);

				}
			}
		});

		mRunnable = new Runnable() {
			@Override
			public void run() {
				Intent i = new Intent(SplashActivity.this, MainActivity.class);
				startActivity(i);
				SplashActivity.this.finish();
			}
		};

	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			System.exit(0);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		JPushInterface.onResume(this);
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(mContext);
	}

	@Override
	protected void onPause() {
		super.onPause();
		JPushInterface.onPause(this);
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(mContext);
	}
}
