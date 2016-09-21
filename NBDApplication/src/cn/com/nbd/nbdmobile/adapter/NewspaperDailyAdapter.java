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

import cn.com.nbd.nbdmobile.activity.DailyNewsDetailActivity;
import cn.com.nbd.nbdmobile.adapter.NewsListAdapter.ViewHolder;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.Options;

import com.nbd.article.article.ArticleType;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.FeatureDetail;
import com.nbd.article.bean.FeatureLable;
import com.nbd.article.bean.NewspaperDailyBean;
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
public class NewspaperDailyAdapter extends BaseAdapter {

	Activity activity;
	LayoutInflater inflater = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	DisplayImageOptions options;

	private List<NewspaperDailyBean> mDailyNewsList;

	private List<ArticleInfo> mArticleInfos; // 不同标签下的文章列表

	private List<Integer> mTagFirstPosition; // 显示标题的item位置列表
	private List<String> mTagList; // 显示新闻小标题的内容列表
	
	private boolean isDayTheme;

	public static interface OnNewsClickListener {
		public void onNewsClick(View view, ArticleInfo article); // 传递数据给activity
	}

	private OnNewsClickListener onNewsClick;
	
	public void setOnNewsClickListener(OnNewsClickListener listener){
		this.onNewsClick = listener;
	}

	public NewspaperDailyAdapter(Activity activity,
			List<NewspaperDailyBean> dailyBeans,List<String> titles,boolean isDay) {
		this.activity = activity;
		inflater = LayoutInflater.from(activity);
		options = Options.getListOptions();
		this.mTagList = titles;
		this.isDayTheme = isDay;

		this.mDailyNewsList = dailyBeans;

		dealFeatureData(mDailyNewsList);

	}

	/**
	 * 处理传入的文章详情数据
	 */
	private void dealFeatureData(List<NewspaperDailyBean> dailyNews) {

		if (mTagFirstPosition == null) {
			mTagFirstPosition = new ArrayList<Integer>();
		}

		if (mTagList == null) {
			mTagList = new ArrayList<String>();
		}

		if (mArticleInfos == null) {
			mArticleInfos = new ArrayList<ArticleInfo>();
		}

		int temptPosition = 0;
		// 根据标签的列表，和每个标签里面的文章个数，解析数据
		for (int i = 0; i < dailyNews.size(); i++) {
			mTagFirstPosition.add(temptPosition);

			int articleSize = dailyNews.get(i).getArticles().size();
			temptPosition = temptPosition + articleSize;
			mArticleInfos.addAll(dailyNews.get(i).getArticles());
		}

		Log.e("FEATURE-DETAIL==>", "TAG==>" + mTagFirstPosition.size() + "=="
				+ mTagList.size() + "==" + mArticleInfos.size());

	}

	@Override
	public int getCount() {
		return mArticleInfos.size();
	}

	@Override
	public ArticleInfo getItem(int position) {

		return mArticleInfos.get(position);
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
			view = inflater.inflate(R.layout.item_daily_news, null);
			mHolder = new ViewHolder();
			// 初始化holder控件
			initHolder(mHolder, view);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		
		showThemeUi(mHolder);
		
		String tagString = IsFristNewsItem(position);
		if (tagString != null) {
			mHolder.tagLayout.setVisibility(View.VISIBLE);
			mHolder.tagText.setText(tagString);
		}else {
			mHolder.tagLayout.setVisibility(View.GONE);
		}

		
		mHolder.newsTitle.setText(getItem(position).getTitle());
//		showNewsByType(mHolder, getItem(position), position);

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



	private void showThemeUi(ViewHolder mHolder) {
		if (isDayTheme) {
			mHolder.mMainLayout.setBackgroundColor(activity.getResources().getColor(R.color.day_section_background));
			mHolder.newsLayout.setBackgroundColor(activity.getResources().getColor(R.color.day_item_background));
			mHolder.mTagDiv.setBackgroundColor(activity.getResources().getColor(R.color.day_section_background));
			mHolder.newsTitle.setTextColor(activity.getResources().getColor(R.color.day_black));
		}else {
			mHolder.mMainLayout.setBackgroundColor(activity.getResources().getColor(R.color.night_common_background));
			mHolder.newsLayout.setBackgroundColor(activity.getResources().getColor(R.color.night_section_background));
			mHolder.mTagDiv.setBackgroundColor(activity.getResources().getColor(R.color.night_common_background));
			mHolder.newsTitle.setTextColor(activity.getResources().getColor(R.color.night_black));
			
			
		}
		
		
	}

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
		LinearLayout mMainLayout;
		TextView mTagDiv;

		// 新闻的内容布局
		RelativeLayout newsLayout;
	
		// 新闻标签栏
		RelativeLayout tagLayout;
		// 标签栏里面的文字
		TextView tagText;
		// 新闻的标题
		TextView newsTitle;

	}

	/**
	 * 初始化Holder和控件
	 * 
	 * @param mHolder
	 * @param view
	 */
	private void initHolder(ViewHolder mHolder, View view) {
		
		mHolder.mMainLayout = (LinearLayout) view.findViewById(R.id.dailynews_item_layout);
		mHolder.mTagDiv = (TextView) view.findViewById(R.id.dailynews_tags_btm_div);
		
		mHolder.newsLayout = (RelativeLayout) view.findViewById(R.id.dailynewsl_news_layout);
		mHolder.tagLayout = (RelativeLayout) view.findViewById(R.id.dailynews_news_tag_layout);
		mHolder.tagText = (TextView) view.findViewById(R.id.dailynews_news_tag_txt);
		mHolder.newsTitle = (TextView) view.findViewById(R.id.dailynews_article_content);

		view.setTag(mHolder);

	}
	
	public void changeTheme(boolean isDay){
		this.isDayTheme = isDay;
	}

}