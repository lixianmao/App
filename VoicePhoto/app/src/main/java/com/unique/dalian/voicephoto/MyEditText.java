package com.unique.dalian.voicephoto;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import helper.Declare;

/**
 * Created by dalian on 8/2/14.
 */
public class MyEditText extends EditText {

    public float xPos;
    public float yPos;
    private int previousX, previousY;
    private ImageView deleteView;
    private ViewGroup layout;

    public MyEditText(Context context, ViewGroup layout) {
        super(context);
        this.layout = layout;
        this.setText("hello_world");
        this.setBackgroundResource(R.drawable.bg_edittext);
        this.setTextSize(12);
        this.setMaxWidth(300);
        this.setMaxLines(4);
        this.setEnabled(false);
        this.setClickable(false);
    }

    public void setPos(float xPos, float yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }


    public String getMyText() {
        return this.getText().toString();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int currentX = (int) event.getX();
        final int currentY = (int) event.getY();
        if (isEnabled()) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    previousX = currentX;
                    previousY = currentY;
                    break;
                case MotionEvent.ACTION_MOVE:
                    final int left = getLeft();
                    final int top = getTop();
                    final int width = getWidth();
                    final int height = getHeight();
                    int deltaX = currentX - previousX;
                    int deltaY = currentY - previousY;
                    int l = left + deltaX, t = top + deltaY;
                    if (l < Declare.lEdge) l = Declare.lEdge;
                    if (l + width > Declare.rEdge) l = Declare.rEdge - width;
                    if (t < Declare.tEdge) t = Declare.tEdge;
                    if (t + height > Declare.bEdge) t = Declare.bEdge - height;
                    if (deltaX != 0 || deltaY != 0) {
                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
                        params.setMargins(l, t, 0, 0);
                        setLayoutParams(params);
                        if (deleteView != null) {
                            RelativeLayout.LayoutParams delParams = new RelativeLayout.LayoutParams(25, 25);
                            delParams.setMargins(l - 12, t - 12, 0, 0);
                            deleteView.setLayoutParams(delParams);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    this.xPos = (float) (getLeft() - Declare.lEdge) / (Declare.rEdge - Declare.lEdge) * 100;
                    this.yPos = (float) (getTop() - Declare.tEdge) / (Declare.bEdge - Declare.tEdge) * 100;
                    break;
                default:
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    public void setImage() {
        deleteView = new ImageView(getContext());
        deleteView.setImageResource(R.drawable.delete);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(25, 25);
        params.setMargins(getLeft() - 12, getTop() - 12, 0, 0);
        deleteView.setLayoutParams(params);
        deleteView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        deleteView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(deleteView);
                deleteView = null;
                layout.removeView(getView());
                Declare.textList.remove(getView());
            }
        });
        layout.addView(deleteView);
    }

    public void removeImage() {
        if (deleteView != null) {
            layout.removeView(deleteView);
            deleteView = null;
        }
    }

    private View getView() {
        return this;
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {


    }
}
