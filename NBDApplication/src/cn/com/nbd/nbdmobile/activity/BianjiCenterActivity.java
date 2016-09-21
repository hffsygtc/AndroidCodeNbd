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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.R.style;
import cn.com.nbd.nbdmobile.adapter.NewsListAdapter;
import cn.com.nbd.nbdmobile.adapter.SearchResultAdapter;
import cn.com.nbd.nbdmobile.adapter.NewsListAdapter.OnNewsClickListener;
import cn.com.nbd.nbdmobile.manager.OnThemeChangeListener;
import cn.com.nbd.nbdmobile.manager.ThemeChangeManager;
import cn.com.nbd.nbdmobile.utility.ArticleJumpUtil;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused.OnLoadMoreListener;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.ArticleInfoCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

public class BianjiCenterActivity extends Activity implements OnClickListener,
		OnNewsClickListener {

	private RelativeLayout mMainLayout;
	private TextView mCover;

	private WithoutSectionCustomListViewUnused mListView;
	private ImageView mBackBtn;
	private TextView mTitleText;

	private String mAccessTokenString;
	private int mPagePosition = 1;
	
	private int mColumn;

	// private List<ArticleInfo> mArticleInfos;
	// private SearchResultAdapter mAdapter;

	private ArrayList<ArticleInfo> scrollList = new ArrayList<ArticleInfo>(); // 头部轮播的列表
	private ArrayList<ArticleInfo> newsList = new ArrayList<ArticleInfo>(); // 用于传入adapter的新闻内容列表
	private ArrayList<ArticleInfo> moreList = new ArrayList<ArticleInfo>(); // 上拉加载更多时获取的数据

	private NewsListAdapter mAdapter; // 处理停靠栏的适配器

	private LoadingDialog mDialog;

	private SharedPreferences mNativeShare;
	private boolean isDayTheme;
	private boolean isLBDataReturn; // 轮播和内容的数据均返回了再刷新数据
	private boolean isListDataReturn; // 轮播和内容的数据均返回了再刷新数据
	private boolean isCanloamore = true;

	private OnThemeChangeListener mThemeListener;

	public final static int LOAD_DATA_REFRESH = 1;
	public final static int LOAD_DATA_LOADMORE = 2;

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mAdapter == null) {
				initAdapter();
			}
			switch (msg.what) {
			case LOAD_DATA_REFRESH:
					mListView.setCanLoadMore(isCanloamore);
					if (moreList != null) {
						newsList = moreList;
					}
					mAdapter.setDataChange(scrollList, null, newsList);
					mAdapter.notifyDataSetChanged();
				break;
			case LOAD_DATA_LOADMORE:
				mListView.setCanLoadMore(isCanloamore);
				if (moreList != null) {
					newsList.addAll(moreList);
					mAdapter.setDataChange(scrollList, null, newsList);
					mAdapter.notifyDataSetChanged();
				}
				mListView.onLoadMoreComplete();
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

		setContentView(R.layout.activity_with_pull_listview);

		Intent i = getIntent();
		mColumn = i.getIntExtra("column", 0);
		mAccessTokenString = i.getStringExtra("token");

		Log.e("BIANJI TEST -->", ""+mColumn);
		mDialog = new LoadingDialog(this, style.loading_dialog, "加载中...");

		initUi();

		setListener();

		initData(mPagePosition, false);
		// initAdapter();
	}

	/**
	 * 设置页面中的事件监听
	 */
	private void setListener() {

		mThemeListener = new OnThemeChangeListener() {

			@Override
			public void onNightThemeChange(boolean isNowTheme) {
				Log.e("CHANGE-THEME", "search-result--" + isNowTheme);

				changeTheme(isNowTheme);

			}
		};

		mBackBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				BianjiCenterActivity.this.finish();

			}
		});

		mListView.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
