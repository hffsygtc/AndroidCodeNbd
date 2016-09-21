package cn.com.nbd.nbdmobile.activity;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.utility.UserConfigUtile;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;
import cn.com.nbd.nbdmobile.wxapi.WXEntryActivity;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.nbd.article.article.LoginType;
import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.LoginConfig;
import com.nbd.article.bean.UserInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.UserInfoCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

public class LoginActivity extends Activity implements OnClickListener,
		PlatformActionListener {

	private ImageView mBackBtn; // 返回按钮

	private EditText mUserIdEdit; // 用户名输入框
	private EditText mPasswordEdit; // 密码输入框
	private Button mLoginButton; // 登陆按钮

	private TextView mForgetBtn; // 忘记密码按钮

	private RelativeLayout mRegisterLayout; // 注册的入口

	private RelativeLayout mWeiboLoginLayout; // 微博登陆按钮
	private RelativeLayout mQqLoginLayout; // QQ登录按钮
	private RelativeLayout mWeixinLayout; // 微信按钮

	private String mUserNameStr;
	private String mPasswordStr;
	private String mIds; // 登录时同步登录的文章
	private String mAccessToken;

	private SharedPreferences mConfigShare;
	private SharedPreferences mNativeShare;
	private boolean isDayTheme;
	private LoadingDialog mLoadingDialog;

	public final static int THIRD_LOGIN_CANCLE = 0;
	public final static int THIRD_LOGIN_OK = 1;
	public final static int THIRD_LOGIN_ERROR = 2;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case THIRD_LOGIN_OK:
				Platform platform = (Platform) msg.obj;
				if (platform != null) {
					thirdLogin(platform);
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
		setContentView(R.layout.activity_login);

		mLoadingDialog = new LoadingDialog(LoginActivity.this,
				R.style.loading_dialog, "登录中...");

		initUi();

		initData();
		setListener();

	}

	/**
	 * 第三方登录
	 * 
	 * @param platform
	 */
	private void thirdLogin(Platform platform) {
		LoginConfig config = new LoginConfig();
		config.setToken(platform.getDb().getToken());
		config.setOpenId(platform.getDb().getUserId());
		if (mLoadingDialog != null) {
			mLoadingDialog.showFullDialog();
		}
		// 新浪微博登陆的返回
		if (platform.getName().equals("SinaWeibo")) {

			ArticleManager.getInstence().Login(config, LoginType.WEIBO,
					new UserInfoCallback() {

						@Override
						public void onUserinfoCallback(UserInfo info,
								String failMsg) {
							// if (mLoadingDialog != null) {
							// mLoadingDialog.dismiss();
							// }

							if (info != null) {
								SharedPreferences.Editor mEditor = mConfigShare
										.edit();
								UserConfigUtile.storeSelfConfigToNative(
										mEditor, info);

								mAccessToken = info.getAccess_token();
								synchroCollections();
								Log.e("LOGIN", "SINA RETUREN OK");
								// Intent i = new Intent();
								// Bundle b = new Bundle();
								// LoginActivity.this.setResult(1, i);
								// LoginActivity.this.finish();
							} else if (failMsg != null) {
								if (mLoadingDialog != null) {
									mLoadingDialog.dismiss();
								}
								Toast.makeText(LoginActivity.this, failMsg,
										Toast.LENGTH_SHORT).show();
							} else {
								if (mLoadingDialog != null) {
									mLoadingDialog.dismiss();
								}
								Toast.makeText(LoginActivity.this, "登录失败",
										Toast.LENGTH_SHORT).show();

							}
						}
					});
		} else if (platform.getName().equals("QQ")) {

			ArticleManager.getInstence().Login(config, LoginType.QQ,
					new UserInfoCallback() {

						@Override
						public void onUserinfoCallback(UserInfo info,
								String failMsg) {
							// if (mLoadingDialog != null) {
							// mLoadingDialog.dismiss();
							// }
							if (info != null) {
								SharedPreferences.Editor mEditor = mConfigShare
										.edit();
								UserConfigUtile.storeSelfConfigToNative(
										mEditor, info);

								mAccessToken = info.getAccess_token();
								synchroCollections();
								// Intent i = new Intent();
								// Bundle b = new Bundle();
								// LoginActivity.this.setResult(1, i);
								// LoginActivity.this.finish();
							} else if (failMsg != null) {
								if (mLoadingDialog != null) {
									mLoadingDialog.dismiss();
								}
								Toast.makeText(LoginActivity.this, failMsg,
										Toast.LENGTH_SHORT).show();
							} else {
								if (mLoadingDialog != null) {
									mLoadingDialog.dismiss();
								}
								Toast.makeText(LoginActivity.this, "登录失败",
										Toast.LENGTH_SHORT).show();

							}
						}
					});
		} else if (platform.getName().equals("Wechat")) {
			ArticleManager.getInstence().Login(config, LoginType.WEIXIN,
					new UserInfoCallback() {

						@Override
						public void onUserinfoCallback(UserInfo info,
								String failMsg) {
							// if (mLoadingDialog != null) {
							// mLoadingDialog.dismiss();
							// }
							if (info != null) {
								SharedPreferences.Editor mEditor = mConfigShare
										.edit();
								UserConfigUtile.storeSelfConfigToNative(
										mEditor, info);

								mAccessToken = info.getAccess_token();
								synchroCollections();
								// Intent i = new Intent();
								// Bundle b = new Bundle();
								// LoginActivity.this.setResult(1, i);
								// LoginActivity.this.finish();
							} else if (failMsg != null) {
								if (mLoadingDialog != null) {
									mLoadingDialog.dismiss();
								}
								Toast.makeText(LoginActivity.this, failMsg,
										Toast.LENGTH_SHORT).show();
							} else {
								if (mLoadingDialog != null) {
									mLoadingDialog.dismiss();
								}
								Toast.makeText(LoginActivity.this, "登录失败",
										Toast.LENGTH_SHORT).show();

							}
						}
					});
		}

	}

	private void initData() {

		mConfigShare = this
				.getSharedPreferences("AppConfig", this.MODE_PRIVATE);

	}

	private void initUi() {

		mBackBtn = (ImageView) findViewById(R.id.login_back_btn);
		mUserIdEdit = (EditText) findViewById(R.id.login_user_edittext);
		mPasswordEdit = (EditText) findViewById(R.id.login_password_edittext);
		mLoginButton = (Button) findViewById(R.id.login_button);

		mForgetBtn = (TextView) findViewById(R.id.login_forget_textview);

		mRegisterLayout = (RelativeLayout) findViewById(R.id.login_register_layout);

		mWeixinLayout = (RelativeLayout) findViewById(R.id.login_other_weixin_layout);
		mWeiboLoginLayout = (RelativeLayout) findViewById(R.id.login_other_weibo_layout);
		mQqLoginLayout = (RelativeLayout) findViewById(R.id.login_other_qq_layout);

		mLoginButton.setEnabled(false);
	}

	private void setListener() {
		/**
		 * 返回
		 */
		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LoginActivity.this.finish();
			}
		});

		/**
		 * 注册
		 */
		mRegisterLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(LoginActivity.this,
						RegisterActivity.class);
				// codeActivityType = i.getIntExtra("type", -1);
				i.putExtra("type", 0);
				startActivity(i);
			}
		});

		/**
		 * 忘记密码
		 */
		mForgetBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(LoginActivity.this,
						RegisterActivity.class);
				// codeActivityType = i.getIntExtra("type", -1);
				i.putExtra("type", 1);
				startActivity(i);
			}
		});

		/**
		 * 微博登陆
		 */
		mWeiboLoginLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MobclickAgent.onEvent(LoginActivity.this,
						UmenAnalytics.THIRD_LOGIN);
				ShareSDK.initSDK(LoginActivity.this);

				Platform weibo = ShareSDK.getPlatform(LoginActivity.this,
						SinaWeibo.NAME);
				// C8998

				weibo.setPlatformActionListener(LoginActivity.this);

				weibo.SSOSetting(false);

				weibo.authorize();

			}
		});

		/**
		 * QQ登陆
		 */
		mQqLoginLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MobclickAgent.onEvent(LoginActivity.this,
						UmenAnalytics.THIRD_LOGIN);
				ShareSDK.initSDK(LoginActivity.this);

				Platform qq = ShareSDK.getPlatform(LoginActivity.this, QQ.NAME);

				qq.removeAccount();

				Log.e("jbjkbjk", "==" + qq.getDb().getToken());
				qq.setPlatformActionListener(LoginActivity.this);

				qq.SSOSetting(false);

				qq.authorize();
				// qq.showUser(null);

			}
		});

		/**
		 * 微信登录
		 */
		mWeixinLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MobclickAgent.onEvent(LoginActivity.this,
						UmenAnalytics.THIRD_LOGIN);
				ShareSDK.initSDK(LoginActivity.this);

				Platform weixin = ShareSDK.getPlatform(LoginActivity.this,
						Wechat.NAME);

				// weixin.removeAccount();

				Log.e("WEIXIN", "==" + weixin.getDb().getToken());
				weixin.setPlatformActionListener(LoginActivity.this);

				weixin.SSOSetting(false);

				weixin.authorize();

				// qq.showUser(null);

			}
		});

		/**
		 * 用户名输入框
		 */
		mUserIdEdit.addTextChangedListener(new TextWatcher() {

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
				mUserNameStr = s.toString();
				changLoginBtnState();

			}
		});

		/**
		 * 密码输入框
		 */
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
				changLoginBtnState();
			}
		});

		/**
		 * 登陆按钮
		 */

		mLoginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MobclickAgent.onEvent(LoginActivity.this, UmenAnalytics.LOGIN);
				LoginConfig config = new LoginConfig();
				config.setUserId(mUserNameStr);
				config.setPassword(mPasswordStr);
				if (mLoadingDialog != null) {
					mLoadingDialog.showFullDialog();
				}

				ArticleManager.getInstence().Login(config, LoginType.PHONE,
						new UserInfoCallback() {

							@Override
							public void onUserinfoCallback(UserInfo info,
									String failMsg) {
								// if (mLoadingDialog != null) {
								// mLoadingDialog.dismiss();
								// }

								if (info != null) {
									SharedPreferences.Editor mEditor = mConfigShare
											.edit();
									UserConfigUtile.storeSelfConfigToNative(
											mEditor, info);

									mAccessToken = info.getAccess_token();
									synchroCollections();

									// Intent i = new Intent();
									// Bundle b = new Bundle();
									// LoginActivity.this.setResult(1, i);
									// LoginActivity.this.finish();
								} else if (failMsg != null) {
									if (mLoadingDialog != null) {
										mLoadingDialog.dismiss();
									}
									Toast.makeText(LoginActivity.this, failMsg,
											Toast.LENGTH_SHORT).show();
								} else {
									if (mLoadingDialog != null) {
										mLoadingDialog.dismiss();
									}
									Toast.makeText(LoginActivity.this, "登录失败",
											Toast.LENGTH_SHORT).show();
								}

							}
						});

			}
		});
		// mLoginButton.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {

	}

	/**
	 * 根据输入的内容改变按钮的状态
	 */
	private void changLoginBtnState() {

		if (mUserNameStr == null || mPasswordStr == null
				|| mUserNameStr.length() < 1 || mPasswordStr.length() < 1) {
			mLoginButton.setBackgroundResource(R.drawable.login_button_normal);
			mLoginButton.setTextColor(LoginActivity.this.getResources()
					.getColor(R.color.video_share_txt));
			mLoginButton.setEnabled(false);
		} else {
			mLoginButton.setBackgroundResource(R.drawable.login_button_click);
			mLoginButton.setTextColor(LoginActivity.this.getResources()
					.getColor(R.color.white));
			mLoginButton.setEnabled(true);
		}

	}

	@Override
	public void onClick(View v) {

	}

	/**
	 * 授权登陆的回调
	 */
	@Override
	public void onCancel(Platform platform, int arg1) {
		if (platform.getName().equals("SinaWeibo")) {
			Toast.makeText(LoginActivity.this, "取消微博登录", Toast.LENGTH_SHORT)
					.show();
		} else if (platform.getName().equals("QQ")) {
			Toast.makeText(LoginActivity.this, "取消QQ登录", Toast.LENGTH_SHORT)
					.show();
		} else if (platform.getName().equals("Wechat")) {
			Toast.makeText(LoginActivity.this, "取消微信登录", Toast.LENGTH_SHORT)
					.show();
		}

	}

	@Override
	public void onComplete(Platform platform, int arg1,
			HashMap<String, Object> arg2) {
		Log.e("THIRD_LOGIN==>", "complete" + arg1 + "MAP==" + arg2 + platform
				+ "=TOKEN=" + platform.getDb().getToken() + "=USERID="
				+ platform.getDb().getUserId() + "=ID=" + platform.getId()
				+ "=NAME=" + platform.getName() + "==" + platform.getSortId()
				+ "==" + platform.getDb().get("openid") + "=="
				+ platform.getDb().getTokenSecret());

		Message msg = new Message();
		msg.what = THIRD_LOGIN_OK;
		msg.obj = platform;
		handler.sendMessage(msg);
		// WEIBO
		// 06-01 10:38:35.095: E/THIRD_LOGIN==>(16975):
		// complete1nullcn.sharesdk.sina.weibo.SinaWeibo@f0221f4=TOKEN=2.00O1rdmC0H7CVc4b00a84523xEYrmC=USERID=2551029570=ID=1=NAME=SinaWeibo==1==
		// 06-01 10:40:33.541: E/THIRD_LOGIN==>(6541):
		// complete1nullcn.sharesdk.sina.weibo.SinaWeibo@b0e0223=TOKEN=2.00O1rdmC0H7CVc4b00a84523xEYrmC=USERID=2551029570=ID=1=NAME=SinaWeibo==1====

		// QQ
		// complete1nullcn.sharesdk.tencent.qq.QQ@7e5a8d4=TOKEN=FB6EF78C87C86F3E1B2F015F5953B102=USERID=DA707EF9DE63AD5C0CFDC9217B509A6A=ID=7=NAME=QQ==7====
		// complete1nullcn.sharesdk.tencent.qq.QQ@3f8022c=TOKEN=FB6EF78C87C86F3E1B2F015F5953B102=USERID=DA707EF9DE63AD5C0CFDC9217B509A6A=ID=7=NAME=QQ==5====

	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		// TODO Auto-generated method stub
		Log.e("THIRD_LOGIN==>", "ERROR" + arg2.getMessage());
		arg0.removeAccount();
		// handler.obtainMessage(THIRD_LOGIN_ERROR).sendToTarget();

	}

	/**
	 * 登录时同步本地收藏文章
	 */
	public void synchroCollections() {

		mIds = ArticleManager.getInstence().getLocalCollections();
		Log.e("LOGIN AYNCHRO", "==>" + mIds);
		ArticleConfig synchroConfig = new ArticleConfig();
		synchroConfig.setType(RequestType.COLLECTION);
		synchroConfig.setAccessToken(mAccessToken);
		synchroConfig.setCollection(true);
		synchroConfig.setCustomString(mIds);
		ArticleManager.getInstence().setArticleCollection(synchroConfig,
				new UserInfoCallback() {

					@Override
					public void onUserinfoCallback(UserInfo info, String failMsg) {
						if (mLoadingDialog != null) {
							mLoadingDialog.dismiss();
						}

						if (info != null) {
							SharedPreferences.Editor mEditor = mConfigShare
									.edit();
							UserConfigUtile.storeSelfConfigToNative(mEditor,
									info);

							Intent i = new Intent();
							Bundle b = new Bundle();
							LoginActivity.this.setResult(1, i);
							LoginActivity.this.finish();
						} else {
							Intent i = new Intent();
							Bundle b = new Bundle();
							LoginActivity.this.setResult(1, i);
							LoginActivity.this.finish();
						}
					}
				});
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
