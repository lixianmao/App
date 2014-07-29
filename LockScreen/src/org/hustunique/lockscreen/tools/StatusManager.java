package org.hustunique.lockscreen.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.hustunique.lockscreen.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.text.format.Time;
import android.view.View;
import android.widget.TextView;

public class StatusManager {

	private Context context;
	private Activity activity;
	private TextView dateView, timeView;
	private Handler handler;
	private Date curDate;
	private Time time;
	private SimpleDateFormat format;

	public StatusManager(Context context, Activity activity) {
		this.context = context;
		this.activity = activity;

		initView();
		refreshDate();
	}

	private void initView() {
		dateView = (TextView) findViewById(R.id.lock_date);
		timeView = (TextView) findViewById(R.id.lock_time);
		registerComponent();
		time = new Time("GMT+8");
		format = new SimpleDateFormat("HH:mm");
	}

	private View findViewById(int id) {
		return activity.findViewById(id);
	}

	private BroadcastReceiver timeChangeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			handler = new Handler();
			handler.post(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					updateTime();
				}
			});
			//如果时区改变则需要刷新日期
			if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				refreshDate();
			}
			//如果锁屏界面被销毁则需要反注册监听器
			if (activity == null && timeChangeReceiver != null) {
				context.unregisterReceiver(this);
			}

		}

	};
	
	//更新时间
	private void updateTime() {
		curDate = new Date(System.currentTimeMillis());
		timeView.setText(format.format(curDate));
	}
	
	//注册时间变化监听器
	private void registerComponent() {
		if (timeChangeReceiver != null) {
			IntentFilter filter = new IntentFilter();
			filter.addAction(Intent.ACTION_TIME_TICK);
			filter.addAction(Intent.ACTION_TIME_CHANGED);
			filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
			context.registerReceiver(timeChangeReceiver, filter);
		}
	}
	
	//显示日期与时间
	private void refreshDate() {
		time.setToNow();
		int month = time.month + 1;
		int day = time.monthDay;
		int week = time.weekDay;
		String weekDay = new String();
		if (week == 7) {
			weekDay = "周日";
		} else if (week == 1) {
			weekDay = "周一";
		} else if (week == 2) {
			weekDay = "周二";
		} else if (week == 3) {
			weekDay = "周三";
		} else if (week == 4) {
			weekDay = "周四";
		} else if (week == 5) {
			weekDay = "周五";
		} else {
			weekDay = "周六";
		}
		dateView.setText(month + "月" + day + "日  " + weekDay);
		updateTime();
	}

}
