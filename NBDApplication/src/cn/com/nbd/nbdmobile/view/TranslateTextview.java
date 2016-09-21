package cn.com.nbd.nbdmobile.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

public class TranslateTextview extends TextView{

	public TranslateTextview(Context context) {
		super(context);
	}

	public TranslateTextview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public TranslateTextview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}
	
	
	
}
