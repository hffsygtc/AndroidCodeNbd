package cn.com.nbd.nbdmobile.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import cn.com.nbd.nbdmobile.adapter.CollectingArticleAdapter;
import cn.com.nbd.nbdmobile.adapter.MessageCenterAdapter;
import cn.com.nbd.nbdmobile.adapter.SelfCommentAdapter;
import cn.com.nbd.nbdmobile.adapter.CollectingArticleAdapter.OnNewsClickListener;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.manager.OnThemeChangeListener;
import cn.com.nbd.nbdmobile.manager.ThemeChangeManager;
import cn.com.nbd.nbdmobile.utility.ArticleJumpUtil;
import cn.com.nbd.nbdmobile.utility.CommentHelper;
import cn.com.nbd.nbdmobile.utility.UserConfigUtile;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused.OnLoadMoreListener;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;

import com.nbd.article.article.ArticleType;
import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.MyMessageBean;
import com.nbd.article.bean.SingleComment;
import com.nbd.article.bean.UserInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.ArticleInfoCallback;
import com.nbd.article.managercallback.MessageInfoCallback;
import com.nbd.article.managercallback.SelfCommentCallback;
import com.nbd.article.managercallback.UserInfoCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

/**
 * 消息中心的页面
 * 
 * @author riche
 * 
 */

public class MessageCenterActivity extends Activity implements OnClickListener {

	private TextView mTitleText; // 标题栏

	private RelativeLayout mLeftToggleLayout;
	private RelativeLayout mRightToggleLayout;

	private ImageView mBackBtn;

	private TextView mLeftToggleTitle; // 左边选项的名称
	private TextView mRightToggleTitle; // 右边选项的名称

	private TextView mLeftLine; // 左边选项选中下划线
	private TextView mRightLine; // 右边选项选中下划线

	private RelativeLayout mMainLayout;
	private TextView mCover;

	private ImageView mDefaultBackground; // 默认背景图片
	private WithoutSectionCustomListViewUnused mContentListview; // 内容的载体

	private int mActivityType; // 当前页面的类型

	private final static int MY_MESSAGE = 0; // 消息中心
	private final static int MY_COLLECT = 1; // 收藏

	private final static int LEFT_SECTION = 0; // 左边的点击
	private final static int RIGHT_SECTION = 1; // 右边的点击

	private int mNowShowSection; // 用户手动选择要显示的数据

	private String mAccessTokenString;

	private ArrayList<MyMessageBean> myMessageBeans; // 我的通知

	private List<ArticleInfo> mNormalCollections = new ArrayList<ArticleInfo>(); // 一般的文章收藏
	private List<ArticleInfo> mImageCollections = new ArrayList<ArticleInfo>(); // 图片的文章收藏
	private List<ArticleInfo> mShowCollections = new ArrayList<ArticleInfo>(); // 显示的收藏文章
	private List<SingleComment> mShowComments = new ArrayList<SingleComment>();
	private List<SingleComment> returnComments = new ArrayList<SingleComment>();

	private SelfCommentAdapter relativeCommentAdapter;
	private MessageCenterAdapter relativeMessageAdapter;
	private CollectingArticleAdapter mCollectionAdapter;

	private OnThemeChangeListener mThemeChangeListener;

	private OnLoadMoreListener mloadMoreListener;

	private LoadingDialog mLoadingDialog;

	public final static int LOAD_DATABASE_NORMAL_COMPLETE = 0;
	public final static int LOAD_DATABASE_IMAGE_COMPLETE = 1;
	public final static int LOAD_COLLECT_NORMAL_COMPLETE = 2;
	public final static int LOAD_COLLECT_IMAGE_COMPLETE = 3;
	public final static int LOAD_MESSAGE_COMPLETE = 4;
	public final static int LOAD_MORE_MESSAGE_COMPLETE = 5;
	public final static int LOAD_COMMENT_COMPLETE = 6;
	public final static int LOAD_MORE_COMMENT_COMPLETE = 7;

	private final static int PER_PAGE_COUNT = 15;

	private int leftPage = 1;
	private int rightPage = 1;

	private boolean isLeftCanLoadMore;
	private boolean isRightCanLoadMore;
	private boolean isDayTheme;
	private boolean isTextMode;

	private SharedPreferences mNativeShare;
	private SharedPreferences mConfigShare;
	
