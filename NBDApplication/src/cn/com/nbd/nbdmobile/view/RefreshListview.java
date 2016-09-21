package cn.com.nbd.nbdmobile.view;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;

public class RefreshListview extends ListView implements OnScrollListener {

	// TAG文字标签
	private final static String TAG = "Refresh_Listview";

	private BasePulldownView mPulldownView;

	protected IPinnedAdapter mAdapter;

	/**
	 * 底部加载更多显示状态
	 */
	private final static int ENDINT_LOADING = 1;// 手动完成刷新
	private final static int ENDINT_MANUAL_LOAD_DONE = 2;// 自动完成刷新
	private final static int ENDINT_AUTO_LOAD_DONE = 3; // 没有更多加载的内容了
	private final static int ENDINT_NO_MORE_CONTENT = 4; // 没有更多内容加载
	private int mEndState; // 加载更多的状态

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;

	protected int mListScrollState; // listview滑动的状态，判断是否滑动停下来了
	private boolean mCanLoadMore = false;// 可以上拉加载更多
	private boolean mCanRefresh = false;// 可以下拉刷新
	private boolean mIsAutoLoadMore = true;// 可以自动加载更多吗（注意，先判断是否有加载更多，如果没有，这个flag也没有意义）
	private boolean mIsMoveToFirstItemAfterRefresh = false;// 下拉刷新后是否显示第一条Item
	private boolean isDayTheme; // 用于section背景切换的标志位
	private static final int COLOR_NORMAL_DAY = 0xfff4f5f6; // 白天的停靠栏背景
	private static final int COLOR_NORMAL_NIGHT = 0xff252525; // 夜间的停靠栏背景

	private LayoutInflater mInflater;
	private LinearLayout mHeadView; // 下拉刷新的布局

	private View mFootView; // 上拉加载的布局
	private ProgressBar mFootLoadBar; // 上拉的加载圈
	private TextView mFootTextView; // 上拉的文字提示

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean mIsRecored;

	private int mHeadViewHeight; // 下拉布局的高

	private int mStartY;
	private int mStartX;
	private boolean mIsBack;
	private boolean mIsFirstTouchMove;

	private int mFirstItemIndex;
	private int mLastItemIndex;
	private int mCount;
	private boolean mEnoughCount;// 足够数量充满屏幕

	/**
	 * 下拉刷新监听接口
	 * 
	 */
	public interface OnRefreshListener {
		public void onRefresh();
	}

	/**
	 * 上拉加载更多监听接口
	 * 
	 */
	public interface OnLoadMoreListener {
		public void onLoadMore();
	}

	private OnRefreshListener mRefreshListener;
	private OnLoadMoreListener mLoadMoreListener;

	protected boolean isPinnedClicked;

	// 构造函数
	public RefreshListview(Context pContext, AttributeSet pAttrs) {
		super(pContext, pAttrs);
		init(pContext);
	}

	public RefreshListview(Context pContext) {
		super(pContext);
		init(pContext);
	}

	public RefreshListview(Context pContext, AttributeSet pAttrs, int pDefStyle) {
		super(pContext, pAttrs, pDefStyle);
		init(pContext);
	}

	/**
	 * 
	 * 初始化操作
	 */
	private void init(Context pContext) {

		mInflater = LayoutInflater.from(pContext);

		initHandler();

		addHeadView();

		setOnScrollListener(this);

	}

	private void initHandler() {

	}

	/**
	 * 添加下拉刷新的HeadView 加载一个布局文件，然后measure他的宽高
	 * 
	 */
	private void addHeadView() {

		mPulldownView = new PulldownViewN(getContext());

		mHeadViewHeight = mPulldownView.getPullHeight();

		mPulldownView.setState(BasePulldownView.DONE);

		addHeaderView(mPulldownView, null, false);

	}

