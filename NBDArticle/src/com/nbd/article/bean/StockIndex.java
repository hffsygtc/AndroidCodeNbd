package com.nbd.article.bean;

import org.litepal.crud.DataSupport;

public class StockIndex extends DataSupport {

	/** 证券指数的名称 */
	private String secShortName;
	/** 最新的股价 */
	private String new_price;
	/** 改变的大小 */
	private String change_amount;
	/** 改变 */
	private String change;
	public String getSecShortName() {
		return secShortName;
	}
	public void setSecShortName(String secShortName) {
		this.secShortName = secShortName;
	}
	public String getNew_price() {
		return new_price;
	}
	public void setNew_price(String new_price) {
		this.new_price = new_price;
	}
	public String getChange_amount() {
		return change_amount;
	}
	public void setChange_amount(String change_amount) {
		this.change_amount = change_amount;
	}
	public String getChange() {
		return change;
	}
	public void setChange(String change) {
		this.change = change;
	}
	
	
}