	private int mCommentNotice;
	private int mMessageNotice;
	
	/**
	 * 处理文章数据回调的事件
	 * 
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (mLoadingDialog != null) {
				mLoadingDialog.dismiss();
			}
			switch (msg.what) {
			case LOAD_COLLECT_NORMAL_COMPLETE:
				mContentListview.setCanLoadMore(isLeftCanLoadMore);
				if (mCollectionAdapter == null) {
					initAdapter(0);
				}
				if (mNowShowSection == LEFT_SECTION) {
					mCollectionAdapter.changeShowSection(LEFT_SECTION);
					mShowCollections = mNormalCollections;
					mCollectionAdapter
							.setDataChange((ArrayList<ArticleInfo>) mShowCollections);
					mCollectionAdapter.notifyDataSetChanged();
					if (msg.arg1 == 2) {
						mContentListview.onLoadMoreComplete();
					}
				} else {
					Log.e("COLLECT NORMAL", "RETURN IN OTHER PAGE");
				}
				break;
			case LOAD_COLLECT_IMAGE_COMPLETE:
				mContentListview.setCanLoadMore(isRightCanLoadMore);
				if (mCollectionAdapter == null) {
					initAdapter(0);
				}
				if (mNowShowSection == RIGHT_SECTION) {
					mCollectionAdapter.changeShowSection(RIGHT_SECTION);
					mShowCollections = mImageCollections;
					mCollectionAdapter
							.setDataChange((ArrayList<ArticleInfo>) mShowCollections);
					mCollectionAdapter.notifyDataSetChanged();
					if (msg.arg1 == 2) {
						mContentListview.onLoadMoreComplete();
					}
				} else {
					Log.e("COLLECT IMAGE", "RETURN IN OTHER PAGE");
				}
				break;
			case LOAD_DATABASE_NORMAL_COMPLETE:
				mContentListview.setCanLoadMore(isLeftCanLoadMore);
				if (mCollectionAdapter == null) {
					initAdapter(0);
				}
				if (mNowShowSection == LEFT_SECTION) {
					mCollectionAdapter.changeShowSection(LEFT_SECTION);
					mShowCollections = mNormalCollections;
					mCollectionAdapter
							.setDataChange((ArrayList<ArticleInfo>) mShowCollections);
					mCollectionAdapter.notifyDataSetChanged();
				} else {
					Log.e("COLLECT NORMAL", "RETURN IN OTHER PAGE");
				}
				break;
			case LOAD_DATABASE_IMAGE_COMPLETE:
				mContentListview.setCanLoadMore(isRightCanLoadMore);
				if (mCollectionAdapter == null) {
					initAdapter(0);
				}
				if (mNowShowSection == RIGHT_SECTION) {
					mCollectionAdapter.changeShowSection(RIGHT_SECTION);
					mShowCollections = mImageCollections;
					mCollectionAdapter
							.setDataChange((ArrayList<ArticleInfo>) mShowCollections);
					mCollectionAdapter.notifyDataSetChanged();
				} else {
					Log.e("COLLECT IMAGE", "RETURN IN OTHER PAGE");
				}
				break;
			case LOAD_MESSAGE_COMPLETE:
				mContentListview.setCanLoadMore(isRightCanLoadMore);
				if (relativeMessageAdapter == null) {
					initAdapter(1);
				}
				if (mNowShowSection == RIGHT_SECTION) {
					relativeMessageAdapter.setDataChange(myMessageBeans);
					relativeMessageAdapter.notifyDataSetChanged();
				} else {
					Log.e("MESSAGE", "RETURN IN OTHER PAGE");
				}
				// if (mloadMoreListener != null && mContentListview != null) {
				// mContentListview.setOnLoadListener(mloadMoreListener);
				// }
				break;
			case LOAD_MORE_MESSAGE_COMPLETE:
				mContentListview.setCanLoadMore(isRightCanLoadMore);
				if (relativeMessageAdapter == null) {
					initAdapter(1);
				}
				relativeMessageAdapter.setDataChange(myMessageBeans);
				relativeMessageAdapter.notifyDataSetChanged();
				mContentListview.onLoadMoreComplete();
				break;
			case LOAD_COMMENT_COMPLETE:
				mContentListview.setCanLoadMore(isLeftCanLoadMore);
				if (relativeCommentAdapter == null) {
					initAdapter(2);
				}
				if (mNowShowSection == LEFT_SECTION) {
					relativeCommentAdapter.setDataChange(mShowComments);
					relativeCommentAdapter.notifyDataSetChanged();
				} else {
					Log.e("MESSAGE", "RETURN IN OTHER PAGE");
				}
				// if (mloadMoreListener != null && mContentListview != null) {
				// mContentListview.setOnLoadListener(mloadMoreListener);
				// }
				break;
			case LOAD_MORE_COMMENT_COMPLETE:
				mContentListview.setCanLoadMore(isLeftCanLoadMore);
				if (relativeCommentAdapter == null) {
					initAdapter(2);
				}
				relativeCommentAdapter.setDataChange(mShowComments);
				relativeCommentAdapter.notifyDataSetChanged();
				mContentListview.onLoadMoreComplete();
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
		isTextMode = mNativeShare.getBoolean("textMode", false);

		if (isDayTheme) {
			setTheme(R.style.DayTheme);
		} else {
			setTheme(R.style.NightTheme);
		}
		setContentView(R.layout.activity_message_center);

		mConfigShare = this
				.getSharedPreferences("AppConfig", this.MODE_PRIVATE);
		
		UserInfo user = UserConfigUtile.getUserinfoFormNative(mConfigShare);
		mAccessTokenString = user.getAccess_token();
		mCommentNotice = user.getNotifications().getMy_msg();
		mMessageNotice = user.getNotifications().getSys_msg();
		
		Intent i = getIntent();
		mActivityType = i.getIntExtra("activityType", 0);

		mNowShowSection = LEFT_SECTION;

		mLoadingDialog = new LoadingDialog(MessageCenterActivity.this,
				R.style.loading_dialog, "加载中...");
		initUi();
		setListener();
		initData();

	}

	/**
	 * 初始化适配器
	 * 
	 * @param type
	 *            0：收藏相关 1：我的消息 2：我的评论
	 */
	private void initAdapter(int type) {
		switch (type) {
		case 1:
			relativeMessageAdapter = new MessageCenterAdapter(this,
					myMessageBeans, isDayTheme);
			mContentListview.setAdapter(relativeMessageAdapter);
			break;
		case 0:
			mCollectionAdapter = new CollectingArticleAdapter(this,
					(ArrayList<ArticleInfo>) mShowCollections, isTextMode,
					isDayTheme);
			mCollectionAdapter.setNewsClickListener(new OnNewsClickListener() {

				@Override
				public void onNewsClick(View view, ArticleInfo article) {
					if (article != null) {

						ArticleJumpUtil.jumpToArticleDetal(
								MessageCenterActivity.this, article, "文章收藏",false);

					}
				}
			});
			mContentListview.setAdapter(mCollectionAdapter);
			break;
		case 2:
			relativeCommentAdapter = new SelfCommentAdapter(
					MessageCenterActivity.this, mShowComments, isDayTheme);
			
			
			mContentListview.setAdapter(relativeCommentAdapter);
			break;

		default:
			break;
		}

	}

