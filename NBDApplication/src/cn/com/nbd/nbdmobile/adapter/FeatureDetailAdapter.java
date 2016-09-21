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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import cn.com.nbd.nbdmobile.activity.FeatureDetailActivity;
import cn.com.nbd.nbdmobile.adapter.NewsListAdapter.ViewHolder;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.Options;

import com.nbd.article.article.ArticleType;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.FeatureDetail;
import com.nbd.article.bean.FeatureLable;
import com.nbd.article.bean.StockIndex;
//import com.nbd.view.HeadListView;
//import com.nbd.view.HeadListView.HeaderAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 新闻界面适配器，不带子栏目切换功能，拥有证券信息 集成下拉上拉功能
 * 
 * @author riche
 */
public class FeatureDetailAdapter extends BaseAdapter {

	Activity activity;
	LayoutInflater inflater = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	DisplayImageOptions options;

	private Map<ViewHolder, String> imgMap = new HashMap<FeatureDetailAdapter.ViewHolder, String>();

	private FeatureDetail mFeatureDetail;

	private List<FeatureLable> mFeatureLables; // 热点标签的列表
	private List<ArticleInfo> mArticleInfos; // 不同标签下的文章列表

	private String mHeadImg; // 顶部主题图片
	private String mLeadText; // 顶部导语的内容

	private List<Integer> mTagFirstPosition; // 显示标题的item位置列表
	private List<String> mTagList; // 显示新闻小标题的内容列表

	private boolean isDayTheme;
	private boolean isTextMode;
	private String imageUri = "";

	private int mAdvWidth;

	public static interface OnNewsClickListener {
		public void onNewsClick(View view, ArticleInfo article); // 传递数据给activity
	}

	public interface onHotTagClickListener {
		void onTagClickPosition(int position, int tagPosition);
	}

	private onHotTagClickListener mTagClickListener;
	OnNewsClickListener onNewsClick;

	public void setNewsClickListener(OnNewsClickListener onNewsClick) {
		this.onNewsClick = onNewsClick;
	}

	public void setTagClickListener(onHotTagClickListener tagClickListener) {
		this.mTagClickListener = tagClickListener;
	}

