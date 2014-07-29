package org.hustunique.lockscreen.function;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.Environment;
import android.util.Log;

public class DownUtils {

	private String host;
	private HttpClient client = new DefaultHttpClient();
	private long fileSize;
	private boolean isFinished = false;
	private DownThread thread;

	public DownUtils(String host) {
		this.host = host;
	}

	public String getFiles() {
		String files = "";
		HttpGet get = new HttpGet(host + "files.json");

		try {
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						entity.getContent()));
				StringBuffer sb = new StringBuffer();
				String line = null;
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}
				br.close();
				files = sb.toString();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return files;
	}

	class DownThread extends Thread {

		private String fileName;
		public long length = 0;

		public DownThread(String fileName) {
			this.fileName = fileName;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			HttpGet get = new HttpGet(host + fileName);
			HttpResponse response = null;
			try {
				response = client.execute(get);
				HttpEntity entity = response.getEntity();
	
				if (!Environment.getExternalStorageState().equals(
						android.os.Environment.MEDIA_MOUNTED))
					return;

				File folder = new File(
						Environment.getExternalStorageDirectory() + "/l2l");
				if (!folder.exists())
					folder.mkdir();
				File file = new File(folder, fileName);
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(file));

				if (entity != null) {
					BufferedInputStream bis = new BufferedInputStream(
							entity.getContent());
					
					fileSize = entity.getContentLength();
					System.out.println(entity.getContentEncoding());
					System.out.println(entity.getContentType());
					System.out.println(entity.getContentLength());
					byte[] buffer = new byte[1024];
					int count = 0;
					while ((count = bis.read(buffer)) != -1) {
						bos.write(buffer, 0, count);
						length += count;
					}

					isFinished = true;
					bos.flush();
					bis.close();
					bos.close();
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 

			super.run();
		}

	}

	public void downFile(String fileName) {
		thread = new DownThread(fileName);
		thread.start();
	}

	public double getCompleteRate() {
		Log.e("length", thread.length + " / " + fileSize);
		return thread.length * 1.0 / fileSize;

	}

	public boolean isFinished() {
		return isFinished;
	}
}
