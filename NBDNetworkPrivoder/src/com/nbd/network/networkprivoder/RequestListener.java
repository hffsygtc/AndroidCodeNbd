package com.nbd.network.networkprivoder;

import com.nbd.network.bean.ResponseWrapper;

public interface RequestListener {
	
	void onResponse(ResponseWrapper response);
	
	void onErrorResponse();
}
