package cn.com.nbd.nbdmobile.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 修复ViewPager的bug
 * 
 * @author riche
 * */
public class ViewPagerFixed extends android.support.v4.view.ViewPager {
	
	float downX = 0;
	float downY = 0;


    public ViewPagerFixed(Context context) {
        super(context);
    }

    public ViewPagerFixed(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//	@Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        try {
//        	getParent().requestDisallowInterceptTouchEvent(true);
//            return super.onTouchEvent(ev);
//        } catch (IllegalArgumentException ex) {
//            ex.printStackTrace();
//        }
//        return false;
//    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	
    	    	switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.e("Inter", "dowm");
			downX = ev.getX();
			downY = ev.getY();
//			return false;
		case MotionEvent.ACTION_MOVE:
			Log.e("Inter", "move"+ev.getY()+"=="+downY+"=="+ev.getX()+"=="+downX);
			if (Math.abs((ev.getY() - downY)) > Math.abs(ev.getX()-downX)) {
				Log.e("Inter", "move==#####");
				return super.onInterceptTouchEvent(ev);
			}
			else {
				Log.e("Inter", "move==>");
				return true;
			}
			
		case MotionEvent.ACTION_UP:
			Log.e("Inter", "up");
			return super.onInterceptTouchEvent(ev);

		default:
			break;
		}
    	
    	return super.onInterceptTouchEvent(ev);
    	}
    
}