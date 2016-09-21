package cn.com.nbd.nbdmobile.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * 带顶部可以固定栏的ListView
 * 
 * @author riche
 *
 */
public class HeadListView extends ListView {

	public interface HeaderAdapter {
		public static final int HEADER_GONE = 0;
		public static final int HEADER_VISIBLE = 1;
		public static final int HEADER_PUSHED_UP = 2;

		int getHeaderState(int position);

		void configureHeader(View header, int position, int alpha);

		void onHeaderClick(View header,int position);
	}

	private static final int MAX_ALPHA = 255;

	private HeaderAdapter mAdapter;
	private View mHeaderView; // 停靠栏的View
	private boolean mHeaderViewVisible; // 是否显示停靠栏

	private int mHeaderViewWidth; // 悬靠列的宽度
	private int mHeaderViewHeight; // 悬靠列的高度

	public HeadListView(Context context) {
		super(context);
	}

	public HeadListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HeadListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 定义布局位置
	 * 
	 */
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if (mHeaderView != null) {
			mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
			configureHeaderView(getFirstVisiblePosition());
		}
	}

	/**
	 * 测量停靠栏的宽高属性
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
	 * 处理停靠栏的布局
	 * 
	 * @param view
	 *            加载的停靠栏布局的layout
	 */
	public void setPinnedHeaderView(View view) {
		mHeaderView = view;
		if (mHeaderView != null) {
			// listview的上边和下边有黑色的阴影。xml中： android:fadingEdge="none"
			setFadingEdgeLength(0);
		}

		requestLayout();
	}

	public void setAdapter(ListAdapter adapter) {
		super.setAdapter(adapter);
		mAdapter = (HeaderAdapter) adapter;
	}

	/**
	 * 设置顶部停靠栏
	 * 
	 * @param position
	 */
	public void configureHeaderView(int position) {
		if (mHeaderView == null) {
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

	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (mHeaderViewVisible) {
			drawChild(canvas, mHeaderView, getDrawingTime());
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mHeaderViewVisible) {
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (ev.getY() > mHeaderViewHeight) { // 没有点击在停靠栏

				} else if (ev.getX() < mHeaderViewWidth / 2) { // 点击了左边的控件
					mAdapter.onHeaderClick(mHeaderView,0);
				} else { // 点击了右边的控件
					mAdapter.onHeaderClick(mHeaderView,1);
				}
				return true;
			default:
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		 return super.onInterceptTouchEvent(ev);

//		if (ev.getY() > mHeaderViewHeight) { // 没有点击在停靠栏
//			return false;
//		} else {
//			Log.d("hff", "ACTION>>>" + ev.getY() + "==" + mHeaderViewHeight);
//			return true;
//		}
//
	}
}
