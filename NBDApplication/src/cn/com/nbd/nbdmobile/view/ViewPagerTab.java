package cn.com.nbd.nbdmobile.view;

import java.util.List;

import cn.com.nbd.nbdmobile.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public class ViewPagerTab extends LinearLayout {
	private Context mContext;

	private Paint mPaint; // 画笔

	private Path mPath; // 没有三角形的画笔，就弄一个线条的路径

	private int mTriangleWidth; // 三角形指示器的宽度

	private int mTriangleHeight; // 三角形指示器的高度

	private static final float RADIO_TRIANGLE_WIDTH = 1 / 6F; // 三角形的宽度占TAB宽度的比例
	// private static final int DIMENSION_TRIANGLE_WIDTH_MAX = (int)
	// ((int)getScreenWidth()/3*RADIO_TRIANGLE_WIDTH);

	private int mInitTranslationX; // 初始化

	private int mTranslationX;

	private int mTabVisibleCount; // 可见的tab数量

	private static final int COUNT_DEFAULT_TAB = 5;

	private static final int COLOR_TEXT_NORMAL = 0xffeb716b;
	private static final int COLOR_TEXT_HIGHTLIGHR = 0xffffffff;

	private List<String> mTitles;

	private ViewPager mViewPager;

	private int TabWidth; // 控件XML文件中定义的宽度

	public ViewPagerTab(Context context) {
		this(context, null);
	}

	public ViewPagerTab(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Log.d("PagerTab", "构造函数");
		mContext = context;
		// 获取可见TAB的数量,自定义属性，在XML布局文件中定义
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ViewPagerTab);

		mTabVisibleCount = a.getInt(R.styleable.ViewPagerTab_visible_tab_count,
				COUNT_DEFAULT_TAB);
		if (mTabVisibleCount < 0) {
			mTabVisibleCount = COUNT_DEFAULT_TAB;
		}

		a.recycle();

		// 初始化画笔
		mPaint = new Paint();
		mPaint.setAntiAlias(true); // 抗锯齿属性
		mPaint.setColor(Color.parseColor("#ffffff"));
		mPaint.setStyle(Style.FILL); // 填充
		mPaint.setPathEffect(new CornerPathEffect(3));// 设置一下圆角，减小尖锐感

	}

	/**
	 * 绘制指示器图标
	 */
	@Override
	protected void dispatchDraw(Canvas canvas) {

		// Log.d("PagerTab", "dispatchDraw");

		canvas.save();

		canvas.translate(mInitTranslationX + mTranslationX, getHeight() + 2);
//		 canvas.drawPath(mPath, mPaint);
//		canvas.drawCircle(mInitTranslationX + mTranslationX, getHeight() + 2, 16515165, mPaint);

		canvas.restore();

		// highLightText(0);

		super.dispatchDraw(canvas);
	}

	/**
	 * 滑动的时候绘制移动的指示器
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// Log.d("PagerTab", "onSizeChanged");

		mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGLE_WIDTH);

		mInitTranslationX = w / mTabVisibleCount / 2 - mTriangleWidth / 2;

//		 initTriangle();
	}

	/**
	 * 加载完XML文件后调用执行设置TAB
	 */
	@Override
	protected void onFinishInflate() {

		// Log.d("PagerTab", "onFinishInflate");

		super.onFinishInflate();

		int cCount = getChildCount();
		if (cCount == 0)
			return;

		for (int i = 0; i < cCount; i++) {
			View view = getChildAt(i);
			LinearLayout.LayoutParams lp = (LayoutParams) view
					.getLayoutParams();

			lp.weight = 0;
			lp.width = TabWidth / mTabVisibleCount;
			view.setLayoutParams(lp);
		}

		// highLightText(0);
		// setItemClickEvent();
	}

	// ********由于控件两把有设置其余图标，不能全屏，所有改为onMeasure()方法获取控件本身的高度******
	// /**
	// * 获得屏幕宽度
	// *
	// * @return
	// */
	// private int getScreenWidth() {
	// WindowManager vm = (WindowManager) getContext().getSystemService(
	// Context.WINDOW_SERVICE);
	// DisplayMetrics outMetrics = new DisplayMetrics();
	// vm.getDefaultDisplay().getMetrics(outMetrics);
	// Log.d("hff", "!!!!!!!!!!"+outMetrics.widthPixels);
	// return outMetrics.widthPixels;
	//
	// }

	/**
	 * 初始化指示标 画一个三角形
	 */
	private void initTriangle() {

		mTriangleHeight = mTriangleWidth / 2;

		mPath = new Path();
		mPath.moveTo(0, 0);
		 mPath.lineTo(mTriangleWidth, 0);
		 mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
		mPath.close();

	}

	/**
	 * 指示器跟随手指一起滚动
	 * 
	 * @param position
	 * @param positionOffset
	 */

	public void scroll(int position, float positionOffset) {

		int tabWidth = getWidth() / mTabVisibleCount;
		mTranslationX = (int) (tabWidth * (positionOffset + position));

		// 容器移动，当TAB处于移动至最后一个时
		if (position >= (mTabVisibleCount - 1) && positionOffset > 0
				&& getChildCount() > mTabVisibleCount) {
			if (mTabVisibleCount != 1) {
				this.scrollTo((position - (mTabVisibleCount - 1)) * tabWidth
						+ (int) (tabWidth * positionOffset), 0);

			} else {
				this.scrollTo(position * tabWidth
						+ (int) (tabWidth * positionOffset), 0);
			}

		}

		invalidate(); // 重绘

	}

	/**
	 * 提供给外部的设置TAB数量和内容的方法
	 * 
	 * @param titles
	 */
	public void setTabItemTitles(List<String> titles) {
		if (titles != null && titles.size() > 0) {
			this.removeAllViews();
			mTitles = titles;
		}

		// setItemClickEvent();

	}

	/**
	 * 设置可见的TAB数量
	 * 
	 * @param count
	 */
	public void setVisibleTabCount(int count) {
		mTabVisibleCount = count;
	}

	/**
	 * 根据title创建Tab
	 * 
	 * @param title
	 * @return
	 */
	private View generateTextView(String title, int color) {
		RelativeLayout wrraperLayout = new RelativeLayout(mContext);
		RelativeLayout.LayoutParams wrraperParams = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		wrraperParams.width = TabWidth / mTabVisibleCount;
		wrraperLayout.setLayoutParams(wrraperParams);

		TextView tv = new TextView(mContext);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.CENTER_IN_PARENT);
		tv.setText(title);
		tv.setGravity(Gravity.CENTER);
