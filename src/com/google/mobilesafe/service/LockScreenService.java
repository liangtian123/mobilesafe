package com.google.mobilesafe.service;

import com.google.mobilesafe.engine.ProcessInfoProvider;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

public class LockScreenService extends Service {
	private MyLockScreenReceiver myLockScreenReceiver;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		myLockScreenReceiver = new MyLockScreenReceiver();
		IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
		registerReceiver(myLockScreenReceiver, intentFilter);
	}
	class MyLockScreenReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//当这个广播被触发是， 就接收到了这个广播了， 那么：就杀死进程， 但是本身是不能杀死的， 
			//因此：
			ProcessInfoProvider.killAll(context);
		}
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//当这个服务被销毁的话， 那么这个广播接收者就会一起销毁
		if(myLockScreenReceiver!=null){
			unregisterReceiver(myLockScreenReceiver);
		}
	}
}
