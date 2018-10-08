package com.google.mobilesafe.activity;

import com.google.mobilesafe.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;

public class Setup1Activity extends BaseSetupActivity {
	private GestureDetector gestureDetector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup1);
	}
		
	@Override
	public void showPrePage() {
		// TODO Auto-generated method stub
		//空实现
	}
	@Override
	public void showNextPage() {
		// TODO Auto-generated method stub
		//点击下一页，跳转到下个界面
		Intent intent = new Intent(this,Setup2Activity.class);
		startActivity(intent);
		finish();
		//开启平移动画
		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
	}
}
