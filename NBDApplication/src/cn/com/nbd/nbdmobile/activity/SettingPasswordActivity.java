package cn.com.nbd.nbdmobile.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.utility.UserConfigUtile;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.UserInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.StringInfoCallback;
import com.nbd.article.managercallback.UserInfoCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

/**
 * 设置密码界面
 * 
 * @author riche
 * 
 */

public class SettingPasswordActivity extends Activity implements
		OnClickListener {

	private ImageView mBackBtn; // 返回按钮
	private TextView mTitleText; // 标题栏名称
	private EditText mPasswordEdit; // 手机号码输入框
	private EditText mPasswordCheckEdit; // 验证码输入框
	private EditText mPasswordThirdEdit; // 修改密码时的第三个输入框
	private RelativeLayout mThirdLayout; // 第三个板块
	private TextView mSecondDivLine; // 第二个分割线
	private CheckBox mHideCheckBox; // 同意条款勾选框
	private Button mCompleteButton; // 下一步按钮

	private String mPasswordStr;
	private String mPasswordCheckStr;
	private String mPasswordThirdStr;

	private String mPhoneNum;
	private String mCode;

	private SharedPreferences configShare;

	private int passwordActivityType; // 页面类型

	private static int SET_PASSWORD = 0; //注册时设置密码
	private static int RESET_PASSWORD = 1;//修改密码时 
	private static int FORGET_PASSWORD =2; //忘记密码时设置密码

	private String mAccessToken; // 用户的登录标识
	
	private SharedPreferences mNativeShare;
	private boolean isDayTheme;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mNativeShare = this.getSharedPreferences("NativeSetting",
				this.MODE_PRIVATE);

		isDayTheme = mNativeShare.getBoolean("theme", true);
		
		if (isDayTheme) {
			setTheme(R.style.DayTheme);
		}else {
			setTheme(R.style.NightTheme);
		}
		setContentView(R.layout.activity_register_password);
		Intent i = getIntent();
		mPhoneNum = i.getStringExtra("phone");
		mCode = i.getStringExtra("code");

		passwordActivityType = i.getIntExtra("type", -1);

		configShare = this.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);

		mAccessToken = configShare.getString("accessToken", null);
		
		Log.e("RESET_PASSWORD-", "TOKEN"+mAccessToken);

		initUi();
		setListener();

	}

	private void initUi() {

		mBackBtn = (ImageView) findViewById(R.id.password_set_back_btn);
		mTitleText = (TextView) findViewById(R.id.password_setting_title);
		mPasswordEdit = (EditText) findViewById(R.id.password_set_user_edittext);
		mPasswordCheckEdit = (EditText) findViewById(R.id.password_set_password_edittext);
		mPasswordThirdEdit = (EditText) findViewById(R.id.password_third_password_edittext);
		mThirdLayout = (RelativeLayout) findViewById(R.id.password_third_password_layout);
		mSecondDivLine = (TextView) findViewById(R.id.password_set_seconed_gap);
		mHideCheckBox = (CheckBox) findViewById(R.id.password_set_agree_checkbox);
		mCompleteButton = (Button) findViewById(R.id.password_set_button);

		mHideCheckBox.setChecked(true);
		mCompleteButton.setEnabled(false);

		if (passwordActivityType == SET_PASSWORD) { // 设置密码
			mTitleText.setText("注册账号");
			mThirdLayout.setVisibility(View.GONE);
			mSecondDivLine.setVisibility(View.GONE);
			mPasswordEdit.setHint("请设置个人登录密码");
			mPasswordCheckEdit.setHint("请再输入一次");

		} else if (passwordActivityType == RESET_PASSWORD) { // 重设密码
			mTitleText.setText("修改密码");
			mThirdLayout.setVisibility(View.VISIBLE);
			mSecondDivLine.setVisibility(View.VISIBLE);
			mPasswordEdit.setHint("请输入旧密码");
			mPasswordCheckEdit.setHint("请输入新密码");
			mPasswordThirdEdit.setHint("请再输入一次新密码");

		}else {
			mTitleText.setText("忘记密码");
			mThirdLayout.setVisibility(View.GONE);
			mSecondDivLine.setVisibility(View.GONE);
			mPasswordEdit.setHint("请设置个人登录密码");
			mPasswordCheckEdit.setHint("请再输入一次");
		}

	}

	private void setListener() {

		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SettingPasswordActivity.this.finish();
			}
		});

		mPasswordEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				mPasswordStr = s.toString();
				changeCompleteButtonState();
			}
		});

		mPasswordCheckEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				mPasswordCheckStr = s.toString();
				changeCompleteButtonState();
			}
		});

		mPasswordThirdEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				mPasswordThirdStr = s.toString();
				changeCompleteButtonState();
			}
		});

		mHideCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					mPasswordCheckEdit
							.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
					mPasswordEdit
							.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
					mPasswordThirdEdit
							.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);

				} else {
					mPasswordCheckEdit.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					mPasswordEdit.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					mPasswordThirdEdit.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);

				}

			}
		});

		mCompleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mAccessToken != null) { //  登录的情况下修改密码
					if (!mPasswordCheckStr.equals(mPasswordThirdStr)) {
						Toast.makeText(SettingPasswordActivity.this,
								"请输入相同的密码", Toast.LENGTH_SHORT).show();
					} else {
						ArticleConfig config = new ArticleConfig();
						config.setType(RequestType.RESET_PASSWORD);
						config.setPassword(mPasswordStr);
						config.setNewPassword(mPasswordThirdStr);
						config.setAccessToken(mAccessToken);
						ArticleManager.getInstence().resetPassword(config,new StringInfoCallback() {
							
							@Override
							public void onStringDataCallback(String s, boolean isSuccess) {
								if (isSuccess) {
									SettingPasswordActivity.this.finish();
								}else {
									Toast.makeText(SettingPasswordActivity.this, s, Toast.LENGTH_SHORT).show();
								}
							}
						});
						
					}

				} else if (passwordActivityType == FORGET_PASSWORD) { //忘记密码的时候的情况
					
					if (!mPasswordCheckStr.equals(mPasswordStr)) {
						Toast.makeText(SettingPasswordActivity.this,
								"请输入相同的密码", Toast.LENGTH_SHORT).show();
					} else {
						ArticleConfig config = new ArticleConfig();
						config.setType(RequestType.RESET_PASSWORD);
						config.setPassword(mPasswordStr);
						config.setLocalArticle(true);
						config.setNewPassword(mCode);
						config.setAccessToken(mPhoneNum);
						ArticleManager.getInstence().resetPassword(config, new StringInfoCallback() {
							
							@Override
							public void onStringDataCallback(String s, boolean isSuccess) {
								if (isSuccess) {
									Intent i = new Intent(
											SettingPasswordActivity.this,
											LoginActivity.class);
									startActivity(i);
								}else {
									Toast.makeText(SettingPasswordActivity.this, s, Toast.LENGTH_SHORT).show();
								}
							}
						});
					}
					
				}else{ //正常的注册的情况
					if (!mPasswordCheckStr.equals(mPasswordStr)) {
						Toast.makeText(SettingPasswordActivity.this,
								"请输入相同的密码", Toast.LENGTH_SHORT).show();
					} else {
						MobclickAgent.onEvent(SettingPasswordActivity.this, UmenAnalytics.REGISTER);
						ArticleManager.getInstence().getRegisterResult(
								mPhoneNum, mCode, mPasswordCheckStr,new UserInfoCallback() {
									
									@Override
									public void onUserinfoCallback(UserInfo info, String failMsg) {
										if (failMsg != null) {
											Toast.makeText(SettingPasswordActivity.this, failMsg,
													Toast.LENGTH_SHORT).show();
										}else {
											if (info != null) {
												SharedPreferences.Editor mEditor = configShare
														.edit();
												UserConfigUtile
														.storeSelfConfigToNative(
																mEditor, info);
											}
											Intent i = new Intent(
													SettingPasswordActivity.this,
													SelfCenterActivity.class);
											startActivity(i);
										}
									}
								});
					}
				}
			}
		});
	}

	/**
	 * 根据输入状况显示完成按钮的样式
	 */
	private void changeCompleteButtonState() {
		if (passwordActivityType == SET_PASSWORD || passwordActivityType == FORGET_PASSWORD) { // 设置密码

			if (mPasswordStr == null || mPasswordCheckStr == null
					|| mPasswordStr.length() < 1
					|| mPasswordCheckStr.length() < 1) {
				mCompleteButton
						.setBackgroundResource(R.drawable.login_button_normal);
				mCompleteButton.setTextColor(SettingPasswordActivity.this
						.getResources().getColor(R.color.video_share_txt));
				mCompleteButton.setEnabled(false);
			} else {
				mCompleteButton
						.setBackgroundResource(R.drawable.login_button_click);
				mCompleteButton.setTextColor(SettingPasswordActivity.this
						.getResources().getColor(R.color.white));
				mCompleteButton.setEnabled(true);
			}
		} else if (passwordActivityType == RESET_PASSWORD) {
			if (mPasswordStr == null || mPasswordCheckStr == null
					|| mPasswordThirdStr == null || mPasswordStr.length() < 1
					|| mPasswordCheckStr.length() < 1
					|| mPasswordThirdStr.length() < 1) {
				mCompleteButton
						.setBackgroundResource(R.drawable.login_button_normal);
				mCompleteButton.setTextColor(SettingPasswordActivity.this
						.getResources().getColor(R.color.video_share_txt));
				mCompleteButton.setEnabled(false);
			} else {
				mCompleteButton
						.setBackgroundResource(R.drawable.login_button_click);
				mCompleteButton.setTextColor(SettingPasswordActivity.this
						.getResources().getColor(R.color.white));
				mCompleteButton.setEnabled(true);

			}
		}
	}

	@Override
	public void onClick(View v) {

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
