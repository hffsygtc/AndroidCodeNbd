package cn.com.nbd.nbdmobile.webview;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.view.KeyEvent;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * 页面加载事件步骤回调
 * 
 * @author riche
 *
 */

@SuppressLint("NewApi")
public class NBDWebViewClient extends WebViewClient{

	//webview加载结束
	@Override
	public void onPageFinished(WebView view, String url) {
		// 事件处理
		super.onPageFinished(view, url);
		
	}

	//webview开始加载
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		// TODO Auto-generated method stub
		super.onPageStarted(view, url, favicon);
	}

	//https接口处理
	@Override
	public void onReceivedSslError(WebView view, SslErrorHandler handler,
			SslError error) {
		// TODO Auto-generated method stub
		super.onReceivedSslError(view, handler, error);
	}

	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
		// TODO Auto-generated method stub
		return super.shouldInterceptRequest(view, url);
	}

	//浏览器按键事件分发
	@Override
	public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.shouldOverrideKeyEvent(view, event);
	}

	//重定向
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		// TODO Auto-generated method stub
		view.loadUrl(url);
		return true;
	}
	
}
