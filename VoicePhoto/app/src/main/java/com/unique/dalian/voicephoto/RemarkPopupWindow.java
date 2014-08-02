package com.unique.dalian.voicephoto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    public RemarkPopupWindow(Context context, ViewGroup layout) {
        super(context);
        this.context = context;
        this.layout = layout;

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
        popupView.setOnTouchListener(touchListener);
    }

    @Override
    public void onClick(View v) {
        dismiss();
        switch (v.getId()) {
            case R.id.popup_add_voice:

                break;
            case R.id.popup_add_text:
                TextHelper text = new TextHelper(context);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(200, 200, 200, 200);
                layout.addView(text, params);
                break;
            default:
                break;
        }
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getY() > popupView.getBottom() || event.getY() < popupView.getTop()
                    || event.getX() < popupView.getLeft() || event.getX() > popupView.getRight())
                dismiss();
            return true;
        }
    };
}