	/**
	 * 添加加载更多FootView 添加加载更多的点击事件 根据是否自动加载更多设置标志位
	 * 
	 */
	private void addFooterView() {
		mFootView = mInflater.inflate(R.layout.pulldown_foot, null);
		mFootView.setVisibility(View.VISIBLE);
		setTheme(isDayTheme);
		mFootLoadBar = (ProgressBar) mFootView
				.findViewById(R.id.pull_to_refresh_progress);
		mFootTextView = (TextView) mFootView.findViewById(R.id.load_more);
		mFootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCanLoadMore) {
					if (mCanRefresh) {
						// 当可以下拉刷新时，如果FootView没有正在加载，并且HeadView没有正在刷新，才可以点击加载更多。
						if (mEndState != ENDINT_LOADING
								&& mPulldownView != null
								&& mPulldownView.getPullState() != BasePulldownView.REFRESHING) {
							mEndState = ENDINT_LOADING;
							onLoadMore();
						}
					} else if (mEndState != ENDINT_LOADING) {
						// 当不能下拉刷新时，FootView不正在加载时，才可以点击加载更多。
						mEndState = ENDINT_LOADING;
						onLoadMore();
					}
				}
			}
		});

		addFooterView(mFootView);

		if (mIsAutoLoadMore) {
			mEndState = ENDINT_AUTO_LOAD_DONE;
		} else {
			mEndState = ENDINT_MANUAL_LOAD_DONE;
		}
	}

	/**
	 * 测量HeadView宽高 该方法经测试只适用于Linearlayout
	 * 
	 */
	private void measureView(View pChild) {
		ViewGroup.LayoutParams p = pChild.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;

		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		pChild.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * 为了判断滑动到ListView底部没 数量差异根据具体的情况进行修改
	 * 
	 */
	@Override
	public void onScroll(AbsListView pView, int pFirstVisibleItem,
			int pVisibleItemCount, int pTotalItemCount) {

		mFirstItemIndex = pFirstVisibleItem;
		mLastItemIndex = pFirstVisibleItem + pVisibleItemCount - 2;
		mCount = pTotalItemCount - 2;
		if (pTotalItemCount > pVisibleItemCount) {
			mEnoughCount = true;
			// endingView.setVisibility(View.VISIBLE);
		} else {
			mEnoughCount = false;
		}

		// 先将消耗的滑动事件传递出去
		if (mAdapter != null) {
			mAdapter.onListViewScroll(pView, pFirstVisibleItem,
					pVisibleItemCount, pTotalItemCount);
		}

	}

	/**
	 * 当滑动状态改变时状态的处理
	 */
	@Override
	public void onScrollStateChanged(AbsListView pView, int pScrollState) {

		mListScrollState = pScrollState;

		if (mCanLoadMore) {// 存在加载更多功能
			if (mLastItemIndex == mCount && pScrollState == SCROLL_STATE_IDLE) {
				// SCROLL_STATE_IDLE=0，滑动停止
				if (mEndState != ENDINT_LOADING) {
					if (mIsAutoLoadMore) {// 自动加载更多，我们让FootView显示 “更 多”
						if (mCanRefresh) {
							// 存在下拉刷新并且HeadView没有正在刷新时，FootView可以自动加载更多。
							if (mPulldownView != null
									&& mPulldownView.getPullState() != BasePulldownView.REFRESHING) {
								// FootView显示 : 更 多 ---> 加载中...
								mEndState = ENDINT_LOADING;
								onLoadMore();
								changeEndViewByState();
							}
						} else {// 没有下拉刷新，我们直接进行加载更多。
								// FootView显示 : 更 多 ---> 加载中...
							mEndState = ENDINT_LOADING;
							onLoadMore();
							changeEndViewByState();
						}
					} else {// 不是自动加载更多，我们让FootView显示 “点击加载”
							// FootView显示 : 点击加载 ---> 加载中...
						mEndState = ENDINT_MANUAL_LOAD_DONE;
						changeEndViewByState();
					}
				}
			}
		} else if (mFootView != null && mFootView.getVisibility() == VISIBLE) {
			// 突然关闭加载更多功能之后，我们要移除FootView。
			System.out.println("this.removeFooterView(endRootView);...");

			// mEndRootView.setVisibility(View.GONE);
			// this.removeFooterView(mEndRootView);
		}
	}

	/**
	 * 改变加载更多状态
	 * 
	 */
	private void changeEndViewByState() {
		if (mCanLoadMore) {
			// 允许加载更多
			switch (mEndState) {
			case ENDINT_LOADING:// 刷新中
				// 加载中...
				if (mFootTextView.getText().equals(
						R.string.p2refresh_doing_end_refresh)) {
					break;
				}
				mFootTextView.setText(R.string.p2refresh_doing_end_refresh);
				mFootTextView.setVisibility(View.VISIBLE);
				mFootLoadBar.setVisibility(View.VISIBLE);
				break;
			case ENDINT_MANUAL_LOAD_DONE:// 手动刷新完成
				// 点击加载
				mFootTextView.setText(R.string.p2refresh_end_click_load_more);
				mFootTextView.setVisibility(View.VISIBLE);
				mFootLoadBar.setVisibility(View.GONE);

				mFootView.setVisibility(View.VISIBLE);
				break;
			case ENDINT_AUTO_LOAD_DONE:// 自动刷新完成
				// 更 多
				mFootTextView.setText(R.string.p2refresh_end_load_more);
				mFootTextView.setVisibility(View.VISIBLE);
				mFootLoadBar.setVisibility(View.GONE);

				mFootView.setVisibility(View.VISIBLE);
				break;
			default:
				// 当所有item的高度小于ListView本身的高度时，要隐藏掉FootView

				// if (enoughCount) {
				// endRootView.setVisibility(View.VISIBLE);
				// } else {
				// endRootView.setVisibility(View.GONE);
				// }
				break;
			}
		} else {
			mFootTextView.setText(R.string.p2refresh_end_no_more);
			mFootTextView.setVisibility(View.VISIBLE);
			mFootLoadBar.setVisibility(View.GONE);

			mFootView.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 点击事件
	 */
	public boolean onTouchEvent(MotionEvent event) {

		if (mCanRefresh) {
			if (mCanLoadMore && mEndState == ENDINT_LOADING) {
				// 如果存在加载更多功能，并且当前正在加载更多，默认不允许下拉刷新，必须加载完毕后才能使用。
				return super.onTouchEvent(event);
			}

			switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				isPinnedClicked = false; // 重置标志位
				if (mFirstItemIndex == 0 && !mIsRecored) {
					mIsRecored = true;
					mStartY = (int) event.getY();
					mStartX = (int) event.getX();
				}
				mIsFirstTouchMove = true;

				handlePinnedTouch(event);

				if (isPinnedClicked) {
					return true;
				}

				break;
			case MotionEvent.ACTION_UP:
				Log.e("TEST HEAD",
						"head view height" + mPulldownView.getMeasuredHeight()
								+ "w" + mPulldownView.getPullHeight()
								+ "positon" + mPulldownView.getTop() + "--"
								+ mPulldownView.getBottom());

				// 手指抬离时，如果没在加载刷新状态
				if (mPulldownView != null
						&& mPulldownView.getPullState() != BasePulldownView.REFRESHING
						&& mPulldownView.getPullState() != BasePulldownView.LOADING) {

					if (mPulldownView.getPullState() == BasePulldownView.DONE) {
					}
					if (mPulldownView.getPullState() == BasePulldownView.PULL_TO_REFRESH) {
						mPulldownView.setState(BasePulldownView.DONE);
						dealPinnedVisible(0);
					}
					if (mPulldownView.getPullState() == BasePulldownView.RELEASE_TO_REFRESH) {
						mPulldownView.setState(BasePulldownView.REFRESHING);

						dealPinnedVisible(1);

						onRefresh();
					}
				}

				mIsRecored = false;
				mIsBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY(); // 当前滑动位置的Y值
				int tempX = (int) event.getX(); // 当前滑动位置的X值

				if (!mIsRecored && mFirstItemIndex == 0) { // 第一个值时记录为初始化位置
					mIsRecored = true;
					mStartY = tempY;
					mStartX = tempX;
				}

				if (mPulldownView != null) { // 将拉动的距离传递给Headview
					mPulldownView.setDistance(tempY - mStartY);
				}

				if (mPulldownView != null
						&& Math.abs(tempY - mStartY) < (mHeadViewHeight / 2)) { //
					Log.e(TAG, "阈值之内" + Math.abs(tempY - mStartY));
					mPulldownView.setState(BasePulldownView.DONE);
					dealPinnedVisible(0);
				} else {

					if (!mIsFirstTouchMove) {
						mIsFirstTouchMove = false;

						// 当前头部没有在加载状态时进行滑动的状态处理
						if (mPulldownView != null
								&& mPulldownView.getPullState() != BasePulldownView.REFRESHING
								&& mIsRecored
								&& mPulldownView.getPullState() != BasePulldownView.LOADING) {

							// 保证在设置padding的过程中，当前的位置一直是在head，
							// 否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
							// 可以松手去刷新了
							if (mPulldownView.getPullState() == BasePulldownView.RELEASE_TO_REFRESH) {

								Log.e("Test head-->", "state release");

								setSelection(0);

								// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
								if (((tempY - mStartY) / RATIO < mHeadViewHeight)
										&& (tempY - mStartY) > 0) {
									mPulldownView
											.setState(BasePulldownView.PULL_TO_REFRESH);
									dealPinnedVisible(1);
									if (mIsBack) {
										mIsBack = true;
									} else {

									}

								}
								// 一下子推到顶了
								else if (tempY - mStartY <= 0) {
									mPulldownView
											.setState(BasePulldownView.DONE);
								}
								// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
							}
							// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
							if (mPulldownView.getPullState() == BasePulldownView.PULL_TO_REFRESH) {
								setSelection(0);
								// 下拉到可以进入RELEASE_TO_REFRESH的状态
								if ((tempY - mStartY) / RATIO >= mHeadViewHeight) {
									mPulldownView
											.setState(BasePulldownView.RELEASE_TO_REFRESH);
									mIsBack = true;
								} else if (tempY - mStartY <= 0) {
									mPulldownView
											.setState(BasePulldownView.DONE);
								}
							}
							if (mPulldownView.getPullState() == BasePulldownView.DONE) {
								if (tempY - mStartY > 0) {
									mPulldownView
											.setState(BasePulldownView.PULL_TO_REFRESH);
									dealPinnedVisible(1);
									if (mIsBack) {
										mIsBack = true;
									} else {

									}
								}
							}

							if (mPulldownView.getPullState() == BasePulldownView.PULL_TO_REFRESH) {
								mPulldownView.setPadding(0, -1
										* mHeadViewHeight + (tempY - mStartY)
										/ RATIO, 0, 0);
							}
							if (mPulldownView.getPullState() == BasePulldownView.RELEASE_TO_REFRESH) {
								mPulldownView.setPadding(0, (tempY - mStartY)
										/ RATIO - mHeadViewHeight, 0, 0);
							}
						}
					}

					if (mIsFirstTouchMove) {
						mIsFirstTouchMove = false;
					}
				}
				break;
			}
		}

		return super.onTouchEvent(event);
	}

	protected void handlePinnedTouch(MotionEvent event) {

	}

	protected void dealPinnedVisible(int i) {
		// TODO Auto-generated method stub

	}

	public void setOnRefreshListener(OnRefreshListener pRefreshListener) {
		if (pRefreshListener != null) {
			mRefreshListener = pRefreshListener;
			mCanRefresh = true;
		}
	}

	public void setOnLoadListener(OnLoadMoreListener pLoadMoreListener) {
		if (pLoadMoreListener != null) {
			mLoadMoreListener = pLoadMoreListener;
			mCanLoadMore = true;
			if (mCanLoadMore && getFooterViewsCount() == 0) {
				addFooterView();
			}
		}
	}

	/**
	 * 正在下拉刷新
	 * 
	 */
	private void onRefresh() {
		if (mRefreshListener != null) {
			mRefreshListener.onRefresh();
		}
	}

	/**
	 * 下拉刷新完成
	 * 
	 */
	public void onRefreshComplete() {
		// 下拉刷新后是否显示第一条Item
		if (mIsMoveToFirstItemAfterRefresh)
			setSelection(0);

		if (mPulldownView != null) {
			mPulldownView.setState(BasePulldownView.DONE);
			dealPinnedVisible(0);
		}
		// 最近更新: Time

	}

	/**
	 * 正在加载更多，FootView显示 ： 加载中...
	 * 
	 */
	private void onLoadMore() {
		if (mLoadMoreListener != null) {
			// 加载中...
			mFootTextView.setText(R.string.p2refresh_doing_end_refresh);
			mFootTextView.setVisibility(View.VISIBLE);
			mFootLoadBar.setVisibility(View.VISIBLE);

			mLoadMoreListener.onLoadMore();
		}
	}

	/**
	 * 加载更多完成
	 * 
	 */
	public void onLoadMoreComplete() {
		if (mIsAutoLoadMore) {
			mEndState = ENDINT_AUTO_LOAD_DONE;
		} else {
			mEndState = ENDINT_MANUAL_LOAD_DONE;
		}
		changeEndViewByState();
	}

	/**
	 * 页面第一次载入时，不滑动显示下拉加载状态
	 */
	public void handSetToRefresh() {
		if (mPulldownView != null) {
			mPulldownView.setState(BasePulldownView.REFRESHING);
			mPulldownView.setDistance(900);
		}
		onRefresh();
	}

	/**
	 * 切换子栏目的时候，获取当前的栏目的listview状态，保存起来
	 * 
	 * @return
	 */
	public Map<String, Integer> getHeaderState() {
		Map<String, Integer> ListStates = new HashMap<String, Integer>();
		// ListStates.put("HeadState", mHeadState);
		// ListStates.put("EndState", mEndState);
		// if (mCanLoadMore) {
		// ListStates.put("LoadMore", 1);
		// } else {
		// ListStates.put("LoadMore", 0);
		// }
		return ListStates;
	}

	/**
	 * 切换子栏目时，根据传入的头尾状态值显示
	 * 
	 * @param states
	 */
	public void setLoadState(Map<String, Integer> states) {
		// if (states != null) {
		// mHeadState = states.get("HeadState");
		// mEndState = states.get("EndState");
		// mCanLoadMore = states.get("LoadMore") == 1;
		// } else { // 初始化状态
		// mHeadState = DONE;
		// mEndState = ENDINT_AUTO_LOAD_DONE;
		// mCanLoadMore = true;
		// }
		// changeHeadViewByState();
		// changeEndViewByState();
	}

	/**
	 * 根据当前状态刷新界面的样子
	 */
	public void refreshListState() {
		// changeHeadViewByState();
		// changeEndViewByState();
	}

	public void setHeadFootTheme(Activity activity, boolean isDay) {
		if (mHeadView != null) {
			if (isDay) {
				ThemeUtil.setBackgroundDay(activity, mHeadView);
			} else {
				ThemeUtil.setBackgroundNight(activity, mHeadView);
			}
		}
		if (mFootView != null) {
			if (isDay) {
				ThemeUtil.setBackgroundDay(activity, mFootView);
			} else {
				ThemeUtil.setBackgroundNight(activity, mFootView);
			}
		}
	}

	public void setTheme(boolean isDay) {
		this.isDayTheme = isDay;
		if (isDayTheme) {
			if (mHeadView != null) {
				mHeadView.setBackgroundColor(COLOR_NORMAL_DAY);
			}
			if (mFootView != null) {
				mFootView.setBackgroundColor(COLOR_NORMAL_DAY);
			}
		} else {
			if (mHeadView != null) {
				mHeadView.setBackgroundColor(COLOR_NORMAL_NIGHT);
			}
			if (mFootView != null) {
				mFootView.setBackgroundColor(COLOR_NORMAL_NIGHT);
			}
		}
	}

	/**
	 * 类属性的GETTER&SETTER构造函数
	 */
	public boolean isCanLoadMore() {
		return mCanLoadMore;
	}

	public void setCanLoadMore(boolean pCanLoadMore) {
		mCanLoadMore = pCanLoadMore;
		if (mCanLoadMore && getFooterViewsCount() == 0) {
			addFooterView();
		}
		changeEndViewByState();
	}

	public boolean isCanRefresh() {
		return mCanRefresh;
	}

	public void setCanRefresh(boolean pCanRefresh) {
		mCanRefresh = pCanRefresh;
	}

	public boolean isAutoLoadMore() {
		return mIsAutoLoadMore;
	}

	public void setAutoLoadMore(boolean pIsAutoLoadMore) {
		mIsAutoLoadMore = pIsAutoLoadMore;
	}

	public boolean isMoveToFirstItemAfterRefresh() {
		return mIsMoveToFirstItemAfterRefresh;
	}

	public void setMoveToFirstItemAfterRefresh(
			boolean pIsMoveToFirstItemAfterRefresh) {
		mIsMoveToFirstItemAfterRefresh = pIsMoveToFirstItemAfterRefresh;
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		mAdapter = (IPinnedAdapter) adapter;
		super.setAdapter(adapter);
	}

}
