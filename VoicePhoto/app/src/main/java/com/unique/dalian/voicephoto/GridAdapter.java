package com.unique.dalian.voicephoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by dalian on 8/13/14.
 */
public class GridAdapter extends BaseAdapter {

    private Context context;
    private String[] pathArray;
    private LruCache<String, Bitmap> imageCache;
    private int mFirstVisibleItem, mVisibleItemCount;
    private boolean isFirst = true;
    private GridView gridView;

    public GridAdapter(Context context, GridView gridView, String[] pathArray, LruCache<String, Bitmap> imageCache) {
        this.context = context;
        this.pathArray = pathArray;
        this.imageCache = imageCache;
        this.gridView = gridView;

        this.gridView.setOnScrollListener(new MyOnScrollListener());
    }

    @Override
    public int getCount() {
        if (pathArray == null || pathArray.length == 0)
            return -1;
        return pathArray.length;
    }

    @Override
    public Object getItem(int position) {
        if (pathArray == null || pathArray.length == 0)
            return -1;
        return position;
    }

    @Override
    public long getItemId(int position) {
        if (pathArray == null || pathArray.length == 0)
            return -1;
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (pathArray == null || pathArray.length == 0)
            return null;
        final ImageView imageView;
        if (null == convertView) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setId(position);
        String path = pathArray[position];
        Bitmap bmp = imageCache.get(path);
        if (bmp != null)
            imageView.setImageBitmap(bmp);
        else {
            imageView.setImageResource(R.drawable.ic_launcher);
            imageView.setTag("no");
        }

        return imageView;
    }

    class MyOnScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE) {
                loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            mFirstVisibleItem = firstVisibleItem;
            mVisibleItemCount = visibleItemCount;
            if (isFirst && visibleItemCount > 0) {
                loadBitmaps(firstVisibleItem, visibleItemCount);
                isFirst = false;
            }
        }
    }

    private void loadBitmaps(int firstVisibleItem, int visibleItemCount) {
        for (int i = firstVisibleItem; i < firstVisibleItem + visibleItemCount; i++) {
            ImageView imageView = (ImageView) gridView.findViewById(i);
            String tag = (String) imageView.getTag();
            if (tag != null && tag.equals("null")) {
                String path = pathArray[i];
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(path, options);

                options.inSampleSize = Math.round((float) options.outWidth / 200);
                options.inPurgeable = true;
                options.inInputShareable = true;
                options.inPreferredConfig = Bitmap.Config.RGB_565;      //save memory
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeFile(path, options);
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                    imageCache.put(path, bitmap);
                }

            }
            imageView.setTag("yes");
        }
    }
}
