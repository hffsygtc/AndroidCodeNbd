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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.nbd.nbdmobile.R.style;
import cn.com.nbd.nbdmobile.adapter.SearchResultAdapter;
import cn.com.nbd.nbdmobile.adapter.SearchResultAdapter.OnNewsClickListener;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.manager.OnThemeChangeListener;
import cn.com.nbd.nbdmobile.manager.ThemeChangeManager;
import cn.com.nbd.nbdmobile.utility.ArticleJumpUtil;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused.OnLoadMoreListener;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;

import com.nbd.article.article.ArticleType;
import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.ArticleInfoCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

/**
 * 搜索结果的列表页面
 * 
 * @author riche
 *
 */
public class SearchResultActivity extends Activity {

	
	private RelativeLayout mMainLayout;
	private TextView mCover;
	
	private WithoutSectionCustomListViewUnused mlListView;
	private ImageView mBackBtn;
	private TextView mTitleText;
	
	private String mSearchString;
	private int mPagePosition = 1;
	
	private List<ArticleInfo> mArticleInfos;
	private SearchResultAdapter mAdapter;
	
	private LoadingDialog mDialog ;
	
	private SharedPreferences mNativeShare;
	private boolean isDayTheme;
	
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
//				// 当前显示的界面和异步数据请求返回的数据一致，更新数据显示
//				mAdapter.setDataChange((ArrayList<ArticleInfo>) mArticleInfos);
//				mAdapter.notifyDataSetChanged();
				break;
			case LOAD_DATA_LOADMORE:
				mAdapter.setDataChange((ArrayList<ArticleInfo>) mArticleInfos);
				mAdapter.notifyDataSetChanged();
				mlListView.onLoadMoreComplete();
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
		}else {
			setTheme(R.style.NightTheme);
		}
		
		setContentView(R.layout.activity_with_pull_listview);
		
		Intent i = getIntent();
		mSearchString = i.getStringExtra("key"); //获取关键字
		
		Log.e("SEARCH-----", mSearchString);
		
		mDialog = new LoadingDialog(this, style.loading_dialog, "搜索中...");
		
		initUi();
		
		setListener();
		
		initData(mPagePosition,false);
//		initAdapter();
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
				SearchResultActivity.this.finish();
				
			}
		});
		
		 mlListView.setOnLoadListener(new OnLoadMoreListener() {
			
			@Override
			public void onLoadMore() {
				mPagePosition = mPagePosition + 1;
				initData(mPagePosition, true);
				
			}
		});
		 
		 ThemeChangeManager.getInstance().registerThemeChangeListener(mThemeListener);
		
	}

		/**
		 * 用于当节目处于栈内时被在搜索文章详情页面被更改了主题时切换主题模式
		 * @param isNowTheme
		 */
		private void changeTheme(boolean isNowTheme) {
			if (isNowTheme & isDayTheme) { //如果当前主题和更改的主题相同则不动
				
			}else{
				isDayTheme = isNowTheme;
				if (isDayTheme) { //日间主题的配色
					mMainLayout.setBackgroundColor(SearchResultActivity.this.getResources().getColor(R.color.day_section_background));
					mCover.setBackgroundColor(SearchResultActivity.this.getResources().getColor(R.color.day_cover));
					
					
				}else { //夜间主题的配色
					mMainLayout.setBackgroundColor(SearchResultActivity.this.getResources().getColor(R.color.night_common_background));
					mCover.setBackgroundColor(SearchResultActivity.this.getResources().getColor(R.color.night_cover));
					
				}
				
				if (mAdapter != null) {
					mAdapter.changeTheme(isDayTheme);
					mAdapter.notifyDataSetChanged();
					
				}
				
				if (mlListView != null) {
					mlListView.setTheme(isDayTheme);
				}
				
			}
			
		}
		

	/**
	 * 初始化数据，传递来的内容数据，图片链接集合
	 */
	private void initData(int page,final boolean isLoadMore) {
		if (page == 1) {
			mDialog.showFullDialog();
		}
		
		ArticleConfig config = new ArticleConfig();
		config.setType(RequestType.SEARCH);
		config.setAccessToken(mSearchString);
		config.setPageCount(mPagePosition);
		ArticleManager.getInstence().getSearchArticle(config, new ArticleInfoCallback() {
			
			@Override
			public void onArticleInfoCallback(List<ArticleInfo> infos, RequestType type) {
				if (mDialog != null) {
					mDialog.dismiss();
				}
				if (infos != null) {
					Log.e("SEARCH-RESULT", "size=>"+infos.size());
					Message message = new Message();
					if (isLoadMore) {
						mArticleInfos.addAll(infos);
						message.what = LOAD_DATA_LOADMORE;
					}else {
						mArticleInfos = infos;
						message.what = LOAD_DATA_REFRESH;
					}
					
					handler.sendMessage(message);
//					initAdapter();
				}else {
					Toast.makeText(SearchResultActivity.this, "数据加载失败", Toast.LENGTH_SHORT).show();
				}
				
			}
		});
		
	}

	/**
	 * 初始化适配器 界面数据显示
	 */
	private void initAdapter() {
		
		if (mAdapter == null) {
			mAdapter = new SearchResultAdapter(SearchResultActivity.this, (ArrayList<ArticleInfo>) mArticleInfos,false,isDayTheme);
		}
		mAdapter.setNewsClickListener(new OnNewsClickListener() {
			
			@Override
			public void onNewsClick(View view, ArticleInfo article) {
				if (article != null) {
					
					ArticleJumpUtil.jumpToArticleDetal(SearchResultActivity.this, article, "文章搜索",false);
					
			}
				}
		});
		
		mlListView.setAdapter(mAdapter);
		
	}

	/**
	 * 初始化界面控件
	 */
	private void initUi() {
		mMainLayout = (RelativeLayout) findViewById(R.id.single_activity_main_layout);
		mCover = (TextView) findViewById(R.id.single_activity_cover);
		mBackBtn = (ImageView) findViewById(R.id.single_activity_back_btn);
		mTitleText = (TextView) findViewById(R.id.single_activity_title);
		mlListView = (WithoutSectionCustomListViewUnused) findViewById(R.id.single_activity_mListView);
		
		mTitleText.setText(mSearchString);
		
		mlListView.setTheme(isDayTheme);
		
	}
	
	@Override
	protected void onDestroy() {
		ThemeChangeManager.getInstance().unregisterThemeChangeListener(mThemeListener);
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
