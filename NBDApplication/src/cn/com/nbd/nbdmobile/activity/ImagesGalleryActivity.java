package cn.com.nbd.nbdmobile.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.R.style;
import cn.com.nbd.nbdmobile.utility.ArticleHandleType;
import cn.com.nbd.nbdmobile.utility.Options;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.utility.UserConfigUtile;
import cn.com.nbd.nbdmobile.view.CustomListViewUnused;
import cn.com.nbd.nbdmobile.view.ZoomImageView;
import cn.com.nbd.nbdmobile.view.ZoomImageView.onImageLongPressListener;
import cn.com.nbd.nbdmobile.widget.ArticleMoreHandleDialog;
import cn.com.nbd.nbdmobile.widget.CommentEditDialog;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;
import cn.com.nbd.nbdmobile.widget.StaticViewPager;
import cn.com.nbd.nbdmobile.widget.ArticleMoreHandleDialog.onDialogBtnClick;
import cn.com.nbd.nbdmobile.widget.CommentEditDialog.onCommentSendInterface;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.Gallery;
import com.nbd.article.bean.UserInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.ArticleDetailCallback;
import com.nbd.article.managercallback.ArticleInfoCallback;
import com.nbd.article.managercallback.GalleryInfoCallback;
import com.nbd.article.managercallback.UserInfoCallback;
import com.nbd.network.bean.CommentHandleType;
import com.nbd.network.bean.RequestType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

/**
 * 图集类型文章展示的界面，左右滑动浏览图集内容
 * 
 * @author riche
 * 
 */
public class ImagesGalleryActivity extends Activity {

	private TextView mHeadTitleText; // 头部标题栏
	private ViewPager mImageViewpager; // 用于滑动展示图片的pager
	private TextView mCurPageText; // 当前页面的位置数据
	private TextView mTotalCountText; // 图集总数量显示数据
	private TextView mContentText; // 图片主要内容的文字
	private ImageView mBackImg; // 左下角返回图标
	private ImageView mMoreImg; // 点击展开更多的按钮
	private ImageView mTopShare; // 顶部在没有收藏时的显示分享
	private RelativeLayout mCommentNumLayout;

	private LinearLayout mBottomLayout;
	private EditText mCommentEdit;
	private TextView mCommentNumText;
	private ImageView mShareImg;

	private RelativeLayout mContentLayout; // 用于显示隐藏文字的layout

	private EditText mCommentInput; // 评论输入框

	private LayoutInflater mInflater;
	private ArrayList<View> pageViews;

	private List<String> mContens = new ArrayList<String>();
	private List<String> mImages = new ArrayList<String>();

	private int pagerCount;
	private String mTitle;
	private long mArticleId;
	private int mGalleryId;

	private long mReviewCount;
	private boolean isCanReview;
	private boolean isCollection;

	private int activityStarFrom; // 从哪种方式跳转而来，0普通文章跳转，有收藏功能，1相关文章跳转，只能分享

	// private ArticleInfo mArticleInfo;

	private Gallery mDatabaseGallery;
	private Gallery mGallery;

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	DisplayImageOptions options;

	private boolean isHideText; // 是否隐藏文字内容

	private ArticleMoreHandleDialog mDialog;
	private CommentEditDialog mCommentDialog;
	private LoadingDialog mLoadingDialog;

	private SharedPreferences mConfigSharedPreferences;
	private SharedPreferences.Editor mEditor;

	private boolean isNight;
	private boolean isStore;
	private int mTextSize;

	private int mArticleFromType; // 0 是带了图片列表的文章跳转，1是从相关图集跳转，仅仅是图集ID的
	private String mAccessToken;

	public final static int LOAD_DATABASE_COMPLETE = 0; // 数据库加载完成
	public final static int LOAD_INTERNET_COMPLETE = 1; // 下拉加载数据完成

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			initAdapter();

