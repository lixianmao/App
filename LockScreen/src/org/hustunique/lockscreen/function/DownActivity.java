package org.hustunique.lockscreen.function;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.hustunique.lockscreen.R;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class DownActivity extends Activity {

	private ListView listView;
	private String host = "http://192.168.1.103/";
	private DownUtils downUtils;
	private int downProgress;
	private ProgressBar bar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_down);

		listView = (ListView) findViewById(R.id.down_lv);
		bar = (ProgressBar) findViewById(R.id.down_pb);
		downUtils = new DownUtils(host);

		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message msg = new Message();
				msg.what = 0x123;
				msg.obj = downUtils.getFiles();
				Log.e("files", downUtils.getFiles());
				handler.sendMessage(msg);
				super.run();
			}

		}.start();
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == 0x123 && !((String) msg.obj).equals("")) {
				List<String> list = new ArrayList<String>();
				try {
					JSONArray array = new JSONArray((String) msg.obj);
					for (int i = 0; i < array.length(); i++) {
						list.add(array.getString(i));
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				listView.setAdapter(new DownBaseAdapter(list));

			} else if (msg.what == 0x456) {
				bar.setProgress(downProgress);
			} else if (msg.what == 0x789) {
				// Todo
			}

			super.handleMessage(msg);
		}

	};

	class DownBaseAdapter extends BaseAdapter {

		private List<String> list;

		public DownBaseAdapter(List<String> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			LayoutInflater inflater = getLayoutInflater();
			convertView = inflater.inflate(R.layout.item_down, null);
			TextView tv = (TextView) convertView
					.findViewById(R.id.item_down_tv);
			Button btn = (Button) convertView.findViewById(R.id.item_down_btn);
			btn.setTag(position);
			tv.setText(list.get(position));
			btn.setOnClickListener(new MyOnClickListener(list.get(position)));
			return convertView;
		}

	}

	class MyOnClickListener implements OnClickListener {

		private String fileName;

		public MyOnClickListener(String fileName) {
			this.fileName = fileName;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			new Thread() {

				@Override
				public void run() {
					final Timer timer = new Timer();
					timer.schedule(new TimerTask() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							downProgress = (int) (downUtils.getCompleteRate() * 100);
							handler.sendEmptyMessage(0x456);
							if (downProgress >= 100) {
								timer.cancel();
								handler.sendEmptyMessage(0x789);
							}
						}
					}, 0, 100);
					downUtils.downFile(fileName);
					super.run();
				}

			}.start();
		}

	}

}
