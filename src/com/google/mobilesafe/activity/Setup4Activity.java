package com.google.mobilesafe.activity;

import com.google.mobilesafe.R;
import com.google.mobilesafe.utils.ContentValue;
import com.google.mobilesafe.utils.SpUtils;
import com.google.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Setup4Activity extends BaseSetupActivity {
	private CheckBox cb_setup_over;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup4);
		initUI();
	}
	private void initUI() {
		// TODO Auto-generated method stub
		cb_setup_over = (CheckBox) findViewById(R.id.cb_setup_over);
		//是否选中状态的回显
		boolean open_security = SpUtils.getBoolean(getApplicationContext(), ContentValue.OPEN_SECURITY, false);
		//这里的勾也需要回显
		cb_setup_over.setChecked(open_security);
		//根据状态修改后面的文字
		if(open_security){
			cb_setup_over.setText("安全设置以开启");
		}else{
			cb_setup_over.setText("安全设置以关闭");
		}
		//当点击这个checkbox的时候， 也需要开启或是关闭
//		cb_setup_over.setChecked(!cb_setup_over.isChecked());
		//当在点击的过程中才发生状态的变化
		cb_setup_over.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				//然后将这个状态存储到sharedpreference中
				SpUtils.putBoolean(getApplicationContext(), ContentValue.OPEN_SECURITY, isChecked);
				//根据状态修改后面的文字
				if(isChecked){
					cb_setup_over.setText("安全设置以开启");
				}else{
					cb_setup_over.setText("安全设置以关闭");
				}
			}
		});
	}
	@Override
	public void showPrePage() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,Setup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}
	@Override
	public void showNextPage() {
		// TODO Auto-generated method stub
		//在下一页的时候，就需要判断是否已经开启了， 
		boolean open_security = SpUtils.getBoolean(getApplicationContext(), ContentValue.OPEN_SECURITY, false);
		if(open_security){
			Intent intent = new Intent(this,SetupOverActivity.class);
			startActivity(intent);
			finish();
			//当到了最后一个页面， 用来判断已经设置完成，用sharedpreference来记录
			SpUtils.putBoolean(getApplicationContext(), ContentValue.SETUP_OVER, true);
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		}else{
			ToastUtils.show(getApplicationContext(), "请开启防盗保护");
		}
	}
	//点击进入设置完成界面
//	public void nextPage(View v){
//		//在下一页的时候，就需要判断是否已经开启了， 
//		boolean open_security = SpUtils.getBoolean(getApplicationContext(), ContentValue.OPEN_SECURITY, false);
//		if(open_security){
//			Intent intent = new Intent(this,SetupOverActivity.class);
//			startActivity(intent);
//			finish();
//			//当到了最后一个页面， 用来判断已经设置完成，用sharedpreference来记录
//			SpUtils.putBoolean(getApplicationContext(), ContentValue.SETUP_OVER, true);
//			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
//		}else{
//			ToastUtils.show(getApplicationContext(), "请开启防盗保护");
//		}
//	}
	//点击进入第三个界面
//	public void prePage(View v){
//		Intent intent = new Intent(this,Setup3Activity.class);
//		startActivity(intent);
//		finish();
//		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
//	}
}
