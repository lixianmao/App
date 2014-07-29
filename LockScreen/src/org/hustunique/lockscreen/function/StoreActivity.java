package org.hustunique.lockscreen.function;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hustunique.lockscreen.R;
import org.hustunique.lockscreen.database.ContentHelper;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

public class StoreActivity extends Activity {

	private FrameLayout frameLayout;
	private SwipeListView swipeListView;
	private SwipeAdapter swipeAdapter;
	public static int deviceWidth;
	private View mView;
	private List<String> storeList;
	private String filePath = "data"
			+ Environment.getDataDirectory().getAbsolutePath()
			+ "/org.hustunique.lockscreen" + "/database";
	private String store = "";
	private MyBaseAdapter myBaseAdapter;
	private List<String> dataList;
	private ListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_store);

		initView();
	}

	private void initView() {
		frameLayout = (FrameLayout) findViewById(R.id.store_framelayout);
		swipeListView = (SwipeListView) findViewById(R.id.store_list);
		storeList = getList();
		swipeAdapter = new SwipeAdapter(getApplicationContext(), swipeListView,
				storeList);

		deviceWidth = getResources().getDisplayMetrics().widthPixels;
		swipeListView.setAdapter(swipeAdapter);
		swipeListView.setSwipeListViewListener(new MySwipeListViewListener());
		reload();
	}

	private List<String> getList() {
		File file = new File(filePath);
		String[] strings = file.list();

		List<String> list = new ArrayList<String>();
		list.add("新建列表");

		for (int i = 0; i < strings.length; i++) {
			if (strings[i].endsWith(".db"))
				list.add(strings[i]);
		}
		return list;
	}

	private void reload() {
		swipeListView.setSwipeMode(SwipeListView.SWIPE_MODE_LEFT);
		swipeListView.setSwipeActionLeft(SwipeListView.SWIPE_ACTION_REVEAL);
		swipeListView.setOffsetLeft(deviceWidth * 2 / 3);

		swipeListView.setAnimationTime(0);
		swipeListView.setSwipeOpenOnLongPress(false);
	}

	class MySwipeListViewListener extends BaseSwipeListViewListener {

		@Override
		public void onClickFrontView(int position) {
			// TODO Auto-generated method stub
			super.onClickFrontView(position);
			if (position == 0) {
				mView = getLayoutInflater().inflate(R.layout.layout_new_store,
						null);
				RelativeLayout rLayout = (RelativeLayout) mView
						.findViewById(R.id.item_new_store_rl);
				TextView finishView = (TextView) mView
						.findViewById(R.id.store_tv_finish);

				rLayout.setOnClickListener(mOnClickListener);
				finishView.setOnClickListener(mOnClickListener);
				frameLayout.addView(mView);
			} else {
				store = (String) swipeListView.getItemAtPosition(position);
				Log.e("store", store + " selected");

				mView = getLayoutInflater()
						.inflate(R.layout.layout_store, null);
				RelativeLayout rLayout = (RelativeLayout) mView
						.findViewById(R.id.item_store_rl);
				TextView nameView = (TextView) mView
						.findViewById(R.id.item_store_title);
				TextView editView = (TextView) mView
						.findViewById(R.id.item_store_edit);
				listView = (ListView) mView
						.findViewById(R.id.item_store_listview);

				nameView.setText(store);
				rLayout.setOnClickListener(mOnClickListener);
				editView.setOnClickListener(mOnClickListener);
				dataList = getStoreData();
				if (dataList != null && dataList.size() > 0) {
					myBaseAdapter = new MyBaseAdapter(getApplicationContext(),
							dataList);
					listView.setAdapter(myBaseAdapter);
					listView.setOnItemClickListener(new MyOnItemClickListener());
				}
				frameLayout.addView(mView);

			}
			swipeListView.setEnabled(false);
		}

		@Override
		public void onDismiss(int[] reverseSortedPositions) {
			// TODO Auto-generated method stub
			super.onDismiss(reverseSortedPositions);
			for (int position : reverseSortedPositions) {
				String path = filePath + "/" + storeList.get(position);
				File file = new File(path);
				if (!file.delete()) {
					Toast.makeText(getApplicationContext(), "删除词库失败",
							Toast.LENGTH_SHORT).show();
				}

				storeList.remove(position);
			}
			swipeAdapter.notifyDataSetChanged();
		}

	}

	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (null == mView) {
				Log.e("StoreActivity", "mView is null");
				return;
			}
			int id = v.getId();
			if (id == R.id.store_tv_finish) {
				EditText editText = (EditText) mView
						.findViewById(R.id.store_et);
				String title = editText.getText().toString();
				if (TextUtils.isEmpty(title)) {
					Log.e("title", "标题不能为空");
					return;
				}
				if (!title.isEmpty()) {
					File file = new File(filePath, title + ".db");
					try {
						if (file.createNewFile()) {
							storeList.add(title + ".db");
							SQLiteDatabase database = SQLiteDatabase
									.openOrCreateDatabase(file, null);
							String sql = "create table content("
									+ "ID integer primary key autoincrement, "
									+ "BODY text not null, " + "MEAN text, "
									+ "MARK text)";
							database.execSQL(sql);
							database.close();
						} else {
							Toast.makeText(getApplicationContext(), "创建词库失败",
									Toast.LENGTH_SHORT).show();
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				swipeAdapter.notifyDataSetChanged();
				frameLayout.removeView(mView);
				swipeListView.setEnabled(true);

			} else if (id == R.id.item_store_edit) {
				Intent intent = new Intent();
				intent.setClass(getApplicationContext(), EditActivity.class);
				intent.setAction("insert");
				intent.putExtra("storePath", filePath + "/" + store);
				startActivityForResult(intent, 12);
			} else if (id == R.id.item_store_rl || id == R.id.item_new_store_rl) {
				frameLayout.removeView(mView);
				swipeListView.setEnabled(true);
			}

		}
	};

	class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
			intent.setClass(getApplicationContext(), EditActivity.class);
			intent.setAction("update");
			intent.putExtra("position", position);
			intent.putExtra("storePath", filePath + "/" + store);
			startActivityForResult(intent, 11);
		}

	}

	private List<String> getStoreData() {
		List<String> list = new ArrayList<String>();
		if (store.equals(""))
			return null;
		try {

			String path = filePath + "/" + store;
			SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null,
					SQLiteDatabase.OPEN_READONLY);
			Cursor cursor = database.query(true, ContentHelper.CONTENT_TABLE,
					new String[] { ContentHelper.CONTENT_BODY }, null, null,
					null, null, null, null, null);
			if (cursor == null)
				return list;

			cursor.moveToLast();
			int length = cursor.getCount(); // 数据库中全部数据条数
			if (length == 0)
				return list;

			cursor.moveToFirst();
			do {
				list.add(cursor.getString(0));
			} while (cursor.moveToNext());

			cursor.close();
			database.close();

		} catch (SQLiteException e) {
			// TODO: handle exception
			Log.e("empty database", "this store is empty");
		}

		return list;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == 14) {
			dataList.add(data.getStringExtra("result body"));
			myBaseAdapter.notifyDataSetChanged();
		} else if (resultCode == 13) {
			dataList.set(data.getIntExtra("result position", 1),
					data.getStringExtra("result body"));
			myBaseAdapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
