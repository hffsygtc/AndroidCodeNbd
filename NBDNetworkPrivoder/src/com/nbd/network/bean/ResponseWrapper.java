package com.nbd.network.bean;

import java.io.Serializable;


public class ResponseWrapper implements Serializable {

	RequestType requestType;
	
	/**
	 * 返回的原始数据
	 */
	String responseData;
	final boolean isError;
	
	public ResponseWrapper(RequestType requestType,boolean isError){
		this.requestType = requestType;
		this.isError = isError;
	}
	
	public boolean isError() {
		return isError;
	}
	/**
	 * 获取返回的数据
	 * @return
	 */
	public String getResponseData() {
		return responseData;
	}
	public void setResponseData(String responseData) {
		this.responseData = responseData;
	}
	public RequestType getRequestType() {
		return requestType;
	}
	public void setRequestType(RequestType requestType) {
		this.requestType = requestType;
	}
}
