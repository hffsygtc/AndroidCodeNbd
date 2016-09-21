package cn.com.nbd.nbdmobile.utility;

import android.content.Context;
import android.graphics.Bitmap;

import cn.com.nbd.nbdmobile.R;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class Options {
	/** 新闻列表中用到的图片加载配置 */
	public static DisplayImageOptions getListOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				// 设置图片在下载期间显示的图片
				.showImageOnLoading(R.drawable.nbd_test_bg)
				// 设置图片Uri为空或是错误的时候显示的图片
				.showImageForEmptyUri(R.drawable.nbd_test_bg)
				// 设置图片加载/解码过程中错误时候显示的图片
				.showImageOnFail(R.drawable.nbd_test_bg)
				.cacheInMemory(false)
				// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)
				// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
				// .decodingOptions(android.graphics.BitmapFactory.Options
				// decodingOptions)//设置图片的解码配置
				.considerExifParams(true)
				// 设置图片下载前的延迟
				// .delayBeforeLoading(int delayInMillis)//int
				// delayInMillis为你设置的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// 。preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				// .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
				.displayer(new FadeInBitmapDisplayer(100))// 淡入
				.build();
		return options;
	}

	/** 新闻列表中用到的图片加载配置 */
	public static DisplayImageOptions getPaperOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		// 设置图片在下载期间显示的图片
		.showImageOnLoading(R.drawable.default_paper_bg)
		// 设置图片Uri为空或是错误的时候显示的图片
		.showImageForEmptyUri(R.drawable.default_paper_bg)
		// 设置图片加载/解码过程中错误时候显示的图片
		.showImageOnFail(R.drawable.default_paper_bg)
		.cacheInMemory(false)
		// 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true)
		// 设置下载的图片是否缓存在SD卡中
		.considerExifParams(true)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
		// .decodingOptions(android.graphics.BitmapFactory.Options
		// decodingOptions)//设置图片的解码配置
		.considerExifParams(true)
		// 设置图片下载前的延迟
		// .delayBeforeLoading(int delayInMillis)//int
		// delayInMillis为你设置的延迟时间
		// 设置图片加入缓存前，对bitmap进行设置
		// 。preProcessor(BitmapProcessor preProcessor)
		.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
		// .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
		.displayer(new FadeInBitmapDisplayer(100))// 淡入
		.build();
		return options;
	}
	/** 新闻列表中用到的图片加载配置 */
	public static DisplayImageOptions getHeadOptions(Context context) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		// 设置图片在下载期间显示的图片
		.showImageOnLoading(R.drawable.self_center_default_head)
		// 设置图片Uri为空或是错误的时候显示的图片
		.showImageForEmptyUri(R.drawable.self_center_default_head)
		// 设置图片加载/解码过程中错误时候显示的图片
		.showImageOnFail(R.drawable.self_center_default_head)
		.cacheInMemory(false)
		// 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true)
		// 设置下载的图片是否缓存在SD卡中
		.considerExifParams(true)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
		// .decodingOptions(android.graphics.BitmapFactory.Options
		// decodingOptions)//设置图片的解码配置
		.considerExifParams(true)
		// 设置图片下载前的延迟
		// .delayBeforeLoading(int delayInMillis)//int
		// delayInMillis为你设置的延迟时间
		// 设置图片加入缓存前，对bitmap进行设置
		// 。preProcessor(BitmapProcessor preProcessor)
		.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
		// .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
		.displayer(new RoundedBitmapDisplayer(dip2px(context, 36), 0))
//		.displayer(new FadeInBitmapDisplayer(100))// 淡入
		.build();
		return options;
	}
	
	/** 新闻列表中用到的图片加载配置 */
	public static DisplayImageOptions getSplashOptions(Context context) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		// 设置图片在下载期间显示的图片
		.cacheInMemory(false)
		// 设置下载的图片是否缓存在内存中
		.cacheOnDisc(true)
		// 设置下载的图片是否缓存在SD卡中
		.considerExifParams(true)
		.imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片以如何的编码方式显示
		.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
		// .decodingOptions(android.graphics.BitmapFactory.Options
		// decodingOptions)//设置图片的解码配置
		.considerExifParams(true)
		// 设置图片下载前的延迟
		// .delayBeforeLoading(int delayInMillis)//int
		// delayInMillis为你设置的延迟时间
		// 设置图片加入缓存前，对bitmap进行设置
		// 。preProcessor(BitmapProcessor preProcessor)
		.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
//		.displayer(new FadeInBitmapDisplayer(100))// 淡入
		.build();
		return options;
	}
	
	private static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	
	/**图集加载的显示页面，就不设置默认图片，以免缩放比例计算时，比例出错*/
	public static DisplayImageOptions getGalleryOptions() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.cacheInMemory(false)
				// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)
				// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
				// .decodingOptions(android.graphics.BitmapFactory.Options
				// decodingOptions)//设置图片的解码配置
				.considerExifParams(true)
				// 设置图片下载前的延迟
				// .delayBeforeLoading(int delayInMillis)//int
				// delayInMillis为你设置的延迟时间
				// 设置图片加入缓存前，对bitmap进行设置
				// 。preProcessor(BitmapProcessor preProcessor)
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				// .displayer(new RoundedBitmapDisplayer(20))//是否设置为圆角，弧度为多少
				.displayer(new FadeInBitmapDisplayer(100))// 淡入
				.build();
		return options;
	}

	
}
