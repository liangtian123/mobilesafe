package com.google.mobilesafe.activity;

import java.io.File;

import com.google.mobilesafe.R;
import com.google.mobilesafe.engine.SmsBackUp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AToolActivity extends Activity {
	private TextView tv_query_phone_address,tv_commonnumber_query;
	private ProgressDialog progressDialog;
	private ProgressBar pb_bar;
	private TextView tv_app_lock;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_atool);
		//模块划分用不同的方法来实现
		//电话归属地查询方法
		initPhoneAddress();
		//短信备份
		initSmsBackUp();
		/*GradientDrawable drawable = new GradientDrawable();
		drawable.setGradientType(GradientDrawable.RECTANGLE);
//		drawable.setColor(#fdg);
		drawable.setCornerRadius(radius)*/
		//常用号码查询
		initCommonNumberQuery();
		//程序锁
		initAppLock();
	}

	/**
	 * 程序锁
	 */
	private void initAppLock() {
		//找到程序锁的控件设置点击事件
		tv_app_lock = (TextView) findViewById(R.id.tv_app_lock);
		tv_app_lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
//				跳转到程序锁的界面
				startActivity(new Intent(getApplicationContext(),AppLockActivity.class));
			}
		});
	}

	/**
	 * 常用号码查询
	 */
	private void initCommonNumberQuery() {
		//找到这个控件， 然后再跳转到这个常用号码的界面
		tv_commonnumber_query = (TextView) findViewById(R.id.tv_commonnumber_query);
		tv_commonnumber_query.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(), CommonNumberQueryActivity.class));
			}
		});
	}

	/**
	 * 短信备份 
	 */
	private void initSmsBackUp() { 
		//点击这个条目， 创建这个对话框
		TextView tv_sms_backup = (TextView) findViewById(R.id.tv_sms_backup);
		tv_sms_backup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//当点击的时候，就弹出一个对话框
				showSmsBackUp();
			}
		});
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		
	}

	/**
	 * 这个对话框用于备份短信
	 */
	protected void showSmsBackUp() {
		progressDialog = new ProgressDialog(this);
		//给这个进度条对话框设置图标
		progressDialog.setIcon(R.drawable.ic_launcher);
		//设置标题
		progressDialog.setTitle("短信备份");
		//显示进度条的样式
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		//进度条对话框显示出来
		progressDialog.show();
		//备份文件
		//首先需要从短信的数据库中得到短信。由于备份短信的过程是一个耗时的过程， 因此需要在子线程中运行
//		new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				String path=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"sms.xml";
//				SmsBackUp.backup(getApplicationContext(),path,progressDialog);
//				progressDialog.dismiss();
//			}
//		}).start();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String path=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"sms.xml";
				SmsBackUp.backup(getApplicationContext(),path,new CallBack(){

					@Override
					public void setMax(int max) {
						// TODO Auto-generated method stub
						progressDialog.setMax(max);
						pb_bar.setMax(max);
					}
					@Override
					public void setProgress(int index) {
						// TODO Auto-generated method stub
						progressDialog.setProgress(index);
						pb_bar.setProgress(index);
					}
				});
				progressDialog.dismiss();
			}
		}).start();
	}
	public interface CallBack{
		//需要定义两个需要实现的方法
		void setMax(int max);
		void setProgress(int index);
	}
	/**
	 * 电话归属地查询方法
	 */
	private void initPhoneAddress() {
		tv_query_phone_address = (TextView) findViewById(R.id.tv_query_phone_address);
		tv_query_phone_address.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getApplicationContext(),QueryAddressActivity.class));
			}
		});
	}
}
