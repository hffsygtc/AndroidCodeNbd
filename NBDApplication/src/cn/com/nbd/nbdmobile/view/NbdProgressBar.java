package cn.com.nbd.nbdmobile.view;

import cn.com.nbd.nbdmobile.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * 自定义的进度条 可自由切换左右进度显示的颜色
 * 
 * @author he
 * 
 */
public class NbdProgressBar extends ProgressBar {

	private static final int DEFAULT_TEXT_SIZE = 10;
	private static final int DEFAULT_TEXT_COLOR = 0XFFAABB;
	private static final int DEFAULT_TEXT_OFFSET = 10;
	private static final int DEFAULT_COLOR_UNREACH = 0XFFBBCC;
	private static final int DEFAULT_HEIGHT_UNREACH = 2;
	private static final int DEFAULT_COLOR_REACH = 0XFFCCDD;
	private static final int DEFAULT_HEIGHT_REACH = 2;

	private int mTextSize = sp2px(DEFAULT_TEXT_SIZE);
	private int mTextColor = DEFAULT_TEXT_COLOR;
	private int mUnReachColor = DEFAULT_COLOR_UNREACH;
	private int mUnReachHeight = dip2px(DEFAULT_HEIGHT_UNREACH);
	private int mReachColor = DEFAULT_COLOR_REACH;
	private int mReachHeight = dip2px(DEFAULT_HEIGHT_REACH);
	private int mTextOffset = dip2px(DEFAULT_TEXT_OFFSET);

	private Paint mPaint = new Paint();
	private int mRealWidth;

	public NbdProgressBar(Context context) {
		this(context, null);
	}

	public NbdProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public NbdProgressBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		obtainStyleAttrs(attrs);
	}

	/**
	 * 获取自定义的属性
	 * 
	 * @param attrs
	 */
	private void obtainStyleAttrs(AttributeSet attrs) {
		TypedArray ta = getContext().obtainStyledAttributes(attrs,
				R.styleable.SelfProgress);

		mTextSize = (int) ta.getDimension(
				R.styleable.SelfProgress_progress_text_size, mTextSize);

		mTextColor = ta.getColor(R.styleable.SelfProgress_progress_text_color,
				mTextColor);

		mTextOffset = (int) ta.getDimension(
				R.styleable.SelfProgress_progress_text_offset, mTextOffset);

		mUnReachColor = ta.getColor(
				R.styleable.SelfProgress_progress_unreach_color, mUnReachColor);

		mUnReachHeight = (int) ta.getDimension(
				R.styleable.SelfProgress_progress_unreach_height,
				mUnReachHeight);

		mReachColor = ta.getColor(
				R.styleable.SelfProgress_progress_reach_color, mReachColor);

		mReachHeight = (int) ta.getDimension(
				R.styleable.SelfProgress_progress_reach_height, mReachHeight);

		ta.recycle();

		mPaint.setTextSize(mTextSize);

	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec,
			int heightMeasureSpec) {
		// int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthVal = MeasureSpec.getSize(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);

		setMeasuredDimension(widthVal, height);

		mRealWidth = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

	}

	private int measureHeight(int heightMeasureSpec) {
		int result = 0;
		int mode = MeasureSpec.getMode(heightMeasureSpec);
		int size = MeasureSpec.getSize(heightMeasureSpec);

		if (mode == MeasureSpec.EXACTLY) {
			result = size;
		} else {
			int textHeight = (int) (mPaint.descent() - mPaint.ascent());
			result = getPaddingTop()
					+ getPaddingBottom()
					+ Math.max(Math.max(mReachHeight, mUnReachHeight),
							Math.abs(textHeight));

			if (mode == MeasureSpec.AT_MOST) {
				result = Math.min(result, size);
			}

		}

		return result;
	}

	@Override
	protected synchronized void onDraw(Canvas canvas) {

		canvas.save();
		canvas.translate(getPaddingLeft(), getHeight() / 2);

		boolean noNeedUnReach = false;

		String text = getProgress() + "%";
		int textWidth = (int) mPaint.measureText(text);
		float radio = getProgress() * 1.0f / getMax();

		float progressX = radio * mRealWidth;
		if (progressX + textWidth > mRealWidth) {
			progressX = mRealWidth - textWidth;
			noNeedUnReach = true;
		}

		float endX = radio * mRealWidth - mTextOffset / 2;
		if (endX > 0) {
			mPaint.setColor(mReachColor);
			mPaint.setStrokeWidth(mReachHeight);
			canvas.drawLine(0, 0, endX, 0, mPaint);
		}

		mPaint.setColor(mTextColor);
		int y = (int) ((mPaint.descent() + mPaint.ascent() / 2));
		canvas.drawText(text, progressX, y, mPaint);

		if (!noNeedUnReach) {
			float start = progressX + mTextOffset / 2 + textWidth;
			mPaint.setColor(mUnReachColor);
			mPaint.setStrokeWidth(mUnReachHeight);
			canvas.drawLine(start, 0, mRealWidth, 0, mPaint);
		}

		canvas.restore();

	}

	public int dip2px(int dpVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				dpVal, getResources().getDisplayMetrics());
	}

	public int sp2px(int spVal) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				spVal, getResources().getDisplayMetrics());
	}

}
