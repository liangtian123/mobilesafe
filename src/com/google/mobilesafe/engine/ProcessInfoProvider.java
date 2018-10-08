package com.google.mobilesafe.engine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.mobilesafe.db.domain.ProcessInfo;

import android.R;
import android.R.string;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ProcessInfoProvider {
	private static FileReader fileReader;
	private static BufferedReader bufferedReader;
	
	//获取进程总数的方法。
	public static int getProcessCount(Context context){
		//需要得到界面管理者。
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		//获取正在运行的进程的，这是个集合，
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		//然后就得到这个集合的大小， 即为获取这个进程的总数了
		return runningAppProcesses.size();
	}
	//获取可用进程的大小
	public static long getAvailSpace(Context context){
		//获取界面管理者
		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
		//获取内存的大小， 放在了内存信息对象中，因此需要创建这个对象,这里需要导包， 不然的话会出错
		MemoryInfo memoryInfo=new MemoryInfo();
		//通过界面的管理者来获取这个内存的信息
		am.getMemoryInfo(memoryInfo);
		//然后通过这个内存信息对象获取可用内存的大小，并返回值
		return memoryInfo.availMem;
	}
	//获取总共的内存
	public static long getTotalSpace(Context context){
//		//获取界面管理者
//		ActivityManager am = (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
//		//获取内存的大小， 放在了内存信息对象中，因此需要创建这个对象,这里需要导包， 不然的话会出错
//		MemoryInfo memoryInfo=new MemoryInfo();
//		//通过界面的管理者来获取这个内存的信息
//		am.getMemoryInfo(memoryInfo);
//		//然后通过这个内存信息对象获取总共内存的大小，并返回值，但是这里的话， 由于低版本不兼容， 因此无法获取，
//		return memoryInfo.totalMem;
		//这里需要换种方式， 对于每一款手机而言， 内存有多大，都对应着一个配置文件，
		try {
			fileReader = new FileReader("proc/meminfo");
			bufferedReader = new BufferedReader(fileReader);
			//只需要读取一行就可以了
			String lineone = bufferedReader.readLine();
			//由于得到的是Kb,因此需要将他转换成MB,需要将他转变成字符
			char[] charArray = lineone.toCharArray();
			StringBuffer stringBuffer = new StringBuffer();
			for (char c : charArray) {
				if(c<='9'&&c>='0'){
					stringBuffer.append(c);
				}
			}
			Log.i("结果：", stringBuffer.toString());
			return Long.parseLong(stringBuffer.toString())*1024;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(fileReader!=null&&bufferedReader!=null){
				try {
					fileReader.close();
					bufferedReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return 0;
	}
	//获取进程的相关信息
	public static ArrayList<ProcessInfo> getPrecessInfo(Context context){
		//通过界面管理者来获取正在运行的进程，他是一个集合， 然后通过遍历这个集合
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		ArrayList<ProcessInfo> arrayList = new ArrayList<ProcessInfo>();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			//创建这个bean文件， 将进程相关的信息放到这个对象中，
			ProcessInfo processInfo = new ProcessInfo();
			//来获取每个运行的进程， 然后获取这个进程对象的名称，==应用的包名
			processInfo.packageName=runningAppProcessInfo.processName;
			//获取进程占用的内存大小，返回位置为0的对象， 为当前进程的内存信息
			android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{runningAppProcessInfo.pid});
			android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			//获取已使用的大小
			processInfo.memSize= memoryInfo.getTotalPrivateDirty()*1024;
			//获取应用的包名,需要得到包的管理器
			PackageManager pm = context.getPackageManager();
			try {
				//得到应用的信息对象
				ApplicationInfo applicationInfo = pm.getApplicationInfo(processInfo.packageName, 0);
				//这个应用信息对象得到名称，和图标
				processInfo.name=applicationInfo.loadLabel(pm).toString();
				processInfo.icon= applicationInfo.loadIcon(pm);
				//判断是否是系统的应用，用到状态机的概念
				if((applicationInfo.flags&ApplicationInfo.FLAG_SYSTEM)==ApplicationInfo.FLAG_SYSTEM){
					processInfo.isSystem=true;
				}else{
					processInfo.isSystem=false;
				}
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				//如果应用的名称没有找到的话， 那么就用默认的图标来显示，
				processInfo.name=runningAppProcessInfo.processName;
				processInfo.icon=context.getResources().getDrawable(R.drawable.ic_btn_speak_now);
				//由于没有名称，那么说明一定是系统的应用， 如果是手机应用的话， 一定会写应用的名称的，
				processInfo.isSystem=true;
				e.printStackTrace();
			}
			//然后将获取到的每个进程信息添加到集合中， 然后返回出去
			arrayList.add(processInfo);
		}
		//通过集合将这些获取的信息封装
		return arrayList;
	}
	/**
	 * 杀进程方法
	 * @param ctx	上下文环境
	 * @param processInfo	杀死进程所在的javabean的对象
	 */
	public static void killProcess(Context context,ProcessInfo processInfo){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(processInfo.packageName);
	}
	/**
	 * 杀死所有的进程，除了本身之外
	 * @param context 
	 */
	public static void killAll(Context context){
		//1， 需要一个界面的管理者来操作
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		//通过这个借面单饿管理者得到所有运行的进程
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		for (RunningAppProcessInfo runningAppProcessInfo : runningAppProcesses) {
			//遍历所有的正在运行的进程， 如果是等于本身的话， 那么就continue。
			if(runningAppProcessInfo.processName.equals(context.getPackageName())){
				continue;
			}
			//如果不是等于本身的话， 那么就杀死所有得可以杀死的进程
			am.killBackgroundProcesses(runningAppProcessInfo.processName);
		}
		
	}
}
