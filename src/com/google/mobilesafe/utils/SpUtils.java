package com.google.mobilesafe.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SpUtils {
	
	private static SharedPreferences sp;

	public static void putBoolean(Context context,String key,boolean value){
		
		//存储节点文件名称，读写方式
		if(sp==null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putBoolean(key, value).commit();
	}
	public static boolean getBoolean(Context context,String key,boolean defValue){
		
		//存储节点文件名称，读写方式
		if(sp==null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			
		}
		return sp.getBoolean(key, defValue);
	}
	/**
	 * 写入String变量到sp中
	 * @param context 上下文环境
	 * @param key 存储节点的名称
	 * @param value 存储节点的值
	 */
	public static void putString(Context context,String key,String value){
		
		//存储节点文件名称，读写方式
		if(sp==null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putString(key, value).commit();
	}
	/**
	 * 从config xml文件中读取存入的信息
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Context context,String key,String defValue){
		
		//存储节点文件名称，读写方式
		if(sp==null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
			
		}
		return sp.getString(key, defValue);
	}
	/**
	 * @param context 上下文环境
	 * @param key 根据sharedpreference的键来删除操作
	 */
	public static void remove(Context context,String key){
		
		//存储节点文件名称，读写方式
		if(sp==null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		//删除节点
		Editor editor = sp.edit().remove(key);
		editor.commit();
	}
	/**
	 * @param context
	 * @param key	
	 * @param value
	 */
	public static void putInt(Context context,String key,int value){
		
		//存储节点文件名称，读写方式
		if(sp==null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		sp.edit().putInt(key, value).commit();
	}
	/**
	 * @param context
	 * @param key
	 * @param defValue
	 * @return 得到的索引
	 */
	public static int getInt(Context context, String key,
			int defValue) {
		// TODO Auto-generated method stub
		//存储节点文件名称，读写方式
		if(sp==null){
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return sp.getInt(key, defValue);
	}
}
