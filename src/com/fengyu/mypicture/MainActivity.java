package com.fengyu.mypicture;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fengyu.myplayer.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
/**
 * 
 * @author 冯玉（1106，201192048）
 * 
 *
 */
public class MainActivity extends Activity {
	private HashMap<String, List<String>> mGruopMap = new HashMap<String, List<String>>();
	private List<ImageBean> list = new ArrayList<ImageBean>();
	private final static int SCAN_OK = 1;
	private ProgressDialog mProgressDialog;
	private GroupAdapter adapter;
	private GridView mGroupGridView;
	
	private ImageView image;// 要显示选择的图片
	private Bitmap photo;// 选择好的图片的bitmap形式  
	private ImageButton takePhoto_btn;// 拍照 
	private static final int PHO = 2;// 照相 

	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:
				//关闭进度条
				mProgressDialog.dismiss();
				
				adapter = new GroupAdapter(MainActivity.this, list = subGroupOfImage(mGruopMap), mGroupGridView);
				mGroupGridView.setAdapter(adapter);
				break;
			}
		}
		
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_main);
		
		takePhoto_btn = (ImageButton) findViewById(R.id.button2);
		image = (ImageView) findViewById(R.id.imageView1);
		
		mGroupGridView = (GridView) findViewById(R.id.main_grid);
		getImages();
		mGroupGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				List<String> childList = mGruopMap.get(list.get(position).getFolderName());
				
				Intent mIntent = new Intent(MainActivity.this, ShowImageActivity.class);
				mIntent.putStringArrayListExtra("data", (ArrayList<String>)childList);
				startActivity(mIntent);
				
			}
		});
		
		
		// 拍照上传 
		takePhoto_btn.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) {
				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					// 如果存储卡可用 
					Intent intent = new Intent( "android.media.action.IMAGE_CAPTURE"); 
					startActivityForResult(intent, PHO);
					} else { 
						Toast.makeText(getApplicationContext(), "存储卡不可用", Toast.LENGTH_LONG).show();
						} 
				} 
			}); 
		
	}


	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中
	 */
	private void getImages() {
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "暂无外部存储", Toast.LENGTH_SHORT).show();
			return;
		}
		
		//显示进度条
		mProgressDialog = ProgressDialog.show(this, null, "正在加载...");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = MainActivity.this.getContentResolver();

				//只查询jpeg和png的图片
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
				
				while (mCursor.moveToNext()) {
					//获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					
					//获取该图片的父路径名
					String parentName = new File(path).getParentFile().getName();

					
					//根据父路径名将图片放入到mGruopMap中
					if (!mGruopMap.containsKey(parentName)) {
						List<String> chileList = new ArrayList<String>();
						chileList.add(path);
						mGruopMap.put(parentName, chileList);
					} else {
						mGruopMap.get(parentName).add(path);
					}
				}
				
				mCursor.close();
				
				//通知Handler扫描图片完成
				mHandler.sendEmptyMessage(SCAN_OK);
				
			}
		}).start();
		
	}
	
	
	/**
	 * 组装分组界面GridView的数据源，因为我们扫描手机的时候将图片信息放在HashMap中
	 * 所以需要遍历HashMap将数据组装成List
	 * 
	 * @param mGruopMap
	 * @return
	 */
	private List<ImageBean> subGroupOfImage(HashMap<String, List<String>> mGruopMap){
		if(mGruopMap.size() == 0){
			return null;
		}
		List<ImageBean> list = new ArrayList<ImageBean>();
		
		Iterator<Map.Entry<String, List<String>>> it = mGruopMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, List<String>> entry = it.next();
			ImageBean mImageBean = new ImageBean();
			String key = entry.getKey();
			List<String> value = entry.getValue();
			
			mImageBean.setFolderName(key);
			mImageBean.setImageCounts(value.size());
			mImageBean.setTopImagePath(value.get(0));//获取该组的第一张图片
			
			list.add(mImageBean);
		}
		
		return list;
		
	}
	
	/* 拍照取得回传的数据 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null && requestCode == PHO) { 
			// 拍照上传时 
			Bundle extras = data.getExtras(); 
			if (extras != null) { 
				photo = (Bitmap) extras.get("data");
				image.setImageBitmap(photo);
			}
			else {
					Toast.makeText(MainActivity.this, "未找到图片", Toast.LENGTH_LONG) .show(); 
			} 
			}
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = data.getData();// 当选择的图片不为空的话，在获取到图片的途径
				try {
							String[] pojo = {MediaStore.Images.Media.DATA}; 
							Cursor cursor = managedQuery(uri, pojo, null, null, null);
							if (cursor != null) { 
									ContentResolver cr = this.getContentResolver(); 
									int colunm_index = cursor .getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
									cursor.moveToFirst(); 
									String path = cursor.getString(colunm_index);
									/*** 
									 * 这里加这样一个判断主要是为了第三方的软件选择，比如：使用第三方的文件管理器的话，
									 * 你选择的文件就不一定是图片了，
									 * 这样的话，我们判断文件的后缀名 如果是图片格式的话，那么才可以 
									 */ 
									if (path.endsWith("jpg") || path.endsWith("png")) {
												photo = BitmapFactory.decodeStream(cr .openInputStream(uri)); 
												image.setImageBitmap(photo); 
									}
									else { 
											
									} 
						}
						else {
								
						} 
					} catch (Exception e) { 
					
					}
			} 
		}

}
