package cn.com.nbd.nbdmobile.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.activity.SearchResultActivity;
import cn.com.nbd.nbdmobile.utility.Options;

import com.nbd.article.article.ArticleType;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.MyMessageBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
//import com.nbd.view.HeadListView;
//import com.nbd.view.HeadListView.HeaderAdapter;

/**
 * 收藏内容的适配器
 * 
 * @author riche
 */
public class SearchResultAdapter extends BaseAdapter {

	private List<ArticleInfo> mArticles = new ArrayList<ArticleInfo>(); // 一般的新闻文章

	Activity activity;
	LayoutInflater inflater = null;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	private boolean isReadingPage; // 是否是阅读的页面

	private boolean isDayTheme;

	public static interface OnNewsClickListener {
		public void onNewsClick(View view, ArticleInfo article); // 传递数据给activity
	}

	OnNewsClickListener onNewsClick;

	public void setNewsClickListener(OnNewsClickListener onNewsClick) {
		this.onNewsClick = onNewsClick;

	}

	public SearchResultAdapter(Activity activity,
			ArrayList<ArticleInfo> mArticleInfos, boolean isReadingPage,
			boolean isDay) {
		this.activity = activity;
		inflater = LayoutInflater.from(activity);
		options = Options.getListOptions();
		this.isReadingPage = isReadingPage;
		this.mArticles = mArticleInfos;
		this.isDayTheme = isDay;

	}

	@Override
	public int getCount() {
		return mArticles.size();
	}

	@Override
	public ArticleInfo getItem(int position) {
		return mArticles.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder mHolder = null;
		View view = convertView;

		if (view == null) {
			view = inflater.inflate(R.layout.item_collection_article, null);
			mHolder = new ViewHolder();

			mHolder.mMainItemLayout = (LinearLayout) view
					.findViewById(R.id.collection_item_layout);
			mHolder.mMainNewsLayout = (RelativeLayout) view
					.findViewById(R.id.collection_news_layout);

			mHolder.normalNewsLayout = (RelativeLayout) view
					.findViewById(R.id.collection_normal_article_layout);
			mHolder.normalNewsTitle = (TextView) view
					.findViewById(R.id.collection_normal_article_content);
			mHolder.normalNewsComment = (TextView) view
					.findViewById(R.id.collection_normal_article_commonnum);
			mHolder.normalArticleTag = (ImageView) view
					.findViewById(R.id.collection_normal_article_tag);

			mHolder.imageNewsLayout = (RelativeLayout) view
					.findViewById(R.id.collection_threepic_article_layout);
			mHolder.imageNewsTitle = (TextView) view
					.findViewById(R.id.collection_threepic_article_title);
			mHolder.imageLeft = (ImageView) view
					.findViewById(R.id.collection_threepic_left);
			mHolder.imageMid = (ImageView) view
					.findViewById(R.id.collection_threepic_middle);
			mHolder.imageRight = (ImageView) view
					.findViewById(R.id.collection_threepic_right);
			mHolder.imageNewsComment = (TextView) view
					.findViewById(R.id.collection_threepic_article_commonnum);

			mHolder.readingLayout = (RelativeLayout) view
					.findViewById(R.id.reading_article_layout);
			mHolder.readingTitle = (TextView) view
					.findViewById(R.id.reading_article_content);

			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}

		showThemeUi(mHolder, isDayTheme);

		ArticleInfo info = getItem(position);

		if (isReadingPage) {
			mHolder.normalNewsLayout.setVisibility(View.GONE);
			mHolder.imageNewsLayout.setVisibility(View.GONE);
			mHolder.readingLayout.setVisibility(View.VISIBLE);

			mHolder.readingTitle.setText(info.getTitle());

			mHolder.readingLayout
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							onNewsClick.onNewsClick(null, getItem(position));

						}
					});

		} else {
			mHolder.normalNewsLayout.setVisibility(View.VISIBLE);
			mHolder.imageNewsLayout.setVisibility(View.GONE);
			mHolder.readingLayout.setVisibility(View.GONE);

			mHolder.normalNewsTitle.setText(info.getTitle());
			mHolder.normalNewsComment.setText(info.getMobile_click_count()
					+ " 阅读");

			if (info.getType().equals(ArticleType.IMAGE)) {
				mHolder.normalArticleTag.setVisibility(View.VISIBLE);
			} else {
				mHolder.normalArticleTag.setVisibility(View.GONE);
			}

			mHolder.normalNewsLayout
					.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							onNewsClick.onNewsClick(null, getItem(position));

						}
					});
		}

		return view;
	}

	private void showThemeUi(ViewHolder mHolder, boolean isDay) {
		if (isDay) { // 日间配色
			mHolder.mMainItemLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.day_item_background));
			mHolder.mMainNewsLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.day_item_background));
			mHolder.normalNewsTitle.setTextColor(activity.getResources()
					.getColor(R.color.day_black));
			mHolder.readingTitle.setTextColor(activity.getResources().getColor(
					R.color.day_black));

		} else {
			mHolder.mMainItemLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.night_section_background));
			mHolder.mMainNewsLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.night_section_background));
			mHolder.normalNewsTitle.setTextColor(activity.getResources()
					.getColor(R.color.night_black));
			mHolder.readingTitle.setTextColor(activity.getResources().getColor(
					R.color.night_black));

		}

		// mCover.setBackgroundColor(SearchResultActivity.this.getResources().getColor(R.color.day_cover));

	}

	static class ViewHolder {
		LinearLayout mMainItemLayout;
		RelativeLayout mMainNewsLayout;

		// 一般新闻的布局
		RelativeLayout normalNewsLayout;
		// 一般文章的标题
		TextView normalNewsTitle;
		// 一般文章的评论
		TextView normalNewsComment;
		ImageView normalArticleTag;

		// 图片新闻的布局
		RelativeLayout imageNewsLayout;
		// 图片新闻的标题
		TextView imageNewsTitle;
		// 图片新闻的三张图片
		ImageView imageLeft;
		ImageView imageMid;
		ImageView imageRight;
		// 图片新闻的评论
		TextView imageNewsComment;

		RelativeLayout readingLayout;
		TextView readingTitle;

	}

	/**
	 * 设置新闻内容大于三行时的点击展开功能
	 * 
	 * @param position
	 */
	public void checkeContentOpen(int position) {

	}

	public void setDataChange(ArrayList<ArticleInfo> articleInfos) {

		mArticles = articleInfos;
	}

	public void changeTheme(boolean isDay) {
		this.isDayTheme = isDay;
	}
}
