package com.nbd.article.managercallback;

import java.util.List;

import com.nbd.article.bean.StockIndex;
import com.nbd.network.bean.RequestType;

public interface StockInfoCallback {
	
	void onStockInfoCallback(List<StockIndex> infos,RequestType type); 

}
