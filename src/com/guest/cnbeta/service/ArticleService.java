package com.guest.cnbeta.service;

import java.io.IOException;

import android.content.Context;

import com.guest.cnbeta.loadsource.LoadSource;
import com.guest.cnbeta.loadsource.LoadSourceInterface;
import com.guest.cnbeta.module.Article;

public class ArticleService extends BaseService {
	private LoadSourceInterface loadSource = new LoadSource();

	public ArticleService(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public Article getMoreDetail(Article article) throws IOException {
		return loadSource.getArticleContent(article);
	}
}
