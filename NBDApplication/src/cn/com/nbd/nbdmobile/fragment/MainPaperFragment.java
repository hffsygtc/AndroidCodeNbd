package cn.com.nbd.nbdmobile.fragment;

import java.util.ArrayList;
import java.util.List;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.nbd.nbdmobile.activity.DailyNewsDetailActivity;
import cn.com.nbd.nbdmobile.activity.SearchActivity;
import cn.com.nbd.nbdmobile.adapter.MainPaperCoverFlowAdapter;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.CalenderData;
import cn.com.nbd.nbdmobile.utility.CalenderUtil;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.view.CalenderLineView;
import cn.com.nbd.nbdmobile.view.FancyCoverFlow;
import cn.com.nbd.nbdmobile.view.CalenderLineView.onDataClick;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;
import cn.com.nbd.nbdmobile.widget.MonthCalenderDialog;
import cn.com.nbd.nbdmobile.widget.MonthCalenderDialog.onDataClickFromMonth;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.FeatureDetail;
import com.nbd.article.bean.NewspaperImage;
import com.nbd.article.bean.NewspaperMonthBean;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.NewspaperMonthCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

/**
 * 原创每日报纸界面的fragment界面
 * 
 * @author riche
 * 
 */
public class MainPaperFragment extends Fragment {
	private final static String TAG = "DAILY_NEWS";
	  private final String mPageName = "DailyFragment";
	private Activity activity; // 获取到对于的fragment的activity

	private FancyCoverFlow fancyCoverFlow; // 画廊的控件

	private CalenderLineView calenderTitle;
	private CalenderLineView calenderContent;

	private RelativeLayout mMainLayout;
	private ImageView openMonthDialog;
	private MonthCalenderDialog mDialog;
	private RelativeLayout mPreMonthBtn;
	private RelativeLayout mNextMonthBtn;
	private TextView mDataTitleTxt;

	private RelativeLayout mSearchBtn;

	private LoadingDialog mLoading;

	// private ArrayList<NewspaperMonthBean> newsPaperlist = new
	// ArrayList<NewspaperMonthBean>();

	// 新闻图片地址和板块名称数据
	private List<NewspaperImage> newspaperImages = new ArrayList<NewspaperImage>();
	private List<NewspaperImage> newspaperImagesDatabase = new ArrayList<NewspaperImage>();

	private List<String> titleList = new ArrayList<String>();
	private List<String> titleListDatabase = new ArrayList<String>();

	// private FancyCoverFlowSampleAdapter sampleAdapter; // 画廊数据的adapter
	private MainPaperCoverFlowAdapter sampleAdapter; // 画廊数据的adapter

	public final static int LOAD_DATABASE_COMPLETE = 0;
	public final static int CHANGE_DATA_COMPLETE = 2;
	public final static int CHANGE_DATA_EMPTY = 3;

	private long requestMaxId;

	SharedPreferences sp;
	SharedPreferences.Editor editor;

	private boolean isDayTheme;
	private boolean isTextMode;

	private CalenderData checkData;
	/**
	 * 处理文章数据回调的事件
	 * 
	 */
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (sampleAdapter == null) {
				Log.d(TAG, "handle message with a null adapter");
				initAdapter();
			}
			switch (msg.what) {
			case LOAD_DATABASE_COMPLETE:
				Log.e(TAG, "database handler");
				sampleAdapter
						.setDataChange((ArrayList<NewspaperImage>) newspaperImagesDatabase);
				sampleAdapter.notifyDataSetChanged();
				fancyCoverFlow.setSelection(0);
				break;
			case CHANGE_DATA_COMPLETE:
				Log.e(TAG, "net handler");
				if (sp.getBoolean("Newspaper", true)) {
					editor.putBoolean("Newspaper", false);
					editor.commit();
				}
				Log.e(TAG, "send to adapter-->"+newspaperImages.size());
				sampleAdapter
						.setDataChange((ArrayList<NewspaperImage>) newspaperImages);
				sampleAdapter.notifyDataSetChanged();
				fancyCoverFlow.setSelection(0);
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 可接收从activity传来的bundle数据
		Log.d(TAG, ">>on creat");

		// 获取数据库数据
		// loadDataFromDatabase();
		super.onCreate(savedInstanceState);
	}

