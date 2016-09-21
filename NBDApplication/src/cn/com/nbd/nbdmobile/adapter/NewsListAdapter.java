package cn.com.nbd.nbdmobile.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SectionIndexer;
import android.widget.TextView;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.Options;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;

import com.nbd.article.article.ArticleType;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.StockIndex;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nbd.view.HeadListView;
//import com.nbd.view.HeadListView.HeaderAdapter;

/**
 * 新闻界面适配器，不带子栏目切换功能，拥有证券信息 集成下拉上拉功能
 * 
 * @author riche
 */
public class NewsListAdapter extends BaseAdapter implements SectionIndexer {

	ArrayList<ArticleInfo> newsList;
	ArrayList<ArticleInfo> scrollList;
	ArrayList<StockIndex> stockList;
	// ArrayList<AdInfo> adList;
	Activity activity;
	LayoutInflater inflater = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	DisplayImageOptions options;

	private List<Integer> mPositions;
	private List<String> mSections;
	private List<String> mPagerText = new ArrayList<String>();
	private List<View> pageViews;

	private boolean isToggleClickNotify = false;

	private PagerAdapter mHeadPageAdapter;

	private boolean isDayTheme;
	private boolean isTextMode;
	private boolean isReloadImag;
	private boolean hasHeadPic;
	private String imageUri = ""; // from drawables (only images, non-9patch)

	private Map<ViewHolder, String> imgMap = new HashMap<NewsListAdapter.ViewHolder, String>();
	
	private int mAdvWidth ;

	public static interface OnNewsClickListener {
		public void onNewsClick(View view, ArticleInfo article); // 传递数据给activity
	}

	OnNewsClickListener onNewsClick;

	public void setNewsClickListener(OnNewsClickListener onNewsClick) {
		this.onNewsClick = onNewsClick;

	}

	public NewsListAdapter(Activity activity, ArrayList<ArticleInfo> scrolList,
			ArrayList<StockIndex> stocks, ArrayList<ArticleInfo> newsList,
			boolean isDay, boolean isTextMode,boolean hasHeadPic) {
		this.activity = activity;
		this.newsList = newsList;
		this.scrollList = scrolList;
		this.stockList = stocks;
		this.isDayTheme = isDay;
		this.isTextMode = isTextMode;
		this.hasHeadPic = hasHeadPic;
		inflater = LayoutInflater.from(activity);
		options = Options.getListOptions();

				mAdvWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_PX, 1020, activity
				.getResources().getDisplayMetrics());
//		mAdvWidth = (int) TypedValue.applyDimension(
//				TypedValue.COMPLEX_UNIT_DIP, 340, activity
//				.getResources().getDisplayMetrics());

