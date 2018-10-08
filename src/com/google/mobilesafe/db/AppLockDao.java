package com.google.mobilesafe.db;

import java.util.ArrayList;
import java.util.List;

import com.google.mobilesafe.db.domain.BlackNumberInfo;
import com.google.mobilesafe.utils.ContentValue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

public class AppLockDao {
	private AppLockOpenHelper appLockOpenHelper;
	private Context context;
	//BlackNumberDao的单例设计模式
//	1，构造方法私有化，
	private AppLockDao(Context context){
		this.context=context;
		appLockOpenHelper = new AppLockOpenHelper(context);
	}
//	2，创建一个私有的这个类的对象,声明一个类的对象
	private static AppLockDao appLockDao=null;  
//	3，对外提供一个方法来获取这个类的对象
	public static AppLockDao getInstance(Context context){
		if(appLockDao==null){
			appLockDao=new AppLockDao(context);
		}
		return appLockDao;
	}
	//这个类需要提供增删查改的方法， 以方便提供数据，存储,这个是将数据插入表中。
	/**
	 * 增加一个条目
	 * @param phone 拦截电话号码
	 * @param mode 拦截的类型（1，短信 2，电话 3，所有）
	 */
	public void insert(String packagename){
		//开启数据库， 准备做写入操作
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("packagename", packagename);
		db.insert("applock", null, values);
		db.close();
		//由于需要在数据库发生改变的话， 那么就通过内容观察者， 因此这里需要来通知改变
		context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
	}
	public void delete(String packagename){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		db.delete("applock", "packagename=?", new String[]{packagename});
		db.close();
		context.getContentResolver().notifyChange(Uri.parse("content://applock/change"), null);
	}
	//插入后， 已加锁的需要查询得到整个的加锁饿应用的包名
	public List<String> findAll(){
		SQLiteDatabase db = appLockOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("applock", new String[]{"packagename"}, null, null, null, null, null);
		List<String> lockPackageList = new ArrayList<String>();
		while(cursor.moveToNext()){
			lockPackageList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return lockPackageList;
	}
}	
