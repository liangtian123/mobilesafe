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
public class SettingViewItem extends RelativeLayout {

	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.google.mobilesafe";
	private CheckBox cb_box;
	private TextView tv_des;
	private String mDesoff;
	private String mDeson;

	public SettingViewItem(Context context) {
		this(context,null);
	}

	public SettingViewItem(Context context, AttributeSet attrs) {
		this(context,attrs,0);
	}

	public SettingViewItem(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		//这样的话， 那么就无论如何都会调用这个方法了
		//通过xml--》view， 将设置界面的一个条目转换成view对象， 然后直接添加到当前的settingviewItem
		View view = View.inflate(context, R.layout.setting_view_item, this);
		//自定义组件中的标题描述,不然会有空指针异常
		TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
		tv_des = (TextView) view.findViewById(R.id.tv_des);
		cb_box = (CheckBox) view.findViewById(R.id.cb_box);
		//获取自定义属性和原生的属性，写在此处， 由AttributeSet attrs来获取
		int count = attrs.getAttributeCount();
		Log.i("count", "属性集合的属性个数"+count);
		//需要获得自定义的属性
		String destitle = attrs.getAttributeValue(NAMESPACE, "destitle");
		mDesoff = attrs.getAttributeValue(NAMESPACE, "desoff");
		mDeson = attrs.getAttributeValue(NAMESPACE, "deson");
		//动态的设置到这个每个条目中的各个控件中
		tv_title.setText(destitle);
	}
	/**
	 * @return 放回当前settingViewItem是否选中状态，
	 * true开启（表示checkbox选中，false关闭（表示checkbox关闭））
	 */
	public boolean isChecked(){
		//有checkbox的选中结果，来决定是否开启这个条目
		return cb_box.isChecked();
	}
	/**
	 * 是否作为开启的变量，有点击过程中去做传递m
	 */
	public void setCheck(boolean isCheck){
		//当前条目在选中过程中，cb_box选中状态也在跟随（ischecked）变化
		//这个方法在点击的过程中调用，
		cb_box.setChecked(isCheck);
		if(isCheck){
			//开启
			tv_des.setText(mDeson);
		}else{
			//关闭
			tv_des.setText(mDesoff);
		}
	}
}
