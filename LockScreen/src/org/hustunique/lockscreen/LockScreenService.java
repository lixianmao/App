package org.hustunique.lockscreen;

import org.hustunique.lockscreen.database.SpHelper;
import org.hustunique.lockscreen.guide.GuideActivity;
import org.hustunique.lockscreen.tools.AlarmService;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class LockScreenService extends Service {

	private KeyguardManager km;
	private KeyguardLock kl;
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub

			/** 启动锁屏应用 **/
			Intent lockScreen = new Intent();
			lockScreen.setClass(context, LockScreenActivity.class);
			lockScreen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(lockScreen);

		}

	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		/** 解除系统锁屏 **/
		km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
		kl = km.newKeyguardLock("");
		kl.disableKeyguard();

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.setPriority(1000);
		registerReceiver(receiver, filter);

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		// 添加闹钟事件
		SpHelper spHelper = new SpHelper(this);
		AlarmService alarm = new AlarmService(this);
		alarm.setAlarmUpdate(spHelper.getInt(GuideActivity.UPDATE_TIME));
		alarm.setAlarmReview(spHelper.getString(GuideActivity.REVIEW_TIME));
		Toast.makeText(this, "闹钟事件设置成功", Toast.LENGTH_SHORT).show();
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		/** 重新启动系统锁屏 **/
		kl.reenableKeyguard();
		if (receiver != null) {
			unregisterReceiver(receiver);
		}
	}

}
