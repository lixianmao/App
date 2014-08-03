package com.unique.dalian.voicephoto;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


public class EditActivity extends Activity implements View.OnTouchListener, View.OnClickListener {

    private ImageView photoView, cancelView, saveView;
    private Bitmap bitmap;
    private boolean isEditAble = true;
    private TipHelper tipHelper;
    private boolean isFirst;

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
    }

    private void showPhoto() {
        Intent intent = getIntent();
        Uri data = intent.getData();

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(data, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String path = cursor.getString(columnIndex);
        cursor.close();

        bitmap = BitmapFactory.decodeFile(path);
        photoView.setImageBitmap(bitmap);
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
                    int x = (int) event.getX();
                    int y = (int) event.getY();

                    if (isFirst)
                        tipHelper.removeTip();

                    RelativeLayout layout = (RelativeLayout) findViewById(R.id.edit_parent);
                    RemarkPopupWindow popupWindow = new RemarkPopupWindow(this, layout, x, y);
                    popupWindow.showAtLocation(findViewById(R.id.edit_parent), Gravity.CENTER_HORIZONTAL, 0, 150);
                    //isEditAble = false;
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
                //add code here
                finish();
                break;
            case R.id.edit_iv_save:
                //add code here
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
}
