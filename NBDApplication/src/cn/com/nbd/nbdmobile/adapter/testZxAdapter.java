package cn.com.nbd.nbdmobile.adapter;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import cn.com.nbd.nbdmobile.showview.MixNewsHolder;
import cn.com.nbd.nbdmobile.view.IPinnedAdapter;

import com.nbd.article.article.ArticleType;
import com.nbd.article.bean.ArticleInfo;

public class testZxAdapter extends InfosAdapter implements IPinnedAdapter{

	public testZxAdapter(Context context, List<ArticleInfo> listData,
			List<ArticleInfo> headData, boolean isDay, boolean isText) {
		super(context, listData, headData, isDay, isText);
	}

	@Override
	protected void buildHeadViewpage(MixNewsHolder holder,
			List<ArticleInfo> headListData) {
		super.buildHeadViewpage(holder, headListData);

		holder.layout_list_section.setVisibility(View.GONE);

	}

	@Override
	public void buildViewDatas(final MixNewsHolder mHolder,
			final ArticleInfo news, int position) {
		super.buildViewDatas(mHolder, news, position);

		mHolder.layout_list_section.setVisibility(View.GONE);
		if (news != null && news.getList_show_control() != null) {
			Log.e("HffDebug", position+"--"+news.getList_show_control().getDisplay_form()+news.getTitle());
		}

		mHolder.newsLayout.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (news.getList_show_control() != null) {
					if (news.getList_show_control().getDisplay_form() != 5
							&& news.getList_show_control().getDisplay_form() != 8
							&& onNewsClick != null) {
						onNewsClick.onNewsClick(mHolder.newsLayout, news);
					}
				} else {
					if (!news.getType().equals(ArticleType.VIDEO)
							&& onNewsClick != null) {
						onNewsClick.onNewsClick(mHolder.newsLayout, news);
					}
				}
			}
		});

		setThemeColor(mHolder, isDay);

		if (isText) {
			imgMap.put(mHolder, defaultImage);
			showViewByType(mHolder, news, position);
		} else {
			if (imgMap.get(mHolder) != null
					&& imgMap.get(mHolder).equals(news.getImage())) {
			} else {
				imgMap.put(mHolder, news.getImage());
				showViewByType(mHolder, news, position);
			}
		}
	}

	@Override
	public int getPinnedState(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void configurePinned(View header, int position, int alpha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPinnedClick(View header, int position) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void isPinnedPullDown(int pullDowm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onListViewScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
		//firstItem 包含下拉刷新投，下拉头为0
		//playPositon 不含投头，轮播为0 
		
		if (mVideoPlayPosition != -1) {
			if ((firstVisibleItem-1) >mVideoPlayPosition  || (firstVisibleItem+visibleItemCount-2)<mVideoPlayPosition) {
				if (mVideoView != null) {
					mVideoView.stopAndRelease();
					ViewGroup parent = (ViewGroup) mVideoView.getParent();
					if (parent != null) {
						parent.removeAllViews();
					}
					mVideoPlayPosition = -1;
				}
			}
		}
		
	}

	@Override
	public void onListScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onThemeChange(View headerView, boolean isDay) {
		// TODO Auto-generated method stub
		
	}
	
	

}
