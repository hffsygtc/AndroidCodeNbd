package com.nbd.article.bean;

import java.util.List;

/**
 * 活动详情
 * @author he
 *
 */
public class FeatureDetail {

	private String title;
	private String lead;
	private int click_count;
	private String type;
	private int feature_id;
	private long created_at;
	private long update_at;
	private String avatar;
	private String share_url;
	private List<FeatureLable> app_feature_labels;
	
	
	public List<FeatureLable> getApp_feature_labels() {
		return app_feature_labels;
	}
	public void setApp_feature_labels(List<FeatureLable> app_feature_labels) {
		this.app_feature_labels = app_feature_labels;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getLead() {
		return lead;
	}
	public void setLead(String lead) {
		this.lead = lead;
	}
	public int getClick_count() {
		return click_count;
	}
	public void setClick_count(int click_count) {
		this.click_count = click_count;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getFeature_id() {
		return feature_id;
	}
	public void setFeature_id(int feature_id) {
		this.feature_id = feature_id;
	}
	public long getCreated_at() {
		return created_at;
	}
	public void setCreated_at(long created_at) {
		this.created_at = created_at;
	}
	public long getUpdate_at() {
		return update_at;
	}
	public void setUpdate_at(long update_at) {
		this.update_at = update_at;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getShare_url() {
		return share_url;
	}
	public void setShare_url(String share_url) {
		this.share_url = share_url;
	}
	
	
	
	
	
	
}
