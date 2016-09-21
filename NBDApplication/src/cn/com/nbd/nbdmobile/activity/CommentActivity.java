package cn.com.nbd.nbdmobile.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.SingleComment;
import com.nbd.article.bean.UserInfo;
import com.nbd.article.bean.commentBean;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.CommentCallback;
import com.nbd.article.managercallback.StringInfoCallback;
import com.nbd.article.managercallback.UserInfoCallback;
import com.nbd.network.bean.CommentHandleType;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.adapter.CommentAdapter;
import cn.com.nbd.nbdmobile.adapter.CommentAdapter.commentClickType;
import cn.com.nbd.nbdmobile.adapter.CommentAdapter.onItemFunctionClick;
import cn.com.nbd.nbdmobile.utility.ArticleHandleType;
import cn.com.nbd.nbdmobile.utility.CommentHelper;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.utility.UserConfigUtile;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused.OnLoadMoreListener;
import cn.com.nbd.nbdmobile.widget.CommentEditDialog;
import cn.com.nbd.nbdmobile.widget.CommentEditDialog.onCommentSendInterface;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;
import cn.com.nbd.nbdmobile.widget.NbdAlrltDialog;
import cn.com.nbd.nbdmobile.widget.NbdAlrltDialog.onDialogChooseClick;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CommentActivity extends Activity {

	private TextView mTitleText;
	private ImageView mBackBtn;
	private WithoutSectionCustomListViewUnused mListView;
	private EditText mInputEdit;

	private List<SingleComment> mShowComments = new ArrayList<SingleComment>();
	private List<SingleComment> mMoreComments = new ArrayList<SingleComment>();

	private int mMaxId;
	private boolean isCanLoadMore;

	private CommentAdapter mAdapter;

	private CommentEditDialog mCommentDialog;
	private LoadingDialog mLoadingDialog;
	private NbdAlrltDialog mAlrltDialog;
	private String mAccessToken;
	private long mArticleId;
	private long mCommentNum;
	private boolean isImage;

	private SharedPreferences mNativeShare;
	private SharedPreferences mConfigShare;

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
		setContentView(R.layout.activity_comment);

		mAccessToken = mConfigShare.getString("accessToken", "");

		Intent i = getIntent();
		isImage = i.getBooleanExtra("isImage", false);
		mArticleId = i.getLongExtra("article_id", 0);
		mCommentNum = i.getLongExtra("comment_num", 0);

		mLoadingDialog = new LoadingDialog(CommentActivity.this,
				R.style.loading_dialog, "加载中...");
		mAlrltDialog = new NbdAlrltDialog(CommentActivity.this,
				R.style.loading_dialog, "确定举报该评论？");

		initUi();

		initData();

		setListener();

	}

	private void setListener() {

		mBackBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CommentActivity.this.finish();
			}
		});

		mInputEdit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// if (mAccessToken == null || mAccessToken.equals("")) {
				// Toast.makeText(CommentActivity.this, "请先登录账号...",
				// Toast.LENGTH_SHORT).show();
				// } else 
				if (mCommentDialog == null) {
					mCommentDialog = new CommentEditDialog(
							CommentActivity.this, R.style.loading_dialog);
					mCommentDialog
							.setCommentSendListener(new onCommentSendInterface() {

								@Override
								public void onCommentSend(String comment) {
									handleCommentAction(comment, -1);

								}
							});
				}
				mCommentDialog.showFullDialog();
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						mCommentDialog.showKeyboard();
					}
				}, 200);
				// }
			}
		});
	}

	private void initUi() {

		mTitleText = (TextView) findViewById(R.id.comment_title);
		mBackBtn = (ImageView) findViewById(R.id.comment_back_btn);
		mListView = (WithoutSectionCustomListViewUnused) findViewById(R.id.comment_listview);
		mInputEdit = (EditText) findViewById(R.id.news_detail_comment_edit);

		mListView.setTheme(isDayTheme);

		mTitleText.setText(mCommentNum + "条评论");
		
		Rect r = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
		
		int screenHeight = this.getWindow().getDecorView().getRootView().getHeight();
		int keyboardHeight = screenHeight - r.bottom;
		
		Log.e("KEY BOARD HEIGHT-->", keyboardHeight+"");

	}

	private void initData() {

		ArticleConfig config = new ArticleConfig();
		config.setType(RequestType.GET_COMMENT);
		config.setArticleId(mArticleId);
		config.setCommentType(0);
		config.setAccessToken(mAccessToken);
		config.setCustomFlag(isImage);
		if (mLoadingDialog != null) {
			mLoadingDialog.showFullDialog();
		}
		ArticleManager.getInstence().getComment(config, new CommentCallback() {

			@Override
			public void onCommentAllCallback(List<commentBean> comments) {
				if (mLoadingDialog != null) {
					mLoadingDialog.dismiss();
				}

				if (comments != null) {
					List<SingleComment> showList = CommentHelper
							.covertDatas2Single(comments);
					Log.e("result", showList.size() + "");
					for (int i = 0; i < showList.size(); i++) {
						Log.e("result", showList.get(i).getCommentType() + "");
					}
					mShowComments = showList;
					initAdapter();

				} else {
					Toast.makeText(CommentActivity.this, "加载失败",
							Toast.LENGTH_SHORT).show();
				}

			}
		});

	}

	private void initAdapter() {
		if (mAdapter == null) {
			mAdapter = new CommentAdapter(CommentActivity.this, mShowComments,
					true);
		}

		mAdapter.setOnItemFunctionClickListener(new onItemFunctionClick() {

			@Override
			public void onFunctionChoosed(commentClickType type,
					SingleComment comment, int position) {

				// if (mAccessToken == null || mAccessToken.equals("")) {
				// Toast.makeText(CommentActivity.this, "请先登录账号...",
				// Toast.LENGTH_SHORT).show();
				// } else {
				dealItemHandleAction(type, comment, position);
				// }

			}
		});

		mListView.setAdapter(mAdapter);

		mListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				loadMoreData();
			}
		});

	}

	private void loadMoreData() {
		ArticleConfig config = new ArticleConfig();
		config.setType(RequestType.GET_COMMENT);
		config.setArticleId(mArticleId);
		config.setCommentType(1);
		config.setAccessToken(mAccessToken);
		config.setCustomFlag(isImage);
		if (mShowComments != null && mShowComments.size() > 0) {
			config.setMaxId(mShowComments.get(mShowComments.size() - 1).getId());
		}
		ArticleManager.getInstence().getComment(config, new CommentCallback() {

			@Override
			public void onCommentAllCallback(List<commentBean> comments) {

				if (comments != null) {
					if (comments.size() != 0) {
						mMoreComments = CommentHelper
								.covertNewDatas2Single(comments);
						int showSize = CommentHelper.getCommentCount(comments);
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

	}

	/**
	 * 处理评论里面的事件，和网络进行交互
	 * 
	 * @param type
	 * @param comment
	 */
	private void dealItemHandleAction(commentClickType type,
			SingleComment comment, int position) {
		if (comment != null) {
			Log.e("COMMENT==>",
					"ID" + comment.getId() + "CONTENT" + comment.getBody());
			// ArticleConfig config = new ArticleConfig();
			// config.setType(RequestType.COMMENT);
			// config.setArticleId(997123);
			// config.setcom
			switch (type) {
			case GOOD:
				if (mAccessToken == null || mAccessToken.equals("")) {
					Toast.makeText(CommentActivity.this, "请先登录账号...",
							Toast.LENGTH_SHORT).show();
				} else {
					dealSupport(comment, position);
				}
				break;
			case REPLY:
				final int parentId = comment.getId();
				if (mCommentDialog == null) {
					mCommentDialog = new CommentEditDialog(
							CommentActivity.this, R.style.loading_dialog);
					mCommentDialog
							.setCommentSendListener(new onCommentSendInterface() {

								@Override
								public void onCommentSend(String comment) {
									handleCommentAction(comment, parentId);
								}
							});
				}
				mCommentDialog.showFullDialog();
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						mCommentDialog.showKeyboard();
					}
				}, 200);

				break;
			// case SHARE:
			//
			// break;
			case ALRT:
				dealReport(comment);
				break;

			default:
				break;
			}
		}

	}

	/**
	 * 处理评论的事件
	 * 
	 * @param comment
	 *            评论的内容
	 * @param parentId
	 *            评论的父ID，可选
	 */
	private void handleCommentAction(String comment, int parentId) {

		MobclickAgent.onEvent(CommentActivity.this, UmenAnalytics.ARTICLE_REVIEW);
		ArticleConfig config = new ArticleConfig();
		config.setType(RequestType.COMMENT);
		// config.setArticleId(997123);
		config.setArticleId(mArticleId);
		config.setCustomString(comment);
		config.setAccessToken(mAccessToken);
		config.setCustomFlag(isImage);
		if (parentId != -1) {
			config.setHandleType(CommentHandleType.CHILD_REPLY);
			config.setCommentId(parentId);
		} else {
			config.setHandleType(CommentHandleType.REPLY);
		}

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
					Toast.makeText(CommentActivity.this, "评论成功",
							Toast.LENGTH_SHORT).show();
					if (info.getAccess_token() != null) {
						SharedPreferences.Editor mEditor = mConfigShare.edit();
						UserConfigUtile.storeSelfConfigToNative(mEditor, info);
					}
				} else if (failMsg != null) {
					Toast.makeText(CommentActivity.this, failMsg,
							Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(CommentActivity.this, "评论失败",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	/**
	 * 处理点赞，不需要传入其他参数
	 * 
	 * @param type
	 * @param id
	 */
	private void dealSupport(final SingleComment comment, final int position) {
		MobclickAgent.onEvent(CommentActivity.this, UmenAnalytics.REVIEW_GOOD);
		ArticleConfig config = new ArticleConfig();
		config.setType(RequestType.COMMENT);
		config.setArticleId(mArticleId);
		config.setCommentId(comment.getId());
		config.setCustomFlag(isImage);
		config.setAccessToken(mAccessToken);
		if (comment.isSupported()) {
			config.setHandleType(CommentHandleType.UN_SUPPORT);
		} else {
			config.setHandleType(CommentHandleType.SUPPORT);
		}

		ArticleManager.getInstence().handleComment(config,
				new StringInfoCallback() {

					@Override
					public void onStringDataCallback(String s, boolean isSuccess) {
						if (isSuccess) {
							Log.e("SUPPORT==",
									"BEFORE==" + comment.getSupport_num());

							if (comment.isSupported()) {
								comment.setSupported(false);
								comment.setSupport_num(comment.getSupport_num() - 1);
							} else {
								comment.setSupported(true);
								comment.setSupport_num(comment.getSupport_num() + 1);
							}

							Log.e("SUPPORT==",
									"AFTER==" + comment.getSupport_num());
							mAdapter.changeSupportIcon(comment, position);

						} else {

						}

					}
				});
	}

	/**
	 * 处理举报，不需要传入其他参数
	 * 
	 * @param type
	 * @param id
	 */
	private void dealReport(final SingleComment comment) {
		MobclickAgent.onEvent(CommentActivity.this, UmenAnalytics.REVIEW_REPORT);
		if (mAlrltDialog == null) {
			mAlrltDialog = new NbdAlrltDialog(CommentActivity.this,
					R.style.loading_dialog, "确定举报该评论？");
		}
		mAlrltDialog.setOnBtnClickListener(new onDialogChooseClick() {

			@Override
			public void onModeTypeClick(ArticleHandleType type) {
				if (type == ArticleHandleType.OK) {
					if (mLoadingDialog != null) {
						mLoadingDialog.showFullDialog();
					}

					ArticleConfig config = new ArticleConfig();
					config.setType(RequestType.COMMENT);
					config.setArticleId(mArticleId);
					config.setCommentId(comment.getId());
					config.setHandleType(CommentHandleType.REPORT);
					config.setAccessToken(mAccessToken);
					config.setCustomFlag(isImage);
					ArticleManager.getInstence().handleComment(config,
							new StringInfoCallback() {

								@Override
								public void onStringDataCallback(String s,
										boolean isSuccess) {
									if (mLoadingDialog != null) {
										mLoadingDialog.dismiss();
									}
									if (isSuccess) {
										Toast.makeText(CommentActivity.this,
												"举报成功", Toast.LENGTH_SHORT)
												.show();
									} else if (s != null) {
										Toast.makeText(CommentActivity.this, s,
												Toast.LENGTH_SHORT).show();
									} else {
										Toast.makeText(CommentActivity.this,
												"举报失败", Toast.LENGTH_SHORT)
												.show();
									}
								}
							});
				}

			}
		});
		mAlrltDialog.showFullDialog();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.e("Activity", "BACK");
			CommentActivity.this.finish();
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
