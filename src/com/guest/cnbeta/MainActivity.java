package com.guest.cnbeta;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.feedback.NotificationType;
import com.feedback.UMFeedbackService;
import com.guest.cnbeta.module.Article;
import com.guest.cnbeta.service.ArticleListService;
import com.guest.cnbeta.service.AvatarListService;
import com.guest.cnbeta.util.BaseApplication;
import com.guest.cnbeta.util.DataEngine;
import com.guest.cnbeta.util.PullToRefreshListView;
import com.guest.cnbeta.util.PullToRefreshListView.OnRefreshListener;
import com.guest.cnbeta.util.ScaleAnimationHelper;
import com.guest.cnbeta.util.T;
import com.guest.cnbeta.util.Util;
import com.mobclick.android.MobclickAgent;

public class MainActivity extends ListActivity implements OnItemClickListener {
	private ArticleListService articleListService;
	private AvatarListService avatarListService;
	public static List<Article> articleList;
	private ArticleListAdapter listAdapter;
	String[] avatarList = new String[500];
	private ImageView splashImageView;
	private Dialog dialog;
	private LinearLayout layout;
	private Animation fadeAnim;
	private boolean showHeader = false;
	private boolean showFooter = false;
	private boolean isListEnd = false;
	private boolean isRefreshing = false;
	private boolean isLoadingMore = false;
	private String url;
	private LoadMoreAsyncTask loadMoreAsyncTask;
	private LinearLayout loadingLayout;
	private int moreCount;
	private int avatarCount = 0;
	private View footView;
	private BaseApplication baseApplication;
	private static final int OFFLINE_DOWNLOAD = 1;
	private static final int LOAD_AVATAR = 2;
	public DataEngine dataEngine;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.list);

		baseApplication = new BaseApplication();
		dataEngine = new DataEngine(this);

		fadeAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fade);

		splashImageView = (ImageView) findViewById(R.id.splashIV);
		articleListService = new ArticleListService(MainActivity.this);
		avatarListService = new AvatarListService(MainActivity.this);
		getListView().setOnItemClickListener(MainActivity.this);

		syncArticle();

		init();

		// // Set a listener to be invoked when the list should be refreshed.
		((PullToRefreshListView) getListView())
				.setOnRefreshListener(new OnRefreshListener() {

					@Override
					public void onRefresh() {
						// Do work to refresh the list here.
						isRefreshing = true;
						syncArticle();
					}

					@Override
					public void onLoadMore() {

						if (!isLoadingMore) {
							moreCount += 20;
							loadMoreAsyncTask = (LoadMoreAsyncTask) new LoadMoreAsyncTask();
							loadMoreAsyncTask.execute(String.valueOf(moreCount));
						}
					}
				});

		footView = getLayoutInflater().inflate(R.layout.list_footer_loading,
				null);
		loadingLayout = new LinearLayout(MainActivity.this);
		loadingLayout.addView(footView, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT));
		loadingLayout.setGravity(Gravity.CENTER);

		getListView().setHeaderDividersEnabled(true);

		if ("off"
				.equalsIgnoreCase(MobclickAgent.getConfigParams(this, "domob"))) {
			dataEngine.setIsDisplayAd(false);
		} else {
			dataEngine.setIsDisplayAd(true);
		}
	}

	private void init() {
		// TODO Auto-generated method stub
		MobclickAgent.onError(this);
		MobclickAgent.setUpdateOnlyWifi(false);
		MobclickAgent.update(this);
		UMFeedbackService.enableNewReplyNotification(this,
				NotificationType.AlertDialog);
		MobclickAgent.updateOnlineConfig(this);

		String header = MobclickAgent.getConfigParams(this, "header");
		if ("".equals(header)) {
			return;
		} else {
			if (header.equals("on")) {
				url = MobclickAgent.getConfigParams(this, "url");
				showHeader = true;
			} else {
				showHeader = false;
			}

		}

		String footer = MobclickAgent.getConfigParams(this, "footer");
		if ("".equals(footer)) {
			return;
		} else {
			if (footer.equals("on")) {
				url = MobclickAgent.getConfigParams(this, "url");
				showFooter = true;
			} else {
				showFooter = false;
			}
		}
	}

	public boolean syncArticleContent() {
		if (!Util.isNetworkAvailable(this)) {
			return false;
		}
		articleListService.offLineDownload(MainActivity.this, articleList);
		return true;

	}

	public void loadListView() {

		if (showHeader) {
			WebView webView = new WebView(MainActivity.this);
			webView.loadUrl(url);
			getListView().addHeaderView(webView);
		}

		if (showFooter) {
			WebView webView = new WebView(MainActivity.this);
			webView.loadUrl(url);
			getListView().addFooterView(webView);
		}

		listAdapter = new ArticleListAdapter(this);
		getListView().setAdapter(listAdapter);

		loadAvatar(false);
	}

	public void syncArticle() {

		if (isRefreshing) {

			LayoutInflater inflater = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			layout = (LinearLayout) inflater.inflate(R.layout.dialog_refresh,
					null);

			ScaleAnimationHelper scaleHelper = new ScaleAnimationHelper(this, 0);
			scaleHelper.ScaleOutAnimation(layout);

			dialog = new Dialog(this, R.style.dialog);
			dialog.setContentView(layout);
			dialog.show();

		}

		new Thread() {
			@Override
			public void run() {
				try {
					if (Util.isNetworkAvailable(MainActivity.this)) {
						articleList = articleListService
								.getArticleListFromWeb();
					} else {
						articleList = articleListService.getArticleListFromDB();
						T.a(articleList.size());
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
			}
		}.start();
	}

	public void loadAvatar(boolean isLoadMore) {

		if (isLoadMore) {
			avatarCount = avatarCount - 1;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (articleList != null) {
					if (avatarCount < articleList.size()) {
						avatarList[avatarCount] = avatarListService
								.getAvatarImg(articleList.get(avatarCount));
						handler.sendEmptyMessage(LOAD_AVATAR);
					}
				}
			}
		}).start();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {

			case OFFLINE_DOWNLOAD:

				Intent intent = new Intent();
				intent.setClass(MainActivity.this, MainActivity.class);
				NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				PendingIntent contentIntent = PendingIntent.getActivity(
						MainActivity.this, 0, intent, 0);
				Notification n = new Notification(R.drawable.icon_dialog,
						"cnBeta离线下载已完成", System.currentTimeMillis());
				n.setLatestEventInfo(MainActivity.this, "cnBeta离线下载已完成",
						"让新闻飞一会~", contentIntent);
				n.flags = Notification.FLAG_AUTO_CANCEL;
				n.vibrate = new long[] { 0, 200 };
				nm.notify(0, n);
				finish();
				break;

			case LOAD_AVATAR:

				avatarCount = avatarCount + 1;
				if (avatarCount < articleList.size()) {
					loadAvatar(false);
					listAdapter.notifyDataSetChanged();
				}

				break;

			default:

				loadListView();

				if (isRefreshing) {
					((PullToRefreshListView) getListView()).onRefreshComplete();
					isListEnd = false;
					ScaleAnimationHelper scaleHelper = new ScaleAnimationHelper(
							MainActivity.this, 0);
					scaleHelper.ScaleInAnimation(layout);
					layout.startAnimation(fadeAnim);
					dialog.dismiss();
				} else {
					splashImageView.startAnimation(fadeAnim);
					splashImageView.setVisibility(View.GONE);
					LinearLayout splashLayout = (LinearLayout) findViewById(R.id.splashLL);
					splashLayout.startAnimation(fadeAnim);
					splashLayout.setVisibility(View.GONE);

					makeCacheDir();

					listAdapter.notifyDataSetChanged();
				}

				if (!Util.isNetworkAvailable(MainActivity.this)) {
					Toast.makeText(getApplicationContext(),
							getString(R.string.nointernet), Toast.LENGTH_SHORT)
							.show();
				} else {
					getListView().addFooterView(loadingLayout);
				}

				break;
			}
		}
	};

	public class ArticleListAdapter extends BaseAdapter {

		ViewHolder holder;

		private LayoutInflater mInflater;

		public ArticleListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {

			int size = 0;

			if (articleList != null) {
				size = articleList.size();
			}
			return size;
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public View getView(int id, View convertView, ViewGroup parent) {

			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.item_article, null);
				holder = new ViewHolder();
				holder.avatarIV = (ImageView) convertView
						.findViewById(R.id.avatarIV);
				holder.titleTV = (TextView) convertView
						.findViewById(R.id.titleTV);
				holder.infoTV = (TextView) convertView
						.findViewById(R.id.infoTV);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Article article = articleList.get(id);
			String imgPathString = "/sdcard/.cnBeta/" + avatarList[id] + ".gif";

			File file = new File(imgPathString);
			if (file.exists()) {
				holder.avatarIV.setImageBitmap(BitmapFactory
						.decodeFile(imgPathString));
			} else {
				holder.avatarIV.setImageDrawable(getResources().getDrawable(
						R.drawable.icon));
			}

			holder.titleTV.setText(article.getTitle());
			holder.infoTV.setText(article.getInfo());

			return convertView;
		}

		private class ViewHolder {

			ImageView avatarIV;

			TextView titleTV;

			TextView infoTV;

		}
	}

	class LoadMoreAsyncTask extends AsyncTask<String, Integer, String> {

		int length = 0;

		protected String doInBackground(String... rn) {

			isLoadingMore = true;

			if (Util.isNetworkAvailable(MainActivity.this)) {
				int page = baseApplication.getLoadPage();
				baseApplication.setLoadPage(page);
				List<Article> list = articleListService.getArticleList(page);
				for (Article article : list) {
					articleList.add(article);
				}
				length = list.size();
			} else {
				isListEnd = true;
			}

			if (length == 0) {
				isListEnd = true;
			}

			return null;
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		protected void onPostExecute(String RD) {

			if (length != 0) {
				listAdapter.notifyDataSetChanged();
			}

			T.d("onLoadMoreExcute");
			if (isListEnd) {
				getListView().removeFooterView(loadingLayout);
			} else {
				loadAvatar(true);
			}

			isLoadingMore = false;
		}

		@Override
		protected void onPreExecute() {
		}
	}

	private void makeCacheDir() {
		File myFilePath = new File("/sdcard/.cnBeta");
		try {
			if (!Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				new AlertDialog.Builder(MainActivity.this)
						.setMessage("由于您的手机没有安装SD内存卡，新闻图标将无法显示。")
						.setPositiveButton(android.R.string.ok,
								new AlertDialog.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.dismiss();
									}
								}).create().show();
				return;
			}
			if (myFilePath.isDirectory()) {
			} else {
				myFilePath.mkdir();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "刷新列表").setIcon(R.drawable.ic_menu_refresh);
		menu.add(0, 2, 0, "离线下载").setIcon(R.drawable.ic_menu_download);
		// menu.add(0, 3, 0, "清理缓存").setIcon(R.drawable.ic_menu_clean);
		menu.add(0, 3, 0, "夜间开关").setIcon(R.drawable.ic_menu_bright);
		menu.add(0, 4, 0, "意见反馈").setIcon(R.drawable.ic_menu_feedback);
		// menu.add(0, 4, 0, "系统设置").setIcon(R.drawable.ic_menu_feedback);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			isRefreshing = true;
			syncArticle();
			break;

		case 2:
			offLineDownload();
			break;

		// case 3:
		// Toast.makeText(getApplicationContext(), "已经洗白白了~",
		// Toast.LENGTH_SHORT).show();
		// break;

		case 3:
//			Settings.System.putInt(getApplicationContext().getContentResolver(),
//					Settings.System.SCREEN_BRIGHTNESS_MODE,
//					Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
//			WindowManager.LayoutParams lp = getWindow().getAttributes();
//			lp.screenBrightness = Float.valueOf(30) * (1f / 255f);
//			getWindow().setAttributes(lp);
//
//			Uri uri = android.provider.Settings.System
//					.getUriFor("screen_brightness");
//			android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
//					"screen_brightness", 30);
//			getApplicationContext().getContentResolver().notifyChange(uri, null);
			
			if (dataEngine.getNightMode()) {
				setBrightness(118);
				dataEngine.setNightMode(false);
			}else {
				setBrightness(20);
				dataEngine.setNightMode(true);
			}

			break;

		case 4:
			UMFeedbackService.openUmengFeedbackSDK(this);
			break;

		// case 4:
		// startActivity(new Intent().setClass(this, SettingsActivity.class));
		// break;

		}
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		int id = arg2 - 1;

		try {
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, ArticleActivity.class);
			Bundle bl = new Bundle();
			bl.putSerializable("article", articleList.get(id));
			bl.putInt("index", id);
			intent.putExtras(bl);
			// overridePendingTransition(R.anim.slide_in_right,
			// R.anim.slide_out_left);
			startActivity(intent);
		} catch (Exception e) {
			// TODO: handle exception
			T.e(e);
		}

	}

	public void offLineDownload() {

		final Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Looper.prepare();
				if (syncArticleContent()) {
					handler.sendEmptyMessage(OFFLINE_DOWNLOAD);
				}
			}
		});

		if (Util.isWifiConnected(this)) {
			Toast.makeText(MainActivity.this, "开始啦", Toast.LENGTH_LONG).show();
			thread.start();
		} else {
			AlertDialog.Builder mDialog = new AlertDialog.Builder(this);
			mDialog.setMessage("Wifi未连接。是否使用手机流量进行下载？")
					.setIcon(R.drawable.icon_dialog).setTitle("cnBeta")
					.setPositiveButton("继续", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							thread.start();
						}
					}).setNegativeButton("算了", new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					}).show();
		}
	}
	
	public void setBrightness(int level) {
		Settings.System.putInt(getApplicationContext().getContentResolver(),
				Settings.System.SCREEN_BRIGHTNESS_MODE,
				Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = Float.valueOf(level) * (1f / 255f);
		getWindow().setAttributes(lp);
		Uri uri = android.provider.Settings.System
				.getUriFor("screen_brightness");
		android.provider.Settings.System.putInt(getApplicationContext().getContentResolver(),
				"screen_brightness", level);
		getApplicationContext().getContentResolver().notifyChange(uri, null);
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(MainActivity.this)
					.setIcon(R.drawable.icon_dialog).setTitle("cnBeta")
					.setMessage("要走啦？")
					.setPositiveButton("是啊", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					})
					.setNegativeButton("不嘛", new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
						}
					}).show();
		}

		return false;
	}

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
}