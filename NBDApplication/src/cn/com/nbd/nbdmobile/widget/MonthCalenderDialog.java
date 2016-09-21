package cn.com.nbd.nbdmobile.widget;

import java.util.ArrayList;
import java.util.List;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.CalenderData;
import cn.com.nbd.nbdmobile.utility.CalenderUtil;
import cn.com.nbd.nbdmobile.view.CalenderLineView;
import cn.com.nbd.nbdmobile.view.CalenderLineView.onDataClick;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MonthCalenderDialog extends Dialog {

	private Context mContext;
	// 当前dialog显示数据的年份
	private int mShowYear;
	// 当前dialog显示数据的月份
	private int mShowMonth;

	private CalenderLineView lineTitle;
	private CalenderLineView lineOne;
	private CalenderLineView lineTwo;
	private CalenderLineView lineThree;
	private CalenderLineView lineFour;
	private CalenderLineView lineFive;
	private CalenderLineView lineSix;

	private RelativeLayout mPerMonthBtn;
	private RelativeLayout mNextMonthBtn;
	private TextView mDataTitleTxt;

	private List<CalenderData> firstWeekDatas = new ArrayList<CalenderData>();
	private List<CalenderData> twoWeekDatas = new ArrayList<CalenderData>();
	private List<CalenderData> threeWeekDatas = new ArrayList<CalenderData>();
	private List<CalenderData> fourWeekDatas = new ArrayList<CalenderData>();
	private List<CalenderData> fiveWeekDatas = new ArrayList<CalenderData>();
	private List<CalenderData> sixWeekDatas = new ArrayList<CalenderData>();

	private RelativeLayout dialogLayout;

	private CalenderData mFlagData;
	private CalenderData mShowData;
	
	private boolean isPaper;
	

	public interface onDataClickFromMonth {
		void onDataClicked(CalenderData data, List<CalenderData> dataWeek);
	}

	private onDataClickFromMonth mDataClick;

	private onDataClick mClick = new onDataClick() {

		@Override
		public void onDataClicked(CalenderData data, int weekPosition) {
			Toast.makeText(mContext,
					"" + data.getMonth() + "-" + data.getDay(),
					Toast.LENGTH_SHORT).show();

			lineOne.setCheckDayChanged(data);
			lineTwo.setCheckDayChanged(data);
			lineThree.setCheckDayChanged(data);
			lineFour.setCheckDayChanged(data);
			lineFive.setCheckDayChanged(data);
			lineSix.setCheckDayChanged(data);

			if (mDataClick != null) {
				switch (weekPosition) {
				case 1:
					mDataClick.onDataClicked(data, firstWeekDatas);
					break;
				case 2:
					mDataClick.onDataClicked(data, twoWeekDatas);
					break;
				case 3:
					mDataClick.onDataClicked(data, threeWeekDatas);
					break;
				case 4:
					mDataClick.onDataClicked(data, fourWeekDatas);
					break;
				case 5:
					mDataClick.onDataClicked(data, fiveWeekDatas);
					break;
				case 6:
					mDataClick.onDataClicked(data, sixWeekDatas);
					break;

				default:
					break;
				}
			}
			MonthCalenderDialog.this.dismiss();

		}
	};

	public MonthCalenderDialog(Context context) {
		super(context);
	}

	public MonthCalenderDialog(Context context, int theme, int year, int month,
			CalenderData showData,boolean isPaperPage) {
		super(context, theme);

		this.mContext = context;
		this.mShowYear = year;
		this.mShowMonth = month;

		this.mFlagData = showData;
		this.mShowData = new CalenderData(showData.year,showData.month,showData.day);

		this.isPaper = isPaperPage;
		initData(showData);
	}

	/**
	 * 初始化数据
	 */
	private void initData(CalenderData mShowData) {
		// 获取当月第一天的星期位置
		int firstPosition = CalenderUtil.getMonthFirstDayPosition(mShowData.year,
				mShowData.month, 1);
		// 处理第一周的数据列表
		CalenderData firstDay = new CalenderData(mShowData.year, mShowData.month, 1);
		if (firstWeekDatas!=null && firstWeekDatas.size()>0) {
			firstWeekDatas.clear();
			twoWeekDatas.clear();
			threeWeekDatas.clear();
			fourWeekDatas.clear();
			fiveWeekDatas.clear();
			sixWeekDatas.clear();
		}
		for (int i = 0; i < 7; i++) {
			CalenderData tempData = CalenderUtil.getMoveData(firstDay, i
					- firstPosition + 1);
			firstWeekDatas.add(tempData);
			tempData = CalenderUtil
					.getMoveData(firstDay, i - firstPosition + 8);
			twoWeekDatas.add(tempData);
			tempData = CalenderUtil.getMoveData(firstDay, i - firstPosition
					+ 15);
			threeWeekDatas.add(tempData);
			tempData = CalenderUtil.getMoveData(firstDay, i - firstPosition
					+ 22);
			fourWeekDatas.add(tempData);
			tempData = CalenderUtil.getMoveData(firstDay, i - firstPosition
					+ 29);
			fiveWeekDatas.add(tempData);
			tempData = CalenderUtil.getMoveData(firstDay, i - firstPosition
					+ 36);
			sixWeekDatas.add(tempData);

		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.calender_month_dialog_layout);

		lineTitle = (CalenderLineView) findViewById(R.id.calender_dialog_line_title);
		lineOne = (CalenderLineView) findViewById(R.id.calender_dialog_line_one);
		lineTwo = (CalenderLineView) findViewById(R.id.calender_dialog_line_two);
		lineThree = (CalenderLineView) findViewById(R.id.calender_dialog_line_three);
		lineFour = (CalenderLineView) findViewById(R.id.calender_dialog_line_four);
		lineFive = (CalenderLineView) findViewById(R.id.calender_dialog_line_five);
		lineSix = (CalenderLineView) findViewById(R.id.calender_dialog_line_six);
		mPerMonthBtn = (RelativeLayout) findViewById(R.id.calender_dialog_month_per_layout);
		mNextMonthBtn = (RelativeLayout) findViewById(R.id.calender_dialog_month_next_layout);
		mDataTitleTxt = (TextView) findViewById(R.id.calender_dialog_now_month_txt);
		
		mDataTitleTxt.setText(mFlagData.year+"年"+mFlagData.month+"月");

		if (isPaper) {
			lineTitle.setTabItemTitles(CalenderUtil.getWeekTitle(), true,
					mFlagData, -1,true);
			lineOne.setTabItemTitles(firstWeekDatas, false, mFlagData, 1,true);
			lineTwo.setTabItemTitles(twoWeekDatas, false, mFlagData, 2,true);
			lineThree.setTabItemTitles(threeWeekDatas, false, mFlagData, 3,true);
			lineFour.setTabItemTitles(fourWeekDatas, false, mFlagData, 4,true);
			lineFive.setTabItemTitles(fiveWeekDatas, false, mFlagData, 5,true);
			lineSix.setTabItemTitles(sixWeekDatas, false, mFlagData, 6,true);
		}else {
			lineTitle.setTabItemTitles(CalenderUtil.getWeekTitle(), true,
					mFlagData, -1,false);
			lineOne.setTabItemTitles(firstWeekDatas, false, mFlagData, 1,false);
			lineTwo.setTabItemTitles(twoWeekDatas, false, mFlagData, 2,false);
			lineThree.setTabItemTitles(threeWeekDatas, false, mFlagData, 3,false);
			lineFour.setTabItemTitles(fourWeekDatas, false, mFlagData, 4,false);
			lineFive.setTabItemTitles(fiveWeekDatas, false, mFlagData, 5,false);
			lineSix.setTabItemTitles(sixWeekDatas, false, mFlagData, 6,false);
		}

		dialogLayout = (RelativeLayout) findViewById(R.id.dialog_layout);
		dialogLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MonthCalenderDialog.this.dismiss();

			}
		});

		mPerMonthBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CalenderData temptData = new CalenderData(mShowData.year, mShowData.month-1, 1);
				initData(temptData);

				mShowData = temptData;
				lineOne.setWeekChanged(mFlagData, firstWeekDatas);
				lineTwo.setWeekChanged(mFlagData, twoWeekDatas);
				lineThree.setWeekChanged(mFlagData, threeWeekDatas);
				lineFour.setWeekChanged(mFlagData, fourWeekDatas);
				lineFive.setWeekChanged(mFlagData, fiveWeekDatas);
				lineSix.setWeekChanged(mFlagData, sixWeekDatas);
				
				mDataTitleTxt.setText(twoWeekDatas.get(0).getYear()+"年"+twoWeekDatas.get(0).getMonth()+"月");
				
			}
		});

		mNextMonthBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				CalenderData temptData = new CalenderData(mShowData.year, mShowData.month+1, 1);
				initData(temptData);
				mShowData = temptData;

				lineOne.setWeekChanged(mFlagData, firstWeekDatas);
				lineTwo.setWeekChanged(mFlagData, twoWeekDatas);
				lineThree.setWeekChanged(mFlagData, threeWeekDatas);
				lineFour.setWeekChanged(mFlagData, fourWeekDatas);
				lineFive.setWeekChanged(mFlagData, fiveWeekDatas);
				lineSix.setWeekChanged(mFlagData, sixWeekDatas);
				
				mDataTitleTxt.setText(twoWeekDatas.get(0).getYear()+"年"+twoWeekDatas.get(0).getMonth()+"月");

			}
		});

		setListener();
	}

	/**
	 * 设置监听
	 */
	private void setListener() {
		lineOne.setOnDataClickListener(mClick);
		lineTwo.setOnDataClickListener(mClick);
		lineThree.setOnDataClickListener(mClick);
		lineFive.setOnDataClickListener(mClick);
		lineFour.setOnDataClickListener(mClick);
		lineSix.setOnDataClickListener(mClick);

	}

	public void setOnMonthDataChooseListener(onDataClickFromMonth listener) {
		this.mDataClick = listener;
	}
}
