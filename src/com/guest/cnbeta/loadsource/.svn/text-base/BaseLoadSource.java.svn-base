package com.guest.cnbeta.loadsource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public abstract class BaseLoadSource implements LoadSourceInterface {

	public String sessionId;

	public String getUrlHTML(URL url) throws IOException {
		return getUrlHTML(url, "UTF-8");
	}

	public static String getUrlHTML(URL url, String charset) throws IOException {
		BufferedReader reader;
		reader = new BufferedReader(new InputStreamReader(url.openStream(),
				charset));
		String line;
		StringBuffer result = new StringBuffer();
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}

	public Bitmap getImage(URL url) throws IOException {
		
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoInput(true);
			conn.connect();
			// int length = conn.getContentLength();
			InputStream is = conn.getInputStream();
			Bitmap img = BitmapFactory.decodeStream(is);
			String cookieValue = conn.getHeaderField("Set-Cookie");
			sessionId = cookieValue.substring(0, cookieValue.indexOf(";"));
			return img;
		} catch (Exception e) {
			// TODO: handle exception
		}

		return null;
	}

	public String replaceHTMLJs(String str) {
		str = str.replace("</p>", "\n");
//		str = str.replace("<br />", "\n");
		Pattern p = Pattern.compile("(?s)<[/!]*?[^<>]*?>");
		Matcher m = p.matcher(str);
		str = m.replaceAll("");
		str = str.replace("&nbsp;", " ");
		str = str.replace("&rdquo;", "”");
		str = str.replace("&quot;", "''");
		str = str.replace("&ldquo;", "“");
		str = str.replace("&mdash;", "—");
		str = str.replace("&lsquo;", "‘");
		str = str.replace("&rsquo;", "’");
		str = str.replace("&middot;", "·");
		str = str.replace("&amp;", "&");
		
		if(str.contains("的投递")){
			str = str.split("的投递")[1];
		}
		
		return str;
	}

}
