package cn.com.nbd.nbdmobile.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.adapter.NewspaperDailyAdapter;
import cn.com.nbd.nbdmobile.adapter.NewspaperDailyAdapter.OnNewsClickListener;
import cn.com.nbd.nbdmobile.manager.OnThemeChangeListener;
import cn.com.nbd.nbdmobile.manager.ThemeChangeManager;
import cn.com.nbd.nbdmobile.utility.ArticleJumpUtil;
import cn.com.nbd.nbdmobile.utility.Options;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.NewspaperDailyBean;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.NewspaperDailyCallback;
import com.nbd.network.bean.RequestType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

/**
 * 单天报纸详情页面的activity
 * 
 * @author riche
 * 
 */
public class DailyNewsDetailActivity extends Activity {

	private LayoutInflater mInflater;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	private List<NewspaperDailyBean> mDailyNewsList; // 获取到的具体日期的文章详情数据
	private List<String> titleList = new ArrayList<String>();
	private List<Integer> positionList = new ArrayList<Integer>(); // 存储每一个项目的位置

	private String mDataString; // 报纸的日期
	private int mPosition; // 当前显示的版面位置

	private NewspaperDailyAdapter mAdapter;

	private RelativeLayout mMainLayout;
	private TextView mCover;
	private ListView mlListView;
	private ImageView mBackBtn;

	private SharedPreferences mNativeShare;
	private boolean isDayTheme;

	private OnThemeChangeListener mThemeChangeListener;

	private LoadingDialog mLoadingDialog;

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

		setContentView(R.layout.activity_daily_news);

		Intent i = getIntent();
		Bundle b = i.getBundleExtra("bundle");
		mDataString = b.getString("data");
		mPosition = b.getInt("position");
		titleList = b.getStringArrayList("title");

		mInflater = getLayoutInflater();
		options = Options.getListOptions();

		mLoadingDialog = new LoadingDialog(DailyNewsDetailActivity.this,
				R.style.loading_dialog, "加载中...");

		initUi();

		setListener();

		initData();
		MobclickAgent.onEvent(DailyNewsDetailActivity.this, UmenAnalytics.DETAIL_DAILY_NEWS);
		// initAdapter();
	}

	/**
	 * 设置页面中的事件监听
	 */
	private void setListener() {
		mBackBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DailyNewsDetailActivity.this.finish();

			}
		});

		mThemeChangeListener = new OnThemeChangeListener() {

			@Override
			public void onNightThemeChange(boolean isNowTheme) {

				setThemeUi(isNowTheme);

			}
		};

		ThemeChangeManager.getInstance().registerThemeChangeListener(
				mThemeChangeListener);

	}

	private void setThemeUi(boolean isDay) {

		if (isDayTheme & isDay) {

		} else {
			isDayTheme = isDay;
			if (isDayTheme) {
				mMainLayout.setBackgroundColor(DailyNewsDetailActivity.this
						.getResources()
						.getColor(R.color.day_section_background));
				mCover.setBackgroundColor(DailyNewsDetailActivity.this
						.getResources().getColor(R.color.day_cover));

			} else {
				mMainLayout.setBackgroundColor(DailyNewsDetailActivity.this
						.getResources().getColor(
								R.color.night_common_background));
				mCover.setBackgroundColor(DailyNewsDetailActivity.this
						.getResources().getColor(R.color.night_cover));

			}
			
			if (mAdapter != null) {
				mAdapter.changeTheme(isDayTheme);
				mAdapter.notifyDataSetChanged();
			}
			
		}

	}

	/**
	 * 初始化数据，传递来的内容数据，图片链接集合
	 */
	private void initData() {

		ArticleConfig config = new ArticleConfig();
		config.setType(RequestType.NEWSPAPER_DAILY);
		config.setPaperDate(mDataString);
		// config.setPaperDate("2016-06-13");

		if (mLoadingDialog != null) {
			mLoadingDialog.showFullDialog();
		}
		ArticleManager.getInstence().getDailyNewspaper(config,
				new NewspaperDailyCallback() {

					@Override
					public void onDailyNewsCallback(
							List<NewspaperDailyBean> dailyNews) {
						if (mLoadingDialog != null) {
							mLoadingDialog.dismiss();
						}
						if (dailyNews != null) {
							Log.e("DAILY-NEWS==>", "news callback==>"
									+ dailyNews.size());
							mDailyNewsList = dailyNews;

							initAdapter();
						} else {
							Toast.makeText(DailyNewsDetailActivity.this,
									"数据获取错误...", Toast.LENGTH_SHORT).show();
						}

					}
				});

	}

	/**
	 * 初始化适配器 界面数据显示
	 */
	private void initAdapter() {

		mAdapter = new NewspaperDailyAdapter(DailyNewsDetailActivity.this,
				mDailyNewsList, titleList,isDayTheme);
		mAdapter.setOnNewsClickListener(new OnNewsClickListener() {

			@Override
			public void onNewsClick(View view, ArticleInfo article) {

				if (article != null) {
					Log.d("WEBVIEW FROM DAILYNEWS==>", article.getArticle_id()
							+ "");
					Intent i = new Intent(DailyNewsDetailActivity.this,
							WebviewContentActivity.class);
					Bundle b = new Bundle();
					b.putLong("url", article.getArticle_id());
					b.putString("title", "今日报纸");
					b.putBoolean("comment", article.isAllow_review());
					b.putLong("commentNum", article.getReview_count());
					b.putBoolean("Jpush", false);
					i.putExtra("urlbundle", b);
					startActivity(i);
					
					if (article.getArticle_id() >0 ) {
						ArticleJumpUtil.addClickCount(article.getArticle_id(), "",false);
					}
				}
			}
		});
		mlListView.setAdapter(mAdapter);

		int temptPosition = 0;
		// 根据标签的列表，和每个标签里面的文章个数，解析数据
		for (int i = 0; i < mDailyNewsList.size(); i++) {
			positionList.add(temptPosition);

			int articleSize = mDailyNewsList.get(i).getArticles().size();
			temptPosition = temptPosition + articleSize;
		}

		if (mPosition < positionList.size()) {
			mlListView.setSelectionFromTop(positionList.get(mPosition), 0);
		}

	}

	/**
	 * 初始化界面控件
	 */
	private void initUi() {
		mMainLayout = (RelativeLayout) findViewById(R.id.dailynews_main_layout);
		mCover = (TextView) findViewById(R.id.dailynews_detail_cover);
		mlListView = (ListView) findViewById(R.id.dailynews_detail_mListView);
		mBackBtn = (ImageView) findViewById(R.id.dialynews_detail_back_btn);
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
