package cn.com.nbd.nbdmobile.view;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.SizeUtils;
import android.content.Context;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PulldownViewN extends BasePulldownView {

	private final static String TAG = "Pulldown_N";

	private LayoutInflater mInflater;
	private LinearLayout mHeadView; // 下拉刷新的布局
	private ImageView mHeadLogoN;
	private ImageView mHeadLogoPlus;
	private TextView mHeadTextView;

	private int mHeadHeight; // 下拉布局的一半高度
	private float pullNWidth; // N的宽度
	private int pullNHeight;// N的高度
	private float pullNInitScale; // 根据N的宽高和head的高度计算初始的缩放比例
	private float pullPlusWidth;
	private float pullPlusScale; // 加号的缩放比例
	private float disCount;
	private Animation sunAnimation;
	private Handler handler; // 处理滑动位置时的动画

	public PulldownViewN(Context context) {
		this(context, null);
	}

	public PulldownViewN(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PulldownViewN(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		
		mInflater = LayoutInflater.from(context);

		mHeadView = (LinearLayout) mInflater.inflate(R.layout.pulldown_head,
				this, true);
		mHeadLogoN = (ImageView) mHeadView.findViewById(R.id.head_logo);
		mHeadLogoPlus = (ImageView) mHeadView.findViewById(R.id.head_plus);
		mHeadTextView = (TextView) mHeadView.findViewById(R.id.head_text);
		// nTimeText = (TextView) mHeadView.findViewById(R.id.head_time_text);

		measureView(mHeadView);

		headHeight = mHeadView.getMeasuredHeight();
		
		int width = mHeadView.getMeasuredWidth();

		pullNHeight = mHeadLogoN.getMeasuredHeight();
		pullNWidth = mHeadLogoN.getMeasuredWidth();

		pullPlusWidth = mHeadLogoPlus.getMeasuredWidth();

		float initNWidth = SizeUtils.dp2px(getContext(), 30);
		pullNInitScale = initNWidth / pullNWidth;

		float initPlusWidth = SizeUtils.dp2px(getContext(), 8);
		pullPlusScale = initPlusWidth / pullPlusWidth;

		Matrix initMatrix = new Matrix();
		initMatrix.postScale(0, 0);

//		mHeadLogoN.setImageMatrix(initMatrix);
		mHeadLogoPlus.setImageMatrix(initMatrix);

		headState = BasePulldownView.DONE;
		
		//  初始化旋转动画
		Interpolator _Interpolator = new LinearInterpolator();
		sunAnimation = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime,
					Transformation t) {

				showPlusRotate(interpolatedTime);

			}
		};
		sunAnimation.setRepeatCount(Animation.INFINITE);
		sunAnimation.setRepeatMode(Animation.RESTART);
		sunAnimation.setInterpolator(_Interpolator);
		sunAnimation.setDuration(1000);
	}

	/**
	 * 加载时，加号选择的动画
	 * @param interpolatedTime
	 */
	private void showPlusRotate(float interpolatedTime) {
		Matrix sunMatrix = new Matrix();

		float sunX = 0;
		float sunDeltY = headHeight - disCount
				+ SizeUtils.dp2px(getContext(), 15);
		float sunCenterX = sunX + pullPlusWidth * pullPlusScale / 2;
		float sunCenterY = sunDeltY + pullPlusWidth * pullPlusScale / 2;

		float animScale = (Float) interpolatedTime;

		sunMatrix.postScale(pullPlusScale, pullPlusScale);
		sunMatrix.postTranslate(sunX, sunDeltY);
		sunMatrix.postRotate(360 * animScale * 2, sunCenterX, sunCenterY);

		mHeadLogoPlus.setImageMatrix(sunMatrix);

	}

	@Override
	public void setState(int state) {
		
		headState = state;
		changeHeadViewByState();

	}

	@Override
	public void setDistance(int distance) {

		disCount = distance / 3; // 向下拉动的距离

		if (disCount > headHeight) {
			disCount = headHeight;
		}

		float pullPercent = disCount / headHeight;
		float scale = (float) (pullPercent * pullNInitScale);

		if (scale > pullNInitScale) {
			scale = (float) pullNInitScale;
		}

		Matrix m = new Matrix();
		m.postScale(scale, scale);
		float deltY = headHeight / 2 - pullNHeight * scale;
		float deltX = SizeUtils.dp2px(getContext(), 40) - pullNWidth * scale
				/ 2;
		m.postTranslate(deltX, deltY);
//		Log.e(TAG, "N-Matrix--scale:"+scale+"deltx-"+deltX+"deltY-"+deltY);
		mHeadLogoN.setImageMatrix(m);

		// 处理小太阳
		Matrix sunMatrix = new Matrix();
		// float sunX = dip2pix(getContext(),
		// 35)+pullNWidth*pullNInitScale;
		float sunX = 0;
		float sunDeltY = headHeight - disCount
				+ SizeUtils.dp2px(getContext(), 15);
		float sunCenterX = sunX + pullPlusWidth * pullPlusScale / 2;
		float sunCenterY = sunDeltY + pullPlusWidth * pullPlusScale / 2;

		sunMatrix.postScale(pullPlusScale, pullPlusScale);
		sunMatrix.postTranslate(sunX, sunDeltY);
		sunMatrix.postRotate(360 * pullPercent, sunCenterX, sunCenterY);

		mHeadLogoPlus.setImageMatrix(sunMatrix);

	}

//	@Override
//	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		 super.onLayout(changed, l, t, r, b);
//
//	}

	private void measureView(View pChild) {
		ViewGroup.LayoutParams p = pChild.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;

		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		pChild.measure(childWidthSpec, childHeightSpec);
	}

	/**
	 * 当HeadView状态改变时候，调用该方法，以更新界面
	 * 
	 * @param state
	 * 
	 */
	private void changeHeadViewByState() {
		switch (headState) {
		case RELEASE_TO_REFRESH:

			mHeadLogoPlus.clearAnimation();
			mHeadTextView.setText(R.string.p2refresh_release_refresh);

			break;
		case PULL_TO_REFRESH:

			mHeadLogoPlus.clearAnimation();
			mHeadTextView.setText(R.string.p2refresh_pull_to_refresh);
			
			break;
		case REFRESHING:

			mHeadView.setPadding(0, 0, 0, 0);
			mHeadLogoPlus.startAnimation(sunAnimation);
			
			// 实际上这个的setPadding可以用动画来代替。也有用Scroller可以实现这个效果，

			// 正在刷新...
			mHeadTextView.setText(R.string.p2refresh_doing_head_refresh);
			
			break;
		case DONE:
			// 当回到原始状态或者刷新完成后，得把状态传出去，是上滑时显示停靠栏
			// dealPinnedVisible(0);

			mHeadView.setPadding(0, -1 * headHeight, 0, 0);
			mHeadLogoPlus.clearAnimation();
			// 此处可以改进，同上所述。
			// // 下拉刷新

			break;
		}
	}

}
