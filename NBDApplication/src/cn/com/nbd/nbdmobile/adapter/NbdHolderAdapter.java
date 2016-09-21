package cn.com.nbd.nbdmobile.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class NbdHolderAdapter<T,D,H> extends NbdBaseAdapter<T,D> {

	public NbdHolderAdapter(Context context, List<T> listData,List<D> headListData) {
		super(context, listData, headListData);
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		H holder = null;
		if (convertView == null) {
			convertView = buildConverView(layoutInflater, position);
			holder = buildHolder(convertView);
			
			convertView.setTag(holder);
		}else {
			holder = (H) convertView.getTag();
		}
		
		Log.e("HFFBUG-->", holder.toString());
		
		if (headListData != null) {
			if (position == 0) {
				buildHeadViewpage(holder,headListData);
			}else {
				T t = listData.get(position -1);
				buildViewDatas(holder,t,position);
			}
		}else {
			T t = listData.get(position);
			buildViewDatas(holder,t,position);
		}
		return convertView;
	}

	/**
	 * 当存在头图的时候，给头图的viewpage的数据视图绑定
	 * @param holder
	 * @param headListData
	 */
	protected abstract void buildHeadViewpage(H holder, List<D> headListData);

	/**
	 * 创建 convertView
	 * @param layoutInflater
	 * @param position
	 * @return
	 */
	public abstract View buildConverView(LayoutInflater layoutInflater,
			int position);
	
	/**
	 * 建立视图的Holder，并未holder寻找id初始化
	 * @param convertView
	 * @return
	 */
	public abstract H buildHolder(View convertView);
	
	/**
	 * 绑定数据到视图
	 * @param holder
	 * @param t
	 * @param position
	 */
	public abstract void buildViewDatas(H holder,T t,int position);

}
