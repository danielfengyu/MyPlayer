package com.fengyu.musicui;

import com.fengyu.musicplayer.MusicInfoController;
import com.fengyu.musicplayer.MusicList;
import com.fengyu.musicplayer.MusicPlayerApp;
import com.fengyu.musicplayer.MusicPlayerService;
import com.fengyu.myplayer.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class MusicPlayActivity extends Activity {
	
		//�����
		 ImageView img;  
		 private ImageButton mPlayPauseButton = null;
		 private ImageButton mStopButton = null;
		 private ImageButton mPreviousButton=null;
		 private ImageButton mNextButton=null;
		 private TextView mTextTime=null;
		 //�㲥��Ϣ
	    protected BroadcastReceiver mPlayerEvtReceiver = new BroadcastReceiver() {
	        @Override
	        public void onReceive(Context context, Intent intent) {
	            String action = intent.getAction();
	            Log.i("inout", action);
	            if (action.equals(MusicPlayerService.PLAYER_PREPARE_END)) {
	                // will begin to play
	               // mTextView.setVisibility(View.INVISIBLE);
	                mPlayPauseButton.setVisibility(View.VISIBLE);
	                mStopButton.setVisibility(View.VISIBLE);
	                mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.music_play_pause));
	            } else if(action.equals(MusicPlayerService.PLAY_COMPLETED)) {
	            	  mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.music_play_play));
	            }
	        }
	    };
	    
	 public void onCreate(Bundle savedInstanceState) {
		 
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.musicplay);
	        mPlayPauseButton = (ImageButton) findViewById(R.id.play_pause_btn);
	        mStopButton = (ImageButton) findViewById(R.id.play_stop_btn);
	        mPreviousButton = (ImageButton) findViewById(R.id.play_previous);
	        mNextButton = (ImageButton) findViewById(R.id.play_next);
	        
	       
	        String url;
	        Bundle bundle;
	        bundle=getIntent().getExtras();
			url=bundle.getString("filepath");
			
	        if(MusicList.mMusicPlayerService == null){
	        	return;
	        }
	        MusicList.mMusicPlayerService.setDataSource(url);
	        MusicList.mMusicPlayerService.start();
	        
	        /**
	         * ��һ��
	         */
	        mPreviousButton.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//MusicList.mPosition=MusicList.mPosition-1;
					//���ǵ�һ�ף�������ǰһ��
					if(MusicList.mCursor.moveToPrevious())
					{
						 String url = MusicList.mCursor
			                       .getString(MusicList.mCursor
			                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
						 if(MusicList.mMusicPlayerService == null){
					        	return;
					        }
						 if (MusicList.mMusicPlayerService != null ) {
			                    MusicList.mMusicPlayerService.stop();
			                }
					        MusicList.mMusicPlayerService.setDataSource(url);
					        MusicList.mMusicPlayerService.start();
					}
					else {//�ǵ�һ�ף��������һ��
						  MusicList.mCursor.moveToPosition(MusicList.mCursor.getCount()-1);
						  String url = MusicList.mCursor
			                       .getString(MusicList.mCursor
			                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
						 if(MusicList.mMusicPlayerService == null){
					        	return;
					        }
						 if (MusicList.mMusicPlayerService != null ) {
			                    MusicList.mMusicPlayerService.stop();
			                }
					        MusicList.mMusicPlayerService.setDataSource(url);
					        MusicList.mMusicPlayerService.start();
					}
				}
	        	
	        });
	        
	        /**
	         * ��һ��
	         */
	        mNextButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//�Ƿ������һ�ף�������ǲ���
					if(MusicList.mCursor.moveToNext())
					{
						 String url = MusicList.mCursor
			                       .getString(MusicList.mCursor
			                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
						 if(MusicList.mMusicPlayerService == null){
					        	return;
					        }
						 if (MusicList.mMusicPlayerService != null ) {
			                    MusicList.mMusicPlayerService.stop();
			                }
					        MusicList.mMusicPlayerService.setDataSource(url);
					        MusicList.mMusicPlayerService.start();
					}else {//����������һ�ײ���
						MusicList.mCursor.moveToFirst();
						 String url = MusicList.mCursor
			                       .getString(MusicList.mCursor
			                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
						 if(MusicList.mMusicPlayerService == null){
					        	return;
					        }
						 if (MusicList.mMusicPlayerService != null ) {
			                    MusicList.mMusicPlayerService.stop();
			                }
					        MusicList.mMusicPlayerService.setDataSource(url);
					        MusicList.mMusicPlayerService.start();
					}
				}
			});
	        
	        /**
	         * ����/��ͣ
	         */
	        mPlayPauseButton.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                // Perform action on click
	                if (MusicList.mMusicPlayerService != null && MusicList.mMusicPlayerService.isPlaying()) {
	                	MusicList.mMusicPlayerService.pause();
	                	  mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.music_play_play));
	                } else if (MusicList.mMusicPlayerService != null){
	                	MusicList.mMusicPlayerService.start();
	                	  mPlayPauseButton.setImageDrawable(getResources().getDrawable(R.drawable.music_play_pause));
	                }
	            }
	        });
	        
	        /**
	         * ֹͣ
	         */
	        mStopButton.setOnClickListener(new OnClickListener() {
	            public void onClick(View v) {
	                // Perform action on click
	                if (MusicList.mMusicPlayerService != null ) {
	                    MusicList.mMusicPlayerService.stop();
	                }
	            }
	        });
	        
	        IntentFilter filter = new IntentFilter();
	        filter.addAction(MusicPlayerService.PLAYER_PREPARE_END);
	        filter.addAction(MusicPlayerService.PLAY_COMPLETED);
	        registerReceiver(mPlayerEvtReceiver, filter);
	 }
}