	/**
	 * 获取个人的消息数据
	 */
	private void initData() {

		if (mActivityType == MY_MESSAGE) {
			loadDataFromNet(LEFT_SECTION);
		} else {
			loadDataFromNet(LEFT_SECTION);
		}

	}

	private void initUi() {

		mMainLayout = (RelativeLayout) findViewById(R.id.message_main_layout);
		mCover = (TextView) findViewById(R.id.message_cover);

		mBackBtn = (ImageView) findViewById(R.id.self_msg_back_btn);
		mTitleText = (TextView) findViewById(R.id.self_msg_title_text);
		mLeftToggleLayout = (RelativeLayout) findViewById(R.id.self_msg_left_toggle_layout);
		mRightToggleLayout = (RelativeLayout) findViewById(R.id.self_msg_right_toggle_layout);
		mLeftToggleTitle = (TextView) findViewById(R.id.self_msg_left_title);
		mRightToggleTitle = (TextView) findViewById(R.id.self_msg_right_title);
		mLeftLine = (TextView) findViewById(R.id.self_msg_left_bottom_line);
		mRightLine = (TextView) findViewById(R.id.self_msg_right_bottom_line);

		mDefaultBackground = (ImageView) findViewById(R.id.self_msg_default_img);
		mContentListview = (WithoutSectionCustomListViewUnused) findViewById(R.id.self_msg_listview);

		mContentListview.setTheme(isDayTheme);

		switch (mActivityType) {
		case MY_MESSAGE:
			mTitleText.setText("我的消息");
			mLeftToggleTitle.setText("与我相关");
			mRightToggleTitle.setText("我的通知");
			mLeftLine.setVisibility(View.VISIBLE);
			mRightLine.setVisibility(View.GONE);
			break;
		case MY_COLLECT:
			mTitleText.setText("我的收藏");
			mLeftToggleTitle.setText("新闻");
			mRightToggleTitle.setText("图片");
			mLeftLine.setVisibility(View.VISIBLE);
			mRightLine.setVisibility(View.GONE);
			break;

		default:
			break;
		}

	}

