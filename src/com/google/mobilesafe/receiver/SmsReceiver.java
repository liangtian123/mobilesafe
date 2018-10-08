package com.google.mobilesafe.receiver;

import com.google.mobilesafe.R;
import com.google.mobilesafe.service.LocationService;
import com.google.mobilesafe.utils.ContentValue;
import com.google.mobilesafe.utils.SpUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

	private static final String tag = "SmsReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//设置好了配置文件后， 如果安全手机为这个应用发送了一条短信的话， 那么这个
		//广播就接收到了， 如果短信的内容中包含#*alarm*#，就发出一个服务， 
		//那么就播放音乐；
		//1，这里首先需要一个条件， 就是需要开启防盗保护，
		boolean open_update = SpUtils.getBoolean(context, ContentValue.OPEN_UPDATE, false);
		if(open_update){
			//2，说明防盗保护开启了，需要获取短信的内容，需要短信
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for(Object object:objects){
				//3，得到Smsmessage对象
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[])object);
				//4，通过这个对象得到短信的内容
				String address = smsMessage.getOriginatingAddress();
				String messageBody = smsMessage.getMessageBody();
				Log.i(tag, address+"........"+messageBody);
				//5，然后看是否包含#*alarm*#
				if(messageBody.contains("#*alarm*#")){
					//6, 开启音乐
					MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ylzs);
					//设置循环播放
					mediaPlayer.setLooping(true);
					//开启播放
					mediaPlayer.start();
				}
				//如果接收到的短信包含#*location*#,那么就开启一个服务，
				//并且来获取这个丢失的手机的真实位置，和偏振位置
				if(messageBody.contains("#*location*#")){
					context.startService(new Intent(context,LocationService.class));
				}
				
			}
			
			
		}
	}

}
