package cn.com.nbd.nbdmobile.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.adapter.SignCenterAdapter;
import cn.com.nbd.nbdmobile.adapter.SignCenterAdapter.OnNewsClickListener;
import cn.com.nbd.nbdmobile.view.NbdProgressBar;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;

import com.nbd.article.bean.ActivityListBean;
import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.ActivityCallback;
import com.nbd.article.managercallback.StringInfoCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

/**
 * 报名中心页面
 * 
 * @author riche
 * 
 */
public class SignCenterActivity extends Activity {

	private SharedPreferences mNativeShare;
	private SharedPreferences configShare;
	
	private boolean isDayTheme;
	private boolean isTextMode;
	private String mAccessToken;
	
	private ImageView mBackBtn;
	private ListView mListView;
	
	private List<ActivityListBean> mActivites = new ArrayList<ActivityListBean>();
	private SignCenterAdapter mAdapter;
	
	private LoadingDialog mLoadingDialog;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mNativeShare = this.getSharedPreferences("NativeSetting",
				this.MODE_PRIVATE);

		isDayTheme = mNativeShare.getBoolean("theme", true);
		isTextMode = mNativeShare.getBoolean("textMode", false);
		
		if (isDayTheme) {
			setTheme(R.style.DayTheme);
		}else {
			setTheme(R.style.NightTheme);
		}
		
		setContentView(R.layout.activity_detail_sign_center);

		configShare = this.getSharedPreferences("AppConfig", Context.MODE_PRIVATE);

		mAccessToken = configShare.getString("accessToken", null);
		
		mLoadingDialog = new LoadingDialog(SignCenterActivity.this, R.style.loading_dialog, "加载中...");
		
		initUi();
		
		setListener();

		initData();
	}

	private void initData() {
		
		ArticleConfig config = new ArticleConfig();
		config.setType(RequestType.SIGN_CENTER);
		config.setAccessToken(mAccessToken);
		if (mLoadingDialog != null) {
			mLoadingDialog.showFullDialog();
		}
		ArticleManager.getInstence().getSignCenterData(config,new ActivityCallback() {
			
			@Override
			public void onActivityCallback(List<ActivityListBean> activities, String s) {
				if (mLoadingDialog != null) {
					mLoadingDialog.dismiss();
				}
				
				if (activities != null) {
					mActivites = activities;
					initAdapter();
				}else if (s != null) {
					Toast.makeText(SignCenterActivity.this, s, Toast.LENGTH_SHORT).show();
				}else {
					Toast.makeText(SignCenterActivity.this, "当前网络状况较差，请检查后重试", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	private void initAdapter() {
		
		if (mAdapter == null) {
			mAdapter = new SignCenterAdapter(SignCenterActivity.this, mActivites, isDayTheme, isTextMode);
		}
		
		mAdapter.setNewsClickListener(new OnNewsClickListener() {
			

			@Override
			public void onNewsClick(int position, String url, String title) {
				if (url != null) {
					Intent i = new Intent(SignCenterActivity.this, AdvertiseWebviewActivity.class);
					Bundle b = new Bundle();
					b.putString("link",url);
					b.putString("title", title);
					i.putExtra("urlbundle", b);
					startActivity(i);
				}
			}
		});
		
		mListView.setAdapter(mAdapter);
		
	}

	private void initUi() {
		
		mBackBtn = (ImageView) findViewById(R.id.sign_center_back_btn);
		mListView = (ListView) findViewById(R.id.sign_center_mListView);

	}

	private void setListener() {
		
		mBackBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SignCenterActivity.this.finish();
			}
		});
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
