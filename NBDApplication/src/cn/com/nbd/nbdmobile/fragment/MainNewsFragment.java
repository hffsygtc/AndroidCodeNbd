package cn.com.nbd.nbdmobile.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.activity.FullScreenActivity;
import cn.com.nbd.nbdmobile.activity.SearchActivity;
import cn.com.nbd.nbdmobile.adapter.NewsFragmentPagerAdapter;
import cn.com.nbd.nbdmobile.adapter.NewsSectionAdapter;
import cn.com.nbd.nbdmobile.showview.NewsGsFragment;
import cn.com.nbd.nbdmobile.showview.NewsZxFragment;
import cn.com.nbd.nbdmobile.showview.NewsLcFragment;
import cn.com.nbd.nbdmobile.view.CustomListViewUnused;
import cn.com.nbd.nbdmobile.view.ViewPagerTab;
import cn.com.nbd.nbdmobile.view.ViewPagerTab.PageOnChangeListener;


/**
 * 新闻模块fragment 内含多个子模块
 * 管理各子模块的隐藏，销毁，释放
 * 
 */
public class MainNewsFragment extends Fragment {

	private final static String TAG = "NEWS_FRAGMENT";
	  private final String mPageName = "NewsFragment";
	
	private Activity activity;
	private ViewPager mPager; // 盛放四个可左右滑动fragment的容器
	private ViewPagerTab mTab; // 顶部的左右滑动变色的TAB标签容器

	private List<String> titles = Arrays.asList( "资讯","滚动", "公司", "理财"); // 标题组
	private List<Fragment> mContents = new ArrayList<Fragment>();
	
	
	NewsFastFragment kxFragment ; // 快讯滚动的模块
	NewsListFragment listviewFragment ;
//	NewsZxFragment listviewFragment ;
	NewsCompanyFragment gsFragment ; // 测试公司页面
//	NewsGsFragment gsFragment ; // 测试公司页面
	NewsMoneyFragment lcFragment ; //测试理财页面
//	NewsLcFragment lcFragment ; //测试理财页面
	
	private NewsSectionAdapter commenAdapter; // 测试的公司adapter

	private NewsFragmentPagerAdapter mAdapter; // 滑动页面的adapter
	private NewsFragmentPagerAdapter mmAdapter; // 滑动页面的adapter
	
	private RelativeLayout mSearchLayout;

	private int jzPosition;
	
	private boolean isDayTheme; 
	private boolean isTextMode; 
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initDatas();
		initAdapter();
		Log.d(TAG, "NewsFrg ==> Creat");

	}

	private void setListener() {

		mTab.setOnPageChangeListener(new PageOnChangeListener() {

			@Override
			public void onPagerSelected(int positon) {
			}
			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}
			@Override
			public void onPageScrollStateChanged(int state) {
				if (state == 1) {
					mPager.requestDisallowInterceptTouchEvent(true);
				}else if ( state == 2) {
					mPager.requestDisallowInterceptTouchEvent(false);
					
				}
			}
		});

		mPager.setCurrentItem(0);
		mTab.highLightText(0);
		
		mSearchLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//			Intent i = new Intent(activity,FullScreenActivity.class);
