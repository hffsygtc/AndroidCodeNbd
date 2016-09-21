package cn.com.nbd.nbdmobile.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.UserConfigUtile;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.UserInfo;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.UserInfoCallback;
import com.nbd.network.bean.RequestType;
import com.umeng.analytics.MobclickAgent;

/**
 * 设置密码界面
 * 
 * @author riche
 * 
 */

public class EditNickNameActivity extends Activity implements OnClickListener {

	private ImageView mBackBtn; // 返回按钮

	private EditText mNameEdit;
	private Button mCompleteButton; // 下一步按钮

	private String oldName;
	private String newName;

	private SharedPreferences configShare;

	private String mAccessToken; // 用户的登录标识

	private SharedPreferences mNativeShare;
	private boolean isDayTheme;
	private LoadingDialog mLoadingDialog;

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
		setContentView(R.layout.activity_edit_nickname);
		Intent i = getIntent();
		oldName = i.getStringExtra("name");

		configShare = this.getSharedPreferences("AppConfig",
				Context.MODE_PRIVATE);

		mAccessToken = configShare.getString("accessToken", null);

		Log.e("reset nick name", "TOKEN" + mAccessToken);
		
		mLoadingDialog = new LoadingDialog(EditNickNameActivity.this, R.style.loading_dialog, "修改中...");

		initUi();
		setListener();

	}

	private void initUi() {
		
		mBackBtn = (ImageView) findViewById(R.id.nickname_back_btn);
		mNameEdit = (EditText) findViewById(R.id.nickname_input);
		mCompleteButton = (Button) findViewById(R.id.nickname_button);
		
		mNameEdit.setHint(oldName);
	}

	private void setListener() {

		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent();
				i.putExtra("name", "");
				EditNickNameActivity.this.setResult(1, i);
				EditNickNameActivity.this.finish();
			}
		});

		mNameEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				newName = s.toString();
			}

			@Override
			public void afterTextChanged(Editable s) {
				newName = s.toString();
				changeCompleteButtonState();
			}
		});
		
		mCompleteButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (oldName.equals(newName)) {
					EditNickNameActivity.this.finish();
				}else {
					
					ArticleConfig config = new ArticleConfig();
					config.setType(RequestType.UPDATE_NAME);
					config.setAccessToken(mAccessToken);
					config.setPassword(newName);
					if (mLoadingDialog != null) {
						mLoadingDialog.showFullDialog();
					}
					ArticleManager.getInstence().updateNickName(config,new UserInfoCallback() {
						
						@Override
						public void onUserinfoCallback(UserInfo info, String failMsg) {
							if (mLoadingDialog != null) {
								mLoadingDialog.dismiss();
							}
							if (info != null) {
								SharedPreferences.Editor mEditor = configShare
										.edit();
								UserConfigUtile.storeSelfConfigToNative(
										mEditor, info);

								Intent i = new Intent();
								i.putExtra("name", newName);
								EditNickNameActivity.this.setResult(1, i);
								EditNickNameActivity.this.finish();
							} else if (failMsg != null) {
								Toast.makeText(EditNickNameActivity.this, failMsg,
										Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(EditNickNameActivity.this, "修改失败 ",
										Toast.LENGTH_SHORT).show();

							}
							
						}
					});
					
					
				}
				
			}
		});
		
	}

	/**
	 * 根据输入状况显示完成按钮的样式
	 */
	private void changeCompleteButtonState() {

		if (newName == null || newName.length() < 1) {
			mCompleteButton
					.setBackgroundResource(R.drawable.login_button_normal);
			mCompleteButton.setTextColor(EditNickNameActivity.this
					.getResources().getColor(R.color.video_share_txt));
			mCompleteButton.setEnabled(false);
		} else {
			mCompleteButton
					.setBackgroundResource(R.drawable.login_button_click);
			mCompleteButton.setTextColor(EditNickNameActivity.this
					.getResources().getColor(R.color.white));
			mCompleteButton.setEnabled(true);
		}

	}

	@Override
	public void onClick(View v) {

	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent i = new Intent();
			i.putExtra("name", "");
			EditNickNameActivity.this.setResult(1, i);
			EditNickNameActivity.this.finish();
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
