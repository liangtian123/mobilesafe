package com.google.mobilesafe.db.domain;

import android.graphics.drawable.Drawable;

public class ProcessInfo {
	public String name;//应用名称
	public Drawable icon;//展示应用的图标
	public long memSize;//应用已使用的内存数
	public boolean isCheck;//看是否被选中
	public boolean isSystem;//看是否是系统应用
	public String packageName;//如果没有应用的名称， 那么将包名返回作为名称显示
}
