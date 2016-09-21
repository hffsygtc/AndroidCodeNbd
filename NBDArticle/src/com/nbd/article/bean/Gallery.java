package com.nbd.article.bean;

import java.util.ArrayList;
import java.util.List;

import org.litepal.crud.DataSupport;

import android.util.Log;

/**
 * 图集的数据结构
 * @author he
 *
 */
public class Gallery extends DataSupport {
	
	private long id;
	private int gallery_id;
	private String title;
	private String url;
	private String Image;
	private String type;
	private String desc;
	private int review_count;
	private boolean allow_review;
	private List<ArticleImages> images = new ArrayList<ArticleImages>();
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ArticleImages> getImagesData() {
		return DataSupport.where("galleryId = ?", gallery_id+"")
				.find(ArticleImages.class);
	}
	
	public int getGallery_id() {
		return gallery_id;
	}
	public void setGallery_id(int gallery_id) {
		this.gallery_id = gallery_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getImage() {
		return Image;
	}
	public void setImage(String image) {
		Image = image;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getReview_count() {
		return review_count;
	}
	public void setReview_count(int review_count) {
		this.review_count = review_count;
	}
	public boolean isAllow_review() {
		return allow_review;
	}
	public void setAllow_review(boolean allow_review) {
		this.allow_review = allow_review;
	}
	public List<ArticleImages> getImages() {
		return images;
	}
	public void setImages(List<ArticleImages> images) {
		this.images = images;
	}
	
	
	
	

}
