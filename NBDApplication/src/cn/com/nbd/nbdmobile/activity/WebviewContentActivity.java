package cn.com.nbd.nbdmobile.activity;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.R.style;
import cn.com.nbd.nbdmobile.manager.OnThemeChangeListener;
import cn.com.nbd.nbdmobile.manager.ThemeChangeManager;
import cn.com.nbd.nbdmobile.manager.onTextChangeListener;
import cn.com.nbd.nbdmobile.utility.ArticleHandleType;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.utility.UserConfigUtile;
import cn.com.nbd.nbdmobile.webview.NBDJsNative;
import cn.com.nbd.nbdmobile.webview.NBDWebView;
import cn.com.nbd.nbdmobile.webview.NBDWebViewClient;
import cn.com.nbd.nbdmobile.widget.ArticleMoreHandleDialog;
import cn.com.nbd.nbdmobile.widget.ArticleMoreHandleDialog.onDialogBtnClick;
import cn.com.nbd.nbdmobile.widget.CommentEditDialog;
import cn.com.nbd.nbdmobile.widget.CommentEditDialog.onCommentSendInterface;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.UserInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.ArticleInfoCallback;
import com.nbd.article.managercallback.UserInfoCallback;
import com.nbd.network.bean.CommentHandleType;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

@SuppressLint("SetJavaScriptEnabled")
public class WebviewContentActivity extends Activity {

	private NBDWebView mWebview;
	private NBDJsNative mJsNative;
	private WebSettings mWebSettings;

	private TextView mCommentCount;
	private RelativeLayout mMainLayout;
	private RelativeLayout mTitleLayout;
	private LinearLayout mBottomLayout;
	private TextView mCover;
	private TextView mBottomLine;

	private TextView mTitleText;
	private ImageView mBackBtn;
	private EditText mCommentEdit;

	private ImageView mShareImg;

	private RelativeLayout mMoreHandleImg;
	private RelativeLayout mCommentNumLayout;

	private String deatilHtml;
	private String deatilDatabaseHtml;
	private String mActivityTitle;

	private ArticleInfo mArticleInfo;
	private ArticleInfo mDatabaseArticleInfo;
	private String mUpdataAt;

	private long mArticleId;

	private ArticleMoreHandleDialog mDialog;

	private SharedPreferences mConfigSharedPreferences;
	private SharedPreferences.Editor mEditor;
	private SharedPreferences.Editor mNativeEditor;

	private boolean isNight;
	private boolean isStore;
	private int mTextSize;
	private String mAccessToken;

	private LoadingDialog mLoadingDialog;
	private CommentEditDialog mCommentDialog;

	private SharedPreferences mNativeShare;
	private boolean isDayTheme;
	private OnThemeChangeListener mThemeListener;
	private onTextChangeListener mTextListener;

	private GestureDetectorCompat mDetector;

	private boolean isCanComment;
	private boolean isJpushPage;
	private long commentNum;

	public final static int LOAD_DATABASE_COMPLETE = 0;
	public final static int LOAD_INTERNET_COMPLETE = 1;
	public final static int WEBVIEW_FINISH = 2;

	private final static String NIGHT = "theme_night ";
	private final static String SMALL = "fontSize_small";
	private final static String MID = "fontSize_normal";
	private final static String LARGE = "fontSize_large";

	private int distance = 400;
	private int velocity = 200;

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

			if (mJsNative == null) {
				if (mArticleInfo != null) {
					mJsNative = new NBDJsNative(WebviewContentActivity.this,
							mWebview, mArticleInfo);
				} else if (mDatabaseArticleInfo != null) {
					mJsNative = new NBDJsNative(WebviewContentActivity.this,
							mWebview, mDatabaseArticleInfo);
				}
				mWebview.addJavascriptInterface(mJsNative, "nbd");
			}

