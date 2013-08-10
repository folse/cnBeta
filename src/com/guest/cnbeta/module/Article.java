package com.guest.cnbeta.module;

import java.io.Serializable;

public class Article implements Serializable {
	
	private static final long serialVersionUID = -3742211519069060634L;
	private int id;
	private String title;
	private String avatar;
	private int hits;
	private String info;
	private String content;

	private boolean contented;

	public Article() {
		contented = false;
	}

	public boolean isContented() {
		return contented;
	}

	public void setContented(boolean contented) {
		this.contented = contented;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

}
