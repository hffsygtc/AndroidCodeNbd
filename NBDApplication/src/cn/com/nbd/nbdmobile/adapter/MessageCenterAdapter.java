package cn.com.nbd.nbdmobile.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.Options;
import cn.com.nbd.nbdmobile.view.CustomListViewUnused;
import cn.com.nbd.nbdmobile.view.CustomListViewUnused.HeaderAdapter;
import cn.sharesdk.wechat.utils.m;

import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.MyMessageBean;
//import com.nbd.view.HeadListView;
//import com.nbd.view.HeadListView.HeaderAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 消息中心的内容适配器
 * 
 * @author riche
 */
public class MessageCenterAdapter extends BaseAdapter{

	ArrayList<MyMessageBean> myMsgList;
	Activity activity;
	LayoutInflater inflater = null;

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	DisplayImageOptions options;
	
	private boolean isLeftSection = false;
	private boolean isDayTheme;
	
	
	public static interface OnNewsClickListener {
		public void onNewsClick(View view, ArticleInfo article); // 传递数据给activity
	}
	OnNewsClickListener onNewsClick;
	
	public void setNewsClickListener(OnNewsClickListener onNewsClick) {
		this.onNewsClick = onNewsClick;

	}

	public MessageCenterAdapter(Activity activity, ArrayList<MyMessageBean> myMsgList,boolean isDay) {
		this.activity = activity;
		this.myMsgList = myMsgList;
		this.isDayTheme = isDay;
		inflater = LayoutInflater.from(activity);
		options = Options.getListOptions();
	}

	@Override
	public int getCount() {
		if (isLeftSection) {
			
		}else {
			
			return myMsgList == null ? 0 : myMsgList.size();
		}
		
		return 0 ;
	}

	@Override
	public Object getItem(int position) {
		if (isLeftSection) {
			
		}else {
			
			if (myMsgList != null && myMsgList.size() != 0) {
				return myMsgList.get(position);
			}
		}
		
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		

		if (isLeftSection) {
			
		}else {
			RightViewHolder mHolder = null;
			View view = convertView;
			
			if (view == null) {
				view = inflater.inflate(R.layout.item_message_center_layout, null);
				mHolder = new RightViewHolder();
				mHolder.mMessageLayout = (LinearLayout) view.findViewById(R.id.message_item_layout);
				mHolder.mUserNameTextView = (TextView) view.findViewById(R.id.message_item_user_name_text);
				mHolder.mTimeTextView = (TextView) view.findViewById(R.id.message_item_time_text);
				mHolder.mthreeLineTextView = (TextView) view.findViewById(R.id.message_item_content_txt);
				mHolder.mLinesTextView = (TextView) view.findViewById(R.id.message_item_content_txt_lines);
				
				view.setTag(mHolder);
			} else {
				mHolder = (RightViewHolder) view.getTag();
			}
			
			showThemUi(mHolder);
			
			
			// 获取position对应的数据
			MyMessageBean msgBean = (MyMessageBean) getItem(position);
			
			mHolder.mLinesTextView.setVisibility(View.GONE);
			mHolder.mUserNameTextView.setText(msgBean.getNotifier());
			mHolder.mTimeTextView.setText(msgBean.getDate_format());
			mHolder.mthreeLineTextView.setText(msgBean.getNotify());
			
			
			
			return view;
		}
		return null;
	}

	
	
	private void showThemUi(RightViewHolder mHolder) {
		if (isDayTheme) {
			mHolder.mMessageLayout.setBackgroundColor(activity.getResources().getColor(R.color.day_item_background));
			mHolder.mUserNameTextView.setTextColor(activity.getResources().getColor(R.color.day_black));
		}else {
			mHolder.mMessageLayout.setBackgroundColor(activity.getResources().getColor(R.color.night_section_background));
			mHolder.mUserNameTextView.setTextColor(activity.getResources().getColor(R.color.night_black));
			
		}
		
	}



	static class RightViewHolder {
		LinearLayout mMessageLayout;
		
		TextView mUserNameTextView;
		TextView mTimeTextView;
		TextView mthreeLineTextView;
		TextView mLinesTextView;
	}


	/**
	 * 设置新闻内容大于三行时的点击展开功能
	 * 
	 * @param position
	 */
	public void checkeContentOpen(int position) {


	}



	public void setDataChange(ArrayList<MyMessageBean> msg) {
		this.myMsgList = msg;
	}

	public void changeTheme(boolean isDay){
		this.isDayTheme = isDay;
	}

}
