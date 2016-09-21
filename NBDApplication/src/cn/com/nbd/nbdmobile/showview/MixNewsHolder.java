package cn.com.nbd.nbdmobile.showview;

import android.support.v4.view.ViewPager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 各种混合式新闻展现方式
 * 
 * @author he
 * 
 */
public class MixNewsHolder {

	public LinearLayout mHolderLayout;// 整个列表项布局块

	/**
	 * 顶部的ViewPager 布局
	 */
	public RelativeLayout viewpagerLayout;// viewpager的容器
	public ViewPager pager; // 顶部的viewpager，就第一个子项目显示
	public TextView pagerText; // viewpager下面的文字栏
	public RelativeLayout pagerBottomLayout;// viewpager下面的文字栏目
	public ImageView pagerTagImg; // 轮播新闻为图集时显示图集的标志
	public ImageView leftPoint;
	public ImageView midPoint;
	public ImageView rightPoint;

	// 新闻部分的内容布局
	public RelativeLayout newsLayout;

	/**
	 * 普通文章样式的新闻
	 */
	// 一般常见新闻布局
	public RelativeLayout normalLayout;
	// 一般新闻的图片
	public ImageView normalImage;
	// 一般新闻的新闻标题
	public TextView normalContentTxt;
	// 一般新闻左下角评论数文字
	public TextView normalCommonTxt;
	// 视频新闻的右下角标志TAG图标
	public ImageView normalTagImg;
	// 新闻左下角显示文章的来源
	public TextView ori_source;

	/**
	 * 三张图片新闻的样式
	 */
	// 用于显示隐藏的大布局
	public RelativeLayout threePicLayout;
	// 标题栏
	public TextView threePicTitle;
	// 左边的图片
	public ImageView threePicLeft;
	// 中间的图片
	public ImageView threePicMiddle;
	// 右边的图片
	public ImageView threePicRight;
	// 左下角的图片TAG
	public ImageView threePicTag;
	// 右下角的评论数
	public TextView threePicCommonTxt;
	// 左下角的文章来源
	public TextView threePicOriTxt;

	/**
	 * 大图类型的图片新闻
	 */
	// 大图类型的布局
	public RelativeLayout largeImgLayout;
	// 大图类型的标题
	public TextView largeImgTitleTxt;
	// 大图图片的容器
	public RelativeLayout largeImageLayout;
	// 大图的图片
	public ImageView largeImgImage;
	// 大图类型的右下角图片个数
	public TextView largeImgNum;
	// 普通的大图标签
	public ImageView largeImgTag;
	// 图书馆的大图标签
	public ImageView bookImageTag;
	// 大图的阅读数量
	public TextView largeImgReadNum;
	// 广告标题栏容器
	public RelativeLayout largeImgAdvTitleLayout;
	// 广告标题
	public TextView largeImgAdvTitle;
	// 普通大图底部的布局
	public RelativeLayout largeImgBottomLayout;

	/**
	 * 视频类型的新闻
	 */
	// 视频类型的布局
	public RelativeLayout videoLayout;
	// 视频的标题
	public TextView videoTitle;
	// 广告视频的标题栏
	public RelativeLayout videoAdvTitleLayout;
	//广告视频的标题
	public TextView videoAdvTtitle;
	
	// 视频的播放容器
	public RelativeLayout videoPlayLayout;
	// 视频的图片的容器
	public RelativeLayout videoImageLayout;
	// 视频的背景图片
	public ImageView videoImage;
	// 视频的播放按钮
	public ImageView videoPlayIcon;
	// 视频的遮罩层
	public ImageView videoCover;
	
	//视频底部的内容布局
	public RelativeLayout videoBottomLayout;
	// 视频的标签
	public ImageView videoTag;
	// 视频的底部来源
	public TextView videoOriText;
	// 视频的播放按钮
	public ImageView videoBtmPlay;
	// 视频的播放次数
	public TextView videoPlayNum;
	// 视频的分享按钮
	public ImageView videoBtmShare;

	/**
	 * 头部的停靠栏布局
	 */
	// 头部的日期部分
	public LinearLayout layout_list_section;
	// 左边switch的点击控件
	public RelativeLayout section_left_layout;
	// 头部左边的switch选项
	public TextView section_left;
	// 头部左边下标
	public TextView section_left_bottom;
	// 头部右边的switch点击控件
	public RelativeLayout section_right_layout;
	// 头部右边的switch选项
	public TextView section_right;
	// 头部右边下标
	public TextView section_right_bottom;

}
