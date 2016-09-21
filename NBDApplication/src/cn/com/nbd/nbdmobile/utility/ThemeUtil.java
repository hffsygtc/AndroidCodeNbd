package cn.com.nbd.nbdmobile.utility;

import cn.com.nbd.nbdmobile.R;
import android.app.Activity;
import android.view.View;
import android.widget.TextView;

/**
 * 设置切换主题的工具类
 * 由于更改THEME主题style，在后期的动态变化中不会生效，只有用getview动态配置色彩了
 * @author he
 *
 */
public class ThemeUtil {
	
	/**
	 * 给背景设置为日间背景
	 */
	public static void  setBackgroundDay(Activity activity,View view){
		
		view.setBackgroundColor(activity.getResources().getColor(R.color.day_common_background));
		
	}
	/**
	 * 给背景设置白色为日间背景
	 */
	public static void  setItemBackgroundDay(Activity activity,View view){
		
		view.setBackgroundColor(activity.getResources().getColor(R.color.day_item_background));
		
	}

	/**
	 * 给可点击的item设置日间的背景
	 * @param view
	 */
	public static void  setBackgroundClickDay(Activity activity,View view){
		
		view.setBackground(activity.getResources().getDrawable(R.drawable.news_item_click_selector_day));
		
	}

	
	/**
	 * 给显示的字体设置日间颜色，白天为黑，夜晚为灰
	 * @param view
	 */
	public static void  setTextColorDay(Activity activity,View view){
		
		((TextView)view).setTextColor(activity.getResources().getColor(R.color.day_black));
		
	}

	/**
	 * 给停靠栏设置日间的背景
	 * @param view
	 */
	public static void  setSectionBackgroundDay(Activity activity,View view){
		
		view.setBackgroundColor(activity.getResources().getColor(R.color.day_section_background));
		
	}
	
	/**
	 * 给控件设置带阴影的背景
	 * @param view
	 */
	public static void   setDrawableBackground(Activity activity,View view,int source){
		
		view.setBackground(activity.getResources().getDrawable(source));
		
	}
	

	/**
	 * 给背景设置为夜间背景
	 */
	public static void  setBackgroundNight(Activity activity,View view){
		
		view.setBackgroundColor(activity.getResources().getColor(R.color.night_common_background));
		
	}
	
	/**
	 * 给可点击的item设置夜间的背景
	 * @param view
	 */
	public static void  setBackgroundClickNight(Activity activity,View view){
		
		view.setBackground(activity.getResources().getDrawable(R.drawable.news_item_click_selector_night));
		
	}
	
	
	/**
	 * 给显示的字体设置夜间颜色，白天为黑，夜晚为灰
	 * @param view
	 */
	public static void  setTextColorNight(Activity activity,View view){
		
		((TextView)view).setTextColor(activity.getResources().getColor(R.color.night_black));
		
	}
	
	/**
	 * 给停靠栏设置夜间的背景
	 * @param view
	 */
	public static void  setSectionBackgroundNight(Activity activity,View view){
		
		view.setBackgroundColor(activity.getResources().getColor(R.color.night_section_background));
		
	}
	
	
	
	

}
