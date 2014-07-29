package org.hustunique.lockscreen.guide;

import org.hustunique.lockscreen.R;
import org.hustunique.lockscreen.SlidingActivity;
import org.hustunique.lockscreen.StandardBroadcastReceiver;
import org.hustunique.lockscreen.database.SpHelper;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {

	public static final String IS_FIRST = "isFirst";
	private static final int SWITCH_GUIDE = 1;
	private static final int SWITCH_MAIN = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		ComponentName receiver = new ComponentName(this,
				StandardBroadcastReceiver.class);
		PackageManager pm = this.getPackageManager();
		pm.setComponentEnabledSetting(receiver,
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP);

		boolean isFirst = isFirstEnter(this, this.getClass().getName());
		if (isFirst) {
			handler.sendEmptyMessageDelayed(SWITCH_GUIDE, 500);
		} else {
			handler.sendEmptyMessageDelayed(SWITCH_MAIN, 500);
		}
	}

	private boolean isFirstEnter(Context context, String className) {
		if (context == null || className == null
				|| "".equalsIgnoreCase(className)) {
			return false;
		}
		return new SpHelper(context).getBoolean(IS_FIRST);
	}

	public Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SWITCH_GUIDE:
				Intent intent1 = new Intent();
				intent1.setClass(SplashActivity.this, GuideActivity.class);
				startActivity(intent1);
				SplashActivity.this.finish();
				break;
			case SWITCH_MAIN:
				Intent intent2 = new Intent();
				intent2.setClass(SplashActivity.this, SlidingActivity.class);
				startActivity(intent2);
				SplashActivity.this.finish();
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};

}