		init();

	}

	/**
	 * 定义初始化一些监听和变量
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
										scrollList.get(arg1));

							}
						});

				return pageViews.get(arg1);
			}

		};

	}

	public void setToggleClickNotify(boolean isToggle) {
		this.isToggleClickNotify = isToggle;
	}

	@Override
	public int getCount() {
		if (hasHeadPic) {
			return (newsList != null && newsList.size() > 0) ? (newsList.size() + 1)
					: 0;
		}else {
			return newsList.size();
		}
	}

	@Override
	public ArticleInfo getItem(int position) {

		if (newsList != null && newsList.size() != 0
				&& position < newsList.size() + 1) {
			return newsList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		final ViewHolder mHolder;
		View view = convertView;

		if (view == null) {
			view = inflater.inflate(R.layout.news_fragment_item_zx, null);
			mHolder = new ViewHolder();
			// 初始化holder控件
			initHolder(mHolder, view);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}

		mHolder.sectionLayout.setVisibility(View.GONE);

		// 顶部的viewpager的布局和适配内容
		if (position == 0 && hasHeadPic) {
			Log.e("HFF GETVIEW", "TOGGLE CHECK" + isToggleClickNotify);
			// 不是单独的点击切换，可能滚动图片资源刷新的问题，得重新刷新图片
			pageViews = new ArrayList<View>();

			// 测试的布局加载
			for (int i = 0; i < 3; i++) {
				View scrollView = inflater.inflate(R.layout.scroll_page_layout,
						null);
				ImageView img = (ImageView) scrollView
						.findViewById(R.id.scroll_image);
				ArticleInfo in = null;
				if (scrollList != null && scrollList.size() > 2) {
					in = scrollList.get(i);
					if (isTextMode) {
						imageLoader.displayImage(imageUri, img, options);
					} else {
						imageLoader.displayImage(in.getImage(), img, options);
					}
					if (in.getTitle() != null) {
						mPagerText.add(i, in.getTitle());
					}
				}

				pageViews.add(scrollView);
			}

			mHolder.viewpagerLayout.setVisibility(View.VISIBLE);
			mHolder.newsLayout.setVisibility(View.GONE);

			mHolder.pager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					if (scrollList != null && scrollList.size() > 0 && arg0 < scrollList.size()) {
						mHolder.pagerText.setText(scrollList.get(arg0)
								.getTitle());
						if (scrollList.get(arg0).getType()
								.equals(ArticleType.IMAGE)) {
							mHolder.pagerTagImg.setVisibility(View.VISIBLE);
						} else {
							mHolder.pagerTagImg.setVisibility(View.GONE);
						}
						showPagerTagPoint(mHolder, arg0);
					}
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
					// TODO Auto-generated method stub

				}

			});
			mHolder.pager.setAdapter(mHeadPageAdapter);

			if (scrollList != null && scrollList.size() > 0) {
				mHolder.pagerText.setText(scrollList.get(0).getTitle());
				mHolder.pager.setCurrentItem(0);
				if (scrollList.get(0).getType().equals(ArticleType.IMAGE)) {
					mHolder.pagerTagImg.setVisibility(View.VISIBLE);
				} else {
					mHolder.pagerTagImg.setVisibility(View.GONE);
				}
				showPagerTagPoint(mHolder, 0);
			}
			isToggleClickNotify = false;
			// 证券是否显示及内容显示情况
			if (stockList != null) {
				mHolder.stock_layout.setVisibility(View.VISIBLE);
				if (stockList.size() > 0) {
					setStockViewData(stockList.get(0),
							mHolder.left_stcok_title,
							mHolder.left_stcok_content,
							mHolder.left_stcok_change_amount,
							mHolder.left_stcok_change);
					setStockViewData(stockList.get(1), mHolder.mid_stcok_title,
							mHolder.mid_stcok_content,
							mHolder.mid_stcok_change_amount,
							mHolder.mid_stcok_change);
					setStockViewData(stockList.get(2),
							mHolder.right_stcok_title,
							mHolder.right_stcok_content,
							mHolder.right_stcok_change_amount,
							mHolder.right_stcok_change);
				}

			} else {
				mHolder.stock_layout.setVisibility(View.GONE);
			}

		} else { // 新闻显示的部分
			mHolder.newsLayout.setVisibility(View.VISIBLE);
			mHolder.stock_layout.setVisibility(View.GONE); // 证券部分
			mHolder.viewpagerLayout.setVisibility(View.GONE); // 图片轮播

			// 获取position对应的数据，滚动图片的数据是单独的列表，新闻的位置有 1 的错开
			ArticleInfo news = new ArticleInfo();
			if (hasHeadPic) {
				 news = getItem(position - 1);
			}else {
				 news = getItem(position);
			}

			mHolder.newsLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					if (onNewsClick != null) {
						if (hasHeadPic) {
							onNewsClick.onNewsClick(mHolder.newsLayout,
									newsList.get(position - 1));
						}else {
							onNewsClick.onNewsClick(mHolder.newsLayout,
									newsList.get(position));
						}
					}
				}
			});

			mHolder.threePicCommonTxt.setText(news.getMobile_click_count()
					+ " 阅读");
			mHolder.normalCommonTxt.setText(news.getMobile_click_count()
					+ " 阅读");
			setThemeColor(mHolder, isDayTheme);

			if (isTextMode) {

				// if (imgMap.get(mHolder) != null &&
				// imgMap.get(mHolder).equals(imageUri) ) {
				//
				// }else {
				imgMap.put(mHolder, imageUri);

				showNewsByType(mHolder, news);

				// }
			} else {
				if (imgMap.get(mHolder) != null
						&& imgMap.get(mHolder).equals(news.getImage())) {

				} else {
					imgMap.put(mHolder, news.getImage());

					showNewsByType(mHolder, news);

				}

			}

		}
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
			ThemeUtil.setBackgroundDay(activity, mHolder.mHolderLayout);
			ThemeUtil.setBackgroundClickDay(activity, mHolder.newsLayout);
			ThemeUtil.setTextColorDay(activity, mHolder.normalContentTxt);
			ThemeUtil.setTextColorDay(activity, mHolder.threePicTitle);
			ThemeUtil.setTextColorDay(activity, mHolder.advsTitleTxt);

		} else {
			ThemeUtil.setBackgroundClickNight(activity, mHolder.mHolderLayout);
			ThemeUtil.setBackgroundClickNight(activity, mHolder.newsLayout);
			ThemeUtil.setTextColorNight(activity, mHolder.normalContentTxt);
			ThemeUtil.setTextColorNight(activity, mHolder.threePicTitle);
			ThemeUtil.setTextColorNight(activity, mHolder.advsTitleTxt);
		}

	}

	private void showPagerTagPoint(ViewHolder mHolder, int arg0) {
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
		// circle_shape

	}

	/**
	 * 根据新闻源的数据显示界面
	 */
	private void showNewsByType(ViewHolder mHolder, ArticleInfo info) {

		/** 广告的数据界面解析 */
		if (info.getType().equals(ArticleType.AD_LG)
				|| info.getType().equals(ArticleType.AD_MD) || info.getType().equals(ArticleType.BOOK)) {
			mHolder.advsArticleLayout.setVisibility(View.VISIBLE);
			mHolder.threePicLayout.setVisibility(View.GONE);
			mHolder.normalLayout.setVisibility(View.GONE);
			// long adId = info.getArticle_id();
			// AdInfo showAdInfo = null;
			// for (int i = 0; i < adList.size(); i++) {
			// if (adList.get(i).getId() == adId) {
			// showAdInfo = adList.get(i);
			// }
			// }

			if (info != null) {
				mHolder.advsTitleTxt.setText(info.getTitle());
				
				RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				mHolder.advsTitleLayout.setLayoutParams(titleParams);
				mHolder.advsTitleLayout.setId(1);

				int width = Integer.parseInt(info.getWidth());
				int height = Integer.parseInt(info.getHeight());

				float scale = Float.parseFloat(mAdvWidth+"")/Float.parseFloat(width+"");
				float imgHeight = scale* Float.parseFloat(height+"");
				int imageHeight =Math.round(imgHeight);
				
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						mAdvWidth, imageHeight);
				layoutParams.addRule(RelativeLayout.BELOW, 1);
				layoutParams.topMargin = 6;
				
				mHolder.advsBigImage.setLayoutParams(layoutParams);
//				mHolder.advsMidImage.setLayoutParams(layoutParams);
				
				mHolder.advsBigImage.setVisibility(View.VISIBLE);
				mHolder.advsMidImage.setVisibility(View.GONE);

				if (info.getType().equals(ArticleType.AD_LG) || info.getType().equals(ArticleType.AD_MD)) {
					mHolder.advsImageTag.setVisibility(View.VISIBLE);
					mHolder.bookImageTag.setVisibility(View.GONE);
				} else {
					mHolder.advsImageTag.setVisibility(View.GONE);
					mHolder.bookImageTag.setVisibility(View.VISIBLE);
				}

				if (isTextMode) {
					imageLoader.displayImage(imageUri,
							mHolder.advsBigImage, options);
				} else {
					imageLoader.displayImage(info.getImage(),
							mHolder.advsBigImage, options);
				}
//				RelativeLayout.LayoutParams advLayoutParams = (LayoutParams) mHolder.advsBigImage
//						.getLayoutParams();

			}
			/** 图集新闻的界面解析 */
		} else if (info.getType().equals(ArticleType.IMAGE)) {
			// 新闻的文章分类
			mHolder.threePicLayout.setVisibility(View.VISIBLE);
			mHolder.normalLayout.setVisibility(View.GONE);
			mHolder.advsArticleLayout.setVisibility(View.GONE);

			mHolder.threePicTitle.setText(info.getTitle());
			mHolder.threePicCommonTxt.setText(info.getMobile_click_count()
					+ " 阅读");
			Log.e("ThreePic", "====>enter");

			if (info.getGallery() != null) {
				List<ArticleImages> mArticleImages = info.getGallery().getImages();

				ArticleImages leftImg = null;
				ArticleImages midImg = null;
				ArticleImages rightImg = null;
				if (mArticleImages != null ) {
					for (int i = 0; i < mArticleImages.size(); i++) {
						switch (i) {
						case 0:
							 leftImg = mArticleImages.get(0);
							break;
						case 1:
							 midImg = mArticleImages.get(1);
							break;
						case 2:
							 rightImg = mArticleImages.get(2);
							break;
						default:
							break;
						}
					}
				}

				if (isTextMode) {
					imageLoader.displayImage(imageUri, mHolder.threePicLeft,
							options);
					imageLoader.displayImage(imageUri, mHolder.threePicMiddle,
							options);
					imageLoader.displayImage(imageUri, mHolder.threePicRight,
							options);

				} else {
					imageLoader.displayImage(leftImg != null ?leftImg.getImage_src():imageUri,
							mHolder.threePicLeft, options);
					imageLoader.displayImage(midImg != null ?midImg.getImage_src():imageUri,
							mHolder.threePicMiddle, options);
					imageLoader.displayImage(rightImg != null ?rightImg.getImage_src():imageUri,
							mHolder.threePicRight, options);
				}

			} else {
				Log.e("ThreePic", "====>NULL");
			}
		} else {
			/** 普通的新闻样式布局 */
			mHolder.normalLayout.setVisibility(View.VISIBLE);
			mHolder.threePicLayout.setVisibility(View.GONE);
			mHolder.advsArticleLayout.setVisibility(View.GONE);
			// 小图的广告
			if (info.getType().equals(ArticleType.AD_SM)) {
				mHolder.smAdvTagImg.setVisibility(View.VISIBLE);
				mHolder.normalCommonTxt.setVisibility(View.GONE);
				mHolder.normalTagImg.setVisibility(View.GONE);

				// 小图的普通文章
			} else {
				mHolder.smAdvTagImg.setVisibility(View.GONE);
				mHolder.normalCommonTxt.setVisibility(View.VISIBLE);
				mHolder.normalTagImg.setVisibility(View.VISIBLE);

				if (info.getType().equals(ArticleType.FEATURE)) {
					mHolder.normalTagImg
							.setImageResource(R.drawable.article_tag_market);
				} else if (info.getType().equals(ArticleType.LIVE)) {
					mHolder.normalTagImg
							.setImageResource(R.drawable.article_tag_live);
				} else if (info.getType().equals(ArticleType.MARKET)) {
					mHolder.normalTagImg
							.setImageResource(R.drawable.article_tag_broad);
				} else if (info.getType().equals(ArticleType.VIDEO)) {
					mHolder.normalTagImg
							.setImageResource(R.drawable.article_tag_video);
				} else {
					mHolder.normalTagImg.setVisibility(View.GONE);
					// mHolder.normalTagImg.setImageResource(R.drawable.article_tag_video);
				}
				// mHolder.normalTagImg.
			}

			if (isTextMode) {

				imageLoader
						.displayImage(imageUri, mHolder.normalImage, options);
			} else {

				imageLoader.displayImage(info.getImage(), mHolder.normalImage,
						options);
			}
			// 根据文章的内容TYPE确定新闻的显示样式

			mHolder.normalContentTxt.setText(info.getTitle());
			mHolder.ori_source.setText(info.getOri_source());
			mHolder.normalCommonTxt.setText(info.getMobile_click_count()
					+ " 阅读");
		}
	}

	/**
	 * 设置stock控件的数据
	 */
	private void setStockViewData(StockIndex stock, TextView title,
			TextView content, TextView amount, TextView change) {
		float count = Float.parseFloat(stock.getChange_amount());
		if (count < 0) { // 股指下降为绿色的情况
			content.setTextColor(activity.getResources().getColor(
					R.color.news_point_green));
			amount.setTextColor(activity.getResources().getColor(
					R.color.news_point_green));
			change.setTextColor(activity.getResources().getColor(
					R.color.news_point_green));
		} else {
			content.setTextColor(activity.getResources().getColor(
					R.color.news_point_red));
			amount.setTextColor(activity.getResources().getColor(
					R.color.news_point_red));
			change.setTextColor(activity.getResources().getColor(
					R.color.news_point_red));
		}
		title.setText(stock.getSecShortName());
		content.setText(stock.getNew_price());
		amount.setText(stock.getChange_amount());
		change.setText(stock.getChange() + "%");
	}

	static class ViewHolder {
		LinearLayout mHolderLayout;

		// 新闻部分的容器
		RelativeLayout newsLayout;
		/** 顶部viewpager的内容布局块 */
		// viewpager的容器
		RelativeLayout viewpagerLayout;
		// 顶部的viewpager，就第一个子项目显示
		// StaticViewPager pager;
		ViewPager pager;
		// viewpager下面的文字栏
		TextView pagerText;
		// viewpager下面的文字栏目
		RelativeLayout pagerBottomLayout;
		// viewpager 下面的指示点
		ImageView pagerTagImg;
		ImageView leftPoint;
		ImageView midPoint;
		ImageView rightPoint;

		/** 一般新闻布局的内容块，和小图广告布局共享布局 */
		// 一般常见新闻布局
		RelativeLayout normalLayout;
		// 一般新闻的图片
		ImageView normalImage;
		// 一般新闻的新闻标题
		TextView normalContentTxt;
		// 一般新闻左下角评论数文字
		TextView normalCommonTxt;
		// 视频新闻的右下角标志TAG图标
		ImageView normalTagImg;
		// 小图广告新闻右下角的TAG图标
		ImageView smAdvTagImg;
		//新闻左下角显示文章的来源
		TextView ori_source;

		/** 三张图片图集的新闻样式布局块 */
		// 用于显示隐藏的大布局
		RelativeLayout threePicLayout;
		// 标题栏
		TextView threePicTitle;
		// 左边的图片
		ImageView threePicLeft;
		// 中间的图片
		ImageView threePicMiddle;
		// 右边的图片
		ImageView threePicRight;
		// 左下角的图片TAG
		ImageView threePicTag;
		// 右下角的评论数
		TextView threePicCommonTxt;

		/** 广告推广的布局 */
		RelativeLayout advsArticleLayout;
		LinearLayout advsTitleLayout;
		TextView advsTitleTxt;
		ImageView advsImageTag;
		ImageView bookImageTag;
		ImageView advsBigImage;
		ImageView advsMidImage;

		// 证券布局
		LinearLayout stock_layout;
		TextView left_stcok_title;
		TextView left_stcok_content;
		TextView left_stcok_change_amount;
		TextView left_stcok_change;
		TextView mid_stcok_title;
		TextView mid_stcok_content;
		TextView mid_stcok_change_amount;
		TextView mid_stcok_change;
		TextView right_stcok_title;
		TextView right_stcok_content;
		TextView right_stcok_change_amount;
		TextView right_stcok_change;

		LinearLayout sectionLayout;

	}

	/**
	 * 初始化Holder和控件
	 * 
	 * @param mHolder
	 * @param view
	 */
	private void initHolder(ViewHolder mHolder, View view) {

		mHolder.mHolderLayout = (LinearLayout) view
				.findViewById(R.id.item_layout);

		mHolder.newsLayout = (RelativeLayout) view
				.findViewById(R.id.news_layout);

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
		mHolder.smAdvTagImg = (ImageView) view
				.findViewById(R.id.adv_article_tag_img);
		mHolder.ori_source = (TextView) view.findViewById(R.id.normal_article_ori_source);

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

		// 广告图片文章布局
		mHolder.advsArticleLayout = (RelativeLayout) view
				.findViewById(R.id.advs_article_layout);
		mHolder.advsTitleLayout = (LinearLayout) view.findViewById(R.id.adv_title_layout);
		mHolder.advsTitleTxt = (TextView) view
				.findViewById(R.id.advs_article_title);
		mHolder.advsImageTag = (ImageView) view
				.findViewById(R.id.advs_article_tag_img);
		mHolder.bookImageTag = (ImageView) view
				.findViewById(R.id.book_article_tag_img);
		mHolder.advsBigImage = (ImageView) view
				.findViewById(R.id.advs_bg_img_content);
		mHolder.advsMidImage = (ImageView) view
				.findViewById(R.id.advs_md_img_content);

		// 证券指数部分的数据控件
		mHolder.stock_layout = (LinearLayout) view
				.findViewById(R.id.gp_point_layout);
		mHolder.left_stcok_title = (TextView) view
				.findViewById(R.id.lpoint_name);
		mHolder.left_stcok_content = (TextView) view
				.findViewById(R.id.lpoint_content);
		mHolder.left_stcok_change_amount = (TextView) view
				.findViewById(R.id.lpoint_amount);
		mHolder.left_stcok_change = (TextView) view
				.findViewById(R.id.lpoint_change);

		mHolder.mid_stcok_title = (TextView) view
				.findViewById(R.id.mpoint_name);
		mHolder.mid_stcok_content = (TextView) view
				.findViewById(R.id.mpoint_content);
		mHolder.mid_stcok_change_amount = (TextView) view
				.findViewById(R.id.mpoint_amount);
		mHolder.mid_stcok_change = (TextView) view
				.findViewById(R.id.mpoint_change);

		mHolder.right_stcok_title = (TextView) view
				.findViewById(R.id.rpoint_name);
		mHolder.right_stcok_content = (TextView) view
				.findViewById(R.id.rpoint_content);
		mHolder.right_stcok_change_amount = (TextView) view
				.findViewById(R.id.rpoint_amount);
		mHolder.right_stcok_change = (TextView) view
				.findViewById(R.id.rpoint_change);

		mHolder.sectionLayout = (LinearLayout) view
				.findViewById(R.id.gs_layout_list_section);

		view.setTag(mHolder);

	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return mSections.toArray();
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		if (sectionIndex < 0 || sectionIndex >= mPositions.size()) {
			return -1;
		}
		return mPositions.get(sectionIndex);
	}

	@Override
	public int getSectionForPosition(int position) {
		if (position < 0 || position >= getCount()) {
			return -1;
		}
		int index = Arrays.binarySearch(mPositions.toArray(), position);
		return index >= 0 ? index : -index - 2;
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
	public void setDataChange(ArrayList<ArticleInfo> scrolllists,
			ArrayList<StockIndex> stocks, ArrayList<ArticleInfo> lists) {
		this.stockList = stocks;
		this.scrollList = scrolllists;
		this.newsList = lists;

	}

	public void changeThem(boolean isDay) {
		this.isDayTheme = isDay;
	}

	public void changeMode(boolean isText) {
		Log.e("TEXT MODE", "CHANGE TEXT MODE " + isText + isTextMode);
		if (isText & isTextMode) {

		} else {
			this.isTextMode = isText;
			isReloadImag = true;
		}
	}

}