package cn.com.nbd.nbdmobile.webview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * webview 基本参数设置
 * @author riche
 *
 */

@SuppressLint("SetJavaScriptEnabled")
public class NBDWebView extends WebView{
	
	public NBDWebView(Context context){
		super(context);
		init(context);
	}
	
	public NBDWebView(Context context,AttributeSet attrs){
		super(context, attrs);
		init(context);
	}

	public NBDWebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	private void initWebView() {
		WebSettings settings = this.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSupportZoom(false);
		settings.supportMultipleWindows();
		settings.setAllowFileAccess(true);
		settings.setJavaScriptCanOpenWindowsAutomatically(true);
		settings.setLoadsImagesAutomatically(true);
		settings.setDefaultTextEncodingName("utf-8");
		settings.setAllowFileAccess(true);
		settings.setLoadWithOverviewMode(true);

		this.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View view) {
			   return true;
			}
		});
		
	}
	
	private void init(Context context){
		initWebView();
	}
	
}
