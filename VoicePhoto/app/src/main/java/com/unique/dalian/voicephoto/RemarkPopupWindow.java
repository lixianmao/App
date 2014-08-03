package com.unique.dalian.voicephoto;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

/**
 * Created by dalian on 8/1/14.
 */
public class RemarkPopupWindow extends PopupWindow implements View.OnClickListener {

    private View popupView;
    private ImageView addVoiceView, addTextView;
    private Context context;
    private ViewGroup layout;
    private int x, y;

    public RemarkPopupWindow(Context context, ViewGroup layout, int x, int y) {
        super(context);
        this.context = context;
        this.layout = layout;
        this.x = x;
        this.y = y;

        LayoutInflater inflater = LayoutInflater.from(context);
        popupView = inflater.inflate(R.layout.popup_window_remark, null);

        addVoiceView = (ImageView) popupView.findViewById(R.id.popup_add_voice);
        addTextView = (ImageView) popupView.findViewById(R.id.popup_add_text);

        addVoiceView.setOnClickListener(this);
        addTextView.setOnClickListener(this);

        setContentView(popupView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.popup_add_voice:
                RecordHelper popupWindow = new RecordHelper(context);
                popupWindow.showAtLocation(layout, Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 300);
                break;
            case R.id.popup_add_text:
                MyEditText text = new MyEditText(context);
                text.setBackgroundResource(R.drawable.bg_edittext);
                text.setText("hello_world");

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(x, y, 0, 0);
                layout.addView(text, params);
                break;
            default:
                break;
        }
    }
}
