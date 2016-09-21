package com.nbd.article.utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.nbd.article.article.ArticleBase;
import com.nbd.article.bean.ArticleInfo;

public class ThreePicArticle extends ArticleBase {
	
	private static String TAG = "3PIC";
	private ImageView left;
	private ImageView mid;
	private ImageView right;

	private ArticleInfo info;

	private Context mContext;



	public ThreePicArticle(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
	

	public ThreePicArticle(Context context, AttributeSet attrs) {
		super(context, attrs);
		initUi(context);
		setListener();
	}


	/**
	 * 绘制该类型新闻的布局样式
	 */
	private void initUi(Context context) {
		// TODO 通过 getArticleInfo() 方法获取对应的数据，显示
		mContext = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		/**初始化控件*/
		
		
//		View rootView = inflater.inflate(R.layout., this);
		
	}
	


	/**
	 * 设置各控件的交互事件
	 */
	private void setListener() {
		// TODO Auto-generated method stub
		
	}

}
