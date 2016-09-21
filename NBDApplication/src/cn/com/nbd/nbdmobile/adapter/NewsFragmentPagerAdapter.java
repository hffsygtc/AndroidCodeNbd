package cn.com.nbd.nbdmobile.adapter;

import java.util.ArrayList;

import cn.com.nbd.nbdmobile.fragment.NewsCompanyFragment;
import cn.com.nbd.nbdmobile.fragment.NewsFastFragment;
import cn.com.nbd.nbdmobile.fragment.NewsListFragment;
import cn.com.nbd.nbdmobile.fragment.NewsMoneyFragment;
import cn.com.nbd.nbdmobile.fragment.SimpleFrgment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.view.ViewGroup;

/**
 * 新闻界面的fragment，里面拥有4个子栏目，维护和管理这四个栏目的生命周期
 * 
 * ****待优化
 * @author riche
 *
 */
public class NewsFragmentPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Fragment> fragments;
	private FragmentManager fm;

	public NewsFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		this.fm = fm;
	}

	public NewsFragmentPagerAdapter(FragmentManager fm,
			ArrayList<Fragment> fragments) {
		super(fm);
		this.fm = fm;
		this.fragments = fragments;
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public Fragment getItem(int position) {
//		switch (position) {
//		case 0:
//			return new NewsFastFragment();
//		case 1:
//			return new NewsListFragment();
//		case 2:
//			return new NewsCompanyFragment();
//		case 3:
//			return new NewsMoneyFragment();
//		case 4:
//			return SimpleFrgment.newInstance("title");
//		default:
//			break;
//		}
		return fragments.get(position);
	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	public void setFragments(ArrayList<Fragment> fragments) {
		if (this.fragments != null) {
			FragmentTransaction ft = fm.beginTransaction();
			for (Fragment f : this.fragments) {
				ft.remove(f);
			}
			ft.commit();
			ft = null;
			fm.executePendingTransactions();
		}
		this.fragments = fragments;
		notifyDataSetChanged();
	}

	@Override
	public Object instantiateItem(ViewGroup container, final int position) {
		Object obj = super.instantiateItem(container, position);
		return obj;
	}

}
