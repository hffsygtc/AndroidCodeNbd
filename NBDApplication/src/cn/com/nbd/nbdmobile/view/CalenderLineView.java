package cn.com.nbd.nbdmobile.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.CalenderData;
import cn.com.nbd.nbdmobile.utility.CalenderUtil;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CalenderLineView extends LinearLayout {
	private Context mContext;

	private int mTabVisibleCount; // 可见的tab数量

	private List<CalenderData> mDatas;

	private int clickPosition = -1;
	private int showPosition = -1;

	private int lineViewWidth; // 控件XML文件中定义的宽度

	private boolean isTitleLine; // 是否是显示日期的行数，区别天名的行数
	private boolean ismPaperPage; //是否是报纸的页面，如果是报纸的页面周末就显示为休刊

	private CalenderData nowDay;

	private CalenderData checkDay; // 选中的那一天的日子

	private int mWeekPosition;

	public enum dayType {
		CHOOSED, EMPTY, NORMAL, WEEKEND
	}

	private List<dayType> dayTypList = new ArrayList<CalenderLineView.dayType>();

	public interface onDataClick {
		void onDataClicked(CalenderData data, int weekPosition);
	}

	private onDataClick mDataClick;

	public CalenderLineView(Context context) {
		this(context, null);
	}

	public CalenderLineView(Context context, AttributeSet attrs) {
		super(context, attrs);

		Log.e("selfView", "构造函数");
		mContext = context;

		mTabVisibleCount = 7;

		nowDay = new CalenderData();

	}

	/**
	 * 绘制指示器图标
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {
		Log.e("selfView", "dispatchDraw函数");
		if (!isTitleLine) {

			for (int i = 0; i < 7; i++) {
				
				if (ismPaperPage && (i == 0 || i == 6)) {
					((TextView) ((ViewGroup) getChildAt(i)).getChildAt(0))
					.setText("休");
					((TextView) ((ViewGroup) getChildAt(i)).getChildAt(0))
					.setTextColor(mContext.getResources().getColor(
							R.color.main_paper_canlender_data_empty));
				}else {
					((TextView) ((ViewGroup) getChildAt(i)).getChildAt(0))
					.setText(mDatas.get(i).day + "");
					
					if (dayTypList.get(i) == dayType.EMPTY) {
						((TextView) ((ViewGroup) getChildAt(i)).getChildAt(0))
						.setTextColor(mContext.getResources().getColor(
								R.color.main_paper_canlender_data_empty));
					} else {
						((TextView) ((ViewGroup) getChildAt(i)).getChildAt(0))
						.setTextColor(mContext.getResources().getColor(
								R.color.main_paper_canlender_data));
					}
				}
				
			}

			// 选中的日子没有在个星期的行里面
			if (clickPosition == -1) {
				if (showPosition == -1) { // 当前周里面没有显示的红点，不需要做操作
				} else { // 还原当前行红点的内容
					((TextView) ((ViewGroup) getChildAt(showPosition))
							.getChildAt(0))
							.setBackgroundResource(R.drawable.day_white_bg);
					if (dayTypList != null && dayTypList.size() > 0) {
						if (dayTypList.get(showPosition) == dayType.NORMAL) {
							((TextView) ((ViewGroup) getChildAt(showPosition))
									.getChildAt(0)).setTextColor(mContext
									.getResources().getColor(
											R.color.main_paper_canlender_data));
						} else {
							((TextView) ((ViewGroup) getChildAt(showPosition))
									.getChildAt(0))
									.setTextColor(mContext
											.getResources()
											.getColor(
													R.color.main_paper_canlender_data_empty));
						}
					}
				}
			} else { // 当前选选择的日子在行里面
				if (showPosition == -1) { // 如果当前周里面没有显示选中的红点，直接操作
					((TextView) ((ViewGroup) getChildAt(clickPosition))
							.getChildAt(0))
							.setBackgroundResource(R.drawable.choose_data_bg);
					((TextView) ((ViewGroup) getChildAt(clickPosition))
							.getChildAt(0)).setTextColor(mContext
							.getResources().getColor(R.color.white));
				} else { // 当前周有选中的红点
					((TextView) ((ViewGroup) getChildAt(showPosition))
							.getChildAt(0))
							.setBackgroundResource(R.drawable.day_white_bg);
					if (dayTypList != null && dayTypList.size() > 0) {

						if (dayTypList.get(showPosition) == dayType.NORMAL) {
							((TextView) ((ViewGroup) getChildAt(showPosition))
									.getChildAt(0)).setTextColor(mContext
									.getResources().getColor(
											R.color.main_paper_canlender_data));
						} else {
							((TextView) ((ViewGroup) getChildAt(showPosition))
									.getChildAt(0))
									.setTextColor(mContext
											.getResources()
											.getColor(
													R.color.main_paper_canlender_data_empty));
						}
					}

					((TextView) ((ViewGroup) getChildAt(clickPosition))
							.getChildAt(0))
							.setBackgroundResource(R.drawable.choose_data_bg);
					((TextView) ((ViewGroup) getChildAt(clickPosition))
							.getChildAt(0)).setTextColor(mContext
							.getResources().getColor(R.color.white));
				}
				showPosition = clickPosition;
			}

			setItemClickEvent();

		} else {
			for (int i = 0; i < 7; i++) {
				((TextView) ((ViewGroup) getChildAt(i)).getChildAt(0))
						.setText(mDatas.get(i).getTitle());
				((TextView) ((ViewGroup) getChildAt(i)).getChildAt(0))
						.setTextColor(mContext.getResources().getColor(
								R.color.main_paper_canlender_title));
			}
		}
		super.dispatchDraw(canvas);
	}

	/**
	 * 滑动的时候绘制移动的指示器
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * 加载完XML文件后调用执行设置TAB
	 */
	@Override
	protected void onFinishInflate() {

		Log.e("selfView", "finishinglate函数");
		super.onFinishInflate();

		int cCount = getChildCount();
		if (cCount == 0)
			return;

		for (int i = 0; i < cCount; i++) {
			View view = getChildAt(i);
			LinearLayout.LayoutParams lp = (LayoutParams) view
					.getLayoutParams();

			lp.weight = 0;
			lp.width = lineViewWidth / mTabVisibleCount;
			view.setLayoutParams(lp);
		}

	}

	/**
	 * 提供给外部的设置TAB数量和内容的方法
	 * 
	 * @param titles
	 */
	public void setTabItemTitles(List<CalenderData> datas, boolean isTitle,
			CalenderData nowData, int weekPosition,boolean isPaperPage) {
		if (datas != null && datas.size() > 0) {
			this.removeAllViews();
			mDatas = datas;
		}

		if (!isTitle) {
			boolean isCheckIn = false;
			for (int i = 0; i < mDatas.size(); i++) {
				if (nowData.isEquleTo(mDatas.get(i))) {
					clickPosition = i;
					isCheckIn = true;
				}
			}

			if (!isCheckIn) {
				clickPosition = -1;
			}
		}

		isTitleLine = isTitle;
		ismPaperPage = isPaperPage;

		mWeekPosition = weekPosition;
		getDataType();
	}

	/**
	 * 设置可见的TAB数量
	 * 
	 * @param count
	 */
	public void setVisibleTabCount(int count) {
		mTabVisibleCount = count;
	}

	/**
	 * 根据title创建Tab
	 * 
	 * @param title
	 * @return
	 */
	private View generateTextView(String title, dayType type) {
		RelativeLayout wrraperLayout = new RelativeLayout(mContext);
		LinearLayout.LayoutParams wrraperParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		wrraperParams.width = lineViewWidth / mTabVisibleCount;
		wrraperLayout.setGravity(Gravity.CENTER);
		wrraperLayout.setLayoutParams(wrraperParams);

		TextView tv = new TextView(mContext);
		LinearLayout.LayoutParams lp = new LayoutParams(93,
				93);
//		LinearLayout.LayoutParams lp = new LayoutParams(dip2px(mContext, 31),
//				dip2px(mContext, 31));
		// tv.setText(title);
		tv.setGravity(Gravity.CENTER);
//		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 42);
		tv.setLayoutParams(lp);

		wrraperLayout.addView(tv);
		// tv.setTextColor(COLOR_TEXT_NORMAL);
		return wrraperLayout;
	}

	/**
	 * 重置TAB的文本颜色
	 */
	private void resetTextViewColor() {
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);

			if (view instanceof TextView) {
				// ((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
				((TextView) view).setTextColor(mContext.getResources()
						.getColor(R.color.main_title_txt));

			}
		}

	}

	/**
	 * 当选中对应的TAB的时候，将文件进行一个高亮的处理
	 * 
	 * @param pos
	 */
	public void highLightText(int pos) {
		resetTextViewColor();
		View view = getChildAt(pos);

		if (view instanceof TextView) {
			// ((TextView) view).setTextColor(COLOR_TEXT_HIGHTLIGHR);
			((TextView) view).setTextColor(mContext.getResources().getColor(
					R.color.main_title_txt_hightlight));
			// Log.d("hff", ((TextView) view).getText().toString());
		}
	}

	/**
	 * 设置TAB的点击事件
	 */
	private void setItemClickEvent() {

		int cCount = getChildCount();

		for (int i = 0; i < cCount; i++) {
			final int j = i;

			View view = getChildAt(i);

			if (dayTypList != null && dayTypList.size() > 0) {
				if (dayTypList.get(j) == dayType.NORMAL) {
					view.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {

							// Toast.makeText(mContext,
							// mDatas.get(j).getDay()+"",
							// Toast.LENGTH_SHORT).show();
							clickPosition = j;
							invalidate();
							if (mDataClick != null) {
								mDataClick.onDataClicked(mDatas.get(j),
										mWeekPosition);
							}

						}
					});
				} else {
					view.setClickable(false);
				}
			}

		}
	}

	/**
	 * 绘制控件之前，获取控件的XML属性中控件的宽度值
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.e("selfview", "onmeasure");

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		lineViewWidth = getMeasuredWidth();

		if (mDatas != null) {

			if (getChildCount() == mDatas.size()) {

			} else {
				// 当数量不等的时候，先移除掉现有的子view，再生成添加
				int size = getChildCount();
				for (int i = 0; i < size; i++) {
					removeViewAt(0);
				}

				for (int i = 0; i < mDatas.size(); i++) {
					if (isTitleLine) { // 标题栏的情况
						addView(generateTextView("" + mDatas.get(i).getTitle(),
								dayType.NORMAL));
					} else { // 其他情况
						addView(generateTextView("" + mDatas.get(i).getDay(),
								dayTypList.get(i)));
					}

				}
			}
		}
		if (!isTitleLine) {
			setItemClickEvent();
		}

	}

	private void getDataType() {
		String flagDay = nowDay.getYear() + "/" + nowDay.getMonth() + "/"
				+ nowDay.getDay();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/M/d");
		Date flagData = null;
		if (dayTypList != null && dayTypList.size() > 0) {
			dayTypList.clear();
		}
		if (!isTitleLine) {
			if (mDatas != null && mDatas.size() > 0) {
				for (int i = 0; i < mDatas.size(); i++) {
					
					if (ismPaperPage && (i == 0 || i == 6)) {
						dayTypList.add(i, dayType.WEEKEND);
					}else {

						String compareDay = mDatas.get(i).getYear() + "/"
								+ mDatas.get(i).getMonth() + "/"
								+ mDatas.get(i).getDay();
						try {
							if (flagData == null) {
								flagData = dateFormat.parse(flagDay);
							}
							Date compareDate = dateFormat.parse(compareDay);
							if (flagData.compareTo(compareDate) >= 0) {
								// 结束日期早于开始日期
								dayTypList.add(i, dayType.NORMAL);
							} else {
								dayTypList.add(i, dayType.EMPTY);
							}
						} catch (ParseException e) {
						}
					}
				}
			}
		}

		Log.e("CANLDENER==>", "reGet data type!!");

	}

	private int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public void setOnDataClickListener(onDataClick click) {
		this.mDataClick = click;
	}

	public void setCheckDayChanged(CalenderData checkDay) {
		this.checkDay = checkDay;
		boolean isCheckIn = false;
		for (int i = 0; i < mDatas.size(); i++) {
			if (checkDay.isEquleTo(mDatas.get(i))) {
				clickPosition = i;
				isCheckIn = true;
			}
		}

		if (!isCheckIn) {
			clickPosition = -1;
		}
		invalidate();
	}

	/**
	 * 设置点击的周的和选中的当天的数据变化
	 * 
	 * @param checkDay
	 * @param dataList
	 */
	public void setWeekChanged(CalenderData checkDay,
			List<CalenderData> dataList) {

		Log.e("CANLDENER==>", "data changed!!");
		this.checkDay = checkDay;
		this.mDatas = dataList;

		boolean isCheckIn = false;
		for (int i = 0; i < mDatas.size(); i++) {
			if (checkDay.isEquleTo(mDatas.get(i))) {
				clickPosition = i;
				isCheckIn = true;
			}
		}

		if (!isCheckIn) {
			clickPosition = -1;
		}

		getDataType();
		invalidate();
	}
}
