package com.guest.cnbeta.module;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Comment {
	private Article article;
	private int id;
	private int number;
	private String author;
	private Date date;
	private String comment;
	private int like;
	private int dislike;

	public Article getArticle() {
		return article;
	}

	public void setArticle(Article article) {
		this.article = article;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		comment = code2string(comment);
		this.comment = comment;
	}

	public int getLike() {
		return like;
	}

	public void setLike(int like) {
		this.like = like;
	}

	public int getDislike() {
		return dislike;
	}

	public void setDislike(int dislike) {
		this.dislike = dislike;
	}

	public String code2string(String str) {
		String pattern = "&#(\\w.*?);";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		char chr = 0;
		while (m.find()) {
			
			try {
				chr = (char) Integer.parseInt(m.group(1));
			} catch (Exception e) {
				// TODO: handle exception
				chr = (char)0;
			}
			str = str.replace("&#" + m.group(1) + ";", String.valueOf(chr));
		}
		return str;
	}

}
