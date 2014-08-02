package com.unique.dalian.voicephoto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.InputType;
import android.widget.EditText;

import org.w3c.dom.Text;

/**
 * Created by dalian on 8/2/14.
 */
public class TextHelper extends EditText {


    public TextHelper(Context context) {
        super(context);

        this.setBackgroundColor(0x00000000);
        this.setInputType(InputType.TYPE_CLASS_TEXT);
        this.setText("hello_world");
    }

    /**
     * set the color of the frame
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);         //hollow
        paint.setStrokeWidth(2);

        if (this.isFocused())
            paint.setColor(0xffffff);
        else
            paint.setColor(Color.rgb(0, 0, 0));
        RectF rectF = new RectF(getScrollX()+2, getScrollY()+2, getScrollX()+getWidth()-1, getScrollY()+getHeight()-3);
        canvas.drawRoundRect(rectF, 3, 3, paint);
        super.onDraw(canvas);
    }
}
