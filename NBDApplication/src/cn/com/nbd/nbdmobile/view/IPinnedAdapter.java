package cn.com.nbd.nbdmobile.view;

import android.view.View;
import android.widget.AbsListView;

public interface IPinnedAdapter {

	public static final int HEADER_GONE = 0; // 停靠栏不显示

	public static final int HEADER_VISIBLE = 1; // 停靠栏显示

	public static final int HEADER_PUSHED_UP = 2; // 两个停靠栏碰一起滑动

	/**
	 * 获取对应List子项显示的停靠栏的状态
	 * 
	 * @param position
	 *            列表中的位置
	 * @return GONE/VISIBLE/PUSHEDUP
	 */
	int getPinnedState(int position);

	/**
	 * 配置停靠栏显示的布局样式
	 * 
	 * @param header
	 *            停靠栏的视图
	 * @param position
	 * @param alpha
	 *            透明度
	 */
	void configurePinned(View header, int position, int alpha);

	/**
	 * 停靠栏点击的回调
	 * 
	 * @param header
	 * @param position
	 *            左右的section位置
	 */
	void onPinnedClick(View header, int position);

	/**
	 * 是否在下拉的状态，如果在下拉的状态，不显示停靠栏
	 * 
	 * @param pullDowm
	 */
	void isPinnedPullDown(int pullDowm);

	/**
	 * listview传出了的滑动事件，根据这个滑动事件去处理停靠栏的逻辑
	 * 
	 * @param view
	 * @param firstVisibleItem
	 * @param visibleItemCount
	 * @param totalItemCount
	 */
	void onListViewScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount);

	/**
	 * listview传出了的状态改变事件
	 * 
	 * 当listview停止滑动的时候才能进行停靠栏的左右切换点击事件，确保记录状态的准确性
	 * 
	 * @param view
	 * @param scrollState
	 */
	void onListScrollStateChanged(AbsListView view, int scrollState);

	/**
	 * 主题更改的回调，处理停靠栏的主题颜色更改
	 * 
	 * @param headerView
	 * @param isDay
	 */
	void onThemeChange(View headerView, boolean isDay);

}
