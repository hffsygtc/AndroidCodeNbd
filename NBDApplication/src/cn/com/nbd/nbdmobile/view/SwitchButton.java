package cn.com.nbd.nbdmobile.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.accessibility.AccessibilityEvent;
import android.widget.CompoundButton;

import cn.com.nbd.nbdmobile.R;

public class SwitchButton extends CompoundButton {
	private static final int TOUCH_MODE_IDLE = 0;
	private static final int TOUCH_MODE_DOWN = 1;
	private static final int TOUCH_MODE_DRAGGING = 2;

	private static final int SANS = 1;
	private static final int SERIF = 2;
	private static final int MONOSPACE = 3;
	/** Switch的运动轨迹 ，既背景 */
	private Drawable mThumbDrawable;
	/** Switch的=操作按钮，即开关按钮 */
	private Drawable mTrackDrawable;
	/** Switch的中现实的 on和off情况下字体大小 */
	// 讲道理是开关的宽度
	private int mThumbTextPadding;
	/** Switch控件的最小宽度 */
	private int mSwitchMinWidth;
	/** Switch控件的padding属性值 */
	// 这个属性什么意思
//	private int mSwitchPadding;

	/** Switch控件的触摸时候的模式 */
	private int mTouchMode;
	private int mTouchSlop;
	private float mTouchX;
	private float mTouchY;
	private VelocityTracker mVelocityTracker = VelocityTracker.obtain();
	private int mMinFlingVelocity;

	private float mThumbPosition;
	private int mSwitchWidth;
//	private int mSwitchHeight;
	private int mThumbWidth;

	private int mSwitchLeft;
	private int mSwitchTop;
	private int mSwitchRight;
	private int mSwitchBottom;

	private Context mContext;

	private int mSwitchHeightUsed;
	//滑动的小圆的宽度 
	private int mCircleWidth;

	@SuppressWarnings("hiding")
	private final Rect mTempRect = new Rect();

	private static final int[] CHECKED_STATE_SET = { android.R.attr.state_checked };

	public SwitchButton(Context context) {
		this(context, null);
		mContext = context;
	}