	/**
	 * 点击事件
	 */
	private void setListener() {

		mThemeChangeListener = new OnThemeChangeListener() {

			@Override
			public void onNightThemeChange(boolean isNowTheme) {
				showThemeUi(isNowTheme);
			}
		};

		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MessageCenterActivity.this.finish();

			}
		});
		/**
		 * 左边栏目的点击
		 */
		mLeftToggleLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLeftLine.setVisibility(View.VISIBLE);
				mRightLine.setVisibility(View.GONE);
				mNowShowSection = LEFT_SECTION;
				changeContent(LEFT_SECTION);

			}
		});

		/**
		 * 右边栏目的点击
		 */
		mRightToggleLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLeftLine.setVisibility(View.GONE);
				mRightLine.setVisibility(View.VISIBLE);
				mNowShowSection = RIGHT_SECTION;
				changeContent(RIGHT_SECTION);
				// loadDataFromNet(RIGHT_SECTION);

			}
		});

		mloadMoreListener = new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMore();

			}
		};
		/**
		 * 列表上拉加载更多的监听
		 */
		mContentListview.setOnLoadListener(mloadMoreListener);
		
		mContentListview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Log.e("ITEM CLICK", "POSITION==" + position);

				if (mActivityType == MY_MESSAGE && mNowShowSection == LEFT_SECTION) {
					if (position < mShowComments.size() + 1) {
						ArticleInfo article = new ArticleInfo();
						article.setArticle_id(mShowComments.get(position - 1)
								.getArticle_id());
						article.setType(mShowComments.get(position - 1).getType());
						article.setGallery_id((int)(mShowComments.get(position-1).getType_id()));
						article.setTitle(mShowComments.get(position - 1)
								.getArticle_title());
						article.setAllow_review(true);
						
						ArticleJumpUtil.jumpToArticleDetal(
								MessageCenterActivity.this, article, "评论文章",false);
					}
				}
			}
		});
		

		ThemeChangeManager.getInstance().registerThemeChangeListener(
				mThemeChangeListener);

	}

	private void showThemeUi(boolean isNowTheme) {
		if (isNowTheme & isDayTheme) {

		} else {
			isDayTheme = isNowTheme;

			if (isDayTheme) {
				mMainLayout.setBackgroundColor(MessageCenterActivity.this
						.getResources()
						.getColor(R.color.day_section_background));
				mCover.setBackgroundColor(MessageCenterActivity.this
						.getResources().getColor(R.color.day_cover));

			} else {
				mMainLayout.setBackgroundColor(MessageCenterActivity.this
						.getResources().getColor(
								R.color.night_common_background));
				mCover.setBackgroundColor(MessageCenterActivity.this
						.getResources().getColor(R.color.night_cover));

			}

			if (relativeMessageAdapter != null) {
				relativeMessageAdapter.changeTheme(isDayTheme);
				relativeMessageAdapter.notifyDataSetChanged();
			}

			if (mCollectionAdapter != null) {
				mCollectionAdapter.changeTheme(isDayTheme);
				mCollectionAdapter.notifyDataSetChanged();
			}

			if (mContentListview != null) {
				mContentListview.setTheme(isDayTheme);
			}

		}

	}

	/**
	 * 点击左右栏目进行切换
	 * 
	 * @param leftSection
	 */
	private void changeContent(int section) {
		switch (mActivityType) {
		case MY_COLLECT:
			if (section == LEFT_SECTION && mCollectionAdapter != null) {
				mContentListview.setCanLoadMore(isLeftCanLoadMore);
				if (mNormalCollections != null && mNormalCollections.size()>0) {
					mCollectionAdapter.changeShowSection(LEFT_SECTION);
					mShowCollections = mNormalCollections;
					mCollectionAdapter
							.setDataChange((ArrayList<ArticleInfo>) mShowCollections);
					mCollectionAdapter.notifyDataSetChanged();
				} else {
					loadDataFromNet(LEFT_SECTION);
				}
			} else if (section == RIGHT_SECTION && mCollectionAdapter != null) {
				mContentListview.setCanLoadMore(isRightCanLoadMore);
				if (mImageCollections != null && mImageCollections.size()>0) {
					mCollectionAdapter.changeShowSection(RIGHT_SECTION);
					mShowCollections = mImageCollections;
					mCollectionAdapter
							.setDataChange((ArrayList<ArticleInfo>) mShowCollections);
					mCollectionAdapter.notifyDataSetChanged();
				} else {
					loadDataFromNet(RIGHT_SECTION);
				}
			}

			break;
		case MY_MESSAGE:
			
			if (section == LEFT_SECTION) {
				mContentListview.setCanLoadMore(isLeftCanLoadMore);
				if (relativeCommentAdapter != null) {
					mContentListview.setAdapter(relativeCommentAdapter);
				}else {
					loadDataFromNet(LEFT_SECTION);
				}
			}else {
				mContentListview.setCanLoadMore(isRightCanLoadMore);
				if (relativeMessageAdapter != null) {
					mContentListview.setAdapter(relativeMessageAdapter);
				}else {
					loadDataFromNet(RIGHT_SECTION);
				}
			}
			break;

		default:
			break;
		}

	}

	/**
	 * 获取消息或者收藏的网上数据
	 * 
	 * @param section
	 */
	private void loadDataFromNet(int section) {

		switch (mActivityType) {
		case MY_COLLECT:
			if (mLoadingDialog != null) {
				mLoadingDialog.showFullDialog();
			}
			if (mAccessTokenString != null) { // 登录状态下，收藏文章从服务器上面获取
				Log.e("COLLECT==>", "TOKEN" + mAccessTokenString);

				if (section == LEFT_SECTION) { // 登录状态下，普通文章的收藏获取
					ArticleConfig collectionConfig = new ArticleConfig();
					collectionConfig.setType(RequestType.COLLECTION);
					collectionConfig.setAccessToken(mAccessTokenString);
					collectionConfig.setCustomFlag(false);
					ArticleManager.getInstence().getArticleCollection(
							collectionConfig, new ArticleInfoCallback() {

								@Override
								public void onArticleInfoCallback(
										List<ArticleInfo> infos,
										RequestType type) {

									if (infos != null) {
										
										Log.e("COLLOTION==",
												"COUNT" + infos.size());
										if (infos.size() < 10) {
											isLeftCanLoadMore = false;
										} else {
											isLeftCanLoadMore = true;
										}
										mNormalCollections = infos;
										Message message = new Message();
										message.what = LOAD_COLLECT_NORMAL_COMPLETE;
										handler.sendMessage(message);
										
										SyncCollectionState(infos);
									} else {
										if (mLoadingDialog != null) {
											mLoadingDialog.dismiss();
										}
										Toast.makeText(
												MessageCenterActivity.this,
												"当前网络状况较差，请检查后重试", Toast.LENGTH_SHORT)
												.show();
									}
								}
							});

				} else {// 登录状态下，图片文章的收藏获取
					ArticleConfig collectionConfig = new ArticleConfig();
					collectionConfig.setType(RequestType.COLLECTION);
					collectionConfig.setAccessToken(mAccessTokenString);
					collectionConfig.setCustomFlag(true);
					ArticleManager.getInstence().getArticleCollection(
							collectionConfig, new ArticleInfoCallback() {

								@Override
								public void onArticleInfoCallback(
										List<ArticleInfo> infos,
										RequestType type) {
									if (infos != null) {
										if (infos.size() < 10) {
											isRightCanLoadMore = false;
										} else {
											isRightCanLoadMore = true;
										}

										mImageCollections = infos;
										Message message = new Message();
										message.what = LOAD_COLLECT_IMAGE_COMPLETE;
										handler.sendMessage(message);
										SyncCollectionState(infos);
									} else {
										if (mLoadingDialog != null) {
											mLoadingDialog.dismiss();
										}
										Toast.makeText(
												MessageCenterActivity.this,
												"当前网络状况较差，请检查后重试.", Toast.LENGTH_SHORT)
												.show();
									}

								}
							});

				}

			} else { // 非登录状态下，收藏文章从本地获取
				final ArticleConfig collectionConfig = new ArticleConfig();
				collectionConfig.setType(RequestType.COLLECTION);
				collectionConfig.setLocalArticle(true);
				if (section == LEFT_SECTION) {
					collectionConfig.setCustomFlag(false);
				} else {
					collectionConfig.setCustomFlag(true);
				}
				ArticleManager.getInstence().getArticleCollection(
						collectionConfig, new ArticleInfoCallback() {

							@Override
							public void onArticleInfoCallback(
									List<ArticleInfo> infos, RequestType type) {
								Message message = new Message();
								if (collectionConfig.isCustomFlag()) {
									mImageCollections = infos;
									if (infos != null && infos.size() < 15) {
										isRightCanLoadMore = false;
									} else {
										isRightCanLoadMore = true;
									}
									message.what = LOAD_DATABASE_IMAGE_COMPLETE;
								} else {
									mNormalCollections = infos;
									if (infos != null && infos.size() < 15) {
										isLeftCanLoadMore = false;
									} else {
										isLeftCanLoadMore = true;
									}
									message.what = LOAD_DATABASE_NORMAL_COMPLETE;
								}
								handler.sendMessage(message);

							}
						});
			}
			break;
		case MY_MESSAGE:
			if (section == LEFT_SECTION) { // 点击左边
				if (mLoadingDialog != null) {
					mLoadingDialog.showFullDialog();
				}
				ArticleConfig config = new ArticleConfig();
				config.setType(RequestType.MY_MESSAGE);
				config.setAccessToken(mAccessTokenString);
				ArticleManager.getInstence().getRelativeComment(config,new SelfCommentCallback() {
					
					@Override
					public void onSelfCommentCallback(List<SingleComment> comments, String error) {
						if (mLoadingDialog != null) {
							mLoadingDialog.dismiss();
						}
						if (comments != null) {
							mShowComments = CommentHelper
									.covertSelfDataWithTag(comments);
							if (comments.size() <15) {
								isLeftCanLoadMore = false;
							}else {
								isLeftCanLoadMore = true;
							}
							Message message = new Message();
							message.what = LOAD_COMMENT_COMPLETE;
							handler.sendMessage(message);
						}else {
							Toast.makeText(
									MessageCenterActivity.this,
									"当前网络状况较差，请检查后重试", Toast.LENGTH_SHORT)
									.show();
						}
					}
				});
				
				if (mCommentNotice > 0) {
					ArticleConfig clearConfig = new ArticleConfig();
					clearConfig.setType(RequestType.CLEAR_NOTICE);
					clearConfig.setCustomFlag(true);
					clearConfig.setAccessToken(mAccessTokenString);
					ArticleManager.getInstence().clearMessageNotice(clearConfig,new UserInfoCallback() {
						
						@Override
						public void onUserinfoCallback(UserInfo info, String failMsg) {
							if (info != null) {
								SharedPreferences.Editor mEditor = mConfigShare
										.edit();
								UserConfigUtile.storeSelfConfigToNative(
										mEditor, info);
							} 
						}
					});
				}

			} else { // 点击右边
				if (myMessageBeans != null) {

				} else {
					if (mLoadingDialog != null) {
						mLoadingDialog.showFullDialog();
					}
					ArticleConfig config = new ArticleConfig();
					config.setType(RequestType.SYS_MESSAGE);
					config.setAccessToken(mAccessTokenString);
					ArticleManager.getInstence().getRelativeMessage(config,
							new MessageInfoCallback() {

								@Override
								public void onMyMessageCallback(
										List<MyMessageBean> msgs) {
									Log.e("msgReturn==>", "==back");
									myMessageBeans = (ArrayList<MyMessageBean>) msgs;
									if (myMessageBeans != null) {

										if (myMessageBeans.size() <15) {
											isRightCanLoadMore = false;
										}else {
											isRightCanLoadMore = true;
										}
										Log.e("msgReturn==>",
												myMessageBeans.size() + "");
										Message message = new Message();
										message.what = LOAD_MESSAGE_COMPLETE;
										handler.sendMessage(message);
									} else {
										if (mLoadingDialog != null) {
											mLoadingDialog.dismiss();
										}
										Toast.makeText(
												MessageCenterActivity.this,
												"获取数据失败...", Toast.LENGTH_SHORT)
												.show();
									}
								}
							});
				}
				
				if (mMessageNotice > 0) {
					ArticleConfig clearConfig = new ArticleConfig();
					clearConfig.setType(RequestType.CLEAR_NOTICE);
					clearConfig.setCustomFlag(false);
					clearConfig.setAccessToken(mAccessTokenString);
					ArticleManager.getInstence().clearMessageNotice(clearConfig,new UserInfoCallback() {
						
						@Override
						public void onUserinfoCallback(UserInfo info, String failMsg) {
							if (info != null) {
								SharedPreferences.Editor mEditor = mConfigShare
										.edit();
								UserConfigUtile.storeSelfConfigToNative(
										mEditor, info);
							} 
						}
					});
				}

			}

			break;

		default:
			break;
		}

	}

	/**
	 * 同步网络获取的收藏文章的本地收藏状态
	 * @param infos
	 */
	private void SyncCollectionState(List<ArticleInfo> infos) {
		List<Long> ids = new ArrayList<Long>();
		for (int i = 0; i < infos.size(); i++) {
			if (!infos.get(i).isCollection()) {
				ids.add(infos.get(i).getArticle_id());
			}
		}
		if (ids.size()>0) {
			ArticleManager.getInstence().setNetCollectionsToLacal(ids);
		}
	}

	@Override
	public void onClick(View v) {

	}

	private void loadMore() {

		Log.e("MESSAGE==", "LOADMORE");

		switch (mActivityType) {
		case MY_COLLECT:
			if (mAccessTokenString != null) { // 登录状态下，收藏文章从服务器上面获取
				Log.e("COLLECT==>", "TOKEN" + mAccessTokenString);
				if (mNowShowSection == LEFT_SECTION) { // 登录状态下，普通文章的获取更多
					ArticleConfig collectionConfig = new ArticleConfig();
					collectionConfig.setType(RequestType.COLLECTION);
					collectionConfig.setAccessToken(mAccessTokenString);
					collectionConfig.setCustomFlag(false);
					mNormalCollections.get(mNormalCollections.size() - 1)
							.getArticle_id();
					collectionConfig.setMaxId(mNormalCollections.get(
							mNormalCollections.size() - 1).getArticle_id());
					ArticleManager.getInstence().getArticleCollection(
							collectionConfig, new ArticleInfoCallback() {

								@Override
								public void onArticleInfoCallback(
										List<ArticleInfo> infos,
										RequestType type) {

									if (infos != null) {
										if (infos.size() < PER_PAGE_COUNT) {
											isLeftCanLoadMore = false;
										} else {
											leftPage = leftPage + 1;
											isLeftCanLoadMore = true;
										}
										mNormalCollections.addAll(infos);
										Message message = new Message();
										message.arg1 = 2;
										message.what = LOAD_COLLECT_NORMAL_COMPLETE;
										handler.sendMessage(message);
										SyncCollectionState(infos);
									} else {
										Message message = new Message();
										message.arg1 = 2;
										message.what = LOAD_COLLECT_NORMAL_COMPLETE;
										handler.sendMessage(message);
									}
								}
							});

				} else {// 登录状态下，图片文章的收藏获取
					ArticleConfig collectionConfig = new ArticleConfig();
					collectionConfig.setType(RequestType.COLLECTION);
					collectionConfig.setAccessToken(mAccessTokenString);
					collectionConfig.setCustomFlag(true);
					collectionConfig.setPageCount(rightPage + 1);
					ArticleManager.getInstence().getArticleCollection(
							collectionConfig, new ArticleInfoCallback() {

								@Override
								public void onArticleInfoCallback(
										List<ArticleInfo> infos,
										RequestType type) {
									if (infos != null) {
										if (infos.size() < PER_PAGE_COUNT) {
											isRightCanLoadMore = false;
										} else {
											rightPage = rightPage + 1;
											isRightCanLoadMore = true;
										}

										mImageCollections.addAll(infos);
										Message message = new Message();
										message.arg1 = 2;
										message.what = LOAD_COLLECT_IMAGE_COMPLETE;
										handler.sendMessage(message);
										
										SyncCollectionState(infos);
									} else {

										Message message = new Message();
										message.arg1 = 2;
										message.what = LOAD_COLLECT_IMAGE_COMPLETE;
										handler.sendMessage(message);
									}

								}
							});

				}

			} else { // 非登录状态下，收藏文章从本地获取
				final ArticleConfig collectionConfig = new ArticleConfig();
				collectionConfig.setType(RequestType.COLLECTION);
				collectionConfig.setLocalArticle(true);
				if (mNowShowSection == LEFT_SECTION) {
					collectionConfig.setCustomFlag(false);
					collectionConfig.setPageCount(leftPage);
				} else {
					collectionConfig.setCustomFlag(true);
					collectionConfig.setPageCount(rightPage);
				}
				ArticleManager.getInstence().getArticleCollection(
						collectionConfig, new ArticleInfoCallback() {

							@Override
							public void onArticleInfoCallback(
									List<ArticleInfo> infos, RequestType type) {
								Message message = new Message();
								if (collectionConfig.isCustomFlag()) {
									mImageCollections = infos;
									if (infos != null && infos.size() < 15) {
										isRightCanLoadMore = false;
									} else {
										rightPage = rightPage + 1;
										isRightCanLoadMore = true;
									}
									message.what = LOAD_DATABASE_IMAGE_COMPLETE;
								} else {
									mNormalCollections = infos;
									if (infos != null && infos.size() < 15) {
										isLeftCanLoadMore = false;
									} else {
										leftPage = leftPage + 1;
										isLeftCanLoadMore = true;
									}
									message.what = LOAD_DATABASE_NORMAL_COMPLETE;
								}
								handler.sendMessage(message);

							}
						});
			}
			break;
		case MY_MESSAGE:
			if (mNowShowSection == LEFT_SECTION) { // 点击左边
				ArticleConfig config = new ArticleConfig();
				config.setType(RequestType.MY_MESSAGE);
				config.setAccessToken(mAccessTokenString);
				config.setPageCount(rightPage + 1);
				ArticleManager.getInstence().getRelativeComment(config, new SelfCommentCallback() {
					
					@Override
					public void onSelfCommentCallback(List<SingleComment> comments, String error) {
						if (comments != null) {
							returnComments = CommentHelper
									.covertSelfDataWithTag(comments);
							if (comments.size() <15) {
								isLeftCanLoadMore = false;
							}else {
								isLeftCanLoadMore = true;
							}
							
							Message message = new Message();
							message.what = LOAD_MORE_COMMENT_COMPLETE;
							handler.sendMessage(message);
						}
						
					}
				});
				
			} else { // 点击右边
				ArticleConfig config = new ArticleConfig();
				config.setType(RequestType.SYS_MESSAGE);
				config.setAccessToken(mAccessTokenString);
				config.setPageCount(rightPage + 1);
				ArticleManager.getInstence().getRelativeMessage(config,
						new MessageInfoCallback() {

							@Override
							public void onMyMessageCallback(
									List<MyMessageBean> msgs) {
								Log.e("msgReturn==>", "==back");
								myMessageBeans
										.addAll((ArrayList<MyMessageBean>) msgs);

								if (msgs.size() < PER_PAGE_COUNT) {
									isRightCanLoadMore = false;
								} else {
									Log.e("MESSAGE==", "CAN LOAD MORE");
									rightPage = rightPage + 1;
									mContentListview.setAutoLoadMore(true);
									isRightCanLoadMore = true;
									// isCanLoadMore = true;
									// mContentListview.setCanLoadMore(isCanLoadMore);
								}
								Log.e("msgReturn==>", myMessageBeans.size()
										+ "==" + rightPage);
								Message message = new Message();
								message.what = LOAD_MORE_MESSAGE_COMPLETE;
								handler.sendMessage(message);
							}
						});

			}

			break;

		default:
			break;
		}

	}

	@Override
	protected void onDestroy() {
		ThemeChangeManager.getInstance().unregisterThemeChangeListener(
				mThemeChangeListener);
		super.onDestroy();
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
