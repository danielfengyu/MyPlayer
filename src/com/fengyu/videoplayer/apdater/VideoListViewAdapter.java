package com.fengyu.videoplayer.apdater;

import java.util.ArrayList;
import java.util.List;

import com.fengyu.myplayer.R;
import com.fengyu.videoplayer.ui.VideoList;
import com.fengyu.videoplayer.utils.LoadedImage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VideoListViewAdapter extends BaseAdapter {

	List<VideoList> listVideos;
	int local_postion = 0;
	boolean imageChage = false;
	//LayoutInflater作用是将layout的xml布局文件实例化为View类对象。
	private LayoutInflater mLayoutInflater;
	private ArrayList<LoadedImage> photos = new ArrayList<LoadedImage>();
	//构造函数
	public VideoListViewAdapter(Context context, List<VideoList> listVideos){
		mLayoutInflater = LayoutInflater.from(context);
		this.listVideos = listVideos;
	}
	@Override
	public int getCount() {
		return photos.size();
	}
	public void addPhoto(LoadedImage image){
		photos.add(image);
	}
	@Override
	public Object getItem(int position) {
		return position;
	}
	
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.listview, null);
			holder.img = (ImageView)convertView.findViewById(R.id.video_img);
			holder.title = (TextView)convertView.findViewById(R.id.video_title);
			holder.time = (TextView)convertView.findViewById(R.id.video_time);
			convertView.setTag(holder);
		}else {
			holder = (ViewHolder)convertView.getTag();
		}
			holder.title.setText(listVideos.get(position).getTitle());
			long min = listVideos.get(position).getDuration() /1000 / 60;
			long sec = listVideos.get(position).getDuration() /1000 % 60;
			holder.time.setText(min+" : "+sec);
			holder.img.setImageBitmap(photos.get(position).getBitmap());
			return convertView;
	}

	public final class ViewHolder{
		public ImageView img;
		public TextView title;
		public TextView time;
	}
}
