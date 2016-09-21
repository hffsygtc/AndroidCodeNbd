package cn.com.nbd.nbdmobile.showview;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.adapter.InfosAdapter;
import cn.com.nbd.nbdmobile.adapter.testZxAdapter;
import cn.com.nbd.nbdmobile.adapter.InfosAdapter.OnNewsClickListener;
import cn.com.nbd.nbdmobile.utility.ArticleJumpUtil;
import cn.com.nbd.nbdmobile.utility.ShareUtile;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.view.RefreshListview;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused;

import com.lecloud.skin.videoview.vod.UIVodVideoView;
import com.nbd.article.article.ArticleType;
import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.ArticleInfoCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

/**
 * 资讯滚动的fragment界面
 * 
 * @author riche
 * 
 */
public class NewsZxFragment extends Fragment implements OnNewsClickListener {

	private final static String TAG = "ZX_NEWS";
	private final String mPageName = "ListFragment";

	private Activity activity; // 获取到对于的fragment的activity
	private RefreshListview mListView; // 带停靠栏的滑动列表
	private RelativeLayout mainLayout;

	private ArrayList<ArticleInfo> scrollList = new ArrayList<ArticleInfo>(); // 头部轮播的列表
	private ArrayList<ArticleInfo> newsList = new ArrayList<ArticleInfo>(); // 用于传入adapter的新闻内容列表
	private ArrayList<ArticleInfo> moreList = new ArrayList<ArticleInfo>(); // 上拉加载更多时获取的数据

	private testZxAdapter mAdapter; // 处理停靠栏的适配器

	private boolean isClearData; // 是否被destory做了数据清理
	private boolean isDayThem; // 是否是日间模式
	private boolean isTextMode; // 是否是文字模式
	private boolean isLBDataReturn; // 轮播和内容的数据均返回了再刷新数据
	private boolean isListDataReturn; // 轮播和内容的数据均返回了再刷新数据
	private boolean isCanloamore = true;
	private boolean isHintVisibleFirst; // fragment的visible方法之于oncreat方法之前，UI没有初始化，则在oncreat中获取数据
	private boolean isPre_Create_View; // Fragment 页面被预加载了，执行了ONCREATE
	private boolean isFooterAdd; // 加载更多的监听是否被添加
	private boolean isNotFirstLoad;

	private UIVodVideoView mVideoView;

	SharedPreferences sp;
	SharedPreferences.Editor editor;

	public final static int LOAD_DATABASE_COMPLETE = 0;
	public final static int LOAD_DATA_REFRESH = 1;
	public final static int LOAD_DATA_LOADMORE = 2;

	/**
	 * 构造函数
	 */
	public NewsZxFragment() {
		super();
	}

	/**
	 * 初始化主题配色
	 * 
	 * @param isDay
	 * @param isText
	 */
	public void initTheme(boolean isDay, boolean isText) {
		isDayThem = isDay;
		isTextMode = isText;
	}

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
			if (isClearData) {
				isClearData = false;
			}
			if (!isFooterAdd) {
				mListView
						.setOnLoadListener(new RefreshListview.OnLoadMoreListener() {

							@Override
							public void onLoadMore() {
								Log.i(TAG, "onLoad");
								if (mAdapter != null) {
									mAdapter.stopVideoPlaying();
								}
								loadData(LOAD_DATA_LOADMORE);
							}
						});
				isFooterAdd = true;
			}

			switch (msg.what) {
			case LOAD_DATABASE_COMPLETE:
				mListView.setCanLoadMore(isCanloamore);
				if (newsList == null || newsList.size() < 1) {

				} else {
					mAdapter.setDataChange(newsList, scrollList);
					mAdapter.notifyDataSetChanged();
				}
				mListView.setSelection(0);

				break;
			case LOAD_DATA_REFRESH:
				// 当前显示的界面和异步数据请求返回的数据一致，更新数据显示
				Log.i(TAG, " Refresh Complete");
				if (sp.getBoolean("testZx", true)) {
					editor.putBoolean("testZx", false);
					editor.commit();
				}

				if (isLBDataReturn && isListDataReturn) {
					mListView.setCanLoadMore(isCanloamore);
					mAdapter.setDataChange(newsList, scrollList);
					mAdapter.notifyDataSetChanged();
					mListView.onRefreshComplete();
				}

				break;
			case LOAD_DATA_LOADMORE:
				mListView.setCanLoadMore(isCanloamore);
				if (moreList != null) {
					newsList.addAll(moreList);
				}
				mAdapter.setDataChange(newsList, scrollList);
				mAdapter.notifyDataSetChanged();
				mListView.onLoadMoreComplete();
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
		// fragment可见时加载数据
		Bundle args = getArguments();
		super.onCreate(savedInstanceState);
	}

