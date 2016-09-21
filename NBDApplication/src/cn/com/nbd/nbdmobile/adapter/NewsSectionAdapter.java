package cn.com.nbd.nbdmobile.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import cn.com.nbd.nbdmobile.adapter.NewsListAdapter.OnNewsClickListener;
import cn.com.nbd.nbdmobile.adapter.NewsListAdapter.ViewHolder;
import cn.com.nbd.nbdmobile.callback.ListviewPosRecrodCallback;
import cn.com.nbd.nbdmobile.callback.ToggleCheckedCallback;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.Options;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.view.CustomListViewUnused;
import cn.com.nbd.nbdmobile.view.CustomListViewUnused.HeaderAdapter;
import cn.com.nbd.nbdmobile.widget.StaticViewPager;

import com.nbd.article.article.ArticleType;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.StockIndex;
//import com.nbd.view.HeadListView;
//import com.nbd.view.HeadListView.HeaderAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 带左右子栏目切换的适配器，带有切换栏的停靠功能 集成下拉上拉刷新功能
 * 
 * @author riche
 */
public class NewsSectionAdapter extends BaseAdapter implements SectionIndexer,
		HeaderAdapter {

	ArrayList<ArticleInfo> newsList;
	ArrayList<ArticleInfo> scrollList;
	// ArrayList<AdInfo> adList;
	Activity activity;
	LayoutInflater inflater = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	DisplayImageOptions options;

	private List<Integer> mPositions;
	private List<String> mSections;
	private List<View> pageViews;

	private OnClickListener leftClickListener;
	private OnClickListener rightClickListener;

	private static final int COLOR_TEXT_NORMAL_DAY = 0xff000000;
	private static final int COLOR_TEXT_HIGHTLIGHR = 0xffd53e36;
	private static final int COLOR_TEXT_NORMAL_NIGHT = 0xff7f7f7f;

	private int headerSectionPosition;

	private ToggleCheckedCallback toggleCheckListener;
	private ListviewPosRecrodCallback postionRecrodCallback;

	private boolean isToggleClickNotify = false;
	private boolean isHeaderSectionShow = false;

	private boolean isDayTheme;
	private boolean isTextMode;
	private String imageUri = ""; // 

	private String mAdapterType;
	
	private int mAdvWidth ;

	private Map<ViewHolder, String> imgMap = new HashMap<NewsSectionAdapter.ViewHolder, String>();

	public static interface OnNewsClickListener {
		public void onNewsClick(View view, ArticleInfo article); // 传递数据给activity
	}

	OnNewsClickListener onNewsClick;

	public void setNewsClickListener(OnNewsClickListener onNewsClick) {
		this.onNewsClick = onNewsClick;

	}

	public NewsSectionAdapter(Activity activity,
			ArrayList<ArticleInfo> scrolList, ArrayList<ArticleInfo> newsList,
			String type, boolean isDay, boolean isText) {
		this.activity = activity;
		this.newsList = newsList;
		this.scrollList = scrolList;
		this.mAdapterType = type;
		this.isDayTheme = isDay;
		this.isTextMode = isText;
		inflater = LayoutInflater.from(activity);
		options = Options.getListOptions();
		
		initListener();

		initDateHead();
	}

	public void setToggleClickNotify(boolean isToggle) {
		this.isToggleClickNotify = isToggle;
	}

	private void initListener() {
		leftClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("hff", "点击了左边");
				headerSectionPosition = 0;
				isToggleClickNotify = true;
				toggleCheckListener.onToggleChecked(0);

			}
		};

		rightClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("hff", "点击了右边");
				headerSectionPosition = 1;
				isToggleClickNotify = true;
				toggleCheckListener.onToggleChecked(1);

			}
		};

	}

	private void initDateHead() {
		mSections = new ArrayList<String>();
		mPositions = new ArrayList<Integer>();
		mPositions.add(1);
	}

	@Override
	public int getCount() {
		// Log.d("HFFADAPTER",
		// "listcount ==> " + (newsList == null ? 0 : newsList.size()));

		return (newsList != null && newsList.size() > 0) ? (newsList.size() + 1)
				: 0;
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

		// 顶部的viewpager的布局和适配内容
		if (position == 0) {
			Log.e("HFF GETVIEW", "TOGGLE CHECK" + isToggleClickNotify);
			// 不是单独的点击切换，可能滚动图片资源刷新的问题，得重新刷新图片
			if (!isToggleClickNotify) {
				pageViews = new ArrayList<View>();

				// 测试的布局加载
				for (int i = 0; i < 3; i++) {
					View scrollView = inflater.inflate(
							R.layout.scroll_page_layout, null);
					ImageView img = (ImageView) scrollView
							.findViewById(R.id.scroll_image);
					ArticleInfo in = null;
					if (scrollList != null && scrollList.size() > 2) {
						Log.e("HFFTULB", scrollList.size() + ""
								+ scrollList.get(i).toString());
						in = scrollList.get(i);
						if (isTextMode) {

							imageLoader.displayImage(imageUri, img, options);
						} else {

							imageLoader.displayImage(in.getImage(), img,
									options);
						}
					}
					pageViews.add(scrollView);
				}

				mHolder.viewpagerLayout.setVisibility(View.VISIBLE);
				mHolder.layout_list_section.setVisibility(View.GONE);
				mHolder.newsLayout.setVisibility(View.GONE);
				// mHolder.pager.requestDisallowInterceptTouchEvent(true);

				mHolder.pager
						.setOnPageChangeListener(new OnPageChangeListener() {

							@Override
							public void onPageSelected(int arg0) {
								if (scrollList != null && scrollList.size() > 0 && arg0 < scrollList.size()) {

									mHolder.pagerText.setText(scrollList.get(
											arg0).getTitle());

									if (scrollList.get(arg0).getType()
											.equals(ArticleType.IMAGE)) {
										mHolder.pagerTagImg
												.setVisibility(View.VISIBLE);
									} else {
										mHolder.pagerTagImg
												.setVisibility(View.GONE);
									}
									showPagerTagPoint(mHolder, arg0);
								}

							}

							@Override
							public void onPageScrolled(int arg0, float arg1,
									int arg2) {
								// TODO Auto-generated method stub

							}

							@Override
							public void onPageScrollStateChanged(int arg0) {
								// TODO Auto-generated method stub

							}
						});

				PagerAdapter mPagerAdapter = new PagerAdapter() {

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

										onNewsClick.onNewsClick(
												pageViews.get(arg1),
												scrollList.get(arg1));

									}
								});

						return pageViews.get(arg1);
					}

				};
				mHolder.pager.setAdapter(mPagerAdapter);

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
			} else { // 点击切换，不刷新图片，恢复标志位
				Log.e("hff", "点击了不切换的TOGGLE==》" + isToggleClickNotify);
				isToggleClickNotify = false;
			}

		} else {

			// 根据文章的内容TYPE确定新闻的显示样式
			mHolder.viewpagerLayout.setVisibility(View.GONE);
			mHolder.layout_list_section.setVisibility(View.VISIBLE);
			mHolder.newsLayout.setVisibility(View.VISIBLE);
			// 获取position对应的数据，滚动图片的数据是单独的列表，新闻的位置有 1 的错开
			ArticleInfo news = getItem(position - 1);

			// 头部的相关东西
			int section = getSectionForPosition(position);
			// 设置左右切换栏的点击事件
			if (getPositionForSection(section) == position) {
				mHolder.layout_list_section.setVisibility(View.VISIBLE);

				if (headerSectionPosition == 0) { // 正价值情况
					setHeadeerStyle(mHolder.section_left,
							mHolder.section_left_bottom, mHolder.section_right,
							mHolder.section_right_bottom, 0);
				} else { // 负价值情况
					setHeadeerStyle(mHolder.section_left,
							mHolder.section_left_bottom, mHolder.section_right,
							mHolder.section_right_bottom, 1);
				}

				mHolder.section_left_layout
						.setOnClickListener(leftClickListener);

				mHolder.section_right_layout
						.setOnClickListener(rightClickListener);

			} else {
				mHolder.layout_list_section.setVisibility(View.GONE);
			}
			setThemeColor(mHolder, isDayTheme);
			
			mHolder.threePicCommonTxt.setText(news.getMobile_click_count()+ " 阅读");
			mHolder.normalCommonTxt.setText(news.getMobile_click_count()+ " 阅读");

			if (isTextMode) {

//				if (imgMap.get(mHolder) != null
//						&& imgMap.get(mHolder).equals(imageUri)) {
//
//				} else {
					imgMap.put(mHolder, imageUri);

					showNewsByType(mHolder, news, position - 1);

//				}
			} else {

				if (imgMap.get(mHolder) != null
						&& imgMap.get(mHolder).equals(news.getImage())) {

				} else {
					imgMap.put(mHolder, news.getImage());

					showNewsByType(mHolder, news, position - 1);

				}
			}

			// showNewsByType(mHolder, news, position - 1);

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
			Log.e("ZX--THEME==>", "DAY");
			ThemeUtil.setBackgroundDay(activity, mHolder.mHoldLayout);
			ThemeUtil.setBackgroundClickDay(activity, mHolder.newsLayout);
			ThemeUtil.setSectionBackgroundDay(activity,
					mHolder.layout_list_section);
			ThemeUtil.setTextColorDay(activity, mHolder.normalContentTxt);
			ThemeUtil.setTextColorDay(activity, mHolder.threePicTitle);
			ThemeUtil.setTextColorDay(activity, mHolder.advsTitleTxt);

		} else {
			Log.e("ZX--THEME==>", "NIGHT");
			ThemeUtil.setBackgroundNight(activity, mHolder.mHoldLayout);
			ThemeUtil.setBackgroundClickNight(activity, mHolder.newsLayout);
			ThemeUtil.setSectionBackgroundNight(activity,
					mHolder.layout_list_section);
			ThemeUtil.setTextColorNight(activity, mHolder.normalContentTxt);
			ThemeUtil.setTextColorNight(activity, mHolder.threePicTitle);
			ThemeUtil.setTextColorNight(activity, mHolder.advsTitleTxt);
		}

	}

	private void showPagerTagPoint(ViewHolder mHolder, int arg0) {
		// TODO Auto-generated method stub
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
	private void showNewsByType(ViewHolder mHolder, ArticleInfo info,
			int position) {

		/** 广告的数据界面解析 */
		if (info.getType().equals(ArticleType.AD_LG)
				|| info.getType().equals(ArticleType.AD_MD) || info.getType().equals(ArticleType.BOOK)) {
			mHolder.advsArticleLayout.setVisibility(View.VISIBLE);
			mHolder.threePicLayout.setVisibility(View.GONE);
			mHolder.normalLayout.setVisibility(View.GONE);
			
			if (info != null) {
				mHolder.advsTitleTxt.setText(info.getTitle());
			
				int width = Integer.parseInt(info.getWidth());
				int height = Integer.parseInt(info.getHeight());
				
				if (mAdvWidth <= 0) {
					mAdvWidth =  (int) TypedValue.applyDimension(
							TypedValue.COMPLEX_UNIT_PX, 1020, activity
							.getResources().getDisplayMetrics());
//					mAdvWidth =  (int) TypedValue.applyDimension(
//							TypedValue.COMPLEX_UNIT_DIP, 340, activity
//							.getResources().getDisplayMetrics());
				}

				float scale = Float.parseFloat(mAdvWidth+"")/Float.parseFloat(width+"");
				float imgHeight = scale* Float.parseFloat(height+"");
				int imageHeight =Math.round(imgHeight);
				
				RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
						mAdvWidth, imageHeight);
				mHolder.advsBigImage.setLayoutParams(layoutParams);
				mHolder.advsMidImage.setLayoutParams(layoutParams);
				
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
					imageLoader.displayImage(imageUri, mHolder.advsBigImage,
							options);
				} else {
					imageLoader.displayImage(info.getImage(),
							mHolder.advsBigImage, options);
				}
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

				imageLoader.displayImage(imageUri, mHolder.normalImage, options);
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
	 * 设置选择项的样式
	 * 
	 * @param lv
	 * @param rv
	 * @param position
	 */
	private void setHeadeerStyle(View lv, View lvBtm, View rv, View rvBtom,
			int position) {
		// TODO Auto-generated method stub
		switch (position) {
		case 0:
			((TextView) lvBtm).setVisibility(View.VISIBLE);
			((TextView) rvBtom).setVisibility(View.GONE);
			if (mAdapterType.equals("company")) {
				((TextView) lv).setText("正价值");
				((TextView) rv).setText("负价值");
			} else {
				((TextView) lv).setText("要闻 ");
				((TextView) rv).setText("新三版");
			}

			break;
		case 1:
			((TextView) lvBtm).setVisibility(View.GONE);
			((TextView) rvBtom).setVisibility(View.VISIBLE);
			if (mAdapterType.equals("company")) {
				((TextView) lv).setText("正价值");
				((TextView) rv).setText("负价值");
			} else {
				((TextView) lv).setText("要闻 ");
				((TextView) rv).setText("新三版");
			}
			break;
		default:
			break;
		}
		setThemeTextColot(position, lv, rv);
	}

	private void setThemeTextColot(int position, View lv, View rv) {
		switch (position) {
		case 0:
			if (isDayTheme) {
				((TextView) lv).setTextColor(COLOR_TEXT_HIGHTLIGHR);
				((TextView) rv).setTextColor(COLOR_TEXT_NORMAL_DAY);
			} else {
				((TextView) lv).setTextColor(COLOR_TEXT_HIGHTLIGHR);
				((TextView) rv).setTextColor(COLOR_TEXT_NORMAL_NIGHT);
			}
			break;
		case 1:
			if (isDayTheme) {
				((TextView) lv).setTextColor(COLOR_TEXT_NORMAL_DAY);
				((TextView) rv).setTextColor(COLOR_TEXT_HIGHTLIGHR);
			} else {
				((TextView) lv).setTextColor(COLOR_TEXT_NORMAL_NIGHT);
				((TextView) rv).setTextColor(COLOR_TEXT_HIGHTLIGHR);
			}
			break;

		default:
			break;
		}

		if (position == 0) {

		} else {

		}

	}

	static class ViewHolder {
		LinearLayout mHoldLayout;

		// 新闻部分的容器
		RelativeLayout newsLayout;
		/** 顶部viewpager的内容布局块 */
		// viewpager的容器
		RelativeLayout viewpagerLayout;
		// 顶部的viewpager，就第一个子项目显示
		ViewPager pager;
		// viewpager下面的文字栏
		TextView pagerText;

		ImageView pagerTagImg;
		ImageView leftPoint;
		ImageView midPoint;
		ImageView rightPoint;

		// viewpager下面的文字栏目
		RelativeLayout pagerBottomLayout;
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
		TextView advsTitleTxt;
		ImageView advsImageTag;
		ImageView bookImageTag;
		ImageView advsBigImage;
		ImageView advsMidImage;

		// 头部的日期部分
		LinearLayout layout_list_section;
		// 左边switch的点击控件
		RelativeLayout section_left_layout;
		// 头部左边的switch选项
		TextView section_left;
		// 头部左边下标
		TextView section_left_bottom;
		// 头部右边的switch点击控件
		RelativeLayout section_right_layout;
		// 头部右边的switch选项
		TextView section_right;
		// 头部右边下标
		TextView section_right_bottom;

	}

	/**
	 * 初始化Holder和控件
	 * 
	 * @param mHolder
	 * @param view
	 */
	private void initHolder(ViewHolder mHolder, View view) {

		mHolder.mHoldLayout = (LinearLayout) view
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

	}

	// @Override
	// public void onScrollStateChanged(AbsListView view, int scrollState) {
	//
	// // 不滚动时保存当前滚动到的位置
	// if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
	//
	// // scrollPos记录当前可见的List顶端的一行的位置
	// int scrollPos = view.getFirstVisiblePosition();
	// Log.d("hff", "滑动到的第一个位置是"+scrollPos);
	//
	// View v = view.getChildAt(0);
	// int scrollTop=(v==null)?0:v.getTop();
	// Log.d("hff", "滑动到的第一个位置的空隙"+scrollTop);
	//
	// postionRecrodCallback.setPositionRecrod(scrollPos, scrollTop);
	// }
	// }

	// // 滑动监听
	// @Override
	// public void onScroll(AbsListView view, int firstVisibleItem,
	// int visibleItemCount, int totalItemCount) {
	// // TODO Auto-generated method stub
	// if (view instanceof CustomListView) {
	// // Log.d("first", "first:" + view.getFirstVisiblePosition());
	//
	// ((CustomListView) view).configureHeaderView(firstVisibleItem);
	// }
	// }

	@Override
	public int getHeaderState(int position) {
		int realPosition = position;
		if (realPosition < 1 || position >= getCount()) {
			return HEADER_GONE;
		}
		int section = getSectionForPosition(realPosition);
		int nextSectionPosition = getPositionForSection(section + 1);
		if (nextSectionPosition != -1
				&& realPosition == nextSectionPosition - 1) {
			return HEADER_PUSHED_UP;
		}
		return HEADER_VISIBLE;
	}

	/**
	 * 设置当停靠栏滑动到顶部时顶部的样式和事件
	 * 
	 */
	@Override
	public void configureHeader(View header, int position, int alpha) {
		Log.e("ADAPTER*********", "configureHeader" + header.toString() + "--"
				+ position);
		int realPosition = position;
		int section = getSectionForPosition(realPosition);

		setHeaderStyle(header, headerSectionPosition);

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

	@Override
	public void onHeaderClick(View header, int position) {

		Log.d("hff", "头部点击事件" + position);
		// isHeaderSectionShow = tr
		isToggleClickNotify = false;
		toggleCheckListener.onToggleChecked(position);
		headerSectionPosition = position;
		setHeaderStyle(header, position);
	}

	/**
	 * 设置左右选择项的点击样式
	 * 
	 */
	private void setHeaderStyle(View header, int position) {

		TextView leftView = (TextView) header
				.findViewById(R.id.left_section_text);
		TextView leftViewBtm = (TextView) header
				.findViewById(R.id.left_section_choose);
		TextView rightView = (TextView) header
				.findViewById(R.id.right_section_text);
		TextView rightViewBtm = (TextView) header
				.findViewById(R.id.right_section_choose);
		setHeadeerStyle(leftView, leftViewBtm, rightView, rightViewBtm,
				position);

		// switch (position) {
		// case 0: // 点击了左边
		//
		// // leftView.setText("正价值");
		// // leftView.setTextColor(COLOR_TEXT_NORMAL);
		// // rightView.setText("231dsd31");
		//
		// break;
		// case 1: // 点击了右边
		//
		// rightView.setText("负价值");
		// rightView.setTextColor(COLOR_TEXT_NORMAL);
		// leftView.setText("231dsd31");
		//
		// break;
		//
		// default:
		// break;
		// }

	}

	public void setToggleChecked(ToggleCheckedCallback listener) {
		this.toggleCheckListener = listener;
	}

	public void setDataList(ArrayList<ArticleInfo> news) {
		this.newsList = news;
	}

	/**
	 * 设置滑动状态改变时，记录当前子栏目滑动的位置
	 * 
	 * @param listener
	 */
	public void setItemPosRecordListener(ListviewPosRecrodCallback listener) {
		this.postionRecrodCallback = listener;
	}

	@Override
	public void onListScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		isToggleClickNotify = false;

		if (view instanceof CustomListViewUnused) {
			// Log.d("first", "first:" + view.getFirstVisiblePosition());

			// getListView();

			((CustomListViewUnused) view).configureHeaderView(firstVisibleItem - 1);
		}

	}

	@Override
	public void isPullDown(int pullDowm) {
		// TODO Auto-generated method stub

	}

	public void setDataChange(ArrayList<ArticleInfo> scrolllists,
			ArrayList<ArticleInfo> lists) {
		this.scrollList = scrolllists;
		this.newsList = lists;
	}

	/**
	 * 当状态改变是，记录当前的滑动的位置，切换时还原
	 * 
	 * @param view
	 * @param scrollState
	 */
	@Override
	public void onListScrollStateChanged(AbsListView view, int scrollState) {
		// 不滚动时保存当前滚动到的位置
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			// scrollPos记录当前可见的List顶端的一行的位置
			int scrollPos = view.getFirstVisiblePosition();
			Log.d("hff", "滑动到的第一个位置是" + scrollPos);

			View v = view.getChildAt(0);
			int scrollTop = (v == null) ? 0 : v.getTop();
			Log.d("hff", "滑动到的第一个位置的空隙" + scrollTop);
			if (postionRecrodCallback != null) {
				postionRecrodCallback.setPositionRecrod(scrollPos, scrollTop);
			}
		}
	}

	public void changeThem(boolean isDay) {
		this.isDayTheme = isDay;
	}

	public void changeMode(boolean isText) {
		this.isTextMode = isText;
	}

	@Override
	public void onThemeChange(View headerView, boolean isDay) {

		isDayTheme = isDay;
		TextView leftView = (TextView) headerView
				.findViewById(R.id.left_section_text);
		TextView rightView = (TextView) headerView
				.findViewById(R.id.right_section_text);

		setThemeTextColot(headerSectionPosition, leftView, rightView);
	}

}
