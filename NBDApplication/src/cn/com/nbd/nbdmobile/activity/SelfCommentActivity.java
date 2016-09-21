package cn.com.nbd.nbdmobile.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.SingleComment;
import com.nbd.article.bean.UserInfo;
import com.nbd.article.bean.commentBean;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.CommentCallback;
import com.nbd.article.managercallback.SelfCommentCallback;
import com.nbd.article.managercallback.StringInfoCallback;
import com.nbd.article.managercallback.UserInfoCallback;
import com.nbd.network.bean.CommentHandleType;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.adapter.CommentAdapter;
import cn.com.nbd.nbdmobile.adapter.CommentAdapter.commentClickType;
import cn.com.nbd.nbdmobile.adapter.CommentAdapter.onItemFunctionClick;
import cn.com.nbd.nbdmobile.adapter.SelfCommentAdapter;
import cn.com.nbd.nbdmobile.manager.OnThemeChangeListener;
import cn.com.nbd.nbdmobile.manager.ThemeChangeManager;
import cn.com.nbd.nbdmobile.utility.ArticleHandleType;
import cn.com.nbd.nbdmobile.utility.ArticleJumpUtil;
import cn.com.nbd.nbdmobile.utility.CommentHelper;
import cn.com.nbd.nbdmobile.utility.UserConfigUtile;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused.OnLoadMoreListener;
import cn.com.nbd.nbdmobile.widget.CommentEditDialog;
import cn.com.nbd.nbdmobile.widget.CommentEditDialog.onCommentSendInterface;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;
import cn.com.nbd.nbdmobile.widget.NbdAlrltDialog;
import cn.com.nbd.nbdmobile.widget.NbdAlrltDialog.onDialogChooseClick;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 我的评论相关的内容
 * 
 * @author he
 * 
 */
public class SelfCommentActivity extends Activity {

	private TextView mTitleText;
	private ImageView mBackBtn;
	private WithoutSectionCustomListViewUnused mListView;
	private RelativeLayout mMainLayout;
	private TextView mCover;

	private List<SingleComment> mShowComments = new ArrayList<SingleComment>();
	private List<SingleComment> mMoreComments = new ArrayList<SingleComment>();

	private int page = 1;
	private boolean isCanLoadMore = true;

	private SelfCommentAdapter mAdapter;

	private LoadingDialog mLoadingDialog;
	private String mAccessToken;

	private SharedPreferences mNativeShare;
	private SharedPreferences mConfigShare;

	private OnThemeChangeListener mThemeChangeListener;

	private boolean isDayTheme;

	public final static int LOAD_DATA_INIT = 0;
	public final static int LOAD_DATA_LOADMORE = 1;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mAdapter == null) {
				initAdapter();
			}
			switch (msg.what) {
			case LOAD_DATA_LOADMORE:
				if (mMoreComments != null) {
					mShowComments.addAll(mMoreComments);
					mAdapter.notifyDataSetChanged();
					if (!isCanLoadMore) {
						mListView.setCanLoadMore(false);
					}
					mListView.onLoadMoreComplete();
				}
				// 当前显示的界面和异步数据请求返回的数据一致，更新数据显示
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
		mConfigShare = this
				.getSharedPreferences("AppConfig", this.MODE_PRIVATE);
		mNativeShare = this.getSharedPreferences("NativeSetting",
				this.MODE_PRIVATE);
		isDayTheme = mNativeShare.getBoolean("theme", true);
		if (isDayTheme) {
			setTheme(R.style.DayTheme);
		} else {
			setTheme(R.style.NightTheme);
		}
		setContentView(R.layout.activity_self_comment);

		mAccessToken = mConfigShare.getString("accessToken", "");
		mLoadingDialog = new LoadingDialog(SelfCommentActivity.this,
				R.style.loading_dialog, "加载中...");

		initUi();

		initData();

		setListener();

	}

	private void setListener() {

		mThemeChangeListener = new OnThemeChangeListener() {

			@Override
			public void onNightThemeChange(boolean isNowTheme) {
				showThemeUi(isNowTheme);

			}
		};

		mBackBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				SelfCommentActivity.this.finish();

			}
		});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Log.e("ITEM CLICK", "POSITION==" + position);

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
							SelfCommentActivity.this, article, "评论文章",false);
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
				 mMainLayout.setBackgroundColor(SelfCommentActivity.this
				 .getResources()
				 .getColor(R.color.day_section_background));
				 mCover.setBackgroundColor(SelfCommentActivity.this
				 .getResources().getColor(R.color.day_cover));

			} else {
				 mMainLayout.setBackgroundColor(SelfCommentActivity.this
				 .getResources().getColor(
				 R.color.night_common_background));
				 mCover.setBackgroundColor(SelfCommentActivity.this
				 .getResources().getColor(R.color.night_cover));

			}
