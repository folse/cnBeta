package com.guest.cnbeta;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.guest.cnbeta.loadsource.LoadSource;
import com.guest.cnbeta.module.Article;
import com.guest.cnbeta.util.Util;
import com.mobclick.android.MobclickAgent;

public class PostCommentActivity extends Activity {
	private Intent intent;
	private Bundle bl;
	private Article article;
	private Bitmap safeCode;
	private LoadSource loader = new LoadSource();
	private Button postBT;
	private ImageView imgAddSafeCode;
	private ProgressBar progressBar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.post_comment);
		intent = this.getIntent();
		bl = intent.getExtras();
		article = (Article) bl.getSerializable("article");
		
		progressBar = (ProgressBar)findViewById(R.id.safeCodePB);

		postBT = (Button) findViewById(R.id.BtnAddPost);
		postBT.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					postBT.setBackgroundResource(R.drawable.btn_pressed);
					break;

				case MotionEvent.ACTION_UP:
					postBT.setBackgroundResource(R.drawable.btn);
					syncPostComment();
					break;

				case MotionEvent.ACTION_MOVE:
					postBT.setBackgroundResource(R.drawable.btn);

				default:
					break;
				}

				return false;
			}
		});

		imgAddSafeCode = (ImageView) findViewById(R.id.ImgAddSafeCode);
		imgAddSafeCode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				syncSafeCode();
			}
		});

		syncSafeCode();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			finish();
			return true;
		default:
			return super.onKeyDown(keyCode, event);
		}
	}

	public void syncPostComment() {
		progressBar.setVisibility(View.VISIBLE);
		imgAddSafeCode.setVisibility(View.INVISIBLE);
		
		new Thread() {
			@Override
			public void run() {
				String result = reloadPostComment();
				int msgid = Integer.parseInt(result.substring(0, 1));
				handler_pc.sendEmptyMessage(msgid);
			}
		}.start();
	}

	public void syncSafeCode() {
		imgAddSafeCode.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				if (reloadSafeCode()) {
					handler.sendEmptyMessage(0);
				} else {
					handler.sendEmptyMessage(1);
				}
			}
		}.start();
	}

	public String reloadPostComment() {
		String result = "";
		if (!Util.isNetworkAvailable(this)) {
			return result;
		}
		EditText edtAddComment0 = (EditText) findViewById(R.id.EdtAddComment0);
		EditText edtAddValimg = (EditText) findViewById(R.id.EdtAddValimg);
		try {
			result = loader.postComments(article,
					String.valueOf(edtAddComment0.getText()),
					String.valueOf(edtAddValimg.getText()));
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			return result;
		}
	}

	public boolean reloadSafeCode() {
		if (!Util.isNetworkAvailable(this)) {
			return false;
		}
		try {
			safeCode = loader.getSafeCode();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void reloadImageView() {
		progressBar.setVisibility(View.INVISIBLE);
		imgAddSafeCode.setVisibility(View.VISIBLE);
		imgAddSafeCode.setImageBitmap(safeCode);
	}

	private Handler handler_pc = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			
			progressBar.setVisibility(View.INVISIBLE);
			imgAddSafeCode.setVisibility(View.VISIBLE);
			
			switch (msg.what) {
			case 0:
				Toast.makeText(getApplicationContext(), "您要评论的新闻不存在",
						Toast.LENGTH_SHORT).show();
				break;
			case 1:
				Toast.makeText(getApplicationContext(), "验证码不正确",
						Toast.LENGTH_SHORT).show();
				break;
			case 2:
				Toast.makeText(getApplicationContext(), "30秒内不允许再次评论",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(getApplicationContext(), "请填写评论后再提交",
						Toast.LENGTH_SHORT).show();
				break;
			case 4:
				Toast.makeText(getApplicationContext(), "评论字数超过限制",
						Toast.LENGTH_SHORT).show();
				break;
			case 5:
				Toast.makeText(getApplicationContext(), "0.5元已到帐，请查收",
						Toast.LENGTH_SHORT).show();
				finish();
				break;
			case 6:
				Toast.makeText(getApplicationContext(), "CBFW检测到您输入了不适当字词，请纠正",
						Toast.LENGTH_SHORT).show();
				break;
			case 7:
				Toast.makeText(getApplicationContext(), "您的评论需审核后才能显示",
						Toast.LENGTH_SHORT).show();
				break;
			case 8:
				Toast.makeText(getApplicationContext(), "由于种种原因，这条新闻不开放评论，请见谅",
						Toast.LENGTH_SHORT).show();
				break;
			case 9:
				Toast.makeText(getApplicationContext(), "评论系统维护中",
						Toast.LENGTH_SHORT).show();
				break;
			default:
				Toast.makeText(getApplicationContext(), "未知错误",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				reloadImageView();
			}
		}
	};
	
	public void onResume() {
		super.onResume();
		 MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		 MobclickAgent.onPause(this);
	}
}
