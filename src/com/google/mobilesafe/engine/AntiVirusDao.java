package com.google.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



public class AntiVirusDao {
	private static final String TAG = "AntiVirusDao";
	 //需要查询， 那么需要找到这个数据库所在的文件的位置
	static String path="data/data/com.google.mobilesafe/files/antivirus.db";
	//打开数据库， 获取数据
	public static List<String> getVirusList(){
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.query("datable", new String[]{"md5"}, null, null, null, null, null);
		List<String> virusList = new ArrayList<String>();
		while(cursor.moveToNext()){
			virusList.add(cursor.getString(0));
		}
		cursor.close();
		db.close();
		return virusList;
	}
}
