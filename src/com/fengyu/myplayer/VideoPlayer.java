package com.fengyu.myplayer;

import android.app.Activity;
import android.os.Bundle;

public class VideoPlayer extends Activity {

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.player_activity);
		Bundle bundle = getIntent().getExtras();//�õ���������bundle
		String data = bundle.getString("video");//��������    
        setTitle(data);    //����
	}
}
