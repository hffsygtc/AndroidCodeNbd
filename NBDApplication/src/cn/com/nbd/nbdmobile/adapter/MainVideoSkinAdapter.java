package cn.com.nbd.nbdmobile.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.Options;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.view.WithoutSectionCustomListViewUnused;

import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.skin.videoview.vod.UIVodVideoView;
import com.nbd.article.bean.ArticleInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nbd.view.HeadListView;
//import com.nbd.view.HeadListView.HeaderAdapter;

/**
 * 新闻界面适配器，不带子栏目切换功能，拥有证券信息 集成下拉上拉功能
 * 
 * @author riche
 */
public class MainVideoSkinAdapter extends BaseAdapter {

	private final static String TAG = "VIDEO_ADAPTER";

	ArrayList<ArticleInfo> newsList;

	Activity activity;
	LayoutInflater inflater = null;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	// 对应于不同的固定holder的播放面板
	// 记录每一个item的位置position对应的关联holder
	private Map<Integer, ViewHolder> holderMap = new HashMap<Integer, MainVideoSkinAdapter.ViewHolder>();

	private Map<ViewHolder, UIVodVideoView> videoMap = new HashMap<MainVideoSkinAdapter.ViewHolder, UIVodVideoView>();

	// 当前正在播放的乐视播放器
	private String path = "";

	private Bundle mBundle;

	List<String> videoId = new ArrayList<String>();

	private int videoPlayPosition = -1;

	private boolean isDayTheme;
	private boolean isTextMode;
	private String imageUri = "";

	private UIVodVideoView mVideoView;
	private RelativeLayout.LayoutParams mVideoParams;
	private VideoViewListener mVideoViewListener;

	private int screenType;

	// private V4PlaySkin playSkin;
	// private ReSurfaceView surfaceView;

	public static interface OnNewsClickListener {
		public void onNewsClick(View view, ArticleInfo article); // 传递数据给activity
		
		public void onVideoPlay(Bundle bundle,String title);
	}

	OnNewsClickListener onNewsClick;

	public void setNewsClickListener(OnNewsClickListener onNewsClick) {
		this.onNewsClick = onNewsClick;
	}

	public interface onScreenFullChange {
		void onFullVideo(int position);
	}

	onScreenFullChange onScreenChange;

	public void setScreenChange(onScreenFullChange mChange) {
		this.onScreenChange = mChange;
	}

	// surfaceView的生命周期
	boolean isPanoVideo = false;

	public MainVideoSkinAdapter(Activity activity,
			ArrayList<ArticleInfo> newsList, boolean isDay, boolean isText,
			int initType) {
		this.activity = activity;
		this.newsList = newsList;
		this.isDayTheme = isDay;
		this.isTextMode = isText;
		this.screenType = initType;
		inflater = LayoutInflater.from(activity);
		options = Options.getListOptions();

		init();

	}

	/**
	 * 定义初始化一些监听和变量
	 */
	private void init() {
		mVideoView = new UIVodVideoView(activity,false);

		mVideoParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);

