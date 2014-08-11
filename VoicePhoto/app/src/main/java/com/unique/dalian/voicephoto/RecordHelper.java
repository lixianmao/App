package com.unique.dalian.voicephoto;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import helper.PointPos;

import com.unique.dalian.voicephoto.PlayHelper;
import com.unique.dalian.voicephoto.R;

import helper.RecordEngine;
import helper.Declare;

/**
 * Created by dalian on 8/3/14.
 */
public class RecordHelper extends RelativeLayout {

    private Context context;
    private ViewGroup layout;
    private TextView timeView;
    private ImageButton doImgButton;
    private Button deleteButton, saveButton;
    private RecordEngine recorder;
    private TimeThread timeThread;

    public boolean isRecording;
    public int timeInSec = 0;

    private int x, y;
    private float xPos, yPos;

    public RecordHelper(Context context, ViewGroup layout, int x, int y, float xPos, float yPos) {
        super(context);
        this.context = context;
        this.layout = layout;
        this.x = x;
        this.y = y;
        this.xPos = xPos;
        this.yPos = yPos;

        LayoutInflater.from(context).inflate(R.layout.layout_record, this);
        this.setBackgroundColor(0x88000000);
        timeView = (TextView) findViewById(R.id.record_time);
        doImgButton = (ImageButton) findViewById(R.id.record_do);
        deleteButton = (Button) findViewById(R.id.record_delete);
        saveButton = (Button) findViewById(R.id.record_save);

        doImgButton.setOnClickListener(clickListener);
        saveButton.setOnClickListener(clickListener);
        deleteButton.setOnClickListener(clickListener);
        timeView.setText("00:00");

        recorder = new RecordEngine(context);
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
                    doImgButton.setBackgroundColor(Color.RED);
                    recorder.save();
                    dismiss();

                    Declare.type = Declare.TYPE_VOICE;
                    Declare.posList.add(new PointPos(xPos, yPos));
                    String voicePath = recorder.getPath();
                    Declare.voicePath = voicePath;

                    PlayHelper playHelper = new PlayHelper(context, voicePath);
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(x, y, 0, 0);
                    layout.addView(playHelper, params);
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

    private void dismiss() {
        stopThread();
        layout.removeView(this);
    }
}
