package com.guest.cnbeta.module;

import android.content.Context;

public class Avatar {

	private int id;
	private String topic;
	private String img;

	public Avatar(Context context) {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
}
