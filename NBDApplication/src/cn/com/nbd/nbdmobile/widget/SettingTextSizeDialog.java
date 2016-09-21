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

public class SettingTextSizeDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context mContext;

	private RelativeLayout mDialogLayout; // 用于点击消失的
	private TextView mSpaceLayout;
	private RelativeLayout mCancleLayout; // 取消的按钮

	// 字体小
	private RelativeLayout mSmallTextLayout;
	private ImageView mSmallTextNormalImg;
	private ImageView mSmallTextClickImg;
	private TextView mSmallTextView;
	// 字体中
	private RelativeLayout mMidTextLayout;
	private ImageView mMidTextNormalImg;
	private ImageView mMidTextClickImg;
	private TextView mMidTextView;
	// 字体大
	private RelativeLayout mBigTextLayout;
	private ImageView mBigTextNormalImg;
	private ImageView mBigTextClickImg;
	private TextView mBigTextView;

	private int mTextSize;

	public interface onDialogBtnClick {
		void onModeTypeClick(ArticleHandleType type);
	}

	private onDialogBtnClick mDialogBtnClick;

	public SettingTextSizeDialog(Context context) {
		super(context);
	}

	public SettingTextSizeDialog(Context context, int theme, int textSize) {
		super(context, theme);

		this.mTextSize = textSize;

		this.mContext = context;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting_textsize_dialog);

		initUi();

		setListener();
	}

	private void initUi() {
		mSpaceLayout = (TextView) findViewById(R.id.article_more_top_space);
		mDialogLayout = (RelativeLayout) findViewById(R.id.article_more_dialog_layout);
		mCancleLayout = (RelativeLayout) findViewById(R.id.article_more_dialog_canle_layout);

		mSmallTextLayout = (RelativeLayout) findViewById(R.id.article_more_small_text_layout);
		mSmallTextNormalImg = (ImageView) findViewById(R.id.article_more_small_noraml);
		mSmallTextClickImg = (ImageView) findViewById(R.id.article_more_small_click);
		mSmallTextView = (TextView) findViewById(R.id.article_more_small_textview);

		mMidTextLayout = (RelativeLayout) findViewById(R.id.article_more_normal_text_layout);
		mMidTextNormalImg = (ImageView) findViewById(R.id.article_more_normal_noraml);
		mMidTextClickImg = (ImageView) findViewById(R.id.article_more_normal_click);
		mMidTextView = (TextView) findViewById(R.id.article_more_normal_textview);

		mBigTextLayout = (RelativeLayout) findViewById(R.id.article_more_big_text_layout);
		mBigTextNormalImg = (ImageView) findViewById(R.id.article_more_big_noraml);
		mBigTextClickImg = (ImageView) findViewById(R.id.article_more_big_click);
		mBigTextView = (TextView) findViewById(R.id.article_more_big_textview);

		if (mTextSize == 0) {
			changeTextState(ArticleHandleType.TEXT_SMALL);
		} else if (mTextSize == 1) {
			changeTextState(ArticleHandleType.TEXT_MID);
		} else {
			changeTextState(ArticleHandleType.TEXT_BIG);
		}

	}

	/**
	 * 设置监听
	 */
	private void setListener() {
		mSpaceLayout.setOnClickListener(this);
		mSmallTextLayout.setOnClickListener(this);
		mMidTextLayout.setOnClickListener(this);
		mBigTextLayout.setOnClickListener(this);
		mCancleLayout.setOnClickListener(this);
	}

	public void setOnBtnClickListener(onDialogBtnClick listener) {
		this.mDialogBtnClick = listener;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.article_more_top_space: // 整个对话框
			SettingTextSizeDialog.this.dismiss();
			break;
		case R.id.article_more_small_text_layout:
			changeTextState(ArticleHandleType.TEXT_SMALL);
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.TEXT_SMALL);
			SettingTextSizeDialog.this.dismiss();
			break;
		case R.id.article_more_normal_text_layout:
			changeTextState(ArticleHandleType.TEXT_MID);
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.TEXT_MID);
			SettingTextSizeDialog.this.dismiss();
			break;
		case R.id.article_more_big_text_layout:
			changeTextState(ArticleHandleType.TEXT_BIG);
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.TEXT_BIG);
			SettingTextSizeDialog.this.dismiss();
			break;
		case R.id.article_more_dialog_canle_layout:
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.CANCLE);
			SettingTextSizeDialog.this.dismiss();
			break;

		default:
			break;
		}

	}

	/**
	 * 根据文字的选择，显示字体选中状态
	 * 
	 * @param textSmall
	 */
	private void changeTextState(ArticleHandleType textType) {
		mSmallTextNormalImg.setVisibility(View.VISIBLE);
		mMidTextNormalImg.setVisibility(View.VISIBLE);
		mBigTextNormalImg.setVisibility(View.VISIBLE);
		mSmallTextClickImg.setVisibility(View.GONE);
		mMidTextClickImg.setVisibility(View.GONE);
		mBigTextClickImg.setVisibility(View.GONE);

		mSmallTextView.setTextColor(mContext.getResources().getColor(
				R.color.fast_news_section_time));
		mSmallTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext
				.getResources().getDimension(R.dimen.font_text_size_18));
		mMidTextView.setTextColor(mContext.getResources().getColor(
				R.color.fast_news_section_time));
		mMidTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext
				.getResources().getDimension(R.dimen.font_text_size_18));
		mBigTextView.setTextColor(mContext.getResources().getColor(
				R.color.fast_news_section_time));
		mBigTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext
				.getResources().getDimension(R.dimen.font_text_size_18));

		switch (textType) {
		case TEXT_BIG:
			mBigTextClickImg.setVisibility(View.VISIBLE);
			mBigTextNormalImg.setVisibility(View.GONE);
			mBigTextView.setTextColor(mContext.getResources().getColor(
					R.color.bottom_tab_click_txt));
			mBigTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext
					.getResources().getDimension(R.dimen.font_text_size_24));
			break;
		case TEXT_MID:
			mMidTextNormalImg.setVisibility(View.GONE);
			mMidTextClickImg.setVisibility(View.VISIBLE);
			mMidTextView.setTextColor(mContext.getResources().getColor(
					R.color.bottom_tab_click_txt));
			mMidTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext
					.getResources().getDimension(R.dimen.font_text_size_24));
			break;
		case TEXT_SMALL:
			mSmallTextClickImg.setVisibility(View.VISIBLE);
			mSmallTextNormalImg.setVisibility(View.GONE);
			mSmallTextView.setTextColor(mContext.getResources().getColor(
					R.color.bottom_tab_click_txt));
			mSmallTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext
					.getResources().getDimension(R.dimen.font_text_size_24));
			break;
		default:
			break;
		}
	}

}
