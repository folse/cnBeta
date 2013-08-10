package com.guest.cnbeta.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;

import com.guest.cnbeta.module.Article;

public class ArticleDB extends BaseDB {

	public ArticleDB(Context context) {
		super(context);
	}

	public List<Article> getList() {

		List<Article> list = new ArrayList<Article>();

		Cursor cursor = db.rawQuery(
				"SELECT * FROM article ORDER BY fromid DESC LIMIT 300", null);
		while (cursor.moveToNext()) {
			Article article = new Article();
			article.setId(cursor.getInt(1));
			article.setTitle(cursor.getString(2));
			article.setInfo(cursor.getString(3));
			article.setAvatar(cursor.getString(4));

			list.add(article);
		}
		cursor.close();

		return list;
	}

	public Article getByFromid(int fromid) {
		Article article = null;
		String sql = "SELECT * FROM article WHERE fromid = '" + fromid + "'";
		Cursor cursor = db.rawQuery(sql, null);
		if (cursor.getCount() >= 1) {
			article = new Article();
		}
		cursor.close();
		return article;
	}

	public void save(Article article) {
		if (getByFromid(article.getId()) == null) {
			String sql = "INSERT INTO article (fromid, title, info, avatar) VALUES "
					+ "('"
					+ article.getId()
					+ "', '"
					+ toSafeString(article.getTitle())
					+ "', '"
					+ toSafeString(article.getInfo())
					+ "', '"
					+ toSafeString(article.getAvatar()) + "')";

			db.execSQL(sql);
		}
	}

}
