package com.fengyu.musicplayer;

import java.io.IOException;
import java.io.Serializable;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class MusicPlayerService extends Service implements Serializable
{
	private final IBinder mBinder = new LocalBinder();
    
    private MediaPlayer mMediaPlayer = null;
        
//    public static final String PLAYER_PREPARE_END = "com.yarin.musicplayerservice.prepared";
//    public static final String PLAY_COMPLETED = "com.yarin.musicplayerservice.playcompleted";
    public static final String PLAYER_PREPARE_END = "prepared";
    public static final String PLAY_COMPLETED = "playcompleted";
    
    MediaPlayer.OnCompletionListener mCompleteListener = new MediaPlayer.OnCompletionListener() 
    {
        public void onCompletion(MediaPlayer mp) 
        {
            broadcastEvent(PLAY_COMPLETED);
        }
    };
    
    MediaPlayer.OnPreparedListener mPrepareListener = new MediaPlayer.OnPreparedListener() 
    {
        public void onPrepared(MediaPlayer mp) 
        {   
            broadcastEvent(PLAYER_PREPARE_END);
        }
    };
    
        
    private void broadcastEvent(String what)
	{
		Intent i = new Intent(what);
		sendBroadcast(i);
	}


	public void onCreate()
	{
		super.onCreate();

		Log.i("inout", "Create");
		mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setOnPreparedListener(mPrepareListener);
		mMediaPlayer.setOnCompletionListener(mCompleteListener);
	}

	public class LocalBinder extends Binder
	{
		public MusicPlayerService getService()
		{
			return MusicPlayerService.this;
		}
	}


	public IBinder onBind(Intent intent)
	{
		Log.i("inout", "onBind");
		return mBinder;
	}


	public void setDataSource(String path)
	{

		System.out.println(path);
		try
		{
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(path);
			mMediaPlayer.prepare();
		}
		catch (IOException e)
		{
			return;
		}
		catch (IllegalArgumentException e)
		{
			return;
		}
	}


	public void start()
	{
		Log.i("inout", "start");
		mMediaPlayer.start();
	}


	public void stop()
	{
		mMediaPlayer.stop();
	}


	public void pause()
	{
		mMediaPlayer.pause();
	}


	public boolean isPlaying()
	{
		return mMediaPlayer.isPlaying();
	}


	public int getDuration()
	{
		return mMediaPlayer.getDuration();
	}


	public int getPosition()
	{
		return mMediaPlayer.getCurrentPosition();
	}


	public long seek(long whereto)
	{
		mMediaPlayer.seekTo((int) whereto);
		return whereto;
	}
}

