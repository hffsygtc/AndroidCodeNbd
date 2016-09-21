package cn.com.nbd.nbdmobile.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.ArticleHandleType;

public class NbdAlrltDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context mContext;
	
	private TextView mMessageText;
	private TextView mOkBtn;
	private TextView mCancleBtn;

	private TextView mTopGap;
	private TextView mBottomGap;
	
	private String mShowMsg;

	public interface onDialogChooseClick {
		void onModeTypeClick(ArticleHandleType type);
	}

	private onDialogChooseClick mDialogBtnClick;

	public NbdAlrltDialog(Context context) {
		super(context);
	}

	/**
	 * 
	 * @param context
	 * @param theme
	 * @param message 弹出框显示的内容
	 */
	public NbdAlrltDialog(Context context, int theme, String message) {
		super(context, theme);
		this.mShowMsg = message;
		this.mContext = context;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_alrlt_ok_cancle);

		initUi();

		setListener();
	}

	private void initUi() {
		
		mMessageText = (TextView) findViewById(R.id.alrlt_dialog_top_text);
		mCancleBtn = (TextView) findViewById(R.id.alrlt_dialog_bottom_cancle);
		mOkBtn = (TextView) findViewById(R.id.alrlt_dialog_bottom_ok);

		mTopGap = (TextView) findViewById(R.id.alrlt_dialog_top_gap);
		mBottomGap = (TextView) findViewById(R.id.alrlt_dialog_bottom_gap);
		
		if (mShowMsg != null) {
			mMessageText.setText(mShowMsg);
		}
		
	}

	/**
	 * 设置监听
	 */
	private void setListener() {
		mMessageText.setOnClickListener(this);
		mCancleBtn.setOnClickListener(this);
		mOkBtn.setOnClickListener(this);
		mTopGap.setOnClickListener(this);
		mBottomGap.setOnClickListener(this);
		
	}

	public void setOnBtnClickListener(onDialogChooseClick listener) {
		this.mDialogBtnClick = listener;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.alrlt_dialog_top_gap: // 上面的空白
			NbdAlrltDialog.this.dismiss();
			break;
		case R.id.alrlt_dialog_bottom_gap: // 上面的空白
			NbdAlrltDialog.this.dismiss();
			break;
		case R.id.alrlt_dialog_bottom_ok:
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.OK);
			NbdAlrltDialog.this.dismiss();
			break;
		case R.id.alrlt_dialog_bottom_cancle:
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.CANCLE);
			NbdAlrltDialog.this.dismiss();
			break;

		default:
			break;
		}

	}

	public void showFullDialog(){
		this.show();
		WindowManager windowManager = ((Activity)mContext).getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		WindowManager.LayoutParams lp = this.getWindow()
				.getAttributes();
		lp.width = (int) (display.getWidth()); // 设置宽度
		lp.height = (int)(display.getHeight());
		this.getWindow().setAttributes(lp);
	}
}
