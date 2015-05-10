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
	VideoListViewAdapter myVideoListViewAdapter;//listVideos�����˱������е���Ƶ�������Ϣ
	List<VideoList> listVideos;
	int videoSize;
	
	private final String IMAGE_TYPE = "image/*";

    private final int IMAGE_CODE = 0;   //�����IMAGE_CODE���Լ����ⶨ���
    
    private ImageButton addPic=null,showPicPath=null;
    
    private ImageView imgShow=null;
    
    private TextView imgPath=null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Resources res = getResources(); 
        //��TabActivity�����ȡ����Tab��TabHost
        tabhost = getTabHost();
        TabHost.TabSpec tab1 = tabhost.newTabSpec("one");
        tab1.setIndicator("��Ƶ");
        tab1.setContent(R.id.widget_layout_red);
        
        TabHost.TabSpec tab2 = tabhost.newTabSpec("two");
        tab2.setIndicator("����",res.getDrawable(R.drawable.item));
        tab2.setContent(R.id.widget_layout_yellow);
        
        TabHost.TabSpec tab3 = tabhost.newTabSpec("three");
        tab3.setIndicator("ͼƬ");
        tab3.setContent(R.id.widget_layout_white);
        
        tabhost.addTab(tab1);
        tabhost.addTab(tab2);
        tabhost.addTab(tab3);
        //pictrueCheck.init();//��ʼ����Ƭģ��
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
         //ʹ��intent����ϵͳ�ṩ����Ṧ�ܣ�ʹ��startActivityForResult��Ϊ�˻�ȡ�û�ѡ���ͼƬ
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

            if (resultCode != RESULT_OK) {        //�˴��� RESULT_OK ��ϵͳ�Զ����һ������

                Log.e("TAG->onresult","ActivityResult resultCode error");

                return;

            }

         

            Bitmap bm = null;

         

            //���ĳ������ContentProvider���ṩ���� ����ͨ��ContentResolver�ӿ�

            ContentResolver resolver = getContentResolver();

         

            //�˴��������жϽ��յ�Activity�ǲ�������Ҫ���Ǹ�

            if (requestCode == IMAGE_CODE) {

                try {

                    Uri originalUri = data.getData();        //���ͼƬ��uri 

         

                    bm = MediaStore.Images.Media.getBitmap(resolver, originalUri);        
                    //�Եõ�bitmapͼƬ
                    imgShow.setImageBitmap(bm);
                    

//    ���￪ʼ�ĵڶ����֣���ȡͼƬ��·����

         

                    String[] proj = {MediaStore.Images.Media.DATA};

         

                    //������android��ý�����ݿ�ķ�װ�ӿڣ�����Ŀ�Android�ĵ�

                    Cursor cursor = managedQuery(originalUri, proj, null, null, null); 

                    //���Ҹ������ ����ǻ���û�ѡ���ͼƬ������ֵ

                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

                    //�����������ͷ ���������Ҫ����С�ĺ���������Խ��

                    cursor.moveToFirst();

                    //����������ֵ��ȡͼƬ·��

                    String path = cursor.getString(column_index);
                    //System.out.println(path);
                    imgPath.setText(path);
                }catch (IOException e) {

                    Log.e("TAG-->Error",e.toString()); 

                }

            }

        }
}
