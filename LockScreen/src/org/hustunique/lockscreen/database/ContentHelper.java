package org.hustunique.lockscreen.database;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.hustunique.lockscreen.guide.GuideActivity;
import org.hustunique.lockscreen.tools.LockContent;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ContentHelper {

	private Context context;
	private String name;
	public static final String CONTENT_TABLE = "content"; // 表名
	public static final String CONTENT_ID = "ID"; // 表的4个字段
	public static final String CONTENT_BODY = "BODY";
	public static final String CONTENT_MARK = "MARK";
	public static final String CONTENT_MEAN = "MEAN";
	public static final String SELECTED_IDs = "lock_daily_ids";

	public ContentHelper(Context context, String name) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.context = context;
	}

	/**
	 * 每日新词，一级内容
	 * 
	 * @param order
	 *            单词顺序
	 * @param sum
	 *            每日新词总数
	 * @return 每日新词的id
	 */
	public void getFirstContent(int order, int sum) {
		File file = new File(DbFile.PATH + name);
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(file,
				null);
		Log.e("db path", database.getPath());

		Cursor cursor = database.query(true, CONTENT_TABLE,
				new String[] { CONTENT_ID }, null, null, null, null, null,
				null, null);
		cursor.moveToLast();
		int length = cursor.getCount(); // 数据库中全部数据条数

		SpHelper spHelper = new SpHelper(context);
		int curId = spHelper.getInt(GuideActivity.CUR_ID);

		int[] ids = new int[sum];
		for (int i = 0; i < sum; i++) {
			switch (order) { // 0:正序 1:逆序 2:乱序
			case 0:
				if (curId == length) {
					curId = 0;
				}
				ids[i] = curId;
				curId++;
				break;
			case 1:
				if (curId < 0) {
					curId = length - 1;
				}
				ids[i] = curId;
				curId--;
				break;
			case 2:
			default: // 缺省值，乱序
				Random random = new Random(); // 产生n个不重复的随机数
				String string = "";

				curId = random.nextInt(length);
				while (string.contains(String.valueOf(curId))) {
					curId = random.nextInt(length);
				}
				string += String.valueOf(curId);
				ids[i] = curId;
				break;
			}
		}
		database.close();
		spHelper.setInt(GuideActivity.CUR_ID, curId);
		spHelper.setIntArray(SELECTED_IDs, ids);
	}

	/**
	 * 每次解锁新词， 二级内容
	 * 
	 * @param count
	 *            解锁个数
	 * @return List<LockContent>
	 */
	public List<LockContent> getSecondContents(int count) {
		File file = new File(DbFile.PATH + name);
		SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(file,
				null);
		SpHelper spHelper = new SpHelper(context);
		int[] ids = spHelper.getIntArray(SELECTED_IDs); // 获取每日全部新词的id
		if (ids == null)
			return null;

		Random random = new Random(); // 产生n个不重复的随机数
		List<LockContent> secList = new ArrayList<LockContent>();

		String string = "";
		Cursor cursor = null;
		for (int i = 0; i < count; i++) {
			int a = random.nextInt(ids.length);
			while (string.contains(String.valueOf(a))) {
				a = random.nextInt(ids.length);
			}
			string += String.valueOf(a);
			cursor = database.rawQuery("select * from " + CONTENT_TABLE
					+ " where " + CONTENT_ID + "=" + ids[a], null);
			if (null != cursor && cursor.moveToFirst()) {
				LockContent content = new LockContent();
				content.setBody(cursor.getString(1));
				content.setMark(cursor.getString(3));
				content.setMean(cursor.getString(2));
				content.setId(ids[a]);
				secList.add(content);
				cursor.close();
			}
		}
		database.close();
		return secList;
	}

}
