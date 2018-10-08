package com.google.mobilesafe.receiver;

import com.google.mobilesafe.utils.ContentValue;
import com.google.mobilesafe.utils.SpUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//当手机重启的时候， 就检查sim卡是否和在sharedpreference中保存的sim卡号是否相等。
		//获取系统的sim卡序列号,需要获得电话管理器
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		//通过电话管理器来获得sim卡的序列号
		String simSerialNumber = tm.getSimSerialNumber();
		simSerialNumber=simSerialNumber+"123";//人为的修改这个序列号
		//然后得到sharedpreference中保存的sim卡号
		String sim_number = SpUtils.getString(context, ContentValue.SIM_NUMBER, "");
		if(!simSerialNumber.equals(sim_number)){
			//如果不相等，就发送一个短信到安全的手机， 
			//首先需要得到短信管理者，
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage("5556", null, "your phone have been stolen", null, null);
		}
	}

}
