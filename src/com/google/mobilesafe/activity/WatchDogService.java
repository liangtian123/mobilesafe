package com.google.mobilesafe.activity;

import java.util.List;

import com.google.mobilesafe.db.AppLockDao;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;

public class WatchDogService extends Service {
	private boolean isWatch;
	private AppLockDao mDao;
	private MyReceiver myReceiver;
	private String mSkipPackageName;
	private List<String> mPackageNameList;
	private MyContentObserver myContentObserver;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mDao = AppLockDao.getInstance(getApplicationContext());
		isWatch = true;
		watch();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.SKIP");
		myReceiver = new MyReceiver();
		registerReceiver(myReceiver, intentFilter);
		
		myContentObserver = new MyContentObserver(new Handler());
		getContentResolver().registerContentObserver(Uri.parse("content://applock/change"), true, myContentObserver);
	}
	class MyContentObserver extends ContentObserver{

		public MyContentObserver(Handler handler) {
			super(handler);
			// TODO Auto-generated constructor stub
		}
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					//当数据发生改变的话， 就需要让mPackageNameList从新获取数据
					mPackageNameList = mDao.findAll();
				}
			}).start();
		}
	}
	class MyReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			//当接收到了这个广播，就从inten中得到这个包名，
			mSkipPackageName = intent.getStringExtra("packageName");
		}
	}
	private void watch() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				mPackageNameList = mDao.findAll();
				//然后开始监测
				while(isWatch){
					//监听现在正在开启的应用，任务栈
					//获取界面的管理者
					ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
					//获取正在开启任务的任务栈
					List<RunningTaskInfo> runningTasks = am.getRunningTasks(1);
					RunningTaskInfo runningTaskInfo = runningTasks.get(0);
					//在这个任务栈中得到栈顶的activity,然后得到这个activity的包名
					String packageName = runningTaskInfo.topActivity.getPackageName();
					//然后拿到这个包名在加锁数据库得到的集合中比对，看是否是加锁的包名， 如果是，那么就拦截
					if(mPackageNameList.contains(packageName)){
						if(!packageName.equals(mSkipPackageName)){
							//弹出拦截界面
							Intent intent = new Intent(getApplicationContext(),EnterPsdActivity.class);
							//由于在服务中开启一个独立的activity，因此需要有任务在来维护
							intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							intent.putExtra("packageName", packageName);
							startActivity(intent);
						}
					}
				}
			}
		}).start();
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
//		循环也结束， 停止看门狗的循环
		isWatch=false;
//		服务停止， 广播接收者也需要注销，
		if(myReceiver!=null){
			unregisterReceiver(myReceiver);
		}
//		内容观察者也需要注销，
		if(myContentObserver!=null){
			getContentResolver().unregisterContentObserver(myContentObserver);
		}

		
	}
}
