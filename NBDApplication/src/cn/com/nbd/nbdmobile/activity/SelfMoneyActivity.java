package cn.com.nbd.nbdmobile.activity;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.com.nbd.nbdmobile.R;

/**
 * 金币界面
 * 目前未开发，就一个图片和主题区分
 * 
 * @author riche
 * 
 */

public class SelfMoneyActivity extends Activity implements OnClickListener {

	private ImageView mBackBtn; // 返回按钮

	private TextView mTittle;
	private ImageView mSimpleImage;
	private TextView mSimpleText;
	
	private RelativeLayout mTopLayout;
	private RelativeLayout mMidLayout;

	private int ActivityType;
	private SharedPreferences mNativeShare;
	private boolean isDayTheme;
	
	private static int JB_ACTIVITY = 0; 
	private static int ABOUT_ACTIVITY = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mNativeShare = this.getSharedPreferences("NativeSetting",
				this.MODE_PRIVATE);
		isDayTheme = mNativeShare.getBoolean("theme", true);

		if (isDayTheme) {
			setTheme(R.style.DayTheme);
		} else {
			setTheme(R.style.NightTheme);
		}
		setContentView(R.layout.activity_simple_image_page);
		
		Intent i = getIntent();
		
		ActivityType = i.getIntExtra("type",0);

		initUi();
		setListener();

	}

	private void initUi() {
		
		mBackBtn = (ImageView) findViewById(R.id.simple_page_back);
		mTittle = (TextView) findViewById(R.id.simple_page_title_text);
		mSimpleImage =  (ImageView) findViewById(R.id.simple_page_img);
		mSimpleText = (TextView) findViewById(R.id.simple_page_content);
		
		mTopLayout = (RelativeLayout) findViewById(R.id.simple_top_layout);
		mMidLayout = (RelativeLayout) findViewById(R.id.simple_mid_layout);
		
		if (ActivityType == JB_ACTIVITY) {
			mTittle.setText("金币");
			mMidLayout.setVisibility(View.VISIBLE);
			mTopLayout.setVisibility(View.GONE);
			
		}else if (ActivityType == ABOUT_ACTIVITY) {
			mTittle.setText("关于");
			mMidLayout.setVisibility(View.GONE);
			mTopLayout.setVisibility(View.VISIBLE);
			
		}
		
	}

	private void setListener() {

		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SelfMoneyActivity.this.finish();
			}
		});

	}


	@Override
	public void onClick(View v) {

	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			SelfMoneyActivity.this.finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

}
