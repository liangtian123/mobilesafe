package com.google.mobilesafe.activity;

import com.google.mobilesafe.R;
import com.google.mobilesafe.utils.ContentValue;
import com.google.mobilesafe.utils.SpUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SetupOverActivity extends Activity {
	private TextView tv_reset_setup;
	private TextView tv_phone1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//首先需要判断是否已经设置完成，需要用sharedpreference来记录
		boolean setup_over = SpUtils.getBoolean(getApplicationContext(), ContentValue.SETUP_OVER ,false);
		if(setup_over){
			//表明设置完成，到设置完成界面
			setContentView(R.layout.activity_setup_over);
			//初始化UI
			initUI();
		}else{
			//还没有设置， 那么就从跳转到第一个导航界面
			Intent intent = new Intent(getApplicationContext(),Setup1Activity.class);
			startActivity(intent);
			//开启一个新的界面后， 关闭功能列表界面
			finish();
		}
		
		
	}

	private void initUI() {
		//通过这个控件的id, dedao,得到控件的对象
		tv_phone1 = (TextView) findViewById(R.id.tv_phone1);
		//从sharedpreference中得到这个联系人绑定的电话号码
		String phone = SpUtils.getString(getApplicationContext(), ContentValue.CONTACT_PHONE, "");
		//将这个电话号码设置到这个控件textView中。
		tv_phone1.setText(phone);
		//获取这个TextView的控件， 目的是为了和这个控件设置点击事件， 当这个点击事件被触发了之后， 
		//就会跳转到第一个导航页面
		tv_reset_setup = (TextView) findViewById(R.id.tv_reset_setup);
		tv_reset_setup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),Setup1Activity.class);
				startActivity(intent);
				//开启了一个界面后， 关闭的时候， 需要关闭它， 让他在任务栈中销毁
				finish();
			}
		});
	}
}
