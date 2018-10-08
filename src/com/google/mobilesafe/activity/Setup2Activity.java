package com.google.mobilesafe.activity;

import com.google.mobilesafe.R;
import com.google.mobilesafe.utils.ContentValue;
import com.google.mobilesafe.utils.SpUtils;
import com.google.mobilesafe.utils.ToastUtils;
import com.google.mobilesafe.view.SettingViewItem;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

public class Setup2Activity extends BaseSetupActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setup2);
		//初始话UI
		initUI();
	}
	/**
	 * 初始化UI
	 */
	private void initUI() {
		//由于需要点击这个条目， 因此需要先得到这个控件
		final SettingViewItem siv_sim_bound = (SettingViewItem) findViewById(R.id.siv_sim_bound);
		//首先需要先回显之前是否选中，判断是否选中， 因此需要将其保存到sharedpreference中
		String serialNumber = SpUtils.getString(getApplicationContext(), ContentValue.SIM_NUMBER, "");
		//判断序列号是否为空,那么就回显上次的存储到sharedpreference上的结果
		if(TextUtils.isEmpty(serialNumber)){
			//序列号为空，那么就不打勾，开启
			siv_sim_bound.setCheck(false);
		}else{
			//不为空，就打上勾，绑定
			siv_sim_bound.setCheck(true);
		}
		//还需要是否自己打上了勾， 如果打上了，需要一个点击事件来监听这个动作
		//那么就将条目设置绑定sim卡，并将sim序列号存到sharedpreference中
		//并且强制如果没有打上勾，就不能进入下一个界面，就弹出吐司告诉用户说需要绑定
		//打上勾了，就可以进入下一个界面
		siv_sim_bound.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//点的时候， 就将原来的状态变成和这个相反的，因此需要先获取这个状态
				//这个自定义控件的view与checkbox已经绑定了， 并且事件无法传达到checkbox,设置了属性
				boolean isCheck = siv_sim_bound.isChecked();
				//当这个点击事件发生的话， 就将之前的状态取反
				siv_sim_bound.setCheck(!isCheck);
				//默认是没有选中这个自定义条目的， 因此isCheck默认开始为假,那么取反为真
				if(!isCheck){
					//说明这个勾打上了，因此需要获取sim卡的序列号，并存储到sharedpreference中
					//需要得到电话管理器
					TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					//通过这个电话管理器得到sim卡的序列号
					String simSerialNumber = telephonyManager.getSimSerialNumber();
					//然后将序列号存储sharedpreference中
					SpUtils.putString(getApplicationContext(), ContentValue.SIM_NUMBER, simSerialNumber);
				}else{
					//然后需要将这个sim卡删除（之前的sim卡序列号），以免泄露
					SpUtils.remove(getApplicationContext(), ContentValue.SIM_NUMBER);
				}
			}
		});
	}
	//点击进入第三个界面
//	public void nextPage(View v){
//		//当勾上了， 说明序列号已经存储了， 因此才可以跳转
//		String serialNumber = SpUtils.getString(getApplicationContext(), ContentValue.SIM_NUMBER, "");
//		if(!TextUtils.isEmpty(serialNumber)){
//			Intent intent = new Intent(this,Setup3Activity.class);
//			startActivity(intent);
//			finish();
//			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
//		}else{
//			ToastUtils.show(getApplicationContext(), "请绑定sim卡");
//			
//		}
//		
//	}
	//点击进入第一个界面
//	public void prePage(View v){
//		Intent intent = new Intent(this,Setup1Activity.class);
//		startActivity(intent);
//		finish();
//		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
//	}
	@Override
	public void showPrePage() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this,Setup1Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.pre_in_anim, R.anim.pre_out_anim);
	}
	@Override
	public void showNextPage() {
		// TODO Auto-generated method stub
		//当勾上了， 说明序列号已经存储了， 因此才可以跳转
		String serialNumber = SpUtils.getString(getApplicationContext(), ContentValue.SIM_NUMBER, "");
		if(!TextUtils.isEmpty(serialNumber)){
			Intent intent = new Intent(this,Setup3Activity.class);
			startActivity(intent);
			finish();
			overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
		}else{
			ToastUtils.show(getApplicationContext(), "请绑定sim卡");
			
		}
	}
}
