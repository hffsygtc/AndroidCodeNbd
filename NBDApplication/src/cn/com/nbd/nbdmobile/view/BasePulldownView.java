package cn.com.nbd.nbdmobile.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

/**
 * 下拉刷新的头
 * 
 * 暂时限于LINEARLAYOUT的布局文件
 * 
 * @author he
 * 
 */
public abstract class BasePulldownView extends LinearLayout {

	public final static int RELEASE_TO_REFRESH = 0; // 释放刷新
	public final static int PULL_TO_REFRESH = 1; // 下拉刷新
	public final static int REFRESHING = 2; // 刷新中
	public final static int DONE = 3; // 刷新完成
	public final static int LOADING = 4; // 不知道这是什么状态

	protected int headState;
	protected int headHeight;

	public BasePulldownView(Context context) {
		super(context);
	}

	public BasePulldownView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BasePulldownView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	/**
	 * 设置下拉头的显示状态
	 * 
	 * @param state
	 */
	public abstract void setState(int state);

	/**
	 * 下拉是拉动的距离，计算比例，设置缩放旋转动画
	 * 
	 * @param distance
	 */
	public abstract void setDistance(int distance);

	/**
	 * 获取头部的状态
	 * 
	 * @return
	 */
	public int getPullState() {
		return headState;
	}

	/**
	 * 获取头部的高度
	 * 
	 * @return
	 */
	public int getPullHeight() {
		return headHeight;
	}

}
