package cn.com.nbd.nbdmobile.manager;

import java.util.ArrayList;

import android.content.Context;

public class ThemeChangeManager {
	
	private static ThemeChangeManager instance;
	
	private Context mContext;
	
	private ArrayList<OnThemeChangeListener> themeChangeListeners;
	
	private ArrayList<onTextChangeListener> textChangeListeners;
	
	
	
	private ThemeChangeManager(){}
	
	public static ThemeChangeManager getInstance(){
		if (instance == null) {
			instance = new ThemeChangeManager();
		}
		return instance;
	}
	
	public void init(Context context){
		this.mContext = context;
		themeChangeListeners = new ArrayList<OnThemeChangeListener>();
		textChangeListeners = new ArrayList<onTextChangeListener>();
	}
	
	
	public void registerThemeChangeListener(OnThemeChangeListener lisener) {
		themeChangeListeners.add(lisener);
	}
	
	public void unregisterThemeChangeListener(OnThemeChangeListener lisener) {
		themeChangeListeners.remove(lisener);
	}

	public void registerTextChangeListener(onTextChangeListener lisener) {
		textChangeListeners.add(lisener);
	}
	
	public void unregisterTextChangeListener(onTextChangeListener lisener) {
		textChangeListeners.remove(lisener);
	}
	
	/**
	 * 用于改变了主题的功能主动通知所有的监听者
	 * @param isDayTheme
	 */
	public void changeTheme(boolean isDayTheme){
		for(OnThemeChangeListener listener : themeChangeListeners) {
			listener.onNightThemeChange(isDayTheme);
		}
	}
	
	public void changeText(int nowTextSize){
		for(onTextChangeListener listener : textChangeListeners) {
			listener.onTextSizeChangeListenre(nowTextSize);
		}
	}
	

}
