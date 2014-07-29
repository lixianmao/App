package org.hustunique.lockscreen.tools;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyToast extends TextView implements AnimationListener {

	private RelativeLayout layout;
	private int index = 1;
	private Animation animation1, animation2;

	public MyToast(Context context, RelativeLayout layout, int flag, String text) {
		super(context);
		// TODO Auto-generated constructor stub
		if (context == null) {
			return;
		}
		this.layout = layout;

		setText(text);
		setTextSize(15);
		setBackgroundColor(Color.BLACK);
		setTextColor(Color.WHITE);
		setGravity(Gravity.CENTER);
		setWidth(200);
		setHeight(60);

		layout.addView(this);

		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		int x = dm.widthPixels / 2 - 100;
		int y = dm.heightPixels / 3;

		if (flag == 1) {
			animation1 = new TranslateAnimation(x, x, 0, y);
			animation2 = new TranslateAnimation(x, x, y, -50);
		} else {
			animation1 = new TranslateAnimation(x, x, 3 * y, 2 * y);
			animation2 = new TranslateAnimation(x, x, 2 * y, 3 * y + 50);
		}

		animation1.setDuration(500);
		animation1.setAnimationListener(this);
		animation1.setInterpolator(AnimationUtils.loadInterpolator(context,
				android.R.anim.decelerate_interpolator));

		animation2.setDuration(500);
		animation2.setAnimationListener(this);
		animation2.setInterpolator(AnimationUtils.loadInterpolator(context,
				android.R.anim.accelerate_interpolator));

		startAnimation(animation1);

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		switch (index) {
		case 1:
			if (null != animation2) {
				startAnimation(animation2);
				index++;
			}
			break;
		case 2:
			layout.removeView(this);
			break;
		default:
			break;
		}

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

}
