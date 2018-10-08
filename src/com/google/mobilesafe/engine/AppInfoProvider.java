package com.google.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import com.google.mobilesafe.db.domain.AppInfo;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

public class AppInfoProvider {
	/**
	 * 获取所有应用的相关信息。
	 * 应用程序的包名，名称，图标，是否是sdcard,和系统应用isSystem
	 */
	public static List<AppInfo> getAppInfoList(Context context){
		//需要获取应用程序的内容，那么需要从包的管理者对象中获取，由于获取的这些应用，都需要列出在listView中，
		//因此需要将这些获取的内容封装到一个javaBean对象中， 然后添加到集合中
		PackageManager packageManager = context.getPackageManager();
		//获取系统中程序的安装的包
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		//创建一个list集合来存储获取的包名等信息
		ArrayList<AppInfo> list = new ArrayList<AppInfo>();
		//遍历这个包信息的集合
		for (PackageInfo packageInfo : packageInfos) {
			//需要将获取的内容添加到bean文件中
			AppInfo appInfo = new AppInfo();
			//获取包名
			String packageName = packageInfo.packageName;
			appInfo.packageName=packageName;
			//获取某个应用的名称,需要通过包的信息获取应用的信息
			ApplicationInfo applicationInfo = packageInfo.applicationInfo;
			//获取应用名称
			String name = applicationInfo.loadLabel(packageManager).toString();
			appInfo.name=name;
			//获取应用图标
			Drawable icon = applicationInfo.loadIcon(packageManager);
			appInfo.icon=icon;
			//然后判断是否是系统的应用
			if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
				//通过状态机的判断，是否是系统的，那么就属于系统的应用
				appInfo.isSystem=true;
			}else{
				appInfo.isSystem=false;
			}
			//然后判断是否是安装在sdcard上
			if((applicationInfo.flags&ApplicationInfo.FLAG_EXTERNAL_STORAGE)==ApplicationInfo.FLAG_EXTERNAL_STORAGE){
				//通过状态机的判断，是否是系统的，那么就属于系统的应用
				appInfo.isSdCard=true;
			}else{
				appInfo.isSdCard=false;
			}
			list.add(appInfo);
		}
		return list;
		
	}
}
