package cn.com.nbd.nbdmobile.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.graphics.Bitmap;
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
import com.nbd.article.bean.ActivityBean;
import com.nbd.article.bean.ActivityListBean;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.FeatureDetail;
import com.nbd.article.bean.FeatureLable;
import com.nbd.article.bean.StockIndex;
//import com.nbd.view.HeadListView;
//import com.nbd.view.HeadListView.HeaderAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 活动中心的适配器
 * 
 * @author riche
 */
public class SignCenterAdapter extends BaseAdapter {

	Activity activity;
	LayoutInflater inflater = null;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	DisplayImageOptions circleOptions;
	DisplayImageOptions options;

	private Map<ViewHolder, String> imgMap = new HashMap<SignCenterAdapter.ViewHolder, String>();

	private List<String> mLabes; // 标签列表
	private List<ActivityBean> mActivityBeans; // 不同标签下的活动列表
	private List<ActivityListBean> mAllDatas; //所有的全部数据
	private List<Integer> mTagFirstPosition; // 显示标题的item位置列表

	private String mHeadImg; // 顶部主题图片
	private String mHeadTitle; // 顶部标题
	private int mHeadState; //顶部报名的状态
	private String mHeadUrl; //顶部报名的地址

	private boolean isDayTheme;
	private boolean isTextMode;
	private String imageUri = "";

	public static interface OnNewsClickListener {
		public void onNewsClick(int position, String url,String title); // 传递数据给activity
	}

	OnNewsClickListener onNewsClick;

	public void setNewsClickListener(OnNewsClickListener onNewsClick) {
		this.onNewsClick = onNewsClick;
	}
	

	public SignCenterAdapter(Activity activity, List<ActivityListBean> allDatas,boolean isDay,boolean isText) {
		this.activity = activity;
		inflater = LayoutInflater.from(activity);
		options = Options.getListOptions();
		circleOptions = getListOptions();

		this.isDayTheme = isDay;
		this.mAllDatas = allDatas;
		this.isTextMode = isText;

		dealFeatureData(mAllDatas);

	}

	/**
	 * 处理传入的文章详情数据
	 */
	private void dealFeatureData(List<ActivityListBean> mAllDatas) {
		
		if (mTagFirstPosition == null) {
		mTagFirstPosition = new ArrayList<Integer>();
	}

	if (mLabes == null) {
		mLabes = new ArrayList<String>();
	}

	if (mActivityBeans == null) {
		mActivityBeans = new ArrayList<ActivityBean>();
	}
		int tempPosition = 1;
		
		for (int i = 0; i < mAllDatas.size(); i++) {
			ActivityListBean typeBean = mAllDatas.get(i);
			if (typeBean.getCategory().equals("头图")) {
				
				if (typeBean.getLists() != null && typeBean.getLists().size()>0) {
					ActivityBean headBean = typeBean.getLists().get(0);
					mHeadImg = headBean.getImage();
					mHeadTitle = headBean.getTitle();
					mHeadUrl = headBean.getUrl();
					mHeadState = headBean.getStatus();
				}
			}else {
				mTagFirstPosition.add(tempPosition);
				mLabes.add(typeBean.getCategory());
				
				int size = typeBean.getLists().size();
				tempPosition = tempPosition + size;
				
				mActivityBeans.addAll(typeBean.getLists());
			}
		}
	}

	@Override
	public int getCount() {
		return mActivityBeans.size() + 1;
	}

