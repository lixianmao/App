package org.hustunique.lockscreen.guide;

import java.util.ArrayList;

import org.hustunique.lockscreen.R;
import org.hustunique.lockscreen.SlidingActivity;
import org.hustunique.lockscreen.database.DbFile;
import org.hustunique.lockscreen.database.SpHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class GuideActivity extends Activity implements OnPageChangeListener {

	private ViewPager viewPager;
	private ViewGroup pointGroup;
	private ArrayList<View> views = new ArrayList<View>();
	private int[] pics = new int[] { R.drawable.unlock, R.drawable.lock };
	private ImageView[] pointViews;

	public static final String ORDER = "lock_sel_order";
	public static final String SUM = "lock_daily_num";
	public static final String COUNT = "lock_timely_num";
	public static final String PATH = "lock_store";
	public static final String CUR_ID = "lock_cur_id";
	public static final String REVIEW_TIME = "review_alarm_time";
	public static final String UPDATE_TIME = "update_alarm_time";
	public static final String DO_REMIND = "remind_or_not";
	public static final String DO_LOCK = "lock_or_not";
	public static final String STORE = "store";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);

		initView();
		initPoint();
		initSp();
	}

	private void initView() {
		viewPager = (ViewPager) findViewById(R.id.guide_viewpager);
		pointGroup = (ViewGroup) findViewById(R.id.guide_pointgroup);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		for (int i = 0; i < pics.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(params);
			imageView.setImageResource(pics[i]);
			views.add(imageView);
		}
		views.add(getLayoutInflater().inflate(R.layout.item_guide, null));
		viewPager.setOnPageChangeListener(this);
		viewPager.setAdapter(new GuidePagerAdapter(views));
	}

	private void initPoint() {
		pointViews = new ImageView[pics.length + 1];
		for (int i = 0; i < views.size(); i++) {
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			imageView.setScaleType(ScaleType.FIT_CENTER);
			imageView.setPadding(20, 0, 20, 0);
			pointViews[i] = imageView;

			// 默认选中时i=0
			if (i == 0) {
				pointViews[i].setImageResource(R.drawable.star);
			} else {
				pointViews[i].setImageResource(R.drawable.point);
			}

			pointGroup.addView(pointViews[i]);
		}
	}

	/**
	 * 初始默认信息的设置
	 */
	private void initSp() {
		SpHelper spHelper = new SpHelper(this);
		spHelper.setInt(ORDER, 0);
		spHelper.setInt(COUNT, 5);
		spHelper.setInt(SUM, 30);
		spHelper.setString(PATH, "/cet_6.db");
		spHelper.setBoolean(SplashActivity.IS_FIRST, false);
		spHelper.setInt(CUR_ID, 0);
		spHelper.setString(REVIEW_TIME, "21:00");
		spHelper.setInt(UPDATE_TIME, 0);
		spHelper.setBoolean(DO_LOCK, true);
		spHelper.setBoolean(DO_REMIND, true);
	
		new DbFile(this).createFile("/cet_4.db");
		new DbFile(this).createFile("/cet_6.db");
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		for (int i = 0; i < views.size(); i++) {
			if (i == position)
				pointViews[i].setImageResource(R.drawable.star);
			else
				pointViews[i].setImageResource(R.drawable.point);
		}
		if (position == 2) { // 点击最后一个引导界面的按钮介入应用
			ImageView view = (ImageView) views.get(position).findViewById(
					R.id.guide_button);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent = new Intent();
					intent.setClass(getApplicationContext(), SlidingActivity.class);
					startActivity(intent);
					finish();
				}
			});
		}
	}
}
