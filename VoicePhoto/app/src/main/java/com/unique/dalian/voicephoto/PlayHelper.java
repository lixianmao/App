package com.unique.dalian.voicephoto;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;

import helper.Declare;

/**
 * Created by dalian on 8/10/14.
 */
public class PlayHelper extends LinearLayout {

    private Context context;
    private Button doBtn;
    private TextView curView, totalView;
    private MediaPlayer player;
    private boolean isPlaying;
    private PlayThread playThread;
    public String path;

    private ImageView deleteView;
    private ViewGroup layout;

    public PlayHelper(Context context, String path, ViewGroup layout) {
        super(context);
        this.context = context;
        this.path = path;
        this.layout = layout;

        initView();
    }

    public PlayHelper(Context context, String path) {
        super(context);
        this.context = context;
        this.path = path;

        initView();
    }

    private void initView() {
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
        if (playThread != null) {
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
                    endPlay();
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

    public float xPos;
    public float yPos;

    public void setPos(float xPos, float yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
    }

    private int previousX, previousY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int currentX = (int) event.getX();
        final int currentY = (int) event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                previousX = currentX;
                previousY = currentY;
                break;
            case MotionEvent.ACTION_MOVE:
                final int left = getLeft();
                final int top = getTop();
                final int width = getWidth();
                final int height = getHeight();
                int deltaX = currentX - previousX;
                int deltaY = currentY - previousY;
                int l = left + deltaX, t = top + deltaY;
                if (l < Declare.lEdge) l = Declare.lEdge;
                if (l + width > Declare.rEdge) l = Declare.rEdge - width;
                if (t < Declare.tEdge) t = Declare.tEdge;
                if (t + height > Declare.bEdge) t = Declare.bEdge - height;
                if (deltaX != 0 || deltaY != 0) {
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, height);
                    params.setMargins(l, t, 0, 0);
                    setLayoutParams(params);
                    if (deleteView != null) {
                        RelativeLayout.LayoutParams delParams = new RelativeLayout.LayoutParams(25, 25);
                        delParams.setMargins(l - 12, t - 12, 0, 0);
                        deleteView.setLayoutParams(delParams);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                this.xPos = (float) (getLeft() - Declare.lEdge) / (Declare.rEdge - Declare.lEdge) * 100;
                this.yPos = (float) (getTop() - Declare.tEdge) / (Declare.bEdge - Declare.tEdge) * 100;
                break;
            default:
                break;
        }
        return true;
        //return super.onTouchEvent(event);
    }

    public void setImage() {
        deleteView = new ImageView(context);
        deleteView.setImageResource(R.drawable.delete);
        deleteView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(25, 25);
        params.setMargins(getLeft() - 12, getTop() - 12, 0, 0);
        deleteView.setLayoutParams(params);
        deleteView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                layout.removeView(deleteView);
                deleteView = null;
                layout.removeView(getView());
                Declare.playHelper = null;
            }
        });
        layout.addView(deleteView);
    }

    public void removeImage() {
        if (deleteView != null) {
            layout.removeView(deleteView);
            deleteView = null;
        }
    }

    private View getView() {
        return this;
    }
}
