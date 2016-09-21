package com.nbd.article.bean;

import org.litepal.crud.DataSupport;

/**
 * 图集数据的图片集合数据
 * 
 * @author riche
 *
 */
public class ArticleImages extends DataSupport {

	/** 创建时间 */
	private long created_at;
	/** 描述内容 */
	private String desc;
	/** 图集的ID */
	private long gallery_id;
	/** 图片的ID */
	private long image_id;
	/** 位置 */
	private int pos;
	/** 更新时间 */
	private String updated_at;
	/** 图片资源 */
	private String image_src;
	
	private long galleryId;

	
	public long getGalleryId() {
		return galleryId;
	}

	public void setGalleryId(long galleryId) {
		this.galleryId = galleryId;
	}

	public long getCreated_at() {
		return created_at;
	}

	public void setCreated_at(long created_at) {
		this.created_at = created_at;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getGallery_id() {
		return gallery_id;
	}

	public void setGallery_id(long gallery_id) {
		this.gallery_id = gallery_id;
	}

	public long getImage_id() {
		return image_id;
	}

	public void setImage_id(long image_id) {
		this.image_id = image_id;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}

	public String getImage_src() {
		return image_src;
	}

	public void setImage_src(String image_src) {
		this.image_src = image_src;
	}
	
	public void valueGalleryId(){
		this.galleryId = gallery_id;
	}

}
