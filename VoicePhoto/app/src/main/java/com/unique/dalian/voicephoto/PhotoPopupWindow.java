package com.unique.dalian.voicephoto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

/**
 * Created by dalian on 7/31/14.
 */
public class PhotoPopupWindow extends PopupWindow {

    private View popupView;
    private ImageView fromAlbumView, fromCameraView;
    private Activity activity;
    public static final int POPUP_REQUEST = 13;

    public PhotoPopupWindow(Context context, Activity activity) {
        super(context);
        this.activity = activity;

        LayoutInflater inflater = LayoutInflater.from(context);
        popupView = (View) inflater.inflate(R.layout.popup_window_photo, null);

        fromAlbumView = (ImageView) popupView.findViewById(R.id.popup_from_album);
        fromCameraView = (ImageView) popupView.findViewById(R.id.popup_from_camera);

        fromAlbumView.setOnClickListener(clickListener);
        fromCameraView.setOnClickListener(clickListener);

        setContentView(popupView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        ColorDrawable drawable = new ColorDrawable(0x80000000);
        setBackgroundDrawable(drawable);
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getX();
                int y = (int) event.getY();
                if(y < popupView.getTop() || x < popupView.getLeft() || x > popupView.getRight())
                    dismiss();
                return true;
            }
        });


    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
            switch (v.getId()) {
                case R.id.popup_from_album:
                    Intent albumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    activity.startActivityForResult(albumIntent, POPUP_REQUEST);
                    break;
                case R.id.popup_from_camera:
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    activity.startActivityForResult(cameraIntent, POPUP_REQUEST);
                    break;
                default:
                    break;
            }
        }
    };
}
