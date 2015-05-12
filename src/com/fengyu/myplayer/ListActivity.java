package com.fengyu.myplayer;

import java.io.IOException;
import java.util.List;

import sysu.ss.xu.PlayerActivity;

import com.fengyu.musicplayer.MusicList;
import com.fengyu.videoplayer.apdater.VideoListViewAdapter;
import com.fengyu.videoplayer.ui.VideoList;
import com.fengyu.videoplayer.utils.AbstructProvider;
import com.fengyu.videoplayer.utils.LoadedImage;
import com.fengyu.videoplayer.utils.VideoProvider;

import android.app.Activity;
import android.app.TabActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Video.Thumbnails;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity extends TabActivity {
	private TabHost tabhost;
	public ListActivity instance = null;
	ListView myVideoListView;
	VideoListViewAdapter myVideoListViewAdapter;//listVideos�����˱������е���Ƶ�������Ϣ
	List<VideoList> listVideos;
	int videoSize;
    @SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Resources res = getResources(); 
        //��TabActivity�����ȡ����Tab��TabHost
        tabhost = getTabHost();
        TabHost.TabSpec tab1 = tabhost.newTabSpec("one");
        tab1.setIndicator("��Ƶ",res.getDrawable(R.drawable.video));
        tab1.setContent(R.id.widget_video);
        Intent intent;
        TabHost.TabSpec tab2;
        intent=new Intent(this,MusicList.class);
        tab2=tabhost.newTabSpec("tab2")//�½�һ��tab
                .setIndicator("����",res.getDrawable(R.drawable.music))//�������Ƽ�ͼ��
                .setContent(intent);//������ʾ��intent
 
        
        TabHost.TabSpec tab3 = tabhost.newTabSpec("three");
        tab3.setIndicator("ͼƬ",res.getDrawable(R.drawable.picture));
        tab3.setContent(R.id.widget_layout_white);
        
        tabhost.addTab(tab1);

        tabhost.addTab(tab2);//��ӽ�tabHost
        tabhost.addTab(tab3);
        instance = this;
		AbstructProvider provider = new VideoProvider(instance);
        listVideos = provider.getList();
        videoSize = listVideos.size();
		myVideoListViewAdapter = new VideoListViewAdapter(this, listVideos);
		myVideoListView = (ListView)findViewById(R.id.jievideolistfile);
		myVideoListView.setAdapter(myVideoListViewAdapter);
		myVideoListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,  
                    long id) {  
								Intent intent = new Intent();
								intent.setClass(ListActivity.this, PlayerActivity.class);
								Bundle bundle = new Bundle();
								intent.putExtra("filepath", listVideos.get(position).getPath());
								startActivity(intent);
			}
		});
		loadImages();
	}
	/**
	 * Load images.
	 */
	private void loadImages() {
		final Object data = getLastNonConfigurationInstance();
		if (data == null) {
			new LoadImagesFromSDCard().execute();
		} else {
			//
			final LoadedImage[] photos = (LoadedImage[]) data;
			if (photos.length == 0) {
				new LoadImagesFromSDCard().execute();
			}
			for (LoadedImage photo : photos) {
				addImage(photo);
			}
		}
	}
	private void addImage(LoadedImage...value) {
		for (LoadedImage image : value) {
			myVideoListViewAdapter.addPhoto(image);
			myVideoListViewAdapter.notifyDataSetChanged();
		}
	}
	@Override
	public Object onRetainNonConfigurationInstance() {
		final ListView grid =myVideoListView;
		final int count = grid.getChildCount();
		final LoadedImage[] list = new LoadedImage[count];

		for (int i = 0; i < count; i++) {
			final ImageView v = (ImageView) grid.getChildAt(i);
			list[i] = new LoadedImage(
					((BitmapDrawable) v.getDrawable()).getBitmap());
		}

		return list;
	}
	
	/** 
	    * ��ȡ��Ƶ����ͼ 
	    * @param videoPath 
	    * @param width 
	    * @param height 
	    * @param kind 
	    * @return 
	    */  
	   private Bitmap getVideoThumbnail(String videoPath, int width , int height, int kind){  
	    Bitmap bitmap = null;  
	    bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);  
	    bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);  
	    return bitmap;  
	   }  

	   /**
	    * 
	    * @author fengyu
	    *
	    */
	class LoadImagesFromSDCard extends AsyncTask<Object, LoadedImage, Object> {
		@Override
		protected Object doInBackground(Object... params) {
			Bitmap bitmap = null;
			for (int i = 0; i < videoSize; i++) {
				bitmap = getVideoThumbnail(listVideos.get(i).getPath(), 120, 120, Thumbnails.MINI_KIND);
				if (bitmap != null) {
					publishProgress(new LoadedImage(bitmap));
				}
			}
			return null;
		}
		
		@Override
		public void onProgressUpdate(LoadedImage... value) {
			addImage(value);
		}
	}
}
