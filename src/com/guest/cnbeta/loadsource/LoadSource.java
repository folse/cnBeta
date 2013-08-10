package com.guest.cnbeta.loadsource;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.graphics.Bitmap;

import com.guest.cnbeta.module.Article;
import com.guest.cnbeta.module.Comment;
import com.guest.cnbeta.util.T;

public class LoadSource extends BaseLoadSource {

	List<Article> articleList;

	@Override
	public Article getArticleContent(Article article) throws IOException {

		try {
			String html = getUrlHTML(new URL("http://www.cnbeta.com/articles/"
					+ Integer.toString(article.getId()) + ".htm"), "GB2312");
			String htmlContent = html.substring(
					html.indexOf("<div id=\"news_content\">"),
					html.indexOf("<div class=\"digbox\">"));
			String contentString = htmlContent.replace(
					"<div id=\"news_content\">", "");
			article.setContent("<body style=\"color:#686868;font-size:19px;line-height:150%;\">"
					+ contentString + "</body>");
			article.setContented(true);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}

		return article;
	}

	@Override
	public List<Comment> getComments(Article article) throws IOException {
		List<Comment> list = new ArrayList<Comment>();
		URL url = new URL("http://www.cnbeta.com/comment/normal/"
				+ String.valueOf(article.getId()) + ".html");
		String html = getUrlHTML(url);
		String pattern = "(?s)<dt class=\"re_author\"><span><strong>第(\\w*?)楼</strong>\\s*?(.*?)\\s*?发表于\\s*?(.*?)\\s*?</span></dt>.*?<dd class=\"re_detail\">\\s*?(.*?)</dd>.*?ShowReply\\((\\w*?),.*?<span id=\"support\\w*?\">(\\w*?)</span>.*?<span id=\"against\\w*?\">(\\w*?)<";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(html);
		while (m.find()) {
			Comment comment = new Comment();
			comment.setNumber(Integer.parseInt(m.group(1)));
			comment.setAuthor(replaceHTMLJs(m.group(2)));
			SimpleDateFormat dateFormat = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			try {
				comment.setDate(dateFormat.parse(m.group(3)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			comment.setComment(m.group(4).trim().replace("<br>", "\n"));
			comment.setId(Integer.parseInt(m.group(5)));
			comment.setLike(Integer.parseInt(m.group(6)));
			comment.setDislike(Integer.parseInt(m.group(7)));
			list.add(comment);
		}
		return list;
	}

	@Override
	public Bitmap getSafeCode() throws IOException {
		URL url = new URL("http://www.cnbeta.com/validate1.php");
		return this.getImage(url);
	}

	@Override
	public String postComments(Article article, String comment,
			String valimg_main) throws IOException {
		URL url = new URL("http://www.cnbeta.com/Ajax.comment.php?ver=new");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoInput(true);
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Cookie", sessionId);
		conn.setUseCaches(false);
		conn.setInstanceFollowRedirects(true);
		conn.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		DataOutputStream out = new DataOutputStream(conn.getOutputStream());
		String params = "comment=" + URLEncoder.encode(comment, "gb2312")
				+ "&sid=" + article.getId() + "&tid=0" + "&valimg_main="
				+ URLEncoder.encode(valimg_main, "gb2312");
		out.writeBytes(params);
		out.flush();
		out.close();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String inputLine = null;
		String resultData = "";
		while (((inputLine = reader.readLine()) != null)) {
			resultData += inputLine + "\n";
		}
		reader.close();
		conn.disconnect();

		return resultData;
	}

	@Override
	public boolean supportComment(Comment comment) throws IOException {
		URL url = new URL("http://www.cnbeta.com/Ajax.vote.php?tid="
				+ comment.getId() + "&support=1");
		String result = getUrlHTML(url);
		if (result.substring(0, 1).equals("0")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean againstComment(Comment comment) throws IOException {

		T.d(Integer.toString(comment.getId()));

		URL url = new URL("http://www.cnbeta.com/Ajax.vote.php?tid="
				+ comment.getId() + "&against=1");
		String result = getUrlHTML(url);
		if (result.substring(0, 1).equals("0")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<Article> getArticleListFromWeb() throws MalformedURLException,
			IOException {
		String webContent = getUrlHTML(new URL("http://www.cnbeta.com/index.php"), "GB2312");
		T.d(webContent);
		
//		StringBuffer response = new StringBuffer();
//		FileReader fr = new FileReader("j:/n.txt");
//		BufferedReader br = new BufferedReader(fr);
//		String myreadline;
//		while (br.ready()) {
//			myreadline = br.readLine();
//			response.append(myreadline);
//		}
//		String webContent = response.toString();

		articleList = new ArrayList<Article>();

		Document doc = Jsoup.parse(webContent);
		Elements divs = doc.select("div.newslist");
		if (divs != null) {
			for (Element div : divs) {

				String id = div.select("a").attr("href").substring(10, 16);
				Element dl = div.child(0);
				String title = dl.child(0).child(0).child(0).html();
				String avatar = dl.child(2).child(0).select("img").attr("src")
						.substring(29);
				String info = replaceHTMLJs(dl.child(2).child(1).html());
				
				T.d(id);

				Article article = new Article();
				article.setId(Integer.parseInt(id));
				article.setTitle(replaceHTMLJs(title));
				article.setInfo(replaceHTMLJs(info));
				article.setAvatar(replaceHTMLJs(avatar));
				articleList.add(article);
			}
		}

		return articleList;
	}

	@Override
	public List<Article> getMoreArticleListFromWeb(int page)
			throws MalformedURLException, IOException {

		String retContent = null;

		try {
			URL myURL = new URL("http://www.cnbeta.com/newread.php?page="
					+ Integer.toString(page));
			HttpURLConnection httpConn = (HttpURLConnection) myURL
					.openConnection();
			httpConn.setRequestProperty("Referer", "http://www.cnbeta.com/");
			System.setProperty("http.keepAlive", "false");
			int statusCode = httpConn.getResponseCode();
			InputStream is = httpConn.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "GB2312"));
			String line;
			StringBuffer result = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			retContent = result.toString();
			T.d("HTTP Status OK: " + Integer.toString(statusCode));
			T.d("Load Content " + retContent);

		} catch (Exception e) {
			T.e(e);
		}

		Article article;
		articleList = new ArrayList<Article>();
		
		Document doc = Jsoup.parse(retContent);
		Elements divs = doc.select("div.newslist");
		if (divs != null) {
			for (Element div : divs) {

				String id = div.select("a").attr("href").substring(10, 16);
				Element dl = div.child(0);
				String title = replaceHTMLJs(dl.child(0).child(0).child(0)
						.html());
				String avatar = replaceHTMLJs(dl.child(2).child(0)
						.select("img").attr("src").substring(29));
				String info = replaceHTMLJs(dl.child(2).child(1).html());

				article = new Article();
				article.setId(Integer.parseInt(id));
				article.setTitle(title);
				article.setAvatar(avatar);
				article.setInfo(info);
				articleList.add(article);
			}
		}
		return articleList;
	}

}
