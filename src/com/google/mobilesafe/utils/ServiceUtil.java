package com.google.mobilesafe.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtil {
	private static ActivityManager mAm;

	public static boolean isRunning(Context context,String serviceName){
		//获取界面管理者， 可以去获取所有的在系统中运行的服务
		mAm = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//获取手机中正在运行的服务的集合
		List<RunningServiceInfo> runningServices = mAm.getRunningServices(100);
		//然后遍历这个运行的服务的集合， 通果serviceName的值看是否在这个正在运行的服务中有没
		for (RunningServiceInfo runningServiceInfo : runningServices) {
			if(serviceName.equals(runningServiceInfo.service.getClassName())){
				return true;
			}
		}
		return false;
	}
}
