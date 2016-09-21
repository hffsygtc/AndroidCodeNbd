package cn.com.nbd.nbdmobile.widget;

import com.lecloud.skin.videoview.vod.UIVodVideoView;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import cn.com.nbd.nbdmobile.R;

public class FullScreenVideoDialog extends Dialog {

	private Context mContext;
	
	private RelativeLayout mFullScreenLayout;
	
	private UIVodVideoView mVideoView;
	
	public interface onDialogBtnClick{
		void onChoosePhotoFrom(int type);
	}
	
	private onDialogBtnClick mDialogBtnClick;
	

	public FullScreenVideoDialog(Context context) {
		super(context);
	}

	public FullScreenVideoDialog(Context context, int theme,UIVodVideoView videoView) {
		super(context, theme);

		this.mContext = context;
		this.mVideoView = videoView;


	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_full_video_layout);
		
		mFullScreenLayout = (RelativeLayout) findViewById(R.id.full_screen_layout);
		
		ViewGroup parent = (ViewGroup) mVideoView.getParent();
		if (parent != null) {
			parent.removeAllViews();
		}

		mFullScreenLayout.addView((View) mVideoView,
				computeContainerSize(mContext, 16, 9));

		setListener();
	}

	/**
	 * 设置监听
	 */
	private void setListener() {

	}
	
	public void setOnBtnClickListener(onDialogBtnClick listener){
		this.mDialogBtnClick = listener;
	}
	
	private RelativeLayout.LayoutParams computeContainerSize(Context context,
			int mWidth, int mHeight) {
		int width = getScreenWidth(context);
		int height = width * mHeight / mWidth;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		params.width = width;
		params.height = height;
		params.addRule(RelativeLayout.CENTER_IN_PARENT);
		return params;
	}

	private int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int width = wm.getDefaultDisplay().getWidth();
		return width;
	}

}
