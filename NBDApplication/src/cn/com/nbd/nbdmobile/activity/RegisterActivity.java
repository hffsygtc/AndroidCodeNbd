package cn.com.nbd.nbdmobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.nbd.nbdmobile.R;

import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.StringInfoCallback;
import com.umeng.analytics.MobclickAgent;

/**
 * 用手机号注册页面
 * 
 * 输入手机号码获取验证码，将验证码保存验证
 * 
 * @author riche
 * 
 */
public class RegisterActivity extends Activity implements OnClickListener {

	private ImageView mBackBtn; // 返回按钮
	private EditText mPhoneEdit; // 手机号码输入框
	private EditText mCodeEditl; // 验证码输入框
	private TextView mGetCodeBtn; // 获取验证码按钮
	private CheckBox mAgreeCheckBox; // 同意条款勾选框
	private Button mNextButton; // 下一步按钮

	private TextView mTitleText; // 页面的标题栏
	private RelativeLayout mAgreeLayout; // 同意选中框布局
	private TextView mRedTips;

	private String mPhoneStr;
	private String mCodeStr;

	private String mPhoneIsCoded; // 获取验证码的手机号码

	private String mCodeFromNet;

	private int codeActivityType; // 页面类型

	private static int REGISTER_PAGE = 0; // 注册时的界面
	private static int FORGET_PAGE = 1;// 忘记密码时的界面
	private SharedPreferences mNativeShare;
	private boolean isDayTheme;
	private boolean isChangeCodeBg;
	
	private TimeCount mTimeCount;

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
		
		setContentView(R.layout.activity_register_user);

		Intent i = getIntent();

		codeActivityType = i.getIntExtra("type", -1);
		
		mTimeCount = new TimeCount(60000, 1000);

		initUi();

