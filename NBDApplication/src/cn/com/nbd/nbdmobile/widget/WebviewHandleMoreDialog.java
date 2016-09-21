package cn.com.nbd.nbdmobile.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.utility.ArticleHandleType;


public class WebviewHandleMoreDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context mContext;

	private TextView mSpaceLayout; //顶部用于点击消失的部分
	private RelativeLayout mCancleLayout; // 取消的按钮
	private RelativeLayout mRefreshLayout; //刷新的按钮
	private RelativeLayout mNativeOpenLayout; //本地浏览器打开的按钮

	private RelativeLayout mWeixinShareLayout; // 微信分享的布局
	private RelativeLayout mWeixinCircleLayout; // 朋友圈的布局
	private RelativeLayout mSinaShareLayout; // 新浪分享的布局
	private RelativeLayout mQqShareLayout; // QQ分享的布局
	private RelativeLayout mQzoneShareLayout; // QQ空间的分享布局



	public interface onDialogBtnClick {
		void onModeTypeClick(ArticleHandleType type);
	}

	private onDialogBtnClick mDialogBtnClick;

	public WebviewHandleMoreDialog(Context context) {
		super(context);
	}

	public WebviewHandleMoreDialog(Context context, int theme, boolean isNight,
			boolean isStore, int textSize) {
		super(context, theme);

		this.mContext = context;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_webview_more_handle_layout);

		initUi();

		setListener();
	}

	private void initUi() {
		mSpaceLayout = (TextView) findViewById(R.id.webview_dialog_top_space);
		mCancleLayout = (RelativeLayout) findViewById(R.id.webview_more_dialog_canle_layout);

		mWeixinShareLayout = (RelativeLayout) findViewById(R.id.webview_more_weixin_layout);
		mWeixinCircleLayout = (RelativeLayout) findViewById(R.id.webview_more_pengyouquan_layout);
		mSinaShareLayout = (RelativeLayout) findViewById(R.id.webview_more_weibo_layout);
		mQqShareLayout = (RelativeLayout) findViewById(R.id.webview_more_qq_layout);
		mQzoneShareLayout = (RelativeLayout) findViewById(R.id.webview_more_qqzone_layout);
		
		mRefreshLayout = (RelativeLayout) findViewById(R.id.webview_more_refresh_open);
		mNativeOpenLayout = (RelativeLayout) findViewById(R.id.webview_more_native_open);

	}

	/**
	 * 设置监听
	 */
	private void setListener() {
		mSpaceLayout.setOnClickListener(this);
		mWeixinShareLayout.setOnClickListener(this);
		mWeixinCircleLayout.setOnClickListener(this);
		mSinaShareLayout.setOnClickListener(this);
		mQqShareLayout.setOnClickListener(this);
		mQzoneShareLayout.setOnClickListener(this);
		mCancleLayout.setOnClickListener(this);
		mNativeOpenLayout.setOnClickListener(this);
		mRefreshLayout.setOnClickListener(this);
	}

	public void setOnBtnClickListener(onDialogBtnClick listener) {
		this.mDialogBtnClick = listener;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.webview_dialog_top_space: // 整个对话框
			WebviewHandleMoreDialog.this.dismiss();
			break;
		case R.id.webview_more_weixin_layout: // 微信分享
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.WEIXIN);
			WebviewHandleMoreDialog.this.dismiss();
			break;
		case R.id.webview_more_pengyouquan_layout: // 朋友圈分享
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.WEIXIN_CIRCLE);
			WebviewHandleMoreDialog.this.dismiss();
			break;
		case R.id.webview_more_weibo_layout: // 微博分享
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.SINA);
			WebviewHandleMoreDialog.this.dismiss();
			break;
		case R.id.webview_more_qq_layout: // QQ分享
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.QQ);
			WebviewHandleMoreDialog.this.dismiss();
			break;
		case R.id.webview_more_qqzone_layout: // QQ空间分享
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.QZONE);
			WebviewHandleMoreDialog.this.dismiss();
			break;
		case R.id.webview_more_native_open: // 本地浏览器打开
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.NATIVE);
			WebviewHandleMoreDialog.this.dismiss();
			break;
		case R.id.webview_more_refresh_open: // 刷新网页
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.REFRESH);
			WebviewHandleMoreDialog.this.dismiss();
			break;
		case R.id.webview_more_dialog_canle_layout: // 取消
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.CANCLE);
			WebviewHandleMoreDialog.this.dismiss();
			break;

		default:
			break;
		}

	}


}
