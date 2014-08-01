package com.unique.dalian.voicephoto;


import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by dalian on 8/1/14.
 */
public class AudioRecorder {

    private MediaRecorder recorder;
    private Context context;
    private String filePath;
    private MediaPlayer player;


    public AudioRecorder(Context context, String filePath) {
        this.context = context;
        this.filePath = filePath;
    }

    public void init() {
        recorder = new MediaRecorder();
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(filePath);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    public void start() {
        if (null == recorder) {
            Toast.makeText(context, "start record error", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (null == recorder) {
            Toast.makeText(context, "stop record error", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            recorder.stop();
            recorder.release();
            recorder = null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void release() {
        if (null == recorder)
            return;

        recorder.release();
        ;
        recorder = null;
    }

    public void reset() {
        if (null == recorder)
            return;

        recorder.reset();
    }

    public void play() {
        player = new MediaPlayer();
        try {
            player.setDataSource(context, Uri.parse(filePath));
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void endPlay() {
        if (null == player) {
            Toast.makeText(context, "end play error", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            player.stop();
            player.release();
            player = null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }
}
