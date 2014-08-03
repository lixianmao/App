package com.unique.dalian.voicephoto;

import android.content.Context;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

/**
 * Created by dalian on 8/3/14.
 */
public class RecordHelper extends PopupWindow {

    private Context context;
    private View popupView;
    private TextView timeView;
    private ImageButton doImgButton;
    private Button deleteButton, saveButton;
    private AudioRecorder recorder;

    public boolean isRecording;

    public RecordHelper(Context context) {
        super(context);
        this.context = context;

        LayoutInflater inflater = LayoutInflater.from(context);
        popupView = inflater.inflate(R.layout.layout_record, null);
        timeView = (TextView) popupView.findViewById(R.id.record_time);
        doImgButton = (ImageButton) popupView.findViewById(R.id.record_do);
        deleteButton = (Button) popupView.findViewById(R.id.record_delete);
        saveButton = (Button) popupView.findViewById(R.id.record_save);

        doImgButton.setOnClickListener(clickListener);
        saveButton.setOnClickListener(clickListener);
        deleteButton.setOnClickListener(clickListener);

        this.setContentView(popupView);
        this.setFocusable(true);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        recorder = new AudioRecorder(context);
    }



    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.record_do:
                    if(isRecording) {
                        isRecording = false;
                        doImgButton.setBackgroundColor(0xff0000);
                        recorder.pause();
                    } else{
                        isRecording = true;
                        doImgButton.setBackgroundColor(0x00ff00);
                        recorder.start();
                    }
                    break;
                case R.id.record_save:
                    isRecording = false;
                    doImgButton.setBackgroundColor(0xff0000);
                    recorder.stop();
                    break;
                case R.id.record_delete:
                    recorder.delete();
                    break;
                default:
                    break;
            }
        }
    };
}
