package cn.com.nbd.nbdmobile.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.activity.SearchActivity;
import cn.com.nbd.nbdmobile.activity.VideoPlayActivity;
import cn.com.nbd.nbdmobile.adapter.MainVideoSkinAdapter;
import cn.com.nbd.nbdmobile.adapter.MainVideoSkinAdapter.OnNewsClickListener;
import cn.com.nbd.nbdmobile.adapter.MainVideoSkinAdapter.onScreenFullChange;
import cn.com.nbd.nbdmobile.utility.ShareUtile;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.ArticleInfoCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

/**
 * 主界面视频页面面
 * 
 * @author riche
 * 
 */
public class MainVideoFragment extends Fragment {
	private final static String TAG = "VIDEO_NEWS";
	private final String mPageName = "VideoFragment";
	private Activity activity; // 获取到对于的fragment的activity

	private ArrayList<ArticleInfo> newsList = new ArrayList<ArticleInfo>(); // 用于传入adapter的新闻内容列表
	private ArrayList<ArticleInfo> returnList = new ArrayList<ArticleInfo>(); // 用于传入adapter的新闻内容列表
	// 获取到的广告推广的数据

	private WithoutSectionCustomListViewUnused mListView; // 带停靠栏的滑动列表
	private RelativeLayout mSearchBtn;

	private RelativeLayout mMainLayout;
	private MainVideoSkinAdapter mAdapter; // 处理停靠栏的适配器

