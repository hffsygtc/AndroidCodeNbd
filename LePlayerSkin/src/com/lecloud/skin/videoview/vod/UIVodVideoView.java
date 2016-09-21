package com.lecloud.skin.videoview.vod;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lecloud.sdk.api.md.entity.vod.VideoHolder;
import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.constant.StatusCode;
import com.lecloud.sdk.player.IAdPlayer;
import com.lecloud.sdk.player.IMediaDataPlayer;
import com.lecloud.sdk.player.IPlayer;
import com.lecloud.sdk.videoview.vod.VodVideoView;
import com.lecloud.skin.ui.ILetvVodUICon;
import com.lecloud.skin.ui.LetvVodUIListener;
import com.lecloud.skin.ui.impl.LetvVodUICon;
import com.lecloud.skin.ui.utils.NetworkUtils;
import com.lecloud.skin.ui.utils.ScreenUtils;
import com.lecloud.skin.ui.utils.timer.IChange;
import com.lecloud.skin.ui.utils.timer.LeTimerManager;
import com.lecloud.skin.ui.view.VideoNoticeView.IReplayListener;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class UIVodVideoView extends VodVideoView {
    public static final String TAG = "UIVodVideoView";

    private static final String PREF_SEEK_INFO = "seek_info";

    protected ILetvVodUICon letvVodUICon;
    protected int width = -1;
    protected int height = -1;
    protected LeTimerManager leTimerManager;
    TextView timeTextView;
    private int event;
    private Bundle bundle;
    private long lastPosition;
    private LinkedHashMap<String, String> vtypeList;
    //是否正在seeking
    private boolean isSeeking = false;
    
    private boolean isCanRotote;
    
    public UIVodVideoView(Context context,boolean canScreenRotate) {
        super(context);
        initUICon(context);
        getLeTimerManager(500);
        this.isCanRotote = canScreenRotate;
    }

    public LeTimerManager getLeTimerManager(long delaymillts) {
        if (leTimerManager == null) {
            leTimerManager = new LeTimerManager(new IChange() {

                @Override
                public void onChange() {
                    if (letvVodUICon != null && player != null) {
                        post(new  Runnable() {
                            
                            @Override
                            public void run() {
                                letvVodUICon.setDuration(player.getDuration());
                                Log.d("meng", "isSeeking" +isSeeking);
                                if(!isSeeking && player.getCurrentPosition() <= player.getDuration()){
                                	letvVodUICon.setCurrentPosition(player.getCurrentPosition());
                                }
                                letvVodUICon.setPlayState(player.isPlaying());
                            }
                        });
                    }
                }
            }, delaymillts);
        }
        return leTimerManager;
    }

    private void stopTimer() {
        if (leTimerManager != null) {
            leTimerManager.stop();
            leTimerManager = null;
        }
    }

    private void startTimer() {
        if (leTimerManager == null) {
            getLeTimerManager(500);
        }
        if (leTimerManager != null) {
            leTimerManager.start();
        }
    }

    private void initUICon(final Context context) {
    	Log.e("HFF LE-->", "NEW PLAY CONTEXT==");
        letvVodUICon = new LetvVodUICon(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addView(letvVodUICon.getView(), params);
        letvVodUICon.setRePlayListener(new IReplayListener() {
            
            @Override
            public Bundle getReportParams() {
                return ((IMediaDataPlayer) player).getReportParams();
            }

            @Override
            public void onRePlay() {
                setLastPostion();
                player.retry();
            }
        });
//        letvVodUICon.hideWaterMark()
        letvVodUICon.setLetvVodUIListener(new LetvVodUIListener() {

            @Override
            public void setRequestedOrientation(int requestedOrientation) {
            	Log.e("PLAY FULL SCREEN-->", "CLICK YES");
            	
                if (context instanceof Activity && isCanRotote) {
                    ((Activity) context).setRequestedOrientation(requestedOrientation);
                }
            }

            @Override
            public void resetPlay() {
                // LeLog.dPrint(TAG, "--------resetPlay");
            }

            @Override
            public void onSetDefination(int positon) {
//                UIVodVideoView.super.onInterceptMediaDataSuccess(event, bundle);
                if (vtypeList != null && vtypeList.size() > 0) {
                    setLastPostion();
                    ((IMediaDataPlayer) player).setDataSourceByRate(new ArrayList<String>(vtypeList.keySet()).get(positon));
                }
            }

            @Override
            public void onSeekTo(float sec) {
    			Log.d("meng", "onSeekTo" +isSeeking);
                long msec = (long) Math.floor((sec * player.getDuration()));
                if (player != null) {
                    player.seekTo(msec);
                    if (isComplete()) {
                        player.retry();
                    }else if (!player.isPlaying()) {
                        player.start();
                    }
                    ((LetvVodUICon) letvVodUICon).syncSeekProgress((int)msec);
                }
            }

            @Override
            public void onClickPlay() {
                if (player.isPlaying()) {
                    player.pause();
                } else if (isComplete()) {
                    player.seekTo(0);
                    player.retry();
                } else {
                    player.start();
                }
            }

            @Override
            public void onUIEvent(int event, Bundle bundle) {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onSwitchPanoVideoMode(int mode) {
                switchPanoVideoMode(mode);
            }

			@Override
			public void onProgressChanged(int progress) {
				// TODO Auto-generated method stub
//				((LetvVodUICon) letvVodUICon).syncSeekProgress(progress);
			}

			@Override
			public void onStartSeek() {
				// TODO Auto-generated method stub
				isSeeking = true;
				Log.d("meng", "startSeek" +isSeeking);
			}

        });
    }
    
    protected void switchPanoVideoMode(int mode) {

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (width == -1 || height == -1) {
            width = UIVodVideoView.this.getLayoutParams().width;
            height = UIVodVideoView.this.getLayoutParams().height;
        }
        if (ScreenUtils.getOrientation(getContext()) == Configuration.ORIENTATION_PORTRAIT) {
            UIVodVideoView.this.getLayoutParams().height = height;
            UIVodVideoView.this.getLayoutParams().width = width;
            letvVodUICon.setRequestedOrientation(ILetvVodUICon.SCREEN_PORTRAIT);
        } else {
            UIVodVideoView.this.getLayoutParams().height = ScreenUtils.getHeight(getContext());
            UIVodVideoView.this.getLayoutParams().width = ScreenUtils.getWight(getContext());
            letvVodUICon.setRequestedOrientation(ILetvVodUICon.SCREEN_LANDSCAPE);
        }
        super.onConfigurationChanged(newConfig);
    }
    
    @Override
    protected void onInterceptMediaDataSuccess(int event, Bundle bundle) {
        this.event = event;
        this.bundle = bundle;
        switch (event) {
            case PlayerEvent.MEDIADATA_VOD: {
                VideoHolder videoHolder = bundle.getParcelable(PlayerParams.KEY_RESULT_DATA);
                String defaultVtype = videoHolder.getDefaultVtype();
                vtypeList = videoHolder.getVtypes();
                String title=videoHolder.getTitle();
                if(!TextUtils.isEmpty(title)){
                	letvVodUICon.setTitle(title);
                }
                List<String> ratetypes = new ArrayList<String>(videoHolder.getVtypes().values());
                letvVodUICon.setRateTypeItems(ratetypes, vtypeList.get(defaultVtype));
                letvVodUICon.showWaterMark(((VideoHolder) bundle.getParcelable(PlayerParams.KEY_RESULT_DATA)).getCoverConfig());
                
                Log.e("HFF--LE-->", ((VideoHolder) bundle.getParcelable(PlayerParams.KEY_RESULT_DATA)).getCoverConfig().getLogoConfig().getPicUrl());
                break;
            }

            default:
                break;
        }

        super.onInterceptMediaDataSuccess(event, bundle);
    }
    @Override
    protected void onInterceptMediaDataError(int event, Bundle bundle) {
        super.onInterceptMediaDataError(event, bundle);
        letvVodUICon.hideLoading();
        letvVodUICon.hideWaterMark();
        letvVodUICon.processMediaState(event, bundle);
    }
    

    @Override
    protected void notifyPlayerEvent(int event, Bundle bundle) {
        super.notifyPlayerEvent(event, bundle);
        letvVodUICon.processPlayerState(event, bundle);
        switch (event) {
            case PlayerEvent.PLAY_COMPLETION://202
                //btn pause
                letvVodUICon.setPlayState(false);
                //update progress
                if (letvVodUICon != null && player != null) {
                    letvVodUICon.setDuration(player.getDuration());
                    letvVodUICon.setCurrentPosition(player.getCurrentPosition());
                }
                lastPosition = 0;

                stopTimer();
                break;
            case PlayerEvent.PLAY_INFO:
                int code = bundle.getInt(PlayerParams.KEY_RESULT_STATUS_CODE);
                if (code == StatusCode.PLAY_INFO_VIDEO_RENDERING_START) {
                    startTimer();
                }
                
                switch (code) {
                case StatusCode.PLAY_INFO_BUFFERING_START://500004
                    if (NetworkUtils.hasConnect(context) && !letvVodUICon.isShowLoading()) {
                        letvVodUICon.showLoadingProgress();
                    }
                    break;
                case StatusCode.PLAY_INFO_BUFFERING_END://500005
                    letvVodUICon.hideLoading();;
                    break;
                case StatusCode.PLAY_INFO_VIDEO_RENDERING_START://500006
                    letvVodUICon.showWaterMark();;
                    letvVodUICon.hideLoading();
                    break;
                default:
                    break;
            }
                break;
            case PlayerEvent.PLAY_PREPARED: {
                if (NetworkUtils.hasConnect(context) && !letvVodUICon.isShowLoading()) {
                    letvVodUICon.showLoadingProgress();
                }
                
                if (lastPosition > 0) {
                    player.seekTo(lastPosition);
                    letvVodUICon.setDuration(player.getDuration());
                    letvVodUICon.setCurrentPosition(lastPosition);
                }
                break;
            }
            case PlayerEvent.PLAY_SEEK_COMPLETE: {//209
                setLastPostion();
                isSeeking = false;
                Log.e("meng", "PlayerEvent.PLAY_SEEK_COMPLETE: " + lastPosition);
                break;
            }
            case PlayerEvent.PLAY_ERROR://205
                letvVodUICon.hideLoading();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onInterceptAdEvent(int code, Bundle bundle) {
        switch (code) {
            case PlayerEvent.AD_START:
                letvVodUICon.getView().setVisibility(GONE);
                letvVodUICon.hideLoading();
                LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                timeTextView = new TextView(context);
                timeTextView.setBackgroundColor(Color.BLACK);
                bringChildToFront(timeTextView);
                timeTextView.setPadding(20, 20, 20, 20);
                addView(timeTextView, lp);
                break;
            case IAdPlayer.AD_PLAY_ERROR:
            case PlayerEvent.AD_COMPLETE:
                removeView(timeTextView);
                letvVodUICon.getView().setVisibility(VISIBLE);
                break;
            case PlayerEvent.AD_PROGRESS:
                timeTextView.setText("广告 " + bundle.getInt(IAdPlayer.AD_TIME) + "s");
                break;

            default:
                break;
        }
        super.onInterceptAdEvent(code, bundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (timeTextView != null) {
            removeView(timeTextView);
        }
        setLastPostion();
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    public boolean isComplete() {
        //TODO
        return player != null && player.getStatus() == IPlayer.PLAYER_STATUS_EOS;
    }
    
    private void setLastPostion() {
        lastPosition = player.getCurrentPosition();
    }
}
