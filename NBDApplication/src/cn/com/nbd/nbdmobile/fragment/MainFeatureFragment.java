package cn.com.nbd.nbdmobile.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.activity.FeatureDetailActivity;
import cn.com.nbd.nbdmobile.activity.SearchActivity;
import cn.com.nbd.nbdmobile.adapter.MainFeatureAdapter;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.FeatureInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.FeatureInfoCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

/**
 * 活动页面
 * 
 * @author riche
 *
 */
public class MainFeatureFragment extends Fragment {
	private final static String TAG = "Feature_NEWS";
	  private final String mPageName = "FeatureFragment";
	private Activity activity; // 获取到对于的fragment的activity

	private ArrayList<FeatureInfo> newsList = new ArrayList<FeatureInfo>(); // 用于传入adapter的新闻内容列表

	private RelativeLayout mMainLayout;
	private WithoutSectionCustomListViewUnused mListView; // 带停靠栏的滑动列表
	private RelativeLayout mSearchBtn;

	private MainFeatureAdapter mAdapter; // 处理停靠栏的适配器


	public final static int LOAD_DATABASE_COMPLETE = 0;
	public final static int LOAD_DATA_REFRESH = 1;
	public final static int LOAD_DATA_LOADMORE = 2;

	public final static int PAGE_COUNT = 15;

	private long requestMaxId;

	SharedPreferences sp;
	SharedPreferences.Editor editor;
	private boolean isDayTheme;
	private boolean isTextMode;
	private boolean isCanLoadmore = true;
	private boolean isFootAdd;
	private boolean isAddFooterListener;

	/**
	 * 处理文章数据回调的事件
	 * 
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mAdapter == null) {
				Log.d(TAG, "adapter null><>");
				initAdapter();
			}
			
			if (!isAddFooterListener) {
			mListView
			.setOnLoadListener(new WithoutSectionCustomListViewUnused.OnLoadMoreListener() {

				@Override
				public void onLoadMore() {
					// TODO 加载更多
					Log.e(TAG, "onLoad");
					loadData(LOAD_DATA_LOADMORE);
				}
			});
			
			mListView.setCanLoadMore(isCanLoadmore);
			isAddFooterListener = true;
			}
		
			switch (msg.what) {
			case LOAD_DATABASE_COMPLETE:
				mAdapter.setDataChange(newsList);
				mAdapter.notifyDataSetChanged();
				break;
			case LOAD_DATA_REFRESH:
				if (sp.getBoolean("Feature", true)) {
						 editor.putBoolean("Feature", false);
						 editor.commit();
				}
				// 当前显示的界面和异步数据请求返回的数据一致，更新数据显示
				Log.e("hff", " 加载数据完成了");
				mAdapter.setDataChange(newsList);
				mAdapter.notifyDataSetChanged();
				mListView.onRefreshComplete();
				break;
			case LOAD_DATA_LOADMORE:
				mAdapter.notifyDataSetChanged();
				mListView.onLoadMoreComplete();
				mListView.setCanLoadMore(isCanLoadmore);
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
		Log.d("hff", ">>>>>>>>>gxfrg creat");
		// fragment可见时加载数据
		Bundle args = getArguments();

		// 获取数据库数据
//		loadDataFromDatabase();

		super.onCreate(savedInstanceState);
	}

	/**
	 * 加载数据库的数据 TODO
	 */
	private void loadDataFromDatabase() {
		ArticleConfig config = new ArticleConfig();
		config.setLocalArticle(true);
		config.setType(RequestType.FEATURE);
		
		ArticleManager.getInstence().getFeatureList(config, new FeatureInfoCallback() {
			
			@Override
			public void onArticleInfoCallback(List<FeatureInfo> infos, RequestType type) {
				
				Log.e("FEATURE-DATABASE==>", "==>"+infos.size());
				if (infos != null && infos.size()>0) {
					newsList = (ArrayList<FeatureInfo>) infos;
					Message msg = new Message();
					msg.what = LOAD_DATABASE_COMPLETE;
					handler.sendMessage(msg);
				}
				  
			}
		});


	}

	/**
	 * 通过这样的方法获取activity实例，以免在嵌套fragment过程中，再加载activity为null的问题
	 */
	@Override
	public void onAttach(Activity activity) {
		Log.d("hff", ">>>>>>>>>gxfrg onAttach");
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
				R.layout.activity_main_feature_fragment, null);
		mMainLayout = (RelativeLayout) view.findViewById(R.id.main_feature_layout);
		mListView = (WithoutSectionCustomListViewUnused) view.findViewById(R.id.activity_listView);
		
		mListView.setTheme(isDayTheme);
		
		mSearchBtn = (RelativeLayout) view.findViewById(R.id.activity_title_search);

//		initAdapter();
		
		changeThemeUi();

		isCanLoadmore = true;
		setListener();
		
		//第一次进入这个页面，请求网络获取最新报纸
		
		loadDataFromDatabase();
		showDataToView();
//		initData();
		
