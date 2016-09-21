package cn.com.nbd.nbdmobile.view;

import android.content.Context;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.SizeUtils;

public class PullDownViewNLogo extends LinearLayout implements IPulldownView {
	
	private LayoutInflater mInflater;
	private LinearLayout mHeadView; // 下拉刷新的布局
	private ImageView mHeadLogoN;
	private ImageView mHeadLogoPlus;
	private TextView mHeadTextView;
	
	private int mHeadViewWidth; // 下拉布局的宽
	private int mHeadViewHeight; // 下拉布局的高
	
	private int mHeadHeight; // 下拉布局的一半高度
	private float pullNWidth; // N的宽度
	private int pullNHeight;// N的高度
	private float pullNInitScale; // 根据N的宽高和head的高度计算初始的缩放比例
	private float pullPlusWidth;
	private float pullPlusScale; // 加号的缩放比例
	private float disCount;
	private Animation sunAnimation;
	private Handler handler; // 处理滑动位置时的动画
	
	private int mHeadState;

	public PullDownViewNLogo(Context context) {
		this(context,null);
	}

	public PullDownViewNLogo(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public PullDownViewNLogo(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
//		mInflater = LayoutInflater.from(context);
		mInflater = (LayoutInflater) context
		.getSystemService(Context.LAYOUT_INFLATER_SERVICE);;

		mHeadView = (LinearLayout) mInflater.inflate(R.layout.pulldown_head,
				this,true);
		mHeadLogoN = (ImageView) mHeadView.findViewById(R.id.head_logo);
		mHeadLogoPlus = (ImageView) mHeadView.findViewById(R.id.head_plus);
		mHeadTextView = (TextView) mHeadView.findViewById(R.id.head_text);
		// nTimeText = (TextView) mHeadView.findViewById(R.id.head_time_text);


		measureView(mHeadView);

		mHeadViewHeight = mHeadView.getMeasuredHeight();
		// mHeadViewHeight = dip2pix(getContext(), 60);
		 Log.e("TEST HEAD!@!@!@!", "headViewHeight-->"+mHeadViewHeight);
		mHeadViewWidth = mHeadView.getMeasuredWidth();

		pullNHeight = mHeadLogoN.getMeasuredHeight();
		pullNWidth = mHeadLogoN.getMeasuredWidth();

		pullPlusWidth = mHeadLogoPlus.getMeasuredWidth();

//		mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
//		mHeadView.invalidate();

//		addHeaderView(mHeadView, null, false);

		float initNWidth = SizeUtils.dp2px(getContext(), 30);
		pullNInitScale = initNWidth / pullNWidth;

		float initPlusWidth = SizeUtils.dp2px(getContext(), 8);
		pullPlusScale = initPlusWidth / pullPlusWidth;

		Matrix initMatrix = new Matrix();
		initMatrix.postScale(0.5f, 0.5f);

		mHeadLogoN.setImageMatrix(initMatrix);
		mHeadLogoPlus.setImageMatrix(initMatrix);
		
//		initPullImageAnimation();

//		mHeadState = DONE;
		
		
	}
	
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
	
	public void setTheme(boolean isDay){
		
	}

	@Override
	public void onPulldownDistance(int distance) {
		// TODO Auto-generated method stub
		
		Log.e("PULL N TEST-->", "dis--"+distance);
		


		if (distance== 2000) {
			Matrix sunMatrix = new Matrix();

			float sunX = 0;
			float sunDeltY = mHeadViewHeight - disCount
					+ SizeUtils.dp2px(getContext(), 15);
			float sunCenterX = sunX + pullPlusWidth * pullPlusScale / 2;
			float sunCenterY = sunDeltY + pullPlusWidth * pullPlusScale
					/ 2;

//			float animScale = (Float) msg.obj;
			float animScale =  0.1f;

			sunMatrix.postScale(pullPlusScale, pullPlusScale);
			sunMatrix.postTranslate(sunX, sunDeltY);
			sunMatrix.postRotate(360 * animScale * 2, sunCenterX,
					sunCenterY);

			mHeadLogoPlus.setImageMatrix(sunMatrix);
		} else {

			disCount = distance / 3; // 向下拉动的距离

			if (disCount > mHeadViewHeight) {
				disCount = mHeadViewHeight;
			}

			float pullPercent = disCount / mHeadViewHeight;
			float scale = (float) (pullPercent * pullNInitScale);

			if (scale > pullNInitScale) {
				scale = (float) pullNInitScale;
			}

			Matrix m = new Matrix();
			m.postScale(scale, scale);
			float deltY = mHeadViewHeight / 2 - pullNHeight * scale;
			float deltX = SizeUtils.dp2px(getContext(), 40) - pullNWidth
					* scale / 2;
			m.postTranslate(deltX, deltY);
			mHeadLogoN.setImageMatrix(m);

			// 处理小太阳
			Matrix sunMatrix = new Matrix();
			// float sunX = dip2pix(getContext(),
			// 35)+pullNWidth*pullNInitScale;
			float sunX = 0;
			float sunDeltY = mHeadViewHeight - disCount
					+ SizeUtils.dp2px(getContext(), 15);
			float sunCenterX = sunX + pullPlusWidth * pullPlusScale / 2;
			float sunCenterY = sunDeltY + pullPlusWidth * pullPlusScale
					/ 2;

			sunMatrix.postScale(pullPlusScale, pullPlusScale);
			sunMatrix.postTranslate(sunX, sunDeltY);
			sunMatrix.postRotate(360 * pullPercent, sunCenterX,
					sunCenterY);

			mHeadLogoPlus.setImageMatrix(sunMatrix);

		}
	}

	@Override
	public void onHeadStateChange(int state) {
		mHeadState = state;
		changeHeadViewByState();
	}

	@Override
	public int getPullState() {
		return mHeadState;
	}
	/**
	 * 实例化下拉刷新的箭头的动画效果
	 * 
	 * @param pAnimDuration
	 *            动画运行时长
	 */
	private void initPullImageAnimation() {

		Interpolator _Interpolator = new LinearInterpolator();

		sunAnimation = new Animation() {
			@Override
			public void applyTransformation(float interpolatedTime,
					Transformation t) {
				Message msg = new Message();
				msg.obj = interpolatedTime;
				msg.what = 2000;
				handler.sendMessage(msg);
				// setRotate(interpolatedTime);
			}
		};
		sunAnimation.setRepeatCount(Animation.INFINITE);
		sunAnimation.setRepeatMode(Animation.RESTART);
		sunAnimation.setInterpolator(_Interpolator);
		sunAnimation.setDuration(1000);

	}
	

	/**
	 * 当HeadView状态改变时候，调用该方法，以更新界面
	 * @param state 
	 * 
	 */
	private void changeHeadViewByState() {
		switch (mHeadState) {
		case RELEASE_TO_REFRESH:

			mHeadLogoPlus.clearAnimation();
			// // 松开刷新
			mHeadTextView.setText(R.string.p2refresh_release_refresh);

			break;
		case PULL_TO_REFRESH:

//			dealPinnedVisible(1);
			mHeadLogoPlus.clearAnimation();
			// 是由RELEASE_To_REFRESH状态转变来的
//			if (mIsBack) {
//				mIsBack = false;
//				// // 下拉刷新
//			} else {
//				// 下拉刷新
//			}

			mHeadTextView.setText(R.string.p2refresh_pull_to_refresh);
			break;

		case REFRESHING:
//			dealPinnedVisible(1);

			mHeadView.setPadding(0, 0, 0, 0);

//			mHeadLogoPlus.startAnimation(sunAnimation);
			// 实际上这个的setPadding可以用动画来代替。也有用Scroller可以实现这个效果，

			// 正在刷新...
			mHeadTextView.setText(R.string.p2refresh_doing_head_refresh);
			break;
		case DONE:
			// 当回到原始状态或者刷新完成后，得把状态传出去，是上滑时显示停靠栏
//			dealPinnedVisible(0);

			mHeadView.setPadding(0, -1 * mHeadViewHeight, 0, 0);
			mHeadLogoPlus.clearAnimation();
			// 此处可以改进，同上所述。
			// // 下拉刷新

			break;
		}
	}
}
