package cn.com.nbd.nbdmobile.fragment;
//package com.nbd.nbdnews.fragment;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.AdapterView.OnItemClickListener;
//
//import com.nbd.article.bean.ArticleConfig;
//import com.nbd.article.bean.ArticleInfo;
//import com.nbd.article.manager.ArticleManager;
//import com.nbd.article.managercallback.ArticleInfoCallback;
//import com.nbd.nbdnews.R;
//import com.nbd.nbdnews.adapter.NewsSectionAdapter;
//import com.nbd.nbdnews.callback.ListviewPosRecrodCallback;
//import com.nbd.nbdnews.callback.ToggleCheckedCallback;
//import com.nbd.network.bean.RequestType;
//import com.nbd.nubnews.utility.FragmentType;
//import com.nbd.view.CustomListView;
//
///**
// * 带有两个子选项的停靠栏的界面，用于公司和理财
// * 
// * @author riche
// *
// */
//public class NewsSectionFragment extends Fragment {
//	private final static String TAG = "GX_NEWS";
//	private Activity activity; // 获取到对于的fragment的activity
//
//	// 当前页面显示的子栏目内容
//	private final static int LEFT_SECTION = 0;
//	private final static int RIGHT_SECTION = 1;
//
//	private ArrayList<ArticleInfo> scrollList = new ArrayList<ArticleInfo>(); // 头部轮播的列表
//	private ArrayList<ArticleInfo> newsList = new ArrayList<ArticleInfo>(); // 用于传入adapter的新闻内容列表
//	private ArrayList<ArticleInfo> rightNewsList = new ArrayList<ArticleInfo>(); // 右边栏目的新闻内容列表
//	private ArrayList<ArticleInfo> leftNewsList = new ArrayList<ArticleInfo>(); // 左边栏目的新闻内容列表
//
//	private CustomListView mListView; // 带停靠栏的滑动列表
//
//	private NewsSectionAdapter mAdapter; // 处理停靠栏的适配器
//
//	private int showSection = -1; // 显示的子内容
//
//	private boolean isLeftFristLoad = true; // 是否是第一次加载，第一次加载会主动下拉
//	private boolean isRightFristLoad = true; // 是否是第一次加载，第一次加载会主动下拉
//
//	public final static int LOAD_DATABASE_COMPLETE = 0;
//	public final static int LOAD_DATA_REFRESH = 1;
//	public final static int LOAD_DATA_LOADMORE = 2;
//
//	private int lItem; // 左边选择项的第一个显示item位置
//	private int rItem; // 右边选择项的第一个显示item位置
//	private int lPos; // 左边第一个显示项的滑动距离
//	private int rPos; // 右边第一个显示项的滑动距离
//
//	// 切换时保存当前子栏目的头部尾部状态
//	private Map<String, Integer> lSectionStates;
//	private Map<String, Integer> rSectionStates;
//
//	private RequestType scrollRequest;
//	private RequestType leftRequest;
//	private RequestType rightRequest;
//
//	/**
//	 * 构造方法，构造请求的参数
//	 */
//	public NewsSectionFragment(FragmentType type) {
//		switch (type) {
//		case GS:
//			scrollRequest = RequestType.GSLB;
//			leftRequest = RequestType.GSZJZ;
//			rightRequest = RequestType.GSFJZ;
//			break;
//		case LC:
//			scrollRequest = RequestType.LCLB;
//			leftRequest = RequestType.LCYW;
//			rightRequest = RequestType.IPO;
//			break;
//
//		default:
//			break;
//		}
//	}
//
//	/**
//	 * 处理文章数据回调的事件
//	 * 
//	 */
//	Handler handler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			if (mAdapter == null) {
//				Log.d(TAG, "adapter null><>");
//				initAdapter();
//			}
//			switch (msg.what) {
//			case LOAD_DATABASE_COMPLETE:
//				mAdapter.setDataChange(scrollList, newsList);
//				mAdapter.notifyDataSetChanged();
//				break;
//			case LOAD_DATA_REFRESH:
//				// 当前显示的界面和异步数据请求返回的数据一致，更新数据显示
//				Log.e("hff", " 加载数据完成了");
//				if (msg.arg1 == LEFT_SECTION) {
//					isLeftFristLoad = false;
//				} else {
//					isRightFristLoad = false;
//				}
//				if (showSection == msg.arg1) {
//					if (showSection == LEFT_SECTION) {
//						newsList.clear();
//						newsList.addAll(leftNewsList);
//					} else {
//						newsList.clear();
//						newsList.addAll(rightNewsList);
//					}
//					mAdapter.setDataChange(scrollList, newsList);
//					mAdapter.notifyDataSetChanged();
//					mListView.onRefreshComplete();
//				} else { // 当前显示界面和异步请求数据返回不一致 不处理
//					Log.e("hff", "另一个子界面数据返回了");
//					if (msg.arg1 == LEFT_SECTION) {
//						lSectionStates = null;
//					} else {
//						rSectionStates = null;
//					}
//				}
//				break;
//			case LOAD_DATA_LOADMORE:
//				mAdapter.setDataChange(scrollList, newsList);
//				mAdapter.notifyDataSetChanged();
//				mListView.onLoadMoreComplete();
//				break;
//			default:
//				break;
//			}
//			super.handleMessage(msg);
//		}
//	};
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		// 可接收从activity传来的bundle数据
//		Log.d("hff", ">>>>>>>>>gxfrg creat");
//		// fragment可见时加载数据
//		Bundle args = getArguments();
//		if (showSection == -1) {
//			showSection = 0;
//		}
//
//		// 获取数据库数据
//		loadDataFromDatabase();
//
//		super.onCreate(savedInstanceState);
//	}
//
//	/**
//	 * 加载数据库的数据 TODO
//	 */
//	private void loadDataFromDatabase() {
//		ArticleConfig config = new ArticleConfig();
//		config.setLocalArticle(true);
//		config.setType(scrollRequest);
//
//		ArticleManager.getInstence().getArticleInfo(config,
//				new ArticleInfoCallback() {
//
//					@Override
//					public void onArticleInfoCallback(List<ArticleInfo> infos,
//							RequestType type) {
//						newsList = (ArrayList<ArticleInfo>) infos;
//
//						Log.d(TAG, "gslb content database==>" + infos.size());
//						if (newsList != null && newsList.size() != 0) {
//							handler.obtainMessage(LOAD_DATABASE_COMPLETE)
//									.sendToTarget();
//						}
//
//					}
//				});
//
//	}
//
//	/**
//	 * 通过这样的方法获取activity实例，以免在嵌套fragment过程中，再加载activity为null的问题
//	 */
//	@Override
//	public void onAttach(Activity activity) {
//		Log.d("hff", ">>>>>>>>>gxfrg onAttach");
//		this.activity = activity;
//		super.onAttach(activity);
//	}
//
//	/**
//	 * 此方法意思为fragment是否可见 ,可见时候加载数据 不做这个处理，会加载多个fragment数据
//	 */
//	@Override
//	public void setUserVisibleHint(boolean isVisibleToUser) {
//		if (isVisibleToUser) {
//
//			if (mAdapter != null) {
//				// Log.d("hff>>>	", "xiangshile shuaxin   sdasdasda s  @$@!#!");
//				// mAdapter.notifyDataSetChanged();
//			}
//			Log.d("hff", ">>>>>>>>>gxfrg isVisibleToUser");
//		} else {
//			if (mListView!=null) {
//				mListView.refreshListState();
//			}
//			// fragment不可见时不执行操作
//		}
//		super.setUserVisibleHint(isVisibleToUser);
//	}
//
//	/**
//	 * 创建，加载对应Fragment的布局文件
//	 */
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		Log.d("hff", ">>>>>>>>>gxfrg creatView");
//		View view = LayoutInflater.from(getActivity()).inflate(
//				R.layout.kx_fragment_layout, null);
//		mListView = (CustomListView) view.findViewById(R.id.mListView);
//
//		initAdapter();
//
//		initData();
//		return view;
//	}
//
//	private void initAdapter() {
//		if (mAdapter == null) {
//			mAdapter = new NewsSectionAdapter(activity, scrollList, null,
//					newsList);
//		}
//
//		mListView.setAdapter(mAdapter);
//
//		mAdapter.setItemPosRecordListener(new ListviewPosRecrodCallback() {
//
//			@Override
//			public void setPositionRecrod(int position, int scrollY) {
//				if (showSection == LEFT_SECTION) {
//					lItem = position;
//					lPos = scrollY;
//				} else {
//					rItem = position;
//					rPos = scrollY;
//				}
//			}
//		});
//
//		mAdapter.setToggleChecked(new ToggleCheckedCallback() {
//
//			@Override
//			public void onToggleChecked(int position) {
//				// toggleListenr.onToggleChecked(position);
//
//				Log.d("hff", "toggle点击了===" + position);
//				showSection = position;
//
//				// mAdapter.setToggleClickNotify(true);
//				// 点击栏目切换过后，切换数据源，判断是否是第一次点击，第一次点击得主动刷新
//				if (position == LEFT_SECTION) {
//					// 保存当前的加载状态
//					rSectionStates = mListView.getHeaderState();
//					mListView.setLoadState(lSectionStates);
//
//					newsList.clear();
//					if (isLeftFristLoad) {
//						mListView.handSetToRefresh();
//					} else { // 直接更新数据
//						newsList.addAll(leftNewsList);
//						mAdapter.setDataChange(scrollList, newsList);
//						mAdapter.notifyDataSetChanged();
//					}
//				} else {
//					lSectionStates = mListView.getHeaderState();
//					mListView.setLoadState(rSectionStates);
//
//					newsList.clear();
//					if (isRightFristLoad) { // 第一次加载右边栏目
//						mListView.handSetToRefresh();
//					} else { // 直接加载数据
//						newsList.addAll(rightNewsList);
//						mAdapter.setDataChange(scrollList, newsList);
//						mAdapter.notifyDataSetChanged();
//					}
//				}
//
//				/******
//				 * 
//				 * 这部分功能还得增加下拉上拉的状态
//				 * 
//				 * */
//				Log.d("hff", "滑动么====》" + lItem + lPos);
//				if (showSection == LEFT_SECTION) {
//					mListView.setSelectionFromTop(lItem, lPos);
//				} else {
//					mListView.setSelectionFromTop(rItem, rPos);
//				}
//			}
//		});
//
//		mListView.setPinnedHeaderView(LayoutInflater.from(activity).inflate(
//				R.layout.switch_item_section, mListView, false));
//
//		mListView.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//
//				Log.d("hff", "click position>>>>>" + position);
//
//			}
//		});
//
//		mListView.setOnRefreshListener(new CustomListView.OnRefreshListener() {
//			@Override
//			public void onRefresh() {
//				// TODO 下拉刷新
//				Log.e(TAG, "onRefresh");
//				loadData(LOAD_DATA_REFRESH);
//			}
//
//		});
//
//		mListView.setOnLoadListener(new CustomListView.OnLoadMoreListener() {
//
//			@Override
//			public void onLoadMore() {
//				// TODO 加载更多
//				Log.e(TAG, "onLoad");
//				// loadData(LOAD_DATA_LOADMORE);
//			}
//		});
//
//	}
//
//	/**
//	 * 加载网络数据 下拉 上拉 根据不同的情况进行分开的网络请求
//	 * 
//	 * @param loadDataRefresh
//	 */
//	private void loadData(int loadDataRefresh) {
//		switch (loadDataRefresh) {
//		case LOAD_DATA_REFRESH:// 这里是下拉刷新
//			// 先判断当前页面是哪个子页面，根据子页面进行不同的数据请求
//			ArticleConfig refreshConfig = new ArticleConfig();
//			refreshConfig.setLocalArticle(false);
//			// 设置请求的种类
//			if (showSection == LEFT_SECTION) {
//				refreshConfig.setType(leftRequest);
//			} else {
//				refreshConfig.setType(rightRequest);
//			}
//
//			ArticleManager.getInstence().getArticleInfo(refreshConfig,
//					new ArticleInfoCallback() {
//						@Override
//						public void onArticleInfoCallback(
//								List<ArticleInfo> infos, RequestType type) {
//
//							if (infos != null) {
//								Log.d(TAG,
//										"kx content netdata==>" + infos.size());
//
//								Message msg = new Message();
//								msg.what = LOAD_DATA_REFRESH;
//
//								if (type == RequestType.GSZJZ) { // 公司正价值的情况
//									msg.arg1 = LEFT_SECTION;
//									leftNewsList = (ArrayList<ArticleInfo>) infos; // 保存左边子项的数据
//								} else {
//									msg.arg1 = RIGHT_SECTION;
//									rightNewsList = (ArrayList<ArticleInfo>) infos; // 保存右边子项的数据
//								}
//								handler.sendMessage(msg);
//								// handler.obtainMessage(msg)
//								// .sendToTarget();
//							} else {
//								handler.obtainMessage(LOAD_DATA_REFRESH)
//										.sendToTarget();
//
//							}
//
//						}
//					});
//			break;
//
//		case LOAD_DATA_LOADMORE:
//
//			ArticleConfig loadmoreConfig = new ArticleConfig();
//			loadmoreConfig.setLocalArticle(false);
//			// 设置请求的种类
//			if (showSection == LEFT_SECTION) {
//				loadmoreConfig.setType(leftRequest);
//			} else {
//				loadmoreConfig.setType(rightRequest);
//			}
//			ArticleManager.getInstence().getArticleInfo(loadmoreConfig,
//					new ArticleInfoCallback() {
//
//						@Override
//						public void onArticleInfoCallback(
//								List<ArticleInfo> infos, RequestType type) {
//
//							if (infos != null) {
//								Log.d(TAG,
//										"kx content netdata==>" + infos.size());
//								newsList.addAll(infos);
//								if (newsList != null && newsList.size() != 0) {
//									handler.obtainMessage(LOAD_DATA_LOADMORE)
//											.sendToTarget();
//								}
//
//							} else {
//								handler.obtainMessage(LOAD_DATA_LOADMORE)
//										.sendToTarget();
//
//							}
//
//						}
//					});
//
//			// 这里是上拉刷新
//			// int _Index = mCount + 10;
//			// mCount = _Index;
//			break;
//		}
//
//	}
//
//	/**
//	 * 初始化数据，传递给adapter显示 获取方案未定，可以ACTIVITY获取，可以在该fragment获取
//	 */
//	private void initData() {
//
//		ArticleConfig config = new ArticleConfig();
//		config.setLocalArticle(false);
//		config.setType(scrollRequest);
//
//		ArticleManager.getInstence().getArticleInfo(config,
//				new ArticleInfoCallback() {
//
//					@Override
//					public void onArticleInfoCallback(List<ArticleInfo> infos,
//							RequestType type) {
//						if (infos != null) {
//							Log.d(TAG, "gslb content network==>" + infos.size());
//							scrollList = (ArrayList<ArticleInfo>) infos;
//							handler.obtainMessage(LOAD_DATA_REFRESH)
//									.sendToTarget();
//						} else {
//							handler.obtainMessage(LOAD_DATA_REFRESH)
//									.sendToTarget();
//						}
//					}
//				});
//		// 初始化自动刷新
//		mListView.handSetToRefresh();
//
//	}
//
//	/**
//	 * 摧毁视图
//	 */
//	@Override
//	public void onDestroyView() {
//		super.onDestroyView();
//		mAdapter = null;
//	}
//
//	/**
//	 * 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁
//	 */
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//	}
//
//}
