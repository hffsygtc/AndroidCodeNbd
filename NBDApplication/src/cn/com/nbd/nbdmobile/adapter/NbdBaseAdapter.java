package cn.com.nbd.nbdmobile.adapter;

import java.util.List;

import cn.com.nbd.nbdmobile.utility.Options;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;

public abstract class NbdBaseAdapter<T, D> extends BaseAdapter {

	protected Context context;

	protected List<T> listData;

	protected List<D> headListData;

	protected LayoutInflater layoutInflater;

	protected ImageLoader imageLoader = ImageLoader.getInstance();

	protected DisplayImageOptions options;

	public NbdBaseAdapter(Context context, List<T> listData, List<D> headList) {
		this.context = context;
		this.listData = listData;
		this.headListData = headList;
		layoutInflater = LayoutInflater.from(context);
		options = getImageOptions();
	}

	protected DisplayImageOptions getImageOptions() {
		return Options.getListOptions();
	}

	@Override
	public int getCount() {
		if (headListData == null) {
			return listData == null ? 0 : listData.size();
		} else {
			return listData == null ? 1 : listData.size() + 1;
		}
	}

	@Override
	public Object getItem(int position) {
		if (headListData == null) {
			return listData == null ? null : listData.get(position);
		}else {
			return listData == null ? null : listData.get(position -1);
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setDataChange(List<T> listData,List<D> headList) {
		this.listData = listData;
		this.headListData = headList;
	}

	public View inflate(int layoutId) {
		return layoutInflater.inflate(layoutId, null);
	}

}
