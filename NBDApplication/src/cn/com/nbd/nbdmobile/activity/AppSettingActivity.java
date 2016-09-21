package cn.com.nbd.nbdmobile.activity;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.ArticleHandleType;
import cn.com.nbd.nbdmobile.utility.DataCleanManager;
import cn.com.nbd.nbdmobile.widget.NbdAlrltDialog;
import cn.com.nbd.nbdmobile.widget.NbdAlrltDialog.onDialogChooseClick;
import cn.com.nbd.nbdmobile.widget.SettingTextSizeDialog;
import cn.com.nbd.nbdmobile.widget.SettingTextSizeDialog.onDialogBtnClick;

import com.nostra13.universalimageloader.utils.StorageUtils;
import com.umeng.analytics.MobclickAgent;

public class AppSettingActivity extends Activity implements OnClickListener {

	private ImageView mBackBtn; // 返回按钮

	private RelativeLayout mCacheLayout; // 清理缓存模块
	private TextView mCacheText; // 缓存大小

	private RelativeLayout mSizeLayout; // 字号大小条
	private TextView mSizeText; // 字号大小
	private RelativeLayout mCommentLayout; // 意见反馈条
	private RelativeLayout mAboutLayout; // 关于

	private RelativeLayout mNoticeLayout; // 声明条款条
	private RelativeLayout mVersionLayout; // 版本号条
	private TextView mVersionText; // 版本号
	private SharedPreferences mNativeShare;
	private SharedPreferences.Editor mNativeEditor;
	private boolean isDayTheme;
	private int mTextSize;

	private String mCacheSize;
	private String mVersionStr;

	private SettingTextSizeDialog mSizeDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mNativeShare = this.getSharedPreferences("NativeSetting",
				this.MODE_PRIVATE);
		mNativeEditor = mNativeShare.edit();

		isDayTheme = mNativeShare.getBoolean("theme", true);
		mTextSize = mNativeShare.getInt("ArticleTextSize", 1);

		if (isDayTheme) {
			setTheme(R.style.DayTheme);
		} else {
			setTheme(R.style.NightTheme);
		}
		setContentView(R.layout.activity_setting_app_info);

		getCacheSize();
		mVersionStr = getVersion();

		initUi();
		setListener();

	}

	/**
	 * 获取本地的图片缓存
	 */
	private void getCacheSize() {
		File cacheDir = StorageUtils.getOwnCacheDirectory(
				getApplicationContext(), "nbdnews/Cache");// 获取到缓存的目录地址
		long size;
		try {
			size = DataCleanManager.getFolderSize(cacheDir);
			mCacheSize = DataCleanManager.getFormatSize(size);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initUi() {
		mBackBtn = (ImageView) findViewById(R.id.app_setting_back_btn);

		mSizeLayout = (RelativeLayout) findViewById(R.id.app_setting_size_layout);
		mSizeText = (TextView) findViewById(R.id.app_setting_size_text);

		mCacheLayout = (RelativeLayout) findViewById(R.id.app_setting_cache_layout);
		mCacheText = (TextView) findViewById(R.id.app_setting_name_text);

		mAboutLayout = (RelativeLayout) findViewById(R.id.app_setting_about_layout);
		mNoticeLayout = (RelativeLayout) findViewById(R.id.app_setting_notice_layout);
		mCommentLayout = (RelativeLayout) findViewById(R.id.app_setting_comment_layout);
		
		mVersionText = (TextView) findViewById(R.id.app_setting_version_text);
		

		if (mTextSize == 0) {
			mSizeText.setText("小");
		} else if (mTextSize == 1) {
			mSizeText.setText("中");
		} else {
			mSizeText.setText("大");
		}

		if (mCacheSize != null) {
			mCacheText.setText(mCacheSize);
		}
		
		if (mVersionStr != null) {
			mVersionText.setText("V"+mVersionStr);
		}

	}

	private void setListener() {
		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AppSettingActivity.this.finish();
			}
		});

		mSizeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSizeDialog = new SettingTextSizeDialog(
						AppSettingActivity.this, R.style.headdialog, mTextSize);
				mSizeDialog.setOnBtnClickListener(new onDialogBtnClick() {

					@Override
					public void onModeTypeClick(ArticleHandleType type) {
						handleDialogMode(type);

					}
				});
				mSizeDialog.show();
				WindowManager windowManager = getWindowManager();
				Display display = windowManager.getDefaultDisplay();
				WindowManager.LayoutParams lp = mSizeDialog.getWindow()
						.getAttributes();
				lp.width = (int) (display.getWidth()); // 设置宽度
				mSizeDialog.getWindow().setAttributes(lp);
			}

		});

		mCacheLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				NbdAlrltDialog tipsDialog = new NbdAlrltDialog(
						AppSettingActivity.this, R.style.loading_dialog,
						"确定清除缓存？");
				tipsDialog.setOnBtnClickListener(new onDialogChooseClick() {

					@Override
					public void onModeTypeClick(ArticleHandleType type) {
						if (type == ArticleHandleType.OK) {
							File cacheDir = StorageUtils.getOwnCacheDirectory(
									getApplicationContext(), "nbdnews/Cache");// 获取到缓存的目录地址

							if (cacheDir != null) {
								if (DataCleanManager.deleteFolderFile(
										cacheDir.getPath(), true)) {
									Toast.makeText(AppSettingActivity.this,
											"清除缓存成功", Toast.LENGTH_SHORT)
											.show();
									mCacheText.setText("0.0MB");
								} else {
									Toast.makeText(AppSettingActivity.this,
											"清除缓存异常", Toast.LENGTH_SHORT)
											.show();
								}
							}
						}
					}
				});
				tipsDialog.showFullDialog();

			}
		});

		mNoticeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(AppSettingActivity.this,
						AdvertiseWebviewActivity.class);
				Bundle b = new Bundle();
				b.putString("link", "http://www.nbd.com.cn/terms_of_service");
				b.putString("title", "声明条款");
				i.putExtra("urlbundle", b);
				startActivity(i);
			}
		});
		
		mAboutLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent aboutIntent = new Intent(AppSettingActivity.this,
						SelfMoneyActivity.class);
				aboutIntent.putExtra("type", 1);
				startActivity(aboutIntent);
			}
		});
		
		mCommentLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent aboutIntent = new Intent(AppSettingActivity.this,
						FeedbacksActivity.class);
				startActivity(aboutIntent);
				
			}
		});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.self_center_setting_btn:

			break;
		case R.id.self_center_setting_layout:

			break;

		default:
			break;
		}

	}

	/**
	 * 处理选择的更多操作中的各种操作
	 * 
	 * @param type
	 */
	private void handleDialogMode(ArticleHandleType type) {
		switch (type) {
		case TEXT_SMALL:
			mNativeEditor.putInt("ArticleTextSize", 0);
			mNativeEditor.commit();
			mTextSize = 0;
			mSizeText.setText("小");
			break;
		case TEXT_MID:
			mNativeEditor.putInt("ArticleTextSize", 1);
			mNativeEditor.commit();
			mTextSize = 1;
			mSizeText.setText("中");
			break;
		case TEXT_BIG:
			mNativeEditor.putInt("ArticleTextSize", 2);
			mNativeEditor.commit();
			mTextSize = 2;
			mSizeText.setText("大");
			break;

		default:
			break;
		}

	}

	/**
	 * 2 * 获取版本号 3 * @return 当前应用的版本号 4
	 */
	public String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
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
