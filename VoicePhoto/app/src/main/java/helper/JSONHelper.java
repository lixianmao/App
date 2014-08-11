package helper;

import android.content.Context;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by dalian on 8/9/14.
 */
public class JSONHelper {

    private SpHelper spHelper;

    public static final String KEY_JSON = "JSON";

    public JSONHelper(Context context) {
        spHelper = new SpHelper(context);
    }

    public JSONArray getArray() {
        String jsonString = spHelper.getString(KEY_JSON, "");
        if (!TextUtils.isEmpty(jsonString))
            try {
                JSONArray array = new JSONArray(jsonString);
                return array;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return null;
    }

    public void putArray(JSONArray array) {
        spHelper.putString(KEY_JSON, array.toString());
    }

    public JSONArray putObject(JSONArray array, int i, JSONObject object) {
        try {
            return array.put(i, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getObject(JSONArray array, int i) {
        if (i > -1 && i < array.length())
            try {
                JSONObject object = (JSONObject) array.get(i);
                return object;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        return null;
    }

    public JSONObject setVoiceObject(String photoPath) {
        JSONObject voiceObject = new JSONObject();
        try {
            voiceObject.put(Declare.PHOTO_PATH, photoPath);
            voiceObject.put(Declare.TYPE, Declare.TYPE_VOICE);
            JSONObject content = new JSONObject();
            content.put(Declare.VOICE_PATH, Declare.voicePath);
            content.put(Declare.X, Declare.posList.get(0).xPos);
            content.put(Declare.Y, Declare.posList.get(0).yPos);
            voiceObject.put(Declare.CONTENT, content);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return voiceObject;
    }

    public JSONObject setTextObject(String photoPath) {
        JSONObject textObject = new JSONObject();
        try {
            textObject.put(Declare.PHOTO_PATH, photoPath);
            textObject.put(Declare.TYPE, Declare.TYPE_TEXT);
            JSONArray array = new JSONArray();
            for (int i = 0; i < Declare.posList.size(); i++) {
                JSONObject content = new JSONObject();
                content.put(Declare.TEXT, Declare.textList.get(i).getMyText());
                content.put(Declare.X, Declare.posList.get(i).xPos);
                content.put(Declare.Y, Declare.posList.get(i).yPos);
                array.put(content);
            }
            textObject.put(Declare.CONTENT, array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return textObject;
    }

    public void deleteObject(JSONArray array, int position) {
        JSONArray newArray = new JSONArray();
        for (int i = 0; i < array.length() && i != position; i++) {
            try{
                newArray.put(array.get(i));
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
        putArray(newArray);
    }
}
