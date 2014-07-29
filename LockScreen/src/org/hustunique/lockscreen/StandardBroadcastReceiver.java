package org.hustunique.lockscreen;

import org.hustunique.lockscreen.database.ContentHelper;
import org.hustunique.lockscreen.database.SpHelper;
import org.hustunique.lockscreen.guide.GuideActivity;
import org.hustunique.lockscreen.tools.AlarmService;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class StandardBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();

		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			/** 解除系统锁屏 **/
			KeyguardManager km = (KeyguardManager) context
					.getSystemService(Context.KEYGUARD_SERVICE);
			KeyguardLock kl = km.newKeyguardLock("");
			kl.disableKeyguard();

			/** 启动服务 **/
			Intent service = new Intent();
			service.setClass(context, LockScreenService.class);
			context.startService(service);
		} else if (action.equals(AlarmService.UPDATE_ALARM)) {
			// 重载每日新词
			SpHelper spHelper = new SpHelper(context);
			String name = spHelper.getString(GuideActivity.PATH);
			int order = spHelper.getInt(GuideActivity.ORDER);
			int sum = spHelper.getInt(GuideActivity.SUM);
			new ContentHelper(context, name).getFirstContent(order, sum);
		} else if (action.equals(AlarmService.REVIEW_ALARM)) {
			// 发送状态栏通知， 复习时间到
			Toast.makeText(context, "闹钟事件设置成功", Toast.LENGTH_SHORT).show();
			
			NotificationCompat.Builder builder = new NotificationCompat.Builder(
					context);
			builder.setLargeIcon(BitmapFactory.decodeResource(
					context.getResources(), R.drawable.lock));
			builder.setSmallIcon(R.drawable.unlock);
			builder.setContentTitle(context.getText(R.string.app_name));
			builder.setContentText("请复习今天的锁屏内容，亲");

			Intent resultIntent = new Intent(context, SlidingActivity.class);
			TaskStackBuilder stackBuilder = TaskStackBuilder.from(context);  //**supportV4包改变后，重新设定的
			stackBuilder.addParentStack(SlidingActivity.class);
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent pi = stackBuilder.getPendingIntent(0,
					PendingIntent.FLAG_UPDATE_CURRENT);
			builder.setContentIntent(pi);
			NotificationManager nm = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			nm.notify(0, builder.getNotification());	//**supportV4包改变后，重新设定的

		}

	}

}
