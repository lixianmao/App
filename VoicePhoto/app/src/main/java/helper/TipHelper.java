package helper;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.unique.dalian.voicephoto.R;

/**
 * Created by dalian on 8/1/14.
 * when entering the EditActivity, judge whether it is the first time to be here...
 */
public class TipHelper {

    private ViewGroup layout;
    private TextView tv;
    private Context context;
    private SpHelper spHelper;

    public static final String KEY_FIRST = "IsFirst";

    public TipHelper(Context context, ViewGroup layout) {
        this.layout = layout;
        this.context = context;
        spHelper = new SpHelper(context);
    }

    public boolean getIsFirst() {
        //return true;
        return spHelper.getBoolean(KEY_FIRST, true);
    }

    public void addTip() {
        tv = new TextView(context);
        tv.setText("click to add description");
        tv.setTextSize(18);
        tv.setGravity(Gravity.CENTER);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(400, 100);
        params.addRule(RelativeLayout.ALIGN_TOP, R.id.edit_iv_photo);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        params.topMargin = 40;
        layout.addView(tv, params);

        spHelper.putBoolean(KEY_FIRST, false);
    }

    public void removeTip() {
        if (tv != null && layout != null)
            layout.removeView(tv);
    }
}
