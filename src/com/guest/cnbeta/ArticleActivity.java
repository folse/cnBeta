package com.guest.cnbeta;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.renderscript.Element;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.domob.android.ads.DomobAdListener;
import cn.domob.android.ads.DomobAdView;

import com.guest.cnbeta.module.Article;
import com.guest.cnbeta.service.ArticleService;
import com.guest.cnbeta.util.DataEngine;
import com.guest.cnbeta.util.T;
import com.mobclick.android.MobclickAgent;

public class ArticleActivity extends Activity {
	private ArticleService cbArticleService;
	private Intent intent;
	private int id;
	private Bundle bl;
	private Article article;
	private WebView webView;
	private WebSettings settings;
	private static final int SYNC_PAGE = 1;
	private static final int CLICK_ON_URL = 2;
	private static final int CLICK_ON_WEBVIEW = 3;
	public DataEngine dataEngine;
	private RelativeLayout footerLayout;
	private int fontSize = 3;
	private Button smallerButton;
	private Button largerButton;
	private TableLayout fontSizeLayout;

	RelativeLayout mAdContainer;
	DomobAdView mAdview320x50;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.article);

		dataEngine = new DataEngine(this);

		cbArticleService = new ArticleService(this);
		intent = this.getIntent();
		bl = intent.getExtras();
		id = bl.getInt("index");
		article = (Article) bl.getSerializable("article");

		webView = (WebView) findViewById(R.id.WebArtContent);
		WebViewClient client = new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				handler.sendEmptyMessage(CLICK_ON_URL);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				try {
					Thread.sleep(100);
					footerLayout = (RelativeLayout) findViewById(R.id.footer);
					footerLayout.setVisibility(View.VISIBLE);
					footerLayout.setOnTouchListener(new OnTouchListener() {
						@Override
						public boolean onTouch(View v, MotionEvent event) {
							// TODO Auto-generated method stub
							switch (event.getAction()) {
							case MotionEvent.ACTION_DOWN:
								footerLayout
										.setBackgroundDrawable(getResources()
												.getDrawable(
														R.drawable.item_pressed));
								break;

							case MotionEvent.ACTION_UP:
								footerLayout.setBackgroundColor(Color.WHITE);
								Intent intent = new Intent();
								intent.setClass(ArticleActivity.this,
										CommentActivity.class);
								Bundle bundle = new Bundle();
								bundle.putSerializable("article", article);
								intent.putExtras(bundle);
								startActivity(intent);
								overridePendingTransition(R.anim.slide_in_down,
										R.anim.slide_out_up);
								finish();
								break;

							default:
								break;
							}

							return true;
						}
					});
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		webView.setWebViewClient(client);
		settings = webView.getSettings();
		settings.setDefaultTextEncodingName("utf-8");
		settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
		settings.setLoadsImagesAutomatically(dataEngine.getIsLoadImg());
		settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		settings.setSupportZoom(true);
		if (settings.getTextSize() == WebSettings.TextSize.SMALLEST) {
			fontSize = 1;
		} else if (settings.getTextSize() == WebSettings.TextSize.SMALLER) {
			fontSize = 2;
		} else if (settings.getTextSize() == WebSettings.TextSize.NORMAL) {
			fontSize = 3;
		} else if (settings.getTextSize() == WebSettings.TextSize.LARGER) {
			fontSize = 4;
		} else if (settings.getTextSize() == WebSettings.TextSize.LARGEST) {
			fontSize = 5;
		}
		setFontSize(dataEngine.getFontSize());

		TextView textViewTitle = (TextView) findViewById(R.id.TxtArtTitle);
		textViewTitle.setText(article.getTitle());

		loadContent();

		if (dataEngine.getIsDisplayAd()) {

			mAdContainer = (RelativeLayout) findViewById(R.id.adcontainer);
			// 创建一个320x50的广告View
			mAdview320x50 = new DomobAdView(this, "56OJz49IuMp7FSmoaO",
					DomobAdView.INLINE_SIZE_320X50);
			mAdview320x50.setKeyword("news");
			mAdview320x50.setUserGender("male");

			// 设置广告view的监听器。
			mAdview320x50.setOnAdListener(new DomobAdListener() {
				@Override
				public void onReceivedFreshAd(DomobAdView adview) {
					Log.i("DomobSDKDemo", "onReceivedFreshAd");
				}

				@Override
				public void onFailedToReceiveFreshAd(DomobAdView adview) {
					Log.i("DomobSDKDemo", "onFailedToReceiveFreshAd");
				}

				@Override
				public void onLandingPageOpening() {
					Log.i("DomobSDKDemo", "onLandingPageOpening");
				}

				@Override
				public void onLandingPageClose() {
					Log.i("DomobSDKDemo", "onLandingPageClose");
				}
			});
			// 将广告View增加到视图中。
			mAdContainer.addView(mAdview320x50);
		}
	}

	private void setFontSize(int fontSize) {

		switch (fontSize) {

		case 1:
			settings.setTextSize(WebSettings.TextSize.SMALLEST);
			break;
		case 2:
			settings.setTextSize(WebSettings.TextSize.SMALLER);
			break;
		case 3:
			settings.setTextSize(WebSettings.TextSize.NORMAL);
			break;
		case 4:
			settings.setTextSize(WebSettings.TextSize.LARGER);
			break;
		case 5:
			settings.setTextSize(WebSettings.TextSize.LARGEST);
			break;
		}

	}

	private void loadContent() {
		try {
			FileInputStream in = this.openFileInput("cache_art_"
					+ article.getId() + ".html");
			in.close();
			webView.loadUrl("file://" + this.getFilesDir() + "/cache_art_"
					+ article.getId() + ".html");
		} catch (FileNotFoundException e) {
			syncArticle();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void syncArticle() {
		setProgressBarIndeterminateVisibility(true);

		new Thread() {
			@Override
			public void run() {
				if (!article.isContented()) {
					try {
						article = cbArticleService.getMoreDetail(article);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				Message msg = new Message();
				msg.what = SYNC_PAGE;
				handler.sendMessage(msg);
			}
		}.start();
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "查看评论").setIcon(R.drawable.ic_menu_comment);
		menu.add(0, 2, 0, "发表吐槽").setIcon(R.drawable.ic_menu_post);
		menu.add(0, 3, 0, "字体大小").setIcon(R.drawable.ic_menu_font);
		menu.add(0, 4, 0, "分享文章").setIcon(R.drawable.ic_menu_share);
		menu.add(0, 5, 0, "夜间开关").setIcon(R.drawable.ic_menu_bright);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			Intent intent = new Intent();
			intent.setClass(ArticleActivity.this, CommentActivity.class);
			Bundle bl = new Bundle();
			bl.putSerializable("article", article);
			intent.putExtras(bl);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
			finish();
			break;

		case 2:
			Intent intent2 = new Intent();
			intent2.setClass(ArticleActivity.this, PostCommentActivity.class);
			Bundle bl2 = new Bundle();
			bl2.putSerializable("article", article);
			intent2.putExtras(bl2);
			startActivity(intent2);
			break;

		case 3:

			Builder dialog = new AlertDialog.Builder(ArticleActivity.this);
			LayoutInflater inflater = (LayoutInflater) ArticleActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout layout = (LinearLayout) inflater.inflate(
					R.layout.font_resizer, null);
			dialog.setView(layout);
			/**
			 * 缩小按钮
			 */
			smallerButton = (Button) layout.findViewById(R.id.smallerBtn);
			smallerButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					fontSize--;

					if (fontSize < 0) {
						fontSize = 1;
					}
					setFontSize(fontSize);
					loadContent();
					dataEngine.setFontSize(fontSize);
				}
			});

			/**
			 * 放大按钮
			 */
			largerButton = (Button) layout.findViewById(R.id.largerBtn);
			largerButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					fontSize++;

					if (fontSize > 5) {
						fontSize = 5;
					}
					setFontSize(fontSize);
					loadContent();
					dataEngine.setFontSize(fontSize);
				}
			});
			dialog.show();

			break;

		case 4:
			String htmlUrl = "http://www.cnbeta.com/articles/"
					+ Integer.toString(article.getId()) + ".htm";

			Intent intent3 = new Intent(Intent.ACTION_SEND);
			intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent3.putExtra(Intent.EXTRA_TEXT, htmlUrl + "  来自cnbeta离线阅读");
			intent3.putExtra(Intent.EXTRA_SUBJECT, "分享:" + article.getTitle());
			intent3.setType("text/plain");
			startActivity(Intent.createChooser(intent3, "请选择分享方式"));
			break;

		case 5:
			
			if (dataEngine.getNightMode()) {
				setBrightness(118);
				dataEngine.setNightMode(false);
			}else {
				setBrightness(20);
				dataEngine.setNightMode(true);
			}
			
			break;
		}
		return true;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {

			if (msg.what == CLICK_ON_URL) {
				handler.removeMessages(CLICK_ON_WEBVIEW);
			}

			if (msg.what == SYNC_PAGE) {
				// write to sdcard
				try {
					FileOutputStream out = ArticleActivity.this.openFileOutput(
							"cache_art_" + article.getId() + ".html",
							Context.MODE_PRIVATE);
					T.d(Integer.toString(article.getId()));
					out.write(article.getContent().getBytes());
					out.flush();
					out.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				ArticleActivity.this.webView.loadUrl("file://"
						+ ArticleActivity.this.getFilesDir() + "/cache_art_"
						+ article.getId() + ".html");
				setProgressBarIndeterminateVisibility(false);
			}

		}
	};

	public boolean onKeyDown(int keyCode, KeyEvent event) {

		boolean isOpenArticle = false;

		if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
			id = id - 1;
			if (id < 0) {
				id = 0;
				Toast.makeText(ArticleActivity.this, "已经到了世界的尽头",
						Toast.LENGTH_LONG).show();
			} else {
				openArticle();
			}

			isOpenArticle = true;

		} else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
			id = id + 1;
			openArticle();

			isOpenArticle = true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}

		return isOpenArticle;
	}

	public void openArticle() {
		try {
			Intent intent = new Intent();
			intent.setClass(ArticleActivity.this, ArticleActivity.class);
			Bundle bl = new Bundle();
			bl.putSerializable("article", MainActivity.articleList.get(id));
			bl.putInt("index", id);
			intent.putExtras(bl);
			overridePendingTransition(R.anim.slide_in_right,
					R.anim.slide_out_left);
			startActivity(intent);
			finish();
		} catch (Exception e) {
			// TODO: handle exception
			T.e(e);
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

	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
