package com.guest.cnbeta.util;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

public class ScaleAnimationHelper {
	Context con;
	int order;

	public ScaleAnimationHelper(Context con, int order) {
		this.con = con;
		this.order = order;
	}

	ScaleAnimation myAnimation_Scale;

	// 放大的类,不需要设置监听器
	public void ScaleOutAnimation(View view) {
		myAnimation_Scale = new ScaleAnimation(0, 1.0f, 0, 1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		myAnimation_Scale.setInterpolator(new AccelerateInterpolator());
		AnimationSet as = new AnimationSet(true);
		as.addAnimation(myAnimation_Scale);
		as.setDuration(900);
		view.startAnimation(as);
	}

	public void ScaleInAnimation(View view) {
		myAnimation_Scale = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		myAnimation_Scale.setInterpolator(new AccelerateInterpolator());
		AnimationSet as = new AnimationSet(true);
		as.addAnimation(myAnimation_Scale);
		as.setDuration(300);
		view.startAnimation(as);
	}
}
