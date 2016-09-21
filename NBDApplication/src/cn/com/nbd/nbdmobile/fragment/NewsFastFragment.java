package cn.com.nbd.nbdmobile.fragment;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;

import cn.com.nbd.nbdmobile.adapter.NewsFastAdapter;
import cn.com.nbd.nbdmobile.adapter.NewsFastAdapter.OnNewsClickListener;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.ArticleJumpUtil;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.view.CustomListViewUnused;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.ArticleInfoCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

/**
 * 快讯滚动的fragment界面 纯文字版的列表新闻布局
 * 
 * @author riche
 * 
 */
public class NewsFastFragment extends Fragment {

	private final static String TAG = "FAST_NEWS";
	private final String mPageName = "FastFragmet";

	private Activity activity; // 获取到对于的fragment的activity
	private View view; // 停靠栏的加载视图
	private CustomListViewUnused mListView; // 带停靠栏的滑动列表
	private RelativeLayout mainLayout;
	

	private ArrayList<ArticleInfo> newsList = new ArrayList<ArticleInfo>(); // 新闻内容
	private ArrayList<ArticleInfo> returnList = new ArrayList<ArticleInfo>(); // 网络请求接口的返回数据

	private NewsFastAdapter mAdapter; // 处理停靠栏的适配器

	// handler处理三种数据请求完成情况
	public final static int LOAD_DATABASE_COMPLETE = 0; // 数据库加载完成
	public final static int LOAD_DATA_REFRESH = 1; // 下拉加载数据完成
	public final static int LOAD_DATA_LOADMORE = 2; // 上拉加载更多数据完成
	private final static int PAGE_COUNT = 15; // 单次请求数据的条数

	private int page = 0; // 当前请求的页数

	private boolean isCanloamore = true; // 数据加载完否，能否继续上拉
	private boolean isClearData;
	private boolean isLoadMoreNotify;
	private boolean isAddFooterListener; // 是否加载了上拉加载更多的监听
	private boolean isHintVisibleFirst; // fragment的visible方法之于oncreat方法之前，UI没有初始化，则在oncreat中获取数据
	private boolean isPre_Create_View; // Fragment 页面被预加载了，执行了ONCREATE
	private boolean isDayTheme; // 是否是日间模式
	private boolean isNotFirstLoad;

	private SharedPreferences firstLoadSp; // 用于存放第一次加载的本地数据存储

	/**
	 * 构造函数
	 */
	public NewsFastFragment() {
		super();
	}

