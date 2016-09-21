package cn.com.nbd.nbdmobile.fragment;

import java.util.LinkedList;
import java.util.List;

import cn.com.nbd.nbdmobile.adapter.InfosAdapter;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.view.RefreshListview;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.ArticleInfoCallback;
import com.nbd.network.bean.RequestType;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * 新闻类fragment父类方法类
 * 
 * @author he
 * 
 */
public abstract class BaseNewsFragment extends Fragment {

	protected Activity activity;

	protected RelativeLayout mainLayout;

	protected RefreshListview listView;

	protected View view;

	protected List<ArticleInfo> headList = new LinkedList<ArticleInfo>();
	protected List<ArticleInfo> headDbList = new LinkedList<ArticleInfo>();

	protected List<ArticleInfo> contentList = new LinkedList<ArticleInfo>();
	protected List<ArticleInfo> contentDbList = new LinkedList<ArticleInfo>();

	protected InfosAdapter adapter;

	protected Handler handler;

	protected SharedPreferences firstSp;
	protected SharedPreferences.Editor firstEditor;

	protected boolean isDataClear;
	protected boolean isDayTheme;
	protected boolean isTextMode;
	protected boolean isHeadReturn;
	protected boolean isContentReturn;
	protected boolean isCanLoadMore;

	// 是否可见
	protected boolean isVisible;
	// 标志位，标志界面已经初始化完成
	public boolean isPrepared = false;

	/**
	 * 构造函数，Fragmet的构造函数是不支持直接参数的传入的
	 */
	public BaseNewsFragment() {
		initHandler();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (getUserVisibleHint()) {
			isVisible = true;
			onVisible();
		} else {
			isVisible = false;
			onInVisible();
		}
	}

	protected void onInVisible() {
	};

	protected void onVisible() {
		// 加载数据
		loadData();
	};

	protected void loadData() {

		if (isVisible && isPrepared) {

			//显示的数据组有内容，直接刷新一下界面
			if (headList != null && headList.size() > 0 && contentList != null
					&& contentList.size() > 0) {
				adapter.notifyDataSetChanged();
			}else {
				//获取本地数据库的内容
				ArticleConfig headDbConfig = getHeadConfig();
				headDbConfig.setLocalArticle(true);
				ArticleManager.getInstence().getArticleInfo(headDbConfig,
						new ArticleInfoCallback() {
					
					@Override
					public void onArticleInfoCallback(
							List<ArticleInfo> infos, RequestType type) {
						
						
						
					}
				});
				
				
				
				
				
				
			}


		}

	};

	/**
	 * 获取头部轮播的请求数据
	 * 
	 * @return
	 */
	protected abstract ArticleConfig getHeadConfig();

	@Override
	@Deprecated
	public void onAttach(Activity activity) {
		this.activity = activity;
		firstSp = activity.getSharedPreferences("FirstLoad",
				activity.MODE_PRIVATE);
		firstEditor = firstSp.edit();
		super.onAttach(activity);
	}

	@Override
	@Nullable
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		initViews();

		// 设置界面主题属性
		listView.setTheme(isDayTheme);

		if (isDayTheme) {
			ThemeUtil.setBackgroundDay(activity, mainLayout);
		} else {
			ThemeUtil.setBackgroundNight(activity, mainLayout);
		}

		isPrepared = true;

		loadData();

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	/**
	 * 初始化消息处理器
	 */
	protected void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {

				super.handleMessage(msg);
			}
		};
	}

	/**
	 * 初始化界面控件，主View,listview,mainLayout
	 */
	protected abstract void initViews();

	/**
	 * 初始化界面的主题和模式
	 * 
	 * @param isDay
	 * @param isText
	 */
	public void initTheme(boolean isDay, boolean isText) {
		this.isDayTheme = isDay;
		this.isTextMode = isText;
	};

	/**
	 * 更改界面的主题模式
	 * 
	 * @param isDay
	 */
	public void changeTheme(boolean isDay) {
		this.isDayTheme = isDay;
		if (mainLayout != null) {
			if (isDayTheme) {
				ThemeUtil.setBackgroundDay(activity, mainLayout);
			} else {
				ThemeUtil.setBackgroundNight(activity, mainLayout);
			}
		}

		// 传递给listview用于更改pinned停靠栏的颜色
		if (listView != null) {
			listView.setTheme(isDayTheme);
		}

		// 将主题模式改变传递给adapter，执行item的主题颜色更改
		if (adapter != null) {
			adapter.changeThem(isDayTheme);
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 更改文字模式的切换
	 * 
	 * @param isText
	 */
	public void changeMode(boolean isText) {
		this.isTextMode = isText;
		if (adapter != null) {
			adapter.changeMode(isText);
			adapter.notifyDataSetChanged();
		}
	}

}
