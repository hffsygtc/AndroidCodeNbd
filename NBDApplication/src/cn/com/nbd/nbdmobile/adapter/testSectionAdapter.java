package cn.com.nbd.nbdmobile.adapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.callback.ListviewPosRecrodCallback;
import cn.com.nbd.nbdmobile.callback.ToggleCheckedCallback;
import cn.com.nbd.nbdmobile.showview.MixNewsHolder;
import cn.com.nbd.nbdmobile.view.CustomListViewUnused;
import cn.com.nbd.nbdmobile.view.IPinnedAdapter;
import cn.com.nbd.nbdmobile.view.RefreshListview;
import cn.com.nbd.nbdmobile.view.RefreshPinnedListview;

import com.nbd.article.bean.ArticleInfo;

public class testSectionAdapter extends InfosAdapter implements SectionIndexer,
		IPinnedAdapter {
	
	private final static String TAG = "2_Section Adapter ";

	private List<Integer> mPositions;
	private List<String> mSections;

	private OnClickListener leftClickListener;
	private OnClickListener rightClickListener;

	private int headerSectionPosition;

	private ToggleCheckedCallback toggleCheckListener;
	private ListviewPosRecrodCallback postionRecrodCallback;

	private String mAdapterType;

	private static final int COLOR_TEXT_NORMAL_DAY = 0xff000000;
	private static final int COLOR_TEXT_HIGHTLIGHR = 0xffd53e36;
	private static final int COLOR_TEXT_NORMAL_NIGHT = 0xff7f7f7f;

	public testSectionAdapter(Context context, List<ArticleInfo> listData,
			List<ArticleInfo> headData, boolean isDayTheme, boolean isTextMode,
			String type) {
		super(context, listData, headData, isDayTheme, isTextMode);

		this.mAdapterType = type;
		initListener();

		initDateHead();
	}

	public void setToggleClickNotify(boolean isToggle) {
		this.isToggleClickNotify = isToggle;
	}
	
	public void setToggleChecked(ToggleCheckedCallback listener) {
		this.toggleCheckListener = listener;
	}
	
	/**
	 * 设置滑动状态改变时，记录当前子栏目滑动的位置
	 * 
	 * @param listener
	 */
	public void setItemPosRecordListener(ListviewPosRecrodCallback listener) {
		this.postionRecrodCallback = listener;
	}


	private void initListener() {
		leftClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "_____click left section");
				headerSectionPosition = 0;
				isToggleClickNotify = true;
				toggleCheckListener.onToggleChecked(0);

			}
		};

		rightClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d(TAG, "_____click right section");
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
	public void buildViewDatas(MixNewsHolder mHolder, ArticleInfo news,
			int position) {
		super.buildViewDatas(mHolder, news, position);

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

			mHolder.section_left_layout.setOnClickListener(leftClickListener);

			mHolder.section_right_layout.setOnClickListener(rightClickListener);

		} else {
			mHolder.layout_list_section.setVisibility(View.GONE);
		}

		setThemeColor(mHolder, isDay);

		if (isText) {
			imgMap.put(mHolder, defaultImage);
			showViewByType(mHolder, news,position);
		} else {
			if (imgMap.get(mHolder) != null
					&& imgMap.get(mHolder).equals(news.getImage())) {
			} else {
				imgMap.put(mHolder, news.getImage());
				showViewByType(mHolder, news,position);
			}
		}

	}

	
	@Override
	public int getPinnedState(int position) {
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

	@Override
	public void configurePinned(View header, int position, int alpha) {

		setHeaderStyle(header, headerSectionPosition);

	}

	@Override
	public void onPinnedClick(View header, int position) {
		isToggleClickNotify = false;
		toggleCheckListener.onToggleChecked(position);
		headerSectionPosition = position;
		setHeaderStyle(header, position);

	}

	@Override
	public void isPinnedPullDown(int pullDowm) {
	}

	@Override
	public void onListViewScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		isToggleClickNotify = false;
		if (view instanceof RefreshPinnedListview) {
			((RefreshPinnedListview) view).configureHeaderView(firstVisibleItem - 1);
		}
		
		if (mVideoPlayPosition != -1) {
			if ((firstVisibleItem-1) >mVideoPlayPosition  || (firstVisibleItem+visibleItemCount-2)<mVideoPlayPosition) {
				if (mVideoView != null) {
					mVideoView.stopAndRelease();
					ViewGroup parent = (ViewGroup) mVideoView.getParent();
					if (parent != null) {
						parent.removeAllViews();
					}
					mVideoPlayPosition = -1;
				}
			}
		}
	}

	@Override
	public void onListScrollStateChanged(AbsListView view, int scrollState) {
		// 不滚动时保存当前滚动到的位置
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					// scrollPos记录当前可见的List顶端的一行的位置
					int scrollPos = view.getFirstVisiblePosition();

					View v = view.getChildAt(0);
					int scrollTop = (v == null) ? 0 : v.getTop();
					
					Log.d(TAG, "__ Store state___" + "position__"+scrollPos+"__top dis__"+scrollTop);
					
					if (postionRecrodCallback != null) {
						postionRecrodCallback.setPositionRecrod(scrollPos, scrollTop);
					}
				}
	}

	@Override
	public void onThemeChange(View headerView, boolean isDayTheme) {
		isDay = isDayTheme;
		TextView leftView = (TextView) headerView
				.findViewById(R.id.left_section_text);
		TextView rightView = (TextView) headerView
				.findViewById(R.id.right_section_text);
		setThemeTextColot(headerSectionPosition, leftView, rightView);
	}

	@Override
	public Object[] getSections() {
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
	 * 设置选择项的样式
	 * 
	 * @param lv
	 * @param rv
	 * @param position
	 */
	private void setHeadeerStyle(View lv, View lvBtm, View rv, View rvBtom,
			int position) {
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
			if (isDay) {
				((TextView) lv).setTextColor(COLOR_TEXT_HIGHTLIGHR);
				((TextView) rv).setTextColor(COLOR_TEXT_NORMAL_DAY);
			} else {
				((TextView) lv).setTextColor(COLOR_TEXT_HIGHTLIGHR);
				((TextView) rv).setTextColor(COLOR_TEXT_NORMAL_NIGHT);
			}
			break;
		case 1:
			if (isDay) {
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
	}

}
