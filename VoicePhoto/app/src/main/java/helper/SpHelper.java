package helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by dalian on 8/7/14.
 */
public class SpHelper {

    private SharedPreferences sp;

    public SpHelper(Context context) {
        sp = context.getSharedPreferences("VoicePhoto", Context.MODE_PRIVATE);
    }

    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key, Boolean defValue) {
        return sp.getBoolean(key, defValue);
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }
}
