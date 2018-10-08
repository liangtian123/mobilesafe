package com.google.mobilesafe.engine;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AddressDao {
	private static final String tag = "AddressDao";
	static //需要查询， 那么需要找到这个数据库所在的文件的位置
	String path="data/data/com.google.mobilesafe/files/address.db";
	private static String mAddress="未知号码";
	private static SQLiteDatabase database;
	//通过用户键入的手机号码来查询
	public static String getAddress(String phone){
		//首先需要创建一个数据库的对象,参数数据库下的的常量只读
		String reg="^1[3-8]\\d{9}";
		if(phone.matches(reg)){
			phone = phone.substring(0, 7);
			database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
			//数据库的查询
			Cursor cursor = database.query("data1", new String[]{"outkey"}, "id=?", new String[]{phone}, null, null, null);
			if(cursor.moveToNext()){
				String outkey = cursor.getString(0);
				Log.i(tag, outkey);
				Cursor indexCursor = database.query("data2", new String[]{"location"}, "id=?", new String[]{outkey}, null, null, null);
				if(indexCursor.moveToNext()){
					mAddress = indexCursor.getString(0);
				}
			}else{
				return "未知号码";
			}
		}else{
			int phone_len = phone.length();
			switch (phone_len) {
				case 3:
					mAddress="报警电话";
					break;
				case 4:
					mAddress="模拟器";
					break;
				case 5:
					mAddress="服务电话";
					break;
				case 7:
					mAddress="本地电话";
					break;
				case 8:
					mAddress="本地电话";
					break;
				case 11:
					//(3+8) 区号+座机号码（外地）
					String area = phone.substring(1, 3);
					//数据库的查询
					Cursor indexCursor = database.query("data2", new String[]{"location"}, "area=?", new String[]{area}, null, null, null);
					if(indexCursor.moveToNext()){
						mAddress = indexCursor.getString(0);
					}else{
						return "未知号码";
						}
					break;
				case 12:
					String area1 = phone.substring(1, 4);
					//数据库的查询
					Cursor indexCursor1 = database.query("data2", new String[]{"location"}, "area=?", new String[]{area1}, null, null, null);
					if(indexCursor1.moveToNext()){
						mAddress = indexCursor1.getString(0);
					}else{
						return "未知号码";
						}
					break;
			}
		}
		return mAddress;
	}
}
