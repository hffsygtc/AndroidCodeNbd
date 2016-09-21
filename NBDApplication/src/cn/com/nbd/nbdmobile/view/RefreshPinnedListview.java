package cn.com.nbd.nbdmobile.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListAdapter;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;

public class RefreshPinnedListview extends RefreshListview {

	/**
	 * 停靠栏相关的属性
	 */
	private View mPinnedView; // 停靠栏的View
	private boolean mPinnedViewVisible; // 是否显示停靠栏
	private int mPinnedViewWidth; // 停靠栏的宽度
	private int mPinnedViewHeight; // 停靠栏的高度
	private static final int MAX_ALPHA = 255; // 停靠栏的透明度

	public RefreshPinnedListview(Context pContext) {
		super(pContext);
		init();
	}

	public RefreshPinnedListview(Context pContext, AttributeSet pAttrs) {
		super(pContext, pAttrs);
		init();
	}

	public RefreshPinnedListview(Context pContext, AttributeSet pAttrs,
			int pDefStyle) {
		super(pContext, pAttrs, pDefStyle);
		init();
	}

	private void init() {

	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mPinnedView != null) {
			mPinnedView.layout(0, 0, mPinnedViewWidth, mPinnedViewHeight);
			configureHeaderView(getFirstVisiblePosition() - 1);
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mPinnedView != null) {
			measureChild(mPinnedView, widthMeasureSpec, heightMeasureSpec);
			mPinnedViewWidth = mPinnedView.getMeasuredWidth();
			mPinnedViewHeight = mPinnedView.getMeasuredHeight();
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mPinnedViewVisible) {
			drawChild(canvas, mPinnedView, getDrawingTime());
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView pView, int pScrollState) {
		super.onScrollStateChanged(pView, pScrollState);
		// 先将消耗的滑动事件传递出去
		if (mAdapter != null) {
			mAdapter.onListScrollStateChanged(pView, pScrollState);
		}
	}

	@Override
	protected void handlePinnedTouch(MotionEvent event) {
		super.handlePinnedTouch(event);
		if (mPinnedViewVisible
				&& mListScrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			if (event.getY() > mPinnedViewHeight) { // 没有点击在停靠栏

			} else if (event.getX() < mPinnedViewWidth / 2) { // 点击了左边的控件
				mAdapter.onPinnedClick(mPinnedView, 0);
			} else { // 点击了右边的控件
				mAdapter.onPinnedClick(mPinnedView, 1);
			}
			isPinnedClicked = true;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mPinnedViewVisible
					&& mListScrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				if (event.getY() > mPinnedViewHeight) { // 没有点击在停靠栏
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

	@Override
	protected void dealPinnedVisible(int i) {
		super.dealPinnedVisible(i);
		// 有下拉操作将状态传出去，下拉时根据这个标志位不显示停靠栏
		if (mAdapter != null) {
			mAdapter.isPinnedPullDown(i);
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
		mPinnedView = view;
		if (mPinnedView != null) {
			// listview的上边和下边有黑色的阴影。xml中： android:fadingEdge="none"
			setFadingEdgeLength(0);
			if (isDay) {
				if (isToggle) {
					ThemeUtil.setSectionBackgroundDay(activity, mPinnedView);
				} else {
					ThemeUtil.setBackgroundDay(activity, mPinnedView);
				}
			} else {
				if (isToggle) {
					ThemeUtil.setSectionBackgroundNight(activity, mPinnedView);
				} else {
					ThemeUtil.setBackgroundNight(activity, mPinnedView);
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
		if (mPinnedView != null) {
			if (isDay) {
				if (isToggle) {
					ThemeUtil.setSectionBackgroundDay(activity, mPinnedView);
				} else {
					ThemeUtil.setBackgroundDay(activity, mPinnedView);
				}
			} else {
				if (isToggle) {
					ThemeUtil.setSectionBackgroundNight(activity, mPinnedView);
				} else {
					ThemeUtil.setBackgroundNight(activity, mPinnedView);
				}
			}
			if (mAdapter != null) {
				mAdapter.onThemeChange(mPinnedView, isDay);
			}
		}
	}

	/**
	 * 停靠栏：设置顶部停靠栏的显示滑动状态
	 * 
	 * @param position
	 */
	public void configureHeaderView(int position) {
		if (mPinnedView == null) {
			return;
		}
		int state = mAdapter.getPinnedState(position);
		switch (state) {

		case IPinnedAdapter.HEADER_GONE: {
			mPinnedViewVisible = false;
			break;
		}
		case IPinnedAdapter.HEADER_VISIBLE: {
			mAdapter.configurePinned(mPinnedView, position, MAX_ALPHA);
			if (mPinnedView.getTop() != 0) {
				mPinnedView.layout(0, 0, mPinnedViewWidth, mPinnedViewHeight);
			}
			mPinnedViewVisible = true;
			break;
		}
		case IPinnedAdapter.HEADER_PUSHED_UP: {
			View firstView = getChildAt(0);
			int bottom = firstView.getBottom();
			int headerHeight = mPinnedView.getHeight();
			int y;
			int alpha;
			if (bottom < headerHeight) {
				y = (bottom - headerHeight);
				alpha = MAX_ALPHA * (headerHeight + y) / headerHeight;
			} else {
				y = 0;
				alpha = MAX_ALPHA;
			}
			mAdapter.configurePinned(mPinnedView, position, alpha);
			if (mPinnedView.getTop() != y) {
				mPinnedView.layout(0, y, mPinnedViewWidth, mPinnedViewHeight
						+ y);
			}
			mPinnedViewVisible = true;
			break;
		}
		}
	}

	/**
	 * 获取是否在显示停靠栏
	 * 
	 * @return
	 */
	public boolean getIsSectionShow() {
		return mPinnedViewVisible;
	}

}
