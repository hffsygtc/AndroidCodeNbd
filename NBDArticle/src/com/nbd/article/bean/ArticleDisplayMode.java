package com.nbd.article.bean;

import java.util.List;

import org.litepal.crud.DataSupport;

public class ArticleDisplayMode extends DataSupport {
	
	/**
	 * 显示的样式
	 */
	private int display_form;
	
	private List<String> images;
	
	private String imageOne;
	private String imageTwo;
	private String imageThree;
	

	public String getImageOne() {
		return imageOne;
	}

	public void setImageOne(String imageOne) {
		this.imageOne = imageOne;
	}

	public String getImageTwo() {
		return imageTwo;
	}

	public void setImageTwo(String imageTwo) {
		this.imageTwo = imageTwo;
	}

	public String getImageThree() {
		return imageThree;
	}

	public void setImageThree(String imageThree) {
		this.imageThree = imageThree;
	}

	public int getDisplay_form() {
		return display_form;
	}

	public void setDisplay_form(int display_form) {
		this.display_form = display_form;
	}

	public List<String> getImages() {
		return images;
	}

	public void setImages(List<String> images) {
		this.images = images;
	}

}
