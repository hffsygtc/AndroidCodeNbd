package cn.com.nbd.nbdmobile.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import u.aly.br;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.showview.MixNewsHolder;
import cn.com.nbd.nbdmobile.utility.SizeUtils;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.widget.FullScreenVideoDialog;

import com.lecloud.sdk.constant.PlayerEvent;
import com.lecloud.sdk.constant.PlayerParams;
import com.lecloud.sdk.videoview.VideoViewListener;
import com.lecloud.skin.videoview.vod.UIVodVideoView;
import com.nbd.article.article.ArticleType;
import com.nbd.article.article.MixArticleType;
import com.nbd.article.bean.ArticleDisplayMode;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;

/**
 * 处理多样化新闻项显示的适配器
 * 
 * 可派生带切换栏的，也可派生不带切换栏的
 * 
 * @author he
 * 
 */
public class InfosAdapter extends
		NbdHolderAdapter<ArticleInfo, ArticleInfo, MixNewsHolder> {

	private final static String TAG = "MixNewsAdapter";

	protected boolean isDay; // 是否是日间模式

	protected boolean isText; // 是否是文字模式

	protected String defaultImage = ""; // 文字模式下的默认图片

	protected int mBigPicWidth; // 显示广告等自适应宽高大小的图片宽度

	protected List<View> pageViews; // 顶部图片适配栏的图片视图

	protected List<String> mPagerText = new ArrayList<String>(); // 顶部图片视图显示的文字

	protected PagerAdapter mHeadPageAdapter; // 顶部的图片pager适配器

	protected List<ArticleInfo> mHeadDatas = new ArrayList<ArticleInfo>(); // 轮播图文章列表

	protected Map<MixNewsHolder, String> imgMap = new HashMap<MixNewsHolder, String>(); // 用于判断是否需要刷新图片的判断标志

	protected boolean isToggleClickNotify = false;

	protected UIVodVideoView mVideoView; // 视频的播放视图
	protected int mVideoPlayPosition = -1; // 视频播放的位置
	private VideoViewListener mVideoViewListener; // 视频播放视图的监听
	private Map<Integer, MixNewsHolder> videoHolderMap = new HashMap<Integer, MixNewsHolder>(); // 存储视频播放的位置的Holder的数据集

	private final static int NORMAL = 0;
	private final static int LARGE_PIC = 1;
	private final static int VIDEO = 2;
	private final static int THREE_PIC = 3;

	// 新闻点击传递新闻数据的接口
	public static interface OnNewsClickListener {
		public void onNewsClick(View view, ArticleInfo article); // 传递数据给activity
	}

	public OnNewsClickListener onNewsClick;

	public void setNewsClickListener(OnNewsClickListener onNewsClick) {
		this.onNewsClick = onNewsClick;

	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param listData
	 *            新闻列表数据
	 * @param headData
	 *            头部轮播列表数据
	 * @param isDayTheme
	 *            日间模式
	 * @param isTextMode
	 *            文字模式
	 */
	public InfosAdapter(Context context, List<ArticleInfo> listData,
			List<ArticleInfo> headData, boolean isDayTheme, boolean isTextMode) {
		super(context, listData, headData);

		this.isDay = isDayTheme;
		this.isText = isTextMode;
		this.mHeadDatas = headData;

		mBigPicWidth = SizeUtils.dp2px(context, 340);

		init();
	}

	/**
	 * 初始化
	 */
	private void init() {

		mHeadPageAdapter = new PagerAdapter() {

			// 断是否由对象生成界面
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0 == arg1;
			}

			// 获取当前窗体界面数
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return pageViews.size();
			}

			// 是从ViewGroup中移出当前View
			public void destroyItem(View arg0, int arg1, Object arg2) {
				((ViewPager) arg0).removeView(pageViews.get(arg1));
			}

			// 返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
			public Object instantiateItem(View arg0, final int arg1) {
				((ViewPager) arg0).removeView(pageViews.get(arg1));
				((ViewPager) arg0).addView(pageViews.get(arg1));
				pageViews.get(arg1).setOnClickListener(
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								onNewsClick.onNewsClick(pageViews.get(arg1),
										mHeadDatas.get(arg1));
							}
						});
				return pageViews.get(arg1);
			}
		};

		mVideoViewListener = new VideoViewListener() {

			@Override
			public void onStateResult(int event, Bundle bundle) {
				handlePlayerEvent(event, bundle);// 处理播放器事件
			}
		};

		mVideoView = new UIVodVideoView(context, false);
		mVideoView.setVideoViewListener(mVideoViewListener);

	}

	@Override
	public View buildConverView(LayoutInflater layoutInflater, int position) {
		return inflate(R.layout.news_item_mix_infos);
	}

	@Override
	public MixNewsHolder buildHolder(View view) {
		MixNewsHolder mHolder = new MixNewsHolder();
		mHolder.mHolderLayout = (LinearLayout) view
				.findViewById(R.id.item_layout);

		mHolder.viewpagerLayout = (RelativeLayout) view
				.findViewById(R.id.viewpaget_layout);
		mHolder.pager = (ViewPager) view.findViewById(R.id.viewpager);
		mHolder.pagerText = (TextView) view
				.findViewById(R.id.scroll_bottom_txt);
		mHolder.pagerBottomLayout = (RelativeLayout) view
				.findViewById(R.id.scroll_bottom_layout);

		mHolder.leftPoint = (ImageView) view
				.findViewById(R.id.news_pager_point_left);
		mHolder.midPoint = (ImageView) view
				.findViewById(R.id.news_pager_point_mid);
		mHolder.rightPoint = (ImageView) view
				.findViewById(R.id.news_pager_point_right);
		mHolder.pagerTagImg = (ImageView) view
				.findViewById(R.id.news_pager_img_tag);

		mHolder.newsLayout = (RelativeLayout) view
				.findViewById(R.id.news_layout);
		// 一般新闻布局方式
		mHolder.normalLayout = (RelativeLayout) view
				.findViewById(R.id.normal_article_layout);
		mHolder.normalImage = (ImageView) view
				.findViewById(R.id.normal_article_img);
		mHolder.normalContentTxt = (TextView) view
				.findViewById(R.id.normal_article_content);
		mHolder.normalCommonTxt = (TextView) view
				.findViewById(R.id.normal_article_commonnum);
		mHolder.normalTagImg = (ImageView) view
				.findViewById(R.id.normal_article_tag_img);
		mHolder.ori_source = (TextView) view
				.findViewById(R.id.normal_article_ori_source);

		// 三张图片新闻布局方式
		mHolder.threePicLayout = (RelativeLayout) view
				.findViewById(R.id.threepic_article_layout);
		mHolder.threePicTitle = (TextView) view
				.findViewById(R.id.threepic_article_title);
		mHolder.threePicLeft = (ImageView) view
				.findViewById(R.id.threepic_left);
		mHolder.threePicMiddle = (ImageView) view
				.findViewById(R.id.threepic_middle);
		mHolder.threePicRight = (ImageView) view
				.findViewById(R.id.threepic_right);
		mHolder.threePicTag = (ImageView) view
				.findViewById(R.id.threepic_article_tag_img);
		mHolder.threePicCommonTxt = (TextView) view
				.findViewById(R.id.threepic_article_commonnum);
		mHolder.threePicOriTxt = (TextView) view
				.findViewById(R.id.threepic_article_ori_text);

		mHolder.largeImgLayout = (RelativeLayout) view
				.findViewById(R.id.large_img_article_layout);
		mHolder.largeImageLayout = (RelativeLayout) view
				.findViewById(R.id.large_img_image_layout);
		mHolder.largeImgImage = (ImageView) view
				.findViewById(R.id.large_img_image);
		mHolder.largeImgNum = (TextView) view
				.findViewById(R.id.large_img_num_text);
		mHolder.largeImgTag = (ImageView) view
				.findViewById(R.id.large_img_article_tag_img);
		mHolder.bookImageTag = (ImageView) view
				.findViewById(R.id.large_img_article_tag_book);
		mHolder.largeImgReadNum = (TextView) view
				.findViewById(R.id.large_img_article_commonnum);

		mHolder.largeImgTitleTxt = (TextView) view
				.findViewById(R.id.large_img_article_title);
		mHolder.largeImgAdvTitleLayout = (RelativeLayout) view.findViewById(R.id.large_img_adv_title_layout);
		mHolder.largeImgAdvTitle = (TextView) view.findViewById(R.id.large_img_adv_title);
		mHolder.largeImgBottomLayout = (RelativeLayout) view.findViewById(R.id.large_img_bottom_layout);
		

		mHolder.videoLayout = (RelativeLayout) view
				.findViewById(R.id.video_article_layout);
		mHolder.videoTitle = (TextView) view
				.findViewById(R.id.video_article_title);
		mHolder.videoAdvTitleLayout = (RelativeLayout) view.findViewById(R.id.video_article_adv_title_layout);
		mHolder.videoAdvTtitle = (TextView) view.findViewById(R.id.video_article_adv_title);
		mHolder.videoImageLayout = (RelativeLayout) view
				.findViewById(R.id.video_image_layout);
		mHolder.videoPlayLayout = (RelativeLayout) view
				.findViewById(R.id.video_play_layout);
		mHolder.videoImage = (ImageView) view
				.findViewById(R.id.video_article_image);
		mHolder.videoPlayIcon = (ImageView) view
				.findViewById(R.id.video_article_play_img);
		mHolder.videoCover = (ImageView) view
				.findViewById(R.id.video_article_image_cover);
		mHolder.videoBottomLayout = (RelativeLayout) view.findViewById(R.id.video_article_bottom_layout);
		mHolder.videoTag = (ImageView) view
				.findViewById(R.id.video_article_tag_img);
		mHolder.videoOriText = (TextView) view
				.findViewById(R.id.video_article_ori_text);
		mHolder.videoBtmPlay = (ImageView) view
				.findViewById(R.id.video_article_video_icon);
		mHolder.videoPlayNum = (TextView) view
				.findViewById(R.id.video_article_playnum_text);
		mHolder.videoBtmShare = (ImageView) view
				.findViewById(R.id.video_article_share_icon);

		// 头部切换栏
		mHolder.layout_list_section = (LinearLayout) view
				.findViewById(R.id.gs_layout_list_section);
		mHolder.section_left_layout = (RelativeLayout) view
				.findViewById(R.id.left_section_layout);
		// 头部左边的switch选项
		mHolder.section_left = (TextView) view
				.findViewById(R.id.left_section_text);
		mHolder.section_left_bottom = (TextView) view
				.findViewById(R.id.left_section_choose);
		mHolder.section_right_layout = (RelativeLayout) view
				.findViewById(R.id.right_section_layout);
		// 头部右边的switch选项
		mHolder.section_right = (TextView) view
				.findViewById(R.id.right_section_text);
		mHolder.section_right_bottom = (TextView) view
				.findViewById(R.id.right_section_choose);

		view.setTag(mHolder);

		return mHolder;
	}

	@Override
	public void buildViewDatas(MixNewsHolder holder, ArticleInfo t, int position) {
		holder.viewpagerLayout.setVisibility(View.GONE);
		holder.newsLayout.setVisibility(View.VISIBLE);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void buildHeadViewpage(final MixNewsHolder holder,
			final List<ArticleInfo> headListData) {
		holder.viewpagerLayout.setVisibility(View.VISIBLE);
		holder.newsLayout.setVisibility(View.GONE);
		holder.layout_list_section.setVisibility(View.GONE);

		if (!isToggleClickNotify) {
			pageViews = new ArrayList<View>();

			// 测试的布局加载
			for (int i = 0; i < 3; i++) {
				View scrollView = layoutInflater.inflate(
						R.layout.scroll_page_layout, null);
				ImageView img = (ImageView) scrollView
						.findViewById(R.id.scroll_image);
				ArticleInfo in = null;
				if (headListData != null && headListData.size() > 2) {
					in = headListData.get(i);
					if (isText) {
						imageLoader.displayImage(defaultImage, img, options);
					} else {
						imageLoader.displayImage(in.getImage(), img, options);
					}
					if (in.getTitle() != null) {
						mPagerText.add(i, in.getTitle());
					}
				}

				pageViews.add(scrollView);
			}

			// 滑动图片的时候，更换图片的红点定位和标题文字
			holder.pager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					if (headListData != null && headListData.size() > 0
							&& arg0 < headListData.size()) {
						holder.pagerText.setText(headListData.get(arg0)
								.getTitle());
						if (headListData.get(arg0).getType()
								.equals(ArticleType.IMAGE)) {
							holder.pagerTagImg.setVisibility(View.VISIBLE);
						} else {
							holder.pagerTagImg.setVisibility(View.GONE);
						}
						showPagerTagPoint(holder, arg0);
					}
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});
			holder.pager.setAdapter(mHeadPageAdapter);

			if (headListData != null && headListData.size() > 0) {
				holder.pagerText.setText(headListData.get(0).getTitle());
				holder.pager.setCurrentItem(0);
				if (headListData.get(0).getType().equals(ArticleType.IMAGE)) {
					holder.pagerTagImg.setVisibility(View.VISIBLE);
				} else {
					holder.pagerTagImg.setVisibility(View.GONE);
				}
				showPagerTagPoint(holder, 0);
			}
			isToggleClickNotify = false;
		} else {
			isToggleClickNotify = false;
		}
	}

	public void showViewByType(MixNewsHolder mHolder, ArticleInfo info,
			int position) {

		if (info.getList_show_control() != null) {
			Log.e("SHOW VIEW-->", "position-"+position+"-new type-->"+info.getList_show_control().getDisplay_form());
			switch (info.getList_show_control().getDisplay_form()) {
			case MixArticleType.Library: // 图书馆
			case MixArticleType.Gallary: // 图集
			case MixArticleType.Feature: // 专题
			case MixArticleType.Adv_ImgL: // 大图广告
				showItemLayout(mHolder, LARGE_PIC);
				showLargeImageNews(mHolder, info, info.getList_show_control());
				break;
			case MixArticleType.ThreePic:
				showItemLayout(mHolder, THREE_PIC);
				showThreeImageNews(mHolder, info, info.getList_show_control());
				break;
			case MixArticleType.Video:
			case MixArticleType.Adv_Video:
				showItemLayout(mHolder, VIDEO);
				showVideoNews(mHolder, info, position,
						info.getList_show_control());
				break;
			case MixArticleType.Normal: // 普通文章单图小
			case MixArticleType.Live:// 直播文章
			case MixArticleType.Adv_ImgS: // 广告单图小
			case MixArticleType.Market: // 推广软文文章
			case MixArticleType.VideoPage:// 内嵌视频的文章
			default:
				showItemLayout(mHolder, NORMAL);
				showNormalNews(mHolder, info, info.getList_show_control());
				break;
			}

		} else {
			// 显示大图的文章形式
			Log.e("SHOW VIEW-->", "position-"+position+"-old type-->"+info.getType());
			if (info.getType().equals(ArticleType.AD_LG)
					|| info.getType().equals(ArticleType.AD_MD)
					|| info.getType().equals(ArticleType.BOOK)
					|| info.getType().equals(ArticleType.FEATURE)) {
				showItemLayout(mHolder, LARGE_PIC);
				showLargeImageNews(mHolder, info, null);
				// 三张图片的新闻 待定类型
			} else if (info.getType().equals(ArticleType.IMAGE)) {
				showItemLayout(mHolder, THREE_PIC);
				showThreeImageNews(mHolder, info, null);
				// 视频类型的新闻形式
			} else if (info.getType().equals(ArticleType.VIDEO)) {
				showItemLayout(mHolder, VIDEO);
				showVideoNews(mHolder, info, position, null);
				// 普通类型的新闻形式
			} else {
				showItemLayout(mHolder, NORMAL);
				showNormalNews(mHolder, info, null);
			}
		}
	}

	/**
	 * 显示对应文章类型的布局块
	 * 
	 * @param mHolder
	 * @param type
	 */
	private void showItemLayout(MixNewsHolder mHolder, int type) {

		mHolder.normalLayout.setVisibility(View.GONE);
		mHolder.threePicLayout.setVisibility(View.GONE);
		mHolder.largeImgLayout.setVisibility(View.GONE);
		mHolder.videoLayout.setVisibility(View.GONE);
		switch (type) {
		case NORMAL:
			mHolder.normalLayout.setVisibility(View.VISIBLE);
			break;
		case THREE_PIC:
			mHolder.threePicLayout.setVisibility(View.VISIBLE);
			break;
		case LARGE_PIC:
			mHolder.largeImgLayout.setVisibility(View.VISIBLE);
			break;
		case VIDEO:
			mHolder.videoLayout.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}

	}

	/**
	 * 普通文章类型，默认情况下的类型
	 * 
	 * @param mHolder
	 * @param info
	 * @param articleDisplayMode
	 */
	private void showNormalNews(MixNewsHolder mHolder, ArticleInfo info,
			ArticleDisplayMode displayMode) {

		String imgSrc = "";

		if (displayMode != null) { // 新版本处理逻辑
			switch (displayMode.getDisplay_form()) {
			case MixArticleType.Adv_ImgS:
				mHolder.normalTagImg.setVisibility(View.VISIBLE);
				mHolder.normalTagImg
						.setImageResource(R.drawable.article_tag_broad);
				break;
			case MixArticleType.Market:
				mHolder.normalTagImg.setVisibility(View.VISIBLE);
				mHolder.normalTagImg
						.setImageResource(R.drawable.article_tag_broad);
				break;
			case MixArticleType.Live:
				mHolder.normalTagImg.setVisibility(View.VISIBLE);
				mHolder.normalTagImg
						.setImageResource(R.drawable.article_tag_live);
				break;
			default:
				mHolder.normalTagImg.setVisibility(View.GONE);
				break;
			}
		} else { // 老版本处理逻辑
			if (info.getType().equals(ArticleType.LIVE)) { // 直播类型的图标
				mHolder.normalTagImg
						.setImageResource(R.drawable.article_tag_live);
			} else if (info.getType().equals(ArticleType.MARKET)) { // 推广类型的图标
				mHolder.normalTagImg
						.setImageResource(R.drawable.article_tag_broad);
			} else {
				mHolder.normalTagImg.setVisibility(View.GONE);
			}
		}

		if (displayMode != null) {
			imgSrc = displayMode.getImageOne();
		}

		imgSrc = imgSrc.equals("") ? info.getImage() : imgSrc;

		// 图片的显示
		if (isText) {
			imageLoader
					.displayImage(defaultImage, mHolder.normalImage, options);
		} else {
			imageLoader.displayImage(imgSrc, mHolder.normalImage, options);
		}

		// 主体内容的显示
		mHolder.normalContentTxt.setText(info.getTitle());
		mHolder.ori_source.setText(info.getOri_source());
		mHolder.normalCommonTxt.setText(info.getMobile_click_count() + " 阅读");

	}

	/**
	 * 视频类型的文章形式（普通视频，推广广告）
	 * 
	 * @param mHolder
	 * @param info
	 * @param articleDisplayMode
	 */
	private void showVideoNews(final MixNewsHolder mHolder,
			final ArticleInfo info, final int position,
			ArticleDisplayMode displayMode) {

		String coverSrc = "";

		// 新版本有新闻样式字段
		if (displayMode != null) {
			if (displayMode.getDisplay_form() == MixArticleType.Adv_Video) {
				mHolder.videoTitle.setVisibility(View.GONE);
				mHolder.videoAdvTitleLayout.setVisibility(View.VISIBLE);
				mHolder.videoBottomLayout.setVisibility(View.GONE);
				
			} else {
				mHolder.videoTitle.setVisibility(View.VISIBLE);
				mHolder.videoAdvTitleLayout.setVisibility(View.GONE);
				mHolder.videoBottomLayout.setVisibility(View.VISIBLE);

				mHolder.videoPlayNum.setText(info.getMobile_click_count() + "");
			}
		} else { // 老版本逻辑按TYPE处理
			if (info.getType().equals(ArticleType.VIDEO)) {
				mHolder.videoTitle.setVisibility(View.VISIBLE);
				mHolder.videoAdvTitleLayout.setVisibility(View.GONE);
				mHolder.videoBottomLayout.setVisibility(View.VISIBLE);

				mHolder.videoPlayNum.setText(info.getMobile_click_count() + "");
			} else {
				mHolder.videoTitle.setVisibility(View.GONE);
				mHolder.videoAdvTitleLayout.setVisibility(View.VISIBLE);
				mHolder.videoBottomLayout.setVisibility(View.GONE);
			}
		}

		mHolder.videoOriText.setText(info.getOri_source());

		if (position == mVideoPlayPosition) {
			mHolder.videoPlayLayout.setVisibility(View.VISIBLE);
			
		} else {
			mHolder.videoPlayLayout.setVisibility(View.GONE);
		}

		mHolder.videoTitle.setText(info.getTitle());
		mHolder.videoAdvTtitle.setText(info.getTitle());

		if (displayMode != null) {
			coverSrc = displayMode.getImageOne();
		}

		coverSrc = coverSrc.equals("") ? info.getImage() : coverSrc;

		if (isText) {
			imageLoader.displayImage(defaultImage, mHolder.videoImage, options);
		} else {
			imageLoader.displayImage(coverSrc, mHolder.videoImage, options);
		}

		// 视频播放按钮点击事件
		mHolder.videoPlayIcon.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mVideoPlayPosition == -1) { // 初始化默认状态，还未有视频播放

					mHolder.videoPlayLayout.setVisibility(View.VISIBLE);

					RelativeLayout videoContainer = mHolder.videoPlayLayout;

					ViewGroup parent = (ViewGroup) mVideoView.getParent();
					if (parent != null) {
						parent.removeAllViews();
					}

					videoContainer.addView((View) mVideoView,
							computeContainerSize(context, 16, 9));

					Log.e(TAG, "video show ID-->" + info.getVideo_id());
					Bundle mBundle = setVodParams("hqrinotu0v",
							info.getVideo_id(), "",
							"297d2469f53cd4727909656aba345ab9", "", false);
					mVideoView.setDataSource(mBundle);

					// 存储正在播放位置的HOLDER
					mVideoPlayPosition = position;
					videoHolderMap.put(position, mHolder);

				} else if (mVideoPlayPosition != position) { // 当前点击播放的位置和正在播放的位置不一样
					// TODO deal stop and release..

					mVideoView.stopAndRelease();
					ViewGroup parent = (ViewGroup) mVideoView.getParent();
					if (parent != null) {
						parent.removeAllViews();
					}

					MixNewsHolder playHolder = videoHolderMap
							.get(mVideoPlayPosition);

					if (playHolder != mHolder) {
						playHolder.videoPlayLayout.setVisibility(View.GONE);
						mHolder.videoPlayLayout.setVisibility(View.VISIBLE);

					}

					RelativeLayout videoContainer = mHolder.videoPlayLayout;
					videoContainer.addView((View) mVideoView,
							computeContainerSize(context, 16, 9));

					Bundle mBundle = setVodParams("hqrinotu0v",
							info.getVideo_id(), "",
							"297d2469f53cd4727909656aba345ab9", "", false);
					mVideoView.setDataSource(mBundle);

					// 存储正在播放位置的HOLDER
					mVideoPlayPosition = position;
					videoHolderMap.put(position, mHolder);

				}

			}
		});
		
		mHolder.videoBtmShare.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (onNewsClick != null && info != null) {
					onNewsClick.onNewsClick(null, info);
				}
			}
		});

	}

	/**
	 * 显示三张图片类型的新闻模式 （普通文章类型，内部含有较多图片是这样显示）
	 * 
	 * @param mHolder
	 * @param info
	 * @param articleDisplayMode
	 */
	private void showThreeImageNews(MixNewsHolder mHolder, ArticleInfo info,
			ArticleDisplayMode displayMode) {

		mHolder.threePicTag.setVisibility(View.GONE);
		// mHolder.videoTag.setImageResource(R.drawable.article_tag_picture);

		mHolder.threePicTitle.setText(info.getTitle());
		mHolder.threePicCommonTxt.setText(info.getMobile_click_count() + " 阅读");
		mHolder.threePicOriTxt.setText(info.getOri_source());

		String leftSrc = "";
		String midSrc = "";
		String rightSrc = "";

		if (displayMode != null) {
			leftSrc = displayMode.getImageOne();
			midSrc = displayMode.getImageTwo();
			rightSrc = displayMode.getImageThree();
		}

		if (info.getGallery() != null) {
			List<ArticleImages> mArticleImages = info.getGallery().getImages();

			ArticleImages leftImg = null;
			ArticleImages midImg = null;
			ArticleImages rightImg = null;
			if (mArticleImages != null) {
				for (int i = 0; i < mArticleImages.size(); i++) {
					switch (i) {
					case 0:
						leftImg = mArticleImages.get(0);
						leftSrc = leftSrc.equals("") ? leftImg.getImage_src()
								: defaultImage;
						break;
					case 1:
						midImg = mArticleImages.get(1);
						midSrc = midSrc.equals("") ? midImg.getImage_src()
								: defaultImage;
						break;
					case 2:
						rightImg = mArticleImages.get(2);
						rightSrc = rightSrc.equals("") ? rightImg
								.getImage_src() : defaultImage;
						break;
					default:
						break;
					}
				}
			}

		} else {
			Log.e(TAG, "Gallery is Null");
		}

		if (isText) {
			imageLoader.displayImage(defaultImage, mHolder.threePicLeft,
					options);
			imageLoader.displayImage(defaultImage, mHolder.threePicMiddle,
					options);
			imageLoader.displayImage(defaultImage, mHolder.threePicRight,
					options);

		} else {
			imageLoader.displayImage(leftSrc, mHolder.threePicLeft, options);
			imageLoader.displayImage(midSrc, mHolder.threePicMiddle, options);
			imageLoader.displayImage(rightSrc, mHolder.threePicRight, options);
		}
	}

	/**
	 * 显示大图的类型新闻
	 * 
	 * 专题、图书馆、图集（3：4） 大图广告（自适应）
	 * 
	 * @param mHolder
	 * @param info
	 * @param articleDisplayMode
	 */
	private void showLargeImageNews(MixNewsHolder mHolder, ArticleInfo info,
			ArticleDisplayMode displayMode) {
		String imageSrc = "";

		if (info != null) {
			mHolder.largeImgTitleTxt.setText(info.getTitle());

			// 标题栏的
			RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mHolder.largeImgTitleTxt.setLayoutParams(titleParams);
			mHolder.largeImgTitleTxt.setId(0);
			
			RelativeLayout.LayoutParams advTitleParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			advTitleParams.addRule(RelativeLayout.BELOW, 0);
			mHolder.largeImgAdvTitleLayout.setLayoutParams(advTitleParams);
			mHolder.largeImgAdvTitleLayout.setId(1);

			float imgHeight = 0f;
			if (displayMode != null) {
				if (displayMode.getDisplay_form() == MixArticleType.Adv_ImgL) {
					int width = Integer.parseInt(info.getWidth()); // 接口返回的宽度
					int height = Integer.parseInt(info.getHeight()); // 接口返回的高度
					float scale = Float.parseFloat(mBigPicWidth + "")
							/ Float.parseFloat(width + "");
					imgHeight = scale * Float.parseFloat(height + "");
				} else {
					imgHeight = mBigPicWidth / 16 * 9;
				}
			} else {
				if (info.getType().equals(ArticleType.AD_LG)
						|| info.getType().equals(ArticleType.AD_MD)) {
					int width = Integer.parseInt(info.getWidth()); // 接口返回的宽度
					int height = Integer.parseInt(info.getHeight()); // 接口返回的高度
					float scale = Float.parseFloat(mBigPicWidth + "")
							/ Float.parseFloat(width + "");
					imgHeight = scale * Float.parseFloat(height + "");
				} else {
					imgHeight = mBigPicWidth / 16 * 9;
				}
			}

			int imageHeight = Math.round(imgHeight);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					mBigPicWidth, imageHeight);
			layoutParams.addRule(RelativeLayout.BELOW, 1);
			layoutParams.topMargin = SizeUtils.dp2px(context, 6);

			mHolder.largeImageLayout.setLayoutParams(layoutParams);
			mHolder.largeImgNum.setVisibility(View.GONE);

			if (displayMode != null) {
				imageSrc = displayMode.getImageOne();
				switch (displayMode.getDisplay_form()) {
				case MixArticleType.Library:
					mHolder.largeImgTitleTxt.setVisibility(View.VISIBLE);
					mHolder.largeImgBottomLayout.setVisibility(View.VISIBLE);
					mHolder.largeImgAdvTitleLayout.setVisibility(View.GONE);
					mHolder.largeImgTag.setVisibility(View.GONE);
					mHolder.largeImgReadNum.setVisibility(View.VISIBLE);
					mHolder.bookImageTag.setVisibility(View.VISIBLE);
					break;
				case MixArticleType.Gallary:
					mHolder.largeImgTitleTxt.setVisibility(View.VISIBLE);
					mHolder.largeImgBottomLayout.setVisibility(View.VISIBLE);
					mHolder.largeImgAdvTitleLayout.setVisibility(View.GONE);
					mHolder.largeImgTag.setVisibility(View.VISIBLE);
					mHolder.bookImageTag.setVisibility(View.GONE);
					mHolder.largeImgTag
							.setImageResource(R.drawable.article_tag_picture);
					mHolder.largeImgNum.setVisibility(View.VISIBLE);
					mHolder.largeImgReadNum.setVisibility(View.VISIBLE);
					if (info.getImages() != null) {
						mHolder.largeImgNum.setText(info.getImages().size()
								+ "图");
					}
					break;
				case MixArticleType.Feature:
					mHolder.largeImgTitleTxt.setVisibility(View.VISIBLE);
					mHolder.largeImgBottomLayout.setVisibility(View.VISIBLE);
					mHolder.largeImgAdvTitleLayout.setVisibility(View.GONE);
					mHolder.largeImgTag.setVisibility(View.VISIBLE);
					mHolder.bookImageTag.setVisibility(View.GONE);
					mHolder.largeImgReadNum.setVisibility(View.VISIBLE);
					mHolder.largeImgTag
							.setImageResource(R.drawable.article_tag_market);
					break;
				case MixArticleType.Adv_ImgL:
					mHolder.largeImgTitleTxt.setVisibility(View.GONE);
					mHolder.largeImgBottomLayout.setVisibility(View.GONE);
					mHolder.largeImgAdvTitleLayout.setVisibility(View.VISIBLE);
					mHolder.largeImgTag.setVisibility(View.VISIBLE);
					mHolder.bookImageTag.setVisibility(View.GONE);
					mHolder.largeImgReadNum.setVisibility(View.GONE);
					mHolder.largeImgTag
							.setImageResource(R.drawable.article_tag_broad);
					break;
				default:
					mHolder.largeImgTitleTxt.setVisibility(View.VISIBLE);
					mHolder.largeImgBottomLayout.setVisibility(View.VISIBLE);
					mHolder.largeImgAdvTitleLayout.setVisibility(View.GONE);
					mHolder.largeImgTag.setVisibility(View.GONE);
					mHolder.bookImageTag.setVisibility(View.GONE);
					mHolder.largeImgNum.setVisibility(View.GONE);
					mHolder.largeImgReadNum.setVisibility(View.VISIBLE);
					break;
				}
			} else {
				imageSrc = info.getImage();
				if (info.getType().equals(ArticleType.BOOK)) { // 图书馆类型
					mHolder.largeImgTitleTxt.setVisibility(View.VISIBLE);
					mHolder.largeImgBottomLayout.setVisibility(View.VISIBLE);
					mHolder.largeImgAdvTitleLayout.setVisibility(View.GONE);
					mHolder.largeImgTag.setVisibility(View.GONE);
					mHolder.bookImageTag.setVisibility(View.VISIBLE);
					mHolder.largeImgReadNum.setVisibility(View.VISIBLE);
				} else {
					mHolder.largeImgTag.setVisibility(View.VISIBLE);
					mHolder.bookImageTag.setVisibility(View.GONE);
					
					if (info.getType().equals(ArticleType.FEATURE)) { // 专题类型大图
						mHolder.largeImgTitleTxt.setVisibility(View.VISIBLE);
						mHolder.largeImgBottomLayout.setVisibility(View.VISIBLE);
						mHolder.largeImgAdvTitleLayout.setVisibility(View.GONE);
						mHolder.largeImgTag
								.setImageResource(R.drawable.article_tag_market);
						mHolder.largeImgReadNum.setVisibility(View.VISIBLE);
					} else if (info.getType().equals(ArticleType.AD_LG)
							|| info.getType().equals(ArticleType.AD_MD)) { // 广告类型大图
						mHolder.largeImgTitleTxt.setVisibility(View.GONE);
						mHolder.largeImgBottomLayout.setVisibility(View.GONE);
						mHolder.largeImgAdvTitleLayout.setVisibility(View.VISIBLE);
						mHolder.largeImgReadNum.setVisibility(View.GONE);
						mHolder.largeImgTag
								.setImageResource(R.drawable.article_tag_broad);
					} else { // 普通类型大图
						mHolder.largeImgTitleTxt.setVisibility(View.VISIBLE);
						mHolder.largeImgBottomLayout.setVisibility(View.VISIBLE);
						mHolder.largeImgAdvTitleLayout.setVisibility(View.GONE);
						mHolder.largeImgTag
								.setImageResource(R.drawable.article_tag_picture);
						mHolder.largeImgNum.setVisibility(View.VISIBLE);
						mHolder.largeImgReadNum.setVisibility(View.VISIBLE);
					}
				}
			}
			
			mHolder.largeImgReadNum.setText(info.getMobile_click_count()+"阅读");

			// 文字模式的显示切换
			if (isText) {
				imageLoader.displayImage(defaultImage, mHolder.largeImgImage,
						options);
			} else {
				imageLoader.displayImage(imageSrc, mHolder.largeImgImage,
						options);
			}
		}
	}

	/**
	 * 显示头图滑动时小红点的切换方法
	 * 
	 * @param mHolder
	 * @param arg0
	 */
	protected void showPagerTagPoint(MixNewsHolder mHolder, int arg0) {
		mHolder.leftPoint.setBackgroundResource(R.drawable.circle_shape);
		mHolder.midPoint.setBackgroundResource(R.drawable.circle_shape);
		mHolder.rightPoint.setBackgroundResource(R.drawable.circle_shape);
		switch (arg0) {
		case 0:
			mHolder.leftPoint
					.setBackgroundResource(R.drawable.circle_shape_hightlight);
			break;
		case 1:
			mHolder.midPoint
					.setBackgroundResource(R.drawable.circle_shape_hightlight);
			break;
		case 2:
			mHolder.rightPoint
					.setBackgroundResource(R.drawable.circle_shape_hightlight);
			break;

		default:
			break;
		}
	}

	/**
	 * 根据日间模式或者夜间模式切换ITEM的显示方式
	 * 
	 * @param mHolder
	 * @param isDayTheme2
	 */
	public void setThemeColor(MixNewsHolder mHolder, boolean isDayTheme) {

		if (isDayTheme) {
			ThemeUtil.setBackgroundDay((Activity) context,
					mHolder.mHolderLayout);
			ThemeUtil.setBackgroundClickDay((Activity) context,
					mHolder.newsLayout);
			ThemeUtil.setTextColorDay((Activity) context,
					mHolder.normalContentTxt);
			ThemeUtil
					.setTextColorDay((Activity) context, mHolder.threePicTitle);
			ThemeUtil.setTextColorDay((Activity) context,
					mHolder.largeImgTitleTxt);
			ThemeUtil.setTextColorDay((Activity) context,
					mHolder.largeImgAdvTitle);
			ThemeUtil.setTextColorDay((Activity) context,
					mHolder.videoAdvTtitle);
			ThemeUtil.setTextColorDay((Activity) context,
					mHolder.videoTitle);
			
			if (mHolder.layout_list_section != null) {
				ThemeUtil.setSectionBackgroundDay((Activity) context,
						mHolder.layout_list_section);
			}

		} else {
			ThemeUtil.setBackgroundClickNight((Activity) context,
					mHolder.mHolderLayout);
			ThemeUtil.setBackgroundClickNight((Activity) context,
					mHolder.newsLayout);
			ThemeUtil.setTextColorNight((Activity) context,
					mHolder.normalContentTxt);
			ThemeUtil.setTextColorNight((Activity) context,
					mHolder.threePicTitle);
			ThemeUtil.setTextColorNight((Activity) context,
					mHolder.largeImgTitleTxt);
			ThemeUtil.setTextColorNight((Activity) context,
					mHolder.largeImgAdvTitle);
			ThemeUtil.setTextColorNight((Activity) context,
					mHolder.videoTitle);
			ThemeUtil.setTextColorNight((Activity) context,
					mHolder.videoAdvTtitle);
			
			if (mHolder.layout_list_section != null) {
				ThemeUtil.setSectionBackgroundNight((Activity) context,
						mHolder.layout_list_section);
			}
		}
	}

	public void changeThem(boolean isDay) {
		this.isDay = isDay;
	}

	public void changeMode(boolean isNowText) {
		Log.e(TAG, "Text Mode changed" + "__old__" + isText + "__now__"
				+ isNowText);
		if (isText & isNowText) {

		} else {
			this.isText = isNowText;
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
			if (mVideoView != null) {
				mVideoView.onStart();

				// FullScreenVideoDialog dialog = new
				// FullScreenVideoDialog(context, R.style.loading_dialog,
				// mVideoView);
				// dialog.show();
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

	/**
	 * 当界面显示INVISIBLE的时候，停止视频的播放
	 */
	public void stopVideoPlaying() {
		if (mVideoPlayPosition != -1 && mVideoView != null) {
				mVideoView.stopAndRelease();
				ViewGroup parent = (ViewGroup) mVideoView.getParent();
				if (parent != null) {
					parent.removeAllViews();
				}
				mVideoPlayPosition = -1;
		}
	}

	public void initVideoView(UIVodVideoView video) {
		this.mVideoView = video;
	}
}
