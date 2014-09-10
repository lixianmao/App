package com.unique.dalian.voicephoto;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import helper.Declare;
import helper.JSONHelper;

/**
 * modified by dalian on 8/31/2014
 * initialize the child views that reflect the description of the photo
 */
public class ShowActivity extends ActionBarActivity implements View.OnClickListener, View.OnTouchListener {

    private ImageView photoView;
    private Button deleteBtn, editBtn;
    private JSONHelper jsonHelper;
    private JSONArray array;
    private JSONObject object;
    private int position;
    private Bitmap bitmap;

    private PlayHelper playHelper;
    private List<MyEditText> textList = new ArrayList<MyEditText>();
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);

        photoView = (ImageView) findViewById(R.id.show_photo);
        deleteBtn = (Button) findViewById(R.id.show_delete);
        editBtn = (Button) findViewById(R.id.show_edit);
        showPhoto();
        initJSON();
        photoView.setOnTouchListener(this);
        deleteBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);
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
            if (type == Declare.TYPE_TEXT && textList.size() > 0) {
                for (MyEditText editText : textList) {
                    float xPos = editText.xPos;
                    float yPos = editText.yPos;
                    if (Math.abs(x - xPos) < 3 && Math.abs(y - yPos) < 3) {
                        editText.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            } else if (type == Declare.TYPE_VOICE && playHelper != null) {
                float xPos = playHelper.xPos;
                float yPos = playHelper.yPos;
                if (Math.abs(x - xPos) < 3 && Math.abs(y - yPos) < 3) {
                    playHelper.setVisibility(View.VISIBLE);
                }
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
            case R.id.show_edit:
                if (type == Declare.TYPE_TEXT && textList.size() > 0) {
                    for (MyEditText editText : textList) {
                        editText.setVisibility(View.VISIBLE);
                        editText.setFocusable(true);
                        editText.setFocusableInTouchMode(true);
                    }
                } else if (type == Declare.TYPE_VOICE && playHelper != null) {
                    playHelper.setVisibility(View.VISIBLE);
                }
                break;
            default:
                break;
        }
    }

    /**
     * get the width and height of the photoView since it can only be accessed after the onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();
        photoView.postDelayed(new Runnable() {
            @Override
            public void run() {
                int photoWidth = photoView.getWidth();
                int photoHeight = photoView.getHeight();
                Log.e("photoView", "width:" + photoWidth + " height:" + photoHeight);

                RelativeLayout layout = (RelativeLayout) findViewById(R.id.show_parent);
                try {
                    type = object.getInt(Declare.TYPE);
                    if (type == Declare.TYPE_TEXT) {
                        String content = object.getString(Declare.CONTENT);
                        JSONArray textArray = new JSONArray(content);
                        if (content != null && content.length() > 0) {
                            for (int i = 0; i < textArray.length(); i++) {
                                JSONObject textObject = (JSONObject) textArray.get(i);
                                String text = textObject.getString(Declare.TEXT);
                                float xPos = (float) textObject.getDouble(Declare.X);
                                float yPos = (float) textObject.getDouble(Declare.Y);

                                MyEditText editText = new MyEditText(getApplicationContext(), layout);
                                editText.setText(text);
                                editText.setPos(xPos, yPos);
                                editText.setVisibility(View.INVISIBLE);
                                editText.setFocusable(false);
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                                params.leftMargin = Math.round(xPos / 100 * photoView.getWidth()) + photoView.getLeft();
                                params.topMargin = Math.round(yPos / 100 * photoView.getHeight()) + photoView.getTop();
                                layout.addView(editText, params);
                                textList.add(editText);
                            }
                        }
                    } else {
                        JSONObject voiceObject = object.getJSONObject(Declare.CONTENT);
                        String voicePath = voiceObject.getString(Declare.VOICE_PATH);
                        float xPos = (float) voiceObject.getDouble(Declare.X);
                        float yPos = (float) voiceObject.getDouble(Declare.Y);

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                                RelativeLayout.LayoutParams.WRAP_CONTENT);
                        params.leftMargin = Math.round(xPos / 100 * photoView.getWidth()) + photoView.getLeft();
                        params.topMargin = Math.round(yPos / 100 * photoView.getHeight()) + photoView.getTop();
                        playHelper = new PlayHelper(getApplicationContext(), voicePath);
                        playHelper.setPos(xPos, yPos);
                        playHelper.setVisibility(View.INVISIBLE);
                        layout.addView(playHelper, params);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, 300);
    }

    /**
     * require the system to free memory in time
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
            System.gc();
        }
    }
}
