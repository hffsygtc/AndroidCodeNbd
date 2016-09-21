package cn.com.nbd.nbdmobile.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class StaticViewPager extends android.support.v4.view.ViewPager {

	private boolean isCanScroll = true;

//	private int abc = 1;
//	private float mLastMotionX;
//	private float firstDownX;
//	private float firstDownY;
//	private boolean flag = false;

	public StaticViewPager(Context context) {
		super(context);
	}

	public StaticViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setCanScroll(boolean isCanScroll) {
		this.isCanScroll = isCanScroll;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {

		if (isCanScroll) {
			return super.onTouchEvent(ev);
		} else {
			return false;
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (isCanScroll) {
			return super.onInterceptHoverEvent(ev);
		} else {
			return false;
		}
	}

	@Override
	public void scrollTo(int x, int y) {
		if (isCanScroll) {
			super.scrollTo(x, y);
		}
	}

	/**
	 * 该pager消化掉滑动事件，不回传给父控件，让子控件滑动边缘不拉动父控件滑动
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		this.requestDisallowInterceptTouchEvent(true);
		// TODO Auto-generated method stub
		// final float x = ev.getX();
		// switch (ev.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// this.requestDisallowInterceptTouchEvent(true);
		// abc = 1;
		// mLastMotionX = x;
		// break;
		// case MotionEvent.ACTION_MOVE:
		// Log.d("hff", x+"<<>>"+mLastMotionX);
		// if (abc == 1) {
		// if (x - mLastMotionX > 5 && getCurrentItem() == 0) {
		// abc = 0;
		// this.requestDisallowInterceptTouchEvent(false);
		// }
		//
		// if (x - mLastMotionX < -5
		// && getCurrentItem() == getAdapter().getCount() - 1) {
		// abc = 0;
		// this.requestDisallowInterceptTouchEvent(false);
		// }
		// }
		// break;
		// case MotionEvent.ACTION_UP:
		// case MotionEvent.ACTION_CANCEL:
		// this.requestDisallowInterceptTouchEvent(false);
		// break;
		// }
		return super.dispatchTouchEvent(ev);

	}

}
