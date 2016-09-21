package com.nbd.article.bean;

import org.litepal.crud.DataSupport;

public class FeatureInfo extends DataSupport {

	private String title;
	private String lead;
	private int click_count;
	private String type;
	private int feature_id;
	private long created_at;
	private long updated_at;
	private String avatar;
	private String redirect_to;

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

	public long getUpdated_at() {
		return updated_at;
	}

	public void setUpdated_at(long updated_at) {
		this.updated_at = updated_at;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getRedirect_to() {
		return redirect_to;
	}

	public void setRedirect_to(String redirect_to) {
		this.redirect_to = redirect_to;
	}

}