			if (isCanReview) {
				mBottomLayout.setVisibility(View.VISIBLE);
				mCommentNumText.setText(mReviewCount + "");
			} else {
				mBottomLayout.setVisibility(View.GONE);
			}

			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.DayTheme);
		setContentView(R.layout.activity_detail_gallery);
		mInflater = getLayoutInflater();
		options = Options.getGalleryOptions();

		mConfigSharedPreferences = this.getSharedPreferences("AppConfig",
				this.MODE_PRIVATE);
		mEditor = mConfigSharedPreferences.edit();
		mTextSize = mConfigSharedPreferences.getInt("ArticleTextSize", 1);
		mAccessToken = mConfigSharedPreferences.getString("accessToken", null);

		mLoadingDialog = new LoadingDialog(this, style.loading_dialog, "加载中...");
		mCommentDialog = new CommentEditDialog(this, R.style.loading_dialog);

		Intent i = getIntent();
		Bundle b = i.getBundleExtra("urlbundle");
		mGalleryId = b.getInt("galleryId");
		mArticleId = b.getLong("articleId", 0);
		mReviewCount = b.getLong("reviewCount",0);
		isCanReview = b.getBoolean("allowReview",false);
		isStore = b.getBoolean("isCollection");
		activityStarFrom = b.getInt("fromType");

		initUi();
		setListener();
		initData();
		MobclickAgent.onEvent(ImagesGalleryActivity.this, UmenAnalytics.DETAIL_GALLERY);
	}

	private void setListener() {

		mBackImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				ImagesGalleryActivity.this.finish();
			}
		});

		mMoreImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog = new ArticleMoreHandleDialog(
						ImagesGalleryActivity.this, R.style.headdialog, false,
						isStore, mTextSize);
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

		mShareImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mGallery != null) {
					showShare(mGallery, null);
				}
			}
		});

		mTopShare.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mGallery != null) {
					showShare(mGallery, null);
				}
			}
		});

		mCommentEdit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isCanReview) {
					if (mCommentDialog == null) {
						mCommentDialog = new CommentEditDialog(
								ImagesGalleryActivity.this,
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
					Toast.makeText(ImagesGalleryActivity.this, "该文章不能评论",
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		mCommentNumLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isCanReview) {
					Intent i = new Intent(ImagesGalleryActivity.this,
							CommentActivity.class);
					i.putExtra("article_id", (long) mGalleryId);
					i.putExtra("comment_num", mReviewCount);
					i.putExtra("isImage", true);
					startActivity(i);
				}

			}
		});

	}

	/**
	 * 初始化数据，传递来的内容数据，图片链接集合
	 */
	private void initData() {

		// // 取数据库，判断数据库对应ID的数据是否存在content
		// if (mArticleId > 0) { // 如果有文章articleId，取数据就取完整的文章数据，带收藏状态
		// ArticleConfig config = new ArticleConfig();
		// config.setType(RequestType.ARTICLE_DETAIL);
		// config.setLocalArticle(true);
		// config.setArticleId(mArticleId);
		// ArticleManager.getInstence().getArticleDetail(config,
		// new ArticleInfoCallback() {
		//
		// @Override
		// public void onArticleInfoCallback(
		// List<ArticleInfo> infos, RequestType type) {
		// if (infos != null && infos.size() > 0) {
		// isStore = infos.get(0).isCollection();
		// }
		// }
		// });
		// }

		ArticleConfig netConfig = new ArticleConfig();
		netConfig.setPageCount(mGalleryId);
		netConfig.setType(RequestType.GALLERY);
		netConfig.setLocalArticle(true);
		if (mLoadingDialog != null) {
			mLoadingDialog.showFullDialog();
		}
		ArticleManager.getInstence().getGalleries(netConfig,
				new GalleryInfoCallback() {

					@Override
					public void onGalleryCallback(Gallery gallery) {
						if (gallery != null) {
							mDatabaseGallery = gallery;
							Log.e("IMAGE GALLERY==", "DATABASE NOT NULL");
							// handler.sendEmptyMessage(0);
							// initAdapter();
							getNetGallery();
						} else {
							getNetGallery();
						}
					}
				});

		/**
		 * 调用文章被阅读的接口，如果在登录的状态下
		 */
		if (mAccessToken != null && mArticleId > 0) {
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
							}

						}
					});
		}

		// mArticleInfo = ArticleManager.getInstence()
		// .getShareArticleInfo(mArticleId);
	}

	private void getNetGallery() {
		ArticleConfig netConfig = new ArticleConfig();
		netConfig.setPageCount(mGalleryId);
		netConfig.setType(RequestType.GALLERY);
		netConfig.setLocalArticle(false);
		ArticleManager.getInstence().getGalleries(netConfig,
				new GalleryInfoCallback() {

					@Override
					public void onGalleryCallback(Gallery gallery) {
						if (gallery != null) {
							mGallery = gallery;
							Log.e("gallery net ==", "not null");
							handler.sendEmptyMessage(0);
							// initAdapter();
						} else {
							if (mDatabaseGallery != null) {
								mGallery = mDatabaseGallery;
								handler.sendEmptyMessage(0);
							} else {
								if (mLoadingDialog != null) {
									mLoadingDialog.dismiss();
									Toast.makeText(ImagesGalleryActivity.this,
											"当前网络较差，请检查后重试", Toast.LENGTH_SHORT)
											.show();
								}
							}
						}
					}
				});
	}

	/**
	 * 初始化适配器 界面数据显示
	 */
	private void initAdapter() {
		if (mLoadingDialog != null) {
			mLoadingDialog.dismiss();
		}

		List<ArticleImages> images = mGallery.getImages();

		Log.e("gallery size==", images.size() + "");

		if (images != null && images.size() > 0) {
			pagerCount = images.size();
			for (int i = 0; i < images.size(); i++) {
				mContens.add(images.get(i).getDesc());
				mImages.add(images.get(i).getImage_src());
			}

			mTitle = mGallery.getTitle();

			if (activityStarFrom != 0) { // 网络返回情况，以gallery取到的数据为主
				isCanReview = mGallery.isAllow_review();
				mReviewCount = mGallery.getReview_count();
			}

			mHeadTitleText.setText(mGallery.getTitle());
			mContentText.setText(mContens.get(0));
			mTotalCountText.setText("/" + pagerCount);
			mCurPageText.setText("1");

			pageViews = new ArrayList<View>();
			// 测试的布局加载
			for (int i = 0; i < pagerCount; i++) {
				View scrollView = mInflater.inflate(
						R.layout.item_gallery_page_detail, null);
				pageViews.add(scrollView);
			}

			mImageViewpager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					mCurPageText.setText("" + (arg0 + 1));
					mContentText.setText(mContens.get(arg0));

				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub

				}
			});
			mImageViewpager.setAdapter(new MyAdapter());
		}

	}

	/**
	 * 初始化界面控件
	 */
	private void initUi() {
		mHeadTitleText = (TextView) findViewById(R.id.gallert_content_title);

		mImageViewpager = (ViewPager) findViewById(R.id.gallery_viewpager);
		mCurPageText = (TextView) findViewById(R.id.gallery_curpage_num);
		mTotalCountText = (TextView) findViewById(R.id.gallery_total_page_num);
		mContentText = (TextView) findViewById(R.id.gallery_content_txt);
		mBackImg = (ImageView) findViewById(R.id.gallery_title_back_btn);
		mMoreImg = (ImageView) findViewById(R.id.gallery_title_more);
		mTopShare = (ImageView) findViewById(R.id.gallery_top_share);

		mContentLayout = (RelativeLayout) findViewById(R.id.gallery_content_layout);

		mBottomLayout = (LinearLayout) findViewById(R.id.gallery_bottom_layout);
		mCommentEdit = (EditText) findViewById(R.id.gallery_comment_edit);
		mCommentNumText = (TextView) findViewById(R.id.gallery_detail_comment_txt);
		mShareImg = (ImageView) findViewById(R.id.gallery_detail_share_btn);
		mCommentNumLayout = (RelativeLayout) findViewById(R.id.gallery_detail_comment_layout);

		mCommentEdit.setFocusable(false);

		if (activityStarFrom == 0) {
			mTopShare.setVisibility(View.GONE);
			mMoreImg.setVisibility(View.VISIBLE);
		} else {
			mTopShare.setVisibility(View.VISIBLE);
			mMoreImg.setVisibility(View.GONE);
		}

	}

	// PagerAdapter是object的子类
	class MyAdapter extends PagerAdapter {

		/**
		 * PagerAdapter管理数据大小
		 */
		@Override
		public int getCount() {
			return pageViews.size();
		}

		/**
		 * 关联key 与 obj是否相等，即是否为同一个对象
		 */
		@Override
		public boolean isViewFromObject(View view, Object obj) {
			return view == obj; // key
		}

		/**
		 * 销毁当前page的相隔2个及2个以上的item时调用
		 */
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object); // 将view 类型 的object熊容器中移除,根据key
		}

		/**
		 * 当前的page的前一页和后一页也会被调用，如果还没有调用或者已经调用了destroyItem
		 */
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			System.out.println("pos:" + position);
			View view = pageViews.get(position);
			// 如果访问网络下载图片，此处可以进行异步加载
			ZoomImageView img = (ZoomImageView) view
					.findViewById(R.id.scroll_image);
			imageLoader.displayImage(mImages.get(position), img, options);

			img.setOnImgaePressListener(new onImageLongPressListener() {

				@Override
				public void onTapStateChange() {

					if (isHideText) {
						isHideText = false;
						mContentLayout.setVisibility(View.VISIBLE);
					} else {
						isHideText = true;
						mContentLayout.setVisibility(View.GONE);
					}

				}
			});

			container.addView(view);
			return pageViews.get(position); // 返回该view对象，作为key
		}
	}

	private void showShare(Gallery gallery, ArticleHandleType type) {

		MobclickAgent.onEvent(ImagesGalleryActivity.this, UmenAnalytics.SHARE_GALLERY);
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();

		if (gallery != null) {

			// 关闭sso授权
			oks.disableSSOWhenAuthorize();
			// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
			// oks.setNotification(R.drawable.ic_launcher,
			// getString(R.string.app_name));
			// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
			oks.setTitle(gallery.getTitle());
			// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
			oks.setTitleUrl(gallery.getUrl());
			// text是分享文本，所有平台都需要这个字段
			oks.setText(gallery.getDesc());
			// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
			if (gallery.getImage() == null || gallery.getImage().equals("")) {
				oks.setImageUrl("http://static.nbd.com.cn/images/nbd_icon.png");
			}else {
				oks.setImageUrl(gallery.getImage());
			}
//			oks.setImageUrl(gallery.getImage());
			// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
			// url仅在微信（包括好友和朋友圈）中使用
			oks.setUrl(gallery.getUrl());
			// comment是我对这条分享的评论，仅在人人网和QQ空间使用
			oks.setComment("我是测试评论文本");
			// site是分享此内容的网站名称，仅在QQ空间使用
			oks.setSite(getString(R.string.app_name));
			// siteUrl是分享此内容的网站地址，仅在QQ空间使用
			oks.setSiteUrl(gallery.getUrl());

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

			showShare(mGallery, ArticleHandleType.WEIXIN);
			break;
		case WEIXIN_CIRCLE:
			showShare(mGallery, ArticleHandleType.WEIXIN_CIRCLE);
			break;
		case SINA:
			showShare(mGallery, ArticleHandleType.SINA);
			break;
		case QQ:
			showShare(mGallery, ArticleHandleType.QQ);
			break;
		case QZONE:
			showShare(mGallery, ArticleHandleType.QZONE);
			break;
		case NIGHT:
			break;
		case STORE:
			isStore = !isStore;
			collectArticle();
			break;
		case TEXT_SMALL:
			mEditor.putInt("ArticleTextSize", 0);
			mEditor.commit();
			mTextSize = 0;
			break;
		case TEXT_MID:
			mEditor.putInt("ArticleTextSize", 1);
			mEditor.commit();
			mTextSize = 1;
			break;
		case TEXT_BIG:
			mEditor.putInt("ArticleTextSize", 2);
			mEditor.commit();
			mTextSize = 2;
			break;

		default:
			break;
		}

	}

	/**
	 * 根据当前文章的状态，收藏文章
	 */
	private void collectArticle() {
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
		config.setArticleId((long) mGalleryId);
		config.setCustomString(comment);
		config.setAccessToken(mAccessToken);
		config.setCustomFlag(true);
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
					Toast.makeText(ImagesGalleryActivity.this, "评论成功",
							Toast.LENGTH_SHORT).show();
					if (info.getAccess_token() != null) {
						SharedPreferences.Editor mEditor = mConfigSharedPreferences
								.edit();
						UserConfigUtile.storeSelfConfigToNative(mEditor, info);
					}
				} else if (failMsg != null) {
					Toast.makeText(ImagesGalleryActivity.this, failMsg,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ImagesGalleryActivity.this, "评论失败",
							Toast.LENGTH_SHORT).show();
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