	/**
	 * 初始化时界面的日夜间模式
	 * 
	 * @param isDay
	 */
	public void initTheme(boolean isDay) {
		isDayTheme = isDay;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	/**
	 * 通过这样的方法获取activity实例，以免在嵌套fragment过程中，再加载activity为null的问题
	 */
	@Override
	public void onAttach(Activity activity) {
		this.activity = activity;
		firstLoadSp = activity.getSharedPreferences("FirstLoad",
				Context.MODE_PRIVATE);
		super.onAttach(activity);
	}

	/**
	 * 创建，加载对应Fragment的布局文件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (view == null) {
			view = LayoutInflater.from(getActivity()).inflate(
					R.layout.fragment_layout_with_section, null);
		}

		mListView = (CustomListViewUnused) view.findViewById(R.id.mListView);
		mainLayout = (RelativeLayout) view.findViewById(R.id.custom_listview_layout);
		if (isDayTheme) {
			ThemeUtil.setBackgroundDay(activity, mainLayout);
		}else {
			ThemeUtil.setBackgroundNight(activity, mainLayout);
		}
		mListView.setTheme(isDayTheme);
		

		setListener(); // 设置监听

		if (!isPre_Create_View) { // 设置页面已加载标志位
			Log.e(TAG, "make page true");
			isPre_Create_View = true;
		}

		if (isHintVisibleFirst) { // 直接点击的TAB，visible方法先执行,页面未加载，数据处理放这里
			isPre_Create_View = false;
			showDataToView(); // 初始化数据及数据显示
			if (isNotFirstLoad) {
				isPre_Create_View = true;
			}

		} else { // 正常流程，这边进行预加载
			loadDataFromDatabase();
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
			handler.obtainMessage(LOAD_DATABASE_COMPLETE).sendToTarget(); // 先将页面保存或加载的数据显示在界面中

			if (firstLoadSp.getBoolean("Kx", true)) { // 显示时未请求网络数据同步，数据为数据库预加载
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
				loadDataFromDatabase();
				isHintVisibleFirst = true; // 标志位，将数据的初始化放置在createview方法中
				if (!isNotFirstLoad) {
					isNotFirstLoad = true;
				}
			}
		} else {
		}
		super.setUserVisibleHint(isVisibleToUser);
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
			if (!isAddFooterListener) {
				Log.e(TAG, "add footer");
				mListView
						.setOnLoadListener(new CustomListViewUnused.OnLoadMoreListener() {

							@Override
							public void onLoadMore() {
								// TODO 加载更多
								Log.i(TAG, "onLoad");
								page = page + 1;
								loadData(LOAD_DATA_LOADMORE);
							}
						});
				isAddFooterListener = true;
			}

			mListView.setCanLoadMore(isCanloamore);

			switch (msg.what) {
			case LOAD_DATABASE_COMPLETE:
				mAdapter.setDataChange(newsList);
				mAdapter.notifyDataSetChanged();
				break;
			case LOAD_DATA_REFRESH:
				if(newsList != null){
					newsList.addAll(returnList);
				}else{
					newsList = returnList;
				}
				mAdapter.setDataChange(newsList);
				mAdapter.notifyDataSetChanged();
				mListView.onRefreshComplete();
				break;
			case LOAD_DATA_LOADMORE:
				if (returnList != null) {
					newsList.addAll(returnList);
					mAdapter.setDataChange(newsList);
					mAdapter.notifyDataSetChanged();
				}
				if (!isCanloamore) {
					mListView.setCanLoadMore(false);
					mListView.onLoadMoreComplete();
					isLoadMoreNotify = true;
				} else {
					mListView.onLoadMoreComplete();
					isLoadMoreNotify = true;
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

	/**
	 * 设置下拉 上拉监听，好初始化时手动调用下拉刷新
	 */
	private void setListener() {

		mListView
				.setOnRefreshListener(new CustomListViewUnused.OnRefreshListener() {
					@Override
					public void onRefresh() {
						// TODO 下拉刷新
						Log.i(TAG, "onRefresh");
						if (firstLoadSp.getBoolean("Kx", true)) {
							SharedPreferences.Editor editor = firstLoadSp
									.edit();
							editor.putBoolean("Kx", false);
							editor.commit();
						}
						page = 0;
						loadData(LOAD_DATA_REFRESH);
					}

				});

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mAdapter.checkeContentOpen(view, position);
				Log.e("fast click==", "" + position);

				if (newsList != null && newsList.size() >= position) {
					ArticleJumpUtil.addClickCount(newsList.get(position - 1)
							.getArticle_id(), "",true);
				}

			}
		});

	}

	/**
	 * 初始化数据，传递给adapter显示 获取方案未定，可以ACTIVITY获取，可以在该fragment获取
	 */
	private void initData() {

		if (firstLoadSp.getBoolean("Kx", true)) { // 第一次加载
			if (mAdapter == null) {
				initAdapter();
			}
			Log.i(TAG, "First load this page");
			mListView.handSetToRefresh();
		} else {
			loadDataFromDatabase();
		}
	}

	/**
	 * 有数据的时候初始化适配器
	 */
	private void initAdapter() {

		if (mAdapter == null) {
			mAdapter = new NewsFastAdapter(activity, newsList, isDayTheme);
		}

		mListView.setAdapter(mAdapter);
		mListView.setPinnedHeaderView(activity, LayoutInflater.from(activity)
				.inflate(R.layout.section_fast_head, mListView, false),
				isDayTheme, false);

		mAdapter.setNewsClickListener(new OnNewsClickListener() {

			@Override
			public void onNewsClick(View view, ArticleInfo article) {
				showShare(article);

			}
		});

	}

	/**
	 * 通过数据库加载数据
	 */
	private void loadDataFromDatabase() {

		ArticleConfig config = new ArticleConfig();
		config.setLocalArticle(true);
		config.setType(RequestType.KX);
		ArticleManager.getInstence().getArticleInfo(config,
				new ArticleInfoCallback() {
					@Override
					public void onArticleInfoCallback(List<ArticleInfo> infos,
							RequestType type) {
						if (infos != null) { // 数据库获取数据不为空
							Log.i(TAG,
									"***[>>DATABASE<<]*** reture size ---->>>"
											+ infos.size());
							newsList = (ArrayList<ArticleInfo>) infos;
							if (newsList != null && newsList.size() != 0) {
								isCanloamore = true;
								handler.obtainMessage(LOAD_DATABASE_COMPLETE)
										.sendToTarget();
							}
						} else {
							Log.i(TAG, "***[>>DATABASE<<]*** reture --->>>"
									+ "[null]");
						}
					}
				});
	}

	/*
	 * 上下拉刷新加载数据方法
	 */
	public void loadData(final int type) {
		switch (type) {
		case LOAD_DATA_REFRESH:// 这里是下拉刷新

			MobclickAgent
					.onEvent(activity, UmenAnalytics.EVENT_ROLLING_REFRESH);

			ArticleConfig config = new ArticleConfig();
			config.setLocalArticle(false);
			config.setType(RequestType.KX);

			ArticleManager.getInstence().getArticleInfo(config,
					new ArticleInfoCallback() {

						@Override
						public void onArticleInfoCallback(
								List<ArticleInfo> infos, RequestType type) {
							if (infos != null) {
								Log.i(TAG,
										"***[>>NET<<]***  Refresh size--->>>"
												+ infos.size());
								newsList = (ArrayList<ArticleInfo>) infos;
								if (newsList != null) {
									if (newsList.size() < PAGE_COUNT) {
										isCanloamore = false;
									} else {
										isCanloamore = true;
									}
									// newsList = null;
									handler.obtainMessage(LOAD_DATA_REFRESH)
											.sendToTarget();
								}
							} else {
								Log.i(TAG,
										"***[>>NET<<]***  Refresh size--->>>  NULL");
								Toast.makeText(activity, "当前网络状况较差，请检查后重试",
										Toast.LENGTH_SHORT).show();
								isCanloamore = true;
								mListView.onRefreshComplete();
								handler.obtainMessage(LOAD_DATA_REFRESH)
										.sendToTarget();
							}
						}
					});

			break;

		case LOAD_DATA_LOADMORE:
			isLoadMoreNotify = false;
			MobclickAgent.onEvent(activity, UmenAnalytics.EVENT_ROLLING_MORE);
			ArticleConfig config1 = new ArticleConfig();
			config1.setLocalArticle(false);
			config1.setType(RequestType.KX);
			if (newsList != null && newsList.size() > 0) {
				int newsListLen = newsList.size();
				ArticleInfo lastArticle = newsList.get(newsListLen - 1);
				config1.setMaxId(lastArticle.getArticle_id());
			}
			ArticleManager.getInstence().getArticleInfo(config1,
					new ArticleInfoCallback() {

						@Override
						public void onArticleInfoCallback(
								List<ArticleInfo> infos, RequestType type) {
							if (infos != null) {
								Log.i(TAG,
										"***[>>NET<<]***  Loadmore size--->>>"
												+ infos.size());
								if (infos.size() < PAGE_COUNT) {
									isCanloamore = false;
								} else {
									isCanloamore = true;
								}
								returnList = (ArrayList<ArticleInfo>) infos;
								// newsList.addAll(infos);
								handler.obtainMessage(LOAD_DATA_LOADMORE)
										.sendToTarget();
							} else {
								returnList = null;
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
	 * 摧毁视图
	 */
	@Override
	public void onDestroyView() {
		Log.i(TAG, "==>onDestroyView");
		mAdapter = null;
//		view = null;
//		newsList = null;
		isPre_Create_View = false;
		isAddFooterListener = false;
		if (newsList != null) {
			reStoreData();
		}
		super.onDestroyView();
	}

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

	private void showShare(ArticleInfo shArticleInfo) {

		ShareSDK.initSDK(activity);
		OnekeyShare oks = new OnekeyShare();

		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(shArticleInfo.getTitle());
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(shArticleInfo.getUrl());
		// text是分享文本，所有平台都需要这个字段
		oks.setText(shArticleInfo.getDigest());
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		if (shArticleInfo.getImage() == null
				|| shArticleInfo.getImage().equals("")) {
			oks.setImageUrl("http://static.nbd.com.cn/images/nbd_icon.png");
		} else {
			oks.setImageUrl(shArticleInfo.getImage());
		}
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(shArticleInfo.getUrl());
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(shArticleInfo.getUrl());

		// oks.setCallback(new PlatformActionListener() {
		//
		// @Override
		// public void onError(Platform arg0, int arg1, Throwable arg2) {
		// // TODO Auto-generated method stub
		// Log.e("SHARE==>", "error"+arg1+arg2.toString());
		// }
		//
		// @Override
		// public void onComplete(Platform arg0, int arg1, HashMap<String,
		// Object> arg2) {
		// // TODO Auto-generated method stub
		// Log.e("SHARE==>", "complete");
		//
		// }
		//
		// @Override
		// public void onCancel(Platform arg0, int arg1) {
		// // TODO Auto-generated method stub
		// Log.e("SHARE==>", "cancle");
		//
		// }
		// });

		// 启动分享GUI
		oks.show(activity);
		
		MobclickAgent.onEvent(activity, UmenAnalytics.SHARE_ROLLING);
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
			mListView.changePinnedThemeUi(activity, isDay, false);
			mListView.setTheme(isDay);
		}
		if (mAdapter != null) {
			mAdapter.changeTheme(isDay);
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
