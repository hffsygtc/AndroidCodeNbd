package cn.com.nbd.nbdmobile.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.nbd.nbdmobile.R.style;
import cn.com.nbd.nbdmobile.adapter.FeatureDetailAdapter;
import cn.com.nbd.nbdmobile.adapter.FeatureDetailAdapter.OnNewsClickListener;
import cn.com.nbd.nbdmobile.adapter.FeatureDetailAdapter.onHotTagClickListener;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.manager.OnThemeChangeListener;
import cn.com.nbd.nbdmobile.manager.ThemeChangeManager;
import cn.com.nbd.nbdmobile.utility.ArticleJumpUtil;
import cn.com.nbd.nbdmobile.utility.Options;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;
import cn.com.nbd.nbdmobile.widget.StaticViewPager;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.nbd.article.article.ArticleType;
import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.FeatureDetail;
import com.nbd.article.bean.FeatureLable;
import com.nbd.article.bean.NewspaperDailyBean;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.FeatureDetailCallback;
import com.nbd.article.managercallback.NewspaperDailyCallback;
import com.nbd.network.bean.RequestType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

/**
 * 专题活动详情的activity
 * 
 * @author riche
 *
 */
public class FeatureDetailActivity extends Activity implements OnNewsClickListener {


	private LayoutInflater mInflater;
	private ArrayList<View> pageViews;

	private RelativeLayout mMainLayout;
	private TextView mCover;
	
	private ListView mListView;
	private ImageView mBackBtn;
	private ImageView mShareBtn;
	
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	DisplayImageOptions options;
	
	private int mFeatureId; 
	
	private FeatureDetail mFeatureDetail; //专题文章的详情
	
	private int mLabelCount; //关键字标签的个数
	private List<FeatureLable> mFeatureLables; //关键字的标签结构
	
	private FeatureDetailAdapter mAdapter;
	
	private LoadingDialog mLoadingDialog;
	private SharedPreferences mNativeShare;
	private boolean isDayTheme;
	private boolean isTextMode;
	
	private OnThemeChangeListener mThemeChangeListener;
	
	
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mAdapter == null) {
				initAdapter();
			}
			super.handleMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNativeShare = this.getSharedPreferences("NativeSetting",
				this.MODE_PRIVATE);

		isDayTheme = mNativeShare.getBoolean("theme", true);
		isTextMode = mNativeShare.getBoolean("textMode", false);
		
		if (isDayTheme) {
			setTheme(R.style.DayTheme);
		}else {
			setTheme(R.style.NightTheme);
		}
		setContentView(R.layout.activity_detail_feature);
		
		Intent i = getIntent();
		mFeatureId = i.getIntExtra("featureId", -1);
		
		mInflater = getLayoutInflater();
		options = Options.getListOptions();
		
		mLoadingDialog = new LoadingDialog(this, style.loading_dialog, "加载中...");
		
		initUi();
		initData();
		
		setListener();
		MobclickAgent.onEvent(FeatureDetailActivity.this, UmenAnalytics.DETAIL_FEATURE);
