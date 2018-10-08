package com.google.mobilesafe.activity;

import com.google.mobilesafe.R;
import com.google.mobilesafe.service.LockScreenService;
import com.google.mobilesafe.utils.ContentValue;
import com.google.mobilesafe.utils.ServiceUtil;
import com.google.mobilesafe.utils.SpUtils;
import com.lidroid.xutils.db.annotation.Check;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SignalStrength;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class ProcessSettingActivity extends Activity {
	private CheckBox cb_show_system,cb_lock_clear;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_process_setting);
		//显示系统进程
		initShowSystem();
		//显示锁屏清理
		initLockScreenClear();
	}

	/**
	 * 显示锁屏清理
	 */
	private void initLockScreenClear() {
		//当点击这个checkbox的时候， 就会触发一个服务，在这个服务中
		
		cb_lock_clear= (CheckBox) findViewById(R.id.cb_lock_clear);
		//当服务需要和这个选中的状态需要绑定， 下次回显用
		boolean isRunning = ServiceUtil.isRunning(getApplicationContext(), "com.google.mobilesafe.service.LockScreenService");
		//这个checkbox的状态也是需要维护的
		cb_lock_clear.setChecked(isRunning);
		if(isRunning){
			cb_lock_clear.setText("锁屏清理已开启");
		}else{
			cb_lock_clear.setText("锁屏清理已关闭");
		}
		cb_lock_clear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					cb_lock_clear.setText("锁屏清理已开启");
					//这里需要开启一个服务
					//当屏幕被锁上的时候， 那么就有个广播的接受者来接收这个锁屏的广播， 然后在这个广播中，杀死进程
					startService(new Intent(getApplicationContext(),LockScreenService.class));
				}else{
					cb_lock_clear.setText("锁屏清理已关闭");
					stopService(new Intent(getApplicationContext(),LockScreenService.class));
				}
			}
		});
	}

	/**
	 * 显示系统进程
	 */
	private void initShowSystem() {
		cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
		//这里首先需要回显上次选中的状态
		boolean show_system = SpUtils.getBoolean(this, ContentValue.SHOW_SYSTEM, false);
		//这里需要判断checkbox的状态
		cb_show_system.setChecked(show_system);
		//回显的结果也需要展示出来
		if(show_system){
			cb_show_system.setText("显示系统进程");
		}else{
			cb_show_system.setText("关闭系统进程");
		}
		cb_show_system.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					//当点击这个checkbooks的时候， 打钩， 显示系统进程
					cb_show_system.setText("显示系统进程");
				}else{
					//不点的时候， 就关闭系统的进程
					cb_show_system.setText("关闭系统进程");
				}
				//由于下次进来的时候， 需要回显操作， 因此需要将这个状态存储到sharedpreference中
				SpUtils.putBoolean(ProcessSettingActivity.this, ContentValue.SHOW_SYSTEM, isChecked);
			}
		});
		
		
	}
}
