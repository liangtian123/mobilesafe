package com.google.mobilesafe.engine;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CommonNumberDao {
	//需要查询， 那么需要找到这个数据库所在的文件的位置
	String path="data/data/com.google.mobilesafe/files/commonnum.db";
	//开启数组
	public List<Group> getGroup(){
		//创建一个数据库对象
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.query("classlist", new String[]{"name","idx"}, null, null, null, null, null);
		List<Group> grouplist = new ArrayList<Group>();
		while (cursor.moveToNext()) {
			String name = cursor.getString(0);
			String idx = cursor.getString(1);
			Group group = new Group();
			group.name=name;
			group.idx=idx;
			group.childlist=getChild(group.idx);
			grouplist.add(group);
		}
		db.close();
		cursor.close();
		return grouplist;
	}
	
	/**
	 * @author Administrator
	 *这里必须设置为public,不然的话， 在其他的类中将无法访问这个类
	 */
	public class Group{
		public String name;
		public String idx;
		public List<Child> childlist;
	}
	//开启数组
	public List<Child> getChild(String idx){
		//创建一个数据库对象
		SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null,SQLiteDatabase.OPEN_READONLY);
		Cursor cursor = db.rawQuery("select * from table"+idx+";", null);
		List<Child> childlist = new ArrayList<Child>();
		while (cursor.moveToNext()) {
			Child child = new Child();
			child._id=cursor.getString(0);
			child.name=cursor.getString(1);
			child.number=cursor.getString(2);
			childlist.add(child);
		}
		db.close();
		cursor.close();
		return childlist;
	}
	public class Child{
		public String _id;
		public String name;
		public String number;
	}
}
