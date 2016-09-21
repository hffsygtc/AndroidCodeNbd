/*
 * Copyright 2013 David Schreiber
 *           2013 John Paul Nalog
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package cn.com.nbd.nbdmobile.adapter;

import java.util.ArrayList;
import java.util.List;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.Options;
import cn.com.nbd.nbdmobile.view.FancyCoverFlow;
import cn.com.nbd.nbdmobile.view.FancyCoverFlow.LayoutParams;

import com.nbd.article.bean.NewspaperImage;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 报纸画廊适配器
 * 
 * @author riche
 * 
 */

public class MainPaperCoverFlowAdapter extends BaseAdapter {

	private LayoutInflater inflater = null;
	private Activity activity;
	private List<NewspaperImage> imagesList;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	private List<View> mHolderList = new ArrayList<View>();
	private List<Integer> mPositionList = new ArrayList<Integer>();

	DisplayImageOptions options;

	private ImageView paperImg;
	private TextView paperTitle;
	
	private boolean isTextMode;
	private String imageUri = ""; 

//	public interface onDailyNewsClickListener {
//		void onDailyNewsChecked(int position);
//	}
//
//	private onDailyNewsClickListener mListener;

	public MainPaperCoverFlowAdapter(Activity activity,
			List<NewspaperImage> images,boolean isText) {
		this.activity = activity;
		this.inflater = LayoutInflater.from(activity);
		this.imagesList = images;
		this.isTextMode = isText;

		options = Options.getPaperOptions();
	}

	@Override
	public final View getView(final int position, View convertView,
			ViewGroup viewGroup) {

		Log.e("COVER===>", "position==>" + position + "view==?>" + convertView);

		// 父类画廊的控件
		FancyCoverFlow coverFlow = (FancyCoverFlow) viewGroup;

		View view = null;

		if (convertView != null) {
			view = convertView;
		} else {
			view = inflater.inflate(R.layout.paper_item_layout, null);
			view.setLayoutParams(new FancyCoverFlow.LayoutParams(633, 1008));
//			view.setLayoutParams(new FancyCoverFlow.LayoutParams(dip2px(
//					activity, 211), dip2px(activity, 336)));
			paperImg = (ImageView) view.findViewById(R.id.paper_item_paper_img);
			paperTitle = (TextView) view
					.findViewById(R.id.paper_item_title_txt);

		}

		paperTitle.setText(imagesList.get(position).getSection());
		if (isTextMode) {
			
			imageLoader.displayImage(imageUri,
					paperImg, options);
		}else {
			
			imageLoader.displayImage(getItem(position).getSection_avatar(),
					paperImg, options);
		}

//		paperImg.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				mListener.onDailyNewsChecked(position);
//			}
//		});

		return view;
	}

	@Override
	public int getCount() {
		Log.e("cover adapter size-->	", ""+imagesList.size());
		return imagesList.size();
	}

	@Override
	public NewspaperImage getItem(int position) {
		return imagesList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public void setDataChange(ArrayList<NewspaperImage> scrolllists) {
		Log.e("cover adapter-->", scrolllists.size()+"");
		this.imagesList = scrolllists;
		
		Log.e("cover adapter-->", imagesList.size()+"");
	}
	
	public void changeTextMode(boolean isText){
		this.isTextMode = isText;
	}

//	public void setOnDailyNewsClickListener(onDailyNewsClickListener listener) {
//		this.mListener = listener;
//	}
}