		return view;
	}

	private void changeThemeUi() {
		if (isDayTheme) {
			ThemeUtil.setBackgroundDay(activity, mMainLayout);
		}else {
			ThemeUtil.setBackgroundNight(activity, mMainLayout);
		}
		
	}

	private void setListener() {
		
		mSearchBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(activity,SearchActivity.class);
				startActivity(i);
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Log.d("hff", "click position>>>>>" + position);
				
				FeatureInfo feature = newsList.get(position-1);
				
				if (feature != null) {
					Intent i  = new Intent(activity,FeatureDetailActivity.class);
					i.putExtra("featureId", feature.getFeature_id());
					startActivity(i);
				}

			}
		});

		mListView
				.setOnRefreshListener(new WithoutSectionCustomListViewUnused.OnRefreshListener() {
					@Override
					public void onRefresh() {
						// TODO 下拉刷新
						Log.e(TAG, "onRefresh");
						loadData(LOAD_DATA_REFRESH);
					}

				});
		
		if (isAddFooterListener) {
			mListView
					.setOnLoadListener(new WithoutSectionCustomListViewUnused.OnLoadMoreListener() {

						@Override
						public void onLoadMore() {
							// TODO 加载更多
							Log.e(TAG, "onLoad");
							loadData(LOAD_DATA_LOADMORE);
						}
					});

			mListView.setCanLoadMore(isCanLoadmore);
		}
	}

	private void showDataToView() {
		// 进入页面时，数据列表数据不为空
		if (newsList != null && newsList.size() != 0) {
			if (mAdapter == null) {
				initAdapter();
			}
			handler.obtainMessage(LOAD_DATABASE_COMPLETE).sendToTarget(); // 先将页面保存或加载的数据显示在界面中

			if (sp.getBoolean("Feature", true)) { // 显示时未请求网络数据同步，数据为数据库预加载
				mListView.handSetToRefresh(); // 自动下拉刷新，请求数据同步
			}
		} else { // 数据项为空，加载数据
			initData();
		}
	}
	
	
	private void initAdapter() {
		if (mAdapter == null) {
			mAdapter = new MainFeatureAdapter(activity, newsList,isDayTheme,isTextMode);
		}

		mListView.setAdapter(mAdapter);

	}

	/**
	 * 加载网络数据 下拉 上拉 根据不同的情况进行分开的网络请求
	 * 
	 * @param loadDataRefresh
	 */
	private void loadData(int loadDataRefresh) {
		switch (loadDataRefresh) {
		case LOAD_DATA_REFRESH:// 这里是下拉刷新

			MobclickAgent.onEvent(activity, UmenAnalytics.EVENT_FEATURE_REFRESH);
			// 先判断当前页面是哪个子页面，根据子页面进行不同的数据请求
			ArticleConfig refreshConfig = new ArticleConfig();
			refreshConfig.setLocalArticle(false);
			// 设置请求的种类
			refreshConfig.setType(RequestType.FEATURE);

			ArticleManager.getInstence().getFeatureList(refreshConfig,
					new FeatureInfoCallback() {
						
						@Override
						public void onArticleInfoCallback(List<FeatureInfo> infos, RequestType type) {
							
							if (infos != null) {
								Log.d(TAG,
										"kx content netdata==>" + infos.size());
								Message msg = new Message();
								msg.what = LOAD_DATA_REFRESH;
								newsList = (ArrayList<FeatureInfo>) infos;

								if (newsList.size() <15) {
									isCanLoadmore = false;
								}else {
									isCanLoadmore = true;
								}
								handler.sendMessage(msg);
								// handler.obtainMessage(msg)
								// .sendToTarget();
							} else {
								handler.obtainMessage(LOAD_DATA_REFRESH)
										.sendToTarget();
								Toast.makeText(activity, "当前网络较差，请检查后重试", Toast.LENGTH_SHORT).show();

								mListView.onRefreshComplete();
							}
							
						}
					}); 

			break;

		case LOAD_DATA_LOADMORE:
			MobclickAgent.onEvent(activity, UmenAnalytics.EVENT_FEATURE_MORE);
				ArticleConfig config1 = new ArticleConfig();
				config1.setLocalArticle(false);
				config1.setType(RequestType.FEATURE);
				if (newsList != null && newsList.size() > 1) {
					int newsListLen = newsList.size();
					FeatureInfo lastArticle = newsList.get(newsListLen - 1);
					config1.setMaxId(lastArticle.getFeature_id());
				}

				ArticleManager.getInstence().getFeatureList(config1,
						new FeatureInfoCallback() {
							
							@Override
							public void onArticleInfoCallback(List<FeatureInfo> infos, RequestType type) {
								
								if (infos != null) {
									Log.d(TAG,
											"Feature content netdata==>"
													+ infos.size());
									newsList.addAll(infos);
									if (newsList != null
											&& newsList.size() != 0) {
										if (newsList.size() <15) {
											isCanLoadmore = false;
										}else {
											isCanLoadmore = true;
										}
										handler.obtainMessage(
												LOAD_DATA_LOADMORE)
												.sendToTarget();
									}
								} else {
									handler.obtainMessage(LOAD_DATA_LOADMORE)
											.sendToTarget();

								}
								
							}
						}); 

			break;
		}

	}

	/**
	 * 初始化数据，传递给adapter显示 获取方案未定，可以ACTIVITY获取，可以在该fragment获取
	 */
	private void initData() {

		if (sp.getBoolean("Feature", true)) {
			if (mAdapter == null) {
				initAdapter();
			}
			mListView.handSetToRefresh();
		}else {
			loadDataFromDatabase();
		}
		// 初始化自动刷新

	}

	/**
	 * 摧毁视图
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		 mAdapter = null;
	}

	/**
	 * 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	public void setTheme(boolean isDay){
		this.isDayTheme = isDay;
	}
	public void setMode(boolean isText){
		this.isTextMode	 = isText;
	}
	public void changeTheme(boolean isDay){
		this.isDayTheme = isDay;
		changeThemeUi();
		if (mListView != null) {
			mListView.setTheme(isDayTheme);
		}
		if (mAdapter != null) {
			mAdapter.changeTheme(isDay);
			mAdapter.notifyDataSetChanged();
		}
	}
	public void changeMode(boolean isText){
		this.isTextMode = isText;
		if (mAdapter != null) {
			mAdapter.changeMode(isTextMode);
			mAdapter.notifyDataSetChanged();
		}
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
