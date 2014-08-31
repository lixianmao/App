package helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.unique.dalian.voicephoto.R;

import java.util.List;

/**
 * Created by dalian on 8/9/14.
 */
public class MyAdapter extends BaseAdapter {

    private List<String> list;
    private Context context;
    private GridView mGridView;
    private int mFirstVisibleItem;
    private int mVisibleItemCount;
    private boolean isFirst = true;
    private LruCache<String, Bitmap> mLruCache;

    public MyAdapter(Context context, List<String> list, GridView gridView) {
        this.context = context;
        this.list = list;
        mGridView = gridView;

        mGridView.setOnScrollListener(new MyOnScrollListener());
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        Log.e("max memory", maxMemory / 1024 + "");
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight();
            }
        };

    }

    @Override
    public int getCount() {
        if (list == null || list.size() == 0)
            return -1;
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null || list.size() == 0)
            return -1;
        return position;
    }

    @Override
    public long getItemId(int position) {
        if (list == null || list.size() == 0)
            return -1;
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (list == null || list.size() == 0)
            return null;
        final ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            String path = list.get(position);
            imageView.setTag(path);
            setImageForImageView(imageView, path);
        } else {
            imageView = (ImageView) convertView;
        }

        return imageView;
    }

    private void setImageForImageView(ImageView imageView, String path) {
        Bitmap bitmap = getBitmapFromCache(path);
        Log.e("log", "setBitmap");
        if (bitmap != null) {
            Log.e("log", "from cache");
            imageView.setImageBitmap(bitmap);
        } else
            imageView.setImageResource(R.drawable.ic_launcher);
    }

    public void addBitmapToCache(String key, Bitmap bitmap) {
        if (getBitmapFromCache(key) != null)
            mLruCache.put(key, bitmap);
        Log.e("log", "add");
    }

    public Bitmap getBitmapFromCache(String key) {
        return mLruCache.get(key);
    }

    class MyOnScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            if (scrollState == SCROLL_STATE_IDLE)
                loadBitmaps(mFirstVisibleItem, mVisibleItemCount);
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
        for (int i = firstVisibleItem; i < visibleItemCount; i++) {
            String path = list.get(i);
            Bitmap bitmap = getBitmapFromCache(path);
            ImageView imageView = (ImageView) mGridView.findViewWithTag(path);
            if (bitmap == null) {
                bitmap = decodeBitmap(path);
                if (bitmap != null && imageView != null)
                    imageView.setImageBitmap(bitmap);
                addBitmapToCache(path, bitmap);
            } else {
                if (imageView != null)
                    imageView.setImageBitmap(bitmap);
            }
        }
    }

    private Bitmap decodeBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        options.inSampleSize = Math.round((float) options.outWidth / 200);
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inPreferredConfig = Bitmap.Config.RGB_565;      //save memory
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path, options);
    }
}
