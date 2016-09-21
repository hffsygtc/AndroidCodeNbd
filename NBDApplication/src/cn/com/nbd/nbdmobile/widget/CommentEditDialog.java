package cn.com.nbd.nbdmobile.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.activity.RegisterActivity;
import cn.com.nbd.nbdmobile.utility.ArticleHandleType;

public class CommentEditDialog extends Dialog {

	private Context mContext;

	private EditText mEditText;
	private ImageView mSendBtn;

	private TextView mTopGap;
	
	private String mCommentStr;
	

	public interface onCommentSendInterface {
		void onCommentSend(String comment);
	}

	private onCommentSendInterface mSendListener;

	public void setCommentSendListener(onCommentSendInterface listner) {
		this.mSendListener = listner;
	}

	public CommentEditDialog(Context context) {
		super(context);
	}

	public CommentEditDialog(Context context, int theme) {
		super(context, theme);

		this.mContext = context;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_comment_input);

		initUi();

		setListener();
	}

	private void initUi() {
		mEditText = (EditText) findViewById(R.id.comment_input_edit);
		mSendBtn = (ImageView) findViewById(R.id.comment_input_send_btn);
		mTopGap = (TextView) findViewById(R.id.comment_dialog_top_gap);
		
		mEditText.requestFocus();

	}

	private void setListener() {

		mEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				Log.e("EDIT_TEXT_bottom", s + "==" + count);

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				mCommentStr = s.toString();
				changeNextBtnState();

			}
		});

		mSendBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mSendListener != null) {
					mSendListener.onCommentSend(mCommentStr);
					mCommentStr = "";
					CommentEditDialog.this.dismiss();
				}
			}
		});

		
		mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					CommentEditDialog.this.dismiss();
				}
			}
		});
		
		mTopGap.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				CommentEditDialog.this.dismiss();
				
			}
		});
		
	}

	private void changeNextBtnState() {
		if (mCommentStr == null || mCommentStr.length() < 1) {
			mSendBtn.setBackgroundResource(R.drawable.comment_send_grey);
			mSendBtn.setEnabled(false);
		} else {
			mSendBtn.setBackgroundResource(R.drawable.comment_send_red);
			mSendBtn.setEnabled(true);
		}
	}

	public void showFullDialog() {
		this.show();
		if (mEditText != null) {
			mEditText.setText(mCommentStr);
		}
		WindowManager windowManager = ((Activity) mContext).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		lp.width = (int) (display.getWidth()); // 设置宽度
		lp.height = (int) (display.getHeight());
		this.getWindow().setAttributes(lp);
	}
	
	public void showKeyboard() {  
        if(mEditText!=null){  
            //设置可获得焦点  
        	mEditText.setFocusable(true);  
        	mEditText.setFocusableInTouchMode(true);  
            //请求获得焦点  
        	mEditText.requestFocus();  
            //调用系统输入法  
            InputMethodManager inputManager = (InputMethodManager) mEditText  
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);  
            inputManager.showSoftInput(mEditText, 0);  
        }  
        
    	Rect r = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
		
		int screenHeight = this.getWindow().getDecorView().getRootView().getHeight();
		int keyboardHeight = screenHeight - r.bottom;
		
		Log.e("KEY BOARD HEIGHT-->", keyboardHeight+"--screenH--"+screenHeight+"--rtop--"+r.top);
    }  
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.e("DIALOG", "BACK");
			CommentEditDialog.this.dismiss();
		}
		return super.onKeyDown(keyCode, event);
	}

}
