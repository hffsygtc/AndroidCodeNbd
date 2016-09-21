package com.nbd.network.networkprivoder;

import com.nbd.network.bean.RequestType;
import com.nbd.network.bean.RequestWrapper;

public interface NetworkBase {

	/**
	 * 请求网络数据接口
	 * 
	 * @param request
	 *            数据请求封装
	 * @param listener
	 *            数据返回回调，可为空
	 */
	void requestData(RequestWrapper request, RequestListener listener);

	/**
	 * 注册数据监听回调，用于长期关注某种网络事件事件,必须保证线程安全
	 * 
	 * @param listener
	 *            数据请求回调
	 * @param type
	 *            关注的网络请求事件类型
	 */
	void registerRequestListener(RequestListener listener, RequestType type);

	/**
	 * 反注册事件监听回调
	 * 
	 * @param listener
	 *            反注册回调,必须保证线程安全
	 * @param type
	 *            反注册的监听类型
	 */
	void unRegisterRequestListener(RequestListener listener, RequestType type);

}
