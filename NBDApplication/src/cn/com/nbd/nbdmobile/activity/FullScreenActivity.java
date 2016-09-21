package cn.com.nbd.nbdmobile.activity;

import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.skin.videoview.vod.UIVodVideoView;

import cn.com.nbd.nbdmobile.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class FullScreenActivity extends Activity {
	
	private RelativeLayout videoLayout;
	private UIVodVideoView mVideoView;
	private VideoViewListener mVideoViewListener; // 视频播放视图的监听
	
	String videoId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_full_video_layout);
		
		Intent intent = getIntent();
		
		videoLayout = (RelativeLayout) findViewById(R.id.full_screen_layout);
		
		mVideoViewListener = new VideoViewListener() {

			@Override
			public void onStateResult(int event, Bundle bundle) {
				handlePlayerEvent(event, bundle);// 处理播放器事件
			}
		};

		mVideoView = new UIVodVideoView(this, false);
		
		mVideoView.setVideoViewListener(mVideoViewListener);
		
		videoLayout.addView((View) mVideoView,
				computeContainerSize(this, 16, 9));

		Bundle mBundle = setVodParams("hqrinotu0v",
				"0036b8bc14", "",
				"297d2469f53cd4727909656aba345ab9", "", false);
		mVideoView.setDataSource(mBundle);
		
		
	}
	
	/**
	 * 处理播放器本身事件，具体事件可以参见IPlayer类
	 */
	private void handlePlayerEvent(int state, Bundle bundle) {
		switch (state) {
		case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
			/**
			 * 获取到视频的宽高的时候，此时可以通过视频的宽高计算出比例，进而设置视频view的显示大小。
			 * 如果不按照视频的比例进行显示的话，(以surfaceView为例子)内容会填充整个surfaceView。
			 * 意味着你的surfaceView显示的内容有可能是拉伸的
			 */
			break;

		case PlayerEvent.PLAY_PREPARED:
			// 播放器准备完成，此刻调用start()就可以进行播放了
			if (mVideoView != null) {
				mVideoView.onStart();
				
//				FullScreenVideoDialog dialog = new FullScreenVideoDialog(context, R.style.loading_dialog, mVideoView);
//				dialog.show();
			}
			break;
		default:
			break;
		}
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

	private Bundle setVodParams(String uuid, String vuid, String checkCode,
			String userKey, String playName, boolean isPannoVideo) {
		Bundle mBundle = new Bundle();
		mBundle.putInt(PlayerParams.KEY_PLAY_MODE,
				PlayerParams.VALUE_PLAYER_VOD);
		mBundle.putString(PlayerParams.KEY_PLAY_UUID, uuid);
		mBundle.putString(PlayerParams.KEY_PLAY_VUID, vuid);
		// mBundle.putString(PlayProxy.PLAY_CHECK_CODE, checkCode);
		// mBundle.putString(PlayProxy.PLAY_PLAYNAME, playName);
		// mBundle.putString(PlayProxy.PLAY_USERKEY, userKey);
		// mBundle.putBoolean(PlayProxy.BUNDLE_KEY_ISPANOVIDEO, isPannoVideo);
		return mBundle;
	}

}
