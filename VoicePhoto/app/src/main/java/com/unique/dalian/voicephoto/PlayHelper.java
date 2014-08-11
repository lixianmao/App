package com.unique.dalian.voicephoto;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.unique.dalian.voicephoto.R;

import java.io.IOException;

/**
 * Created by dalian on 8/10/14.
 */
public class PlayHelper extends LinearLayout {

    private Context context;
    private String path;
    private Button doBtn;
    private TextView curView, totalView;
    private MediaPlayer player;
    private boolean isPlaying;
    private PlayThread playThread;

    public PlayHelper(Context context, String path) {
        super(context);
        this.context = context;
        this.path = path;

        LayoutInflater.from(context).inflate(R.layout.layout_play, this);
        doBtn = (Button) findViewById(R.id.play_do);
        curView = (TextView) findViewById(R.id.play_time_cur);
        totalView = (TextView) findViewById(R.id.play_time_total);

        doBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPlaying) {
                    isPlaying = false;
                    doBtn.setText("start");
                    curView.setText("00:00");
                    endPlay();
                    stopThread();
                } else {
                    isPlaying = true;
                    doBtn.setText("pause");
                    play();
                    startThread();
                }
            }
        });

        playThread = new PlayThread();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                int total = msg.arg1;
                int cur = msg.arg2;
                curView.setText(formatTime(cur));
                totalView.setText(formatTime(total));
            }
        }
    };

    class PlayThread implements Runnable {
        boolean vRun = true;

        public void stopThread() {
            vRun = false;
        }
        public void startThread() {
            vRun = true;
        }

        @Override
        public void run() {
            while (vRun) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (isPlaying) {
                    int total = player.getDuration() / 1000;
                    int cur = player.getCurrentPosition() / 1000;
                    Message msg = new Message();
                    msg.what = 2;
                    msg.arg1 = total;
                    msg.arg2 = cur;
                    handler.sendMessage(msg);
                }
            }
        }
    }

    private void stopThread() {
        if (playThread != null) {
            playThread.stopThread();
        }
        if (handler != null) {
            handler.removeCallbacks(playThread);
        }
    }
    private void startThread() {
        if(playThread!=null) {
            playThread.startThread();
            new Thread(playThread).start();
        }
    }

    private String formatTime(int timeInSec) {
        int min = timeInSec / 60;
        int sec = timeInSec % 60;
        String minute = min < 10 ? "0" + min : min + "";
        String second = sec < 10 ? "0" + sec : sec + "";
        return minute + ":" + second;
    }

    private void play() {
        player = new MediaPlayer();
        try {
            player.setDataSource(context, Uri.parse(path));
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    isPlaying = false;
                    doBtn.setText("start");
                    curView.setText("00:00");
                    stopThread();
                }
            });
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void endPlay() {
        if (null == player)
            return;
        try {
            player.stop();
            player.release();
            player = null;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }
}
