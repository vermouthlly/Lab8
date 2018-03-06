package com.example.dell.lab8;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by dell on 2017/11/23.
 */


public class MusicService extends Service{

    public MediaPlayer mplayer= new MediaPlayer();
    public final IBinder binder = new MyBinder();
    public String mp_state="empty";
    public int mp_degree= 1;

    public MusicService() {
        try {
            mplayer.setDataSource("/data/lucifer.mp3");
            mplayer.prepare();
            mplayer.setLooping(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop()
    {
        mplayer.pause();
        mp_state= "isstopped";
    }

    public void play() {
        mplayer.start();
        mp_state = "isplaying";
    }

    public void pause() {
        mplayer.pause();
        mp_state= "ispaused";
    }


    public IBinder onBind(Intent intent)
    {
        return this.binder;
    }

    public class MyBinder extends Binder
    {
        MusicService getService()
        {
            return MusicService.this;
        }
    }
}