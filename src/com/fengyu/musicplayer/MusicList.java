package com.fengyu.musicplayer;

import java.io.Serializable;

import com.fengyu.musicui.MusicPlayActivity;
import com.fengyu.myplayer.R;

import android.app.ListActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class MusicList extends ListActivity 
{
   
    public static Cursor mCursor = null;
    
    public static MusicPlayerService mMusicPlayerService = null;
    private MusicInfoController mMusicInfoController = null;
    private TextView mTextView = null;
    
    private ServiceConnection mPlaybackConnection = new ServiceConnection() 
    {
        public void onServiceConnected(ComponentName className, IBinder service) 
        {  
        	Log.i("inout", "bind");
        	mMusicPlayerService = ((MusicPlayerService.LocalBinder)service).getService();
        }
        public void onServiceDisconnected(ComponentName className) 
        {
        	mMusicPlayerService = null;
        }
    };
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.music_list_layout);
        
        MusicPlayerApp musicPlayerApp=(MusicPlayerApp)getApplication();
        mMusicInfoController = (musicPlayerApp).getMusicInfoController();
        
        // bind playback service
        startService(new Intent(this,MusicPlayerService.class));        
        /*
         * 今天调用一个服务，一个是使用StartService，启动正常，一个是使用bindService启动不正常，很奇怪，因为在别的程序里面是正常的，百思不得其解
		 *	后来得到了答案，原因是我使用了TabActivity，但是我的Activity是当前TabActivity里的子Activity，这是android的BUG，其实也不能算是BUG，解决方法就是使用
		 * this.getApplicationContext().bindService就可以了
         */
        this.getApplicationContext().bindService(new Intent(this,MusicPlayerService.class), 
        mPlaybackConnection, Context.BIND_AUTO_CREATE);
        mTextView = (TextView)findViewById(R.id.show_text);
        
    }

    protected void onResume() {
        super.onResume();
        mCursor = mMusicInfoController.getAllSongs();

        ListAdapter adapter = new MusicListAdapter(this, android.R.layout.simple_expandable_list_item_2, mCursor, new String[]{}, new int[]{});
        setListAdapter(adapter);
    }
    
    /*条目响应*/
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        
        if (mCursor == null ||mCursor.getCount() == 0) {
            return;
        }
        
        Intent intent;
        intent=new Intent(MusicList.this,MusicPlayActivity.class);
        mCursor.moveToPosition(position);
        String url = mCursor
                       .getString(mCursor
                            .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA));
        String musicInfo=mCursor
        		.getString(mCursor
        				.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))+"\n"+
        		mCursor.getString(mCursor
        				.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST));
        
        long duration = mCursor.getInt(mCursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION));
        intent.putExtra("duration", duration);
        intent.putExtra("musicinfo", musicInfo);
        Log.i("fengyu","MusicList"+musicInfo);
		intent.putExtra("filepath", url);
        this.startActivity(intent);
    }
}