	/**
	 * 通过这样的方法获取activity实例，以免在嵌套fragment过程中，再加载activity为null的问题
	 */
	@Override
	public void onAttach(Activity activity) {
		Log.i(TAG, "==> onAttach");
		this.activity = activity;
		sp = activity.getSharedPreferences("FirstLoad", activity.MODE_PRIVATE);
		editor = sp.edit();

		super.onAttach(activity);
	}

	/**
	 * 创建，加载对应Fragment的布局文件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		activity.setTheme(R.style.NightTheme);
		Log.i(TAG, "==> creatView");
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_refresh_layout, null);
		mListView = (RefreshListview) view.findViewById(R.id.mListView);
		mainLayout = (RelativeLayout) view
				.findViewById(R.id.nosection_custom_layout);
		mListView.setTheme(isDayThem);
		if (isDayThem) {
			ThemeUtil.setBackgroundDay(activity, mainLayout);
		} else {
			ThemeUtil.setBackgroundNight(activity, mainLayout);
		}
		setListener();

		if (!isPre_Create_View) { // 设置页面已加载标志位
			isPre_Create_View = true;
		}

		if (isHintVisibleFirst) { // 直接点击的TAB，visible方法先执行,页面未加载，数据处理放这里
			isPre_Create_View = false;
			showDataToView(); // 初始化数据及数据显示
			if (isNotFirstLoad) {
				isPre_Create_View = true;
			}
		} else { // 正常流程，这边进行预加载
			if (!isClearData) { // 没进行过数据的优化保存，需从数据库预加载
				loadDataFromDatabase();
			}
		}

		return view;
	}

	/**
	 * 页面显示时，根据数据列表的数据情况，进行数据的更新，请求，显示
	 */
	private void showDataToView() {
		// 进入页面时，数据列表数据不为空
		if (newsList != null && newsList.size() != 0) {
			Log.i(TAG, "show view with content size==>" + newsList.size());
			if (mAdapter == null) {
				initAdapter();
			}
			if (isClearData) {
				handler.obtainMessage(LOAD_DATABASE_COMPLETE).sendToTarget(); // 先将页面保存或加载的数据显示在界面中
			}

			if (sp.getBoolean("testZx", true)) { // 显示时未请求网络数据同步，数据为数据库预加载
				mListView.handSetToRefresh(); // 自动下拉刷新，请求数据同步
			}
		} else { // 数据项为空，加载数据
			initData();
		}
	}

