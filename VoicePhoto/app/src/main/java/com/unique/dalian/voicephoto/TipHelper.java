package com.unique.dalian.voicephoto;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by dalian on 8/1/14.
 * when entering the EditActivity, judge whether it is the first time to be here...
 */
public class TipHelper {
    public static final String NAME = "name_first";
    public static final String KEY = "key_first";

    private SharedPreferences sp;
    private ViewGroup layout;
    private TextView tv;
    private Context context;

    public TipHelper(Context context, ViewGroup layout) {
        this.layout = layout;
        this.context = context;
        sp = context.getSharedPreferences(NAME, Context.MODE_PRIVATE);
    }

    public boolean getIsFirst() {
        return true;
        //return sp.getBoolean(KEY, true);
    }

    public void setNotFirst() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY, false);
        //editor.commit();
        editor.apply();
    }

    public void addTip() {
        tv = new TextView(context);
        tv.setText("click to add description");
        tv.setTextSize(18);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(0x88000000);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
//                    LinearLayout.LayoutParams.WRAP_CONTENT);
//            params.gravity = Gravity.CENTER;
//            params.topMargin = 200;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(40, 300, 40, 300);
        layout.addView(tv, params);

        setNotFirst();
    }

    public void removeTip() {
        if (tv != null && layout != null)
            layout.removeView(tv);
    }

}
