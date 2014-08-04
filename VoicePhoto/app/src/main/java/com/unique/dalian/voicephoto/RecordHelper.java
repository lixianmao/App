package com.unique.dalian.voicephoto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
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
import java.util.logging.Handler;
import java.util.logging.LogRecord;

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
    private TimeThread timeThread;

    public boolean isRecording;
    public int timeInSec = 0;

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
        timeView.setText("00:00");
        this.setContentView(popupView);
        this.setFocusable(true);
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);

        recorder = new AudioRecorder(context);
        timeThread = new TimeThread();
        new Thread(timeThread).start();
    }


    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.record_do:
                    if (isRecording) {
                        isRecording = false;
                        doImgButton.setBackgroundColor(Color.RED);
                        recorder.pause();
                    } else {
                        isRecording = true;
                        doImgButton.setBackgroundColor(Color.GREEN);
                        recorder.start();
                    }
                    break;
                case R.id.record_save:
                    isRecording = false;
                    doImgButton.setBackgroundColor(0xff0000);
                    recorder.save();
                    dismiss();
                    stopThread();
                    break;
                case R.id.record_delete:
                    isRecording = false;
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("File won't be saved").setTitle("Delete");
                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            recorder.delete();
                            dialog.dismiss();
                            dismiss();
                            stopThread();
                        }
                    });
                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.create().show();
                    break;
                default:
                    break;
            }
        }
    };

    android.os.Handler handler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Log.e("handler", "receive");
                int min = timeInSec / 60;
                int sec = timeInSec % 60;
                String minute = min < 10 ? "0" + min : min + "";
                String second = sec < 10 ? "0" + sec : sec + "";
                Log.e("time", minute + ":" + second);
                timeView.setText(minute + ":" + second);
            }
        }
    };

    class TimeThread implements Runnable {
        boolean vRun = true;

        public void stopThread() {
            vRun = false;
        }

        @Override
        public void run() {
            while (vRun) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (isRecording) {
                    timeInSec++;
                    handler.sendEmptyMessage(1);
                }
            }
        }
    }

    private void stopThread() {
        if (timeThread != null)
            timeThread.stopThread();
        if (handler != null)
            handler.removeCallbacks(timeThread);
    }
}
