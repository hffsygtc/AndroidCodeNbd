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
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.nbd.nbdmobile.adapter.SearchResultAdapter;
import cn.com.nbd.nbdmobile.adapter.SearchResultAdapter.OnNewsClickListener;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.manager.OnThemeChangeListener;
import cn.com.nbd.nbdmobile.manager.ThemeChangeManager;
import cn.com.nbd.nbdmobile.utility.ArticleJumpUtil;
import cn.com.nbd.nbdmobile.utility.CalenderData;
import cn.com.nbd.nbdmobile.utility.CalenderUtil;
import cn.com.nbd.nbdmobile.view.CalenderLineView;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused;
import cn.com.nbd.nbdmobile.view.CalenderLineView.onDataClick;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused.OnLoadMoreListener;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;
import cn.com.nbd.nbdmobile.widget.MonthCalenderDialog;
import cn.com.nbd.nbdmobile.widget.MonthCalenderDialog.onDataClickFromMonth;

import com.nbd.article.article.ArticleType;
import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.ArticleInfoCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

/**
 * 原创每日报纸界面的fragment界面
 * 
 * @author riche
 * 
 */
public class SelfReadingActivity extends Activity {
	private final static String TAG = "DAILY_NEWS";
	private Activity activity; // 获取到对于的fragment的activity

	private CalenderLineView calenderTitle;
	private CalenderLineView calenderContent;

	private RelativeLayout mMainLayout;
	private TextView mCover;
	private ImageView openMonthDialog;
	private MonthCalenderDialog mDialog;
	private RelativeLayout mPreMonthBtn;
	private RelativeLayout mNextMonthBtn;
	private TextView mDataTitleTxt;

	private WithoutSectionCustomListViewUnused mListview;
	private ImageView mBackBtn;

	private List<ArticleInfo> mReadingArticles = new ArrayList<ArticleInfo>();

	private SearchResultAdapter mAdapter; // 和搜索文章结果公用一个适配器

	private OnThemeChangeListener mThemeChangeListener;

	public final static int LOAD_DATA_COMPLETE = 0;
	public final static int LOAD_CHANGE_COMPLETE = 1;

	private String mAccessToken;

	private boolean isCanLoadMore;

	SharedPreferences sp;
	SharedPreferences.Editor editor;
	private SharedPreferences mNativeShare;
	private boolean isDayTheme;

	private LoadingDialog mloadingDialog;

	private CalenderData checkData;
	/**
	 * 处理文章数据回调的事件
	 * 
	 */
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (mAdapter == null) {
				initAdapter();
			}
			switch (msg.what) {
			case LOAD_DATA_COMPLETE:
				mAdapter.setDataChange((ArrayList<ArticleInfo>) mReadingArticles);
				mAdapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mNativeShare = this.getSharedPreferences("NativeSetting",
				this.MODE_PRIVATE);
		isDayTheme = mNativeShare.getBoolean("theme", true);

		if (isDayTheme) {
			setTheme(R.style.DayTheme);
		} else {
			setTheme(R.style.NightTheme);
		}

		setContentView(R.layout.activity_self_reading);

		this.activity = this;

		Intent intent = getIntent();
		mAccessToken = intent.getStringExtra("token");
		Log.e("READING--TOKEN", mAccessToken);

		mloadingDialog = new LoadingDialog(SelfReadingActivity.this,
				R.style.loading_dialog, "加载中...");
		initUi();

		setListener();

		// testData();

		getData(false);

	}

