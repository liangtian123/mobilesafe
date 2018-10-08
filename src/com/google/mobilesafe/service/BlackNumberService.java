package com.google.mobilesafe.service;

import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;
import com.google.mobilesafe.db.BlackNumberDao;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;

public class BlackNumberService extends Service {
	private InnerSmsReceiver innerSmsReceiver;
	private TelephonyManager mTm;
	private BlackNumberDao mDao;
	private MyContentObserver myContentObserver;
	private MyPhoneStateListener mPhoneStateListener;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//放到这里是为了防止空指针异常的情况
		mDao= BlackNumberDao.getInstance(getApplicationContext());
		//拦截短信
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
		intentFilter.setPriority(1000);
		innerSmsReceiver = new InnerSmsReceiver();
		registerReceiver(innerSmsReceiver, intentFilter);
		mTm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mPhoneStateListener = new MyPhoneStateListener();
		mTm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
	class MyPhoneStateListener extends PhoneStateListener{ 
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				//在响铃的时候， 就挂断电话
//				mTm.endCall(incomingNumber);
				endCall(incomingNumber);
				break;
			}
		}
	}
	public void endCall(String incomingNumber) {
		// TODO Auto-generated method stub
		int mode = mDao.getMode(incomingNumber);
		if(mode==2||mode==3){
			//说明是拦截电话的
			 try {
				 //在一个服务中去调用另一个服务的方法， 需要用到iBinder，
				 //由于ServiceManger此类安卓对开发者隐藏，所以不能直接调用器方法， 需要用到反射的方法
				Class<?> clazz = Class.forName("android.os.ServiceManager");
				Method method = clazz.getMethod("getService",String.class);
				//得到中间人对象
				IBinder iBinder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
				//调用获取aidl文件对象方法
				ITelephony iTelephony = ITelephony.Stub.asInterface(iBinder);
				//得到这个ITelephony对象， 然后调用这个方法endCall
				iTelephony.endCall();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			myContentObserver = new MyContentObserver(new Handler(),incomingNumber);
			getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, myContentObserver);
		}
	}
	class MyContentObserver extends ContentObserver{
		String phone;
		public MyContentObserver(Handler handler,String phone) {
			super(handler);
			this.phone=phone;
			// TODO Auto-generated constructor stub
		}
		//数据库中指定当calls表发生改变的时候，去调用此方法
		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			getContentResolver().delete(Uri.parse("content://call_log/calls"), "number=?", new String[]{phone});
		}
	}
	class InnerSmsReceiver extends BroadcastReceiver{


		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//获取短信的内容，获取短信的电话号码，如果此电话号码在黑名单中，并且设置模式为1或者3，拦截短信
			//1,获取短信的内容
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			//循环遍历短信的过程
			for (Object object : objects) {
				SmsMessage sms = SmsMessage.createFromPdu((byte[])object);
				//获取短信的基本信息
				String address = sms.getOriginatingAddress();
				String messageBody = sms.getMessageBody();
				
				int mode = mDao.getMode(address);
				if(mode==1||mode==3){
					//拦截短信
					abortBroadcast();
				}
			}
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
		if(innerSmsReceiver!=null){
			//当服务销毁的时候，就取消注册广播
			unregisterReceiver(innerSmsReceiver);
		}
		//取消内容观察者的监听
		if(myContentObserver!=null){
			getContentResolver().unregisterContentObserver(myContentObserver);
		}
		//取消电话状态的监听
		if(mPhoneStateListener!=null){
			mTm.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
	}

}
