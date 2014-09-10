package com.unique.dalian.voicephoto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import helper.Declare;
import helper.JSONHelper;
import helper.MyAdapter;

public class SeeActivity extends Activity {

    private GridView gridView;
    private String[] pathArray;
    private LruCache<String, Bitmap> imageCache;
    private GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see);

        gridView = (GridView) findViewById(R.id.see_grid);
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        imageCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };
        new BitmapTask().execute();
        gridView.setOnItemClickListener(new MyOnItemClickListener());
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Intent intent = new Intent(getApplicationContext(), ShowActivity.class);
            intent.putExtra(Declare.INTENT_PATH, pathArray[position]);
            intent.putExtra(Declare.INTENT_POSITION, position);
            startActivity(intent);
        }
    }

    class BitmapTask extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            JSONHelper jsonHelper = new JSONHelper(getApplicationContext());
            JSONArray array = jsonHelper.getArray();
            if (array == null)
                return null;

            pathArray = new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = jsonHelper.getObject(array, i);
                try {
                    String path = (String) object.get(Declare.PHOTO_PATH);
                    pathArray[i] = path;

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(path, options);

                    options.inSampleSize = Math.round((float) options.outWidth / 200);
                    options.inPurgeable = true;
                    options.inInputShareable = true;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;      //save memory
                    options.inJustDecodeBounds = false;
                    Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                    imageCache.put(path, bitmap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (i == 12 || i == array.length() - 1)
                    publishProgress();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            adapter = new GridAdapter(getApplicationContext(), gridView, pathArray, imageCache);
            gridView.setAdapter(adapter);
        }
    }
}