	private void initUi() {

		mMainLayout = (RelativeLayout) findViewById(R.id.reading_main_layout);
		mCover = (TextView) findViewById(R.id.reading_cover);

		calenderTitle = (CalenderLineView) findViewById(R.id.reading_calender_title);
		calenderContent = (CalenderLineView) findViewById(R.id.reading_calender_data_line);

		checkData = new CalenderData(); // 获取实时当日的时间

		calenderTitle.setTabItemTitles(CalenderUtil.getWeekTitle(), true,
				checkData, -1, false);
		calenderContent.setTabItemTitles(CalenderUtil.getThisWeek(), false,
				checkData, 1, false);
		openMonthDialog = (ImageView) findViewById(R.id.reading_btm_open_btn);

		mPreMonthBtn = (RelativeLayout) findViewById(R.id.reading_month_per_layout);
		mNextMonthBtn = (RelativeLayout) findViewById(R.id.reading_month_next_layout);
		mDataTitleTxt = (TextView) findViewById(R.id.reading_now_month_txt);
		mDataTitleTxt.setText(checkData.getYear() + "年" + checkData.getMonth()
				+ "月");

		mBackBtn = (ImageView) findViewById(R.id.reading_back_btn);
		mListview = (WithoutSectionCustomListViewUnused) findViewById(R.id.reading_mListView);
		mListview.setTheme(isDayTheme);

	}

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
				SelfReadingActivity.this.finish();

			}
		});

		calenderContent.setOnDataClickListener(new onDataClick() {

			@Override
			public void onDataClicked(CalenderData data, int weekPosition) {
				Log.e(TAG, "get change data==>" + data.toString());
				checkData = data;
				changePaperData(checkData);
			}
		});

		openMonthDialog.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog = new MonthCalenderDialog(activity, R.style.dialog,
						checkData.year, checkData.month, checkData, false);
				mDialog.setOnMonthDataChooseListener(new onDataClickFromMonth() {

					@Override
					public void onDataClicked(CalenderData data,
							List<CalenderData> dataWeek) {

						calenderContent.setWeekChanged(data, dataWeek);

						checkData = data;

						mDataTitleTxt.setText(checkData.getYear() + "年"
								+ checkData.getMonth() + "月");
						changePaperData(checkData);
					}
				});
				mDialog.show();
				WindowManager windowManager = activity.getWindowManager();
				Display display = windowManager.getDefaultDisplay();
				WindowManager.LayoutParams lp = mDialog.getWindow()
						.getAttributes();
				lp.width = (int) (display.getWidth()); // 设置宽度
				mDialog.getWindow().setAttributes(lp);

			}
		});

		mPreMonthBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mDialog = new MonthCalenderDialog(activity, R.style.dialog,
						checkData.year, checkData.month - 1, checkData, false);
				mDialog.setOnMonthDataChooseListener(new onDataClickFromMonth() {

					@Override
					public void onDataClicked(CalenderData data,
							List<CalenderData> dataWeek) {

						calenderContent.setWeekChanged(data, dataWeek);

						checkData = data;

						mDataTitleTxt.setText(checkData.getYear() + "年"
								+ checkData.getMonth() + "月");
						changePaperData(checkData);
					}
				});
				mDialog.show();
				WindowManager windowManager = activity.getWindowManager();
				Display display = windowManager.getDefaultDisplay();
				WindowManager.LayoutParams lp = mDialog.getWindow()
						.getAttributes();
				lp.width = (int) (display.getWidth()); // 设置宽度
				mDialog.getWindow().setAttributes(lp);

			}
		});

		mNextMonthBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				mDialog = new MonthCalenderDialog(activity, R.style.dialog,
						checkData.year, checkData.month + 1, checkData, false);
				mDialog.setOnMonthDataChooseListener(new onDataClickFromMonth() {

					@Override
					public void onDataClicked(CalenderData data,
							List<CalenderData> dataWeek) {

						calenderContent.setWeekChanged(data, dataWeek);

						checkData = data;

						mDataTitleTxt.setText(checkData.getYear() + "年"
								+ checkData.getMonth() + "月");
						changePaperData(checkData);
					}
				});
				mDialog.show();
				WindowManager windowManager = activity.getWindowManager();
				Display display = windowManager.getDefaultDisplay();
				WindowManager.LayoutParams lp = mDialog.getWindow()
						.getAttributes();
				lp.width = (int) (display.getWidth()); // 设置宽度
				mDialog.getWindow().setAttributes(lp);

			}
		});

		mListview.setOnLoadListener(new OnLoadMoreListener() {

			@Override
			public void onLoadMore() {
				if (isCanLoadMore) {
					getData(true);
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
				mMainLayout.setBackgroundColor(SelfReadingActivity.this
						.getResources()
						.getColor(R.color.day_section_background));
				mCover.setBackgroundColor(SelfReadingActivity.this
						.getResources().getColor(R.color.day_cover));

			} else {
				mMainLayout.setBackgroundColor(SelfReadingActivity.this
						.getResources().getColor(
								R.color.night_common_background));
				mCover.setBackgroundColor(SelfReadingActivity.this
						.getResources().getColor(R.color.night_cover));

			}

			if (mAdapter != null) {
				mAdapter.changeTheme(isDayTheme);
				mAdapter.notifyDataSetChanged();
			}
			if (mListview != null) {
				mListview.setTheme(isDayTheme);
			}
		}
	}

	/**
	 * 初始化适配器，对适配器进行各种监听
	 */
	private void initAdapter() {

		if (mAdapter == null) {
			mAdapter = new SearchResultAdapter(SelfReadingActivity.this,
					(ArrayList<ArticleInfo>) mReadingArticles, true, isDayTheme);
		}
		mAdapter.setNewsClickListener(new OnNewsClickListener() {

			@Override
			public void onNewsClick(View view, ArticleInfo article) {

				if (article != null) {

					ArticleJumpUtil.jumpToArticleDetal(
							SelfReadingActivity.this, article, "阅读历史",false);
				}

			}
		});

		mListview.setAdapter(mAdapter);

	}

	/**
	 * 初始化数据，传递给adapter显示 获取方案未定，可以ACTIVITY获取，可以在该fragment获取
	 */
	private void getData(final boolean isLoadMore) {
		ArticleConfig todayConfig = new ArticleConfig();
		todayConfig.setLocalArticle(false);
		// 设置请求的种类
		todayConfig.setType(RequestType.READING);
		todayConfig.setPaperDate(checkData.getYear() + "-"
				+ checkData.getMonth() + "-" + checkData.getDay());

		if (mReadingArticles != null && mReadingArticles.size() > 0) {
			ArticleInfo lastArticle = mReadingArticles.get(mReadingArticles
					.size() - 1);
			todayConfig.setMaxId(lastArticle.getArticle_id());
		}
		todayConfig.setAccessToken(mAccessToken);

		if (mloadingDialog != null) {
			mloadingDialog.showFullDialog();
		}

		ArticleManager.getInstence().getReadingArticle(todayConfig,
				new ArticleInfoCallback() {
					@Override
					public void onArticleInfoCallback(List<ArticleInfo> infos,
							RequestType type) {

						if (mloadingDialog != null) {
							mloadingDialog.dismiss();
						}
						if (infos != null) {
							if (infos.size() < 10) {
								isCanLoadMore = false;
								mListview.setCanLoadMore(false);
							} else {
								isCanLoadMore = true;
								mListview.setCanLoadMore(true);
							}

							if (isLoadMore) {
								mReadingArticles.addAll(infos);
							} else {
								if (mReadingArticles != null
										&& mReadingArticles.size() > 0) {
									mReadingArticles.clear();
								}
								mReadingArticles.addAll(infos);
							}
							handler.sendEmptyMessage(0);
							// initAdapter();
						} else {

						}
					}
				});

	}

	/**
	 * 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁
	 */
	@Override
	public void onDestroy() {
		ThemeChangeManager.getInstance().unregisterThemeChangeListener(
				mThemeChangeListener);
		super.onDestroy();
	}

	/**
	 * 改变了选中的日期
	 * 
	 * @param data
	 *            日期
	 */
	private void changePaperData(CalenderData data) {

		getData(false);
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
