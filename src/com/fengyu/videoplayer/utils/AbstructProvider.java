package com.fengyu.videoplayer.utils;

import java.util.List;

import com.fengyu.videoplayer.ui.VideoList;

/*
 * 一个接口，来获取搜索的视频的一个集合
 */
public interface AbstructProvider {
	
    public List<VideoList> getList();
    
}
