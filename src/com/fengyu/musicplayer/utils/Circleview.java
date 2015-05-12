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
 * �õ�һ��bitmapλͼ
 * ����һ�Ż���
 * �ڻ����ϻ�Բ
 * ��������ͼ�ĺϳ�ģʽ
 * ����һ��ͼ���ڻ�����
 * �����ص���ͼ��
 * ��ondraw�����л���ԭͼ
 * 
 * ����������
 * 	ͼƬ�ϳ�ģʽ�����ã�λ������ͼ����֮�䣬�úϳ�ģʽֻ������ͼ��Ч
 *  ������ת�ı���Ӧ��ȫ�ֱ��������������ڷ�����
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
		//�����
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
		//offset��ת�ǶȲ��ϱ��
		canvas.rotate(offset, getWidth()/2, getHeight()/2);
		canvas.drawBitmap(_Bitmap,getWidth()/2-_Bitmap.getWidth()/2, 
				getHeight()/2-_Bitmap.getHeight()/2, null);
		offset=offset+1;
			//��ͣˢ�£�������תЧ��
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