		mVideoViewListener = new VideoViewListener() {

			@Override
			public void onStateResult(int event, Bundle bundle) {
				handlePlayerEvent(event, bundle);

			}
		};

	}

	@Override
	public int getCount() {
		// return newsList == null ? 0 : newsList.size() + 1;
		return newsList.size();
	}

	@Override
	public ArticleInfo getItem(int position) {

		return newsList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder mHolder;
		View view = convertView;
		// 关联holder控件
		if (view == null) {
			view = inflater.inflate(R.layout.item_video_list, null);
			mHolder = new ViewHolder();

			mHolder.itemLayout = (RelativeLayout) view
					.findViewById(R.id.video_item_layout);
			mHolder.textLayout = (LinearLayout) view
					.findViewById(R.id.video_text_layout);
			mHolder.shareLayout = (RelativeLayout) view
					.findViewById(R.id.video_btm_right_layout);

			mHolder.playLayout = (RelativeLayout) view
					.findViewById(R.id.letv_play_layout);
			mHolder.videoBackground = (ImageView) view
					.findViewById(R.id.video_bg_image);
			mHolder.playImg = (ImageView) view
					.findViewById(R.id.video_play_img);

			mHolder.playNumTxt = (TextView) view
					.findViewById(R.id.video_play_times_txt);
			mHolder.videoCoverImg = (ImageView) view
					.findViewById(R.id.video_bg_image_cover);
			mHolder.titleTxt = (TextView) view
					.findViewById(R.id.video_item_title_txt);

			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}

		setThemeColor(mHolder, isDayTheme);

		/**
		 * STEP 2:getview复用的holder和根据播放位置取出的holder一致，正在播放的view被移除了
		 */
		// if (videoPlayPosition != -1 && holderMap.get(videoPlayPosition) !=
		// null
		// && mHolder == holderMap.get(videoPlayPosition)) {
		// Log.e("DESTROY==TEST", "LOCATION" + videoPlayPosition);
		// if (videoMap.get(holderMap.get(videoPlayPosition)) != null) {
		// videoMap.get(holderMap.get(videoPlayPosition)).stopAndRelease();
		// }
		// videoPlayPosition = -1;
		// }

//		if (videoPlayPosition != -1 && position == videoPlayPosition) {
//			mHolder.videoBackground.setVisibility(View.GONE);
//			mHolder.playImg.setVisibility(View.GONE);
//			mHolder.titleTxt.setVisibility(View.GONE);
//			mHolder.videoCoverImg.setVisibility(View.GONE);
//			mHolder.playLayout.setVisibility(View.VISIBLE);
//		} else {
			mHolder.videoBackground.setVisibility(View.VISIBLE);
			mHolder.playImg.setVisibility(View.VISIBLE);
			mHolder.titleTxt.setVisibility(View.VISIBLE);
			mHolder.videoCoverImg.setVisibility(View.VISIBLE);
			mHolder.playLayout.setVisibility(View.GONE);
			// 显示界面的数据
			if (getItem(position) != null) {
				mHolder.titleTxt.setText(getItem(position).getTitle());
				if (isTextMode) {
					imageLoader.displayImage(imageUri, mHolder.videoBackground,
							options);
				} else {
					imageLoader.displayImage(getItem(position).getThumbnail(),
							mHolder.videoBackground, options);
				}
				mHolder.playNumTxt.setText(getItem(position)
						.getMobile_click_count() + "");
			}
//		}

		/**
		 * 播放按钮的功能
		 */
		mHolder.playImg.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if (getItem(position) != null) {
					mBundle = setVodParams("hqrinotu0v", getItem(position)
							.getVideo_id(), "", "297d2469f53cd4727909656aba345ab9",
							"", false);
					
					if (mBundle != null && onNewsClick != null) {
						onNewsClick.onVideoPlay(mBundle,getItem(position).getTitle());
					}
				}

//				// step 1: 判断点击的position是否已经在MAP里面关联了HOLDER
//				if (holderMap.get(position) == null) {
//					holderMap.put(position, mHolder); // 将点击位置的holder保存起来
//				}
//
//				// 当前点击的位置和正在播放的视频位置不一样
//				if (videoPlayPosition != -1 && position != videoPlayPosition) {
//					Log.e(TAG, "now cilck is DIFFERENT from playing one!"
//							+ "--playing position==>" + videoPlayPosition
//							+ "--now position-->" + position);
//					// if (playBoard != null) {
//					// playBoard.onDestroy();
//					if (videoPlayPosition != -1
//							&& holderMap.get(videoPlayPosition) != null) {
//						Log.e(TAG,
//								"show position not null" + "not null"
//										+ "--holder-->"
//										+ holderMap.get(videoPlayPosition));
//						if (videoMap.get(holderMap.get(videoPlayPosition)) != null) {
//							videoMap.get(holderMap.get(videoPlayPosition))
//									.stopAndRelease();
//						}
//						holderMap.get(videoPlayPosition).playLayout
//								.setVisibility(View.GONE);
//						holderMap.get(videoPlayPosition).videoBackground
//								.setVisibility(View.VISIBLE);
//						holderMap.get(videoPlayPosition).titleTxt
//								.setVisibility(View.VISIBLE);
//						holderMap.get(videoPlayPosition).videoCoverImg
//								.setVisibility(View.VISIBLE);
//						holderMap.get(videoPlayPosition).playImg
//								.setVisibility(View.VISIBLE);
//					}
//				}
//
//				Log.e(TAG, "show video id-->" + getItem(position).getVideo_id()
//						+ "--click holder-->" + mHolder);
//				mBundle = setVodParams("hqrinotu0v", getItem(position)
//						.getVideo_id(), "", "297d2469f53cd4727909656aba345ab9",
//						"", false);
//
//				if (videoMap.get(mHolder) == null) { // 如果对应的HOLDER还没有播放器
//					UIVodVideoView positionVideo = new UIVodVideoView(activity);
//					positionVideo.setVideoViewListener(new VideoViewListener() {
//
//						@Override
//						public void onStateResult(int event, Bundle bundle) {
//							handlePlayerEvent(event, bundle);
//						}
//					});
//					mHolder.playLayout.addView((View) positionVideo,
//							mVideoParams);
//					videoMap.put(mHolder, positionVideo);
//					Log.e(TAG, "put video to holder-->" + mHolder);
//				}
//
//				videoPlayPosition = position;
//				mHolder.playLayout.setVisibility(View.VISIBLE);
//				videoMap.get(mHolder).setDataSource(mBundle);
//
//				mHolder.playImg.setVisibility(View.GONE);
//				mHolder.videoBackground.setVisibility(View.GONE);
//				mHolder.titleTxt.setVisibility(View.GONE);
//				mHolder.videoCoverImg.setVisibility(View.GONE);
			}
		});

		mHolder.shareLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onNewsClick.onNewsClick(null, getItem(position));
			}
		});

		return view;
	}

	/**
	 * 根据日间模式或者夜间模式切换ITEM的显示方式
	 * 
	 * @param mHolder
	 * @param isDayTheme2
	 */
	private void setThemeColor(ViewHolder mHolder, boolean isDayTheme) {

		if (isDayTheme) {
			ThemeUtil.setBackgroundDay(activity, mHolder.itemLayout);
			ThemeUtil.setDrawableBackground(activity, mHolder.textLayout,
					R.drawable.video_item_bottom_bg);

		} else {
			ThemeUtil.setBackgroundNight(activity, mHolder.itemLayout);
			ThemeUtil.setDrawableBackground(activity, mHolder.textLayout,
					R.drawable.video_item_bottom_bg);
		}

	}

	static class ViewHolder {

		// 整个项目布局，用于控制背景
		RelativeLayout itemLayout;
		// 文字部分的布局
		LinearLayout textLayout;
		// 播放面板皮肤
		// V4PlaySkin playSkin;
		RelativeLayout playLayout;

		// 视频背景缩略图
		ImageView videoBackground;
		// 视频遮罩图层
		ImageView videoCoverImg;
		// 视频播放按钮
		ImageView playImg;
		// 视频标题
		TextView titleTxt;
		// 视频播放次数
		TextView playNumTxt;

		RelativeLayout shareLayout;

	}

	public void setDataList(ArrayList<ArticleInfo> news) {
		this.newsList = news;
	}

	/**
	 * 设置数据的更新
	 * 
	 * @param scrolllists
	 *            轮播数据
	 * @param stocks
	 *            股指数据
	 * @param ads
	 *            广告数据
	 * @param lists
	 *            列表数据
	 */
	public void setDataChange(ArrayList<ArticleInfo> lists) {
		this.newsList = lists;

	}

	/**
	 * 乐视云点播参数设置 没有的数值传空字符串""
	 * 
	 * @param uuid
	 * @param vuid
	 * @param checkCode
	 * @param userKey
	 * @param isPannoVideo
	 * @return
	 */
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

	public void changeTheme(boolean isDay) {
		this.isDayTheme = isDay;
	}

	public void changeTextMode(boolean isText) {
		this.isTextMode = isText;
	}

	
	public void changeShowScreenType(Configuration newConfig) {
//
//		Log.e("CHANGE TYPE SCREEN==", "PLAY POSITION==" + videoPlayPosition);
//
//		// if (playBoard != null) {
//		Log.e("adapter==", "play borad!!");
//
//		// onScreenChange.onFullVideo(videoPlayPosition);
//
//		if (holderMap.get(videoPlayPosition) != null) {
//			if (videoMap.get(holderMap.get(videoPlayPosition)) != null) {
//				videoMap.get(holderMap.get(videoPlayPosition))
//						.onConfigurationChanged(newConfig);
//				
//			}
//		}
	}

	/**
	 * 处理播放器本身的事件
	 * 
	 * @param event
	 * @param bundle
	 */
	private void handlePlayerEvent(int state, Bundle bundle) {
		Log.e(TAG, "state=>" + state);

		switch (state) {
		case PlayerEvent.PLAY_PREPARED:
			Log.e(TAG, "=" + videoPlayPosition);
			if (videoPlayPosition != -1
					&& holderMap.get(videoPlayPosition) != null) {
				Log.e(TAG, "show holder--" + holderMap.get(videoPlayPosition));
				if (videoMap.get(holderMap.get(videoPlayPosition)) != null) {
					Log.e(TAG, "do-onstar");
					videoMap.get(holderMap.get(videoPlayPosition)).onStart();
				}
			}
			break;
		case PlayerEvent.PLAY_VIDEOSIZE_CHANGED:
			break;

		default:
			break;
		}

	}

}