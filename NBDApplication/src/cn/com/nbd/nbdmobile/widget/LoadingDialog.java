package cn.com.nbd.nbdmobile.widget;

import cn.com.nbd.nbdmobile.R;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class LoadingDialog extends Dialog {

	private ImageView mImageView;
	private TextView mTitle;
	private String mContent;
	private Context mContext;
	
	public LoadingDialog(Context context, int theme,String content) {
		super(context, theme);
		this.mContext = context;
		this.mContent = content;
	}

	public LoadingDialog(Context context) {
		super(context);
		this.mContext = context;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading_dialog);
		
		mImageView = (ImageView) findViewById(R.id.loading_img);
		mTitle = (TextView) findViewById(R.id.loading_text);
		
		 // 加载动画  
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
                mContext, R.drawable.loading_bar);  
        // 使用ImageView显示动画  
        mImageView.startAnimation(hyperspaceJumpAnimation);  
        
        if (mContent != null) {
        	mTitle.setText(mContent);
		}
	}
	
	public void showFullDialog(){
		this.show();
		// 加载动画  
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(  
				mContext, R.drawable.loading_bar);  
		// 使用ImageView显示动画  
		mImageView.startAnimation(hyperspaceJumpAnimation);  
		
		if (mContent != null) {
			mTitle.setText(mContent);
		}
		WindowManager windowManager = ((Activity)mContext).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = this.getWindow()
				.getAttributes();
		lp.width = (int) (display.getWidth()); // 设置宽度
		lp.height = (int)(display.getHeight());
		this.getWindow().setAttributes(lp);
	}
	
}
