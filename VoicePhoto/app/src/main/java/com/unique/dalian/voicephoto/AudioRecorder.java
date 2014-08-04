package com.unique.dalian.voicephoto;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by dalian on 8/1/14.
 */
public class AudioRecorder {

    private MediaRecorder recorder;
    private Context context;
    private MediaPlayer player;
    private List<File> fileList = new ArrayList<File>();
    private File finalFile;
    private File dir;
    private int segment = 1;


    public AudioRecorder(Context context) {
        this.context = context;
    }

    private void init() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            dir = new File(Environment.getExternalStorageDirectory(), "VoicePhoto/audio");
            if (!dir.exists())
                dir.mkdirs();
        } else {
            Toast.makeText(context, "sdCard is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        recorder = new MediaRecorder();
        try {
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            File tmpFile = new File(dir, new Date().getTime() + "_" + segment + ".amr");
            if (!tmpFile.exists()) {
                try {
                    tmpFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            fileList.add(tmpFile);
            segment++;
            recorder.setOutputFile(tmpFile.getAbsolutePath());

        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }


    public void start() {
        init();

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
        Toast.makeText(context, "recoriding ...", Toast.LENGTH_SHORT).show();
    }

    public void pause() {
        if (null != recorder) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }

    }

    public void save() {
        pause();

        finalFile = new File(dir, new Date().getTime() + ".amr");
        if (!finalFile.exists()) {
            try {
                finalFile.createNewFile();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(finalFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < fileList.size(); i++) {
            File tmpFile = fileList.get(i);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(tmpFile);
                byte[] tmpBytes = new byte[fis.available()];
                int length = tmpBytes.length;

                if (i == 0) {
                    while (fis.read(tmpBytes) != -1)
                        fos.write(tmpBytes, 0, length);
                } else {
                    while (fis.read(tmpBytes) != -1)
                        fos.write(tmpBytes, 6, length - 6);
                }

                fos.flush();
                fis.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fis = null;
            }

        }

        if (fos != null) {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                fos = null;
            }
        }

        for (File file : fileList)
            file.delete();
        fileList.clear();
        segment = 1;
        Toast.makeText(context, "saved in" + finalFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }

    public void delete() {
        pause();

        if (fileList != null && fileList.size() != 0) {
            for (File file : fileList)
                file.delete();
            fileList.clear();
            segment = 1;
        }
    }

    public void play() {
        player = new MediaPlayer();
        try {
            player.setDataSource(context, Uri.fromFile(finalFile));
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
