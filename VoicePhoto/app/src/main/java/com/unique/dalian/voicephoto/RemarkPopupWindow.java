package com.unique.dalian.voicephoto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import helper.Declare;

/**
 * Created by dalian on 8/1/14.
 */
public class RemarkPopupWindow extends PopupWindow implements View.OnClickListener {

    private View popupView;
    private ImageView addVoiceView, addTextView;
    private Context context;
    private ViewGroup layout;
    private int x, y;
    private float xPos, yPos;

    /**
     * @param context
     * @param layout  the parent view that contains this PopupWindow
     * @param x       x coordinate to the layout
     * @param y       y coordinate to the layout
     * @param xPos    x coordinate to the image, in percent
     * @param yPos    y coordinate to the image, in percent
     */
    public RemarkPopupWindow(Context context, ViewGroup layout, int x, int y, float xPos, float yPos) {
        super(context);
        this.context = context;
        this.layout = layout;
        this.x = x;
        this.y = y;
        this.xPos = xPos;
        this.yPos = yPos;

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
                RecordHelper recordHelper = new RecordHelper(context, layout, x, y, xPos, yPos);
                RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        400);
                param.addRule(RelativeLayout.CENTER_HORIZONTAL);
                param.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                layout.addView(recordHelper, param);
                break;
            case R.id.popup_add_text:
                MyEditText text = new MyEditText(context, layout);
                text.setBackgroundResource(R.drawable.bg_edittext);
                text.setText("hello_world");
                text.setPos(xPos, yPos);

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(x, y, 0, 0);
                layout.addView(text, params);

                Declare.type = Declare.TYPE_TEXT;
                Declare.textList.add(text);
                break;
            default:
                break;
        }
    }
}
