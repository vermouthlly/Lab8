package com.example.dell.lab8;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.text.SimpleDateFormat;

public class MainActivity extends AppCompatActivity {

    private Button btn1;
    private TextView cur_time, state;
    private ImageView img;
    private SeekBar bar;
    private MusicService ms;
    private SimpleDateFormat time = new SimpleDateFormat("mm:ss");
    private boolean rotate = false; //旋转
    private int degree = 1;
    Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(ms.mp_state.equals("isplaying")) {
                rotate= true;
                bar.setEnabled(true);
                btn1.setText(R.string.pause);
                state.setText(R.string.playing);
            } else if (ms.mp_state.equals("isstopped")) {
                rotate= false;
                bar.setEnabled(false);
                btn1.setText(R.string.play);
                state.setText(R.string.stop);
            } else if (ms.mp_state.equals("ispaused")) {
                rotate= false;
                bar.setEnabled(true);
                btn1.setText(R.string.play);
                state.setText(R.string.pause);
            }
            cur_time.setText(time.format(ms.mplayer.getCurrentPosition()) + "/"
                    + time.format(ms.mplayer.getDuration()));
            bar.setProgress(ms.mplayer.getCurrentPosition());
            if (rotate) {
                img.setRotation(degree);
                degree++;
            }
            bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        ms.mplayer.seekTo(seekBar.getProgress());
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }
                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            mHandler.postDelayed(mRunnable, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1= (Button) findViewById(R.id.pp);
        Button btn2= (Button) findViewById(R.id.stop);
        Button btn3= (Button) findViewById(R.id.quit);
        cur_time= (TextView) findViewById(R.id.current_time);
        state= (TextView) findViewById(R.id.state);
        img= (ImageView) findViewById(R.id.image);
        bar= (SeekBar) findViewById(R.id.seekbar);
        ms = new MusicService();
        bar.setMax(ms.mplayer.getDuration());

        Intent intent = new Intent(this, MusicService.class);
        startService(intent);
        bindService(intent, sc, BIND_AUTO_CREATE);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state.getText().toString().equals("Stopped")) {
                    bar.setProgress(0);
                    ms.mplayer.seekTo(0);
                }
                if (btn1.getText().toString().equals("PAUSE")) {
                    ms.pause();
                    bar.setEnabled(true);
                    rotate= false;
                    btn1.setText(R.string.play);
                    state.setText(R.string.pause);
                } else if (btn1.getText().toString().equals("PLAY")) {
                    ms.play();
                    bar.setEnabled(true);
                    rotate= true;
                    btn1.setText(R.string.pause);
                    state.setText(R.string.playing);
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate= false;
                ms.stop();
                bar.setEnabled(false);
                btn1.setText(R.string.play);
                state.setText(R.string.stop);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(mRunnable);
                unbindService(sc);
                try {
                    finish();
                    System.exit(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mHandler.post(mRunnable);
    }

    private ServiceConnection sc = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ms = ((MusicService.MyBinder)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            ms = null;
        }
    };

    @Override
    protected void onDestroy() {
        unbindService(sc);
        super.onDestroy();
    }
}