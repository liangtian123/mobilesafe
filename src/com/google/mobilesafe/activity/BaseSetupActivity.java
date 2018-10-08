package com.google.mobilesafe.activity;


import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public abstract class BaseSetupActivity extends Activity {
	private GestureDetector gestureDetector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//创建一个这个手势监听器的对象，来接收这个屏幕的触摸事件
		gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener(){
			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// TODO Auto-generated method stub
	//						super.onFling(e1, e2, velocityX, velocityY);
				//监听手势的移动
				//但手势右滑动，说明E1>E2	
				if(e1.getX()-e2.getX()>0){
					//由右向左，下一页
					//点击下一页，跳转到下个界面
					//这里的每一个页面的内容都是不同的， 因此需要展示不同页面的内容， 
					showNextPage();
				}
				if(e2.getX()-e1.getX()>0){
					//由左向右滑，上一页
					//点击上一页，跳转到下个界面
					//这里的每一个页面的内容都是不同的， 因此需要展示不同页面的内容， 
					showPrePage();
				}
			
				return super.onFling(e1, e2, velocityX, velocityY);
			}

		});
	}
	//由于不同的页面展示不同的内容，因此需要抽取出一个抽象的方法
	public abstract void showPrePage();
	public abstract void showNextPage();
	
	//当界面中的点击事件发生的时候， 也需要跳转到另一个页面，并且每一个界面都有这两个方法， 因此：
	//也可以抽取到这个抽象类中
	public void nextPage(View v){
		showNextPage();
	}
	public void prePage(View v){
		showPrePage();
	}
	
	//当触摸屏幕的时候，当滑动的时候， 也可以触发执行下一页， 上一页的操作
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		//这里需要配合手势监听器来完成才可以。这样的触屏动作由手势监听器来完成
		gestureDetector.onTouchEvent(event);
		return super.onTouchEvent(event);
	}
}
