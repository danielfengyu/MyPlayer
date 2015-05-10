package com.fengyu.myplayer;

import java.io.IOException;
import java.util.List;

import sysu.ss.xu.PlayerActivity;

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
import android.widget.TextView;
import android.widget.Toast;

public class ListActivity extends TabActivity {
	private TabHost tabhost;
	public ListActivity instance = null;
	ListView myVideoListView;
	VideoListViewAdapter myVideoListViewAdapter;//listVideos包含了本地所有的视频的相关信息
	List<VideoList> listVideos;
	int videoSize;
	
	private final String IMAGE_TYPE = "image/*";

    private final int IMAGE_CODE = 0;   //这里的IMAGE_CODE是自己任意定义的
    
    private ImageButton addPic=null,showPicPath=null;
    
    private ImageView imgShow=null;
    
    private TextView imgPath=null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Resources res = getResources(); 
        //从TabActivity上面获取放置Tab的TabHost
        tabhost = getTabHost();
        TabHost.TabSpec tab1 = tabhost.newTabSpec("one");
        tab1.setIndicator("视频");
        tab1.setContent(R.id.widget_layout_red);
        
        TabHost.TabSpec tab2 = tabhost.newTabSpec("two");
        tab2.setIndicator("音乐",res.getDrawable(R.drawable.item));
        tab2.setContent(R.id.widget_layout_yellow);
        
        TabHost.TabSpec tab3 = tabhost.newTabSpec("three");
        tab3.setIndicator("图片");
        tab3.setContent(R.id.widget_layout_white);
        
        tabhost.addTab(tab1);
        tabhost.addTab(tab2);
        tabhost.addTab(tab3);
        //pictrueCheck.init();//初始化照片模块
        init();
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
								System.out.println(listVideos.get(position).getPath());
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
	    * 获取视频缩略图 
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

	private void init() {
        // TODO Auto-generated method stub
        addPic=(ImageButton) findViewById(R.id.btnClose);
        showPicPath=(ImageButton) findViewById(R.id.btnSend);
        imgPath=(TextView) findViewById(R.id.img_path);
        imgShow=(ImageView) findViewById(R.id.imgShow);
        addPic.setOnClickListener(listener);
        showPicPath.setOnClickListener(listener);
    }
private OnClickListener listener=new OnClickListener(){
    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        ImageButton btn=(ImageButton) v; 
        switch(btn.getId()){
        case R.id.btnClose:
            setImage();
            break;         
        case R.id.btnSend:
             break;
        }
    }

    

    private void setImage() {
        // TODO Auto-generated method stub
         //使用intent调用系统提供的相册功能，使用startActivityForResult是为了获取用户选择的图片
        Intent getAlbum = new Intent(Intent.ACTION_GET_CONTENT);
        getAlbum.setType(IMAGE_TYPE);
        startActivityForResult(getAlbum, IMAGE_CODE);
    }};
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       // getMenuInflater().inflate(R.menu.picture, menu);
        return false;
    }
    
     protected void onActivityResult(int requestCode, int resultCode, Intent data){

            if (resultCode != RESULT_OK) {        //此处的 RESULT_OK 是系统自定义得一个常量

                Log.e("TAG->onresult","ActivityResult resultCode error");

                return;

            }

         

            Bitmap bm = null;

         

            //外界的程序访问ContentProvider所提供数据 可以通过ContentResolver接口

            ContentResolver resolver = getContentResolver();

         

            //此处的用于判断接收的Activity是不是你想要的那个

            if (requestCode == IMAGE_CODE) {

                try {

                    Uri originalUri = data.getData();        //获得图片的uri 

         

                    bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        
                    //显得到bitmap图片
                    imgShow.setImageBitmap(bm);
                    

//    这里开始的第二部分，获取图片的路径：

         

                    String[] proj = {MediaStore.Images.Media.DATA};

         

                    //好像是android多媒体数据库的封装接口，具体的看Android文档

                    Cursor cursor = managedQuery(originalUri, proj, null, null, null); 

                    //按我个人理解 这个是获得用户选择的图片的索引值

                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    //将光标移至开头 ，这个很重要，不小心很容易引起越界

                    cursor.moveToFirst();

                    //最后根据索引值获取图片路径

                    String path = cursor.getString(column_index);
                    //System.out.println(path);
                    imgPath.setText(path);
                }catch (IOException e) {

                    Log.e("TAG-->Error",e.toString()); 

                }

            }

        }
}