//			startActivity(i);
			Intent i = new Intent(activity,SearchActivity.class);
			startActivity(i);
			}
		});

	}

	// 初始化数据
	private void initDatas() {
		if (mContents != null && mContents.size() != 4) {
			mContents.clear();

			if (listviewFragment == null) {
				listviewFragment = new NewsListFragment();
//				listviewFragment = new NewsZxFragment();
				listviewFragment.initTheme(isDayTheme, isTextMode);
				
				
			}
			mContents.add(listviewFragment);

			if (kxFragment == null) {
				kxFragment = new NewsFastFragment();
				kxFragment.initTheme(isDayTheme);
			}
			mContents.add(kxFragment);

			if (gsFragment == null) {
				gsFragment = new NewsCompanyFragment();
//				gsFragment = new NewsGsFragment();
				gsFragment.initTheme(isDayTheme, isTextMode);
			}
			mContents.add(gsFragment);

//		
			if (lcFragment == null) {
				lcFragment = new NewsMoneyFragment();
//				lcFragment = new NewsLcFragment();
				lcFragment.initTheme(isDayTheme, isTextMode);
			}
			mContents.add(lcFragment);

		}
	}

	/**
	 * 通过这样的方法获取activity实例，以免在嵌套fragment过程中，再加载activity为null的问题
	 */
	@Override
	public void onAttach(Activity activity) {
		Log.d(TAG, "NewsFrg ==> onAttach");
		this.activity = activity;
		super.onAttach(activity);
	}

	/**
	 * 此方法意思为fragment是否可见 ,可见时候加载数据 不做这个处理，会加载多个fragment数据
	 */
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		Log.d(TAG, "NewsFrg ==> setVisible");
		if (isVisibleToUser) {
			// fragment可见时加载数据
		} else {
			// fragment不可见时不执行操作
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	/**
	 * 创建，加载对应Fragment的布局文件
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		Log.d(TAG, "NewsFrg ==> Creat View");
		
		activity.setTheme(R.style.DayTheme);

		View view = LayoutInflater.from(getActivity()).inflate(
				R.layout.activity_main_news_fragment, null);
		mPager = (ViewPager) view.findViewById(R.id.viewpager);
		mTab = (ViewPagerTab) view.findViewById(R.id.tab);
		mSearchLayout = (RelativeLayout) view.findViewById(R.id.main_title_search_layout);

		mTab.setVisibleTabCount(4);
		mTab.setTabItemTitles(titles);
		mPager.setAdapter(mAdapter);
		mTab.setViewPager(mPager, 0);

		mPager.setOffscreenPageLimit(1);

		setListener();
		return view;
	}

	private void initAdapter() {
		
		mAdapter = new NewsFragmentPagerAdapter(getChildFragmentManager(),(ArrayList<Fragment>) mContents);
	}

	/**
	 * 摧毁视图
	 */
	@Override
	public void onDestroyView() {
		Log.d(TAG, "NewsFrg ==> onDestroy View");
		super.onDestroyView();
		mAdapter = null;
	}

	/**
	 * 摧毁该Fragment，一般是FragmentActivity 被摧毁的时候伴随着摧毁
	 */
	@Override
	public void onDestroy() {
		Log.d(TAG, "NewsFrg ==> onDestroy");
		mAdapter = null;
		super.onDestroy();
	}

	
	public void setTheme(boolean isDay){
		Log.e(TAG, "SetTheme" + isDay);
		this.isDayTheme = isDay;
	}
	public void setTextMode(boolean isText){
		Log.e(TAG, "setMode" + isText);
		this.isTextMode = isText;
	}
	
	public void changeTheme(boolean isDay){
		this.isDayTheme = isDay;
		if (kxFragment != null) {
			kxFragment.changeTheme(isDay);
		}
		if (listviewFragment != null) {
			listviewFragment.changeTheme(isDay);
		}
		if (gsFragment != null) {
			gsFragment.changeTheme(isDay);
		}
		if (lcFragment != null) {
			lcFragment.changeTheme(isDay);
		}
	}
	public void changeMode(boolean isText){
		this.isTextMode = isText;
//		if (kxFragment != null) {
//			kxFragment.changeMode(isText);
//		}
		if (listviewFragment != null) {
			listviewFragment.changeMode(isText);
		}
		if (gsFragment != null) {
			gsFragment.changeMode(isText);
		}
		if (lcFragment != null) {
			lcFragment.changeMode(isText);
		}
	}
	
	public void stopVideoPlaying(){
//		if (listviewFragment != null) {
//			listviewFragment.stopVideoPlaying();
//		}
//		if (gsFragment != null) {
//			gsFragment.stopVideoPlaying();
//		}
//		if (lcFragment != null) {
//			lcFragment.stopVideoPlaying();
//		}
	}

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPageEnd(mPageName);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onPageStart(mPageName);
    }
}
