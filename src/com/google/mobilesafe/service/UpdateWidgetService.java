package com.google.mobilesafe.service;

import java.util.Timer;
import java.util.TimerTask;

import com.google.mobilesafe.R;
import com.google.mobilesafe.engine.ProcessInfoProvider;
import com.google.mobilesafe.receiver.MyAppWidgetProvider;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {
	private Timer mTimer;
	private InnerReceiver innerReceiver;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//开启这个服务的时候， 那么就会管理进程总数， 和可用内存的更新（需要一个定时器来操作）；
		startTimer();
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
		intentFilter.addAction(Intent.ACTION_SCREEN_ON);
		innerReceiver = new InnerReceiver();
		registerReceiver(innerReceiver, intentFilter);
	}
	class InnerReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
				cancelTimerTask();
			}else{
				startTimer();
			}
		}
		
	}
	/**
	 * 开启这个服务的时候， 那么就会管理进程总数， 和可用内存的更新（需要一个定时器来操作）；
	 */
	private void startTimer() {
		mTimer = new Timer();
		mTimer.scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				updateWidget();
				Log.i("UpdateWidgetService","5秒一次的定时任务正在运行");
			}
		}, 0, 5000);
	}
	/**
	 * 结束这个定时的任务
	 */
	public void cancelTimerTask() {
		// TODO Auto-generated method stub
		if(mTimer!=null){
			mTimer.cancel();
			mTimer=null;
		}
	}
	/**
	 * 这个制定的任务就是更新UI
	 */
	protected void updateWidget() {
		// TODO Auto-generated method stub
		//更新组件的一些信息， 那么就需要用到借面单饿管理者
		AppWidgetManager aWM = AppWidgetManager.getInstance(this);
		
		ComponentName componentName = new ComponentName(this, MyAppWidgetProvider.class);
		
		//获取窗体布局转换成view对象， 
		RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.process_widget);
		//然后给这个view对象中的饿控件赋值
		remoteViews.setTextViewText(R.id.tv_process_count1, "进程总数："+ProcessInfoProvider.getProcessCount(this));
		remoteViews.setTextViewText(R.id.tv_process_memory1, "可用内存："+Formatter.formatFileSize(getApplicationContext(), ProcessInfoProvider.getAvailSpace(this)));
		
		//点击窗体小部件， 进入应用
		//在那个控件上响应这个事件， 2，延迟的意图
		Intent intent = new Intent("android.intent.action.HOME");
		intent.addCategory("android.intent.category.DEFAULT");
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.ll_root, pendingIntent);
		
		//通过这个延期意图发送广播，在广播接收者中杀死进程匹配规则看action
		Intent broadcastIntent = new Intent("android.intent.action.KILL_BACKGROUND_PROCESS");
		PendingIntent broadcast = PendingIntent.getBroadcast(this, 0, broadcastIntent, PendingIntent.FLAG_CANCEL_CURRENT);
		remoteViews.setOnClickPendingIntent(R.id.btn_clear, broadcast);
		
		aWM.updateAppWidget(componentName, remoteViews);
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(innerReceiver!=null){
			unregisterReceiver(innerReceiver);
		}
		cancelTimerTask();
		super.onDestroy();
	}
}
