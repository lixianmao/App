package helper;

import com.unique.dalian.voicephoto.MyEditText;
import com.unique.dalian.voicephoto.PlayHelper;

import java.util.List;

/**
 * Created by dalian on 8/9/14.
 */
public class Declare {

    //public static List<PointPos> posList;
    public static PlayHelper playHelper;
    public static List<MyEditText> textList;
    //public static String voicePath;
    public static int type;

    //the coordinate of the photo, make sure that the dynamic window within it
    public static int lEdge, tEdge, rEdge, bEdge;

    public static final int TYPE_VOICE = 1;
    public static final int TYPE_TEXT = 2;
    public static final int POPUP_REQUEST = 13;

    public static final String PHOTO_PATH = "photo_path";
    public static final String TYPE = "type";
    public static final String VOICE_PATH = "voice_path";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String CONTENT = "content";
    public static final String TEXT = "text";

    public static final String INTENT_PATH = "path";
    public static final String INTENT_POSITION = "position";
}