		setListener();

	}

	private void initUi() {
		mBackBtn = (ImageView) findViewById(R.id.register_back_btn);
		mPhoneEdit = (EditText) findViewById(R.id.register_user_edittext);
		mCodeEditl = (EditText) findViewById(R.id.register_password_edittext);
		mGetCodeBtn = (TextView) findViewById(R.id.register_getphone_num_img);
		mAgreeCheckBox = (CheckBox) findViewById(R.id.register_agree_checkbox);
		mNextButton = (Button) findViewById(R.id.register_button);
		mRedTips = (TextView) findViewById(R.id.register_red_tips);

		mTitleText = (TextView) findViewById(R.id.register_title_text);
		mAgreeLayout = (RelativeLayout) findViewById(R.id.register_agree_tips_layout);

		mNextButton.setEnabled(false);
		
		if (codeActivityType == REGISTER_PAGE) { // 注册时的页面
			mTitleText.setText("注册账号");
			mAgreeLayout.setVisibility(View.VISIBLE);

			mAgreeCheckBox.setChecked(true);
		} else if (codeActivityType == FORGET_PAGE) { // 忘记密码时的页面
			mTitleText.setText("忘记密码");
			mAgreeLayout.setVisibility(View.GONE);

		}

	}

	private void setListener() {

		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RegisterActivity.this.finish();
			}
		});

		mPhoneEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				Log.e("EDIT_TEXT_TOP", s + "==" + count);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// if (mPhoneStr!=null) {
				// if (11-mPhoneStr.length() < count) {
				// Toast.makeText(RegisterActivity.this,
				// "请输入11位手机号码",Toast.LENGTH_SHORT).show();
				// }
				// }else {
				// if (count>11) {
				// Toast.makeText(RegisterActivity.this,
				// "请输入11位手机号码",Toast.LENGTH_SHORT).show();
				// }
				// }
			}

			@Override
			public void afterTextChanged(Editable s) {
				mPhoneStr = s.toString();
				changeNextBtnState();
				Log.e("EDIT_TEXT_TOP", s + "==");
			}
		});

		mCodeEditl.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				Log.e("EDIT_TEXT_bottom", s + "==" + count);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				mCodeStr = s.toString();
				changeNextBtnState();

			}
		});

		/**
		 * 获取验证码的按钮
		 */
		mGetCodeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mPhoneStr == null || mPhoneStr.length() != 11) {
					Toast.makeText(RegisterActivity.this, "请输入正确的手机号码",
							Toast.LENGTH_SHORT).show();
				} else {
					
					if (mTimeCount == null) {
						mTimeCount = new TimeCount(60000, 1000);
					}
					mTimeCount.start();

					mPhoneIsCoded = mPhoneStr;
					if (codeActivityType == FORGET_PAGE) { //忘记密码的页面
						ArticleManager.getInstence().getPhoneCode(mPhoneStr,
								false, new StringInfoCallback() {

									@Override
									public void onStringDataCallback(String s,
											boolean isSuccess) {
										mCodeFromNet = s;

									}
								});
					} else {  //注册页面
						ArticleManager.getInstence().getPhoneCode(mPhoneStr,
								true, new StringInfoCallback() {

									@Override
									public void onStringDataCallback(String s,
											boolean isSuccess) {
										if (isSuccess) {
											mCodeFromNet = s;
										}else {
											if (s != null) {
												Toast.makeText(RegisterActivity.this, s, Toast.LENGTH_SHORT).show();
											}
										}

									}
								});
					}
				}
			}
		});

		mAgreeCheckBox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						changeNextBtnState();

					}
				});
		
		mRedTips.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(RegisterActivity.this,
						AdvertiseWebviewActivity.class);
				Bundle b = new Bundle();
				b.putString("link", "http://www.nbd.com.cn/news_privacy");
				b.putString("title", "服务条款");
				i.putExtra("urlbundle", b);
				startActivity(i);
				
			}
		});

		mNextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (!mPhoneStr.equals(mPhoneIsCoded)) {
					Toast.makeText(RegisterActivity.this, "请获取对应手机的验证码 ",
							Toast.LENGTH_SHORT).show();
				}

				 else if (mCodeStr.equals(mCodeFromNet)) {
//				else if (mCodeStr.equals("123456")) {
					if (codeActivityType == FORGET_PAGE) {
						Intent i = new Intent(RegisterActivity.this,
								SettingPasswordActivity.class);
						i.putExtra("phone", mPhoneStr);
						i.putExtra("code", mCodeStr);
						i.putExtra("type", 2);
						startActivity(i);
					} else {
						Intent i = new Intent(RegisterActivity.this,
								SettingPasswordActivity.class);
						i.putExtra("phone", mPhoneStr);
						i.putExtra("code", mCodeStr);
						i.putExtra("type", 0);
						startActivity(i);
					}

				} else {
					Toast.makeText(RegisterActivity.this, "请输入正确的验证码",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	private void changeNextBtnState() {
		if (codeActivityType == FORGET_PAGE) { // 忘记密码
			if (mPhoneStr == null || mCodeStr == null || mPhoneStr.length() < 1
					|| mCodeStr.length() < 1) {
				mNextButton
						.setBackgroundResource(R.drawable.login_button_normal);
				mNextButton.setTextColor(RegisterActivity.this.getResources()
						.getColor(R.color.video_share_txt));
				mNextButton.setEnabled(false);
			} else {
				mNextButton
						.setBackgroundResource(R.drawable.login_button_click);
				mNextButton.setTextColor(RegisterActivity.this.getResources()
						.getColor(R.color.white));
				mNextButton.setEnabled(true);
			}
		} else {
			if (!mAgreeCheckBox.isChecked() || mPhoneStr == null
					|| mCodeStr == null || mPhoneStr.length() < 1
					|| mCodeStr.length() < 1) {
				mNextButton
						.setBackgroundResource(R.drawable.login_button_normal);
				mNextButton.setTextColor(RegisterActivity.this.getResources()
						.getColor(R.color.video_share_txt));
				mNextButton.setEnabled(false);
			} else {
				mNextButton
						.setBackgroundResource(R.drawable.login_button_click);
				mNextButton.setTextColor(RegisterActivity.this.getResources()
						.getColor(R.color.white));
				mNextButton.setEnabled(true);
			}
		}

	}

	@Override
	public void onClick(View v) {

	}
	
	
	class TimeCount extends CountDownTimer{

		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (!isChangeCodeBg) {
				mGetCodeBtn.setBackgroundResource(R.drawable.register_code_grey);
				mGetCodeBtn.setEnabled(false);
				isChangeCodeBg = true;
			}
			mGetCodeBtn.setText(millisUntilFinished/1000+"秒");
		}

		@Override
		public void onFinish() {
			mGetCodeBtn.setBackgroundResource(R.drawable.register_code_red);
			mGetCodeBtn.setEnabled(true);
			mGetCodeBtn.setText("重新获取");
			isChangeCodeBg = false;
		}
		
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
