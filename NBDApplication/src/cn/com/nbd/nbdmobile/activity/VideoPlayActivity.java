package cn.com.nbd.nbdmobile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;

import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.videoview.IMediaDataVideoView;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.skin.videoview.vod.UIVodVideoView;
import com.umeng.analytics.MobclickAgent;

public class VideoPlayActivity extends Activity {
	public final static String DATA = "data";

	private IMediaDataVideoView videoView;
	
	private TextView mTitleText;
	private String Title;
	VideoViewListener mVideoViewListener = new VideoViewListener() {

		@Override
		public void onStateResult(int event, Bundle bundle) {
			handleVideoInfoEvent(event, bundle);// 处理视频信息事件
			handlePlayerEvent(event, bundle);// 处理播放器事件
			handleLiveEvent(event, bundle);// 处理直播类事件,如果是点播，则这些事件不会回调

		}
	};
	private Bundle mBundle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_fullscreen);
		initData();
		initView();
		MobclickAgent.onEvent(VideoPlayActivity.this, UmenAnalytics.DETAIL_VIDEO);
	}

	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			mBundle = intent.getBundleExtra(DATA);
			Title = intent.getStringExtra("title");
		}
	}

	private void initView() {
		videoView = new UIVodVideoView(this,true);
		videoView.setVideoViewListener(mVideoViewListener);

		mTitleText = (TextView) findViewById(R.id.video_title);
		if (Title != null && !Title.equals("")) {
			mTitleText.setText(Title);
		}
		RelativeLayout videoContainer = (RelativeLayout) findViewById(R.id.videoContainer);
		
		videoContainer.addView((View) videoView,
				computeContainerSize(this, 16, 9));

		videoView.setDataSource(mBundle);
	}

	@Override
	protected void onResume() {
		super.onResume();
		videoView.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		videoView.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (videoView != null) {
			videoView.onDestroy();
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (videoView != null) {
			videoView.onConfigurationChanged(newConfig);
		}
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
			if (videoView != null) {
				videoView.onStart();
//				Configuration newConfig = new Configuration();
//				newConfig.orientation = Configuration.ORIENTATION_LANDSCAPE;
//				videoView.onConfigurationChanged(newConfig);
//				setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 处理直播类事件
	 */
	private void handleLiveEvent(int state, Bundle bundle) {
	}

	/**
	 * 处理视频信息类事件
	 */
	private void handleVideoInfoEvent(int state, Bundle bundle) {
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

	private int getScreenHeight(Context context) {
		WindowManager wm = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		int height = wm.getDefaultDisplay().getHeight();
		return height;
	}
	
}
