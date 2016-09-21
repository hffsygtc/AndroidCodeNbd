package cn.com.nbd.nbdmobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.inputmethodservice.Keyboard.Key;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.manager.OnThemeChangeListener;
import cn.com.nbd.nbdmobile.manager.ThemeChangeManager;
import cn.com.nbd.nbdmobile.utility.Options;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.utility.UserConfigUtile;
import cn.com.nbd.nbdmobile.view.SwitchButton;
import cn.jpush.android.api.JPushInterface;

import com.nbd.article.bean.UserInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class SelfCenterActivity extends Activity implements OnClickListener {

	private ImageView mBackBtn; // 返回按钮
	private ImageView mSettingBtn; // 右上角设置按钮
	private ImageButton mHeadImg; // 头像图像
	private TextView mLoginText; // 登陆的文字
	private RelativeLayout mMainLayout;
	
	private TextView mCover;

	private LinearLayout mTopFourLayout; // 顶部四大板块的容器

	private RelativeLayout mScLayout; // 收藏块
	private RelativeLayout mScLoginLayout; // 登陆后的收藏块
	private TextView mScTitleText;
	private TextView mScLoginTitleText;
	private TextView mScNumText; // 登陆后的收藏数量控件

	private RelativeLayout mPlLayout; // 评论块
	private RelativeLayout mPlLoginLayout; // 登陆后的评论块
	private TextView mPlTitleText;
	private TextView mPlLoginTitleText;
	private TextView mPlNumText; // 登陆后的评论数量控件

	private RelativeLayout mYdLayout; // 阅读块
	private RelativeLayout mYdLoginLayout; // 登陆后的阅读块
	private TextView mYdTitleText;
	private TextView mYdLoginTitleText;
	private TextView mYdNumText; // 登陆后的阅读数量控件

	private RelativeLayout mJbLayout; // 金币块
	private RelativeLayout mJbLoginLayout; // 登陆后的金币块
	private TextView mJbTitleText;
	private TextView mJbLoginTitleText;
	private TextView mJbNumText; // 登陆后的金币数量控件

	private RelativeLayout mMessageLayout; // 我的消息条
	private TextView mMessageTitle; // 我的消息数量
	private TextView mMessageText; // 我的消息数量

	private RelativeLayout mSignCenterLayout; // 离线下载条
	private TextView mSignCenterTitle;

	private RelativeLayout mSearchLayout; // 新闻搜索条
	private TextView mSearchTitle;
	private RelativeLayout mSettingLayout; // 设置功能条
	private TextView mSettingTitle;
	private RelativeLayout mFeedbacksLayout;
	private TextView mFeedbacksTitle;
	
	private RelativeLayout mBianjiLayout; //编辑中心条

	private RelativeLayout mNightLayout;
	private SwitchButton mNightToggle; // 夜间模式的
	private TextView mNightTitle;
	private RelativeLayout mTextLayout;
	private TextView mTextTitle;
	private SwitchButton mTextToggle; // 文字模式
	private TextView mPushTitle;
	private RelativeLayout mPushLayout;
	private SwitchButton mPushToggle; // 推送功能
	
	private TextView mFirstGap;
	private TextView mSecondGap;

	private SharedPreferences mConfigShare; // 获取配置的个人信息
	private SharedPreferences mNativeShare; // 获取配置的个人信息
	private SharedPreferences.Editor mNativeEditor;

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	DisplayImageOptions options;

	// private String accessToken;
	// private String headImg;
	// private String nickName;

	private UserInfo mUserInfo;
	private boolean isDayTheme;
	private boolean isTextMode;
	private boolean isPushOpen;
	
	private OnThemeChangeListener mThemeChangeListener;

	private static int LOGIN_REQUEST = 0; // 登录请求码
	private static int SELF_SETTING_REQUEST = 1; // 个人设置请求码

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setTheme(R.style.DayTheme);
		setContentView(R.layout.activity_self_center);

		Log.e("SECLF ACTIVITY", "ONCREAT==");
		// 本地配置，用户相关信息的share
		mConfigShare = this
				.getSharedPreferences("AppConfig", this.MODE_PRIVATE);
		mNativeShare = this.getSharedPreferences("NativeSetting",
				this.MODE_PRIVATE);
		mNativeEditor = mNativeShare.edit();

		isDayTheme = mNativeShare.getBoolean("theme", true);
		isTextMode = mNativeShare.getBoolean("textMode", false);
		isPushOpen = mNativeShare.getBoolean("jpush", true);
		options = Options.getHeadOptions(this);

		initData();

		initUi();

		setListener();

	}

	/**
	 * 获取本地数据中的登陆状态
	 */
	private void initData() {

		mUserInfo = UserConfigUtile.getUserinfoFormNative(mConfigShare);

		Log.e("INIT-DATA==>",
				"token=" + mUserInfo.getAccess_token() + "nickname=="
						+ mUserInfo.getNickname() + "img=="
						+ mUserInfo.getAvatar());

	}

	private void initUi() {

		mCover = (TextView) findViewById(R.id.self_center_cover);
		mMainLayout = (RelativeLayout) findViewById(R.id.self_center_main_layout);
		mBackBtn = (ImageView) findViewById(R.id.self_center_back_btn);
		mSettingBtn = (ImageView) findViewById(R.id.self_center_setting_btn);
		mHeadImg = (ImageButton) findViewById(R.id.self_center_head_img);
		mLoginText = (TextView) findViewById(R.id.self_center_login_text);
		mTopFourLayout = (LinearLayout) findViewById(R.id.self_center_top_four_layout);

		mScLayout = (RelativeLayout) findViewById(R.id.self_center_sc_layout);
		mPlLayout = (RelativeLayout) findViewById(R.id.self_center_pl_layout);
		mYdLayout = (RelativeLayout) findViewById(R.id.self_center_yd_layout);
		mJbLayout = (RelativeLayout) findViewById(R.id.self_center_jb_layout);

		mScLoginLayout = (RelativeLayout) findViewById(R.id.self_center_sc_login_layout);
		mPlLoginLayout = (RelativeLayout) findViewById(R.id.self_center_pl_login_layout);
		mYdLoginLayout = (RelativeLayout) findViewById(R.id.self_center_yd_login_layout);
		mJbLoginLayout = (RelativeLayout) findViewById(R.id.self_center_jb_login_layout);

		mScNumText = (TextView) findViewById(R.id.self_center_login_sc_numtxt);
		mPlNumText = (TextView) findViewById(R.id.self_center_login_pl_numtxt);
		mYdNumText = (TextView) findViewById(R.id.self_center_login_yd_numtxt);
		mJbNumText = (TextView) findViewById(R.id.self_center_login_jb_numtxt);

		mMessageLayout = (RelativeLayout) findViewById(R.id.self_center_my_message_layout);
		mMessageText = (TextView) findViewById(R.id.self_center_item_mymsg_text);

		mSignCenterLayout = (RelativeLayout) findViewById(R.id.self_center_offline_layout);
		mSearchLayout = (RelativeLayout) findViewById(R.id.self_center_search_layout);
		mSettingLayout = (RelativeLayout) findViewById(R.id.self_center_setting_layout);
		mBianjiLayout = (RelativeLayout) findViewById(R.id.self_center_bianji_layout);
		mFeedbacksLayout = (RelativeLayout) findViewById(R.id.self_center_feedbacks_layout);

		mNightLayout = (RelativeLayout) findViewById(R.id.self_center_night_mode_layout);
		mTextLayout = (RelativeLayout) findViewById(R.id.self_center_text_mode_layout);
		mPushLayout = (RelativeLayout) findViewById(R.id.self_center_push_layout);

		mNightToggle = (SwitchButton) findViewById(R.id.self_center_night_toggle);
		mTextToggle = (SwitchButton) findViewById(R.id.self_center_text_toggle);
		mPushToggle = (SwitchButton) findViewById(R.id.self_center_push_toggle);
		
		mScTitleText = (TextView) findViewById(R.id.sc_title);
		mScLoginTitleText = (TextView) findViewById(R.id.self_center_login_sc_title);
		mPlTitleText = (TextView) findViewById(R.id.pl_title);
		mPlLoginTitleText = (TextView) findViewById(R.id.self_center_login_pl_title);
		mYdTitleText = (TextView) findViewById(R.id.yd_title);
		mYdLoginTitleText = (TextView) findViewById(R.id.self_center_login_yd_title);
		mJbTitleText = (TextView) findViewById(R.id.jb_title);
		mJbLoginTitleText = (TextView) findViewById(R.id.self_center_login_jb_title);
		mMessageTitle = (TextView) findViewById(R.id.message_title);
		mNightTitle = (TextView) findViewById(R.id.night_title);
		mTextTitle = (TextView) findViewById(R.id.text_title);
		mSignCenterTitle = (TextView) findViewById(R.id.offline_title);
		mSearchTitle = (TextView) findViewById(R.id.search_title);
		mPushTitle = (TextView) findViewById(R.id.push_title);
		mSettingTitle = (TextView) findViewById(R.id.setting_title);
		mFeedbacksTitle = (TextView) findViewById(R.id.feedbacks_title);
		
		mFirstGap = (TextView) findViewById(R.id.self_center_night_gap);
		mSecondGap = (TextView) findViewById(R.id.self_center_offline_gap);
		

		if (isDayTheme) {
			mNightToggle.setChecked(false);
		} else {
			mNightToggle.setChecked(true);
		}
		
		if (isTextMode) {
			mTextToggle.setChecked(true);
		}else {
			mTextToggle.setChecked(false);
		}
		
		if (isPushOpen) {
			mPushToggle.setChecked(true);
		}else {
			mPushToggle.setChecked(false);
		}

		setThemeColor();

		// setItemState();
	}

	
	/**
	 * 初始化更改界面的状态
	 */
	private void setItemState() {
		if (mUserInfo.getAccess_token() == null) {
//			 mHeadImg.setClickable(true);
			mMessageText.setText("0");

			changeLayoutByLoginState(0);
		} else {
//			 mHeadImg.setClickable(false);
			imageLoader.displayImage(mUserInfo.getAvatar(), mHeadImg, options);
			mLoginText.setText(mUserInfo.getNickname());

			Log.e("MESSGAE==>", "" + mUserInfo.getNotifications().getMy_msg()
					+ "==" + mUserInfo.getNotifications().getSys_msg());

			mMessageText
					.setText((mUserInfo.getNotifications().getMy_msg() + mUserInfo
							.getNotifications().getSys_msg()) + "");

			changeLayoutByLoginState(1);
			
			if (mUserInfo.getTest_account_info() != null) {
				mBianjiLayout.setVisibility(View.VISIBLE);
			}else {
				mBianjiLayout.setVisibility(View.GONE);
			}
		}

	}

	private void setListener() {
		mThemeChangeListener = new OnThemeChangeListener() {
			
			@Override
			public void onNightThemeChange(boolean isNowTheme) {
				
				if (isNowTheme & isDayTheme) {
					
				}else {
					isDayTheme = isNowTheme;
					if (isDayTheme) {
						mNightToggle.setChecked(false);
					} else {
						mNightToggle.setChecked(true);
					}
					
					setThemeColor();
					
				}
				
			}
		};
		
		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent resultIntent = new Intent();
				resultIntent.putExtra("theme", isDayTheme);
				resultIntent.putExtra("mode", isTextMode);
				SelfCenterActivity.this.setResult(1, resultIntent);
				SelfCenterActivity.this.finish();
			}
		});

		// 个人设置按钮的跳转，可进行第三方绑定和退出登录功能
		mSettingBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelfCenterActivity.this,
						AppSettingActivity.class);
				startActivity(i);
			}
		});

		mSettingLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelfCenterActivity.this,
						AppSettingActivity.class);
				startActivity(i);

			}
		});
		
		mBianjiLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelfCenterActivity.this,
						BianjiCenterActivity.class);
				if (mUserInfo != null && mUserInfo.getTest_account_info() != null) {
					i.putExtra("column", mUserInfo.getTest_account_info().getColumn_id());
					i.putExtra("token", mUserInfo.getAccess_token());
				}
				startActivity(i);
			}
		});
		
		mFeedbacksLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent aboutIntent = new Intent(SelfCenterActivity.this,
						FeedbacksActivity.class);
				startActivity(aboutIntent);
			}
		});
		
		mSearchLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelfCenterActivity.this, SearchActivity.class);
				startActivity(i);
			}
		});
		
		ThemeChangeManager.getInstance().registerThemeChangeListener(mThemeChangeListener);

		mHeadImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mUserInfo.getAccess_token() == null) {
					login();
				} else {
					Intent i = new Intent(SelfCenterActivity.this,
							SelfSettingActivity.class);
					Bundle b = new Bundle();
					b.putString("headImg", mUserInfo.getAvatar());
					b.putString("nickName", mUserInfo.getNickname());
					b.putString("token", mUserInfo.getAccess_token());
					i.putExtra("self", b);
					startActivityForResult(i, SELF_SETTING_REQUEST);
				}
				// startActivity(i);
			}
			
			
		});

		mNightToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				MobclickAgent.onEvent(SelfCenterActivity.this, UmenAnalytics.NIGHT_MODE_CHANGE);
				mNativeEditor.putBoolean("theme", !isChecked);
				mNativeEditor.commit();

				isDayTheme = !isChecked;
				
				ThemeChangeManager.getInstance().changeTheme(isDayTheme);

				setThemeColor();
			}
		});
		
		mTextToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				mNativeEditor.putBoolean("textMode", isChecked);
				mNativeEditor.commit();
				
				isTextMode = isChecked;
				
			}
		});
	
		mPushToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				
				mNativeEditor.putBoolean("jpush", isChecked);
				mNativeEditor.commit();
				
				isPushOpen = isChecked;
				
				if (isPushOpen) {
					JPushInterface.resumePush(getApplicationContext());
					if (JPushInterface.isPushStopped(getApplicationContext())) {
						JPushInterface.resumePush(getApplicationContext());
					}
				}else {
					JPushInterface.stopPush(getApplicationContext());
					if (!JPushInterface.isPushStopped(getApplicationContext())) {
						JPushInterface.stopPush(getApplicationContext());
					}
				}
				
			}
		});
		
		mSignCenterLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MobclickAgent.onEvent(SelfCenterActivity.this, UmenAnalytics.SIGN_CENTER);
				Intent i = new Intent(SelfCenterActivity.this,
						SignCenterActivity.class);
				startActivity(i);
			}
		});

		mMessageLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mUserInfo.getAccess_token() == null) {
					login();
				} else {
					Intent i = new Intent(SelfCenterActivity.this,
							MessageCenterActivity.class);
					i.putExtra("activityType", 0);
					i.putExtra("token", mUserInfo.getAccess_token());
					startActivity(i);
				}
			}
		});

		mScLayout.setOnClickListener(this);
		mPlLayout.setOnClickListener(this);
		mYdLayout.setOnClickListener(this);
		mJbLayout.setOnClickListener(this);

		mScLoginLayout.setOnClickListener(this);
		mPlLoginLayout.setOnClickListener(this);
		mYdLoginLayout.setOnClickListener(this);
		mJbLoginLayout.setOnClickListener(this);
	}

	/**
	 * 四大板块两种状态的点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.self_center_sc_layout:
			Intent scIntent = new Intent(SelfCenterActivity.this,
					MessageCenterActivity.class);
			scIntent.putExtra("activityType", 1);
			startActivity(scIntent);
			MobclickAgent.onEvent(SelfCenterActivity.this, UmenAnalytics.DETAIL_COLLECTION);
			break;
		case R.id.self_center_pl_layout:
			login();
			break;
		case R.id.self_center_yd_layout:
			login();
			break;
		case R.id.self_center_jb_layout:
			Intent jbIntent = new Intent(SelfCenterActivity.this,
					SelfMoneyActivity.class);
			jbIntent.putExtra("type", 0);
			startActivity(jbIntent);
			break;
		case R.id.self_center_sc_login_layout:
			MobclickAgent.onEvent(SelfCenterActivity.this, UmenAnalytics.DETAIL_COLLECTION);
			Intent scloginIntent = new Intent(SelfCenterActivity.this,
					MessageCenterActivity.class);
			scloginIntent.putExtra("activityType", 1);
			scloginIntent.putExtra("token", mUserInfo.getAccess_token());
			startActivity(scloginIntent);
			break;
		case R.id.self_center_pl_login_layout:
			MobclickAgent.onEvent(SelfCenterActivity.this, UmenAnalytics.SELF_REVIEW);
			Intent plloginIntent = new Intent(SelfCenterActivity.this,
					SelfCommentActivity.class);
			startActivity(plloginIntent); 

			break;
		case R.id.self_center_yd_login_layout:
			MobclickAgent.onEvent(SelfCenterActivity.this, UmenAnalytics.SELF_READING);
			Intent readingIntent = new Intent(SelfCenterActivity.this,
					SelfReadingActivity.class);
			readingIntent.putExtra("token", mUserInfo.getAccess_token());
			startActivity(readingIntent);

			break;
		case R.id.self_center_jb_login_layout:
			Intent jbLoginIntent = new Intent(SelfCenterActivity.this,
					SelfMoneyActivity.class);
			jbLoginIntent.putExtra("type", 0);
			startActivity(jbLoginIntent);

			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		Log.e("SECLF ACTIVITY", "ONRESUM==");
		initData();
		setItemState();
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == LOGIN_REQUEST) { // 个人登录部分的返回码
			if (data != null) {
				mUserInfo = UserConfigUtile.getUserinfoFormNative(mConfigShare);
				// Bundle resultBundle = data.getBundleExtra("login");
				// nickName = resultBundle.getString("nickName");
				// headImg = resultBundle.getString("headImg");

				// if (accessToken == null || accessToken.equals("")) {
				// mHeadImg.setClickable(true);
				// } else {
				imageLoader.displayImage(mUserInfo.getAvatar(), mHeadImg,
						options);
				mLoginText.setText(mUserInfo.getNickname());
				mMessageText
						.setText((mUserInfo.getNotifications().getMy_msg() + mUserInfo
								.getNotifications().getSys_msg()) + "");
				changeLayoutByLoginState(1);
				// }
			}
		} else if (requestCode == SELF_SETTING_REQUEST) {
			Log.e("SETTING CLEAR", "***");
			if (resultCode == 1) {
				mUserInfo = UserConfigUtile.getUserinfoFormNative(mConfigShare);
				mHeadImg.setBackgroundResource(R.drawable.self_center_default_head);
				imageLoader.displayImage("", mHeadImg, options);
				mLoginText.setText("点击登录");
				changeLayoutByLoginState(0);
			} else if (resultCode == 2) {
				mUserInfo = UserConfigUtile.getUserinfoFormNative(mConfigShare);
				imageLoader.displayImage(mUserInfo.getAvatar(), mHeadImg,
						options);
				mLoginText.setText(mUserInfo.getNickname());
			}
		}
	}

	/**
	 * 根据登录状态，改变四大板块显示的内容
	 * 
	 * @param 0 表示未登录 1表示登录
	 */
	private void changeLayoutByLoginState(int loginState) {
		switch (loginState) {
		case 0: // 未登录
			mScLayout.setVisibility(View.VISIBLE);
			mPlLayout.setVisibility(View.VISIBLE);
			mYdLayout.setVisibility(View.VISIBLE);
			mJbLayout.setVisibility(View.VISIBLE);

			mScLoginLayout.setVisibility(View.GONE);
			mPlLoginLayout.setVisibility(View.GONE);
			mYdLoginLayout.setVisibility(View.GONE);
			mJbLoginLayout.setVisibility(View.GONE);
			break;
		case 1: // 登录
			mScLayout.setVisibility(View.GONE);
			mPlLayout.setVisibility(View.GONE);
			mYdLayout.setVisibility(View.GONE);
			mJbLayout.setVisibility(View.GONE);

			mScLoginLayout.setVisibility(View.VISIBLE);
			mPlLoginLayout.setVisibility(View.VISIBLE);
			mYdLoginLayout.setVisibility(View.VISIBLE);
			mJbLoginLayout.setVisibility(View.VISIBLE);

			mScNumText.setText(mUserInfo.getCollection() + "");
			mPlNumText.setText(mUserInfo.getReview() + "");
			mYdNumText.setText(mUserInfo.getReading() + "");
			mJbNumText.setText("0");

			break;

		default:
			break;
		}

	}

	/**
	 * 未登录时点击登陆
	 */
	private void login() {
		Intent i = new Intent(SelfCenterActivity.this, LoginActivity.class);

		startActivityForResult(i, LOGIN_REQUEST);
	}
	
	/**
	 * 根据当前模式，调节界面的显示颜色
	 */
	private void setThemeColor() {
		if (isDayTheme) {
			Log.e("SELF_CENTER==>", "day color ====");
			ThemeUtil.setSectionBackgroundDay(SelfCenterActivity.this,
					mMainLayout);
			ThemeUtil.setItemBackgroundDay(SelfCenterActivity.this,
					mTopFourLayout);
			ThemeUtil.setItemBackgroundDay(SelfCenterActivity.this,
					mMessageLayout);
			ThemeUtil.setItemBackgroundDay(SelfCenterActivity.this,
					mSignCenterLayout);
			ThemeUtil.setItemBackgroundDay(SelfCenterActivity.this,
					mSearchLayout);
			ThemeUtil.setItemBackgroundDay(SelfCenterActivity.this,
					mFeedbacksLayout);
			ThemeUtil.setItemBackgroundDay(SelfCenterActivity.this,
					mSettingLayout);
			ThemeUtil.setItemBackgroundDay(SelfCenterActivity.this,
					mNightLayout);
			ThemeUtil.setItemBackgroundDay(SelfCenterActivity.this,
					mFirstGap);
			ThemeUtil.setItemBackgroundDay(SelfCenterActivity.this,
					mSecondGap);
			ThemeUtil
					.setItemBackgroundDay(SelfCenterActivity.this, mTextLayout);
			ThemeUtil
					.setItemBackgroundDay(SelfCenterActivity.this, mPushLayout);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mLoginText);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mScTitleText);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mScLoginTitleText);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mPlTitleText);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mPlLoginTitleText);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mYdTitleText);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mYdLoginTitleText);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mJbTitleText);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mJbLoginTitleText);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mMessageTitle);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mNightTitle);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mTextTitle);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mSignCenterTitle);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mSearchTitle);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mPushTitle);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mFeedbacksTitle);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mSettingTitle);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mScNumText);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mPlNumText);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mYdNumText);
			ThemeUtil.setTextColorDay(SelfCenterActivity.this, mJbNumText);
			
			
			mCover.setBackgroundColor(this.getResources().getColor(R.color.day_cover));
			
		} else {
			ThemeUtil.setBackgroundNight(SelfCenterActivity.this, mMainLayout);
			ThemeUtil.setSectionBackgroundNight(SelfCenterActivity.this,
					mTopFourLayout);
			ThemeUtil.setSectionBackgroundNight(SelfCenterActivity.this,
					mMessageLayout);
			ThemeUtil.setSectionBackgroundNight(SelfCenterActivity.this,
					mSignCenterLayout);
			ThemeUtil.setSectionBackgroundNight(SelfCenterActivity.this,
					mSearchLayout);
			ThemeUtil.setSectionBackgroundNight(SelfCenterActivity.this,
					mSettingLayout);
			ThemeUtil.setSectionBackgroundNight(SelfCenterActivity.this,
					mNightLayout);
			ThemeUtil.setSectionBackgroundNight(SelfCenterActivity.this,
					mTextLayout);
			ThemeUtil.setSectionBackgroundNight(SelfCenterActivity.this,
					mPushLayout);
			ThemeUtil.setSectionBackgroundNight(SelfCenterActivity.this,
					mFeedbacksLayout);
			ThemeUtil.setSectionBackgroundNight(SelfCenterActivity.this,
					mFirstGap);
			ThemeUtil.setSectionBackgroundNight(SelfCenterActivity.this,
					mSecondGap);
			
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mLoginText);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mScTitleText);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mScLoginTitleText);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mPlTitleText);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mPlLoginTitleText);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mYdTitleText);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mYdLoginTitleText);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mJbTitleText);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mJbLoginTitleText);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mMessageTitle);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mNightTitle);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mTextTitle);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mSignCenterTitle);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mSearchTitle);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mPushTitle);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mFeedbacksTitle);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mSettingTitle);
			
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mScNumText);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mPlNumText);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mYdNumText);
			ThemeUtil.setTextColorNight(SelfCenterActivity.this, mJbNumText);
			
			mCover.setBackgroundColor(this.getResources().getColor(R.color.night_cover));

		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent resultIntent = new Intent();
			resultIntent.putExtra("theme", isDayTheme);
			resultIntent.putExtra("mode", isTextMode);
			SelfCenterActivity.this.setResult(1, resultIntent);
			SelfCenterActivity.this.finish();
		}
		
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		ThemeChangeManager.getInstance().unregisterThemeChangeListener(mThemeChangeListener);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

}