//		initAdapter();
	}

	private void setListener() {
		
		mThemeChangeListener = new OnThemeChangeListener() {
			
			@Override
			public void onNightThemeChange(boolean isNowTheme) {
				
				setThemeUi(isNowTheme);
				
			}
		};
		
		mBackBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FeatureDetailActivity.this.finish();
				
			}
		});
		
		mShareBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MobclickAgent.onEvent(FeatureDetailActivity.this, UmenAnalytics.SHARE_FEATURE);
				showShare( mFeatureDetail);
			}
		});
		
		ThemeChangeManager.getInstance().registerThemeChangeListener(mThemeChangeListener);
		
		
	}

	private void setThemeUi(boolean isNowTheme) {
		if (isNowTheme & isDayTheme) {
			
		}else {
			isDayTheme = isNowTheme;
			
			if (isDayTheme) {
				mMainLayout.setBackgroundColor(FeatureDetailActivity.this
						.getResources()
						.getColor(R.color.day_section_background));
				mCover.setBackgroundColor(FeatureDetailActivity.this
						.getResources().getColor(R.color.day_cover));

			} else {
				mMainLayout.setBackgroundColor(FeatureDetailActivity.this
						.getResources().getColor(
								R.color.night_common_background));
				mCover.setBackgroundColor(FeatureDetailActivity.this
						.getResources().getColor(R.color.night_cover));
			}
			
			
			if (mAdapter != null) {
				mAdapter.changeTheme(isDayTheme);
				mAdapter.notifyDataSetChanged();
			}
		}
		
	}

	/**
	 * 初始化数据，传递来的内容数据，图片链接集合
	 */
	private void initData() {
		if (mFeatureId != -1) {
			mLoadingDialog.showFullDialog();
			
			ArticleConfig config = new ArticleConfig();
			config.setType(RequestType.FEATURE_DETAIL);
			config.setArticleId(mFeatureId);
			
			ArticleManager.getInstence().getFeatureDetail(config, new FeatureDetailCallback() {
				
				@Override
				public void onFeatureDetailCallback(FeatureDetail detail) {
					if (mLoadingDialog != null) {
						mLoadingDialog.dismiss();
					}
					mFeatureDetail = detail;
					if (mFeatureDetail != null) {
						handler.sendEmptyMessage(0);
					}else {
						Toast.makeText(FeatureDetailActivity.this, "获取数据失败...", Toast.LENGTH_SHORT).show();
					}
				}
			});
			
		}
		
	}

	

	/**
	 * 初始化适配器 界面数据显示
	 */
	private void initAdapter() {
		
		mAdapter = new FeatureDetailAdapter(FeatureDetailActivity.this, mFeatureDetail,isDayTheme,isTextMode);

		mListView.setAdapter(mAdapter);
		
		mAdapter.setTagClickListener( new onHotTagClickListener() {
			
			@Override
			public void onTagClickPosition(int position, int tagPosition) {
				
				Log.e("TAG-CILCI","=="+position+"=="+tagPosition);
				mListView.setSelectionFromTop(position, 0);
				
			}
		});
		
		mAdapter.setNewsClickListener(this);

	}

	/**
	 * 初始化界面控件
	 */
	private void initUi() {
		mMainLayout = (RelativeLayout) findViewById(R.id.feature_detail_main_layout);
		mCover = (TextView) findViewById(R.id.feature_detail_cover);
		mBackBtn = (ImageView) findViewById(R.id.feature_detail_back_btn);
		mShareBtn = (ImageView) findViewById(R.id.feature_detail_share);
		mListView = (ListView) findViewById(R.id.feature_detail_mListView);
		
		
	}

	@Override
	public void onNewsClick(View view, ArticleInfo article) {
		
		ArticleJumpUtil.jumpToArticleDetal(FeatureDetailActivity.this, article, "专题详情",false);
		
	}

	
	/**
	 * 分享的文章详情
	 * @param shArticleInfo
	 */
	private void showShare(FeatureDetail detail) {

		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();

		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(detail.getTitle());
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(detail.getShare_url());
		// text是分享文本，所有平台都需要这个字段
		oks.setText(detail.getLead());
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		if (detail.getAvatar() == null || detail.getAvatar().equals("")) {
			oks.setImageUrl("http://static.nbd.com.cn/images/nbd_icon.png");
		}else {
			oks.setImageUrl(detail.getAvatar());
		}
		// oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl(detail.getShare_url());
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl(detail.getShare_url());
		
//		oks.setPlatform(QQ.NAME);
		
		// 启动分享GUI
		oks.show(this);
	}

	@Override
	protected void onDestroy() {
		ThemeChangeManager.getInstance().unregisterThemeChangeListener(mThemeChangeListener);
		super.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}


}
