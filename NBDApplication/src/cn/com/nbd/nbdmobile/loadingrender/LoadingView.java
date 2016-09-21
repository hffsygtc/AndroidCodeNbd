package cn.com.nbd.nbdmobile.loadingrender;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class LoadingView extends ImageView {

	private LoadingDrawable mLoadingDrawable;

	public LoadingView(Context context) {
		this(context, null);
	}

	public LoadingView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public LoadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context);
	}

	private void initAttrs(Context context) {
//		LoadingRenderer showRenderer = new DanceLoadingRenderer(context);
//		LoadingRenderer showRenderer = new CircleBloodLoadingRenderer(context);
		LoadingRenderer showRenderer = new CoolWaitLoadingRenderer(context);
		setLoadingRenderer(showRenderer);
	}

	public void setLoadingRenderer(LoadingRenderer loadingRenderer) {
		mLoadingDrawable = new LoadingDrawable(loadingRenderer);
		setImageDrawable(mLoadingDrawable);
	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
		startAnimation();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		stopAnimation();
	}

	@Override
	protected void onVisibilityChanged(View changedView, int visibility) {
		super.onVisibilityChanged(changedView, visibility);
		if (visibility == View.VISIBLE) {
			startAnimation();
		} else {
			stopAnimation();
		}
	}

	private void startAnimation() {
		if (mLoadingDrawable != null) {
			mLoadingDrawable.start();
		}
	}

	private void stopAnimation() {
		if (mLoadingDrawable != null) {
			mLoadingDrawable.stop();
		}
	}

}
