package com.google.mobilesafe.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtils {

	public static void show(Context context, String string) {
		// TODO Auto-generated method stub
		Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
	}

}
