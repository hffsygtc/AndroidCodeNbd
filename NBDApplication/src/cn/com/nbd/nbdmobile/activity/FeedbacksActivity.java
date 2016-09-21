package cn.com.nbd.nbdmobile.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.StringInfoCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * 意见反馈界面
 * 
 * @author riche
 * 
 */
public class FeedbacksActivity extends Activity implements OnClickListener {

	private ImageView mBackBtn; // 返回按钮
	private EditText mContentEdit; // 手机号码输入框
	private Button mNextButton; // 下一步按钮

	private String mContentStr;
	private String mAccessToken;

	private SharedPreferences mNativeShare;
	private SharedPreferences mConfigShare;
	private boolean isDayTheme;
	
	private LoadingDialog mLoadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		mNativeShare = this.getSharedPreferences("NativeSetting",
				this.MODE_PRIVATE);
		mConfigShare = this
				.getSharedPreferences("AppConfig", this.MODE_PRIVATE);

		isDayTheme = mNativeShare.getBoolean("theme", true);

		if (isDayTheme) {
			setTheme(R.style.DayTheme);
		} else {
			setTheme(R.style.NightTheme);
		}

		setContentView(R.layout.activity_feedbacks);

		mAccessToken = mConfigShare.getString("accessToken", "");
		mLoadingDialog = new LoadingDialog(FeedbacksActivity.this, R.style.loading_dialog, "上传中...");

		initUi();

		setListener();

	}

	private void initUi() {

		mBackBtn = (ImageView) findViewById(R.id.feedbacks_back_btn);
		mContentEdit = (EditText) findViewById(R.id.feedbacks_content_edittext);
		mNextButton = (Button) findViewById(R.id.feedbacks_button);

		mNextButton.setEnabled(false);

	}

	private void setListener() {

		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				FeedbacksActivity.this.finish();
			}
		});

		mContentEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				Log.e("EDIT_TEXT_bottom", s + "==" + count);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				mContentStr = s.toString();
				changeNextBtnState();

			}
		});

		mNextButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (mContentStr == null || mContentStr.equals("")) {
					Toast.makeText(FeedbacksActivity.this, "请输入反馈意见",Toast.LENGTH_SHORT).show();
				}else {
					ArticleConfig config = new ArticleConfig();
					config.setType(RequestType.FEEDBACKS);
					config.setAccessToken(mAccessToken);
					config.setCustomString(mContentStr);
					if (mLoadingDialog != null) {
					mLoadingDialog.showFullDialog();
					}
					
					ArticleManager.getInstence().postFeedbacks(config,new StringInfoCallback() {
						
						@Override
						public void onStringDataCallback(String s, boolean isSuccess) {
							if (mLoadingDialog != null) {
								mLoadingDialog.dismiss();
							}
							if (isSuccess) {
								Toast.makeText(FeedbacksActivity.this, "反馈成功", Toast.LENGTH_SHORT).show();
								FeedbacksActivity.this.finish();
							}else {
								if (s != null) {
									Toast.makeText(FeedbacksActivity.this, s , Toast.LENGTH_SHORT).show();
								}else {
									Toast.makeText(FeedbacksActivity.this, "反馈异常" , Toast.LENGTH_SHORT).show();
								}
								
							}
							
							
						}
					});
					
					
				}

			}
		});

	}

	private void changeNextBtnState() {
		if (mContentStr == null || mContentStr.length() < 1) {
			mNextButton.setBackgroundResource(R.drawable.login_button_normal);
			mNextButton.setTextColor(FeedbacksActivity.this.getResources()
					.getColor(R.color.video_share_txt));
			mNextButton.setEnabled(false);
		} else {
			mNextButton.setBackgroundResource(R.drawable.login_button_click);
			mNextButton.setTextColor(FeedbacksActivity.this.getResources()
					.getColor(R.color.white));
			mNextButton.setEnabled(true);
		}

	}

	@Override
	public void onClick(View v) {

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
