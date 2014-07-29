package org.hustunique.lockscreen.database;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SpHelper {

	public SharedPreferences sp;
	public static final String SP_NAME = "set_info";

	/**
	 * 设置信息的保存与更改
	 * 
	 * @param context
	 */
	public SpHelper(Context context) {
		sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
	}

	public int getInt(String key) {
		return sp.getInt(key, -1);
	}

	public String getString(String key) {
		return sp.getString(key, "");
	}

	public void setInt(String key, int value) {
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public void setString(String key, String value) {
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public boolean delete(String key) {
		Editor editor = sp.edit();
		editor.remove(key);
		return editor.commit();
	}

	public boolean getBoolean(String key) {
		return sp.getBoolean(key, true);
	}

	public void setBoolean(String key, boolean value) {
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void setIntArray(String key, int[] ints) {
		Editor editor = sp.edit();
		Set<String> set = new HashSet<String>();
		for (int i : ints) {
			set.add(String.valueOf(i));
		}
		editor.putStringSet(key, set);
		editor.commit();
	}

	public int[] getIntArray(String key) {
		Set<String> set = sp.getStringSet(key, null);
		if (set == null)
			return null;

		String[] strings = set.toArray(new String[set.size()]);
		int[] ints = new int[strings.length];
		for (int i = 0; i < strings.length; i++) {
			ints[i] = Integer.parseInt(strings[i]);
		}
		return ints;
	}

	public void setStringArray(String key, List<String> list) {
		Editor editor = sp.edit();
		Set<String> set = new HashSet<String>();
		for (int i = 0; i < list.size(); i++) {
			set.add(list.get(i));
		}
		editor.putStringSet(key, set);
		editor.commit();
	}

	public List<String> getStringArray(String key) {
		Set<String> set = sp.getStringSet(key, null);
		List<String> list = new ArrayList<String>();
		if (null == set)
			return null;
		Iterator<String> iterator = set.iterator();
		while (iterator.hasNext()) {
			list.add(iterator.next());
		}
		return list;

	}
}