	/**
	 * 此方法意思为fragment是否可见 ,可见时候加载数据 不做这个处理，会加载多个fragment数据
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			// 当页面显示的时候，是否被预加载了 数据,初始化了SP等变量
			if (isPre_Create_View) {
				Log.i(TAG, "==>per load than visible [>>YES<<]");

				showDataToView();

			} else { // 没有进行预加载数据,SP等对象未被初始化
				Log.i(TAG, "==>set user Visible  by  click [>>NO<<]");
				if (!isClearData) { // 没进行过数据的优化保存，需从数据库预加载
					loadDataFromDatabase();
				}
				isHintVisibleFirst = true; // 标志位，将数据的初始化放置在createview方法中
				if (!isNotFirstLoad) {
					isNotFirstLoad = true;
				}
			}

		} else {
			if (mAdapter != null) {
				mAdapter.stopVideoPlaying();
			}
			// TODO HINT HANDLE
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	/**
	 * 设置列表的下拉刷新事件等
	 */
	private void setListener() {
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i(TAG, "***[>>CLICK<<]*** news position--->>>" + position);
			}
		});

		mListView.setOnRefreshListener(new RefreshListview.OnRefreshListener() {
			@Override
			public void onRefresh() {
				// TODO 下拉刷新
				Log.i(TAG, "onRefresh");
				if (mAdapter != null) {
					mAdapter.stopVideoPlaying();
				}
				loadData(LOAD_DATA_REFRESH);
			}

		});

	}

	/**
	 * 加载数据库的数据 TODO
	 */
	private void loadDataFromDatabase() {
		Log.i(TAG, "***[>>DATABASE<<]***");
		ArticleConfig lbConfig = new ArticleConfig();
		lbConfig.setLocalArticle(true);
		lbConfig.setType(RequestType.ZXLB);
		ArticleManager.getInstence().getArticleInfo(lbConfig,
				new ArticleInfoCallback() {

					@Override
					public void onArticleInfoCallback(List<ArticleInfo> infos,
							RequestType type) {

						if (scrollList != null && scrollList.size() > 0) {
							// 可能第一次网络请求回调已经赋值了
						} else {
							scrollList = (ArrayList<ArticleInfo>) infos;

							Log.i(TAG,
									"***[>>DATABASE<<]*** ZXLB return size--->>>"
											+ infos.size());
							if (scrollList != null && scrollList.size() != 0) {
								handler.obtainMessage(LOAD_DATABASE_COMPLETE)
										.sendToTarget();
							}
						}

					}
				});

		ArticleConfig zxConfig = new ArticleConfig();
		zxConfig.setLocalArticle(true);
		zxConfig.setType(RequestType.ZX);
		ArticleManager.getInstence().getArticleInfo(zxConfig,
				new ArticleInfoCallback() {

					@Override
					public void onArticleInfoCallback(List<ArticleInfo> infos,
							RequestType type) {
						if (newsList != null && newsList.size() > 0) {

						} else {
							newsList = (ArrayList<ArticleInfo>) infos;

							Log.i(TAG,
									"***[>>DATABASE<<]*** ZX return size--->>>"
											+ infos.size());
							if (newsList != null && newsList.size() > 0) {
								isCanloamore = true;
								handler.obtainMessage(LOAD_DATABASE_COMPLETE)
										.sendToTarget();
							}
						}
					}
				});

	}

	private void initAdapter() {
		if (mAdapter == null) {
			Log.e("Test List Frg==》", "init Mode-->" + isTextMode);
			mAdapter = new testZxAdapter((Context) activity, newsList,
					scrollList, isDayThem, isTextMode);
		}

		mListView.setAdapter(mAdapter);

		mAdapter.setNewsClickListener(this);
	}

	/**
	 * 加载网络数据 下拉 上拉 根据不同的情况进行分开的网络请求
	 * 
	 * @param loadDataRefresh
	 */
	private void loadData(int loadDataRefresh) {
		switch (loadDataRefresh) {
		case LOAD_DATA_REFRESH:// 这里是下拉刷新
			isLBDataReturn = false;
			isListDataReturn = false;

			MobclickAgent.onEvent(activity, UmenAnalytics.EVENT_ZX_REFRESH);
			// 先判断当前页面是哪个子页面，根据子页面进行不同的数据请求
			ArticleConfig refreshScrollConfig = new ArticleConfig();
			refreshScrollConfig.setLocalArticle(false);
			// 设置请求的种类
			refreshScrollConfig.setType(RequestType.ZXLB);

			ArticleManager.getInstence().getArticleInfo(refreshScrollConfig,
					new ArticleInfoCallback() {
						@Override
						public void onArticleInfoCallback(
								List<ArticleInfo> infos, RequestType type) {
							isLBDataReturn = true;

							if (infos != null) {
								Log.i(TAG,
										"***[>>NET<<]*** ZXLB refresh return size--->>>"
												+ infos.size());

								Message msg = new Message();
								msg.what = LOAD_DATA_REFRESH;

								scrollList = (ArrayList<ArticleInfo>) infos;

								if (isLBDataReturn && isListDataReturn) {
									handler.sendMessage(msg);
								}
							} else {
								if (isLBDataReturn && isListDataReturn) {
									handler.obtainMessage(LOAD_DATA_REFRESH)
											.sendToTarget();
								}
							}

						}
					});

			ArticleConfig refreshContentConfig = new ArticleConfig();
			refreshContentConfig.setLocalArticle(false);
			// 设置请求的种类
			refreshContentConfig.setType(RequestType.ZX);

			ArticleManager.getInstence().getArticleInfo(refreshContentConfig,
					new ArticleInfoCallback() {
						@Override
						public void onArticleInfoCallback(
								List<ArticleInfo> infos, RequestType type) {
							isListDataReturn = true;

							if (infos != null) {
								Log.i(TAG,
										"***[>>NET<<]*** ZX refresh return size-->>"
												+ infos.size());

								if (infos.size() < 15) {
									isCanloamore = false;
								} else {
									isCanloamore = true;
								}
								Message msg = new Message();
								msg.what = LOAD_DATA_REFRESH;

								newsList = (ArrayList<ArticleInfo>) infos; // 保存左边子项的数据
								if (isLBDataReturn && isListDataReturn) {
									handler.sendMessage(msg);
								}
							} else {
								isCanloamore = true;
								if (isLBDataReturn && isListDataReturn) {
									handler.obtainMessage(LOAD_DATA_REFRESH)
											.sendToTarget();
								}

								Toast.makeText(activity, "当前网络状况较差，请检查后重试",
										Toast.LENGTH_SHORT).show();
							}
						}
					});

			break;

		case LOAD_DATA_LOADMORE:

			MobclickAgent.onEvent(activity, UmenAnalytics.EVENT_ZX_MORE);
			ArticleConfig config1 = new ArticleConfig();
			config1.setLocalArticle(false);
			config1.setType(RequestType.ZX);
			if (newsList != null && newsList.size() > 0) {
				int newsListLen = newsList.size();
				ArticleInfo lastArticle = newsList.get(newsListLen - 1);
				config1.setMaxId(lastArticle.getPos());
			}

			ArticleManager.getInstence().getArticleInfo(config1,
					new ArticleInfoCallback() {

						@Override
						public void onArticleInfoCallback(
								List<ArticleInfo> infos, RequestType type) {

							if (infos != null) {
								Log.i(TAG,
										"***[>>NET<<]*** ZX load more size-->"
												+ infos.size());
								if (infos.size() < 15) {
									isCanloamore = false;
								} else {
									isCanloamore = true;
								}

								moreList = (ArrayList<ArticleInfo>) infos;
								handler.obtainMessage(LOAD_DATA_LOADMORE)
										.sendToTarget();

							} else {
								isCanloamore = true;
								handler.obtainMessage(LOAD_DATA_LOADMORE)
										.sendToTarget();
								Toast.makeText(activity, "当前网络状况较差，请检查后重试",
										Toast.LENGTH_SHORT).show();
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

		if (sp.getBoolean("Zx", true)) {
			if (mAdapter == null) {
				initAdapter();
			}
			mListView.handSetToRefresh();
		} else {
			loadDataFromDatabase();
		}
		// 初始化自动刷新
	}

	/**
	 * 摧毁视图
	 */
	@Override
	public void onDestroyView() {
		Log.i(TAG, "destroyView==>");
		super.onDestroyView();
		mAdapter = null;
		isPre_Create_View = false;
		isFooterAdd = false;

		if (newsList != null) {
			reStoreData();
		}

	}

	/**
	 * 保留15条数据不清除，为防止广告被清除了不正常显示
	 */
	private void reStoreData() {
		List<ArticleInfo> storeList = new ArrayList<ArticleInfo>();
		if (newsList.size() < 15) {
			for (int i = 0; i < newsList.size(); i++) {
				storeList.add(newsList.get(i));
			}
		} else {
			for (int i = 0; i < 15; i++) {
				storeList.add(newsList.get(i));
			}
		}

		newsList.clear();
		newsList.addAll(storeList);
		isClearData = true;
	}

	/**
	 * 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onNewsClick(View view, ArticleInfo article) {

		if (mAdapter != null) {
			mAdapter.stopVideoPlaying();
		}

		if (article.getList_show_control() != null) {
			if (article.getList_show_control().getDisplay_form() == 5) {
				ShareUtile.showShare(activity, article, null);
			} else {
				ArticleJumpUtil.jumpToArticleDetal(activity, article, "资讯",
						false);
			}
		} else {
			if (article.getType() != null
					&& article.getType().equals(ArticleType.VIDEO)) {
				ShareUtile.showShare(activity, article, null);
			} else {
				ArticleJumpUtil.jumpToArticleDetal(activity, article, "资讯",
						false);
			}
		}
	}

	public void changeTheme(boolean isDay) {
		this.isDayThem = isDay;
		if (mainLayout != null) {
			if (isDayThem) {
				ThemeUtil.setBackgroundDay(activity, mainLayout);
			} else {
				ThemeUtil.setBackgroundNight(activity, mainLayout);
			}
		}
		if (mListView != null) {
			mListView.setTheme(isDayThem);
		}
		if (mAdapter != null) {
			mAdapter.changeThem(isDay);
			mAdapter.notifyDataSetChanged();
		}
	}

	public void changeMode(boolean isText) {
		Log.e("TEST LIST-->", "change mode--" + isText);
		this.isTextMode = isText;
		if (mAdapter != null) {
			mAdapter.changeMode(isText);
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

	public void setVideoView(UIVodVideoView video) {
		this.mVideoView = video;
	}

	public void stopVideoPlaying() {
		if (mAdapter != null) {
			mAdapter.stopVideoPlaying();
		}
	}

}
