package cn.com.nbd.nbdmobile.activity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Instrumentation.ActivityResult;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager.OnActivityResultListener;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.R.style;
import cn.com.nbd.nbdmobile.utility.ArticleHandleType;
import cn.com.nbd.nbdmobile.utility.CropImage;
import cn.com.nbd.nbdmobile.utility.FileUtil;
import cn.com.nbd.nbdmobile.utility.Options;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.utility.UserConfigUtile;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;
import cn.com.nbd.nbdmobile.widget.NbdAlrltDialog;
import cn.com.nbd.nbdmobile.widget.NbdAlrltDialog.onDialogChooseClick;
import cn.com.nbd.nbdmobile.widget.UploadHeadDialog;
import cn.com.nbd.nbdmobile.widget.UploadHeadDialog.onDialogBtnClick;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.google.gson.reflect.TypeToken;
import com.nbd.article.article.LoginType;
import com.nbd.article.bean.LoginConfig;
import com.nbd.article.bean.UserInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.UserInfoCallback;
import com.nbd.article.utility.JsonUtil;
import com.nbd.network.utility.Cst;
import com.nbd.network.utility.NetUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class SelfSettingActivity extends Activity implements OnClickListener, PlatformActionListener {

	private ImageView mBackBtn; // 返回按钮
	private ImageView mHeadImgView; // 个人头像
	private RelativeLayout mHeadLayout;

	private RelativeLayout mNameLayout; // 昵称模块
	private TextView mNameText; // 昵称名字

	private RelativeLayout mPasswordChangeLayout; // 修改密码的模块

	private RelativeLayout mSinaLayout; // 新浪绑定条
	private TextView mSinaText;
	private RelativeLayout mWeixinLayout; // 微信绑定条
	private TextView mWeixinText;
	private RelativeLayout mQQLayout; // QQ绑定条
	private TextView mQQText;

	private RelativeLayout mExitLayout; // 退出按钮
	private SharedPreferences configSp;

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	DisplayImageOptions options;

	private String urlpath;
	private String resultStr;

	private String mHeadImgString;
	private String mNickNameString;
	private String mAccessToken;

	private UploadHeadDialog mDialog;

	private Activity mActivity;

	private CropImage cropImage = null;
	private ImageView imageView = null;
	
	private UserInfo mUserInfo;

	private SharedPreferences mConfigShare; // 获取配置的个人信息
	private SharedPreferences.Editor mEditor;
	private SharedPreferences mNativeShare; // 获取配置的个人信息
	private boolean isDayTheme;

	private boolean isChangeHead;

	private LoadingDialog mLoadingDialog;
	
	public final static int THIRD_BAND = 0;
	public final static int HEAD_CHANGE = 1;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HEAD_CHANGE:
				if (mLoadingDialog != null) {
					mLoadingDialog.dismiss();
				}
				imageLoader.displayImage(mHeadImgString, mHeadImgView, options);
				break;
			case THIRD_BAND:
				Platform platform = (Platform) msg.obj;
				if (platform != null) {
					thirdBlind(platform);
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
		setContentView(R.layout.activity_setting_self_info);

		mActivity = this;

		Intent i = getIntent();
		Bundle b = i.getBundleExtra("self");
		mHeadImgString = b.getString("headImg");
		mNickNameString = b.getString("nickName");
		mAccessToken = b.getString("token");
		options = Options.getHeadOptions(this);

		mLoadingDialog = new LoadingDialog(this, style.loading_dialog, "处理中...");

		// 本地配置，用户相关信息的share
		mConfigShare = this
				.getSharedPreferences("AppConfig", this.MODE_PRIVATE);
		mEditor = mConfigShare.edit();

		mUserInfo = UserConfigUtile.getUserinfoFormNative(mConfigShare);

		cropImage = new CropImage(this);

		initUi();
		setListener();

	}

	private void initUi() {
		mBackBtn = (ImageView) findViewById(R.id.self_setting_back_btn);
		mHeadImgView = (ImageView) findViewById(R.id.self_setting_head_img);

		mNameLayout = (RelativeLayout) findViewById(R.id.self_setting_name_layout);
		mNameText = (TextView) findViewById(R.id.self_setting_name_text);

		mPasswordChangeLayout = (RelativeLayout) findViewById(R.id.self_setting_password_layout);
		mExitLayout = (RelativeLayout) findViewById(R.id.self_setting_exit_layout);

		mHeadLayout = (RelativeLayout) findViewById(R.id.self_setting_head_layout);
		
		mSinaLayout = (RelativeLayout) findViewById(R.id.self_setting_sina_layout);
		mSinaText = (TextView) findViewById(R.id.self_setting_sina_text);
		mQQLayout = (RelativeLayout) findViewById(R.id.self_setting_qq_layout);
		mQQText = (TextView) findViewById(R.id.self_setting_qq_text);
		mWeixinLayout = (RelativeLayout) findViewById(R.id.self_setting_weixin_layout);
		mWeixinText = (TextView) findViewById(R.id.self_setting_weixin_text);
		

		imageLoader.displayImage(mHeadImgString, mHeadImgView, options);
		mNameText.setText(mNickNameString);
		
		if (mUserInfo != null) {
			if (mUserInfo.getQqName() != null) {
				mQQLayout.setEnabled(false);
				mQQText.setText(mUserInfo.getQqName());
			}else {
				mQQLayout.setEnabled(true);
			}
			if (mUserInfo.getWeiboName() != null) {
				mSinaLayout.setEnabled(false);
				mSinaText.setText(mUserInfo.getWeiboName());
			}else {
				mSinaLayout.setEnabled(true);
			}
			if (mUserInfo.getWeixinName() != null) {
				mWeixinLayout.setEnabled(false);
				mWeixinText.setText(mUserInfo.getWeixinName());
			}else {
				mWeixinLayout.setEnabled(true);
			}
		}

	}

	private void setListener() {

		// 返回按钮
		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isChangeHead) {
					SelfSettingActivity.this.setResult(2);
				}
				SelfSettingActivity.this.finish();
			}
		});

		// 退出按钮
		mExitLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				NbdAlrltDialog mDialog = new NbdAlrltDialog(SelfSettingActivity.this, R.style.loading_dialog, "确认退出？");
				mDialog.setOnBtnClickListener(new onDialogChooseClick() {
					
					@Override
					public void onModeTypeClick(ArticleHandleType type) {
						if (type == ArticleHandleType.OK) {
							configSp = SelfSettingActivity.this.getSharedPreferences(
									"AppConfig", Context.MODE_PRIVATE);
							SharedPreferences.Editor editor1 = configSp.edit();
							editor1.clear();
							editor1.commit();
							
							SelfSettingActivity.this.setResult(1);
							SelfSettingActivity.this.finish();
						}
					}
				});
				
				mDialog.showFullDialog();
			}
		});

		mHeadLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mDialog = new UploadHeadDialog(mActivity, R.style.headdialog);
				mDialog.setOnBtnClickListener(new onDialogBtnClick() {

					@Override
					public void onChoosePhotoFrom(int type) {
						switch (type) {
						case 0: // 取消
							mDialog.dismiss();
							break;
						case 1: // 相册
							cropImage.openAlbums();
							break;
						case 2: // 相机
							cropImage.openCamera();
							break;

						default:
							break;
						}

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

		mNameLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(SelfSettingActivity.this,
						EditNickNameActivity.class);
				intent.putExtra("name", mNickNameString);
				startActivityForResult(intent, 8);

			}
		});

		mPasswordChangeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(SelfSettingActivity.this,
						SettingPasswordActivity.class);
				i.putExtra("phone", "");
				i.putExtra("code", "");
				i.putExtra("type", 1);
				startActivity(i);

			}
		});
		
		/**
		 * 微博登陆
		 */
		mSinaLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ShareSDK.initSDK(SelfSettingActivity.this);

				Platform weibo = ShareSDK.getPlatform(SelfSettingActivity.this,
						SinaWeibo.NAME);
				// C8998

				weibo.setPlatformActionListener(SelfSettingActivity.this);

				weibo.SSOSetting(false);

				weibo.authorize();

			}
		});

		/**
		 * QQ登陆
		 */
		mQQLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				ShareSDK.initSDK(SelfSettingActivity.this);

				Platform qq = ShareSDK.getPlatform(SelfSettingActivity.this, QQ.NAME);

				qq.removeAccount();

				Log.e("jbjkbjk", "==" + qq.getDb().getToken());
				qq.setPlatformActionListener(SelfSettingActivity.this);

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

				ShareSDK.initSDK(SelfSettingActivity.this);

				Platform weixin = ShareSDK.getPlatform(SelfSettingActivity.this,
						Wechat.NAME);

				// weixin.removeAccount();

				Log.e("WEIXIN", "==" + weixin.getDb().getToken());
				// weixin.setPlatformActionListener(LoginActivity.this);

				weixin.SSOSetting(false);

				weixin.authorize();
				// qq.showUser(null);

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 8) {
			String newName = data.getStringExtra("name");
			if (newName != null && !newName.equals("")) {
				mNameText.setText(newName);
			}

		} else {
			Bitmap bitmap = cropImage.onResult(requestCode, resultCode, data);
			if (bitmap != null) {
				Log.e("IMAGE-RETUREN",
						bitmap.getWidth() + "___" + bitmap.getHeight());
				urlpath = FileUtil.saveFile(SelfSettingActivity.this,
						"temphead.jpg", bitmap);

				if (mDialog != null) {
					mDialog.dismiss();
				}
				if (mLoadingDialog != null) {
					mLoadingDialog.showFullDialog();
				}

				new Thread(uploadImageRunnable).start();
			}
		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	Runnable uploadImageRunnable = new Runnable() {
		@Override
		public void run() {

			Map<String, String> textParams = new HashMap<String, String>();
			Map<String, File> fileparams = new HashMap<String, File>();

			try {
				// 创建一个URL对象
				URL url = new URL(Cst.UPLOAD_HEAD);
				textParams = new HashMap<String, String>();
				fileparams = new HashMap<String, File>();
				textParams.put("access_token", mAccessToken);
				textParams.put("app_key", "ae1bd0a8b32505a86c0b20187f5093ec");
				// 要上传的图片对象
				// Bitmap map = BitmapFactory.decodeByteArray("", 0, 0);
				File file = new File(urlpath);
				fileparams.put("avatar", file);
				// 利用HttpURLConnection对象从网络中获取网页数据
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				// 设置连接超时（记得设置连接超时,如果网络不好,Android系统在超过默认时间会收回资源中断操作）
				conn.setConnectTimeout(5000);
				// 设置允许输出（发送POST请求必须设置允许输出）
				conn.setDoOutput(true);
				// 设置使用POST的方式发送
				conn.setRequestMethod("POST");
				// 设置不使用缓存（容易出现问题）
				conn.setUseCaches(false);
				// 在开始用HttpURLConnection对象的setRequestProperty()设置,就是生成HTML文件头
				conn.setRequestProperty("ser-Agent", "Fiddler");
				// 设置contentType
				conn.setRequestProperty("Content-Type",
						"multipart/form-data; boundary=" + NetUtil.BOUNDARY);
				OutputStream os = conn.getOutputStream();
				DataOutputStream ds = new DataOutputStream(os);
				NetUtil.writeStringParams(textParams, ds);
				NetUtil.writeFileParams(fileparams, ds);
				NetUtil.paramsEnd(ds);
				// 对文件流操作完,要记得及时关闭
				os.close();
				// 服务器返回的响应吗
				int code = conn.getResponseCode(); // 从Internet获取网页,发送请求,将网页以流的形式读回来
				// 对响应码进行判断
				if (code == 200) {// 返回的响应码200,是成功
					// 得到网络返回的输入流
					InputStream is = conn.getInputStream();
					resultStr = NetUtil.readString(is);

					JSONObject result = new JSONObject(resultStr);
					if (result.getInt("status_code") == 0) {

					} else {
						JSONObject resultData = new JSONObject();
						resultData = result.getJSONObject("data");

						UserInfo user = JsonUtil.fromJson(
								resultData.toString(),
								new TypeToken<UserInfo>() {
								}.getType());

						UserConfigUtile.storeSelfConfigToNative(mEditor, user);
						Log.e("NEW-HEAD==>", user.getAvatar());

						mHeadImgString = user.getAvatar();
						isChangeHead = true;
						Message msg = new Message();
						msg.what = HEAD_CHANGE;
						handler.sendMessage(msg);

					}

					Log.e("UPLOAD--RETURN", resultStr);
				} else {
					if (mLoadingDialog != null) {
						mLoadingDialog.dismiss();
					}
					Toast.makeText(SelfSettingActivity.this, "上传失败",
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			// handler.sendEmptyMessage(0);// ִ执行耗时的方法之后发送消给handler
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isChangeHead) {
				SelfSettingActivity.this.setResult(2);
			}
			SelfSettingActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCancel(Platform platform, int arg1) {
		if (platform.getName().equals("SinaWeibo")) {
			Toast.makeText(SelfSettingActivity.this, "取消微博绑定", Toast.LENGTH_SHORT)
					.show();
		} else if (platform.getName().equals("QQ")) {
			Toast.makeText(SelfSettingActivity.this, "取消QQ绑定", Toast.LENGTH_SHORT)
					.show();
		} else if (platform.getName().equals("Wechat")) {
			Toast.makeText(SelfSettingActivity.this, "取消微信绑定", Toast.LENGTH_SHORT)
					.show();
		}
		
	}

	@Override
	public void onComplete(Platform platform, int arg1, HashMap<String, Object> arg2) {
		
		if (platform.getDb().getToken() != null && !platform.getDb().getToken().equals("")) {
			Message msg = new Message();
			msg.what = THIRD_BAND;
			msg.obj = platform;
			handler.sendMessage(msg);
		}
		
	}

	@Override
	public void onError(Platform arg0, int arg1, Throwable arg2) {
		Log.e("THIRD_blind==>", "ERROR" + arg2.getMessage());
		arg0.removeAccount();
		
	}
	
	/**
	 * 绑定第三方账号
	 * @param platform
	 */
	private void thirdBlind(Platform platform) {
		LoginConfig config = new LoginConfig();
		config.setToken(platform.getDb().getToken());
		config.setOpenId(platform.getDb().getUserId());
		config.setUserToken(mAccessToken);
		config.setBlindAccount(true);
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
							if (mLoadingDialog != null) {
								mLoadingDialog.dismiss();
							}

							if (info != null) {
								SharedPreferences.Editor mEditor = mConfigShare
										.edit();
								UserConfigUtile.storeSelfConfigToNative(
										mEditor, info);
								mSinaText.setText(info.getWeiboName());
								mSinaLayout.setEnabled(false);

							} else if (failMsg != null) {
								Toast.makeText(SelfSettingActivity.this, failMsg,
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(SelfSettingActivity.this, "绑定失败",
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
							if (mLoadingDialog != null) {
								mLoadingDialog.dismiss();
							}
							if (info != null) {
								SharedPreferences.Editor mEditor = mConfigShare
										.edit();
								UserConfigUtile.storeSelfConfigToNative(
										mEditor, info);
								mQQText.setText(info.getQqName());
								mQQLayout.setEnabled(false);

							} else if (failMsg != null) {
								Toast.makeText(SelfSettingActivity.this, failMsg,
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(SelfSettingActivity.this, "绑定失败",
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
							if (mLoadingDialog != null) {
								mLoadingDialog.dismiss();
							}
							if (info != null) {
								SharedPreferences.Editor mEditor = mConfigShare
										.edit();
								UserConfigUtile.storeSelfConfigToNative(
										mEditor, info);
								mWeixinText.setText(info.getWeixinName());
								mWeixinLayout.setEnabled(false);

							} else if (failMsg != null) {
								Toast.makeText(SelfSettingActivity.this, failMsg,
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(SelfSettingActivity.this, "绑定失败",
										Toast.LENGTH_SHORT).show();

							}
						}
					});
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
