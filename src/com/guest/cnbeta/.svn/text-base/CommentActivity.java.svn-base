package com.guest.cnbeta;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guest.cnbeta.loadsource.LoadSource;
import com.guest.cnbeta.loadsource.LoadSourceInterface;
import com.guest.cnbeta.module.Article;
import com.guest.cnbeta.module.Comment;
import com.guest.cnbeta.service.CommentService;
import com.guest.cnbeta.util.Util;
import com.mobclick.android.MobclickAgent;

public class CommentActivity extends Activity {
	private CommentService cbCommentService;
	private Intent intent;
	private Bundle bl;
	private Article article;
	private List<Comment> commentList;
	private ListView listView;
	private Boolean isLikeVoted = false;
	private Boolean isDisLikeVoted = false;
	private CommentListAdapter listAdapter;
	private Button postBT;
	private Bitmap safeCode;
	private ImageView imgAddSafeCode;
	private ProgressBar progressBar;
	private LoadSourceInterface loader = new LoadSource();

	Comment currentComment = new Comment();
	LoadSourceInterface loadSource = new LoadSource();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		cbCommentService = new CommentService(this);
		setContentView(R.layout.list_comment);
		intent = this.getIntent();
		bl = intent.getExtras();
		article = (Article) bl.getSerializable("article");
		this.setTitle(getString(R.string.comment) + article.getTitle());
		
		AnimationSet set = new AnimationSet(true);

		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(50);
		set.addAnimation(animation);

		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(250);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(
				set, 0.5f);

		View footerView = getLayoutInflater().inflate(
				R.layout.list_comment_footer, null);
		
		listAdapter = new CommentListAdapter(this);
		syncComment();
		
		progressBar = (ProgressBar)footerView.findViewById(R.id.safeCodePB);

		listView = (ListView) findViewById(R.id.LstComment);
		listView.setLayoutAnimation(controller);
		listView.addFooterView(footerView);
		
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

	public class CommentListAdapter extends BaseAdapter {

		ViewHolder holder;

		int itemID;

		int voteId;

		int[] likeVoteNum = new int[100];

		int[] disLikeVoteNum = new int[100];

		private LayoutInflater mInflater;

		public CommentListAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return commentList.size();
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

				convertView = mInflater.inflate(R.layout.item_comment, null);
				holder = new ViewHolder();

				holder.authorTV = (TextView) convertView
						.findViewById(R.id.authorTV);
				holder.contentTV = (TextView) convertView
						.findViewById(R.id.contentTV);

				holder.likeView = (View) convertView.findViewById(R.id.like);

				holder.dislikeView = (View) convertView
						.findViewById(R.id.dislike);

				holder.likeTV = (TextView) convertView
						.findViewById(R.id.likeTV);
				holder.dislikeTV = (TextView) convertView
						.findViewById(R.id.dislikeTV);

				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			currentComment = commentList.get(id);

			holder.authorTV.setText(currentComment.getAuthor() + "发表于"
					+ currentComment.getDate().toLocaleString());
			holder.contentTV.setText(currentComment.getComment());

			int likeNumber = currentComment.getLike();
			int disLikeNumber = currentComment.getDislike();

			if (!isLikeVoted) {
				holder.likeTV.setText(Integer.toString(likeNumber));
				likeVoteNum[id] = likeNumber;
			} else {
				holder.likeTV.setText(Integer.toString(likeVoteNum[id]));
			}

			if (!isDisLikeVoted) {
				holder.dislikeTV.setText(Integer.toString(disLikeNumber));
				disLikeVoteNum[id] = disLikeNumber;
			} else {
				holder.dislikeTV.setText(Integer.toString(disLikeVoteNum[id]));
			}

			holder.likeView.setOnClickListener(new ItemListener(id, likeNumber,
					currentComment));
			holder.dislikeView.setOnClickListener(new ItemListener(id,
					disLikeNumber, currentComment));

			return convertView;
		}

		private class ViewHolder {

			View likeView;

			View dislikeView;

			TextView likeTV;

			TextView dislikeTV;

			TextView authorTV;

			TextView contentTV;
		}

		class ItemListener implements OnClickListener {

			private int mNumber;

			private int mPosition;

			private Comment mComment;

			ItemListener(int id, int number, Comment comment) {
				mPosition = id;
				mNumber = number;
				mComment = comment;
			}

			@Override
			public void onClick(View v) {
				int vid = v.getId();
				if (vid == holder.likeView.getId()) {
					Toast.makeText(CommentActivity.this, "这个可以支持下",
							Toast.LENGTH_SHORT).show();
					
					isLikeVoted = true;
					likeVoteNum[mPosition] = mNumber + 1;

					try {
						if (loadSource.supportComment(mComment)) {
						} else {
							Toast.makeText(CommentActivity.this, "赞过了",
									Toast.LENGTH_SHORT).show();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
				if (vid == holder.dislikeView.getId()) {
					
					Toast.makeText(CommentActivity.this, "这个完全扯淡啊",
							Toast.LENGTH_SHORT).show();
					
					isDisLikeVoted = true;
					disLikeVoteNum[mPosition] = mNumber + 1;

					try {
						if (loadSource.againstComment(mComment)) {
						} else {
							Toast.makeText(CommentActivity.this, "踩过了",
									Toast.LENGTH_SHORT).show();
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				listAdapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
			Intent intent = new Intent();
			intent.setClass(CommentActivity.this, ArticleActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("article", article);
			intent.putExtras(bundle);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
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

	public void syncComment() {
		new Thread() {
			@Override
			public void run() {
				if (reloadCommentList()) {
					handler.sendEmptyMessage(0);
				} else {
					handler.sendEmptyMessage(1);
				}
			}
		}.start();
	}

	public boolean reloadCommentList() {
		if (!Util.isNetworkAvailable(this)) {
			return false;
		}
		try {
			commentList = cbCommentService.getCommentList(article);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public void reloadListView() {
		listView.setAdapter(listAdapter);

		if (listAdapter.getCount() == 0) {
			Toast.makeText(this, "暂无评论", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setClass(CommentActivity.this, ArticleActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("article", article);
			intent.putExtras(bundle);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
			finish();
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 0) {
				reloadListView();
			} else if (msg.what == 1) {
				Toast.makeText(CommentActivity.this, "网络不给力啊",
						Toast.LENGTH_SHORT).show();
				Intent intent = new Intent();
				intent.setClass(CommentActivity.this, ArticleActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("article", article);
				intent.putExtras(bundle);
				startActivity(intent);
				overridePendingTransition(R.anim.slide_in_up,
						R.anim.slide_out_down);
				finish();
			} else if (msg.what == 2) {
				reloadImageView();
			}
		}
	};

	public void syncSafeCode() {
		imgAddSafeCode.setVisibility(View.INVISIBLE);
		progressBar.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				if (reloadSafeCode()) {
					handler.sendEmptyMessage(2);
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

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, 1, 0, "发布评论").setIcon(R.drawable.ic_menu_post);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			listView.setSelection(listView.getCount());
			break;
		}
		return true;
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
