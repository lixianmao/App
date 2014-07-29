package org.hustunique.lockscreen.tools;

import java.util.Calendar;

import org.hustunique.lockscreen.StandardBroadcastReceiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class AlarmService {

	private Context context;

	public static final String REVIEW_ALARM = "action_alarm_review";
	public static final String UPDATE_ALARM = "action_alarm_daily_update";

	public AlarmService(Context context) {
		this.context = context;
	}

	/**
	 * 设置更新每日新词的闹钟事件
	 */
	public void setAlarmUpdate(int hourOfDay) {
		Intent intent = new Intent(context, StandardBroadcastReceiver.class);
		intent.setAction(UPDATE_ALARM);
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);

		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, pi);
	}

	/**
	 * 设置复习提醒时间
	 * 
	 * @param hour
	 * @param minute
	 */
	public void setAlarmReview(String time) {
		Intent intent = new Intent(context, StandardBroadcastReceiver.class);
		intent.setAction(REVIEW_ALARM);
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Calendar calendar = Calendar.getInstance();
		int hour = Integer.parseInt(time.substring(0, 2));
		int minute = Integer.parseInt(time.substring(3));

		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		if (Calendar.getInstance().getTimeInMillis() > calendar // 避免闹钟设置时间小于当前时间时，系统启动闹钟的问题
				.getTimeInMillis()) {
			calendar.set(Calendar.DAY_OF_YEAR,
					calendar.get(Calendar.DAY_OF_YEAR) + 1);
		}

		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, pi);
	}

	public void cancelAlarmReview() {
		Intent intent = new Intent(context, StandardBroadcastReceiver.class);
		intent.setAction(REVIEW_ALARM);
		AlarmManager alarm = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.cancel(pi);
	}

}
