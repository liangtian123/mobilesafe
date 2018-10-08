package com.google.mobilesafe.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamStringUtil {

	public static String stream2String(InputStream inputStream) {
		//建立一个字节数组输出流， 将字节写入内存中
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] buffer=new byte[1024];
		int len=0;
		try {
			while((len=inputStream.read(buffer))!=-1){
				byteArrayOutputStream.write(buffer, 0, len);
				return byteArrayOutputStream.toString();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				inputStream.close();
				byteArrayOutputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
}
