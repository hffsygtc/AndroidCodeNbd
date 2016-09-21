package cn.com.nbd.nbdmobile.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
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
import cn.com.nbd.nbdmobile.adapter.NewsSectionAdapter;
import cn.com.nbd.nbdmobile.adapter.NewsSectionAdapter.OnNewsClickListener;
import cn.com.nbd.nbdmobile.callback.ListviewPosRecrodCallback;
import cn.com.nbd.nbdmobile.callback.ToggleCheckedCallback;
import cn.com.nbd.nbdmobile.utility.ArticleJumpUtil;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.view.CustomListViewUnused;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.ArticleInfoCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

/**
 * 公司频道界面
 * 
 * @author riche
 * 
 */
public class NewsCompanyFragment extends Fragment implements
		OnNewsClickListener {
	private final static String TAG = "GS_NEWS";
	  private final String mPageName = "CompanyFragmet";

	private Activity activity; // 获取到对于的fragment的activity
	private CustomListViewUnused mListView; // 带停靠栏的滑动列表
	private RelativeLayout mainLayout;

	private ArrayList<ArticleInfo> scrollList = new ArrayList<ArticleInfo>(); // 头部轮播的列表
	private ArrayList<ArticleInfo> newsList = new ArrayList<ArticleInfo>(); // 用于传入adapter的新闻内容列表
	private ArrayList<ArticleInfo> rightNewsList = new ArrayList<ArticleInfo>(); // 右边栏目的新闻内容列表
	private ArrayList<ArticleInfo> leftNewsList = new ArrayList<ArticleInfo>(); // 左边栏目的新闻内容列表
	private ArrayList<ArticleInfo> moreLoadList = new ArrayList<ArticleInfo>(); // 加载更多时的网络数据

	private NewsSectionAdapter mAdapter; // 处理停靠栏的适配器

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	private int lItem; // 左边选择项的第一个显示item位置
	private int rItem; // 右边选择项的第一个显示item位置
	private int lPos; // 左边第一个显示项的滑动距离
	private int rPos; // 右边第一个显示项的滑动距离
	private int showSection = -1; // 显示的区域，默认-1用于界面销毁后重建的标志

	// 切换时保存当前子栏目的头部尾部状态
	private Map<String, Integer> lSectionStates;
	private Map<String, Integer> rSectionStates;

	private boolean isCanLoadmore = true;
	private boolean isDataClear; // 界面销毁的时候，清除了数据的标志，用于显示有数据时是否刷新界面
	private boolean isFooterAdd;
	private boolean isHintVisibleFirst; // fragment的visible方法之于oncreat方法之前，UI没有初始化，则在oncreat中获取数据
	private boolean isPre_Create_View; // Fragment 页面被预加载了，执行了ONCREATE
	private boolean isDayTheme;
	private boolean isTextMode;
	private boolean isLbReturn;
	private boolean isListReturn;

	private final static int LEFT_SECTION = 0;
	private final static int RIGHT_SECTION = 1;

	public final static int LOAD_DATABASE_COMPLETE = 0;
	public final static int LOAD_DATA_REFRESH = 1;
	public final static int LOAD_DATA_LOADMORE = 2;
	public final static int LOAD_LEFT_DATA_LOADMORE = 3;
	public final static int LOAD_RIGHT_DATA_LOADMORE = 4;
	public final static int LOAD_LB_DATA_REFRESH = 5;

	/**
	 * 构造函数
	 */
	public NewsCompanyFragment() {
		super();
	}

	/**
	 * 初始化界面模式
	 * 
	 * @param isDay
	 * @param isTex
	 */
	public void initTheme(boolean isDay, boolean isTex) {
		isDayTheme = isDay;
		isTextMode = isTex;
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mAdapter == null) {
				initAdapter();
			}
			if (isDataClear) {
				isDataClear = false;
			}

			if (isLbReturn && isListReturn) {
				if (!isFooterAdd) {
					mListView
					.setOnLoadListener(new CustomListViewUnused.OnLoadMoreListener() {
						
						@Override
						public void onLoadMore() {
							Log.e(TAG, "onLoad");
							loadData(LOAD_DATA_LOADMORE);
						}
					});
					isFooterAdd = true;
				}
				mListView.setCanLoadMore(isCanLoadmore);
			}


			switch (msg.what) {
			case LOAD_DATABASE_COMPLETE:
				if (newsList == null || newsList.size() < 1) {
				} else {
					mAdapter.setDataChange(scrollList, newsList);
					mAdapter.notifyDataSetChanged();
					mListView.setCanLoadMore(isCanLoadmore);
				}
				mListView.setSelection(0);
				break;
			case LOAD_DATA_REFRESH:
				// 当前显示的界面和异步数据请求返回的数据一致，更新数据显示
				Log.e(TAG, " 加载数据完成了");
				if (msg.arg1 == LEFT_SECTION) {
					editor.putBoolean("Zjz", false);
					editor.commit();
				} else {
					editor.putBoolean("Fjz", false);
					editor.commit();
				}
				if (showSection == msg.arg1) {
					if (showSection == LEFT_SECTION) {
//						if (newsList != null) {
//							newsList.clear();
//						}
//						newsList.addAll(leftNewsList);
						newsList = leftNewsList;
					} else {
//						if (newsList != null) {
//							newsList.clear();
//						}
//						newsList.addAll(rightNewsList);
						newsList = rightNewsList;
					}
					mAdapter.setDataChange(scrollList, newsList);
					mAdapter.notifyDataSetChanged();
					mListView.onRefreshComplete();
				} else { // 当前显示界面和异步请求数据返回不一致 不处理
					Log.e(TAG, "另一个子界面数据返回了");
					if (msg.arg1 == LEFT_SECTION) {
						lSectionStates = null;
					} else {
						rSectionStates = null;
					}
				}
//				mListView.setCanLoadMore(isCanLoadmore);
				break;
			case LOAD_DATA_LOADMORE:
				mListView.onLoadMoreComplete();
				mListView.setCanLoadMore(isCanLoadmore);
				break;
			case LOAD_LEFT_DATA_LOADMORE:
				leftNewsList.addAll(moreLoadList);
				newsList = leftNewsList;
				mAdapter.setDataChange(scrollList, newsList);
				mAdapter.notifyDataSetChanged();
				mListView.setCanLoadMore(isCanLoadmore);
				mListView.onLoadMoreComplete();
				break;
			case LOAD_RIGHT_DATA_LOADMORE:
				rightNewsList.addAll(moreLoadList);
				newsList = rightNewsList;
				mAdapter.setDataChange(scrollList, newsList);
				mAdapter.notifyDataSetChanged();
				mListView.setCanLoadMore(isCanLoadmore);
				mListView.onLoadMoreComplete();
				break;
			case LOAD_LB_DATA_REFRESH:
				mAdapter.setDataChange(scrollList, newsList);
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
		// 可接收从activity传来的bundle数据
		Log.d(TAG, ">>>>>>>>>gxfrg creat");
		// fragment可见时加载数据
		Bundle args = getArguments();
		if (showSection == -1) {
			showSection = 0;
		}
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
	 * 创建，加载对应Fragment的布局文件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, ">>>>>>>>>gxfrg creatView");

		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.fragment_layout_with_section, null);
		mListView = (CustomListViewUnused) view.findViewById(R.id.mListView);
		mainLayout = (RelativeLayout) view.findViewById(R.id.custom_listview_layout);

		mListView.setTheme(isDayTheme);
		if (isDayTheme) {
			ThemeUtil.setBackgroundDay(activity, mainLayout);
		}else {
			ThemeUtil.setBackgroundNight(activity, mainLayout);
		}

		setListener();

		if (!isPre_Create_View) { // 设置页面已加载标志位
			isPre_Create_View = true;
		}

		if (isHintVisibleFirst) { // 直接点击的TAB，visible方法先执行,页面未加载，数据处理放这里
			Log.e(TAG, "HINT IS FIRST THAN CREAT===");
			isPre_Create_View = false;
			showDataToView(); // 初始化数据及数据显示
		} else { // 正常流程，这边进行预加载
			if (!isDataClear) { // 没进行过数据的优化保存，需从数据库预加载
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
			if (mAdapter == null) {
				initAdapter();
			}
			if (isDataClear) {
				handler.obtainMessage(LOAD_DATABASE_COMPLETE).sendToTarget(); // 先将页面保存或加载的数据显示在界面中
			}

			if (sp.getBoolean("Zjz", true)) { // 显示时未请求网络数据同步，数据为数据库预加载
				mListView.handSetToRefresh(); // 自动下拉刷新，请求数据同步
			}
		} else { // 数据项为空，加载数据
			showSection = LEFT_SECTION;
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
				Log.e(TAG, "==>per load than visible [>>YES<<]");

				showDataToView();

			} else { // 没有进行预加载数据,SP等对象未被初始化
				Log.e(TAG, "==>set user Visible  by  click [>>NO<<]");
				if (!isDataClear) { // 没进行过数据的优化保存，需从数据库预加载
					loadDataFromDatabase();
				}
				isHintVisibleFirst = true; // 标志位，将数据的初始化放置在createview方法中
			}

		} else {
			// fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	/**
	 * 设置关于滑动列表的相关监听
	 */
	private void setListener() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(TAG, "click position>>>>>" + position);

				ArticleInfo article = newsList.get(position - 2);

				if (article != null) {
					ArticleJumpUtil.jumpToArticleDetal(activity, article, "公司",false);
				}
			}
		});

		mListView.setOnRefreshListener(new CustomListViewUnused.OnRefreshListener() {
			@Override
			public void onRefresh() {
				Log.e(TAG, "onRefresh");
				loadData(LOAD_DATA_REFRESH);
			}

		});

	}

	/**
	 * 加载数据库的数据 TODO
	 */
	private void loadDataFromDatabase() {
		ArticleConfig lbConfig = new ArticleConfig();
		lbConfig.setLocalArticle(true);
		lbConfig.setType(RequestType.GSLB);

		ArticleManager.getInstence().getArticleInfo(lbConfig,
				new ArticleInfoCallback() {

					@Override
					public void onArticleInfoCallback(List<ArticleInfo> infos,
							RequestType type) {
						if (scrollList != null && scrollList.size() > 0) {

						} else {
							scrollList = (ArrayList<ArticleInfo>) infos;
							Log.d(TAG,
									"gslb content database==>" + infos.size());
							if (scrollList != null && scrollList.size() != 0) {
								handler.obtainMessage(LOAD_DATABASE_COMPLETE)
										.sendToTarget();
							}
						}
					}
				});
		ArticleConfig zjzConfig = new ArticleConfig();
		zjzConfig.setLocalArticle(true);
		zjzConfig.setType(RequestType.GSZJZ);

		ArticleManager.getInstence().getArticleInfo(zjzConfig,
				new ArticleInfoCallback() {

					@Override
					public void onArticleInfoCallback(List<ArticleInfo> infos,
							RequestType type) {
						if (newsList != null && newsList.size() > 0) {

						} else {
							if (leftNewsList != null) {
								leftNewsList.clear();
								leftNewsList.addAll(infos);
							} else {
								leftNewsList = new ArrayList<ArticleInfo>();
								leftNewsList.addAll(infos);
							}
							Log.e(TAG, "database left==>" + leftNewsList.size());
							newsList = (ArrayList<ArticleInfo>) infos;
							Log.d(TAG,
									"gslb content database==>" + infos.size());
							if (newsList != null && newsList.size() != 0) {
								isCanLoadmore = true;
								handler.obtainMessage(LOAD_DATABASE_COMPLETE)
										.sendToTarget();
							}
						}
					}
				});
		ArticleConfig fjzConfig = new ArticleConfig();
		fjzConfig.setLocalArticle(true);
		fjzConfig.setType(RequestType.GSFJZ);

		ArticleManager.getInstence().getArticleInfo(fjzConfig,
				new ArticleInfoCallback() {

					@Override
					public void onArticleInfoCallback(List<ArticleInfo> infos,
							RequestType type) {
						Log.d(TAG, "gslb content database==>" + infos.size());
						rightNewsList = (ArrayList<ArticleInfo>) infos;
					}
				});

	}

	private void initAdapter() {
		if (mAdapter == null) {
			mAdapter = new NewsSectionAdapter(activity, scrollList, newsList,
					"company", isDayTheme, isTextMode);
		}

		mListView.setAdapter(mAdapter);

		mAdapter.setItemPosRecordListener(new ListviewPosRecrodCallback() {

			@Override
			public void setPositionRecrod(int position, int scrollY) {
				if (showSection == LEFT_SECTION) {
					lItem = position;
					lPos = scrollY;
				} else {
					rItem = position;
					rPos = scrollY;
				}
			}
		});

		mAdapter.setToggleChecked(new ToggleCheckedCallback() {

			@Override
			public void onToggleChecked(int position) {
				// toggleListenr.onToggleChecked(position);

				Log.d(TAG, "toggle点击了===" + position);
				showSection = position;

				// mAdapter.setToggleClickNotify(true);
				// 点击栏目切换过后，切换数据源，判断是否是第一次点击，第一次点击得主动刷新
				if (position == LEFT_SECTION) {
					// 保存当前的加载状态
					rSectionStates = mListView.getHeaderState();
					// 切换到点击的当前栏目的状态
					mListView.setLoadState(lSectionStates);

					newsList = leftNewsList;
//					newsList.clear();

					if (sp.getBoolean("Zjz", true)) {
						mAdapter.notifyDataSetChanged();
						mListView.handSetToRefresh();
					} else { // 直接更新数据
						Log.e(TAG, "click left==>" + leftNewsList.size());
//						newsList.addAll(leftNewsList);
						mAdapter.setDataChange(scrollList, newsList);
						mAdapter.notifyDataSetChanged();
					}
				} else {
					lSectionStates = mListView.getHeaderState();
					mListView.setLoadState(rSectionStates);

					newsList = rightNewsList;
//					newsList.clear();
					if (sp.getBoolean("Fjz", true)) { // 第一次加载右边栏目
//						if (rightNewsList != null && rightNewsList.size() > 0) {
//							newsList.addAll(rightNewsList);
//						}
						mAdapter.setDataChange(scrollList, newsList);
						mAdapter.notifyDataSetChanged();
						mListView.handSetToRefresh();
					} else { // 直接加载数据
//						newsList.addAll(rightNewsList);
						mAdapter.setDataChange(scrollList, newsList);
						mAdapter.notifyDataSetChanged();
					}
				}

				/******
				 * 
				 * 这部分功能还得增加下拉上拉的状态
				 * 
				 * */
				Log.d("hff", "滑动么====》" + lItem + lPos);
				if (showSection == LEFT_SECTION) {
					mListView.setSelectionFromTop(lItem, lPos);
				} else {
					mListView.setSelectionFromTop(rItem, rPos);
				}
			}
		});

		mListView.setPinnedHeaderView(activity, LayoutInflater.from(activity)
				.inflate(R.layout.switch_item_section, mListView, false),
				isDayTheme, true);

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
			isLbReturn = false;
			isListReturn = false;
			// 先判断当前页面是哪个子页面，根据子页面进行不同的数据请求
			ArticleConfig refreshConfig = new ArticleConfig();
			refreshConfig.setLocalArticle(false);
			// 设置请求的种类
			if (showSection == LEFT_SECTION) {
				refreshConfig.setType(RequestType.GSZJZ);
				MobclickAgent.onEvent(activity, UmenAnalytics.EVENT_COMPANY_ZJZ_REFRESH);
			} else {
				refreshConfig.setType(RequestType.GSFJZ);
				MobclickAgent.onEvent(activity, UmenAnalytics.EVENT_COMPANY_FJZ_REFRESH);
			}

			ArticleManager.getInstence().getArticleInfo(refreshConfig,
					new ArticleInfoCallback() {
						@Override
						public void onArticleInfoCallback(
								List<ArticleInfo> infos, RequestType type) {
							isListReturn = true;

							if (infos != null) {
								Log.d(TAG,
										"kx content netdata==>" + infos.size());

								Message msg = new Message();
								msg.what = LOAD_DATA_REFRESH;

								if (type == RequestType.GSZJZ) { // 公司正价值的情况
									msg.arg1 = LEFT_SECTION;
									leftNewsList = (ArrayList<ArticleInfo>) infos; // 保存左边子项的数据
								} else {
									msg.arg1 = RIGHT_SECTION;
									rightNewsList = (ArrayList<ArticleInfo>) infos; // 保存右边子项的数据
								}

								if (infos.size() < 15) {
									isCanLoadmore = false;
								} else {
									isCanLoadmore = true;
								}

							//	if (isLbReturn && isListReturn) {
									handler.sendMessage(msg);
//								}

							} else {
								isCanLoadmore = true;
								Message msg = new Message();
								msg.what = LOAD_DATA_REFRESH;

								if (type == RequestType.LCYW) { // 公司正价值的情况
									msg.arg1 = LEFT_SECTION;
								} else if (type == RequestType.IPO) {
									msg.arg1 = RIGHT_SECTION;
								}
									handler.sendMessage(msg);
//								}
								Toast.makeText(activity, "当前网络状况较差，请检查后重试", Toast.LENGTH_SHORT).show();
							}

						}
					});

			// 请求公司轮播的图片数据
			ArticleConfig config = new ArticleConfig();
			config.setLocalArticle(false);
			config.setType(RequestType.GSLB);

			ArticleManager.getInstence().getArticleInfo(config,
					new ArticleInfoCallback() {

						@Override
						public void onArticleInfoCallback(
								List<ArticleInfo> infos, RequestType type) {
							isLbReturn = true;

							if (infos != null) {
								Log.d(TAG,
										"gslb content network==>"
												+ infos.size());
								scrollList = (ArrayList<ArticleInfo>) infos;
//								if (isLbReturn && isListReturn) {
									handler.obtainMessage(LOAD_LB_DATA_REFRESH)
									.sendToTarget();
//								}
							} else {
//								if (isLbReturn && isListReturn) {
									handler.obtainMessage(LOAD_LB_DATA_REFRESH)
									.sendToTarget();
//								}
							}
						}
					});
			break;

		case LOAD_DATA_LOADMORE:
				ArticleConfig loadmoreConfig = new ArticleConfig();
				loadmoreConfig.setLocalArticle(false);
				// 设置请求的种类
				if (showSection == LEFT_SECTION) {
					loadmoreConfig.setType(RequestType.GSZJZ);
					MobclickAgent.onEvent(activity, UmenAnalytics.EVENT_COMPANY_ZJZ_MORE);
				} else {
					loadmoreConfig.setType(RequestType.GSFJZ);
					MobclickAgent.onEvent(activity, UmenAnalytics.EVENT_COMPANY_FJZ_MORE);
				}
				if (newsList != null && newsList.size() > 0) {
					int newsListLen = newsList.size();
					ArticleInfo lastArticle = newsList.get(newsListLen - 1);
					loadmoreConfig.setMaxId(lastArticle.getPos());
				}
				ArticleManager.getInstence().getArticleInfo(loadmoreConfig,
						new ArticleInfoCallback() {

							@Override
							public void onArticleInfoCallback(
									List<ArticleInfo> infos, RequestType type) {

								if (infos != null) {
									Log.d(TAG,
											"kx content netdata==>"
													+ infos.size());
									if (type == RequestType.GSZJZ) { // 公司正价值的情况
//										leftNewsList.addAll(infos); // 保存左边子项的数据
										moreLoadList = (ArrayList<ArticleInfo>) infos;
										handler.obtainMessage(
												LOAD_LEFT_DATA_LOADMORE)
												.sendToTarget();
									} else {
//										rightNewsList.addAll(infos); // 保存右边子项的数据
										moreLoadList = (ArrayList<ArticleInfo>) infos;
										handler.obtainMessage(
												LOAD_RIGHT_DATA_LOADMORE)
												.sendToTarget();
									}
//									newsList.addAll(infos);

								} else {
									handler.obtainMessage(LOAD_DATA_LOADMORE)
											.sendToTarget();
									Toast.makeText(activity, "当前网络状况较差，请检查后重试", Toast.LENGTH_SHORT).show();

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
		// 初始化自动刷新
		if (sp.getBoolean("Zjz", true)) {
			if (mAdapter == null) {
				initAdapter();
			}
			mListView.handSetToRefresh();
		} else {
			loadDataFromDatabase();
		}
	}

	/**
	 * 摧毁视图
	 */
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mAdapter = null;
		if (leftNewsList != null) {
			restoreDataList(leftNewsList);
		}

		if (rightNewsList != null) {
			restoreDataList(rightNewsList);
		}
		if (newsList != null) {
//			newsList.clear();
//			newsList.addAll(leftNewsList);
			newsList = leftNewsList;
			isDataClear = true;
		}

		lItem = 0;
		rItem = 0;
		lPos = 0;
		rPos = 0;
		isPre_Create_View = false;
		isFooterAdd = false;
	}

	/**
	 * 存储15条初始数据
	 * 
	 * @param restoreList
	 */
	private void restoreDataList(ArrayList<ArticleInfo> restoreList) {

		List<ArticleInfo> storeList = new ArrayList<ArticleInfo>();
		if (restoreList.size() < 15) {
			for (int i = 0; i < restoreList.size(); i++) {
				storeList.add(restoreList.get(i));
			}
		} else {
			for (int i = 0; i < 15; i++) {
				storeList.add(restoreList.get(i));
			}
		}

		restoreList.clear();
		restoreList.addAll(storeList);
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
		Log.e("newsclick", article.getTitle());
		if (article != null) {
			ArticleJumpUtil.jumpToArticleDetal(activity, article, "公司",false);
		}
	}

	public void changeTheme(boolean isDay) {
		this.isDayTheme = isDay;
		if (mainLayout != null) {
			if (isDayTheme) {
				ThemeUtil.setBackgroundDay(activity, mainLayout);
			}else {
				ThemeUtil.setBackgroundNight(activity, mainLayout);
			}
		}
		if (mListView != null) {
			mListView.changePinnedThemeUi(activity, isDay, true);
			mListView.setTheme(isDay);
		}
		if (mAdapter != null) {
			mAdapter.changeThem(isDay);
			mAdapter.notifyDataSetChanged();
		}
	}

	public void changeMode(boolean isText) {
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
}