//				mPagePosition = mPagePosition + 1;
				initData(mPagePosition, true);

			}
		});

		ThemeChangeManager.getInstance().registerThemeChangeListener(
				mThemeListener);

	}

	/**
	 * 用于当节目处于栈内时被在搜索文章详情页面被更改了主题时切换主题模式
	 * 
	 * @param isNowTheme
	 */
	private void changeTheme(boolean isNowTheme) {
		if (isNowTheme & isDayTheme) { // 如果当前主题和更改的主题相同则不动

		} else {
			isDayTheme = isNowTheme;
			if (isDayTheme) { // 日间主题的配色
				mMainLayout.setBackgroundColor(BianjiCenterActivity.this
						.getResources()
						.getColor(R.color.day_section_background));
				mCover.setBackgroundColor(BianjiCenterActivity.this
						.getResources().getColor(R.color.day_cover));

			} else { // 夜间主题的配色
				mMainLayout.setBackgroundColor(BianjiCenterActivity.this
						.getResources().getColor(
								R.color.night_common_background));
				mCover.setBackgroundColor(BianjiCenterActivity.this
						.getResources().getColor(R.color.night_cover));

			}

			// if (mAdapter != null) {
			// mAdapter.changeTheme(isDayTheme);
			// mAdapter.notifyDataSetChanged();
			// }

			if (mListView != null) {
				mListView.setTheme(isDayTheme);
			}
		}
	}

	/**
	 * 初始化数据，传递来的内容数据，图片链接集合
	 */
	private void initData(int page, final boolean isLoadMore) {
		if (page == 1) {
			mDialog.showFullDialog();
		}
		
		ArticleConfig config = new ArticleConfig();
		config.setType(RequestType.TEST_ACCOUNT);
		config.setAccessToken(mAccessTokenString);
		config.setPageCount(mPagePosition);
		config.setPagePositon(mColumn);
		ArticleManager.getInstence().getArticleInfo(config, new ArticleInfoCallback() {
			
			@Override
			public void onArticleInfoCallback(List<ArticleInfo> infos, RequestType type) {
				if (mDialog != null) {
					mDialog.dismiss();
				}
				if (infos != null) {
					Log.e("SEARCH-RESULT", "size=>"+infos.size());
					if (infos.size() < 15) {
						isCanloamore = false;
					}else {
						isCanloamore = true;
						mPagePosition = mPagePosition +1;
					}
					Message message = new Message();
					if (isLoadMore) {
						moreList = (ArrayList<ArticleInfo>) infos;
						message.what = LOAD_DATA_LOADMORE;
					}else {
						moreList = (ArrayList<ArticleInfo>) infos;
						message.what = LOAD_DATA_REFRESH;
					}
					
					handler.sendMessage(message);
//					initAdapter();
				}else {
					Toast.makeText(BianjiCenterActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
					moreList = null;
					handler.obtainMessage(LOAD_DATA_LOADMORE).sendToTarget();
				}
				
			}
		});
		
	}

	/**
	 * 初始化适配器 界面数据显示
	 */
	private void initAdapter() {
		if (mAdapter == null) {
			mAdapter = new NewsListAdapter(BianjiCenterActivity.this,
					scrollList, null, newsList, true, false, false);
		}

		mListView.setAdapter(mAdapter);

		mAdapter.setNewsClickListener(this);

		// mListView.setAdapter(mAdapter);

	}

	/**
	 * 初始化界面控件
	 */
	private void initUi() {
		mMainLayout = (RelativeLayout) findViewById(R.id.single_activity_main_layout);
		mCover = (TextView) findViewById(R.id.single_activity_cover);
		mBackBtn = (ImageView) findViewById(R.id.single_activity_back_btn);
		mTitleText = (TextView) findViewById(R.id.single_activity_title);
		mListView = (WithoutSectionCustomListViewUnused) findViewById(R.id.single_activity_mListView);

		mTitleText.setText("测试数据中心");

		mListView.setTheme(isDayTheme);

	}

	@Override
	protected void onDestroy() {
		ThemeChangeManager.getInstance().unregisterThemeChangeListener(
				mThemeListener);
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

	@Override
	public void onNewsClick(View view, ArticleInfo article) {
		ArticleJumpUtil.jumpToArticleDetal(BianjiCenterActivity.this, article,
				"测试数据",false);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}
