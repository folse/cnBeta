package com.guest.cnbeta.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;

import com.guest.cnbeta.module.Article;
import com.guest.cnbeta.util.T;

public class AvatarListService {

	public AvatarListService(Context context) {
	}

	public String getAvatarImg(Article article) {

		String imgString = "cb.gif";
		imgString = article.getAvatar();
		imgString = imgString.replace("\"", "");
		imgString = imgString.replace(" ", "%20");

		downloadAvatar(imgString);
		
		T.d(imgString);

		return imgString;

	}

	public static void downloadAvatar(String avatarImgString) {
		try {

			File file = new File("/sdcard/.cnBeta/" + avatarImgString + ".gif");
			if (!file.exists()) {
				URL url = new URL("http://img.cnbeta.com/topics/"
						+ avatarImgString);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.setDoInput(true);
				conn.connect();
				InputStream is = conn.getInputStream();
				FileOutputStream fos = new FileOutputStream(file);
				byte buf[] = new byte[128];
				do {
					int numread = is.read(buf);
					if (numread <= 0) {
						break;
					}
					fos.write(buf, 0, numread);
				} while (true);
				is.close();
			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