//			 if (mAdapter != null) {
//			 mAdapter.changeTheme(isDayTheme);
//			 mAdapter.notifyDataSetChanged();
//			 }
//			 if (mListView != null) {
//				 mListView.setTheme(isDayTheme);
//			 }
		}
	}

	private void initUi() {

		mTitleText = (TextView) findViewById(R.id.comment_title);
		mBackBtn = (ImageView) findViewById(R.id.comment_back_btn);
		mListView = (WithoutSectionCustomListViewUnused) findViewById(R.id.comment_listview);
		
		mMainLayout = (RelativeLayout) findViewById(R.id.comment_layout);
		mCover = (TextView) findViewById(R.id.comment_cover);

		mListView.setTheme(true);

	}

	private void initData() {

		ArticleConfig config = new ArticleConfig();
		config.setType(RequestType.SELF_COMMENT);
		config.setPageCount(page);
		config.setAccessToken(mAccessToken);
		if (mLoadingDialog != null) {
			mLoadingDialog.showFullDialog();
		}

		ArticleManager.getInstence().getSelfComment(config,
				new SelfCommentCallback() {

					@Override
					public void onSelfCommentCallback(
							List<SingleComment> comments, String error) {
						if (mLoadingDialog != null) {
							mLoadingDialog.dismiss();
						}
						if (comments != null) {
							mShowComments = CommentHelper
									.covertSelfDataWithTag(comments);

							if (comments.size() < 15) {
								isCanLoadMore = false;
							} else {
								isCanLoadMore = true;
							}

							handler.sendEmptyMessage(0);

						} else if (error != null) {
							Toast.makeText(SelfCommentActivity.this, error,
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(SelfCommentActivity.this,
									"获取数据失败...", Toast.LENGTH_SHORT).show();
						}
					}
				});

	}

	private void initAdapter() {
		if (mAdapter == null) {
			mAdapter = new SelfCommentAdapter(SelfCommentActivity.this,
					mShowComments, true);
		}

		mListView.setAdapter(mAdapter);

		mListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMoreData();
			}
		});

		mListView.setCanLoadMore(isCanLoadMore);

	}

	private void loadMoreData() {
		
		page = page +1;
		
		ArticleConfig config = new ArticleConfig();
		config.setType(RequestType.SELF_COMMENT);
		config.setPageCount(page);
		config.setAccessToken(mAccessToken);
		ArticleManager.getInstence().getSelfComment(config,new SelfCommentCallback() {
			
			@Override
			public void onSelfCommentCallback(List<SingleComment> comments, String error) {
				if (comments != null) {
					if (comments.size() != 0) {
						mMoreComments = CommentHelper
								.covertSelfDataWithTag(comments);
						int showSize = comments.size();
						Log.e("result", showSize + "");
						if (showSize < 15) {
							isCanLoadMore = false;
						} else {
							isCanLoadMore = true;
						}
					} else {
						isCanLoadMore = false;
					}
					Message message = new Message();
					message.what = LOAD_DATA_LOADMORE;
					handler.sendMessage(message);
				}
			}
		});
//		config.setType(RequestType.SELF_COMMENT);
//		config.setPageCount(page);
//		config.setAccessToken(mAccessToken);
//		if (mShowComments != null && mShowComments.size() > 0) {
//			config.setMaxId(mShowComments.get(mShowComments.size() - 1).getId());
//		}
//		ArticleManager.getInstence().getComment(config, new CommentCallback() {
//
//			@Override
//			public void onCommentAllCallback(List<commentBean> comments) {
//
//				if (comments != null) {
//					if (comments.size() != 0) {
//						mMoreComments = CommentHelper
//								.covertNewDatas2Single(comments);
//						int showSize = CommentHelper.getCommentCount(comments);
//						Log.e("result", showSize + "");
//						if (showSize < 15) {
//							isCanLoadMore = false;
//						} else {
//							isCanLoadMore = true;
//						}
//					} else {
//						isCanLoadMore = false;
//					}
//					Message message = new Message();
//					message.what = LOAD_DATA_LOADMORE;
//					handler.sendMessage(message);
//				}
//			}
//		});

	}

	@Override
	protected void onDestroy() {
		ThemeChangeManager.getInstance().unregisterThemeChangeListener(
				mThemeChangeListener);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.e("Activity", "BACK");
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
