package com.fengyu.musicplayer.utils;

import com.fengyu.myplayer.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;


/**
 * 得到一张bitmap位图
 * 创建一张画布
 * 在画布上画圆
 * 设置两种图的合成模式
 * 将另一张图放在画布上
 * 返回重叠的图像
 * 在ondraw（）中画出原图
 * 
 * ！！！！！
 * 	图片合成模式的设置，位于两张图绘制之间，该合成模式只对两张图有效
 *  控制旋转的变量应是全局变量，不能声明在方法中
 * 		
 */
public class Circleview extends View {
	
	private Bitmap mBitmap;
	private Paint mPaint;
	private PorterDuffXfermode mPdDuffXfermode;
	private Bitmap _Bitmap;
	private float offset=0;

	public Circleview(Context context) {
		super(context);
		init();
	}
	public Circleview(Context context,AttributeSet aa) {
		super(context,aa);
		init();
		
	}
	public void init(){
		mBitmap=Bitmap.createBitmap(BitmapFactory.decodeResource(getContext().getResources(),
				R.drawable.run_plan));
		//除锯齿
		mPaint=new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setFilterBitmap(true);
		mPaint.setDither(true);
		mPdDuffXfermode=new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);	
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//
		if(_Bitmap==null){
			_Bitmap=getCircleView();
		}
		//offset旋转角度不断变大
		canvas.rotate(offset, getWidth()/2, getHeight()/2);
		canvas.drawBitmap(_Bitmap,getWidth()/2-_Bitmap.getWidth()/2, 
				getHeight()/2-_Bitmap.getHeight()/2, null);
		offset=offset+1;
			//不停刷新，制造旋转效果
			invalidate();		
	}
	private Bitmap getCircleView(){
		Bitmap _Bitmap=Bitmap.createBitmap(getWidth(), getHeight(), Config.ARGB_8888);
		Canvas _Canvas=new Canvas(_Bitmap);
		_Canvas.drawCircle(getWidth()/2, getHeight()/2, mBitmap.getHeight()/2, mPaint);
		mPaint.setXfermode(mPdDuffXfermode);
		_Canvas.drawBitmap(mBitmap, getWidth()/2-mBitmap.getWidth()/2, 
				getHeight()/2-mBitmap.getHeight()/2, mPaint);
		return _Bitmap;
	}
	
}
