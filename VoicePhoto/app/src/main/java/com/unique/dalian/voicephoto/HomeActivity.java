package com.unique.dalian.voicephoto;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import helper.Declare;


public class HomeActivity extends Activity implements View.OnClickListener {

    private ImageView addImageView, savedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        addImageView = (ImageView) findViewById(R.id.home_iv_add);
        savedImageView = (ImageView) findViewById(R.id.home_iv_saved);

        addImageView.setOnClickListener(this);
        savedImageView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_iv_add:
                PhotoPopupWindow popupWindow = new PhotoPopupWindow(getApplicationContext(), this);
                popupWindow.showAtLocation(findViewById(R.id.home_parent), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 60);
                break;
            case R.id.home_iv_saved:
                Intent intent = new Intent(this, SeeActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == Declare.POPUP_REQUEST && data != null) {
            Uri photoUri = data.getData();
            if (null == photoUri) {
                Toast.makeText(this, "access photo failed", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, EditActivity.class);
                intent.setData(photoUri);
                startActivity(intent);
                if (Build.VERSION.SDK_INT > 5)       //activity skip animation
                    overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        }
    }
}
