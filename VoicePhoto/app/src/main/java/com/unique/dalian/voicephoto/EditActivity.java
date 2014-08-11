package com.unique.dalian.voicephoto;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;

import helper.Declare;
import helper.JSONHelper;
import helper.PointPos;
import helper.TipHelper;


public class EditActivity extends Activity implements View.OnTouchListener, View.OnClickListener {

    private ImageView photoView, cancelView, saveView;
    private Bitmap bitmap;
    private boolean isEditAble = true;
    private TipHelper tipHelper;
    private boolean isFirst;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        initView();
        showPhoto();
    }

    private void initView() {
        photoView = (ImageView) findViewById(R.id.edit_iv_photo);
        cancelView = (ImageView) findViewById(R.id.edit_iv_cancel);
        saveView = (ImageView) findViewById(R.id.edit_iv_save);

        photoView.setOnTouchListener(this);
        cancelView.setOnClickListener(this);
        saveView.setOnClickListener(this);

        tipHelper = new TipHelper(this, (ViewGroup) findViewById(R.id.edit_parent));
        isFirst = tipHelper.getIsFirst();
        if (isFirst)
            tipHelper.addTip();

        Declare.textList = new ArrayList<MyEditText>();
        Declare.posList = new ArrayList<PointPos>();
    }

    private void showPhoto() {
        Intent intent = getIntent();
        Uri data = intent.getData();

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(data, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        photoPath = cursor.getString(columnIndex);
        cursor.close();

        bitmap = BitmapFactory.decodeFile(photoPath);
        photoView.setImageBitmap(bitmap);
        photoView.setLayoutParams(getLayoutParams(bitmap));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (isEditAble) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    float x = event.getX();
                    float y = event.getY();
                    if (x > 0 && x < photoView.getWidth() && y > 0 && y < photoView.getHeight()) {
                        int rawX = (int) (event.getX() + photoView.getLeft());
                        int rawY = (int) (event.getY() + photoView.getTop());
                        if (isFirst)
                            tipHelper.removeTip();
                        Log.e("bitmap", bitmap.getWidth() + " " + bitmap.getHeight());
                        Log.e("photoView", photoView.getWidth() + " " + photoView.getHeight());

                        float xPos = event.getX() / photoView.getWidth() * 100;
                        float yPos = event.getY() / photoView.getHeight() * 100;

                        RelativeLayout layout = (RelativeLayout) findViewById(R.id.edit_parent);
                        RemarkPopupWindow popupWindow = new RemarkPopupWindow(this, layout, rawX, rawY, xPos, yPos);
                        popupWindow.showAtLocation(findViewById(R.id.edit_parent), Gravity.CENTER_HORIZONTAL, 0, 150);

                    }

                    break;
                default:
                    break;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.edit_iv_cancel:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Cancel");
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(Declare.voicePath)) {
                            File file = new File(Declare.voicePath);        //delete the audio file if it exists
                            if (file.exists())
                                file.delete();
                        }
                        dialog.dismiss();
                        finish();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
                break;
            case R.id.edit_iv_save:
                if (Declare.type == Declare.TYPE_VOICE || Declare.type == Declare.TYPE_TEXT) {
                    JSONHelper jsonHelper = new JSONHelper(getApplicationContext());
                    JSONArray array = jsonHelper.getArray();
                    if (null == array)
                        array = new JSONArray();
                    JSONObject object = null;
                    if (Declare.type == Declare.TYPE_VOICE)
                        object = jsonHelper.setVoiceObject(photoPath);
                    else
                        object = jsonHelper.setTextObject(photoPath);
                    array.put(object);
                    jsonHelper.putArray(array);

                    Toast.makeText(getApplicationContext(), "Saving succeeded", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "the photo has not been edited", Toast.LENGTH_SHORT).show();
                }
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    private RelativeLayout.LayoutParams getLayoutParams(Bitmap bitmap) {
        float width, height;
        float rawWidth = bitmap.getWidth();
        float rawHeight = bitmap.getHeight();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int maxWidth = dm.widthPixels;
        int maxHeight = 954;        //from the beginning to the bottom
        float rawScale = rawWidth / rawHeight;
        float maxScale = ((float) maxWidth) / maxHeight;

        if (rawScale > maxScale) {
            if (rawWidth > maxWidth) {
                width = maxWidth;
                height = maxWidth / rawScale;
                Log.e("scale", "1");
            } else {
                width = rawWidth;
                height = rawHeight;
                Log.e("scale", "2");
            }
        } else {
            if (rawHeight > maxHeight) {
                height = maxHeight;
                width = rawScale * maxHeight;
                Log.e("scale", "3");
            } else {
                width = rawWidth;
                height = rawHeight;
                Log.e("scale", "4");
            }
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) width, (int) height);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.BELOW, R.id.edit_iv_save);
        params.topMargin = 40;
        return params;
    }
}
