package com.fengyu.mypicture;

import java.util.List;

import com.fengyu.myplayer.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

public class ShowImageActivity extends Activity {
	private GridView mGridView;
	private List<String> list;
	private ChildAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.picture_show_image_activity);
		
		mGridView = (GridView) findViewById(R.id.child_grid);
		list = getIntent().getStringArrayListExtra("data");
		System.out.println(list.size());
		Log.i("fengyu", "图片张数"+list.size());
		adapter = new ChildAdapter(this, list, mGridView);
		mGridView.setAdapter(adapter);		
	}

	@Override
	public void onBackPressed() {
		Toast.makeText(this, "选中的图片 " + adapter.getSelectItems().size() + " item", Toast.LENGTH_LONG).show();
		super.onBackPressed();
	}
}
