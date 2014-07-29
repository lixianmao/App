package org.hustunique.lockscreen;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;


public class SlidingActivity extends SlidingFragmentActivity {

	protected SlidingMenu slidingMenu;
	private Fragment content;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sliding);
		setBehindContentView(R.layout.sliding_layout);
		initSlidingMenu();
	}

	private void initSlidingMenu() {

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		content = new ReviewFragment();
		ft.replace(R.id.content_fragment, content);
		Fragment menu = new SlidingFragment();
		ft.replace(R.id.sliding_fragment, menu);
		ft.commit();

		slidingMenu = getSlidingMenu();
		slidingMenu.setMode(SlidingMenu.LEFT);
		slidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		slidingMenu.setFadeDegree(0.35f);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setFadeEnabled(true);
		slidingMenu.setBehindScrollScale(1.0f);
		getWindow().setBackgroundDrawableResource(R.drawable.sf_bg);
	}

	/**
	 * 显示指定content
	 */
	public void showContent(Fragment fragment) {
		content = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_fragment, content).commit();
		getSlidingMenu().showContent();
	}

}