	public final static int LOAD_DATABASE_COMPLETE = 0;
	public final static int LOAD_DATA_REFRESH = 1;
	public final static int LOAD_DATA_LOADMORE = 2;

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
				Log.d(TAG, "adapter null");
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
				// 当前显示的界面和异步数据请求返回的数据一致，更新数据显示
				if (sp.getBoolean("Video", true)) {
					editor.putBoolean("Video", false);
					editor.commit();
				}
				mAdapter.setDataChange(newsList);
				mAdapter.notifyDataSetChanged();
				mListView.onRefreshComplete();
				mListView.setCanLoadMore(isCanLoadmore);
				break;
			case LOAD_DATA_LOADMORE:
				if (returnList != null) {
					newsList.addAll(returnList);
				}
				mAdapter.notifyDataSetChanged();
				if (!isCanLoadmore) {
					mListView.setCanLoadMore(false);
					mListView.onLoadMoreComplete();
				} else {
					mListView.onLoadMoreComplete();
				}
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
		Log.e(TAG, "onCreat==>");
		getActivity().getWindow().setFormat(PixelFormat.TRANSLUCENT);
		// fragment可见时加载数据
		super.onCreate(savedInstanceState);
	}

	/**
	 * 加载数据库的数据 TODO
	 */
	private void loadDataFromDatabase() {
		ArticleConfig config = new ArticleConfig();
		config.setLocalArticle(true);
		config.setType(RequestType.VIDEO);

		ArticleManager.getInstence().getArticleInfo(config,
				new ArticleInfoCallback() {

					@Override
					public void onArticleInfoCallback(List<ArticleInfo> infos,
							RequestType type) {
						newsList = (ArrayList<ArticleInfo>) infos;

						Log.d(TAG, "video content database==>" + infos.size());
						if (newsList != null && newsList.size() != 0) {
							handler.obtainMessage(LOAD_DATABASE_COMPLETE)
									.sendToTarget();
						}

					}
				});

	}

	/**
	 * 通过这样的方法获取activity实例，以免在嵌套fragment过程中，再加载activity为null的问题
	 */
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, " ==>onAttach");
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
			Log.e(TAG, "==>isVisibleToUser");

		} else {
			if (mListView != null) {
				mListView.refreshListState();
			}
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
		Log.d(TAG, "==> OncreatView");
		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.activity_main_video_fragment, null);
		mListView = (WithoutSectionCustomListViewUnused) view
				.findViewById(R.id.video_listView);
		mMainLayout = (RelativeLayout) view
				.findViewById(R.id.video_main_layout);

		mListView.setTheme(isDayTheme);

		mSearchBtn = (RelativeLayout) view
				.findViewById(R.id.video_title_search);

		isCanLoadmore  = true;
		changeThemeUi();
		setListener();

		// if (mAdapter == null) {
		// initAdapter();
		// }

		loadDataFromDatabase();
		showDataToView();
		// initData();
		return view;
	}

	private void showDataToView() {
		// 进入页面时，数据列表数据不为空
		if (newsList != null && newsList.size() != 0) {
			if (mAdapter == null) {
				Log.e(TAG, "showDataToView==" + "adapter null");
				initAdapter();
			}
			handler.obtainMessage(LOAD_DATABASE_COMPLETE).sendToTarget(); // 先将页面保存或加载的数据显示在界面中

			if (sp.getBoolean("Video", true)) { // 显示时未请求网络数据同步，数据为数据库预加载
				mListView.handSetToRefresh(); // 自动下拉刷新，请求数据同步
			}
		} else { // 数据项为空，加载数据
			initData();
		}
	}

	private void setListener() {

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Log.d("hff", "click position>>>>>" + position);

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

		mSearchBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(activity, SearchActivity.class);
				startActivity(i);
			}
		});

	}

	private void initAdapter() {
		if (mAdapter == null) {
			Log.e(TAG, "initAdapter==" + "adapter null");
			mAdapter = new MainVideoSkinAdapter(activity, newsList, isDayTheme,
					isTextMode, 0);
		}

		mAdapter.setScreenChange(new onScreenFullChange() {

			@Override
			public void onFullVideo(int position) {

				Log.e("SCREEN FULL", "position" + position);
				// Bundle mBundle = new Bundle();
				// mBundle.putInt(PlayProxy.PLAY_MODE,
				// EventPlayProxy.PLAYER_VOD);
				// mBundle.putString(PlayProxy.PLAY_UUID, "hqrinotu0v");
				// mBundle.putString(PlayProxy.PLAY_VUID, newsList.get(position)
				// .getVideo_id());
				// mBundle.putString(PlayProxy.PLAY_CHECK_CODE, "");
				// mBundle.putString(PlayProxy.PLAY_PLAYNAME, "");
				// mBundle.putString(PlayProxy.PLAY_USERKEY,
				// "297d2469f53cd4727909656aba345ab9");
				// mBundle.putBoolean(PlayProxy.BUNDLE_KEY_ISPANOVIDEO, false);
				// LetvFullPlayBoard mPlayBoard = new LetvFullPlayBoard();
				// mFullSkin.setVisibility(View.VISIBLE);
				// mFullSkin.changeLayoutParamsfull();
				// mPlayBoard.init(activity, mBundle, mFullSkin);

				// mBundle = setVodParams("hqrinotu0v",
				// getItem(videoPlayPosition).getVideo_id(), "",
				// "297d2469f53cd4727909656aba345ab9", "", false);
				//
				// // 初始化播放
				// mPlayBoard.init(activity, mBundle,
				// holderMap.get(videoPlayPosition).playSkin);

			}
		});

		mAdapter.setNewsClickListener(new OnNewsClickListener() {

			@Override
			public void onNewsClick(View view, ArticleInfo article) {
				Log.e("VIDEO SHARE ", article.getTitle());
				MobclickAgent.onEvent(activity, UmenAnalytics.SHARE_VIDEO);
				ShareUtile.showShare(activity, article, null);

			}

			@Override
			public void onVideoPlay(Bundle bundle, String title) {
				Intent i = new Intent(activity, VideoPlayActivity.class);
				i.putExtra(VideoPlayActivity.DATA, bundle);
				i.putExtra("title", title);
				startActivity(i);
				
			}
		});

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
			MobclickAgent.onEvent(activity, UmenAnalytics.EVENT_VIDEO_REFRESH);
			// 先判断当前页面是哪个子页面，根据子页面进行不同的数据请求
			ArticleConfig refreshScrollConfig = new ArticleConfig();
			refreshScrollConfig.setLocalArticle(false);
			// 设置请求的种类
			refreshScrollConfig.setType(RequestType.VIDEO);

			ArticleManager.getInstence().getArticleInfo(refreshScrollConfig,
					new ArticleInfoCallback() {
						@Override
						public void onArticleInfoCallback(
								List<ArticleInfo> infos, RequestType type) {

							if (infos != null) {
								Log.d(TAG,
										"video content netdata==>"
												+ infos.size());

								newsList = (ArrayList<ArticleInfo>) infos;
								Message msg = new Message();
								msg.what = LOAD_DATA_REFRESH;
								if (infos.size() < 15) {
									isCanLoadmore = false;
								} else {
									isCanLoadmore = true;
								}

								handler.sendMessage(msg);

							} else {
								Toast.makeText(activity, "当前网络较差，请检查后重试",
										Toast.LENGTH_SHORT).show();
								mListView.onRefreshComplete();
								handler.obtainMessage(LOAD_DATA_REFRESH)
										.sendToTarget();

							}

						}
					});

			break;

		case LOAD_DATA_LOADMORE:

			MobclickAgent.onEvent(activity, UmenAnalytics.EVENT_VIDEO_MORE);
			ArticleConfig config1 = new ArticleConfig();
			config1.setLocalArticle(false);
			config1.setType(RequestType.VIDEO);
			if (newsList != null && newsList.size() > 1) {
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
								Log.e(TAG,
										"VIDEO content netdata==>"
												+ infos.size());
								returnList = (ArrayList<ArticleInfo>) infos;
								if (infos != null) {
									if (infos.size() < 15) {
										isCanLoadmore = false;
									} else {
										isCanLoadmore = true;
									}
									handler.obtainMessage(LOAD_DATA_LOADMORE)
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

		if (sp.getBoolean("Video", true)) {
			if (mAdapter == null) {
				Log.e(TAG, "initData==" + "adapter null");
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
		super.onDestroyView();
		mAdapter = null;
		// newsList = null;
	}

	/**
	 * 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
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
		if (mListView != null) {
			mListView.setTheme(isDayTheme);
		}
		if (mAdapter != null) {
			mAdapter.changeTheme(isDay);
			mAdapter.notifyDataSetChanged();
		}
	}

	public void changeTextMode(boolean isText) {
		this.isTextMode = isText;
		if (mAdapter != null) {
			mAdapter.changeTextMode(isText);
			mAdapter.notifyDataSetChanged();
		}
	}

	private void changeThemeUi() {
		if (isDayTheme) {
			ThemeUtil.setBackgroundDay(activity, mMainLayout);
		} else {
			ThemeUtil.setBackgroundNight(activity, mMainLayout);
		}
	}

	/**
	 * 屏幕旋转的事件传递进来，再交给
	 * 
	 * @param nowType
	 *            0:切换到横屏 1:切换到竖屏
	 */
	public void noticeFullScreenActiong(Configuration newConfig) {

		if (mAdapter != null) {
			mAdapter.changeShowScreenType(newConfig);
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
