package com.unique.dalian.voicephoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import helper.Declare;
import helper.JSONHelper;

public class ShowActivity extends ActionBarActivity implements View.OnClickListener, View.OnTouchListener {

    private ImageView photoView;
    private Button deleteBtn;
    private JSONHelper jsonHelper;
    private JSONArray array;
    private JSONObject object;
    private int position;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        photoView = (ImageView) findViewById(R.id.show_photo);
        deleteBtn = (Button) findViewById(R.id.show_delete);
        showPhoto();
        initJSON();
        photoView.setOnTouchListener(this);
        deleteBtn.setOnClickListener(this);
    }

    private void showPhoto() {
        Intent intent = getIntent();
        String path = intent.getStringExtra(Declare.INTENT_PATH);
        position = intent.getIntExtra(Declare.INTENT_POSITION, 0);

        if (!TextUtils.isEmpty(path)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);

            options.inJustDecodeBounds = false;
            options.inSampleSize = options.outWidth / 720;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inPreferredConfig = Bitmap.Config.RGB_565;      //save memory

            bitmap = BitmapFactory.decodeFile(path, options);

            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            float height = (float) options.outHeight / options.outWidth * width;
            photoView.setLayoutParams(new RelativeLayout.LayoutParams(width, (int) height));
            photoView.setImageBitmap(bitmap);
        }
    }

    private void initJSON() {
        jsonHelper = new JSONHelper(this);
        array = jsonHelper.getArray();
        object = jsonHelper.getObject(array, position);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            float x = event.getX() / photoView.getWidth() * 100;
            float y = event.getY() / photoView.getHeight() * 100;
            RelativeLayout layout = (RelativeLayout) findViewById(R.id.show_parent);

            try {
                int type = object.getInt(Declare.TYPE);
                if (type == Declare.TYPE_TEXT) {
                    JSONArray array = object.getJSONArray(Declare.CONTENT);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject content = (JSONObject) array.get(i);
                        String text = content.getString(Declare.TEXT);
                        float xPos = (float) content.getDouble(Declare.X);
                        float yPos = (float) content.getDouble(Declare.Y);

                        if (Math.abs(x - xPos) < 3 && Math.abs(y - yPos) < 3) {
                            MyEditText editText = new MyEditText(getApplicationContext());
                            editText.setText(text);
                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                    RelativeLayout.LayoutParams.WRAP_CONTENT);
                            params.leftMargin = Math.round(xPos / 100 * photoView.getWidth()) + photoView.getLeft();
                            params.topMargin = Math.round(yPos / 100 * photoView.getHeight()) + photoView.getTop();
                            layout.addView(editText, params);
                        }
                    }
                } else {
                    JSONObject content = object.getJSONObject(Declare.CONTENT);
                    String voicePath = content.getString(Declare.VOICE_PATH);
                    float xPos = (float) content.getDouble(Declare.X);
                    float yPos = (float) content.getDouble(Declare.Y);
                    if (Math.abs(x - xPos) < 3 && Math.abs(y - yPos) < 3) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = Math.round(xPos / 100 * photoView.getWidth()) + photoView.getLeft();
                        params.topMargin = Math.round(yPos / 100 * photoView.getHeight()) + photoView.getTop();
                        PlayHelper playHelper = new PlayHelper(getApplicationContext(), voicePath);
                        layout.addView(playHelper, params);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.show_delete:
                try {
                    int type = object.getInt(Declare.TYPE);
                    if (type == Declare.TYPE_VOICE) {
                        JSONObject content = object.getJSONObject(Declare.CONTENT);
                        String voicePath = content.getString(Declare.VOICE_PATH);
                        File file = new File(voicePath);
                        if (file.exists())
                            file.delete();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonHelper.deleteObject(array, position);
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
            System.gc();
        }
    }
}
