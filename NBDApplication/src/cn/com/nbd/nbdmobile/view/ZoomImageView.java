package cn.com.nbd.nbdmobile.view;

import android.R.integer;
import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

public class ZoomImageView extends ImageView implements OnGlobalLayoutListener,
		OnScaleGestureListener, OnTouchListener {

	private boolean mOnce = false;

	// 初始化时的缩放的值
	private float mInitScale;
	// 双击放大时达到的值
	private float mMidScale;
	// 放大的最大的值
	private float mMaxSacle;

	/**--------------多点触控缩放------------**/
	//用于缩放的9维数组矩阵，用其封装的post方法即可
	private Matrix mScaleMatrix;
	// 捕获用户多指触控时缩放的比例
	private ScaleGestureDetector mScaleGestureDetector;
	// 记录上一次多点触控的数量
	private int mLastPointerCount;


	/**---------------滑动平移--------------------**/
	private float mLastX;  //上一次滑动手指的中心点
	private float mLastY; //上一次滑动手指的中心点

	private int mTouchSlop;  //最小滑动的距离，取系统的
	private boolean isCanDrag;  //是否能被滑动，如果滑动的时候手指变化，中心点就滑动，不让滑动

	private RectF matrixRectF;

	private boolean isCheckLeftAndRight;   //滑动的时候，检测左右边界
	private boolean isTopAndBottom;  //滑动的时候，检测上下边界

	private GestureDetector mGestureDetector;
	private boolean isAutoScale;   //双击时，通过postdelay的方法，延缓放大缩小的效果

	private Context mContext;
	
	public interface onImageLongPressListener{
		void onTapStateChange();
	}
	
	private onImageLongPressListener mListener;
	
	public void setOnImgaePressListener(onImageLongPressListener listener){
		this.mListener = listener;
	}

	public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {

		super(context, attrs, defStyle);

		this.mContext = context;
		mScaleMatrix = new Matrix();
		setScaleType(ScaleType.MATRIX);

		mScaleGestureDetector = new ScaleGestureDetector(context, this);

		setOnTouchListener(this);

		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop()*3;
		Log.e("IMAGEVIEW--", "MIN GAP==>" + mTouchSlop);

		mGestureDetector = new GestureDetector(context,
				new GestureDetector.SimpleOnGestureListener() {
					@Override
					public boolean onDoubleTap(MotionEvent e) {
						if (isAutoScale) {
							return true;
						}
						float x = e.getX();
						float y = e.getY();

						if (getScale() < mMidScale) {
							// mScaleMatrix.postScale(mMidScale / getScale(),
							// mMidScale / getScale(), x, y);
							// setImageMatrix(mScaleMatrix);

							postDelayed(new AutoScaleRunnable(mMidScale, x, y),
									16);
							isAutoScale = true;
						} else {
							// mScaleMatrix.postScale(mInitScale / getScale(),
							// mInitScale / getScale(), x, y);
							// setImageMatrix(mScaleMatrix);
							postDelayed(
									new AutoScaleRunnable(mInitScale, x, y), 16);
							isAutoScale = true;
						}

						return true;
					}
					
					
//					@Override
//					public void onLongPress(MotionEvent e) {
//						super.onLongPress(e);
//						mListener.onTapStateChange();
//					}
					
					/**
					 * 图片单击事件传递出去，用于显示和隐藏文字内容
					 */
					@Override
					public boolean onSingleTapConfirmed(MotionEvent e) {
						// TODO Auto-generated method stub
						mListener.onTapStateChange();
						return super.onSingleTapConfirmed(e);
					}
					
				});
		
		// init
	}

	private class AutoScaleRunnable implements Runnable {

		/**
		 * 缩放的目标值
		 */
		private float mTargetScale;
		// 缩放的中心点
		private float x;
		private float y;

		private final float BIGGER = 1.07f;
		private final float SMALL = 0.93f;

		private float tmpScale;

		public AutoScaleRunnable(float mTargetScale, float x, float y) {
			this.mTargetScale = mTargetScale;
			this.x = x;
			this.y = y;

			if (getScale() < mTargetScale) {
				tmpScale = BIGGER;
			}

			if (getScale() > mTargetScale) {
				tmpScale = SMALL;
			}
		}

		@Override
		public void run() {

			// 进行缩放
			mScaleMatrix.postScale(tmpScale, tmpScale, x, y);
			checkBorderAndCenterWhenScale();
			setImageMatrix(mScaleMatrix);

			float currentScale = getScale();
			if ((tmpScale > 1.0f && currentScale < mTargetScale)
					|| (tmpScale < 1.0f && currentScale > mTargetScale)) {
				postDelayed(this, 16);
			} else { // 设置为我们的目标值
				float scale = mTargetScale / currentScale;
				mScaleMatrix.postScale(scale, scale, x, y);
				checkBorderAndCenterWhenScale();
				setImageMatrix(mScaleMatrix);

				isAutoScale = false;
			}

		}

	}

	public ZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ZoomImageView(Context context) {
		this(context, null);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	/**
	 * 获取ImageView加载完成的图片
	 */
	@Override
	public void onGlobalLayout() {

		if (!mOnce) {

			// DisplayMetrics metrics = new DisplayMetrics();
			// ((Activity)mContext).getWindowManager().getDefaultDisplay().getMetrics(metrics);
			// int screenHeight = metrics.heightPixels;
			// Log.e("SCREEN-HEIGHT", screenHeight+"");

			// 得到控件的宽和高
			int width = getWidth();
			int height = getHeight();
			// 得到我们的图片，以及宽和高
			Drawable d = getDrawable();
			if (d == null) {
				return;
			}

			int dw = d.getIntrinsicWidth();
			int dh = d.getIntrinsicHeight();

			float scale = 1.0f;

			Log.e("ImgeView----", "width==" + width + "==height==" + height
					+ "dw==" + dw + "dh==" + dh);

			/**
			 * 如果图片的宽度大于控件的宽度，但是高度小于控件的高度，我们将其缩小
			 */
			if (dw > width && dh < height) {
				scale = width * 1.0f / dw;
			}
			/**
			 * 如果图片的高度大于控件的高度，但是宽度小于控件的宽度，我们将其缩小
			 */
			if (dh > height && dw < width) {
				scale = height * 1.0f / dh;
			}

			if ((dw > width && dh > height) || (dw < width && dh < height)) {
				scale = Math.min(width * 1.0f / dw, height * 1.0f / dh);
			}

			/**
			 * 得到初始化时缩放的比例
			 */
			mInitScale = scale;
			mMaxSacle = mInitScale * 4;
			mMidScale = mInitScale * 2;

			// 将图片移动至屏幕的中间，水平方向移动，水平居中即可

			int dx = getWidth() / 2 - dw / 2;
			int dy = getHeight() / 2 - dh / 2;
			Log.e("IMAGEVIEW--", "MOVE==" + dx + "scale==" + mInitScale);

			mScaleMatrix.postTranslate(dx, dy);
			mScaleMatrix.postScale(mInitScale, mInitScale, width / 2,
					height / 2);
			setImageMatrix(mScaleMatrix);

			mOnce = true;
		}

	}

	/**
	 * 获取当前图片的缩放值
	 * 
	 * @return
	 */
	public float getScale() {
		float[] values = new float[9];
		mScaleMatrix.getValues(values);
		return values[Matrix.MSCALE_X];
	}

	// initScale maxScale
	@Override
	public boolean onScale(ScaleGestureDetector detector) {

		float scale = getScale();
		float scaleFactor = detector.getScaleFactor();

		if (getDrawable() == null) {
			return true;
		}

		// 缩放范围控制
		if ((scale < mMaxSacle && scaleFactor > 1.0f)
				|| (scale > mInitScale && scaleFactor < 1.0f)) {
			if (scale * scaleFactor < mInitScale) {
				scaleFactor = mInitScale / scale;
			}

			if (scale * scaleFactor > mMaxSacle) {
				scale = mMaxSacle / scale;
			}

			// 缩放
			mScaleMatrix.postScale(scaleFactor, scaleFactor,
					detector.getFocusX(), detector.getFocusY());
			checkBorderAndCenterWhenScale();
			setImageMatrix(mScaleMatrix);
		}

		return true;
	}

	/**
	 * 获得图片放大缩小以后的 w h l r t b
	 * 
	 * @return
	 */
	private RectF getMatrixRectF() {
		Matrix matrix = mScaleMatrix;
		RectF rectF = new RectF();
		Drawable d = getDrawable();
		if (d != null) {
			rectF.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			matrix.mapRect(rectF);
		}
		return rectF;

	}

	/**
	 * 在缩放是进行边界和位置的控制
	 */
	private void checkBorderAndCenterWhenScale() {

		RectF rect = getMatrixRectF();

		float deltaX = 0;
		float deltaY = 0;

		int width = getWidth();
		int height = getHeight();

		// 缩放时进行边界检测，防止出现白边
		if (rect.width() >= width) {

			if (rect.left > 0) {
				deltaX = -rect.left;
			}

			if (rect.right < width) {
				deltaX = width - rect.right;
			}
		}

		if (rect.height() >= height) {
			if (rect.top > 0) {
				deltaY = -rect.top;
			}

			if (rect.bottom < height) {
				deltaY = height - rect.bottom;
			}
		}

		// 如果宽度或者高度小于控件的宽高，让其居中
		if (rect.width() < width) {
			deltaX = width / 2f - rect.right + rect.width() / 2;
		}

		if (rect.height() < height) {
			deltaY = height / 2f - rect.bottom + rect.height() / 2;
		}

		mScaleMatrix.postTranslate(deltaX, deltaY);

	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (mGestureDetector.onTouchEvent(event)) {
			return true;
		}

		mScaleGestureDetector.onTouchEvent(event);

		float x = 0;
		float y = 0;
		// 拿到多点触控的数量
		int pointerCount = event.getPointerCount();
		Log.e("IMAGE-VIEW==", "pointerCount=="+pointerCount);
		for (int i = 0; i < pointerCount; i++) {
			x += event.getX(i);
			y += event.getY(i);
		}

		x /= pointerCount;
		y /= pointerCount;

		if (mLastPointerCount != pointerCount) {
			Log.e("IMAGE-VIEW==", "!=!=!="+mLastPointerCount);
			isCanDrag = false;
			mLastX = x;
			mLastY = y;
		}
		mLastPointerCount = pointerCount;
		RectF rectF = getMatrixRectF();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (rectF.width() > getWidth() + 0.01
					|| rectF.height() > getHeight() + 0.01) {
					getParent().requestDisallowInterceptTouchEvent(true);
			}

			break;
		case MotionEvent.ACTION_MOVE:
			Log.e("IMAGEVIEW--", "MOVE");

			if (rectF.width() > getWidth() + 0.01
					|| rectF.height() > getHeight() + 0.01) {
				getParent().requestDisallowInterceptTouchEvent(true);
			}

			float dx = x - mLastX;
			float dy = y - mLastY;

			if (!isCanDrag) {
				Log.e("MOVE==>","not can move");
				isCanDrag = isMoveAction(dx, dy);
				Log.e("MOVE==>","is can move"+isCanDrag);
			}

			if (isCanDrag) {
				// RectF rectF = getMatrixRectF();
				if (getDrawable() != null) {

					isCheckLeftAndRight = isTopAndBottom = true;
					// 如果宽度小于控件宽度，不允许横向移动
					if (rectF.width() < getWidth()) {
						isCheckLeftAndRight = false;
						dx = 0;
					}

					// 如果高度小于控件高度，不允许纵向移动
					if (rectF.height() < getHeight()) {
						isTopAndBottom = false;
						dy = 0;
					}

					mScaleMatrix.postTranslate(dx, dy);
					checkBorderWhenTranslate();

					setImageMatrix(mScaleMatrix);

				}
			}

			mLastX = x;
			mLastY = y;
			break;

		case MotionEvent.ACTION_UP:
			Log.e("IMAGEVIEW--", "UP");
		case MotionEvent.ACTION_CANCEL:
			Log.e("IMAGEVIEW--", "CANCLE");
			mLastPointerCount = 0;
			break;

		default:
			break;
		}

		return true;
	}

	/**
	 * 当移动时进行边界检查
	 */
	private void checkBorderWhenTranslate() {
		RectF rectF = getMatrixRectF();

		float deltaX = 0;
		float deltaY = 0;

		int width = getWidth();
		int height = getHeight();

		if (rectF.top > 0 && isTopAndBottom) {
			deltaY = -rectF.top;
		}

		if (rectF.bottom < height && isTopAndBottom) {
			deltaY = height - rectF.bottom;
		}

		if (rectF.left > 0 && isCheckLeftAndRight) {
			deltaX = -rectF.left;
		}

		if (rectF.right < width && isCheckLeftAndRight) {
			deltaX = width - rectF.right;
		}

		mScaleMatrix.postTranslate(deltaX, deltaY);
	}

	/**
	 * 判断是否是move
	 * 
	 * @param dx
	 * @param dy
	 * @return
	 */
	private boolean isMoveAction(float dx, float dy) {
		return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
	}

}
