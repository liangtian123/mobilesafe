package com.google.mobilesafe.activity;

import com.google.mobilesafe.R;
import com.google.mobilesafe.utils.ContentValue;
import com.google.mobilesafe.utils.SpUtils;

import android.app.Activity;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

public class ToastLocationActivity extends Activity {
	private ImageView iv_drag;
	private Button bt_top;
	private Button bt_bottom;
	private int top;
	private int left;
	private int right;
	private int bottom;
	private int mScreenWidth;
	private int mScreenHeight;
	private WindowManager mWM;
	long[] mHits=new long[2];
	private LayoutParams mLayoutParams;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_toast_location);
		initUI();
		initData();
		initDouble();
	}

	private void initDouble() {
		// TODO Auto-generated method stub
		iv_drag.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				System.arraycopy(mHits, 1, mHits, 0, mHits.length-1);
				mHits[mHits.length-1]=SystemClock.uptimeMillis();
				if(mHits[mHits.length-1]-mHits[0]<500){
					left=mScreenWidth/2-iv_drag.getWidth()/2;
					top=mScreenHeight/2-iv_drag.getHeight()/2;
					right=mScreenWidth/2+iv_drag.getWidth()/2;
					bottom = mScreenHeight/2+iv_drag.getHeight()/2;
					iv_drag.layout(left, top, right, bottom);
					SpUtils.putInt(getApplicationContext(), ContentValue.LOCATION_X, left);
					SpUtils.putInt(getApplicationContext(), ContentValue.LOCATION_Y, right);
				}
			}
		});
	}

	private void initData() {
		// TODO Auto-generated method stub
		//为这个可以拖拽的imageview设置触摸事件、
		iv_drag.setOnTouchListener(new OnTouchListener() {
			
			private int startX;
			private int startY;
			private int endX;
			private int endY;
			
			
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					endX = (int) event.getRawX();
					endY = (int) event.getRawY();
					//获取偏移量
					int disX=endX-startX;
					int disY=endY-startY;
					left = iv_drag.getLeft()+disX;
					top = iv_drag.getTop()+disY;
					right = iv_drag.getRight()+disX;
					bottom = iv_drag.getBottom()+disY;
					//容错处理
					if(left<0||top<0||right>mScreenWidth||bottom>(mScreenHeight-22)){
						return false;  
					}
					//拖拽的隐藏效果， 如果超过一半的屏幕宽度的时候，就发生按钮的状态的改变
					if(top<mScreenHeight/2){
						bt_top.setVisibility(View.INVISIBLE);
						bt_bottom.setVisibility(View.VISIBLE);
					}else{
						bt_top.setVisibility(View.VISIBLE);
						bt_bottom.setVisibility(View.INVISIBLE);
					}
					//告知移动的控件， 按计算的来展示
					iv_drag.layout(left, top, right, bottom);
					//需要重置坐标
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					
					
					break;
				case MotionEvent.ACTION_UP:
					//当手指松开的时候， 需要记录这次的坐标位置，下次进来的时候， 就可以在这次的位置上停留了，
					SpUtils.putInt(getApplicationContext(), ContentValue.LOCATION_X, iv_drag.getLeft());
					SpUtils.putInt(getApplicationContext(), ContentValue.LOCATION_Y, iv_drag.getTop());
					break;
				}
				return false;
			}
		});
	}

	private void initUI() {
		iv_drag = (ImageView) findViewById(R.id.iv_drag);
		bt_top = (Button) findViewById(R.id.bt_top);
		bt_bottom = (Button) findViewById(R.id.bt_bottom);
		
		mWM = (WindowManager) getSystemService(WINDOW_SERVICE);
		mScreenWidth = mWM.getDefaultDisplay().getWidth();
		mScreenHeight = mWM.getDefaultDisplay().getHeight();
		//下次进来的时候， 需要给这个iv_drag设置初始位置
		int location_x = SpUtils.getInt(getApplicationContext(), ContentValue.LOCATION_X, 0);
		int location_y = SpUtils.getInt(getApplicationContext(), ContentValue.LOCATION_Y, 0);
		mLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT
				, RelativeLayout.LayoutParams.WRAP_CONTENT); 
		mLayoutParams.leftMargin=location_x;
		mLayoutParams.topMargin=location_y;
		iv_drag.setLayoutParams(mLayoutParams);
		//当开始进来的时候，拖拽的隐藏效果， 如果超过一半的屏幕宽度的时候，就发生按钮的状态的改变
		if(location_y>mScreenHeight/2){
			bt_top.setVisibility(View.VISIBLE);
			bt_bottom.setVisibility(View.INVISIBLE);
		}else{
			bt_top.setVisibility(View.INVISIBLE);
			bt_bottom.setVisibility(View.VISIBLE);
		}
	}
}
