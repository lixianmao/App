package org.hustunique.lockscreen;

import java.util.ArrayList;
import java.util.List;

import org.hustunique.lockscreen.database.ContentHelper;
import org.hustunique.lockscreen.database.SpHelper;
import org.hustunique.lockscreen.guide.GuideActivity;
import org.hustunique.lockscreen.tools.LockContent;
import org.hustunique.lockscreen.tools.MyToast;
import org.hustunique.lockscreen.tools.StatusManager;
import org.hustunique.lockscreen.tools.ViewPagerAdapter;

import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

public class LockScreenActivity extends FragmentActivity implements
		OnPageChangeListener {

	private ViewPager viewPager;
	private ImageView[] pointViews;
	private ViewPagerAdapter pagerAdapter;
	private ViewGroup pointGroup;
	private Vibrator vibrator;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private GestureDetector gestureDetector;
	private List<LockContent> secList;

	private String path; // 数据库文件路径
	private int count;
	public static boolean onScreen = false;
	private static float y = 0;

	public static final String RAW_IDs = "raw_ids";
	public static final String MASTER_IDs = "master_ids";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lock_screen);

		initView();
		initData();
		initFragment();
		initPoint();
		new StatusManager(getApplicationContext(), this); // 管理时间与日期的显示状态
	}

	private void initView() {
		viewPager = (ViewPager) findViewById(R.id.lockscreen_viewpager);
		pointGroup = (ViewGroup) findViewById(R.id.lockscreen_pointgroup);
		pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),
				fragments);
		gestureDetector = new GestureDetector(this, onGestureListener);
	}

	private void initData() {
		onScreen = true;
		SpHelper spHelper = new SpHelper(this);
		count = spHelper.getInt(GuideActivity.COUNT);
		path = spHelper.getString(GuideActivity.PATH);

	}

	private void initPoint() {
		pointViews = new ImageView[count + 1];
		for (int i = 0; i < count + 1; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LinearLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			imageView.setScaleType(ScaleType.FIT_CENTER);
			imageView.setPadding(20, 0, 20, 0);
			pointViews[i] = imageView;

			// 默认选中时i=0
			if (i == 0) {
				pointViews[i].setImageResource(R.drawable.star);
			} else if (i == count) {
				pointViews[i].setImageResource(R.drawable.lock);
			} else {
				pointViews[i].setImageResource(R.drawable.point);
			}

			pointGroup.addView(pointViews[i]);
		}
	}

	private void initFragment() {
		ContentHelper dbHelper = new ContentHelper(this, path);
		secList = dbHelper.getSecondContents(count);
		if (null != secList) {
			for (int i = 0; i < count; i++) {
				Fragment fragment = new ContentFragment(secList.get(i));
				fragments.add(fragment);
			}
		}

		// 最后一页解锁页面
		fragments.add(new UnlockFragment());

		viewPager.setAdapter(pagerAdapter);
		viewPager.setOnPageChangeListener(this);
	}

	// 使back键，音量加减键失效
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return disableKeycode(keyCode, event);
	}

	private boolean disableKeycode(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_BACK:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
		case KeyEvent.KEYCODE_VOLUME_UP:
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// TODO Auto-generated method stub
		if (position == count - 1 && positionOffset > 0.5) {
			finish();
		}
	}

	@Override
	public void onPageSelected(int position) {
		// TODO Auto-generated method stub
		for (int i = 0; i < count; i++) {
			if (i == position)
				pointViews[i].setImageResource(R.drawable.star);
			else
				pointViews[i].setImageResource(R.drawable.point);
		}
		if (position == count) {
			vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE); // 解锁时振动
			vibrator.vibrate(100);
			vibrator = null;
			finish();
		}
	}

	private GestureDetector.OnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			y = e2.getY() - e1.getY();
			if (Math.abs(y) > 200) {
				if (y < 0) {
					viewPager.setY(-200);
				} else {
					viewPager.setY(200);
				}

			} else {
				viewPager.setY(y);
			}
			return true;
		}

	};

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		if (event.getAction() == MotionEvent.ACTION_UP) {

			if (y > 200) {
				// 加入生词本
				SpHelper spHelper = new SpHelper(getApplicationContext());
				String rawIds = spHelper.getString(RAW_IDs);
				String masterIds = spHelper.getString(MASTER_IDs);
				if (null != secList && secList.size() > 0) {
					int id = secList.get(viewPager.getCurrentItem()).getId();

					if (!rawIds.contains("[" + id + "]")) {
						RelativeLayout layout = (RelativeLayout) findViewById(R.id.lockscreen_root);
						new MyToast(getApplicationContext(), layout, 1, "加入生词本");

						spHelper.setString(RAW_IDs, rawIds + "[" + id + "]");
					}
					if (masterIds.contains("[" + id + "]")) {
						spHelper.setString(MASTER_IDs,
								masterIds.replace("[" + id + "]", ""));
					}
				}
			}
			if (y < -200) {
				// 标记为已掌握
				SpHelper spHelper = new SpHelper(getApplicationContext());
				String rawIds = spHelper.getString(RAW_IDs);
				String masterIds = spHelper.getString(MASTER_IDs);
				int id = secList.get(viewPager.getCurrentItem()).getId();
				if (!masterIds.contains("[" + id + "]")) {
					RelativeLayout layout = (RelativeLayout) findViewById(R.id.lockscreen_root);
					new MyToast(getApplicationContext(), layout, 2, "标记为掌握");
					
					spHelper.setString(MASTER_IDs, masterIds + "[" + id + "]");
				}
				if (rawIds.contains("[" + id + "]")) {
					spHelper.setString(RAW_IDs,
							rawIds.replace("[" + id + "]", ""));
				}
			}

			viewPager.setY(0);
			y = 0;
		}
		gestureDetector.onTouchEvent(event);
		return super.dispatchTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		onScreen = false;
		Log.e("lockscreen", "destroy");
	}

}
