package org.hustunique.lockscreen.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.hustunique.lockscreen.R;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class DbFile {

	private Context context;
	public static final String PATH = "data"
			+ Environment.getDataDirectory().getAbsolutePath()
			+ "/org.hustunique.lockscreen" + "/database";

	public DbFile(Context context) {
		this.context = context;
	}

	public void createFile(String store) {
		File dir = new File(PATH);
		if (!dir.exists()) {
			dir.mkdir();
		}
		File file = new File(dir, store);
		InputStream is = null;
		if (store.equals("/cet_4.db")) {
			is = context.getResources().openRawResource(R.raw.cet_4);
		} else {
			is = context.getResources().openRawResource(R.raw.cet_6);
		}
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			byte[] buffer = new byte[1024];
			int count = 0;

			while ((count = is.read(buffer)) > 0) {
				fos.write(buffer, 0, count);
			}
			is.close();
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("log", "file not found");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e("log", "file io exception");
		}
	}

}
