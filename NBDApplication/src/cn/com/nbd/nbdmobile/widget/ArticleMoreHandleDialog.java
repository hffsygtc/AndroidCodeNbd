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


public class ArticleMoreHandleDialog extends Dialog implements
		android.view.View.OnClickListener {

	private Context mContext;

	private RelativeLayout mDialogLayout; // 用于点击消失的
	private TextView mSpaceLayout;
	private RelativeLayout mCancleLayout; // 取消的按钮

	private RelativeLayout mWeixinShareLayout; // 微信分享的布局
	private RelativeLayout mWeixinCircleLayout; // 朋友圈的布局
	private RelativeLayout mSinaShareLayout; // 新浪分享的布局
	private RelativeLayout mQqShareLayout; // QQ分享的布局
	private RelativeLayout mQzoneShareLayout; // QQ空间的分享布局

	private RelativeLayout mNightModelLayout; // 夜间模式的按钮
	private ImageView mNightModelImg; // 夜间模式的图标
	private TextView mNightText;

	private RelativeLayout mStoreLayout; // 收藏的按钮
	private ImageView mStoreImg; // 收藏的图片
	private TextView mStoreText;

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

	private boolean isNight;
	private boolean isStored;
	private int mTextSize;

	public interface onDialogBtnClick {
		void onModeTypeClick(ArticleHandleType type);
	}

	private onDialogBtnClick mDialogBtnClick;

	public ArticleMoreHandleDialog(Context context) {
		super(context);
	}

	public ArticleMoreHandleDialog(Context context, int theme, boolean isNight,
			boolean isStore, int textSize) {
		super(context, theme);

		this.isNight = isNight;
		this.isStored = isStore;
		this.mTextSize = textSize;

		this.mContext = context;

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_article_more_handle_layout);

		initUi();

		setListener();
	}

	private void initUi() {
		mSpaceLayout = (TextView) findViewById(R.id.article_more_top_space);
		mDialogLayout = (RelativeLayout) findViewById(R.id.article_more_dialog_layout);
		mCancleLayout = (RelativeLayout) findViewById(R.id.article_more_dialog_canle_layout);

		mWeixinShareLayout = (RelativeLayout) findViewById(R.id.article_more_weixin_layout);
		mWeixinCircleLayout = (RelativeLayout) findViewById(R.id.article_more_pengyouquan_layout);
		mSinaShareLayout = (RelativeLayout) findViewById(R.id.article_more_weibo_layout);
		mQqShareLayout = (RelativeLayout) findViewById(R.id.article_more_qq_layout);
		mQzoneShareLayout = (RelativeLayout) findViewById(R.id.article_more_qqzone_layout);

		mNightModelLayout = (RelativeLayout) findViewById(R.id.article_more_night_layout);
		mNightModelImg = (ImageView) findViewById(R.id.article_more_night_img);
		mNightText = (TextView) findViewById(R.id.article_more_night_text);

		mStoreLayout = (RelativeLayout) findViewById(R.id.article_more_store_layout);
		mStoreImg = (ImageView) findViewById(R.id.article_more_store_img);
		mStoreText = (TextView) findViewById(R.id.article_more_store_text);

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
		

		changeModeState(ArticleHandleType.NIGHT,  isNight); //处理当前是否是夜间模式的状态
		changeModeState(ArticleHandleType.STORE,  !isStored); //处理文章的是否收藏的状态
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
		mNightModelLayout.setOnClickListener(this);
		mStoreLayout.setOnClickListener(this);
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
			ArticleMoreHandleDialog.this.dismiss();
			break;
		case R.id.article_more_weixin_layout: // 微信分享
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.WEIXIN);
			ArticleMoreHandleDialog.this.dismiss();
			break;
		case R.id.article_more_pengyouquan_layout: // 朋友圈分享
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.WEIXIN_CIRCLE);
			ArticleMoreHandleDialog.this.dismiss();
			break;
		case R.id.article_more_weibo_layout: // 微博分享
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.SINA);
			ArticleMoreHandleDialog.this.dismiss();
			break;
		case R.id.article_more_qq_layout: // QQ分享
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.QQ);
			ArticleMoreHandleDialog.this.dismiss();
			break;
		case R.id.article_more_qqzone_layout: // QQ空间分享
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.QZONE);
			ArticleMoreHandleDialog.this.dismiss();
			break;
		case R.id.article_more_night_layout: // 夜间模式
			changeModeState(ArticleHandleType.NIGHT, isNight);
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.NIGHT);
			ArticleMoreHandleDialog.this.dismiss();
			break;
		case R.id.article_more_store_layout: // 收藏文章
			changeModeState(ArticleHandleType.STORE, isStored);
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.STORE);
			ArticleMoreHandleDialog.this.dismiss();
			break;
		case R.id.article_more_small_text_layout:
			changeTextState(ArticleHandleType.TEXT_SMALL);
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.TEXT_SMALL);
			ArticleMoreHandleDialog.this.dismiss();
			break;
		case R.id.article_more_normal_text_layout:
			changeTextState(ArticleHandleType.TEXT_MID);
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.TEXT_MID);
			ArticleMoreHandleDialog.this.dismiss();
			break;
		case R.id.article_more_big_text_layout:
			changeTextState(ArticleHandleType.TEXT_BIG);
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.TEXT_BIG);
			ArticleMoreHandleDialog.this.dismiss();
			break;
		case R.id.article_more_dialog_canle_layout:
			mDialogBtnClick.onModeTypeClick(ArticleHandleType.CANCLE);
			ArticleMoreHandleDialog.this.dismiss();
			break;

		default:
			break;
		}

	}

	/**
	 * 根据文章收藏，日夜间模式的配置显示对应的
	 * @param type
	 * @param isCheck
	 */
	private void changeModeState(ArticleHandleType type, boolean isCheck) {
		switch (type) {
		case STORE:
			if (isCheck) { // 现在是收藏状态，点击切换为没收藏
				isStored = false;
				mStoreImg
						.setBackgroundResource(R.drawable.article_more_dialog_stroed);
				mStoreText.setText("收藏");
			} else {
				isStored = true;
				mStoreImg
						.setBackgroundResource(R.drawable.article_more_dialog_sotred_ok);
				mStoreText.setText("已收藏");
			}
			break;
		case NIGHT:
			if (isCheck) { // 现在是夜间模式状态，点击切换为没收藏
				isNight = false;
				mNightModelImg
						.setBackgroundResource(R.drawable.article_more_dialog_day);
				mNightText.setText("日间模式");
			} else {
				isNight = true;
				mNightModelImg
						.setBackgroundResource(R.drawable.article_more_dialog_night);
				mNightText.setText("夜间模式");
			}
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
