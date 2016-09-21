package cn.com.nbd.nbdmobile.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.R.id;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;

import com.nbd.article.bean.FeatureInfo;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

//import com.nbd.view.HeadListView.HeaderAdapter;

/**
 * 活动页面的adapter
 * 
 * @author riche
 */
public class MainFeatureAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	private List<FeatureInfo> mFeatures = new ArrayList<FeatureInfo>();

	private Activity activity;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	DisplayImageOptions options;

	private boolean isDayTheme;
	private boolean isTextMode;
	private String imageUri = ""; // from

	private Map<Viewholder, String> imgMap = new HashMap<MainFeatureAdapter.Viewholder, String>();

	public MainFeatureAdapter(Activity activity, List<FeatureInfo> lists,
			boolean isDay, boolean isText) {
		this.activity = activity;
		this.mFeatures = lists;
		this.isDayTheme = isDay;
		this.isTextMode = isText;
		this.inflater = LayoutInflater.from(activity);
		options = getListOptions();

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mFeatures.size();
	}

	@Override
	public FeatureInfo getItem(int position) {
		// TODO Auto-generated method stub
		return mFeatures.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final Viewholder mHolder;
		View view = convertView;

		if (view == null) {
			view = inflater.inflate(R.layout.item_feature_list, null);
			mHolder = new Viewholder();
			// 初始化holder控件
			mHolder.itemLayout = (RelativeLayout) view
					.findViewById(R.id.feature_item_layout);
			mHolder.featureImg = (ImageView) view
					.findViewById(R.id.feature_item_img);
			mHolder.featureContent = (TextView) view.findViewById(R.id.feature_item_content);
			view.setTag(mHolder);
		} else {
			mHolder = (Viewholder) view.getTag();
		}

		setThemeColor(mHolder, isDayTheme);
		// View view = inflater.inflate(R.layout.feature_list_item, null);
		// ImageView img = (ImageView) view.findViewById(R.id.feature_item_img);
		//
		if (getItem(position) != null) {

			if (isTextMode) {

//				if (imgMap.get(mHolder) != null
//						&& imgMap.get(mHolder).equals(imageUri)) {
//
//				} else {
					imgMap.put(mHolder, imageUri);
					imageLoader.displayImage(imageUri, mHolder.featureImg,
							options);
//				}
			} else {

				if (imgMap.get(mHolder) != null
						&& imgMap.get(mHolder).equals(
								getItem(position).getAvatar())) {

				} else {
					imgMap.put(mHolder, getItem(position).getAvatar());
					imageLoader.displayImage(getItem(position).getAvatar(),
							mHolder.featureImg, options);
				}
			}
			
			mHolder.featureContent.setText(getItem(position).getLead());
		}

		return view;
	}

	/**
	 * 根据日间模式或者夜间模式切换ITEM的显示方式
	 * 
	 * @param mHolder
	 * @param isDayTheme2
	 */
	private void setThemeColor(Viewholder mHolder, boolean isDayTheme) {

		if (isDayTheme) {
			ThemeUtil.setBackgroundDay(activity, mHolder.itemLayout);
			ThemeUtil.setTextColorDay(activity, mHolder.featureContent);

		} else {
			ThemeUtil.setBackgroundNight(activity, mHolder.itemLayout);
			ThemeUtil.setTextColorNight(activity, mHolder.featureContent);
		}

	}

	public void setDataChange(ArrayList<FeatureInfo> lists) {
		this.mFeatures = lists;

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

	static class Viewholder {
		RelativeLayout itemLayout;
		ImageView featureImg;
		TextView featureContent;
	}

	public void changeTheme(boolean isDay) {
		this.isDayTheme = isDay;
	}

	public void changeMode(boolean isText) {
		this.isTextMode = isText;
	}
}