	/**
	 * 通过这样的方法获取activity实例，以免在嵌套fragment过程中，再加载activity为null的问题
	 */
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, ">>>>>>>>>gxfrg onAttach");
		this.activity = activity;
		sp = activity.getSharedPreferences("FirstLoad", activity.MODE_PRIVATE);
		editor = sp.edit();
		super.onAttach(activity);
	}

	/**
	 * 此方法意思为fragment是否可见 ,可见时候加载数据 不做这个处理，会加载多个fragment数据
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		Log.d("hff", ">>>>>>>>>paperfrg isVisibleToUser");
		if (isVisibleToUser) {
			Log.d("hff", ">>>>>>>>>gxfrg isVisibleToUser");

		} else {
			// fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	/**
	 * 创建，加载对应Fragment的布局文件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity.setTheme(R.style.NightTheme);
		Log.d("hff", ">>>>>>>>>gxfrg creatView");
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.activity_main_paper_fragment, null);

		mMainLayout = (RelativeLayout) view
				.findViewById(R.id.main_paper_layout);
		// 左右滑动的画廊效果控件
		fancyCoverFlow = (FancyCoverFlow) view
				.findViewById(R.id.paper_content_fancyCoverFlow);
		calenderTitle = (CalenderLineView) view
				.findViewById(R.id.paper_calender_title);
		calenderContent = (CalenderLineView) view
				.findViewById(R.id.paper_calender_data_line);
		mSearchBtn = (RelativeLayout) view
				.findViewById(R.id.paper_title_search);

		if (checkData == null) {
			checkData = new CalenderData(); // 获取实时当日的时间
		}

		calenderTitle.setTabItemTitles(CalenderUtil.getWeekTitle(), true,
				checkData, -1, true);
		calenderContent.setTabItemTitles(CalenderUtil.getThisWeek(), false,
				checkData, 1, true);
		openMonthDialog = (ImageView) view
				.findViewById(R.id.paper_btm_open_btn);

		mPreMonthBtn = (RelativeLayout) view
				.findViewById(R.id.paper_month_per_layout);
		mNextMonthBtn = (RelativeLayout) view
				.findViewById(R.id.paper_month_next_layout);
		mDataTitleTxt = (TextView) view.findViewById(R.id.paper_now_month_txt);
		mDataTitleTxt.setText(checkData.getYear() + "年" + checkData.getMonth()
				+ "月");

		mLoading = new LoadingDialog(activity, R.style.loading_dialog, "加载中...");

		changeThemeUi();

		setListener();

		initAdapter();

		// 第一次进入这个页面，请求网络获取最新报纸
		if (sp.getBoolean("Newspaper", true)) {
			getNewsPaperData();
		} else {

		}

		return view;
	}

	private void changeThemeUi() {
		if (isDayTheme) {
			ThemeUtil.setBackgroundDay(activity, mMainLayout);
		} else {
			ThemeUtil.setBackgroundNight(activity, mMainLayout);
		}

	}

	private void setListener() {
		calenderContent.setOnDataClickListener(new onDataClick() {

			@Override
			public void onDataClicked(CalenderData data, int weekPosition) {
				Log.e(TAG, "get change data==>" + data.toString());
				checkData = data;
				getNewsPaperData();
			}
		});

		openMonthDialog.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mDialog = new MonthCalenderDialog(activity, R.style.dialog,
						checkData.year, checkData.month, checkData, true);
				mDialog.setOnMonthDataChooseListener(new onDataClickFromMonth() {

					@Override
					public void onDataClicked(CalenderData data,
							List<CalenderData> dataWeek) {

						calenderContent.setWeekChanged(data, dataWeek);

						checkData = data;

						mDataTitleTxt.setText(checkData.getYear() + "年"
								+ checkData.getMonth() + "月");

						getNewsPaperData();
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
						checkData.year, checkData.month - 1, checkData, true);
				mDialog.setOnMonthDataChooseListener(new onDataClickFromMonth() {

					@Override
					public void onDataClicked(CalenderData data,
							List<CalenderData> dataWeek) {

						calenderContent.setWeekChanged(data, dataWeek);

						checkData = data;

						mDataTitleTxt.setText(checkData.getYear() + "年"
								+ checkData.getMonth() + "月");

						getNewsPaperData();
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
						checkData.year, checkData.month + 1, checkData, true);
				mDialog.setOnMonthDataChooseListener(new onDataClickFromMonth() {

					@Override
					public void onDataClicked(CalenderData data,
							List<CalenderData> dataWeek) {

						calenderContent.setWeekChanged(data, dataWeek);

						checkData = data;

						mDataTitleTxt.setText(checkData.getYear() + "年"
								+ checkData.getMonth() + "月");

						getNewsPaperData();
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

		mSearchBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (fancyCoverFlow != null) {
					int position = fancyCoverFlow.getLastVisiblePosition(); // 当前显示的item的位置
					int perPosition = fancyCoverFlow.getFirstVisiblePosition();
					Log.e("SHOW POSITION", "" + position + "==" + perPosition);
					if (position > 0 && position < newspaperImages.size() + 1) {
						String imgUrlString = "";
						if (position - perPosition == 1) {
							if (perPosition == 0) {
								imgUrlString = newspaperImages.get(perPosition)
										.getSection_avatar();
							} else {
								imgUrlString = newspaperImages.get(position)
										.getSection_avatar();
							}
						} else {
							imgUrlString = newspaperImages.get(position - 1)
									.getSection_avatar();
						}

						if (imgUrlString != null && !imgUrlString.equals("")) {
							Log.e(TAG, "SHOW IMG SHARE URL-->"+imgUrlString);
							
							showShare(imgUrlString);
						}
					}
				}
			}
		});
	}

	/**
	 * 初始化适配器，对适配器进行各种监听
	 */
	private void initAdapter() {

		if (sampleAdapter == null) {
			sampleAdapter = new MainPaperCoverFlowAdapter(activity,
					newspaperImages, isTextMode);
		}

		fancyCoverFlow.setAdapter(sampleAdapter);

		fancyCoverFlow.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Log.e(TAG, "NEWS CILCK POSITION==> " + position);
				Intent i = new Intent(activity, DailyNewsDetailActivity.class);

				Bundle b = new Bundle();
				if (titleList.size()>0) {
					b.putStringArrayList("title", (ArrayList<String>) titleList);
				}else {
					b.putStringArrayList("title", (ArrayList<String>) titleListDatabase);
				}
				b.putString("data", checkData.year + "-" + checkData.month
						+ "-" + checkData.day);
				b.putInt("position", position);
				i.putExtra("bundle", b);

				startActivity(i);

			}
		});

	}

	/**
	 * 摧毁视图
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		// mAdapter = null;
	}

	/**
	 * 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 获取对应日期报纸的资源 step1：从数据库获取 step2：数据库为空，从网络获取
	 */
	private void getNewsPaperData() {

		MobclickAgent.onEvent(activity, UmenAnalytics.EVENT_DIALY_NEWS_CHECK_DAY);
		ArticleConfig localConfig = new ArticleConfig();
		localConfig.setLocalArticle(true);
		// 设置请求的种类
		localConfig.setType(RequestType.NEWSPAPER_MONTH);

		String todayMonth = getMonthString(checkData, true);
		String todayData = getMonthString(checkData, false);
		

		Log.e(TAG, "[>>GET-DATA<<]--[>>DATABASE<<]" + "**month**" + todayMonth
				+ "**day**" + todayData);

		localConfig.setPaperMonth(todayMonth);
		localConfig.setPaperDate(todayData);

		ArticleManager.getInstence().getMonthNewspaper(localConfig,
				new NewspaperMonthCallback() {

					@Override
					public void onMonthPaperCallback(
							List<NewspaperMonthBean> papers) {

						if (papers != null && papers.size() > 0) {
							Log.d(TAG, "[>>DATABASE<<] ___ OK && SIZE=="
									+ papers.size());
							if (newspaperImagesDatabase != null) {
								newspaperImagesDatabase.clear();
							}
							newspaperImagesDatabase = papers.get(0)
									.getSections();
							// Message msg = new Message();
							// msg.what = LOAD_DATABASE_COMPLETE;
							// handler.sendMessage(msg);
							if (titleListDatabase!=null) {
								titleListDatabase.clear();
							}
							for (int i = 0; i < newspaperImagesDatabase.size(); i++) {
								titleListDatabase.add(i,
										newspaperImagesDatabase.get(i)
												.getSection());
							}
							getDataFromNet();
						} else {
							// 本地数据库不存在数据，从网络获取
							getDataFromNet();
						}

					}
				});

	}

	/**
	 * 修改请求的日期格式
	 * 
	 * @param requestData
	 * @param isMonth
	 * @return
	 */
	private String getMonthString(CalenderData requestData, boolean isMonth) {
		String monthString;
		String dayString;

		if (requestData.getMonth() < 10) {
			monthString = "0" + requestData.getMonth();
		} else {
			monthString = "" + requestData.getMonth();
		}

		if (requestData.getDay() < 10) {
			dayString = "0" + requestData.getDay();
		} else {
			dayString = "" + requestData.getDay();
		}

		if (isMonth) {
			return requestData.getYear() + "-" + monthString;
		} else {
			return requestData.getYear() + "-" + monthString + "-" + dayString;
		}
	}

	/**
	 * 从网络获取报纸数据资源
	 */
	private void getDataFromNet() {

		CalenderData requestData = CalenderUtil.getMoveData(checkData, 1);

		String month = getMonthString(requestData, true);
		String day = getMonthString(requestData, false);

		ArticleConfig changeConfig = new ArticleConfig();
		changeConfig.setLocalArticle(false);
		// 设置请求的种类
		changeConfig.setType(RequestType.NEWSPAPER_MONTH);
		

		Log.e(TAG, "[>>GET-DATA<<]--[>>NET<<]" + "**month**" + month
				+ "**day**" + day);

		changeConfig.setPaperMonth(month);
		changeConfig.setPaperDate(day);

		if (mLoading != null) {
			mLoading.showFullDialog();
		}

		ArticleManager.getInstence().getMonthNewspaper(changeConfig,
				new NewspaperMonthCallback() {

					@Override
					public void onMonthPaperCallback(
							List<NewspaperMonthBean> papers) {

						if (mLoading != null) {
							mLoading.dismiss();
						}
						if (papers != null && papers.size() > 0) {
							Log.d(TAG,
									"[>>NET<<] ___ OK && SIZE=="
											+ papers.size());

							if (newspaperImages != null) {
								newspaperImages.clear();
							}
							newspaperImages = papers.get(0).getSections();
Log.e(TAG, "day image size-->"+newspaperImages.size());
							Message msg = new Message();
							msg.what = CHANGE_DATA_COMPLETE;
							//
							handler.sendMessage(msg);
							if (titleList != null) {
								titleList.clear();
							}
							for (int i = 0; i < newspaperImages.size(); i++) {
								titleList.add(i, newspaperImages.get(i)
										.getSection());
							}
							// handler.obtainMessage(msg)
							// .sendToTarget();
						} else {
							if (newspaperImagesDatabase != null) {
								Message msg = new Message();
								msg.what = LOAD_DATABASE_COMPLETE;
								handler.sendMessage(msg);
							} else {
								handler.obtainMessage(CHANGE_DATA_EMPTY)
										.sendToTarget();
							}

						}

					}
				});

	}

	public void setTheme(boolean isDay) {
		this.isDayTheme = isDay;
	}

	public void setMode(boolean isText) {
		this.isTextMode = isText;
	}

	public void changeTheme(boolean isDay) {
		this.isDayTheme = isDay;
		changeThemeUi();
	}

	public void changeMode(boolean isText) {
		this.isTextMode = isText;
		if (sampleAdapter != null) {
			sampleAdapter.changeTextMode(isTextMode);
			sampleAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 分享的文章详情
	 * 
	 * @param shArticleInfo
	 */
	private void showShare(String imgUrl) {

		MobclickAgent.onEvent(activity, UmenAnalytics.SHARE_NEWSPAPER_IMAGE);
		ShareSDK.initSDK(activity);
		OnekeyShare oks = new OnekeyShare();

		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		oks.setImageUrl(imgUrl);

		// 启动分享GUI
		oks.show(activity);
	}
	
    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
    }

}
