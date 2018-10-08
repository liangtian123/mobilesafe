package com.google.mobilesafe.activity;

import com.google.mobilesafe.R;
import com.google.mobilesafe.utils.ContentValue;
import com.google.mobilesafe.utils.SpUtils;
import com.google.mobilesafe.utils.ToastUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class Setup3Activity extends BaseSetupActivity {
	private EditText et_phone_number;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup3);
		//初始化数据
		initUI();
	}
	/**
	 */
	private void initUI() {
//		得到编辑栏的控件
		et_phone_number = (EditText) findViewById(R.id.et_phone_number);
		//获取回显的结果
		String phone = SpUtils.getString(getApplicationContext(), ContentValue.CONTACT_PHONE, "");
		et_phone_number.setText(phone);
		//得到联系人的按钮，得到时候，点击这个按钮就进入到联系人的界面，
		Button bt_select_number = (Button) findViewById(R.id.bt_select_number);
		bt_select_number.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getApplicationContext(),ContactListActivity.class);
				startActivityForResult(intent, 0);
				
			}
		});
		
		//当点击某个条目的话， 那么这个当前的事件就触发了，并且将结果显示在编辑框中。
		//也可以输入号码，
		//如果没有号码显示在编辑框中的话， 那么不能到下一步中，判断的已经是不能为空。
		//因此这个电话号码需要
		//然后还需要在到下一页后， 在回到上一页， 这个号码可以回显， 因此需要将这个号码
		//存储到sharedpreference中，以便回显用
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if(intent!=null){
			String phone = intent.getStringExtra("phone");
	//		Log.i("setup3", phone);
			//将结果显示在编辑框中。这里的phone的话， 可能会有-，或是空格
			phone=phone.replace("-", "").replace(" ", "").trim();
			et_phone_number.setText(phone);
			//然后需要把这个存储到sharedpreference中
			SpUtils.putString(getApplicationContext(), ContentValue.CONTACT_PHONE, phone);
		}
	}
	
	//点击进入第四个界面
//	public void nextPage(View v){
//		//这里进入下一个界面就需要有电话号码了
//		//由于输入框都是需啊哟显示号码的， 所以只需要判断这个号码是否为空就可以了，
//		String phone = et_phone_number.getText().toString().trim();
////		String phone = SpUtils.getString(getApplicationContext(), ContentValue.CONTACT_PHONE, "");
//		if(!TextUtils.isEmpty(phone)){
//			//就跳转
//			Intent intent = new Intent(this,Setup4Activity.class);
//			startActivity(intent);
//			finish();
//			//这个输入的电话号码也是需要保存到sharedpreference中的
//			SpUtils.putString(getApplicationContext(), ContentValue.CONTACT_PHONE, phone);
//			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
//		}else{
//			ToastUtils.show(getApplicationContext(), "请输入电话号码");
//		}
//		
//	}
	//点击进入第二个界面
//	public void prePage(View v){
//		Intent intent = new Intent(this,Setup2Activity.class);
//		startActivity(intent);
//		finish();
//		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
//	}
	@Override
	public void showPrePage() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,Setup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}
	@Override
	public void showNextPage() {
		//这里进入下一个界面就需要有电话号码了
		//由于输入框都是需啊哟显示号码的， 所以只需要判断这个号码是否为空就可以了，
		String phone = et_phone_number.getText().toString().trim();
//		String phone = SpUtils.getString(getApplicationContext(), ContentValue.CONTACT_PHONE, "");
		if(!TextUtils.isEmpty(phone)){
			//就跳转
			Intent intent = new Intent(this,Setup4Activity.class);
			startActivity(intent);
			finish();
			//这个输入的电话号码也是需要保存到sharedpreference中的
			SpUtils.putString(getApplicationContext(), ContentValue.CONTACT_PHONE, phone);
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		}else{
			ToastUtils.show(getApplicationContext(), "请输入电话号码");
		}
	}
}