	@Override
	public ActivityBean getItem(int position) {

		return mActivityBeans.get(position - 1);
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
			view = inflater.inflate(R.layout.sign_center_item, null);
			mHolder = new ViewHolder();
			// 初始化holder控件
			initHolder(mHolder, view);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		
		showThemeUi(mHolder);

		// 第一项导语部分
		if (position == 0) {
			mHolder.headLayout.setVisibility(View.VISIBLE);
			mHolder.ItemLayout.setVisibility(View.GONE);
			
			if (isTextMode) {
				imageLoader.displayImage(imageUri, mHolder.headImg, options);
			}else {
				imageLoader.displayImage(mHeadImg, mHolder.headImg, options);
			}
			mHolder.headTitle.setText(mHeadTitle);
			mHolder.headLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (onNewsClick != null) {
						onNewsClick.onNewsClick(position, mHeadUrl,mHeadTitle);
					}
				}
			});
			
			if (mHeadState == 1) {
				mHolder.headTag.setText("火热报名中");
			}else {
				mHolder.headTag.setText("敬请期待");
			}
			
		}  else {
			mHolder.headLayout.setVisibility(View.GONE);
			mHolder.ItemLayout.setVisibility(View.VISIBLE);

			String headString = IsFristNewsItem(position);

			if (headString != null) {
				mHolder.itemTagLayout.setVisibility(View.VISIBLE);
				mHolder.itemTagText.setText(headString);

			} else {
				mHolder.itemTagLayout.setVisibility(View.GONE);
			}

			showNewsByType(mHolder, getItem(position), position);

			mHolder.ItemLayout.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (onNewsClick != null) {
						onNewsClick.onNewsClick(position, getItem(position).getUrl(),getItem(position).getTitle());
					}
				}
			});
		}
		

		return view;
	}

	@SuppressWarnings("deprecation")
	private void showThemeUi(ViewHolder mHolder) {
	}

	
	/**
	 * 获取是不是第一个新闻的标志，是的话就显示内容
	 * 
	 * @param position
	 * @return
	 */
	private String IsFristNewsItem(int position) {

		for (int i = 0; i < mTagFirstPosition.size(); i++) {
			Log.e("POSITION", "" + mTagFirstPosition.get(i) + mLabes.get(i));
			if (position == mTagFirstPosition.get(i)) {
				return mLabes.get(i);
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

		RelativeLayout headLayout;
		ImageView headImg;
		TextView headTag;
		TextView headTitle;
		
		RelativeLayout ItemLayout;
		TextView itemTopGap;
		RelativeLayout itemTagLayout;
		TextView itemTagText;
		
		ImageView itemImg;
		TextView itemContent;
		
	
	}

	/**
	 * 初始化Holder和控件
	 * 
	 * @param mHolder
	 * @param view
	 */
	private void initHolder(ViewHolder mHolder, View view) {
		mHolder.headLayout = (RelativeLayout) view.findViewById(R.id.sign_center_head_layout);
		mHolder.headImg = (ImageView) view.findViewById(R.id.sign_center_head_img);
		mHolder.headTag = (TextView) view.findViewById(R.id.sign_center_bottom_tag);
		mHolder.headTitle = (TextView) view.findViewById(R.id.sign_center_bottom_title);
		
		mHolder.ItemLayout = (RelativeLayout) view.findViewById(R.id.sign_center_item_layout);
		mHolder.itemTopGap = (TextView) view.findViewById(R.id.sign_center_tags_top_div);
		mHolder.itemTagLayout = (RelativeLayout) view.findViewById(R.id.sign_center_tag_layout);
		mHolder.itemTagText = (TextView) view.findViewById(R.id.sign_center_news_tag_txt);
		
		mHolder.itemImg = (ImageView) view.findViewById(R.id.sign_center_activity_img);
		mHolder.itemContent = (TextView) view.findViewById(R.id.sign_center_activity_content);
		
		view.setTag(mHolder);

	}

	public void setDataList(ArrayList<ArticleInfo> news) {
	}

	/**
	 * 设置数据的更新
	 * 
	 */
	public void setDataChange(ArrayList<ArticleInfo> scrolllists,
			ArrayList<StockIndex> stocks, ArrayList<ArticleInfo> lists) {

	}

	private void showNewsByType(ViewHolder mHolder, ActivityBean info,
			int position) {
		Log.e("GETVIEW SHOW==>", "" + position);
		
			if (isTextMode) {
				
				imageLoader.displayImage(imageUri, mHolder.itemImg, circleOptions);
			}else {
				
				imageLoader.displayImage(info.getImage(), mHolder.itemImg, circleOptions);
			}
			// 根据文章的内容TYPE确定新闻的显示样式
			
			mHolder.itemContent.setText(info.getDesc());

	}

	
	public void changeTheme(boolean isDay){
		this.isDayTheme = isDay;
	}
	
	
	private DisplayImageOptions getListOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		// 设置图片在下载期间显示的图片
				.showImageOnLoading(R.drawable.nbd_test_bg)
				// 设置图片Uri为空或是错误的时候显示的图片
				.showImageForEmptyUri(R.drawable.nbd_test_bg)
				// 设置图片加载/解码过程中错误时候显示的图片
				.showImageOnFail(R.drawable.nbd_test_bg).cacheInMemory(false)
				// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)
				// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true)
				// .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)//
				// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
				// .decodingOptions(android.graphics.BitmapFactory.Options
				// decodingOptions)//设置图片的解码配置
				.considerExifParams(true)
				// 设置图片下载前的延迟
				// .delayBeforeLoading(int delayInMillis)//int
				// delayInMillis为你设置的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// 。preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.displayer(new RoundedBitmapDisplayer(20, 0))
				// .displayer(new RoundedBitmapDisplayer(80))
				// .displayer(new FadeInBitmapDisplayer(100))// 淡入
				.build();
		return options;
	}

}