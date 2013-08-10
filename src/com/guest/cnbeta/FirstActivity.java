package com.guest.cnbeta;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.guest.cnbeta.util.FlingGallery;
import com.mobclick.android.MobclickAgent;

public class FirstActivity extends Activity {

	private FlingGallery mGallery;

	private SharedPreferences info;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mGallery != null) {
			return mGallery.onGalleryTouchEvent(event);
		} else {
			return true;
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.first);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		info = getSharedPreferences("Data", 0);
		if (!info.getBoolean("isFirst", true)) {
			startActivity(new Intent().setClass(FirstActivity.this,
					MainActivity.class));
			finish();
		} else {

			mGallery = new FlingGallery(FirstActivity.this);
			mGallery.setAdapter(new GalleryAdapter(this));

			setContentView(mGallery);

			MobclickAgent.update(this);
		}
	}

	public class GalleryAdapter extends BaseAdapter {

		public GalleryAdapter(Context context) {
		}

		@Override
		public int getCount() {
			return 4;
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

			if (id == 3) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						try {
							Thread.sleep(300);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						handler.sendEmptyMessage(0);
					}
				}).start();
			}

			return new GalleryViewItem(FirstActivity.this, id);
		}

	}

	class GalleryViewItem extends FrameLayout {

		int[] backgroundImages = { R.drawable.first1, R.drawable.first2,
				R.drawable.splash, R.drawable.splash };

		public GalleryViewItem(final Context context, final int position) {
			super(context);

			// this.setOrientation(LinearLayout.VERTICAL);
			this.setLayoutParams(new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT,
					LinearLayout.LayoutParams.FILL_PARENT));

			this.setBackgroundResource(backgroundImages[position]);

		}
	}
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			info.edit().putBoolean("isFirst", false).commit();
			startActivity(new Intent(FirstActivity.this,
					MainActivity.class));
			finish();
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
