package com.google.mobilesafe.activity;

import com.google.mobilesafe.R;
import com.google.mobilesafe.engine.AddressDao;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class QueryAddressActivity extends Activity {
	private EditText et_phone;
	private Button bt_query;
	private TextView tv_query_result;
	private String mAddress;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			tv_query_result.setText(mAddress);
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_query_address);
		/*
		 * 电话归属地的查询
		 * 1，实时查询， 点击按钮查循
		 * a,数据库， 查询过程： 
		 *  a1,应用已开启的时候， 将asset中的数据库文件通过流的方式保存到data目录下
		 * 	a2,先在data1表中把需要的外键id找到， 然后再data2中，将对应的外键的归属地找到
		 * 	1.1当应用启动的时候， 这个数据库就已经在项目中了， 
		 * 2，输入框抖动的效果
		 * 3，手机的振动效果
		 * */
		initUI();
	}
	private void query(final String phone) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			@Override
			public void run() {
				mAddress = AddressDao.getAddress(phone);
				mHandler.sendEmptyMessage(0);
			}
		}).start();
		
	}
	private void initUI() {
		et_phone = (EditText) findViewById(R.id.et_phone);
		bt_query = (Button) findViewById(R.id.bt_query);
		tv_query_result = (TextView) findViewById(R.id.tv_query_result);
		bt_query.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String phone = et_phone.getText().toString().trim();
				//由于查询数据库是一个耗时的操作， 因此需要在子线程中来完成
				query(phone);
				
			}
		

		
		});
		et_phone.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				String phone = et_phone.getText().toString().trim();
				query(phone);
			}
		});
	}
}
