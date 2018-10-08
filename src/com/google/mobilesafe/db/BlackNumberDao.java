package com.google.mobilesafe.db;

import java.util.ArrayList;

import com.google.mobilesafe.db.domain.BlackNumberInfo;
import com.google.mobilesafe.utils.ContentValue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class BlackNumberDao {
	private BlackNumberOpenHelper blackNumberOpenHelper;

	//BlackNumberDao的单例设计模式
//	1，构造方法私有化，
	private BlackNumberDao(Context context){
		blackNumberOpenHelper = new BlackNumberOpenHelper(context);
	}
//	2，创建一个私有的这个类的对象,声明一个类的对象
	private static BlackNumberDao blackNumberDao=null;
//	3，对外提供一个方法来获取这个类的对象
	public static BlackNumberDao getInstance(Context context){
		if(blackNumberDao==null){
			blackNumberDao=new BlackNumberDao(context);
		}
		return blackNumberDao;
	}
	//这个类需要提供增删查改的方法， 以方便提供数据，存储,这个是将数据插入表中。
	/**
	 * 增加一个条目
	 * @param phone 拦截电话号码
	 * @param mode 拦截的类型（1，短信 2，电话 3，所有）
	 */
	public void insert(String phone,String mode){
		//开启数据库， 准备做写入操作
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("phone", phone);
		//这里需要匹配， 但是在做的时候， 写成了mold。这是错误的。
		values.put("mode", mode);
		db.insert("blacknumber", null, values);
		Log.i("插入数据", "电话："+phone+",,模式："+mode);
		db.close();
	}
	public void delete(String phone){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		db.delete("blacknumber", "phone=?", new String[]{phone});
		db.close();
	}
	public void update(String phone,String mode){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("mode", mode);
		db.update("blacknumber", values, "phone = ?", new String[]{phone});
		db.close();
	}
	public ArrayList<BlackNumberInfo> findAll(){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.query("blacknumber",new String[]{"phone","mode"}, null, null, null, null, "_id desc");
		
		//将查询的结果放到一个集合中。
		//这里创建一个集合
		ArrayList<BlackNumberInfo> list = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			String phone = cursor.getString(0);
			String mode = cursor.getString(1);
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setPhone(phone);
			blackNumberInfo.setMode(mode);
			list.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return list;
	}
	public ArrayList<BlackNumberInfo> findAll(int index){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		Cursor cursor = db.rawQuery("select phone,mode from blacknumber order by _id desc limit ?,20", new String[]{index+""});
		
		//将查询的结果放到一个集合中。
		//这里创建一个集合
		ArrayList<BlackNumberInfo> blackNumberList = new ArrayList<BlackNumberInfo>();
		while(cursor.moveToNext()){
			String phone = cursor.getString(0);
			String mode = cursor.getString(1);
			BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
			blackNumberInfo.setPhone(phone);
			blackNumberInfo.setMode(mode);
			blackNumberList.add(blackNumberInfo);
		}
		cursor.close();
		db.close();
		return blackNumberList;
	}
	public int getCount(){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		int count=0;
		Cursor cursor = db.rawQuery("select count(*) from blacknumber", null);
		if(cursor.moveToNext()){
			count=cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return count;
	}
	public int getMode(String phone){
		SQLiteDatabase db = blackNumberOpenHelper.getWritableDatabase();
		int mode=0;
		Cursor cursor = db.query("blacknumber", new String[]{"mode"}, "phone=?", new String[]{phone}, null, null, null);
		if(cursor.moveToNext()){
			mode=cursor.getInt(0);
		}
		cursor.close();
		db.close();
		return mode;
	}
}	
