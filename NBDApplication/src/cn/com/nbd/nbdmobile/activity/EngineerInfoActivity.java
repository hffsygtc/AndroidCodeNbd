package cn.com.nbd.nbdmobile.activity;


import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.animation.Animator.AnimatorListener;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.JellyInterpolator;

/**
 * 搜索的页面，内含热门的搜索词
 * 
 * @author riche
 * 
 */
public class EngineerInfoActivity extends Activity {

	private ImageView mBackBtn; // 返回按钮
	
	private LinearLayout mHeadClickLayout; //点击显示动画的并且消失的Layout
	
	private RelativeLayout mHeadShowLayout; // 消失后对应位置显示的Layout
	
	private TextView mHeadClickText;
	
	
	private float showHeight;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_engineer_info_layout);

		initUi();
		setListener();
	}
	/**
	 * 初始化界面控件
	 */
	private void initUi() {
		mBackBtn = (ImageView) findViewById(R.id.engineer_back_btn);
		
		mHeadClickLayout = (LinearLayout) findViewById(R.id.engineer_name_layout);
		mHeadShowLayout = (RelativeLayout) findViewById(R.id.engineer_head_img);
		
		mHeadClickText = (TextView) findViewById(R.id.engineer_name_text);
		
	}

	/**
	 * 初始化适配器 界面数据显示
	 */
	private void setListener() {
		
		mHeadClickLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				showHeight = dip2px(EngineerInfoActivity.this, 80);
				mHeadClickText.setVisibility(View.INVISIBLE);
				
				inputAnimator(mHeadClickLayout, showHeight, showHeight);
			}
		});
		
		
		mBackBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EngineerInfoActivity.this.finish();
			}
		});

	}

	private void inputAnimator(final View view, float w, float h) {

		
		AnimatorSet set = new AnimatorSet();

		ValueAnimator animator = ValueAnimator.ofFloat(0, w);
		animator.addUpdateListener(new AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float value = (Float) animation.getAnimatedValue();
				ViewGroup.MarginLayoutParams params = (MarginLayoutParams) view
						.getLayoutParams();
				params.leftMargin = (int) value;
				params.rightMargin = (int) value;
				view.setLayoutParams(params);
			}
		});

		ObjectAnimator animator2 = ObjectAnimator.ofFloat(mHeadClickLayout,
				"scaleX", 1f, 0.5f);
		set.setDuration(1000);
		set.setInterpolator(new AccelerateDecelerateInterpolator());
		
		set.playTogether(animator, animator2);
		
		set.addListener(new AnimatorListener() {

			@Override
			public void onAnimationStart(Animator animation) {

			}

			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animator animation) {

				mHeadShowLayout.setVisibility(View.VISIBLE);
				progressAnimator(mHeadShowLayout);
				mHeadClickLayout.setVisibility(View.INVISIBLE);

			}

			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub

			}
		});

		set.start();
	}

	
	private void progressAnimator(final View view) {
		PropertyValuesHolder animator = PropertyValuesHolder.ofFloat("scaleX",
				0.5f, 1f);
		PropertyValuesHolder animator2 = PropertyValuesHolder.ofFloat("scaleY",
				0.5f, 1f);
		ObjectAnimator animator3 = ObjectAnimator.ofPropertyValuesHolder(view,
				animator, animator2);
		animator3.setDuration(1000);
		animator3.setInterpolator(new JellyInterpolator());
		animator3.start();

	}
	
	private int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}


}
