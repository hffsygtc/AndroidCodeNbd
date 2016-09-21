package cn.com.nbd.nbdmobile.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import cn.com.nbd.nbdmobile.R;

public class ChoosePictureDialog extends Dialog {

	private Context mContext;
	
	private RelativeLayout mDialogLayout; //用于点击消失的
	private RelativeLayout mCamereLayout; //相机拍照的按钮
	private RelativeLayout mGalleryLayout; // 本地相册的按钮
	private RelativeLayout mCancleLayout; //取消的按钮 

	private int CANCLE = 0;
	private int GALLERY = 1;
	private int CAMERE = 2;
	
	public interface onDialogBtnClick{
		void onChoosePhotoFrom(int type);
	}
	
	private onDialogBtnClick mDialogBtnClick;
	

	public ChoosePictureDialog(Context context) {
		super(context);
	}

	public ChoosePictureDialog(Context context, int theme) {
		super(context, theme);

		this.mContext = context;


	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_choose_picture_layout);
		
		mDialogLayout = (RelativeLayout) findViewById(R.id.photo_dialog_layout);
		mCamereLayout = (RelativeLayout) findViewById(R.id.photo_dialog_pz_layout);
		mGalleryLayout = (RelativeLayout) findViewById(R.id.photo_dialog_xc_layout);
		mCancleLayout = (RelativeLayout) findViewById(R.id.photo_dialog_canle_layout);

		setListener();
	}

	/**
	 * 设置监听
	 */
	private void setListener() {
		
		mDialogLayout.setOnClickListener( new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ChoosePictureDialog.this.dismiss();
			}
		});
		
		/**
		 * 从相机拍照 
		 */
		mCamereLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialogBtnClick.onChoosePhotoFrom(CAMERE);
			}
		});
		/**
		 * 从相册选择
		 */
		mGalleryLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialogBtnClick.onChoosePhotoFrom(GALLERY);
			}
		});
		/**
		 * 取消
		 */
		mCancleLayout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialogBtnClick.onChoosePhotoFrom(CANCLE);
			}
		});
		
		

	}
	
	public void setOnBtnClickListener(onDialogBtnClick listener){
		this.mDialogBtnClick = listener;
	}
}
