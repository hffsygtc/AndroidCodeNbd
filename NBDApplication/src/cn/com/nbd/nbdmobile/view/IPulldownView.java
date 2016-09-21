package cn.com.nbd.nbdmobile.view;

public interface IPulldownView {
	
	public final static int RELEASE_TO_REFRESH = 0; // 释放刷新
	public final static int PULL_TO_REFRESH = 1; // 下拉刷新
	public final static int REFRESHING = 2; // 刷新中
	public final static int DONE = 3; // 刷新完成
	public final static int LOADING = 4; // 不知道这是什么状态
	
	/**
	 * 下拉过程中下拉的位置进度的回调
	 */
	void onPulldownDistance(int distance);
	
	/**
	 * 改变下拉头部的状态，界面更改
	 * @param state
	 */
	void onHeadStateChange(int state);
	
	int getPullState();
	
	
	
	

}
