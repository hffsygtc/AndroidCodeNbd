package cn.com.nbd.nbdmobile.view;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;

/**
 * 带停靠栏的具有下拉刷新和上拉加载更多的listview .
 * 
 * @author riche
 * 
 *         <外部设置标准Flag>
 *         1、设置了OnRefreshListener接口和OnLoadMoreListener接口并且不为null，则打开这两个功能了。
 *         2、mIsAutoLoadMore(是否自动加载更多)
 *         3、mIsMoveToFirstItemAfterRefresh(下拉刷新后是否显示第一条Item)
 */

public class CustomListViewUnused extends ListView implements OnScrollListener {

	// LOG文字
	private final static String TAG = "CustomListview";
	// 显示格式化日期模板
	private final static String DATE_FORMAT_STR = "yyyy年MM月dd日 HH:mm";

	// 实际的padding的距离与界面上偏移距离的比例
	private final static int RATIO = 3;

	private final static int RELEASE_TO_REFRESH = 0;
	private final static int PULL_TO_REFRESH = 1;
	private final static int REFRESHING = 2;
	private final static int DONE = 3;
	private final static int LOADING = 4;

	private int mListScrollState;
	private Matrix mScaleMatrix = new Matrix();

	// 加载中
	private final static int ENDINT_LOADING = 1;
	// 手动完成刷新
	private final static int ENDINT_MANUAL_LOAD_DONE = 2;
	// 自动完成刷新
	private final static int ENDINT_AUTO_LOAD_DONE = 3;
	// 没有更多加载的内容了
	private final static int ENDINT_NO_MORE_CONTENT = 4;

	// 0:RELEASE_TO_REFRESH; 1:PULL_To_REFRESH; 2:REFRESHING; 3:DONE; 4:LOADING
	private int mHeadState;

	// 0:完成/等待刷新 ; 1:加载中
	private int mEndState;

	private int mHeadHeight;

	private int mScreenWidth;
	private float pullNWidth; // N的宽度
	private int pullNHeight;// N的高度
	private float pullNInitScale; // 根据N的宽高和head的高度计算初始的缩放比例

	private float pullNScale = 0.3f; // N的总体的缩放尺度
	private int pullNLeftGap = 100; // N距离左边的位置

	private float pullPlusWidth;
	private float pullPlusScale; // 加号的缩放比例
	private float pullPlusX; // 加号上升的X的坐标
	private float pullPlusY; // 几号上升时的Y坐标

	private float disCount;

	private Animation sunAnimation;

	private boolean isRefreshing;

	private Context mContext;