//		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 45);
		tv.setId(1);
		if (color == 0) {
			tv.setTextColor(mContext.getResources().getColor(
					R.color.main_title_txt_hightlight));
		} else {
			tv.setTextColor(mContext.getResources().getColor(
					R.color.main_title_txt));
		}
		tv.setLayoutParams(lp);

		wrraperLayout.addView(tv);
		
		ImageView circleImg = new ImageView(mContext);
		RelativeLayout.LayoutParams circleLp = new RelativeLayout.LayoutParams(12,12);
//		RelativeLayout.LayoutParams circleLp = new RelativeLayout.LayoutParams(dip2px(mContext, 4), dip2px(mContext, 4));
	
		circleLp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		circleLp.addRule(RelativeLayout.BELOW, 1);
//		circleLp.topMargin = dip2px(mContext, 3);
		circleLp.topMargin = 12;
		circleImg.setBackgroundResource(R.drawable.main_title_tab_point);
		circleImg.setLayoutParams(circleLp);
		if (color == 0) {
			circleImg.setVisibility(View.VISIBLE);
		} else {
			circleImg.setVisibility(View.GONE);
		}
		wrraperLayout.addView(circleImg);
		
		
		// tv.setTextColor(COLOR_TEXT_NORMAL);
		return wrraperLayout;
		
		
		
		
