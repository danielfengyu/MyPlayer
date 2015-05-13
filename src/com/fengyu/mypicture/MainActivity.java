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
 * @author ����1106��201192048��
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
	
	private ImageView image;// Ҫ��ʾѡ���ͼƬ
	private Bitmap photo;// ѡ��õ�ͼƬ��bitmap��ʽ  
	private ImageButton takePhoto_btn;// ���� 
	private static final int PHO = 2;// ���� 

	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:
				//�رս�����
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
		
		
		// �����ϴ� 
		takePhoto_btn.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) {
				String state = Environment.getExternalStorageState();
				if (state.equals(Environment.MEDIA_MOUNTED)) {
					// ����洢������ 
					Intent intent = new Intent( "android.media.action.IMAGE_CAPTURE"); 
					startActivityForResult(intent, PHO);
					} else { 
						Toast.makeText(getApplicationContext(), "�洢��������", Toast.LENGTH_LONG).show();
						} 
				} 
			}); 
		
	}


	/**
	 * ����ContentProviderɨ���ֻ��е�ͼƬ���˷��������������߳���
	 */
	private void getImages() {
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "�����ⲿ�洢", Toast.LENGTH_SHORT).show();
			return;
		}
		
		//��ʾ������
		mProgressDialog = ProgressDialog.show(this, null, "���ڼ���...");
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = MainActivity.this.getContentResolver();

				//ֻ��ѯjpeg��png��ͼƬ
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
								+ MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED);
				
				while (mCursor.moveToNext()) {
					//��ȡͼƬ��·��
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					
					//��ȡ��ͼƬ�ĸ�·����
					String parentName = new File(path).getParentFile().getName();

					
					//���ݸ�·������ͼƬ���뵽mGruopMap��
					if (!mGruopMap.containsKey(parentName)) {
						List<String> chileList = new ArrayList<String>();
						chileList.add(path);
						mGruopMap.put(parentName, chileList);
					} else {
						mGruopMap.get(parentName).add(path);
					}
				}
				
				mCursor.close();
				
				//֪ͨHandlerɨ��ͼƬ���
				mHandler.sendEmptyMessage(SCAN_OK);
				
			}
		}).start();
		
	}
	
	
	/**
	 * ��װ�������GridView������Դ����Ϊ����ɨ���ֻ���ʱ��ͼƬ��Ϣ����HashMap��
	 * ������Ҫ����HashMap��������װ��List
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
			mImageBean.setTopImagePath(value.get(0));//��ȡ����ĵ�һ��ͼƬ
			
			list.add(mImageBean);
		}
		
		return list;
		
	}
	
	/* ����ȡ�ûش������� */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null && requestCode == PHO) { 
			// �����ϴ�ʱ 
			Bundle extras = data.getExtras(); 
			if (extras != null) { 
				photo = (Bitmap) extras.get("data");
				image.setImageBitmap(photo);
			}
			else {
					Toast.makeText(MainActivity.this, "δ�ҵ�ͼƬ", Toast.LENGTH_LONG) .show(); 
			} 
			}
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = data.getData();// ��ѡ���ͼƬ��Ϊ�յĻ����ڻ�ȡ��ͼƬ��;��
				try {
							String[] pojo = {MediaStore.Images.Media.DATA}; 
							Cursor cursor = managedQuery(uri, pojo, null, null, null);
							if (cursor != null) { 
									ContentResolver cr = this.getContentResolver(); 
									int colunm_index = cursor .getColumnIndexOrThrow(MediaStore.Images.Media.DATA); 
									cursor.moveToFirst(); 
									String path = cursor.getString(colunm_index);
									/*** 
									 * ���������һ���ж���Ҫ��Ϊ�˵����������ѡ�񣬱��磺ʹ�õ��������ļ��������Ļ���
									 * ��ѡ����ļ��Ͳ�һ����ͼƬ�ˣ�
									 * �����Ļ��������ж��ļ��ĺ�׺�� �����ͼƬ��ʽ�Ļ�����ô�ſ��� 
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