	public SwitchButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		mContext = context;
	}

	public SwitchButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SwitchButton, defStyle, 0);// 从配置文件中获取相关配置

		// drawable
		mThumbDrawable = a.getDrawable(R.styleable.SwitchButton_thumb);// 获取配置的轨迹资源
		// drawable
		mTrackDrawable = a.getDrawable(R.styleable.SwitchButton_track);// 获取配置的开关资源

		mSwitchHeightUsed = a.getDimensionPixelSize(
				R.styleable.SwitchButton_switchHeight, 0);

		mSwitchMinWidth = a.getDimensionPixelSize(
				R.styleable.SwitchButton_switchMinWidth, 0);// 获取配置的最小宽度

		
		//获取滑动圆的宽度
		mCircleWidth = a.getDimensionPixelSize(R.styleable.SwitchButton_circleWidth,0);
		
		
		a.recycle();// 回收配置文件资源
		ViewConfiguration config = ViewConfiguration.get(context);
		mTouchSlop = config.getScaledTouchSlop();
		mMinFlingVelocity = config.getScaledMinimumFlingVelocity();
		refreshDrawableState();
		setChecked(isChecked());
	}

	/** 测量控件宽高，供绘图时使用。 */
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


		// 测量的方式，和大小
		final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		mTrackDrawable.getPadding(mTempRect);
		// switch的宽度是属性中最小的宽度与
		// 文字宽度的2倍，加上文字间距的4倍，两个左右，最后那个方块是个什么鬼的和，暂时是有minWidth决定，因为没文字
		final int switchWidth = mSwitchMinWidth;

		// 1:3 的DP关系

		// final int switchHeight = mTrackDrawable.getIntrinsicHeight();
		final int switchHeight = mSwitchHeightUsed;

		
		//滑动图标的宽度
		mThumbWidth = mCircleWidth;

		switch (widthMode) {
		case MeasureSpec.AT_MOST:
			widthSize = Math.min(widthSize, switchWidth);
			break;

		case MeasureSpec.UNSPECIFIED:
			widthSize = switchWidth;
			break;

		case MeasureSpec.EXACTLY:
			break;
		}

		switch (heightMode) {
		case MeasureSpec.AT_MOST:
			heightSize = Math.min(heightSize, switchHeight);
			break;
		case MeasureSpec.UNSPECIFIED:
			heightSize = switchHeight;
			break;

		case MeasureSpec.EXACTLY:
			break;
		}

		mSwitchWidth = switchWidth;
		mSwitchHeightUsed = switchHeight;

		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int measuredHeight = getMeasuredHeight();
		if (measuredHeight < switchHeight) {
			setMeasuredDimension(getMeasuredWidth(), switchHeight);
		}
	}

	@Override
	public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
		populateAccessibilityEvent(event);
		return false;
	}

	public void populateAccessibilityEvent(AccessibilityEvent event) {
	}

	/**
	 * @描述：return true 说明开关在(x,y)目标区域内
	 */
	private boolean hitThumb(float x, float y) {
		mThumbDrawable.getPadding(mTempRect);
		final int thumbTop = mSwitchTop - mTouchSlop;
		final int thumbLeft = mSwitchLeft + (int) (mThumbPosition + 0.5f)
				- mTouchSlop;
		final int thumbRight = thumbLeft + mThumbWidth + mTempRect.left
				+ mTempRect.right + mTouchSlop;
		final int thumbBottom = mSwitchBottom + mTouchSlop;
		return x > thumbLeft && x < thumbRight && y > thumbTop
				&& y < thumbBottom;
	}

	/** 传递触摸屏触摸事件 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mVelocityTracker.addMovement(ev);
		final int action = ev.getActionMasked();

		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			final float x = ev.getX();
			final float y = ev.getY();
			if (isEnabled() && hitThumb(x, y)) {
				mTouchMode = TOUCH_MODE_DOWN;
				mTouchX = x;
				mTouchY = y;
			}
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			switch (mTouchMode) {
			case TOUCH_MODE_IDLE:
				return true;
			case TOUCH_MODE_DOWN: {
				final float x = ev.getX();
				final float y = ev.getY();
				if (Math.abs(x - mTouchX) > mTouchSlop
						|| Math.abs(y - mTouchY) > mTouchSlop) {
					mTouchMode = TOUCH_MODE_DRAGGING;
					getParent().requestDisallowInterceptTouchEvent(true);
					mTouchX = x;
					mTouchY = y;
					return true;
				}
				break;
			}
			case TOUCH_MODE_DRAGGING: {
				final float x = ev.getX();
				final float dx = x - mTouchX;
				float newPos = Math.max(0,
						Math.min(mThumbPosition + dx, getThumbScrollRange()));
				if (newPos != mThumbPosition) {
					mThumbPosition = newPos;
					mTouchX = x;
					invalidate();
				}
				return true;
			}
			}
			break;
		}
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL: {
			if (mTouchMode == TOUCH_MODE_DRAGGING) {
				stopDrag(ev);
				return true;
			}
			mTouchMode = TOUCH_MODE_IDLE;
			mVelocityTracker.clear();
			break;
		}
		}
		return super.onTouchEvent(ev);
	}

	private void cancelSuperTouch(MotionEvent ev) {
		MotionEvent cancel = MotionEvent.obtain(ev);
		cancel.setAction(MotionEvent.ACTION_CANCEL);
		super.onTouchEvent(cancel);
		cancel.recycle();
	}

	private void stopDrag(MotionEvent ev) {
		mTouchMode = TOUCH_MODE_IDLE;

		boolean commitChange = ev.getAction() == MotionEvent.ACTION_UP
				&& isEnabled();

		cancelSuperTouch(ev);

		if (commitChange) {
			boolean newState;
			mVelocityTracker.computeCurrentVelocity(1000);
			float xvel = mVelocityTracker.getXVelocity();
			if (Math.abs(xvel) > mMinFlingVelocity) {
				newState = xvel > 0;
			} else {
				newState = getTargetCheckedState();
			}
			animateThumbToCheckedState(newState);
		} else {
			animateThumbToCheckedState(isChecked());
		}
	}

	private void animateThumbToCheckedState(boolean newCheckedState) {

		setChecked(newCheckedState);
	}

	private boolean getTargetCheckedState() {
		return mThumbPosition >= getThumbScrollRange() / 2;
	}

	// 设置选中的状态（选中:true 非选中: false）
	@Override
	public void setChecked(boolean checked) {
		super.setChecked(checked);
		mThumbPosition = checked ? getThumbScrollRange() : 0;
		invalidate();
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		
	
		//圆形图标的位置
		mThumbPosition = isChecked() ? getThumbScrollRange() : 0;

		
		int switchRight = getWidth() - getPaddingRight();
		int switchLeft = switchRight - mSwitchWidth;
		int switchTop = 0;
		int switchBottom = 0;
		switch (getGravity() & Gravity.VERTICAL_GRAVITY_MASK) {
		default:
		case Gravity.TOP:
			switchTop = getPaddingTop();
			switchBottom = switchTop + mSwitchHeightUsed;
			break;

		case Gravity.CENTER_VERTICAL:
			switchTop = (getPaddingTop() + getHeight() - getPaddingBottom())
					/ 2 - mSwitchHeightUsed / 2;
			switchBottom = switchTop + mSwitchHeightUsed;
			break;

		case Gravity.BOTTOM:
			switchBottom = getHeight() - getPaddingBottom();
			switchTop = switchBottom - mSwitchHeightUsed;
			break;
		}

		mSwitchLeft = switchLeft;
		mSwitchTop = switchTop;
		mSwitchBottom = switchBottom;
		mSwitchRight = switchRight;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int switchLeft = mSwitchLeft-mThumbWidth/2;
		int switchTop = (mThumbWidth-mSwitchHeightUsed)/2;
		int switchRight = mSwitchRight-mThumbWidth/2;
		int switchBottom = (mThumbWidth-mSwitchHeightUsed)/2+mSwitchHeightUsed;
		
		//背景
		mTrackDrawable.setBounds(switchLeft, switchTop, switchRight,
				switchBottom);
		mTrackDrawable.draw(canvas);

		canvas.save();

		mTrackDrawable.getPadding(mTempRect);
		int switchInnerLeft = switchLeft + mTempRect.left-mThumbWidth/2;
		int switchInnerTop = 0;
		int switchInnerRight = switchRight - mTempRect.right+mThumbWidth/2;
		int switchInnerBottom = mThumbWidth;
		
		//只显示区域内的内容
		canvas.clipRect(switchInnerLeft, switchInnerTop, switchInnerRight,
				switchInnerBottom);

		//小圆点
		mThumbDrawable.getPadding(mTempRect);
		
		final int thumbPos = (int) (mThumbPosition + 0.5f);
		int thumbLeft = switchInnerLeft + thumbPos;
		
		int thumbRight = switchInnerLeft + thumbPos + mThumbWidth;

		mThumbDrawable
				.setBounds(thumbLeft, 0, thumbRight, mThumbWidth);
		mThumbDrawable.draw(canvas);

		canvas.restore();
	}

	@Override
	public int getCompoundPaddingRight() {
		int padding = super.getCompoundPaddingRight() + mSwitchWidth;
		return padding;
	}

	private int getThumbScrollRange() {
		if (mTrackDrawable == null) {
			return 0;
		}
		mTrackDrawable.getPadding(mTempRect);
		return mSwitchWidth;
	}

	@Override
	protected int[] onCreateDrawableState(int extraSpace) {
		final int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
		if (isChecked()) {
			mergeDrawableStates(drawableState, CHECKED_STATE_SET);
		}
		return drawableState;
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		int[] myDrawableState = getDrawableState();
		if (mThumbDrawable != null)
			mThumbDrawable.setState(myDrawableState);
		if (mTrackDrawable != null)
			mTrackDrawable.setState(myDrawableState);
		invalidate();
	}

	@Override
	protected boolean verifyDrawable(Drawable who) {
		return super.verifyDrawable(who) || who == mThumbDrawable
				|| who == mTrackDrawable;
	}

}