	public FeatureDetailAdapter(Activity activity, FeatureDetail detail,
			boolean isDay, boolean isText) {
		this.activity = activity;
		inflater = LayoutInflater.from(activity);
		options = Options.getListOptions();

		this.isDayTheme = isDay;
		this.mFeatureDetail = detail;
		this.isTextMode = isText;

//		mAdvWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
//				1020, activity.getResources().getDisplayMetrics());
		mAdvWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 340, activity
				.getResources().getDisplayMetrics());
		dealFeatureData(mFeatureDetail);

	}

	/**
	 * 处理传入的文章详情数据
	 */
	private void dealFeatureData(FeatureDetail feature) {

		mHeadImg = feature.getAvatar();
		mLeadText = feature.getLead();

		mFeatureLables = feature.getApp_feature_labels();

		if (mTagFirstPosition == null) {
			mTagFirstPosition = new ArrayList<Integer>();
		}

		if (mTagList == null) {
			mTagList = new ArrayList<String>();
		}

		if (mArticleInfos == null) {
			mArticleInfos = new ArrayList<ArticleInfo>();
		}

		int temptPosition = 2;
		// 根据标签的列表，和每个标签里面的文章个数，解析数据
		if (mFeatureLables != null && mFeatureLables.size() > 0) {
			for (int i = 0; i < mFeatureLables.size(); i++) {
				mTagFirstPosition.add(temptPosition);
				mTagList.add(mFeatureLables.get(i).getName());

				int articleSize = mFeatureLables.get(i).getArticles().size();
				temptPosition = temptPosition + articleSize;
				mArticleInfos.addAll(mFeatureLables.get(i).getArticles());
			}
		}

		Log.e("FEATURE-DETAIL==>", "TAG==>" + mTagFirstPosition.size() + "=="
				+ mTagList.size() + "==" + mArticleInfos.size());

	}

	@Override
	public int getCount() {
		return mArticleInfos.size() + 2;
	}

	@Override
	public ArticleInfo getItem(int position) {

		return mArticleInfos.get(position - 2);
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
			view = inflater.inflate(R.layout.item_feature_detail, null);
			mHolder = new ViewHolder();
			// 初始化holder控件
			initHolder(mHolder, view);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}

		showThemeUi(mHolder);

		// 第一项导语部分
		if (position == 0) {
			mHolder.leadLayout.setVisibility(View.VISIBLE);
			mHolder.tagsLayout.setVisibility(View.GONE);
			mHolder.newsLayout.setVisibility(View.GONE);

			mHolder.leaderText.setText("				 " + mLeadText);
			if (isTextMode) {

				imageLoader.displayImage(imageUri, mHolder.headImg, options);
			} else {

				imageLoader.displayImage(mHeadImg, mHolder.headImg, options);
			}
		} else if (position == 1) {
			mHolder.leadLayout.setVisibility(View.GONE);
			mHolder.tagsLayout.setVisibility(View.VISIBLE);
			mHolder.newsLayout.setVisibility(View.GONE);

			makeHotTagView(mHolder);

		} else {
			mHolder.leadLayout.setVisibility(View.GONE);
			mHolder.tagsLayout.setVisibility(View.GONE);
			mHolder.newsLayout.setVisibility(View.VISIBLE);

			String headString = IsFristNewsItem(position);

			if (headString != null) {
				mHolder.newsTagLayout.setVisibility(View.VISIBLE);
				mHolder.featureTagText.setText(headString);

			} else {
				mHolder.newsTagLayout.setVisibility(View.GONE);

			}

			showNewsByType(mHolder, getItem(position), position);

		}

		mHolder.newsLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (onNewsClick != null) {
					onNewsClick.onNewsClick(mHolder.newsLayout,
							getItem(position));
				}
			}
		});

		return view;
	}

	@SuppressWarnings("deprecation")
	private void showThemeUi(ViewHolder mHolder) {
		if (isDayTheme) {
			mHolder.mMainItemLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.day_section_background));
			mHolder.headImg.setBackgroundColor(activity.getResources()
					.getColor(R.color.day_item_background));
			mHolder.mleadLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.day_item_background));
			mHolder.leaderText.setTextColor(activity.getResources().getColor(
					R.color.day_black));
			mHolder.newsTitle.setTextColor(activity.getResources().getColor(
					R.color.day_black));
			mHolder.featureTagLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.day_item_background));
			mHolder.detailNewsLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.day_item_background));
			mHolder.tagsGapDiv.setBackgroundColor(activity.getResources()
					.getColor(R.color.day_section_background));
			mHolder.featureTag1.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape));
			mHolder.featureTag2.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape));
			mHolder.featureTag3.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape));
			mHolder.featureTag4.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape));
			mHolder.featureTag5.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape));
			mHolder.featureTag6.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape));
			mHolder.featureTag7.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape));
			mHolder.featureTag8.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape));

		} else {
			mHolder.mMainItemLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.night_common_background));
			mHolder.headImg.setBackgroundColor(activity.getResources()
					.getColor(R.color.night_section_background));
			mHolder.mleadLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.night_section_background));
			mHolder.leaderText.setTextColor(activity.getResources().getColor(
					R.color.night_black));
			mHolder.newsTitle.setTextColor(activity.getResources().getColor(
					R.color.night_black));
			mHolder.featureTagLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.night_section_background));
			mHolder.detailNewsLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.night_section_background));
			mHolder.tagsGapDiv.setBackgroundColor(activity.getResources()
					.getColor(R.color.night_common_background));
			mHolder.featureTag1.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape_night));
			mHolder.featureTag2.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape_night));
			mHolder.featureTag3.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape_night));
			mHolder.featureTag4.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape_night));
			mHolder.featureTag5.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape_night));
			mHolder.featureTag6.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape_night));
			mHolder.featureTag7.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape_night));
			mHolder.featureTag8.setBackgroundDrawable(activity.getResources()
					.getDrawable(R.drawable.feature_tag_text_shape_night));

		}

	}

	/**
	 * 根据专题详情中的焦点个数生成对应的内容
	 * 
	 * @param tagsLayout
	 */
	private void makeHotTagView(ViewHolder holder) {

		if (mTagList.size() > 4) {
			holder.btmTagLayout.setVisibility(View.VISIBLE);
			holder.featureTag5.setVisibility(View.GONE);
			holder.featureTag6.setVisibility(View.GONE);
			holder.featureTag7.setVisibility(View.GONE);
			holder.featureTag8.setVisibility(View.GONE);
		} else {
			holder.btmTagLayout.setVisibility(View.GONE);
			holder.featureTag1.setVisibility(View.GONE);
			holder.featureTag2.setVisibility(View.GONE);
			holder.featureTag3.setVisibility(View.GONE);
			holder.featureTag4.setVisibility(View.GONE);
		}
		switch (mTagList.size()) {
		case 8:
			holder.featureTag8.setVisibility(View.VISIBLE);
			holder.featureTag8.setText(mTagList.get(7));
			setTagListener(holder.featureTag8, 7);
		case 7:
			holder.featureTag7.setVisibility(View.VISIBLE);
			holder.featureTag7.setText(mTagList.get(6));
			setTagListener(holder.featureTag7, 6);
		case 6:
			holder.featureTag6.setVisibility(View.VISIBLE);
			holder.featureTag6.setText(mTagList.get(5));
			setTagListener(holder.featureTag6, 5);
		case 5:
			holder.featureTag5.setVisibility(View.VISIBLE);
			holder.featureTag5.setText(mTagList.get(4));
			setTagListener(holder.featureTag5, 4);
		case 4:
			holder.featureTag4.setVisibility(View.VISIBLE);
			holder.featureTag4.setText(mTagList.get(3));
			setTagListener(holder.featureTag4, 3);
		case 3:
			holder.featureTag3.setVisibility(View.VISIBLE);
			holder.featureTag3.setText(mTagList.get(2));
			setTagListener(holder.featureTag3, 2);
		case 2:
			holder.featureTag2.setVisibility(View.VISIBLE);
			holder.featureTag2.setText(mTagList.get(1));
			setTagListener(holder.featureTag2, 1);
		case 1:
			holder.featureTag1.setVisibility(View.VISIBLE);
			holder.featureTag1.setText(mTagList.get(0));
			setTagListener(holder.featureTag1, 0);
		default:
			break;
		}

	}

	/**
	 * 给热门标签设置点击事件
	 */
	private void setTagListener(View view, final int positon) {
		view.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				mTagClickListener.onTagClickPosition(
						mTagFirstPosition.get(positon), positon);
			}
		});

	}

	/**
	 * 自动的根据热门标签的数量生成标签
	 * 
	 * @param layout
	 */
	private void makeHotTagAuto(RelativeLayout layout) {

		// RelativeLayout wrraperLayout = new RelativeLayout(mContext);
		// RelativeLayout.LayoutParams wrraperParams = new
		// RelativeLayout.LayoutParams(
		// LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		// wrraperParams.width = TabWidth / mTabVisibleCount;
		// wrraperLayout.setLayoutParams(wrraperParams);
		//
		// TextView tv = new TextView(mContext);
		// RelativeLayout.LayoutParams lp = new
		// RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
		// LayoutParams.WRAP_CONTENT);
		// lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		// tv.setText(title);
		// tv.setGravity(Gravity.CENTER);
		// tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		// tv.setId(1);
		// if (color == 0) {
		// tv.setTextColor(mContext.getResources().getColor(
		// R.color.main_title_txt_hightlight));
		// } else {
		// tv.setTextColor(mContext.getResources().getColor(
		// R.color.main_title_txt));
		// }
		// tv.setLayoutParams(lp);
		//
		// int tagNum = mTagList.size();
		// switch (tagNum) {
		// case value:
		//
		// break;
		//
		// default:
		// break;
		// }
		//
		// for (int i = 0; i < mTagList.size(); i++) {
		//
		// }

	};

	/**
	 * 获取是不是第一个新闻的标志，是的话就显示内容
	 * 
	 * @param position
	 * @return
	 */
	private String IsFristNewsItem(int position) {

		for (int i = 0; i < mTagFirstPosition.size(); i++) {
			Log.e("POSITION", "" + mTagFirstPosition.get(i) + mTagList.get(i));
			if (position == mTagFirstPosition.get(i)) {
				return mTagList.get(i);
			}
		}
		return null;
	}

	/**
	 * Holder
	 * 
	 * @author he
	 * 
	 */
	static class ViewHolder {

		// 顶部导语栏目的布局
		RelativeLayout leadLayout;
		// 顶部的标题图片
		ImageView headImg;
		// 导语的内容
		TextView leaderText;

		// 装热点标签的容器
		RelativeLayout tagsLayout;
		// 新闻内容顶部的标签栏
		RelativeLayout featureTagLayout;

		// 新闻的内容布局
		RelativeLayout newsLayout;
		// 新闻标签栏
		RelativeLayout newsTagLayout;
		// 标签栏里面的文字
		TextView featureTagText;
		// 新闻图片
		ImageView newsImg;
		// 新闻的标题
		TextView newsTitle;
		// 新闻类型标签图片
		ImageView newsTagImg;
		TextView readNumText;

		RelativeLayout normalLayout;

		RelativeLayout bookLayout;
		LinearLayout bookTitleLayout;
		TextView bookTitle;
		ImageView bookImg;
		ImageView bookTag;

		// TAG标签栏
		RelativeLayout topTagLayout;
		RelativeLayout btmTagLayout;

		TextView featureTag1;
		TextView featureTag2;
		TextView featureTag3;
		TextView featureTag4;
		TextView featureTag5;
		TextView featureTag6;
		TextView featureTag7;
		TextView featureTag8;

		// 主题模式的颜色块添加
		TextView tagsGapDiv; // feature_tags_btm_div
		RelativeLayout detailNewsLayout; // feature_detail_news_layout
		RelativeLayout mleadLayout;// feature_item_lead_layout
		LinearLayout mMainItemLayout; // feature_item_layout

	}

	/**
	 * 初始化Holder和控件
	 * 
	 * @param mHolder
	 * @param view
	 */
	private void initHolder(ViewHolder mHolder, View view) {

		// 顶部导语栏目的布局
		mHolder.leadLayout = (RelativeLayout) view
				.findViewById(R.id.feature_item_tips_layout);
		// 顶部的标题图片
		mHolder.headImg = (ImageView) view
				.findViewById(R.id.feature_item_tips_img);
		// 导语的内容
		mHolder.leaderText = (TextView) view
				.findViewById(R.id.feature_item_tips_content);

		// 装热点标签的容器
		mHolder.tagsLayout = (RelativeLayout) view
				.findViewById(R.id.feature_detail_tags_layout);
		// 新闻内容顶部的标签栏
		mHolder.featureTagLayout = (RelativeLayout) view
				.findViewById(R.id.feature_tags_content_layout);

		mHolder.newsTagLayout = (RelativeLayout) view
				.findViewById(R.id.feature_detail_news_tag_layout);
		// 标签栏里面的文字
		mHolder.featureTagText = (TextView) view
				.findViewById(R.id.feature_detail_news_tag_txt);

		// 新闻的内容布局
		mHolder.newsLayout = (RelativeLayout) view
				.findViewById(R.id.feature_detail_news_layout);
		// 新闻图片
		mHolder.newsImg = (ImageView) view
				.findViewById(R.id.feature_detail_article_img);
		// 新闻的标题
		mHolder.newsTitle = (TextView) view
				.findViewById(R.id.feature_detail_article_content);
		// 新闻类型标签图片
		mHolder.newsTagImg = (ImageView) view
				.findViewById(R.id.feature_detail_article_tag_img);
		mHolder.readNumText = (TextView) view
				.findViewById(R.id.feature_detail_article_commonnum);

		mHolder.normalLayout = (RelativeLayout) view
				.findViewById(R.id.feature_detail_article_layout);

		mHolder.bookTitleLayout = (LinearLayout) view
				.findViewById(R.id.feature_detail_book_title_layout);
		mHolder.bookLayout = (RelativeLayout) view
				.findViewById(R.id.feature_detail_big_img_layout);
		mHolder.bookImg = (ImageView) view
				.findViewById(R.id.feature_detail_book_img);
		mHolder.bookTitle = (TextView) view
				.findViewById(R.id.feature_detail_book_title);
		mHolder.bookTag = (ImageView) view
				.findViewById(R.id.feature_detail_book_title_tag);

		mHolder.topTagLayout = (RelativeLayout) view
				.findViewById(R.id.feature_tags_top_layout);
		mHolder.btmTagLayout = (RelativeLayout) view
				.findViewById(R.id.feature_tags_bottom_layout);

		mHolder.featureTag1 = (TextView) view
				.findViewById(R.id.feature_tag_one);
		mHolder.featureTag2 = (TextView) view
				.findViewById(R.id.feature_tag_two);
		mHolder.featureTag3 = (TextView) view
				.findViewById(R.id.feature_tag_three);
		mHolder.featureTag4 = (TextView) view
				.findViewById(R.id.feature_tag_four);
		mHolder.featureTag5 = (TextView) view
				.findViewById(R.id.feature_tag_btm_one);
		mHolder.featureTag6 = (TextView) view
				.findViewById(R.id.feature_tag_btm_two);
		mHolder.featureTag7 = (TextView) view
				.findViewById(R.id.feature_tag_btm_three);
		mHolder.featureTag8 = (TextView) view
				.findViewById(R.id.feature_tag_btm_four);

		mHolder.tagsGapDiv = (TextView) view
				.findViewById(R.id.feature_tags_btm_div);
		mHolder.detailNewsLayout = (RelativeLayout) view
				.findViewById(R.id.feature_detail_news_layout);
		mHolder.mleadLayout = (RelativeLayout) view
				.findViewById(R.id.feature_item_lead_layout);
		mHolder.mMainItemLayout = (LinearLayout) view
				.findViewById(R.id.feature_item_layout);

		view.setTag(mHolder);

	}

	public void setDataList(ArrayList<ArticleInfo> news) {
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

	}

	private void showNewsByType(ViewHolder mHolder, ArticleInfo info,
			int position) {

		if (info.getType().equals(ArticleType.BOOK)) {
			Log.e("FEATURE", "BOOK DATA");
			mHolder.bookLayout.setVisibility(View.VISIBLE);
			mHolder.normalLayout.setVisibility(View.GONE);
			mHolder.bookTag.setVisibility(View.VISIBLE);

			mHolder.bookTitle.setText(info.getTitle());

			RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mHolder.bookTitleLayout.setLayoutParams(titleParams);
			mHolder.bookTitleLayout.setId(1);

			int width = Integer.parseInt(info.getWidth());
			int height = Integer.parseInt(info.getHeight());

			float scale = Float.parseFloat(mAdvWidth + "")
					/ Float.parseFloat(width + "");
			float imgHeight = scale * Float.parseFloat(height + "");
			int imageHeight = Math.round(imgHeight);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					mAdvWidth, imageHeight);
			layoutParams.addRule(RelativeLayout.BELOW, 1);
			layoutParams.topMargin = 6;
			mHolder.bookImg.setLayoutParams(layoutParams);
			mHolder.bookImg.setLayoutParams(layoutParams);

			if (isTextMode) {
				imageLoader.displayImage(imageUri, mHolder.bookImg, options);
			} else {
				imageLoader.displayImage(info.getImage(), mHolder.bookImg,
						options);
			}
		} else if (info.getType().equals(ArticleType.AD_LG)
				|| info.getType().equals(ArticleType.AD_MD)) {
			
			
			Log.e("FEATURE", "ADV DATA");
			mHolder.bookLayout.setVisibility(View.VISIBLE);
			mHolder.normalLayout.setVisibility(View.GONE);
			mHolder.bookTag.setVisibility(View.GONE);

			mHolder.bookTitle.setText(info.getTitle());

			RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			mHolder.bookTitleLayout.setLayoutParams(titleParams);
			mHolder.bookTitleLayout.setId(1);

			int width = Integer.parseInt(info.getWidth());
			int height = Integer.parseInt(info.getHeight());

			float scale = Float.parseFloat(mAdvWidth + "")
					/ Float.parseFloat(width + "");
			float imgHeight = scale * Float.parseFloat(height + "");
			int imageHeight = Math.round(imgHeight);

			RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
					mAdvWidth, imageHeight);
			layoutParams.addRule(RelativeLayout.BELOW, 1);
			layoutParams.topMargin = 6;
			mHolder.bookImg.setLayoutParams(layoutParams);
			mHolder.bookImg.setLayoutParams(layoutParams);

			if (isTextMode) {
				imageLoader.displayImage(imageUri, mHolder.bookImg, options);
			} else {
				imageLoader.displayImage(info.getImage(), mHolder.bookImg,
						options);
			}

		} else {
			mHolder.bookLayout.setVisibility(View.GONE);
			mHolder.normalLayout.setVisibility(View.VISIBLE);
			if (info.getType().equals(ArticleType.FEATURE)) {
				mHolder.newsTagImg
						.setImageResource(R.drawable.article_tag_market);
			} else if (info.getType().equals(ArticleType.LIVE)) {
				mHolder.newsTagImg
						.setImageResource(R.drawable.article_tag_live);
			} else if (info.getType().equals(ArticleType.MARKET)) {
				mHolder.newsTagImg
						.setImageResource(R.drawable.article_tag_broad);
			} else if (info.getType().equals(ArticleType.VIDEO)) {
				mHolder.newsTagImg
						.setImageResource(R.drawable.article_tag_video);
			}
			if (isTextMode) {

				imageLoader.displayImage(imageUri, mHolder.newsImg, options);
			} else {

				imageLoader.displayImage(info.getImage(), mHolder.newsImg,
						options);
			}
			// 根据文章的内容TYPE确定新闻的显示样式

			mHolder.newsTitle.setText(info.getTitle());
			mHolder.readNumText.setText(info.getMobile_click_count() + " 阅读");
		}

	}

	public void changeTheme(boolean isDay) {
		this.isDayTheme = isDay;
	}
}