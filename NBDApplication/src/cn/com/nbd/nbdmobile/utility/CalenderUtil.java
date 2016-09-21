package cn.com.nbd.nbdmobile.utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.util.Log;

public class CalenderUtil {

	public static String[] weekName = { "周日", "周一", "周二", "周三", "周四", "周五",
			"周六" };

	/**
	 * 获取年月的具体天数
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getMonthDays(int year, int month) {
		if (month > 12) {
			month = 1;
			year += 1;
		} else if (month < 1) {
			month = 12;
			year -= 1;
		}
		int[] arr = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		int days = 0;

		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			arr[1] = 29; // 闰年2月29天
		}

		try {
			days = arr[month - 1];
		} catch (Exception e) {
			e.getStackTrace();
		}

		return days;
	}

	/**
	 * 获取当前年
	 * 
	 * @return
	 */
	public static int getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	/**
	 * 获取当前月份
	 * 
	 * @return
	 */
	public static int getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取当前的日期
	 * 
	 * @return
	 */
	public static int getCurrentMonthDay() {
		return Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
	}

	/**
	 * 获取日期在一周中的位置，周日为1，周一为2，需要减一
	 * 
	 * @return
	 */
	public static int getWeekDayPosition() {
		return Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
	}
	
	public static int getWeekDayPositionForDay(CalenderData data) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, data.year);
		c.set(Calendar.MONTH, data.month);
		c.set(Calendar.DAY_OF_MONTH, data.day);
		return c.get(Calendar.DAY_OF_WEEK);
	}

	public static int getHour() {
		return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
	}

	public static int getMinute() {
		return Calendar.getInstance().get(Calendar.MINUTE);
	}

	/**
	 * 获取下一个星期天的
	 * 
	 * @return
	 */
	public static CalenderData getNextSunday() {

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 7 - getWeekDayPosition() + 1);
		CalenderData date = new CalenderData(c.get(Calendar.YEAR),
				c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH));
		return date;
	}

	public static int[] getWeekSunday(int year, int month, int day, int pervious) {
		int[] time = new int[3];
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		c.add(Calendar.DAY_OF_MONTH, pervious);
		time[0] = c.get(Calendar.YEAR);
		time[1] = c.get(Calendar.MONTH) + 1;
		time[2] = c.get(Calendar.DAY_OF_MONTH);
		return time;

	}

	public static int getWeekDayFromDate(int year, int month) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(getDateFromString(year, month));
		int week_index = cal.get(Calendar.DAY_OF_WEEK) - 1;
		if (week_index < 0) {
			week_index = 0;
		}
		return week_index;
	}

	public static Date getDateFromString(int year, int month) {
		String dateString = year + " -" + (month > 9 ? month : (0 + month))
				+ -01;
		Date date = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			System.out.println(e.getMessage());
		}
		return date;
	}

	public static boolean isToday(CalenderData date) {
		return (date.year == CalenderUtil.getYear()
				&& date.month == CalenderUtil.getMonth() && date.day == CalenderUtil
					.getCurrentMonthDay());
	}

	public static boolean isCurrentMonth(CalenderData date) {
		return (date.year == CalenderUtil.getYear() && date.month == CalenderUtil
				.getMonth());
	}

	/**
	 * 获取当天所在的一周的日期列表
	 * 
	 * @return
	 */
	public static List<CalenderData> getThisWeek() {
		// 获取当前月的天数
		int monthCount = getMonthDays(getYear(), getMonth());
		int perMonthCount = getMonthDays(getYear(), getMonth() -1);
		// 获取今天的号数
		int today = getCurrentMonthDay();
		// 获取今天是星期几
		int dayPosition = getWeekDayPosition() - 1;

		List<CalenderData> content = new ArrayList<CalenderData>();
		CalenderData todayData = new CalenderData(getYear(), getMonth(), getCurrentMonthDay());
		for (int i = 0; i < 7; i++) {
//			if (today + i -dayPosition < 1) { //今天的日子如果小于1 就是上个月的日子
//				content.add(i,new CalenderData(getYear(), month, day))
//				
//			}
			
			content.add(i, getMoveData(todayData, i-dayPosition));
//			content.add(i, new CalenderData(getYear(), getMonth(), today + i
//					- dayPosition));
		}
		return content;
	}
	
	public static List<CalenderData> getNextWeek(List<CalenderData> datas,int move) {
		for (int i = 0; i < datas.size(); i++) {
			CalenderData temptData = datas.get(i);
			datas.add(i,getMoveData(temptData, 7*move));
		}
		
		return datas;
	}

	public static List<CalenderData> getWeekTitle() {
		List<CalenderData> content = new ArrayList<CalenderData>();
		CalenderData data = new CalenderData(1970, 1, 1);
		data.setTitle("日");
		CalenderData data1 = new CalenderData(1970, 1, 1);
		data1.setTitle("一");
		CalenderData data2 = new CalenderData(1970, 1, 1);
		data2.setTitle("二");
		CalenderData data3 = new CalenderData(1970, 1, 1);
		data3.setTitle("三");
		CalenderData data4 = new CalenderData(1970, 1, 1);
		data4.setTitle("四");
		CalenderData data5 = new CalenderData(1970, 1, 1);
		data5.setTitle("五");
		CalenderData data6 = new CalenderData(1970, 1, 1);
		data6.setTitle("六");
		content.add(data);
		content.add(data1);
		content.add(data2);
		content.add(data3);
		content.add(data4);
		content.add(data5);
		content.add(data6);
		return content;
	}

	/**
	 * 获取指定日期的星期位置
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static int getMonthFirstDayPosition(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month - 1);
		c.set(Calendar.DATE, day);

		int firstPosition = c.get(Calendar.DAY_OF_WEEK);

		// Log.e("CALENDER==>", "YEAR=="+c.get(Calendar.YEAR)+"MONTH==>"
		// +c.get(Calendar.MONTH )+"DAY==>"
		// +c.get(Calendar.DAY_OF_MONTH)+
		// "POSITION==>"+c.get(Calendar.DAY_OF_WEEK)+"");

		return firstPosition;

	}
	/**
	 * 获取指定日期的星期位置
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static CalenderData getMoveData(CalenderData data, int move) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, data.year);
		c.set(Calendar.MONTH, data.month-1);
		c.set(Calendar.DAY_OF_MONTH, data.day);
		c.add(Calendar.DAY_OF_MONTH, move);
		return new CalenderData(c.get(Calendar.YEAR),c.get(Calendar.MONTH)+1,c.get(Calendar.DAY_OF_MONTH));
	}

}
