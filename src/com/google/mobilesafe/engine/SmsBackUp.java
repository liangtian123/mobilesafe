package com.google.mobilesafe.engine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import com.google.mobilesafe.activity.AToolActivity.CallBack;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsBackUp {
	private static int index=0;
	private static Cursor cursor;
	private static FileOutputStream fos;
	public static void backup(Context context, String path,
			CallBack callBack) {
		try {
			//需要读取短信的内容,首先需要获得他的内容解析者
			//获取备份短信写入的文件
			File file = new File(path);
			cursor = context.getContentResolver().query(Uri.parse("content://sms/"), 
					new String[]{"address","date","type","body"}, null, null, null);
			fos = new FileOutputStream(file);
			//序列化数据库中的数据，放置到xml中
			XmlSerializer newSerializer = Xml.newSerializer();
			newSerializer.setOutput(fos, "utf-8");
			newSerializer.startDocument("utf-8", true);
			newSerializer.startTag(null, "smss");
			//需要给进度条来设置总进度个数
			if(callBack!=null){
				callBack.setMax(cursor.getCount());
			}
			
			if(cursor!=null){
				while (cursor.moveToNext()) {
					newSerializer.startTag(null, "sms");
					
					newSerializer.startTag(null, "address");
					newSerializer.text(cursor.getString(0));
					newSerializer.endTag(null, "address");
					
					newSerializer.startTag(null, "date");
					newSerializer.text(cursor.getString(1));
					newSerializer.endTag(null, "date");
					
					newSerializer.startTag(null, "type");
					newSerializer.text(cursor.getString(2));
					newSerializer.endTag(null, "type");
					
					newSerializer.startTag(null, "body");
					newSerializer.text(cursor.getString(3));
					newSerializer.endTag(null, "body");
					
					newSerializer.endTag(null, "sms");
					
					//走一位， 那么增加一位
					index++;
					//由于需要让用户设置睡眠，让客户放心， 因此需要睡一下
					Thread.sleep(500);
					//对于进度条对话框，可以在子线程中直接更新UI。
					if(callBack!=null){
						callBack.setProgress(index);
					}
					
					
				}
			}
			newSerializer.endTag(null, "smss");
			newSerializer.endDocument();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
				try {
					if(cursor!=null&&fos!=null){
					cursor.close();
					fos.close();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
