package com.google.mobilesafe.view;

import com.google.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;
//通过这个类，去加载此段的布局文件，自定义组合控件
public class SettingClickItem extends RelativeLayout {

	private TextView tv_des;
	private TextView tv_title;

	public SettingClickItem(Context context) {
		this(context,null);
	}

	public SettingClickItem(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public SettingClickItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		//这样的话， 那么就无论如何都会调用这个方法了
		//通过xml--》view， 将设置界面的一个条目转换成view对象， 然后直接添加到当前的settingviewItem
		View view = View.inflate(context, R.layout.setting_click_item, this);
		tv_title = (TextView) view.findViewById(R.id.tv_title);
	    tv_des = (TextView) view.findViewById(R.id.tv_des);
		//获取自定义属性和原生的属性，写在此处， 由AttributeSet attrs来获取
		//需要获得自定义的属性
		//动态的设置到这个每个条目中的各个控件中
	}
	public void setTitle(String title){
		tv_title.setText(title);
	}
	public void setDes(String des){
		tv_des.setText(des);
	}
}
