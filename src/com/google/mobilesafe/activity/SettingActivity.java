package com.google.mobilesafe.activity;

import com.google.mobilesafe.R;
import com.google.mobilesafe.service.AddressService;
import com.google.mobilesafe.service.BlackNumberService;
import com.google.mobilesafe.utils.ContentValue;
import com.google.mobilesafe.utils.ServiceUtil;
import com.google.mobilesafe.utils.SpUtils;
import com.google.mobilesafe.view.SettingClickItem;
import com.google.mobilesafe.view.SettingViewItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.style.BulletSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingActivity extends Activity {
	protected static final String tag = "SettingActivity";
	private SettingClickItem siv_location;
	private String[] mToastStyleDes;
	private int mToastStyle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);		
		//需要初始化更新
		initUpdate();
		/*
		 * 1，当在设置中心界面需要设置自定义的组合控件， 然后需要点击
		 * 2，状态就会发生变化，
		 * 是否开启归属地的一个判断的条件就是当开启归属地的时候， 就打开这个服务， 那么这个就需要绑定了
		 * 当打电话的时候，电话界面就会出现一个吐司，这个吐司可以
		 * 自定义，由于点击的时候， 是没有界面的， 因此需要用服务来做，由于只有到电话响铃的时候， 
		 * 就会出现这个吐司， 因此需要有个电话的监听。
		 * 需要自定义吐司，需要参考安卓自己的吐司来自定义我们自己的吐司控件，
		 * 3，需要设置这个吐司的显示风格，这个条目也是一个自定义的组合控件。
		 * 当点击的时候，需要弹出一个对话框， 用到单选条目，当点击某个条目的话， 
		 * 效果：1，需要回显到这个条目中，2，需要保存到sharedpreference中，3，关闭对话框
		 * 4，需要在吐司中显示查询的归属地，
		 * 5，当点击归属地提示框位置的时候，会有一个半透明的界面效果，
		 * 6，需要有一个可以拖拽的效果在
		 * */
		//1，电话归属地在手机窗体上悬浮显示
		
		//初始化归属地的显示
		initAddress();
		//初始化吐司的样式
		initToastStyle();
		//吐司的拖拽
		initLocation();
		//设置黑名单
		initBlackNumber();
		//设置程序锁
		initAppLock();
	}

	private void initAppLock() {
		// TODO Auto-generated method stub
		final SettingViewItem siv_app_lock = (SettingViewItem) findViewById(R.id.siv_app_lock);
		boolean isRunning = ServiceUtil.isRunning(this,"com.google.mobilesafe.service.WatchDogService");
		Log.i("该服务是否运行:",""+isRunning);
		siv_app_lock.setCheck(isRunning);
		siv_app_lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isChecked = siv_app_lock.isChecked();
				siv_app_lock.setCheck(!isChecked);
				if(!isChecked){
					startService(new Intent(getApplicationContext(), WatchDogService.class));
				}else{
					stopService(new Intent(getApplicationContext(), WatchDogService.class));
				}
			}
		});
	}

	/**
	 * 设置黑名单
	 */
	private void initBlackNumber() {
		final SettingViewItem siv_blacknumber = (SettingViewItem) findViewById(R.id.siv_blacknumber);
		boolean isRunning = ServiceUtil.isRunning(this,"com.google.mobilesafe.service.BlackNumberService");
		Log.i("该服务是否运行:",""+isRunning);
		siv_blacknumber.setCheck(isRunning);
		siv_blacknumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				boolean isChecked = siv_blacknumber.isChecked();
				siv_blacknumber.setCheck(!isChecked);
				if(!isChecked){
					startService(new Intent(getApplicationContext(), BlackNumberService.class));
				}else{
					stopService(new Intent(getApplicationContext(), BlackNumberService.class));
				}
			}
		});
	}

	/**
	 * 吐司的拖拽
	 */
	private void initLocation() {
		// TODO Auto-generated method stub
		SettingClickItem siv_location = (SettingClickItem) findViewById(R.id.siv_location);
		siv_location.setTitle("归属地显示框的位置");
		siv_location.setDes("设置归属地显示框的位置");
		//跳转到一个半透明的界面
		siv_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(),ToastLocationActivity.class));
			}
		});
	}

	/**
	 * 初始化吐司的样式
	 */
	private void initToastStyle() {
		siv_location = (SettingClickItem) findViewById(R.id.siv_toast_style);
		siv_location.setTitle("设置归属地显示风格");
		//定义这个字符串的数组
		mToastStyleDes = new String[]{"透明","橙色","蓝色","灰色","绿色"};
		mToastStyle = SpUtils.getInt(getApplicationContext(), ContentValue.TOAST_STYLE, 0);
		siv_location.setDes(mToastStyleDes[mToastStyle]);
		siv_location.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				showAlertDialog();
			}
		});
	}
	//注意： dialog是依赖于activity存在的
	protected void showAlertDialog() {
		// TODO Auto-generated method stub
		Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle("请选择归属地样式");
		builder.setSingleChoiceItems(mToastStyleDes, mToastStyle, new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
					//1，需要回显到这个条目中，2，需要保存到sharedpreference中，3，关闭对话框
					SpUtils.putInt(getApplicationContext(), ContentValue.TOAST_STYLE, which);
					siv_location.setDes(mToastStyleDes[which]);
					dialog.dismiss();
				}
			}
		);
		//消极按钮
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}

		});
		builder.show();
	}

	/**
	 * 初始化归属地的显示
	 */
	private void initAddress() {
		//获取这个自定义的组合控件
		final SettingViewItem siv_address = (SettingViewItem) findViewById(R.id.siv_address);
		
		//对服务是否开着的状态做显示，从而达到回显的效果
		boolean running = ServiceUtil.isRunning(this, "com.google.mobilesafe.service.AddressService");
		siv_address.setCheck(running);
		
		//2，为这个组合控件来设置点击事件
		siv_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//当点击这个按钮的时候， 就跳转到一个服务中，并且把状态改变
				//首先获取点击之前的状态
				boolean isChecked = siv_address.isChecked();
				//点击的时候， 将之前的状态变成和这相反的
				siv_address.setCheck(!isChecked);
				Log.i(tag, "当前的点击前的状态："+isChecked);
				if(!isChecked){
					//当点击的时候，为真，就开启服务,
					startService(new Intent(getApplicationContext(),AddressService.class));
				}else{
					//否则，当点击的时候，为假，就开启服务,
					stopService(new Intent(getApplicationContext(),AddressService.class));
				}
			}
		});
	}

	/**
	 * 
	 */
	private void initUpdate() {
		// TODO Auto-generated method stub
		Log.i("SettingActivity", "输出了");
		final SettingViewItem siv_update = (SettingViewItem) findViewById(R.id.siv_update);
		//获取已有的开关灯饿状态， 用作显示
		boolean open_update = SpUtils.getBoolean(getApplicationContext(), ContentValue.OPEN_UPDATE, false);
		//是否选中， 根据上次存储的结果去做决定
		siv_update.setCheck(open_update);
		siv_update.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//如果之前是选中的， 那么点击后， 变成未选中的
				//如果之前是未选中的， 那么点击后， 变成选中的
				//获取之前的选中状态
				boolean checked = siv_update.isChecked();//和open_update是一样的结果。
				//将原来的状态取反，
				siv_update.setCheck(!checked);
				//当点击的时候， 需要把当前的状态记住， 下次登录的时候， 就可以回显了，需要定义一个工具类来记录当前的状态
				SpUtils.putBoolean(getApplicationContext(), ContentValue.OPEN_UPDATE, !checked);
				
			}
		});
	}
}
