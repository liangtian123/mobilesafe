package com.google.mobilesafe.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Md5Util {

	public static String exeMd5(String psd) {
		try {
			//首先需要获取md5的一个消化器,MessageDigest.getInstance("MD5");
			MessageDigest digest = MessageDigest.getInstance("MD5");
			//然后将这个密码的字节码文件转变成md5的字节码
			byte[] bytes = digest.digest(psd.getBytes());
			StringBuffer sb=new StringBuffer();
			for(byte b:bytes){
				int i=b & 0xff;//将字节码转变成整数
				//将这个整数转变成16进制
				String hexString = Integer.toHexString(i);
//				System.out.println(hexString);
				if(hexString.length()==1){
					hexString="0"+hexString;
				}
//				System.out.println(hexString);
				sb.append(hexString);
			}
//			System.out.println(sb.toString());
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
