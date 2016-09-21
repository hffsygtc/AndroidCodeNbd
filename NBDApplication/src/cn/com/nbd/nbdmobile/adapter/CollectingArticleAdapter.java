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
import cn.com.nbd.nbdmobile.adapter.SearchResultAdapter.ViewHolder;
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
public class CollectingArticleAdapter extends BaseAdapter {

//	private List<ArticleInfo> mNormalArticle = new ArrayList<ArticleInfo>(); // 一般的新闻文章
//	private List<ArticleInfo> mImageArticle = new ArrayList<ArticleInfo>(); // 图片的新闻文章
	private List<ArticleInfo> mArticles = new ArrayList<ArticleInfo>(); // 图片的新闻文章

	Activity activity;
	LayoutInflater inflater = null;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	private boolean isRightSection = false; // 是否是右边的图片收藏
	private boolean isTextMode;
	private String imageUri = "";
	private boolean isDayTheme;

	public static interface OnNewsClickListener {
		public void onNewsClick(View view, ArticleInfo article); // 传递数据给activity
	}

	OnNewsClickListener onNewsClick;

	public void setNewsClickListener(OnNewsClickListener onNewsClick) {
		this.onNewsClick = onNewsClick;

	}

	public CollectingArticleAdapter(Activity activity,
			ArrayList<ArticleInfo> mArticleInfos,boolean isText,boolean isDay) {
		this.activity = activity;
		this.isTextMode = isText;
		this.isDayTheme = isDay;
		inflater = LayoutInflater.from(activity);
		options = Options.getListOptions();

		if (mArticleInfos != null) {
			initData(mArticleInfos);
		}
	}

	/**
	 * 初始化数据，将文章新闻和图片新闻分开
	 * 
	 * @param mArticleInfos
	 */
	private void initData(ArrayList<ArticleInfo> mArticleInfos) {

//		for (int i = 0; i < mArticleInfos.size(); i++) {
//			ArticleInfo info = mArticleInfos.get(i);
//			if (info.getType().equals(ArticleType.IMAGE)) { // 图片新闻的收藏
//				mImageArticle.add(info);
//			} else {
//				mNormalArticle.add(info);
//			}
//		}
		if (mArticles == null) {
			mArticles = new ArrayList<ArticleInfo>();
		}
		mArticles.addAll(mArticleInfos);
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

			mHolder.mMainLayout = (LinearLayout) view.findViewById(R.id.collection_item_layout);
			mHolder.mMainNewsLayout = (RelativeLayout) view.findViewById(R.id.collection_news_layout);
			
			mHolder.normalNewsLayout = (RelativeLayout) view
					.findViewById(R.id.collection_normal_article_layout);
			mHolder.normalNewsTitle = (TextView) view
					.findViewById(R.id.collection_normal_article_content);
			mHolder.normalNewsComment = (TextView) view
					.findViewById(R.id.collection_normal_article_commonnum);

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

			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		
		showThemeUi(mHolder);

	   ArticleInfo info = getItem(position);

		// 如果是图片的收藏内容
		if (isRightSection) {
			mHolder.normalNewsLayout.setVisibility(View.GONE);
			mHolder.imageNewsLayout.setVisibility(View.VISIBLE);
			if (info != null) {
				mHolder.imageNewsTitle.setText(info.getTitle());
				mHolder.imageNewsComment.setText(info.getMobile_click_count() + " 阅读");

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
						imageLoader.displayImage(imageUri, mHolder.imageLeft,
								options);
						imageLoader.displayImage(imageUri, mHolder.imageMid,
								options);
						imageLoader.displayImage(imageUri, mHolder.imageRight,
								options);

					} else {
						imageLoader.displayImage(leftImg != null ?leftImg.getImage_src():imageUri,
								mHolder.imageLeft, options);
						imageLoader.displayImage(midImg != null ?midImg.getImage_src():imageUri,
								mHolder.imageMid, options);
						imageLoader.displayImage(rightImg != null ?rightImg.getImage_src():imageUri,
								mHolder.imageRight, options);
					}

				} else {
					Log.e("ThreePic", "====>NULL");
				}
				
				mHolder.imageNewsLayout.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						onNewsClick.onNewsClick(null, getItem(position));
						
					}
				});
				
			}
		} else {
			mHolder.normalNewsLayout.setVisibility(View.VISIBLE);
			mHolder.imageNewsLayout.setVisibility(View.GONE);
			
			mHolder.normalNewsTitle.setText(info.getTitle());
			mHolder.normalNewsComment.setText(info.getMobile_click_count() + " 阅读");
			
			mHolder.normalNewsLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					onNewsClick.onNewsClick(null, getItem(position));
					
				}
			});

		}

		return view;
	}
	
	

	static class ViewHolder {
		LinearLayout mMainLayout;
		RelativeLayout mMainNewsLayout;
		
		
		// 一般新闻的布局
		RelativeLayout normalNewsLayout;
		// 一般文章的标题
		TextView normalNewsTitle;
		// 一般文章的评论
		TextView normalNewsComment;

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

	}
	
	private void showThemeUi(ViewHolder mHolder) {
		if (isDayTheme) { // 日间配色
			mHolder.mMainLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.day_item_background));
			mHolder.mMainNewsLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.day_item_background));
			mHolder.normalNewsTitle.setTextColor(activity.getResources()
					.getColor(R.color.day_black));
			mHolder.imageNewsTitle.setTextColor(activity.getResources().getColor(
					R.color.day_black));

		} else {
			mHolder.mMainLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.night_section_background));
			mHolder.mMainNewsLayout.setBackgroundColor(activity.getResources()
					.getColor(R.color.night_section_background));
			mHolder.normalNewsTitle.setTextColor(activity.getResources()
					.getColor(R.color.night_black));
			mHolder.imageNewsTitle.setTextColor(activity.getResources().getColor(
					R.color.night_black));

		}
	}
	

	/**
	 * 设置新闻内容大于三行时的点击展开功能
	 * 
	 * @param position
	 */
	public void checkeContentOpen(int position) {

	}

	public void setDataChange(ArrayList<ArticleInfo> articleInfos) {
		if (mArticles != null && mArticles.size()>0) {
			mArticles.clear();
		}
		
		initData(articleInfos);
	}
	
	/**
	 * 更改显示的左右的栏目内容
	 * @param section
	 */
	public void changeShowSection(int section){
		if (section == 0) {
			isRightSection = false;
		}else {
			isRightSection = true;
		}
	}
	
	public void changeTheme(boolean isDay){
		this.isDayTheme = isDay;
	}

}
