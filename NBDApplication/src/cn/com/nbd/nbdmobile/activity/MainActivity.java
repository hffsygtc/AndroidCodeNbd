package cn.com.nbd.nbdmobile.activity;

import com.lecloud.skin.videoview.vod.UIVodVideoView;
import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.OpenAdvBean;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.OpenAdvCallback;
import com.nbd.network.bean.RequestType;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import cn.com.nbd.nbdmobile.fragment.MainFeatureFragment;
import cn.com.nbd.nbdmobile.fragment.MainNewsFragment;
import cn.com.nbd.nbdmobile.fragment.MainPaperFragment;
import cn.com.nbd.nbdmobile.fragment.MainVideoFragment;
import cn.com.nbd.nbdmobile.manager.OnThemeChangeListener;
import cn.com.nbd.nbdmobile.manager.ThemeChangeManager;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.widget.MyRadioButton;

/**
 * 应用首页，底部4个tab切换功能，个人中心跳转功能 搜索功能在每个不同的子fragment中
 * 
 * @author riche
 * 
 */
@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity {

	/** 底部栏目切换的控件组 */
	private RadioGroup menuGroup; // 底部栏目点击组
	private MyRadioButton newsRb; // 新闻按钮
	private MyRadioButton videoRb; // 视频按钮
	private MyRadioButton paperRb; // 原创按钮
	private MyRadioButton activityRb; // 活动按钮
	
	private RelativeLayout mainVideoLayout;
	
	private UIVodVideoView mVideoView;

	/** 首页管理的子页面组 */
	private FragmentManager fragmentManager; // frg管理器
	private MainNewsFragment mNewsFragment; // 新闻栏目
	private MainVideoFragment mVideoFragment; // 视频栏目
	private MainPaperFragment mPaperFragment; // 报纸原创栏目
	private MainFeatureFragment mFeatureFragment; // 专题活动栏目

	private TextView mNightCover;
	private ImageView mSelfImageBtn; // 个人中心按钮

	private Drawable drawble; // 底部按钮选中时的选中图标

	private Fragment checkFragmentFrom;

	private SharedPreferences sp;
	private SharedPreferences configSp;
	private SharedPreferences nativeSp;
	private SharedPreferences.Editor 	mEditor;
	private String mMd5;

	private int mShowPosition;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	private boolean isDayTheme;
	private boolean isTextMode;

	private long exitTime = 0;

	private OnThemeChangeListener themeListener;
	
	private Context activity;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏

		this.setTheme(R.style.NightTheme);

		setContentView(R.layout.activity_main);
		

		activity = this;
		
		/** 重置本地页面第一次打开的标志位 */
		sp = this.getSharedPreferences("FirstLoad", this.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.clear();
		editor.commit();

		// configSp = this.getSharedPreferences("AppConfig", this.MODE_PRIVATE);
		nativeSp = this
				.getSharedPreferences("NativeSetting", this.MODE_PRIVATE);
		
		mEditor = nativeSp.edit();
		mMd5 = nativeSp.getString("startMd5", "");
		isDayTheme = nativeSp.getBoolean("theme", true);
		isTextMode = nativeSp.getBoolean("textMode", false);
		
		mVideoView = new UIVodVideoView(this, false);

		initUi();

		setListener();

		// initFragment();

		setDefaultFragment();

		initData();
		// testCode();
	}

	private void initData() {
		ArticleConfig config = new ArticleConfig();
		config.setType(RequestType.OPEN_ADV);
		ArticleManager.getInstence().getStartAdv(config, new OpenAdvCallback() {

			@Override
			public void onStartAdvInfoCallback(OpenAdvBean openAdv) {

				if (openAdv != null) {
					Log.e("OPEN ADV",  "NOT NULL");
					if (openAdv.getMd5().equals(mMd5)) { // 如果获取到的数据和当初数据一致

					} else { // 如果不相同，数据有更新
						mEditor.putString("startMd5", openAdv.getMd5());
						mEditor.putString("startUrl", openAdv.getUrl());
						mEditor.putString("startLink", openAdv.getLink());
						mEditor.putString("startTittle", openAdv.getTitle());
						mEditor.commit();
						imageLoader.loadImage(openAdv.getUrl(), null);
					}
				}else {
					Log.e("OPEN ADV",  "NULL");
					if (!mMd5.equals("")) {
						mEditor.putString("startMd5", "");
						mEditor.putString("startUrl","");
						mEditor.putString("startLink","");
						mEditor.putString("startTittle", "");
						mEditor.commit();
					}
				}
			}
		});
		
	}

	private void initFragment() {
		if (mNewsFragment == null) {
			mNewsFragment = new MainNewsFragment();
		}
		if (mVideoFragment == null) {
			mVideoFragment = new MainVideoFragment();
		}
		if (mPaperFragment == null) {
			mPaperFragment = new MainPaperFragment();
		}
		if (mFeatureFragment == null) {
			mFeatureFragment = new MainFeatureFragment();
		}
		// if (fiveFrg == null) {
		// fiveFrg = new SimpleFrgment();
		// }

		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.add(mNewsFragment, "one");
		transaction.add(mVideoFragment, "two");
		transaction.add(mPaperFragment, "three");
		transaction.add(mFeatureFragment, "four");
		// transaction.add(fiveFrg, "five");
		transaction.commit();

	}

	/**
	 * 设置默认的Fragment
	 */
	private void setDefaultFragment() {
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		mNewsFragment = new MainNewsFragment();
		mNewsFragment.setTheme(isDayTheme);
		mNewsFragment.setTextMode(isTextMode);
		// transaction.replace(R.id.fragment_layout, mNewsFragment);
		transaction.add(R.id.fragment_layout, mNewsFragment);
		transaction.commit();
		checkFragmentFrom = mNewsFragment;
		mShowPosition = R.id.news;

		drawble = this.getResources().getDrawable(R.drawable.tab_news_click);
		newsRb.setCompoundDrawablesWithIntrinsicBounds(null, drawble, null,
				null);
		newsRb.setTextColor(getResources().getColor(
				R.color.bottom_tab_click_txt));

	}

	/**
	 * 初始化界面控件
	 * 
	 */
	private void initUi() {
		fragmentManager = getSupportFragmentManager();
		menuGroup = (RadioGroup) findViewById(R.id.main_radiogroup);

		newsRb = (MyRadioButton) menuGroup.getChildAt(0);
		videoRb = (MyRadioButton) menuGroup.getChildAt(1);
		paperRb = (MyRadioButton) menuGroup.getChildAt(2);
		activityRb = (MyRadioButton) menuGroup.getChildAt(3);

		mSelfImageBtn = (ImageView) findViewById(R.id.main_self_btn);
		mNightCover = (TextView) findViewById(R.id.main_night_cover);

		changeTabTheme();

	}

	/**
	 * 更改底部的4个tab的主题颜色
	 */
	private void changeTabTheme() {

		if (isDayTheme) {
			ThemeUtil.setBackgroundDay(MainActivity.this, menuGroup);
			mNightCover.setBackgroundColor(this.getResources().getColor(
					R.color.day_cover));
		} else {
			ThemeUtil.setBackgroundNight(MainActivity.this, menuGroup);
			mNightCover.setBackgroundColor(this.getResources().getColor(
					R.color.night_cover));
		}

	}

	/** 设置界面监听事件 */
	private void setListener() {

		themeListener = new OnThemeChangeListener() {

			@Override
			public void onNightThemeChange(boolean isNowTheme) {
				Log.e("THEME-CHANGE", isNowTheme + "");
				changeThemeToNews(isNowTheme);

			}
		};

		// 底部栏目切换点击事件
		menuGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {

				if (mShowPosition != checkedId) {

					mShowPosition = checkedId;
//					MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
					setChecked(checkedId);
				}
			}
		});

		// 个人中心图标点击事件
		mSelfImageBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent i = new Intent(MainActivity.this,
						SelfCenterActivity.class);
				startActivityForResult(i, 1);

			}
		});

		ThemeChangeManager.getInstance().registerThemeChangeListener(
				themeListener);

	}

	public void setChecked(final int Targetindex) {
		// 开启Fragment事务
		FragmentTransaction transaction = fragmentManager.beginTransaction();

		setTabImage();
		switch (Targetindex) {
		case R.id.news:
			// 使用当前Fragment的布局替代id_content的控件
			if (mNewsFragment == null) {
				mNewsFragment = new MainNewsFragment();
				mNewsFragment.setTheme(isDayTheme);
				mNewsFragment.setTextMode(isTextMode);
			}

			if (!mNewsFragment.isAdded()) {
				transaction.remove(checkFragmentFrom)
						.add(R.id.fragment_layout, mNewsFragment).commitAllowingStateLoss();
			} else {
				transaction.remove(checkFragmentFrom).show(mNewsFragment)
						.commitAllowingStateLoss();
			}

			// transaction.replace(R.id.fragment_layout,
			// mNewsFragment).commit();
			checkFragmentFrom = mNewsFragment;

			// transaction.replace(R.id.fragment_layout, oneFrg);
			drawble = this.getResources()
					.getDrawable(R.drawable.tab_news_click);
			newsRb.setCompoundDrawablesWithIntrinsicBounds(null, drawble, null,
					null);
			newsRb.setTextColor(getResources().getColor(
					R.color.bottom_tab_click_txt));
			break;
		case R.id.videos:
			if (mVideoFragment == null) {
				mVideoFragment = new MainVideoFragment();
				mVideoFragment.setTheme(isDayTheme);
				mVideoFragment.setMode(isTextMode);
			}
			
			//切换时，如果新闻栏目有视频在播放，先关闭释放新闻栏目的视频
			if (checkFragmentFrom != null && (checkFragmentFrom instanceof MainNewsFragment)) {
				((MainNewsFragment)checkFragmentFrom).stopVideoPlaying();
			}

			if (checkFragmentFrom != null && checkFragmentFrom == mNewsFragment) { // 从新闻界面过来的
				transaction.hide(checkFragmentFrom)
						.add(R.id.fragment_layout, mVideoFragment).commitAllowingStateLoss();

			} else {
				transaction.remove(checkFragmentFrom)
						.add(R.id.fragment_layout, mVideoFragment).commitAllowingStateLoss();
				// transaction.replace(R.id.fragment_layout,
				// mVideoFragment).commit();
			}

			// if (!mVideoFragment.isAdded()) {
			// } else {
			// transaction.hide(checkFragmentFrom).show(mVideoFragment)
			// .commit();
			// }

			// transaction.replace(R.id.fragment_layout,
			// mVideoFragment).commit();
			checkFragmentFrom = mVideoFragment;

			// transaction.replace(R.id.fragment_layout, twoFrg);

			drawble = this.getResources().getDrawable(
					R.drawable.tab_video_click);
			videoRb.setCompoundDrawablesWithIntrinsicBounds(null, drawble,
					null, null);
			videoRb.setTextColor(getResources().getColor(
					R.color.bottom_tab_click_txt));
			break;
		case R.id.paper:
			if (mPaperFragment == null) {
				mPaperFragment = new MainPaperFragment();
				mPaperFragment.setTheme(isDayTheme);
				mPaperFragment.setMode(isTextMode);
			}

			// if (!mPaperFragment.isAdded()) {
			// transaction.hide(checkFragmentFrom)
			// .add(R.id.fragment_layout, mPaperFragment).commit();
			// } else {
			// transaction.hide(checkFragmentFrom).show(mPaperFragment)
			// .commit();
			// }
			
			//切换时，如果新闻栏目有视频在播放，先关闭释放新闻栏目的视频
			if (checkFragmentFrom != null && (checkFragmentFrom instanceof MainNewsFragment)) {
				((MainNewsFragment)checkFragmentFrom).stopVideoPlaying();
			}
			
			if (checkFragmentFrom != null && checkFragmentFrom == mNewsFragment) { // 从新闻界面过来的
				transaction.hide(checkFragmentFrom)
						.add(R.id.fragment_layout, mPaperFragment).commitAllowingStateLoss();

			} else {
				transaction.remove(checkFragmentFrom)
						.add(R.id.fragment_layout, mPaperFragment).commitAllowingStateLoss();
				// transaction.replace(R.id.fragment_layout,
				// mPaperFragment).commit();
			}
			// transaction.replace(R.id.fragment_layout,
			// mPaperFragment).commit();
			checkFragmentFrom = mPaperFragment;

			// transaction.replace(R.id.fragment_layout, threeFrg);
			drawble = this.getResources().getDrawable(
					R.drawable.tab_paper_click);
			paperRb.setCompoundDrawablesWithIntrinsicBounds(null, drawble,
					null, null);
			paperRb.setTextColor(getResources().getColor(
					R.color.bottom_tab_click_txt));
			break;
		case R.id.activity:
			if (mFeatureFragment == null) {
				mFeatureFragment = new MainFeatureFragment();
				mFeatureFragment.setTheme(isDayTheme);
				mFeatureFragment.setMode(isTextMode);
			}

			// if (!mFeatureFragment.isAdded()) {
			// transaction.hide(checkFragmentFrom)
			// .add(R.id.fragment_layout, mFeatureFragment).commit();
			// } else {
			// transaction.hide(checkFragmentFrom).show(mFeatureFragment)
			// .commit();
			// }
			
			//切换时，如果新闻栏目有视频在播放，先关闭释放新闻栏目的视频
			if (checkFragmentFrom != null && (checkFragmentFrom instanceof MainNewsFragment)) {
				((MainNewsFragment)checkFragmentFrom).stopVideoPlaying();
			}

			if (checkFragmentFrom != null && checkFragmentFrom == mNewsFragment) { // 从新闻界面过来的
				transaction.hide(checkFragmentFrom)
						.add(R.id.fragment_layout, mFeatureFragment).commitAllowingStateLoss();

			} else {
				transaction.remove(checkFragmentFrom)
						.add(R.id.fragment_layout, mFeatureFragment).commitAllowingStateLoss();
				// transaction.replace(R.id.fragment_layout,
				// mFeatureFragment).commit();
			}

			// transaction.replace(R.id.fragment_layout,
			// mFeatureFragment).commit();
			checkFragmentFrom = mFeatureFragment;

			// transaction.replace(R.id.fragment_layout, fourFrg);
			drawble = this.getResources().getDrawable(
					R.drawable.tab_activity_click);
			activityRb.setCompoundDrawablesWithIntrinsicBounds(null, drawble,
					null, null);
			activityRb.setTextColor(getResources().getColor(
					R.color.bottom_tab_click_txt));
			break;
		}
	}

	/**
	 * 底部TAB按钮点击切换字体颜色和图片资源
	 */
	private void setTabImage() {
		newsRb.setCompoundDrawablesWithIntrinsicBounds(null, this
				.getResources().getDrawable(R.drawable.tab_news), null, null);
		newsRb.setTextColor(getResources().getColor(R.color.bottom_tab_txt));
		videoRb.setCompoundDrawablesWithIntrinsicBounds(null, this
				.getResources().getDrawable(R.drawable.tab_video), null, null);
		videoRb.setTextColor(getResources().getColor(R.color.bottom_tab_txt));
		paperRb.setCompoundDrawablesWithIntrinsicBounds(null, this
				.getResources().getDrawable(R.drawable.tab_paper), null, null);
		paperRb.setTextColor(getResources().getColor(R.color.bottom_tab_txt));
		activityRb.setCompoundDrawablesWithIntrinsicBounds(null, this
				.getResources().getDrawable(R.drawable.tab_activity), null,
				null);
		activityRb
				.setTextColor(getResources().getColor(R.color.bottom_tab_txt));

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1 && resultCode == 1 && data != null) {
			boolean nowTheme = data.getBooleanExtra("theme", isDayTheme);
			boolean nowMode = data.getBooleanExtra("mode", isTextMode);
			// if (nowTheme & isDayTheme) { //当前主题和显示主题一致，不需要变化
			// }else {
			// isDayTheme = nowTheme;
			// changeTabTheme();
			// if (mNewsFragment != null) {
			// mNewsFragment.changeTheme(isDayTheme);
			// }
			//
			// if (mVideoFragment != null) {
			// mVideoFragment.changeTheme(isDayTheme);
			// }
			//
			// if (mPaperFragment != null) {
			// mPaperFragment.changeTheme(isDayTheme);
			// }
			//
			// if (mFeatureFragment != null) {
			// mFeatureFragment.changeTheme(isDayTheme);
			// }
			// }

			if (nowMode & isTextMode) {
			} else {
				isTextMode = nowMode;
				if (mNewsFragment != null) {
					mNewsFragment.changeMode(isTextMode);
				}
				if (mVideoFragment != null) {
					mVideoFragment.changeTextMode(isTextMode);
				}
				if (mPaperFragment != null) {
					mPaperFragment.changeMode(isTextMode);
				}
				if (mFeatureFragment != null) {
					mFeatureFragment.changeMode(isTextMode);
				}
			}

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 更改主界面的底部TAB 更改四个大栏目 更改主新闻节目的四个板块的模式切换
	 * 
	 * @param nowTheme
	 */
	private void changeThemeToNews(boolean nowTheme) {
		if (nowTheme & isDayTheme) { // 当前主题和显示主题一致，不需要变化
		} else {
			isDayTheme = nowTheme;
			changeTabTheme();
			if (mNewsFragment != null) {
				mNewsFragment.changeTheme(isDayTheme);
			}

			if (mVideoFragment != null) {
				mVideoFragment.changeTheme(isDayTheme);
			}

			if (mPaperFragment != null) {
				mPaperFragment.changeTheme(isDayTheme);
			}

			if (mFeatureFragment != null) {
				mFeatureFragment.changeTheme(isDayTheme);
			}
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {

		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

			Log.e("PLAYER==", "FULL SCREEN" + "横屏");
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Log.e("PLAYER==", "FULL SCREEN" + "竖屏");
		} else {
			Log.e("PLAYER==", "FULL SCREEN" + "其他");
		}
		if (mVideoFragment != null) {
			mVideoFragment.noticeFullScreenActiong(newConfig);
		}
		
		if (mNewsFragment != null) {
//			mNewsFragment.
		}

		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再次点击退出",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
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