			switch (msg.what) {
			case LOAD_DATABASE_COMPLETE:
				if (mDatabaseArticleInfo != null) {
					mArticleInfo = mDatabaseArticleInfo;
					mCommentCount.setText(mDatabaseArticleInfo
							.getReview_count() + "");
					mArticleId = mDatabaseArticleInfo.getArticle_id();
					commentNum = mDatabaseArticleInfo.getReview_count();
					if (mArticleInfo.isAllow_review()) {
						mBottomLine.setVisibility(View.VISIBLE);
						mBottomLayout.setVisibility(View.VISIBLE);
					} else {
						mBottomLayout.setVisibility(View.GONE);
						mBottomLine.setVisibility(View.GONE);
					}

					mCommentCount.setText(mArticleInfo.getReview_count() + "");

					isCanComment = mArticleInfo.isAllow_review();
					commentNum = mArticleInfo.getReview_count();
				}
				String showString = dealContentWithSetting(deatilDatabaseHtml);
				mWebview.loadData(showString, "text/html; charset=UTF-8", null);
				break;
			case LOAD_INTERNET_COMPLETE:
				if (mArticleInfo != null) {
					mCommentCount.setText(mArticleInfo.getReview_count() + "");
					mArticleId = mArticleInfo.getArticle_id();
					commentNum = mArticleInfo.getReview_count();

					if (mArticleInfo.isAllow_review()) {
						mBottomLine.setVisibility(View.VISIBLE);
						mBottomLayout.setVisibility(View.VISIBLE);
					} else {
						mBottomLayout.setVisibility(View.GONE);
						mBottomLine.setVisibility(View.GONE);
					}

					mCommentCount.setText(mArticleInfo.getReview_count() + "");

					isCanComment = mArticleInfo.isAllow_review();
					commentNum = mArticleInfo.getReview_count();
				}
				String showNetString = dealContentWithSetting(deatilHtml);
				mWebview.loadData(showNetString, "text/html; charset=UTF-8",
						null);
				break;
			case WEBVIEW_FINISH:
				if (mLoadingDialog != null) {
					mLoadingDialog.dismiss();
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

		setContentView(R.layout.activity_webview_content);

		Intent i = getIntent();
		Bundle b = i.getBundleExtra("urlbundle");
		mActivityTitle = b.getString("title");
		mArticleId = b.getLong("url");
		isCanComment = b.getBoolean("comment");
		commentNum = b.getLong("commentNum");
		isJpushPage = b.getBoolean("Jpush");

		mConfigSharedPreferences = this.getSharedPreferences("AppConfig",
				this.MODE_PRIVATE);
		mEditor = mConfigSharedPreferences.edit();
		mNativeEditor = mNativeShare.edit();
		mTextSize = mNativeShare.getInt("ArticleTextSize", 1);
		mAccessToken = mConfigSharedPreferences.getString("accessToken", null);

		mLoadingDialog = new LoadingDialog(this, style.loading_dialog, "加载中...");
		mCommentDialog = new CommentEditDialog(this, R.style.loading_dialog);

		mDetector = new GestureDetectorCompat(this,
				new GestureDetector.SimpleOnGestureListener() {

					@Override
					public boolean onFling(MotionEvent e1, MotionEvent e2,
							float velocityX, float velocityY) {

						if (e2.getX() - e1.getX() > distance
								&& Math.abs(velocityX) > velocity) {
							if (isJpushPage) {
								Intent intent = new Intent(
										WebviewContentActivity.this,
										MainActivity.class);
								startActivity(intent);
								WebviewContentActivity.this.finish();
							} else {
								WebviewContentActivity.this.finish();
							}
						}

						return super.onFling(e1, e2, velocityX, velocityY);
					}

				});

		initUi();

		initWebSetting();

		setListener();

		initData();
	}

	private String dealContentWithSetting(String oldString) {
		String newString = "<body class=\"";
		if (!isDayTheme) {
			newString = newString + NIGHT;
		}

		if (mTextSize == 0) {
			newString = newString + SMALL;
		} else if (mTextSize == 1) {
			newString = newString + MID;
		} else {
			newString = newString + LARGE;
		}

		newString = newString + "\">";
		Log.e("THEME", "INIT=>" + newString);
		String showString = "";
		if (oldString != null) {
			showString = oldString.replace("<body>", newString);
		}

		return showString;
	}

	private void initWebSetting() {
		mWebSettings = mWebview.getSettings();
		mWebSettings.setJavaScriptEnabled(true);
		mWebSettings.setSupportZoom(true);
		// mWebview.setWebChromeClient(new WebChromeClient());
		mWebview.setWebViewClient(new NBDWebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.e("page load", "finish");

				super.onPageFinished(view, url);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				// TODO Auto-generated method stub
				handler.obtainMessage(WEBVIEW_FINISH).sendToTarget();
				super.onPageStarted(view, url, favicon);
			}
		});
		// Log.d("WEBVIEW", b.getLong("url")+"");

	}

	/**
	 * 获取网页详情的内容
	 */
	private void initData() {
		mLoadingDialog.showFullDialog();
		// 取数据库，判断数据库对应ID的数据是否存在content
		ArticleConfig config = new ArticleConfig();
		config.setType(RequestType.ARTICLE_DETAIL);
		config.setLocalArticle(true);
		config.setArticleId(mArticleId);
		ArticleManager.getInstence().getArticleDetail(config,
				new ArticleInfoCallback() {

					@Override
					public void onArticleInfoCallback(List<ArticleInfo> infos,
							RequestType type) {
						if (infos != null && infos.size() > 0) {
							Log.e("ARTICLE-DETAIL", "DATABASE OK");
							mDatabaseArticleInfo = infos.get(0);
							deatilDatabaseHtml = mDatabaseArticleInfo
									.getContent();
							mUpdataAt = mDatabaseArticleInfo.getUpdated_at();
							isStore = mDatabaseArticleInfo.isCollection();
							getDataFromNet();
							// deatilHtml = infos.get(0).getContent();
							// if (deatilHtml != null && !deatilHtml.equals(""))
							// {
							// handler.obtainMessage(LOAD_DATABASE_COMPLETE)
							// .sendToTarget();
							// Log.e("Article-Collection", "" + isStore);
							// }
						} else {
							Log.e("ARTICLE-DETAIL", "DATABASE NULL");
							getDataFromNet();
						}
					}
				});

		/**
		 * 调用文章被阅读的接口，如果在登录的状态下
		 */
		if (mAccessToken != null) {
			ArticleConfig readConfig = new ArticleConfig();
			readConfig.setType(RequestType.READING);
			readConfig.setArticleId(mArticleId);
			readConfig.setAccessToken(mAccessToken);
			ArticleManager.getInstence().setArticleRead(readConfig,
					new UserInfoCallback() {

						@Override
						public void onUserinfoCallback(UserInfo info,
								String failMsg) {
							if (info != null) {
								UserConfigUtile.storeSelfConfigToNative(
										mEditor, info);
							} else if (failMsg != null) {
								Log.e("READING ERROR-->", failMsg);
							}
						}
					});
		}

		// mArticleInfo = ArticleManager.getInstence().getShareArticleInfo(
		// mArticleId);
		// getDataFromNet();
	}

	/**
	 * 从网络获取文章详情数据
	 */
	private void getDataFromNet() {

		ArticleConfig config = new ArticleConfig();
		config.setLocalArticle(false);
		config.setType(RequestType.ARTICLE_DETAIL);
		config.setArticleId(mArticleId);
		if (mUpdataAt != null && !mUpdataAt.equals("")) {
			long upTime = Long.parseLong(mUpdataAt) / 1000;
			config.setCustomString(upTime + "");
		} else {
			config.setCustomString("");
		}

		ArticleManager.getInstence().getArticleDetail(config,
				new ArticleInfoCallback() {

					@Override
					public void onArticleInfoCallback(List<ArticleInfo> infos,
							RequestType type) {
						if (infos != null) {
							Log.e("ARTICLE-DETAIL", "NET OK");
							mArticleInfo = infos.get(0);
							deatilHtml = infos.get(0).getContent();
							isStore = mArticleInfo.isCollection();
							handler.obtainMessage(LOAD_INTERNET_COMPLETE)
									.sendToTarget();
						} else {
							Log.e("ARTICLE-DETAIL", "net NULL");
							if (mDatabaseArticleInfo != null) {
								handler.obtainMessage(LOAD_DATABASE_COMPLETE)
										.sendToTarget();
							} else {
								if (mLoadingDialog != null) {
									mLoadingDialog.dismiss();
								}
								Toast.makeText(WebviewContentActivity.this,
										"当前网络状况较差，请检查后重试", Toast.LENGTH_SHORT)
										.show();
							}
						}
					}

				});
		// new ArticleDetailCallback() {
		//
		// @Override
		// public void onArticleDetail(String content,
		// boolean isCollection) {
		// if (content != null) {
		// deatilHtml = content;
		// handler.obtainMessage(LOAD_INTERNET_COMPLETE)
		// .sendToTarget();
		// }
		//
		// }
		// });
	}

	private void setListener() {

		mThemeListener = new OnThemeChangeListener() {

			@Override
			public void onNightThemeChange(boolean isNowTheme) {
				isDayTheme = isNowTheme;
				showThemeUi();

			}
		};

		mTextListener = new onTextChangeListener() {

			@Override
			public void onTextSizeChangeListenre(int nowTextSize) {
				mTextSize = nowTextSize;

				if (mTextSize == 0) {
					setTextFontSize(fontSize.SMALL);
				} else if (mTextSize == 1) {
					setTextFontSize(fontSize.MID);
				} else {
					setTextFontSize(fontSize.BIG);
				}

			}
		};

		mBackBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isJpushPage) {
					Intent intent = new Intent(WebviewContentActivity.this,
							MainActivity.class);
					startActivity(intent);
					WebviewContentActivity.this.finish();
				} else {
					WebviewContentActivity.this.finish();
				}
			}
		});

		mShareImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mArticleInfo != null) {
					showShare(mArticleInfo, null);
				}

			}
		});

		mCommentEdit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isCanComment) {
					if (mCommentDialog == null) {
						mCommentDialog = new CommentEditDialog(
								WebviewContentActivity.this,
								R.style.loading_dialog);
					}
					mCommentDialog
							.setCommentSendListener(new onCommentSendInterface() {

								@Override
								public void onCommentSend(String comment) {
									handleCommentAction(comment);
								}
							});
					mCommentDialog.showFullDialog();
					Timer timer = new Timer();
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							mCommentDialog.showKeyboard();
						}
					}, 200);

				} else {
					Toast.makeText(WebviewContentActivity.this, "该文章不能评论",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mCommentNumLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isCanComment) {
					Intent i = new Intent(WebviewContentActivity.this,
							CommentActivity.class);
					i.putExtra("article_id", mArticleId);
					i.putExtra("comment_num", commentNum);
					startActivity(i);
				}

			}
		});

		mMoreHandleImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog = new ArticleMoreHandleDialog(
						WebviewContentActivity.this, R.style.headdialog,
						!isDayTheme, isStore, mTextSize);
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

		mWebview.setLongClickable(true);
		mWebview.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mDetector.onTouchEvent(event);
				return false;
			}
		});

		ThemeChangeManager.getInstance().registerThemeChangeListener(
				mThemeListener);
		ThemeChangeManager.getInstance().registerTextChangeListener(
				mTextListener);

	}

	/**
	 * 显示夜间模式变化的UI
	 */
	private void showThemeUi() {
		if (mWebview != null) {
			if (isDayTheme) {
				mWebview.loadUrl("javascript:changeModeDay()");
			} else {
				mWebview.loadUrl("javascript:changeModeNight()");
			}
		}

		if (isDayTheme) {
			mMainLayout.setBackgroundColor(WebviewContentActivity.this
					.getResources().getColor(R.color.day_section_background));
			mTitleLayout.setBackgroundColor(WebviewContentActivity.this
					.getResources().getColor(R.color.day_item_background));
			mBottomLayout.setBackgroundColor(WebviewContentActivity.this
					.getResources().getColor(R.color.day_item_background));
			mCover.setBackgroundColor(WebviewContentActivity.this
					.getResources().getColor(R.color.day_cover));
			mTitleText.setTextColor(WebviewContentActivity.this.getResources()
					.getColor(R.color.day_black));

		} else {
			mMainLayout.setBackgroundColor(WebviewContentActivity.this
					.getResources().getColor(R.color.night_common_background));
			mTitleLayout.setBackgroundColor(WebviewContentActivity.this
					.getResources().getColor(R.color.night_section_background));
			mBottomLayout.setBackgroundColor(WebviewContentActivity.this
					.getResources().getColor(R.color.night_section_background));
			mCover.setBackgroundColor(WebviewContentActivity.this
					.getResources().getColor(R.color.night_cover));
			mTitleText.setTextColor(WebviewContentActivity.this.getResources()
					.getColor(R.color.night_black));

		}

	}

	private void initUi() {
		mMainLayout = (RelativeLayout) findViewById(R.id.news_detail_main_layout);
		mWebview = (NBDWebView) findViewById(R.id.news_detail_webview);
		mTitleLayout = (RelativeLayout) findViewById(R.id.news_detail_title_layout);
		mBottomLayout = (LinearLayout) findViewById(R.id.news_detail_bottom_layout);
		mCover = (TextView) findViewById(R.id.article_detail_cover);
		mBottomLine = (TextView) findViewById(R.id.news_detail_bottom_line);

		mCommentCount = (TextView) findViewById(R.id.news_detail_comment_txt);
		mTitleText = (TextView) findViewById(R.id.news_detail_title_txt);
		mBackBtn = (ImageView) findViewById(R.id.news_detail_back_arrow);
		mCommentEdit = (EditText) findViewById(R.id.news_detail_comment_edit);
		mCommentNumLayout = (RelativeLayout) findViewById(R.id.news_detail_comment_num_layout);

		mMoreHandleImg = (RelativeLayout) findViewById(R.id.news_detail_more_layout);

		mCommentEdit.setFocusable(false);

		mShareImg = (ImageView) findViewById(R.id.news_detail_share_btn);

		mTitleText.setText(mActivityTitle);

		if (isCanComment) {
			mBottomLine.setVisibility(View.VISIBLE);
			mBottomLayout.setVisibility(View.VISIBLE);
		} else {
			mBottomLayout.setVisibility(View.GONE);
			mBottomLine.setVisibility(View.GONE);
		}

		mCommentCount.setText(commentNum + "");

	}

	/**
	 * 设置网页文字的字体大小
	 * 
	 * @param size
	 */
	@SuppressWarnings("deprecation")
	private void setTextFontSize(fontSize size) {
		if (mWebview != null) {

			switch (size) {
			case SMALL:
				mWebview.loadUrl("javascript:changeFontSizeSmall()");
				break;
			case MID:
				mWebview.loadUrl("javascript:changeFontSizeNormal()");
				break;
			case BIG:
				mWebview.loadUrl("javascript:changeFontSizeBig()");
				break;
			default:
				break;
			}
		}
	}

	private String getSDPath() {

		String path = "";
		boolean isExitSD = Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
		if (isExitSD) {
			File sdDir = Environment.getExternalStorageDirectory();
			if (sdDir != null) {
				path = sdDir.toString() + "/";
			}
		}

		return path;
	}

	private void showShare(ArticleInfo shArticleInfo, ArticleHandleType type) {
		if (shArticleInfo != null) {
			MobclickAgent.onEvent(WebviewContentActivity.this,
					UmenAnalytics.SHARE_ARTICLE);
			ShareSDK.initSDK(this);
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
			if (shArticleInfo.getImage() == null
					|| shArticleInfo.getImage().equals("")) {
				oks.setImageUrl("http://static.nbd.com.cn/images/nbd_icon.png");
			} else {
				oks.setImageUrl(shArticleInfo.getImage());
			}
			// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
			// url仅在微信（包括好友和朋友圈）中使用
			oks.setUrl(shArticleInfo.getUrl());
			// comment是我对这条分享的评论，仅在人人网和QQ空间使用
			oks.setComment("我是测试评论文本");
			// site是分享此内容的网站名称，仅在QQ空间使用
			oks.setSite(getString(R.string.app_name));
			// siteUrl是分享此内容的网站地址，仅在QQ空间使用
			oks.setSiteUrl(shArticleInfo.getUrl());

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

			oks.setCallback(new mShareListener());
			// 启动分享GUI
			oks.show(this);
		}
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
			// ShareUtile.showShare(WebviewContentActivity.this, mArticleInfo,
			// ArticleHandleType.QQ);
			break;
		case QZONE:
			showShare(mArticleInfo, ArticleHandleType.QZONE);
			break;
		case NIGHT:
			isDayTheme = !isDayTheme;
			mNativeEditor.putBoolean("theme", isDayTheme);
			mNativeEditor.commit();
			changeNightMode();
			break;
		case STORE:
			isStore = !isStore;
			collectArticle();
			break;
		case TEXT_SMALL:
			mNativeEditor.putInt("ArticleTextSize", 0);
			mNativeEditor.commit();
			mTextSize = 0;
			ThemeChangeManager.getInstance().changeText(0);
			setTextFontSize(fontSize.SMALL);
			break;
		case TEXT_MID:
			mNativeEditor.putInt("ArticleTextSize", 1);
			mNativeEditor.commit();
			mTextSize = 1;
			ThemeChangeManager.getInstance().changeText(1);
			setTextFontSize(fontSize.MID);
			break;
		case TEXT_BIG:
			mNativeEditor.putInt("ArticleTextSize", 2);
			mNativeEditor.commit();
			mTextSize = 2;
			ThemeChangeManager.getInstance().changeText(2);
			setTextFontSize(fontSize.BIG);
			break;

		default:
			break;
		}

	}

	private void changeNightMode() {
		if (mWebview != null) {
			if (isDayTheme) {
				mWebview.loadUrl("javascript:changeModeDay()");
			} else {
				mWebview.loadUrl("javascript:changeModeNight()");
			}
		}

		ThemeChangeManager.getInstance().changeTheme(isDayTheme);

	}

	private class mShareListener implements PlatformActionListener {

		@Override
		public void onCancel(Platform arg0, int arg1) {
			// TODO Auto-generated method stub
			Log.e("SHARE", "CANCLE");
			if (mDialog != null) {
				mDialog.dismiss();
			}
		}

		@Override
		public void onComplete(Platform arg0, int arg1,
				HashMap<String, Object> arg2) {
			// TODO Auto-generated method stub
			Log.e("SHARE", "ok");
			if (mDialog != null) {
				mDialog.dismiss();
			}

		}

		@Override
		public void onError(Platform arg0, int arg1, Throwable arg2) {
			// TODO Auto-generated method stub
			Log.e("SHARE", "error");
			if (mDialog != null) {
				mDialog.dismiss();
			}

		}

	}

	@Override
	protected void onDestroy() {
		ThemeChangeManager.getInstance().unregisterThemeChangeListener(
				mThemeListener);
		ThemeChangeManager.getInstance().unregisterTextChangeListener(
				mTextListener);
		super.onDestroy();
	}

	/**
	 * 根据当前文章的状态，收藏文章
	 */
	private void collectArticle() {
		MobclickAgent.onEvent(WebviewContentActivity.this,
				UmenAnalytics.ARTICLE_COLLECTION);
		if (mAccessToken != null) { // 登录的token不为空，已登录状态
			ArticleConfig collectionConfig = new ArticleConfig();
			collectionConfig.setType(RequestType.COLLECTION);
			collectionConfig.setCollection(isStore);
			collectionConfig.setArticleId(mArticleId);
			collectionConfig.setAccessToken(mAccessToken);
			ArticleManager.getInstence().setArticleCollection(collectionConfig,
					new UserInfoCallback() {

						@Override
						public void onUserinfoCallback(UserInfo info,
								String failMsg) {
							if (info != null) {
								UserConfigUtile.storeSelfConfigToNative(
										mEditor, info);
							}
						}
					});

		} else {
			ArticleConfig collectionConfig = new ArticleConfig();
			collectionConfig.setType(RequestType.COLLECTION);
			collectionConfig.setLocalArticle(true);
			collectionConfig.setCollection(isStore); // 与当前的存储状态相反
			collectionConfig.setArticleId(mArticleId);
			collectionConfig.setAccessToken(mAccessToken);
			ArticleManager.getInstence().setArticleCollection(collectionConfig,
					new UserInfoCallback() {

						@Override
						public void onUserinfoCallback(UserInfo info,
								String failMsg) {
							// TODO Auto-generated method stub

						}
					});
		}
	}

	/**
	 * 处理评论的事件
	 * 
	 * @param comment
	 *            评论的内容
	 */
	private void handleCommentAction(String comment) {

		ArticleConfig config = new ArticleConfig();
		config.setType(RequestType.COMMENT);
		config.setArticleId(mArticleId);
		config.setCustomString(comment);
		config.setAccessToken(mAccessToken);
		config.setHandleType(CommentHandleType.REPLY);

		if (mLoadingDialog != null) {
			mLoadingDialog.showFullDialog();
		}
		ArticleManager.getInstence().newComment(config, new UserInfoCallback() {

			@Override
			public void onUserinfoCallback(UserInfo info, String failMsg) {
				if (mLoadingDialog != null) {
					mLoadingDialog.dismiss();
				}

				if (info != null) {
					Toast.makeText(WebviewContentActivity.this, "评论成功",
							Toast.LENGTH_SHORT).show();
					if (info.getAccess_token() != null) {
						SharedPreferences.Editor mEditor = mConfigSharedPreferences
								.edit();
						UserConfigUtile.storeSelfConfigToNative(mEditor, info);
					}
				} else if (failMsg != null) {
					Toast.makeText(WebviewContentActivity.this, failMsg,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(WebviewContentActivity.this, "评论失败",
							Toast.LENGTH_SHORT).show();
				}

			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isJpushPage) {
				Intent intent = new Intent(WebviewContentActivity.this,
						MainActivity.class);
				startActivity(intent);
				WebviewContentActivity.this.finish();
			} else {
				WebviewContentActivity.this.finish();
			}
		}
		return super.onKeyDown(keyCode, event);
	}

}
