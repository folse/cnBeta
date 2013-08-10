package com.guest.cnbeta.service;

import java.io.IOException;
import java.util.List;

import android.content.Context;

import com.guest.cnbeta.loadsource.LoadSource;
import com.guest.cnbeta.loadsource.LoadSourceInterface;
import com.guest.cnbeta.module.Article;
import com.guest.cnbeta.module.Comment;

public class CommentService extends BaseService {
	private LoadSourceInterface loadSource = new LoadSource();

	public CommentService(Context context) {
		super(context);
	}

	public List<Comment> getCommentList(Article article) throws IOException {
		return loadSource.getComments(article);
	}
}
