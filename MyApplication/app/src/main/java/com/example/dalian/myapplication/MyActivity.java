package com.example.dalian.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;


public class MyActivity extends Activity implements View.OnClickListener {

    private Button beginBtn;
    private Button stopBtn;
    private MediaRecorder recorder;
    private boolean isRecording;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        beginBtn = (Button) findViewById(R.id.btn_begin);
        stopBtn = (Button) findViewById(R.id.btn_stop);

        beginBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);

        stopBtn.setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_begin:
                if (initRecorder()) {
                    startRecord();
                }
                break;
            case R.id.btn_stop:
                stopRecord();
                if(null != file) {
                    //playRecord(file);
                    play(file);
                }
                break;
            default:
                break;
        }
    }

    private boolean initRecorder() {
        isRecording = false;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File dir = new File(Environment.getExternalStorageDirectory(), "record_audio");
            if (!dir.exists())
                dir.mkdir();
            try {
                file = new File(dir, "rec.amr");
                if (!file.exists()) {
                    file.createNewFile();
                }
                recorder.setOutputFile(file.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }


            return true;
        }
        return false;
    }

    private void startRecord() {
        try {
            recorder.prepare();
            recorder.start();
            beginBtn.setEnabled(false);
            stopBtn.setEnabled(true);
            isRecording = true;
        } catch (IOException e) {
            Log.e("start", "failed");
            e.printStackTrace();
        }
    }

    private void stopRecord() {
        if (isRecording) {
            recorder.stop();
            recorder.release();
            recorder = null;
            isRecording = false;
            beginBtn.setEnabled(true);
            stopBtn.setEnabled(false);
        }
    }

    private void playRecord(File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "audio");
        startActivity(intent);
    }

    private void play(File file) {
        MediaPlayer player = new MediaPlayer();
        try{
            player.setDataSource(this, Uri.fromFile(file));
            player.prepare();
            player.start();
        }catch (IOException e) {
            Log.e("player", "failed");
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        if (isRecording) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        super.onDestroy();
    }
}
