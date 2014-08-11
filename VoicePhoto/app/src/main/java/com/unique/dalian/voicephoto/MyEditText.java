package com.unique.dalian.voicephoto;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Editable;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;

import org.w3c.dom.Text;

/**
 * Created by dalian on 8/2/14.
 */
public class MyEditText extends EditText {

    public MyEditText(Context context) {
        super(context);

        this.setText("hello_world");
        this.setBackgroundResource(R.drawable.bg_edittext);
        this.setTextSize(12);
        this.setMaxWidth(300);
        this.setMaxLines(4);
    }

    public String getMyText() {
        return this.getText().toString();
    }

}
