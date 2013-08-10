package com.guest.cnbeta.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Looper;

public class NetworkStatusReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		if (networkInfo != null) {
			if (networkInfo.isConnected()
					&& networkInfo.getTypeName().equalsIgnoreCase("WIFI")) {
				if (!Util.threadStarted) {
					T.d("Wifi Connected");
					Thread thread = new Thread() {
						@Override
						public void run() {
							Looper.prepare();
//							ArticleListService cbListService = new ArticleListService(context);
//							cbListService.offLineDownload(context);
						}
					};
					thread.start();
					Util.threadStarted = true;
				}
			}
		}
	}
}