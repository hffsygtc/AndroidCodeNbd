package cn.com.nbd.nbdmobile.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import android.R.integer;
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
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.view.CustomListViewUnused;
import cn.com.nbd.nbdmobile.view.CustomListViewUnused.HeaderAdapter;

import com.nbd.article.bean.ArticleInfo;
//import com.nbd.view.HeadListView;
//import com.nbd.view.HeadListView.HeaderAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 滚动新闻的适配器，单独的纯文字列表 长新闻的点击展开功能 下拉上拉刷新集成
 * 
 * @author riche
 */
public class NewsFastAdapter extends BaseAdapter implements SectionIndexer,
		HeaderAdapter {

	ArrayList<ArticleInfo> newsList;
	Activity activity;
	LayoutInflater inflater = null;

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	DisplayImageOptions options;
	private int isPullDown;
	private boolean isDayTheme;
	
	
	public static interface OnNewsClickListener {
		public void onNewsClick(View view, ArticleInfo article); // 传递数据给activity
	}
	OnNewsClickListener onNewsClick;
	
	public void setNewsClickListener(OnNewsClickListener onNewsClick) {
		this.onNewsClick = onNewsClick;

	}

	public NewsFastAdapter(Activity activity, ArrayList<ArticleInfo> newsList,boolean isDayTheme) {
		this.activity = activity;
		this.newsList = newsList;
		inflater = LayoutInflater.from(activity);
		options = Options.getListOptions();
		this.isDayTheme = isDayTheme;
		initDateHead();
	}

	private List<Integer> mPositions;
	private List<String> mSections;

	private void initDateHead() {
		if (mSections != null || mPositions != null) {
			mSections.clear();
			mPositions.clear();
		}else {
			mSections = new ArrayList<String>();
			mPositions = new ArrayList<Integer>();
		}
		String temptData = null ;
		if (newsList != null) {
			for (int i = 0; i < newsList.size(); i++) {
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yyyy年MM月dd日");
				
				long intTime = Long.parseLong(newsList.get(i).getCreated_at());
				String time = simpleDateFormat.format(new Date(intTime));
				
				if (i == 0) {
					mSections.add(time);
					mPositions.add(i);
					temptData = time;
					continue;
				}else if (time.equals(temptData)) {
					continue;
				} else {
					temptData = time;
					mSections.add(time);
					mPositions.add(i);
				}
			}
		}
	}

	@Override
	public int getCount() {
		return newsList == null ? 0 : newsList.size();
	}

	@Override
	public ArticleInfo getItem(int position) {
		if (newsList != null && newsList.size() != 0) {
			return newsList.get(position);
		}
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder mHolder;
		View view = convertView;

		if (view == null) {
			view = inflater.inflate(R.layout.item_main_news_fast, null);
			mHolder = new ViewHolder();
			mHolder.itemLayout = (LinearLayout) view
					.findViewById(R.id.kx_item_layout);
			mHolder.timeTxt = (TextView) view.findViewById(R.id.kx_time_txt);
			mHolder.threeLineContent = (TextView) view
					.findViewById(R.id.kx_content_txt);
			mHolder.linesContent = (TextView) view
					.findViewById(R.id.kx_content_txt_lines);
			
			mHolder.topLayoutGap = (TextView) view.findViewById(R.id.kx_item_top_gap);
			mHolder.shareLayout = (RelativeLayout) view
					.findViewById(R.id.kx_tags_share_layout);
			mHolder.readNumTxt = (TextView) view.findViewById(R.id.kx_read_txt);
			// 头部的日期部分
			mHolder.layout_list_section = (LinearLayout) view
					.findViewById(R.id.layout_list_section);
			mHolder.section_text = (TextView) view
					.findViewById(R.id.section_text);
			view.setTag(mHolder);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		// 获取position对应的数据
		final ArticleInfo news = getItem(position);

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"HH:mm");
		long intTime = Long.parseLong(news.getCreated_at());
		String time = simpleDateFormat.format(new Date(intTime));

		if (news.isOpen()) {
			// item_title.setMaxLines(100);
			mHolder.linesContent.setVisibility(View.VISIBLE);
			mHolder.threeLineContent.setVisibility(View.GONE);
			mHolder.linesContent.setText(newsList.get(position).getContent());
			mHolder.timeTxt.setText(time);
		} else {
			mHolder.threeLineContent.setVisibility(View.VISIBLE);
			mHolder.linesContent.setVisibility(View.GONE);
			mHolder.threeLineContent.setText(newsList.get(position)
					.getContent());
			mHolder.timeTxt.setText(time);
		}

		setThemeColor(mHolder,isDayTheme);
		
	
		if (news.getSpecial() == 7) {
			mHolder.threeLineContent.setTextColor(activity.getResources().getColor(R.color.custom_red));
			mHolder.linesContent.setTextColor(activity.getResources().getColor(R.color.custom_red));
			mHolder.timeTxt.setTextColor(activity.getResources().getColor(R.color.custom_red));
		}
		
		mHolder.readNumTxt.setText(""+news.getMobile_click_count());
		
		mHolder.shareLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onNewsClick.onNewsClick(mHolder.shareLayout, news);
				
			}
		});
		
		
		// 头部的相关东西
		int section = getSectionForPosition(position);
		if (getPositionForSection(section) == position) {
			mHolder.layout_list_section.setVisibility(View.VISIBLE);
			mHolder.topLayoutGap.setVisibility(View.GONE);
			mHolder.section_text.setText(mSections.get(section));
		} else {
			mHolder.topLayoutGap.setVisibility(View.VISIBLE);
			mHolder.layout_list_section.setVisibility(View.GONE);
		}
		return view;
	}

	/**
	 * 根据日间模式或者夜间模式切换ITEM的显示方式
	 * @param mHolder
	 * @param isDayTheme2
	 */
	private void setThemeColor(ViewHolder mHolder, boolean isDayTheme) {
		
		if (isDayTheme) {
			ThemeUtil.setBackgroundDay(activity, mHolder.itemLayout);
			ThemeUtil.setBackgroundDay(activity, mHolder.layout_list_section);
			ThemeUtil.setTextColorDay(activity, mHolder.threeLineContent);
			ThemeUtil.setTextColorDay(activity, mHolder.linesContent);
			ThemeUtil.setTextColorDay(activity, mHolder.timeTxt);
			
		}else {
			ThemeUtil.setBackgroundNight(activity, mHolder.itemLayout);
			ThemeUtil.setBackgroundNight(activity, mHolder.layout_list_section);
			ThemeUtil.setTextColorNight(activity, mHolder.threeLineContent);
			ThemeUtil.setTextColorNight(activity, mHolder.linesContent);
			ThemeUtil.setTextColorNight(activity, mHolder.timeTxt);
		}
		
	}

	static class ViewHolder {
		// 滚动内容布局
		LinearLayout itemLayout;
		// 新闻时间
		TextView timeTxt;
		// 限制行数的新闻内容
		TextView threeLineContent;
		// 不限制行数的新闻内容
		TextView linesContent;
		// 解读评论布局
		LinearLayout tipsLayout;
		// 评论图标
		ImageView tipsPlImg;
		// 解读图标
		ImageView tipsJdImg;

		// 分享的布局
		RelativeLayout shareLayout;
		// 阅读量
		TextView readNumTxt;

		// 头部的日期部分
		LinearLayout layout_list_section;
		TextView section_text;
		TextView section_day;
		
		TextView topLayoutGap;
	}

	@Override
	public int getHeaderState(int position) {
		int realPosition = position;
		if (realPosition < 0 || position >= getCount() || isPullDown == 1) {
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

	@Override
	public void configureHeader(View header, int position, int alpha) {
		int realPosition = position;
		int section = getSectionForPosition(realPosition);
//		String title = (String) getSections()[section];
		((TextView) header.findViewById(R.id.section_text)).setText(mSections
				.get(section));
		// ((TextView) header.findViewById(R.id.section_day)).setText("今天");
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

	/**
	 * 设置新闻内容大于三行时的点击展开功能
	 * 
	 * @param position
	 */
	public void checkeContentOpen(View view,int position) {

		ViewHolder holder = (ViewHolder) view.getTag();
		if (position < newsList.size()) {
			if (newsList.get(position-1).isOpen()) {
				newsList.get(position - 1)
				.setOpen(!newsList.get(position - 1).isOpen());
			
				holder.threeLineContent.setVisibility(View.VISIBLE);
				holder.linesContent.setVisibility(View.GONE);
				holder.threeLineContent.setText(newsList.get(position-1)
							.getContent());
				
				
			}else {
				newsList.get(position - 1)
				.setOpen(!newsList.get(position - 1).isOpen());
			
				holder.linesContent.setVisibility(View.VISIBLE);
				holder.threeLineContent.setVisibility(View.GONE);
				holder.linesContent.setText(newsList.get(position-1).getContent());
				
			}
			
			
		}

	}

	@Override
	public void onHeaderClick(View header, int position) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onListScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (view instanceof CustomListViewUnused) {
			// Log.d("first", "first:" + view.getFirstVisiblePosition());

			((CustomListViewUnused) view).configureHeaderView(firstVisibleItem - 1);
		}
	}

	@Override
	public void isPullDown(int pullDowm) {
		isPullDown = pullDowm;
	}

	public void setDataChange(ArrayList<ArticleInfo> lists) {
		this.newsList = lists;
		initDateHead();
	}

	@Override
	public void onListScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}
	
	public void changeTheme(boolean isDay){
		this.isDayTheme = isDay;
	}


	@Override
	public void onThemeChange(View headerView, boolean isDay) {
		// TODO Auto-generated method stub
		
	}

}