//		TextView tv = new TextView(mContext);
//		LinearLayout.LayoutParams lp = new LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//		lp.width = TabWidth / mTabVisibleCount;
//		tv.setText(title);
//		tv.setPadding(dip2px(mContext,20), 0, 0, 0);
//		tv.setGravity(Gravity.CENTER_VERTICAL);
//		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
//		if (color == 0) {
//			tv.setTextColor(mContext.getResources().getColor(
//					R.color.main_title_txt_hightlight));
//		} else {
//			tv.setTextColor(mContext.getResources().getColor(
//					R.color.main_title_txt));
//		}
//
//		// tv.setTextColor(COLOR_TEXT_NORMAL);
//		tv.setLayoutParams(lp);
//		return tv;
	}

	/**
	 * 由于内部监听了viewPager的 onPagerChangeListener,得定义一根接口暴露给外部监听对应的事件变化
	 * 
	 * @author riche
	 *
	 */
	public interface PageOnChangeListener {

		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels);

		public void onPagerSelected(int positon);

		public void onPageScrollStateChanged(int state);
	}

	public PageOnChangeListener mListener;

	public void setOnPageChangeListener(PageOnChangeListener listener) {
		this.mListener = listener;

	}

	/**
	 * 设置关联的viewPager
	 * 
	 * @param viewPager
	 * @param pos
	 */
	@SuppressWarnings("deprecation")
	public void setViewPager(ViewPager viewPager, int pos) {
		mViewPager = viewPager;
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {

				highLightText(arg0);

				if (mListener != null) {
					mListener.onPagerSelected(arg0);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// tabWidth*positionOffset + position*tabWidth
				scroll(position, positionOffset);
				if (mListener != null) {
					mListener.onPageScrolled(position, positionOffset,
							positionOffsetPixels);
				}

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

				if (mListener != null) {
					mListener.onPageScrollStateChanged(arg0);
				}

			}
		});

		// Log.d("hff", "creat positon " + pos);
		mViewPager.setCurrentItem(pos);
		highLightText(pos);
	}

	/**
	 * 重置TAB的文本颜色
	 */
	private void resetTextViewColor() {
		for (int i = 0; i < getChildCount(); i++) {
			ViewGroup viewGroup = (ViewGroup) getChildAt(i);
			if (viewGroup!=null && viewGroup.getChildCount()>1) {
				View view = viewGroup.getChildAt(0);
				View pointView = viewGroup.getChildAt(1);
				if (view instanceof TextView) {
					((TextView) view).setTextColor(mContext.getResources()
							.getColor(R.color.main_title_txt));
					
				}
				
				if (pointView instanceof ImageView) {
					pointView.setVisibility(View.GONE);
				}
				
			}
		}

	}

	/**
	 * 当选中对应的TAB的时候，将文件进行一个高亮的处理
	 * 
	 * @param pos
	 */
	public void highLightText(int pos) {
		resetTextViewColor();
		ViewGroup viewGroup = (ViewGroup) getChildAt(pos);
		if (viewGroup!=null && viewGroup.getChildCount() > 1) {
			View view = viewGroup.getChildAt(0);
			View pointView = viewGroup.getChildAt(1);
			if (view!= null && view instanceof TextView) {
				// ((TextView) view).setTextColor(COLOR_TEXT_HIGHTLIGHR);
				((TextView) view).setTextColor(mContext.getResources().getColor(
						R.color.main_title_txt_hightlight));
			}
			if (pointView instanceof ImageView) {
				pointView.setVisibility(View.VISIBLE);
			}
		}

	}

	/**
	 * 设置TAB的点击事件
	 */
	private void setItemClickEvent() {
		int cCount = getChildCount();

		for (int i = 0; i < cCount; i++) {
			final int j = i;

			View view = getChildAt(i);

			view.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					mViewPager.setCurrentItem(j);

				}
			});
		}
	}

	/**
	 * 绘制控件之前，获取控件的XML属性中控件的宽度值
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		// Log.d("PagerTab", "measure");

		TabWidth = getMeasuredWidth();
//		Log.e("head tab", "==>" + mTitles.size());
		if (getChildCount() == mTitles.size()) {

		} else {
			//当数量不等的时候，先移除掉现有的子view，再生成添加
			int size = getChildCount();
			for (int i = 0; i < size; i++) {
				removeViewAt(0);
			}

			for (int i = 0; i < mTitles.size(); i++) {
				if (i == 0) {
					addView(generateTextView(mTitles.get(i), 0));
				} else {
					addView(generateTextView(mTitles.get(i), 1));
				}
			}
		}

		setItemClickEvent();

		//
		// for (String title : mTitles) {
		//
		// addView(generateTextView(title));
		// }
	}
	
	private int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
