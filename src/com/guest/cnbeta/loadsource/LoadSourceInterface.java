package com.guest.cnbeta.loadsource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import android.graphics.Bitmap;

import com.guest.cnbeta.module.Article;
import com.guest.cnbeta.module.Comment;

public interface LoadSourceInterface {
	
	public List<Article> getArticleListFromWeb() throws MalformedURLException, IOException;
	
	public List<Article> getMoreArticleListFromWeb(int page) throws MalformedURLException, IOException;
	
	public Article getArticleContent(Article article) throws IOException;
	
	public List<Comment> getComments(Article article) throws IOException;
	
	public boolean supportComment(Comment comment) throws IOException;
	
	public boolean againstComment(Comment comment) throws IOException;
	
	public Bitmap getSafeCode() throws IOException;
	
	public String postComments(Article article, String comment, String valimg_main) throws IOException;

}
