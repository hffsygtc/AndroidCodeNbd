package cn.com.nbd.nbdmobile.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;

/**
 * 夜间模式的切换功能写在BASE里面
 * 
 * @author riche
 *
 */
public class BaseActivity extends FragmentActivity {
	
	public SharedPreferences configShare;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		configShare = this.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(ev);
	}
	
	/*
	 * 返回
	 */
	public void doBack(View view) {
		onBackPressed();
	}


}
