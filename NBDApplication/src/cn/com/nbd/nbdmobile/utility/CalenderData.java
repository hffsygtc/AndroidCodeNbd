package cn.com.nbd.nbdmobile.utility;

import java.io.Serializable;

public class CalenderData implements Serializable {
	private static final long serialVersionUID = 1L;
	public int year;
	public int month;
	public int day;
	public int week;
	public String title;
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public CalenderData(int year,int month,int day){
		if(month > 12){
			month = 1;
			year++;
		}else if(month <1){
			month = 12;
			year--;
		}
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	public CalenderData(){
		this.year = CalenderUtil.getYear();
		this.month = CalenderUtil.getMonth();
		this.day = CalenderUtil.getCurrentMonthDay();
	}
	
	public CalenderData modifiDayForObject(CalenderData date,int day){
		CalenderData modifiDate = new CalenderData(date.year,date.month,day);
		return modifiDate;
	}
	
	public CalenderData changeDayToNextWeek(CalenderData data,int changeCount){
		CalenderData modifiData = new CalenderData(data.year, data.month, data.day+changeCount*7);
		return modifiData;
		
	}
	
	
	@Override
	public String toString() {
		return year+"-"+month+"-"+day;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}
	
	public boolean isEquleTo(CalenderData data){
		if (year==data.getYear() && month==data.getMonth() && day==data.getDay()) {
			return true;
		}
		return false;
	}

}