	/**
	 * 处理下拉的状态
	 */
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			if (msg.what == 2000) {
				Matrix sunMatrix = new Matrix();

				float sunX = 0;
//				float sunDeltY = mHeadViewHeight - disCount+ dip2pix(getContext(), 15);
				float sunDeltY = mHeadViewHeight - disCount+ 45;
				float sunCenterX = sunX + pullPlusWidth * pullPlusScale / 2;
				float sunCenterY = sunDeltY + pullPlusWidth * pullPlusScale / 2;

				float animScale = (Float) msg.obj;

				sunMatrix.postScale(pullPlusScale, pullPlusScale);
				sunMatrix.postTranslate(sunX, sunDeltY);
				sunMatrix.postRotate(360 * animScale * 2, sunCenterX,
						sunCenterY);

				nLogoPlusImg.setImageMatrix(sunMatrix);
			} else {

				disCount = msg.what / RATIO; // 向下拉动的距离

				if (disCount > mHeadViewHeight) {
					disCount = mHeadViewHeight;
				}

				float pullPercent = disCount / mHeadViewHeight;
				float scale = (float) (pullPercent * pullNInitScale);

				if (scale > pullNInitScale) {
					scale = (float) pullNInitScale;
				}

				Matrix m = new Matrix();
				m.postScale(scale, scale);
				float deltY = mHeadViewHeight / 2 - pullNHeight * scale;
//				float deltX = dip2pix(getContext(), 40) - pullNWidth * scale/ 2;
				float deltX = 120 - pullNWidth * scale/ 2;
				m.postTranslate(deltX, deltY);
				nLogoImg.setImageMatrix(m);

				// 处理小太阳
				Matrix sunMatrix = new Matrix();
				// float sunX = dip2pix(getContext(),
				// 35)+pullNWidth*pullNInitScale;
				float sunX = 0;
//				float sunDeltY = mHeadViewHeight - disCount+ dip2pix(getContext(), 15);
				float sunDeltY = mHeadViewHeight - disCount+ 45;
				float sunCenterX = sunX + pullPlusWidth * pullPlusScale / 2;
				float sunCenterY = sunDeltY + pullPlusWidth * pullPlusScale / 2;

				sunMatrix.postScale(pullPlusScale, pullPlusScale);
				sunMatrix.postTranslate(sunX, sunDeltY);
				sunMatrix.postRotate(360 * pullPercent, sunCenterX, sunCenterY);

				nLogoPlusImg.setImageMatrix(sunMatrix);

			}
			super.handleMessage(msg);
		}
	};

	// ******************功能设置Flag***************

	// 可以上拉加载更多
	private boolean mCanLoadMore = false;
	// 可以下拉刷新
	private boolean mCanRefresh = false;
	// 可以自动加载更多吗（注意，先判断是否有加载更多，如果没有，这个flag也没有意义）
	private boolean mIsAutoLoadMore = true;
	// 下拉刷新后是否显示第一条Item
	private boolean mIsMoveToFirstItemAfterRefresh = false;

	private boolean isDayTheme;
	private static final int COLOR_NORMAL_DAY = 0xfff4f5f6;
	private static final int COLOR_NORMAL_NIGHT = 0xff252525;

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

	// ***********************************************

	/**
	 * 添加的停靠栏功能实现
	 * 
	 */
	public interface HeaderAdapter {
		// 停靠栏不显示
		public static final int HEADER_GONE = 0;
		// 停靠栏显示
		public static final int HEADER_VISIBLE = 1;
		// 两个停靠栏碰一起滑动
		public static final int HEADER_PUSHED_UP = 2;

		// 获取停靠栏的显示状态
		int getHeaderState(int position);

		// 设置显示的停靠栏view
		void configureHeader(View header, int position, int alpha);

		// 点击了停靠栏的左右栏目切换
		void onHeaderClick(View header, int position);

		// 是否是下拉状态，下拉时，不显示第一个停靠栏
		void isPullDown(int pullDowm);

		// listview消耗了onscroll监听，得暴露一个出去
		void onListScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount);

		// listview 消耗掉了的停滑动状态改变时的事件
		void onListScrollStateChanged(AbsListView view, int scrollState);

		// 更改了主题配色，通知外界处理停靠栏的字体颜色
		void onThemeChange(View headerView, boolean isDay);

	}

	private HeaderAdapter mAdapter;

	private View mHeaderView; // 停靠栏的View
	private boolean mHeaderViewVisible; // 是否显示停靠栏

	private int mHeaderViewWidth; // 悬靠列的宽度
	private int mHeaderViewHeight; // 悬靠列的高度
	private static final int MAX_ALPHA = 255;

	private LayoutInflater mInflater;

	private LinearLayout mHeadView; // 下拉刷新的布局
	// ImageView skyImg;
	// ImageView buildImg;
	// ImageView sunImg;

	ImageView nLogoImg;
	ImageView nLogoPlusImg;
	TextView nLogoText;
	// TextView nTimeText;
	// private LinearLayout mHeadLayout; // 下拉刷新的布局
	// private TextView mTipsTextView; // 下拉刷新的文字
	// private TextView mLastUpdatedTextView; // 下来刷新的最后更新时间
	// private ImageView mArrowImageView; // 下拉刷新的箭头图标
	// private ProgressBar mProgressBar; // 下拉刷新的进度转圈

	private View mEndRootView; // 上拉加载的布局
	private ProgressBar mEndLoadProgressBar; // 上拉的加载圈
	private TextView mEndLoadTipsTextView; // 上拉的文字提示

	private RotateAnimation mArrowAnim;// headView动画
	private RotateAnimation mArrowReverseAnim;// headView反转动画

	// 用于保证startY的值在一个完整的touch事件中只被记录一次
	private boolean mIsRecored;

	private int mHeadViewWidth; // 下拉布局的宽
	private int mHeadViewHeight; // 下拉布局的高

	private int mStartY;
	private int mStartX;
	private boolean mIsBack;
	private boolean mIsFirstTouchMove;

	private int mFirstItemIndex;
	private int mLastItemIndex;
	private int mCount;
	private boolean mEnoughCount;// 足够数量充满屏幕

	private OnRefreshListener mRefreshListener;
	private OnLoadMoreListener mLoadMoreListener;

	public CustomListViewUnused(Context pContext, AttributeSet pAttrs) {
		super(pContext, pAttrs);
		init(pContext);
	}

	public CustomListViewUnused(Context pContext) {
		super(pContext);
		init(pContext);
	}

	public CustomListViewUnused(Context pContext, AttributeSet pAttrs,
			int pDefStyle) {
		super(pContext, pAttrs, pDefStyle);
		init(pContext);
	}

	/**
	 * 
	 * 初始化操作
	 */
	private void init(Context pContext) {
		// setCacheColorHint(pContext.getResources().getColor(R.color.transparent));
		mInflater = LayoutInflater.from(pContext);

		addHeadView();

		setOnScrollListener(this);

		initPullImageAnimation(0);
	}

	// *****************************停靠栏相关测量操作，设置方法***********************

	/**
	 * 停靠栏：处理停靠栏的布局位置
	 * 
	 */
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mHeaderView != null) {
			mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
			configureHeaderView(getFirstVisiblePosition() - 1);
		}
	}

	/**
	 * 停靠栏：测量停靠栏的宽高属性
	 * 
	 */
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mHeaderView != null) {
			measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
			mHeaderViewWidth = mHeaderView.getMeasuredWidth();
			mHeaderViewHeight = mHeaderView.getMeasuredHeight();
		}
	}

	/**
	 * 停靠栏：处理停靠栏的布局 需要外部调用加载一个布局文件
	 * 
	 * @param view
	 *            加载的停靠栏布局的layout
	 */
	public void setPinnedHeaderView(Activity activity, View view,
			boolean isDay, boolean isToggle) {
		mHeaderView = view;
		if (mHeaderView != null) {
			// listview的上边和下边有黑色的阴影。xml中： android:fadingEdge="none"
			setFadingEdgeLength(0);
			if (isDay) {
				if (isToggle) {
					ThemeUtil.setSectionBackgroundDay(activity, mHeaderView);
				} else {
					ThemeUtil.setBackgroundDay(activity, mHeaderView);
				}
			} else {
				if (isToggle) {
					ThemeUtil.setSectionBackgroundNight(activity, mHeaderView);
				} else {
					ThemeUtil.setBackgroundNight(activity, mHeaderView);
				}
			}
		}
		// 调用布局方法，重新布局
		requestLayout();
	}

	/**
	 * 更改主题的颜色，根据模式
	 * 
	 * @param activity
	 * @param isDay
	 * @param isToggle
	 *            是否是正负价值的配色，正负价值的停靠栏配色不一样
	 */
	public void changePinnedThemeUi(Activity activity, boolean isDay,
			boolean isToggle) {
		if (mHeaderView != null) {
			if (isDay) {
				if (isToggle) {
					ThemeUtil.setSectionBackgroundDay(activity, mHeaderView);
				} else {
					ThemeUtil.setBackgroundDay(activity, mHeaderView);
				}
			} else {
				if (isToggle) {
					ThemeUtil.setSectionBackgroundNight(activity, mHeaderView);
				} else {
					ThemeUtil.setBackgroundNight(activity, mHeaderView);
				}
			}
			if (mAdapter != null) {
				mAdapter.onThemeChange(mHeaderView, isDay);
			}
		}
	}

	/**
	 * 停靠栏：设置顶部停靠栏的显示滑动状态
	 * 
	 * @param position
	 */
	public void configureHeaderView(int position) {
		if (mHeaderView == null) {
			Log.d(TAG, "headerView null");
			return;
		}
		int state = mAdapter.getHeaderState(position);
		switch (state) {

		case HeaderAdapter.HEADER_GONE: {
			mHeaderViewVisible = false;
			break;
		}
		case HeaderAdapter.HEADER_VISIBLE: {
			mAdapter.configureHeader(mHeaderView, position, MAX_ALPHA);
			if (mHeaderView.getTop() != 0) {
				mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
			}
			mHeaderViewVisible = true;
			break;
		}
		case HeaderAdapter.HEADER_PUSHED_UP: {
			View firstView = getChildAt(0);
			int bottom = firstView.getBottom();
			int headerHeight = mHeaderView.getHeight();
			int y;
			int alpha;
			if (bottom < headerHeight) {
				y = (bottom - headerHeight);
				alpha = MAX_ALPHA * (headerHeight + y) / headerHeight;
			} else {
				y = 0;
				alpha = MAX_ALPHA;
			}
			mAdapter.configureHeader(mHeaderView, position, alpha);
			if (mHeaderView.getTop() != y) {
				mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight
						+ y);
			}
			mHeaderViewVisible = true;
			break;
		}
		}
	}

	/**
	 * 停靠栏：绘制顶部停靠栏的内容
	 */
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mHeaderViewVisible) {
			drawChild(canvas, mHeaderView, getDrawingTime());
		}
	}

	/**
	 * 获取是否在显示停靠栏
	 * 
	 * @return
	 */
	public boolean getIsSectionShow() {
		return mHeaderViewVisible;
	}

	// *********************************************************************

	// Handler handler = new Handler() {
	// public void handleMessage(Message msg) {
	//
	// float scale = 1.0f;
	// int position;
	//
	// if (msg.what < 100) {
	// position = 100;
	// } else if (msg.what > 600) {
	// position = 600;
	// } else {
	// position = msg.what;
	// }
	//
	// float count = (position-350f)/500f;
	// Log.e("pull down", "position" +position+"=="+count);
	// float showScale = count + scale;
	//
	// Log.e("IMAGE VIEW SCALE==>", showScale+"");
	//
	//
	// if (mArrowImageView!= null) {
	// mScaleMatrix.postScale(showScale, showScale, mArrowImageView.getX(),
	// mArrowImageView.getY());
	// mArrowImageView.setImageMatrix(mScaleMatrix);
	//
	// }
	// super.handleMessage(msg);
	// };
	//
	// };

	/**
	 * 添加下拉刷新的HeadView 加载一个布局文件，然后measure他的宽高
	 * 
	 */
	private void addHeadView() {
		mHeadView = (LinearLayout) mInflater.inflate(R.layout.pulldown_head,
				null);
		nLogoImg = (ImageView) mHeadView.findViewById(R.id.head_logo);
		nLogoPlusImg = (ImageView) mHeadView.findViewById(R.id.head_plus);
		nLogoText = (TextView) mHeadView.findViewById(R.id.head_text);
		// nTimeText = (TextView) mHeadView.findViewById(R.id.head_time_text);

		setTheme(isDayTheme);

		measureView(mHeadView);

		 mHeadViewHeight = mHeadView.getMeasuredHeight();
//		mHeadViewHeight = dip2pix(getContext(), 60);
//		Log.e(TAG, "headViewHeight-->"+mHeadViewHeight);
		mHeadViewWidth = mHeadView.getMeasuredWidth();

		mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;

		pullNHeight = nLogoImg.getMeasuredHeight();
		pullNWidth = nLogoImg.getMeasuredWidth();

		pullPlusWidth = nLogoPlusImg.getMeasuredWidth();

		mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
		mHeadView.invalidate();

		addHeaderView(mHeadView, null, false);

//		float initNWidth = dip2pix(getContext(), 30);
		float initNWidth = 90;
		pullNInitScale = initNWidth / pullNWidth;

//		float initPlusWidth = dip2pix(getContext(), 8);
		float initPlusWidth = 24;
		pullPlusScale = initPlusWidth / pullPlusWidth;

		Matrix initMatrix = new Matrix();
		initMatrix.postScale(0, 0);

		nLogoImg.setImageMatrix(initMatrix);
		nLogoPlusImg.setImageMatrix(initMatrix);

		mHeadState = DONE;
	}

	/**
	 * 添加加载更多FootView 添加加载更多的点击事件 根据是否自动加载更多设置标志位
	 * 
	 */
	private void addFooterView() {
		mEndRootView = mInflater.inflate(R.layout.pulldown_foot, null);
		mEndRootView.setVisibility(View.VISIBLE);
		setTheme(isDayTheme);
		mEndLoadProgressBar = (ProgressBar) mEndRootView
				.findViewById(R.id.pull_to_refresh_progress);
		mEndLoadTipsTextView = (TextView) mEndRootView
				.findViewById(R.id.load_more);
		mEndRootView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mCanLoadMore) {
					if (mCanRefresh) {
						// 当可以下拉刷新时，如果FootView没有正在加载，并且HeadView没有正在刷新，才可以点击加载更多。
						if (mEndState != ENDINT_LOADING
								&& mHeadState != REFRESHING) {
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

		addFooterView(mEndRootView);

		if (mIsAutoLoadMore) {
			mEndState = ENDINT_AUTO_LOAD_DONE;
		} else {
			mEndState = ENDINT_MANUAL_LOAD_DONE;
		}
	}

	/**
	 * 实例化下拉刷新的箭头的动画效果
	 * 
	 * @param pAnimDuration
	 *            动画运行时长
	 */
	private void initPullImageAnimation(final int pAnimDuration) {
		int _Duration;
		if (pAnimDuration > 0) {
			_Duration = pAnimDuration;
		} else {
			_Duration = 250;
		}
		// Interpolator _Interpolator;
		// switch (pAnimType) {
		// case 0:
		// _Interpolator = new AccelerateDecelerateInterpolator();
		// break;
		// case 1:
		// _Interpolator = new AccelerateInterpolator();
		// break;
		// case 2:
		// _Interpolator = new AnticipateInterpolator();
		// break;
		// case 3:
		// _Interpolator = new AnticipateOvershootInterpolator();
		// break;
		// case 4:
		// _Interpolator = new BounceInterpolator();
		// break;
		// case 5:
		// _Interpolator = new CycleInterpolator(1f);
		// break;
		// case 6:
		// _Interpolator = new DecelerateInterpolator();
		// break;
		// case 7:
		// _Interpolator = new OvershootInterpolator();
		// break;
		// default:
		// _Interpolator = new LinearInterpolator();
		// break;
		// }

		Interpolator _Interpolator = new LinearInterpolator();

		mArrowAnim = new RotateAnimation(0, -180,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mArrowAnim.setInterpolator(_Interpolator);
		mArrowAnim.setDuration(_Duration);
		mArrowAnim.setFillAfter(true);

		mArrowReverseAnim = new RotateAnimation(-180, 0,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f,
				RotateAnimation.RELATIVE_TO_SELF, 0.5f);
		mArrowReverseAnim.setInterpolator(_Interpolator);
		mArrowReverseAnim.setDuration(_Duration);
		mArrowReverseAnim.setFillAfter(true);

		sunAnimation = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime,
					Transformation t) {
				Message msg = new Message();
				msg.obj = interpolatedTime;
				msg.what = 2000;
				handler.sendMessage(msg);
				// setRotate(interpolatedTime);
			}
		};
		sunAnimation.setRepeatCount(Animation.INFINITE);
		sunAnimation.setRepeatMode(Animation.RESTART);
		sunAnimation.setInterpolator(_Interpolator);
		sunAnimation.setDuration(1000);

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
			mAdapter.onListScroll(pView, pFirstVisibleItem, pVisibleItemCount,
					pTotalItemCount);
		}

	}

	/**
	 * 当滑动状态改变时状态的处理
	 */
	@Override
	public void onScrollStateChanged(AbsListView pView, int pScrollState) {

		mListScrollState = pScrollState;

		// 先将消耗的滑动事件传递出去
		if (mAdapter != null) {
			mAdapter.onListScrollStateChanged(pView, pScrollState);
		}

		if (mCanLoadMore) {// 存在加载更多功能
			if (mLastItemIndex == mCount && pScrollState == SCROLL_STATE_IDLE) {
				// SCROLL_STATE_IDLE=0，滑动停止
				if (mEndState != ENDINT_LOADING) {
					if (mIsAutoLoadMore) {// 自动加载更多，我们让FootView显示 “更 多”
						if (mCanRefresh) {
							// 存在下拉刷新并且HeadView没有正在刷新时，FootView可以自动加载更多。
							if (mHeadState != REFRESHING) {
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
		} else if (mEndRootView != null
				&& mEndRootView.getVisibility() == VISIBLE) {
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
				if (mEndLoadTipsTextView.getText().equals(
						R.string.p2refresh_doing_end_refresh)) {
					break;
				}
				mEndLoadTipsTextView
						.setText(R.string.p2refresh_doing_end_refresh);
				mEndLoadTipsTextView.setVisibility(View.VISIBLE);
				mEndLoadProgressBar.setVisibility(View.VISIBLE);
				break;
			case ENDINT_MANUAL_LOAD_DONE:// 手动刷新完成
				// 点击加载
				mEndLoadTipsTextView
						.setText(R.string.p2refresh_end_click_load_more);
				mEndLoadTipsTextView.setVisibility(View.VISIBLE);
				mEndLoadProgressBar.setVisibility(View.GONE);

				mEndRootView.setVisibility(View.VISIBLE);
				break;
			case ENDINT_AUTO_LOAD_DONE:// 自动刷新完成
				// 更 多
				mEndLoadTipsTextView.setText(R.string.p2refresh_end_load_more);
				mEndLoadTipsTextView.setVisibility(View.VISIBLE);
				mEndLoadProgressBar.setVisibility(View.GONE);

				mEndRootView.setVisibility(View.VISIBLE);
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
			mEndLoadTipsTextView.setText(R.string.p2refresh_end_no_more);
			mEndLoadTipsTextView.setVisibility(View.VISIBLE);
			mEndLoadProgressBar.setVisibility(View.GONE);

			mEndRootView.setVisibility(View.VISIBLE);
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
				if (mFirstItemIndex == 0 && !mIsRecored) {
					mIsRecored = true;
					mStartY = (int) event.getY();
					mStartX = (int) event.getX();
				}
				mIsFirstTouchMove = true;

				if (mHeaderViewVisible
						&& mListScrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (event.getY() > mHeaderViewHeight) { // 没有点击在停靠栏

					} else if (event.getX() < mHeaderViewWidth / 2) { // 点击了左边的控件
						mAdapter.onHeaderClick(mHeaderView, 0);
					} else { // 点击了右边的控件
						mAdapter.onHeaderClick(mHeaderView, 1);
					}
					return true;
				}

				break;
			case MotionEvent.ACTION_UP:

				if (mHeadState != REFRESHING && mHeadState != LOADING) {
					if (mHeadState == DONE) {

					}
					if (mHeadState == PULL_TO_REFRESH) {
						mHeadState = DONE;
						changeHeaderViewByState();
					}
					if (mHeadState == RELEASE_TO_REFRESH) {
						mHeadState = REFRESHING;
						changeHeaderViewByState();
						onRefresh();
					}
				}

				mIsRecored = false;
				mIsBack = false;

				break;

			case MotionEvent.ACTION_MOVE:
				int tempY = (int) event.getY();
				int tempX = (int) event.getX();
				// 当是第一个系统判定的MOVE事件
				// if (mIsFirstTouchMove) {
				// //Y方向移动大于X方向移动，就直接消耗了不回传
				// ViewConfiguration.getTouchSlop();
				// if ((Math.abs(tempY - mStartY) - Math.abs(tempX - mStartX)) >
				// 0) {
				// mIsFirstTouchMove = false;
				// return true;
				// }else {
				// mIsFirstTouchMove = false;
				// }
				// }

				if (!mIsRecored && mFirstItemIndex == 0) {
					mIsRecored = true;
					mStartY = tempY;
					mStartX = tempX;
				}

				if (mHeadHeight < 0) {
					mHeadHeight = mHeadViewHeight / 2;
				}

				handler.obtainMessage(tempY - mStartY).sendToTarget();

				if (Math.abs(tempY - mStartY) < (mHeadViewHeight / 2)) { //
					// Log.e(TAG, "阈值之内" + Math.abs(tempY - mStartY));
					mHeadState = DONE;
					changeHeaderViewByState();
				} else {
					if (!mIsFirstTouchMove) {

						// Message msg = new Message();
						// msg.what = tempY - mStartY;
						// handler.sendMessage(msg);
						mIsFirstTouchMove = false;

						if (mHeadState != REFRESHING && mIsRecored
								&& mHeadState != LOADING) {

							// 保证在设置padding的过程中，当前的位置一直是在head，
							// 否则如果当列表超出屏幕的话，当在上推的时候，列表会同时进行滚动
							// 可以松手去刷新了
							if (mHeadState == RELEASE_TO_REFRESH) {

								setSelection(0);

								// 往上推了，推到了屏幕足够掩盖head的程度，但是还没有推到全部掩盖的地步
								if (((tempY - mStartY) / RATIO < mHeadViewHeight)
										&& (tempY - mStartY) > 0) {
									mHeadState = PULL_TO_REFRESH;
									changeHeaderViewByState();
								}
								// 一下子推到顶了
								else if (tempY - mStartY <= 0) {
									mHeadState = DONE;
									changeHeaderViewByState();
								}
								// 往下拉了，或者还没有上推到屏幕顶部掩盖head的地步
							}
							// 还没有到达显示松开刷新的时候,DONE或者是PULL_To_REFRESH状态
							if (mHeadState == PULL_TO_REFRESH) {

								setSelection(0);

								// 下拉到可以进入RELEASE_TO_REFRESH的状态
								if ((tempY - mStartY) / RATIO >= mHeadViewHeight) {
									mHeadState = RELEASE_TO_REFRESH;
									mIsBack = true;
									changeHeaderViewByState();
								} else if (tempY - mStartY <= 0) {
									mHeadState = DONE;
									changeHeaderViewByState();
								}
							}

							if (mHeadState == DONE) {
								if (tempY - mStartY > 0) {
									mHeadState = PULL_TO_REFRESH;
									changeHeaderViewByState();
								}
							}

							if (mHeadState == PULL_TO_REFRESH) {
								mHeadView.setPadding(0, -1 * mHeadViewHeight
										+ (tempY - mStartY) / RATIO, 0, 0);
								// setSelection(0);
								// mHeadView.scrollTo(0, -1*(-1 *
								// mHeadViewHeight
								// + (tempY - mStartY) / RATIO));
								// scrollTo(0,( -1 * mHeadViewHeight
								// + (tempY - mStartY) / RATIO));

							}

							if (mHeadState == RELEASE_TO_REFRESH) {
								mHeadView.setPadding(0, (tempY - mStartY)
										/ RATIO - mHeadViewHeight, 0, 0);
							}
						}

						// return true;
					}

					if (mIsFirstTouchMove) {
						mIsFirstTouchMove = false;
					}
				}
				break;
			}
		}

		// if (mHeaderViewVisible) {
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// if (event.getY() > mHeaderViewHeight) { // 没有点击在停靠栏
		//
		// } else if (event.getX() < mHeaderViewWidth / 2) { // 点击了左边的控件
		// mAdapter.onHeaderClick(mHeaderView, 0);
		// } else { // 点击了右边的控件
		// mAdapter.onHeaderClick(mHeaderView, 1);
		// }
		// // return super.onTouchEvent(event);
		// default:
		// break;
		// }
		// }

		return super.onTouchEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.e(TAG, "DOWN");

			if (mHeaderViewVisible
					&& mListScrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				if (event.getY() > mHeaderViewHeight) { // 没有点击在停靠栏
					return super.onInterceptTouchEvent(event);
				} else {

					return true;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			return super.onInterceptTouchEvent(event);
		}
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(event);
	}

	/**
	 * 当HeadView状态改变时候，调用该方法，以更新界面
	 * 
	 */
	private void changeHeaderViewByState() {
		switch (mHeadState) {
		case RELEASE_TO_REFRESH:

			isRefreshing = false;

			nLogoPlusImg.clearAnimation();
			// mArrowImageView.setVisibility(View.VISIBLE);
			// mProgressBar.setVisibility(View.GONE);
			// mTipsTextView.setVisibility(View.VISIBLE);
			// mLastUpdatedTextView.setVisibility(View.VISIBLE);
			//
			// mArrowImageView.startAnimation(mArrowAnim);
			// // 松开刷新
			nLogoText.setText(R.string.p2refresh_release_refresh);

			break;
		case PULL_TO_REFRESH:

			// 有下拉操作将状态传出去，下拉时根据这个标志位不显示停靠栏
			if (mAdapter != null) {
				mAdapter.isPullDown(1);
			}
			isRefreshing = false;
			nLogoPlusImg.clearAnimation();
			// mProgressBar.setVisibility(View.GONE);
			// mTipsTextView.setVisibility(View.VISIBLE);
			// mLastUpdatedTextView.setVisibility(View.VISIBLE);
			// mArrowImageView.clearAnimation();
			// mArrowImageView.setVisibility(View.VISIBLE);
			// 是由RELEASE_To_REFRESH状态转变来的
			if (mIsBack) {
				mIsBack = false;
				// mArrowImageView.clearAnimation();
				// mArrowImageView.startAnimation(mArrowReverseAnim);
				// // 下拉刷新
			} else {
				// 下拉刷新
			}

			nLogoText.setText(R.string.p2refresh_pull_to_refresh);
			break;

		case REFRESHING:
			if (mAdapter != null) {
				mAdapter.isPullDown(1);
			}

			mHeadView.setPadding(0, 0, 0, 0);

			isRefreshing = true;

			nLogoPlusImg.startAnimation(sunAnimation);
			// 实际上这个的setPadding可以用动画来代替。也有用Scroller可以实现这个效果，

			// mProgressBar.setVisibility(View.VISIBLE);
			// mArrowImageView.clearAnimation();
			// mArrowImageView.setVisibility(View.GONE);
			// // 正在刷新...
			nLogoText.setText(R.string.p2refresh_doing_head_refresh);
			// mLastUpdatedTextView.setVisibility(View.VISIBLE);

			break;
		case DONE:
			// 当回到原始状态或者刷新完成后，得把状态传出去，是上滑时显示停靠栏
			if (mAdapter != null) {
				mAdapter.isPullDown(0);
			}

			mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
			isRefreshing = false;
			nLogoPlusImg.clearAnimation();
			// 此处可以改进，同上所述。
			// mProgressBar.setVisibility(View.GONE);
			// mArrowImageView.clearAnimation();
			// mArrowImageView.setImageResource(R.drawable.pull_arow);
			// // 下拉刷新
			// mTipsTextView.setText(R.string.p2refresh_pull_to_refresh);
			// mLastUpdatedTextView.setVisibility(View.VISIBLE);

			break;
		}
	}

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

		mHeadState = DONE;
		// 最近更新: Time
		// nTimeText.setText(getResources().getString(
		// R.string.p2refresh_refresh_lasttime)
		// + new SimpleDateFormat(DATE_FORMAT_STR, Locale.CHINA)
		// .format(new Date()));
		changeHeaderViewByState();

		// if (!mCanLoadMore) {
		// mCanLoadMore = true;
		// mEndState = ENDINT_AUTO_LOAD_DONE;
		// changeEndViewByState();
		// }
	}

	/**
	 * 正在加载更多，FootView显示 ： 加载中...
	 * 
	 */
	private void onLoadMore() {
		if (mLoadMoreListener != null) {
			// 加载中...
			mEndLoadTipsTextView.setText(R.string.p2refresh_doing_end_refresh);
			mEndLoadTipsTextView.setVisibility(View.VISIBLE);
			mEndLoadProgressBar.setVisibility(View.VISIBLE);

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
	 * 主要更新一下刷新时间啦！
	 * 
	 * @param adapter
	 */
	public void setAdapter(BaseAdapter adapter) {
		// 最近更新: Time
		// nTimeText.setText(getResources().getString(
		// R.string.p2refresh_refresh_lasttime)
		// + new SimpleDateFormat(DATE_FORMAT_STR, Locale.CHINA)
		// .format(new Date()));

		// 设置停靠栏的adapter回调对象
		mAdapter = (HeaderAdapter) adapter;
		super.setAdapter(adapter);
	}

	/**
	 * 页面第一次载入时，不滑动显示下拉加载状态
	 */
	public void handSetToRefresh() {
		mHeadState = REFRESHING;
		changeHeaderViewByState();
		handler.obtainMessage(900).sendToTarget();
		onRefresh();
	}

	/**
	 * 切换子栏目的时候，获取当前的栏目的listview状态，保存起来
	 * 
	 * @return
	 */
	public Map<String, Integer> getHeaderState() {
		Map<String, Integer> ListStates = new HashMap<String, Integer>();
		ListStates.put("HeadState", mHeadState);
		ListStates.put("EndState", mEndState);
		if (mCanLoadMore) {
			ListStates.put("LoadMore", 1);
		} else {
			ListStates.put("LoadMore", 0);
		}
		return ListStates;
	}

	/**
	 * 切换子栏目时，根据传入的头尾状态值显示
	 * 
	 * @param states
	 */
	public void setLoadState(Map<String, Integer> states) {
		if (states != null) {
			mHeadState = states.get("HeadState");
			mEndState = states.get("EndState");
			mCanLoadMore = states.get("LoadMore") == 1;
		} else { // 初始化状态
			mHeadState = DONE;
			mEndState = ENDINT_AUTO_LOAD_DONE;
			mCanLoadMore = true;
		}
		changeHeaderViewByState();
		changeEndViewByState();
	}

	/**
	 * 根据当前状态刷新界面的样子
	 */
	public void refreshListState() {
		changeHeaderViewByState();
		changeEndViewByState();
	}

	public void setHeadFootTheme(Activity activity, boolean isDay) {
		if (mHeadView != null) {
			if (isDay) {
				ThemeUtil.setBackgroundDay(activity, mHeadView);
			} else {
				ThemeUtil.setBackgroundNight(activity, mHeadView);
			}
		}
		if (mEndRootView != null) {
			if (isDay) {
				ThemeUtil.setBackgroundDay(activity, mEndRootView);
			} else {
				ThemeUtil.setBackgroundNight(activity, mEndRootView);
			}
		}
	}

	public void setTheme(boolean isDay) {
		this.isDayTheme = isDay;
		if (isDayTheme) {
			if (mHeadView != null) {
				mHeadView.setBackgroundColor(COLOR_NORMAL_DAY);
			}
			if (mEndRootView != null) {
				mEndRootView.setBackgroundColor(COLOR_NORMAL_DAY);
			}
		} else {
			if (mHeadView != null) {
				mHeadView.setBackgroundColor(COLOR_NORMAL_NIGHT);
			}
			if (mEndRootView != null) {
				mEndRootView.setBackgroundColor(COLOR_NORMAL_NIGHT);
			}
		}
	}

	public void setContext(Context context) {
		this.mContext = context;
	}

	private int dip2pix(Context context, int dp) {
		float density = context.getResources().getDisplayMetrics().density;
		return Math.round((float) dp * density);
	}
}
