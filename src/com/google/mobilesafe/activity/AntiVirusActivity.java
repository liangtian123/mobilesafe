package com.google.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.mobilesafe.R;
import com.google.mobilesafe.engine.AntiVirusDao;
import com.google.mobilesafe.utils.Md5Util;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class AntiVirusActivity extends Activity {
	protected static final int SCANNING = 100;
	protected static final int SCAN_FINISH = 200;
	private ImageView iv_scanning;
	private TextView tv_name;
	private ProgressBar pb_bar;
	private LinearLayout ll_add_text;
	private ArrayList<ScanInfo> mVirusScanList;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SCANNING:
				//得到从子线程中得到的数据
				ScanInfo scanInfo=(ScanInfo) msg.obj;
				tv_name.setText(scanInfo.name);
				TextView textView = new TextView(getApplicationContext());
				if(scanInfo.isVirus){
					textView.setTextColor(Color.RED);
					textView.setText("发现病毒："+scanInfo.name);
					
				}else{
					
					textView.setTextColor(Color.BLACK);
					textView.setText("扫描安全："+scanInfo.name);
				}
				ll_add_text.addView(textView,0);
				break;
			case SCAN_FINISH:
				tv_name.setText("扫描完成");
				//停止动画
				iv_scanning.clearAnimation();
				//告知用户卸载病毒
				unInstallVirus();
				break;
			}
		};
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_anti_virus);
		initUI();
		//一打开这个应用，就执行动画
		initAnimation();
		//需要通过md5来测试是否是病毒，在病毒库中， 因此：
		checkVirus();
	}

	protected void unInstallVirus() {
		// TODO Auto-generated method stub
		for(ScanInfo scanInfo:mVirusScanList){
			String packageName = scanInfo.packageName;
			Intent intent = new Intent("android.intent.action.DELETE");
			intent.addCategory("android.intent.category.DEFAULT");
			intent.setData(Uri.parse("package:"+packageName));
			startActivity(intent);
		}
	}

	/**
	 * 需要通过md5来测试是否是病毒，在病毒库中
	 */
	private void checkVirus() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {
			
			

			@Override
			public void run() {
				// TODO Auto-generated method stub
				int index=0;
				//获取病毒库中的所有md5的集合
				List<String> virusList = AntiVirusDao.getVirusList();
				//首先需要获取包管理者
				PackageManager pm = getPackageManager();
				//通过报管理者得到所有的安装包，从而获取包的签名文件
				List<PackageInfo> installedPackages = pm.getInstalledPackages(PackageManager.GET_SIGNATURES+PackageManager.GET_UNINSTALLED_PACKAGES);
				mVirusScanList = new ArrayList<ScanInfo>();
				//给进度条设置最大的进度
				pb_bar.setMax(installedPackages.size());
				//遍历这个系统中所有的安装包
				for (PackageInfo packageInfo : installedPackages) {
					//通过包信息获取签名文件集合
					Signature[] signatures = packageInfo.signatures;
					//通过包信息获取签名文件
					Signature signature = signatures[0];
					//得到这个包的签名文件的md5
					String string = signature.toCharsString();
					//32位字符串，16进制
					String exeMd5 = Md5Util.exeMd5(string);
					//创建一个这样的javaBean对象
					ScanInfo scanInfo = new ScanInfo();
					if(virusList.contains(exeMd5)){
						//说明这个应用是病毒,并且纪录到一个集合中
						scanInfo.isVirus=true;
						mVirusScanList.add(scanInfo);
					}else{
						//说明这个应用不是病毒,并且纪录到一个集合中
						scanInfo.isVirus=false;
					}
					//然后将报名和名称放到javaBean--scanInfo中
					scanInfo.packageName=packageInfo.packageName;
					scanInfo.name=packageInfo.applicationInfo.loadLabel(pm).toString();
					mVirusScanList.add(scanInfo);
					//在扫描的过程中更新进度条
					index++;
					pb_bar.setProgress(index);
					//这里由于遍历的非常快，因此需要睡眠一下,那么就不是匀速的， 而是无规律的，
					try {
						Thread.sleep(50+new Random().nextInt(100));
					} catch (Exception e) {
						// TODO: handle exception
					}
					//遍历过程中，发送消息告知主线程更新UI，textView,添加到listView中
					Message msg = Message.obtain();
					msg.what=SCANNING;
					msg.obj=scanInfo;
					mHandler.sendMessage(msg);
				}
				//遍历结束后，发送消息告知主线程更新UI，textView,添加到listView中
				Message msg = Message.obtain();
				msg.what=SCAN_FINISH;
				mHandler.sendMessage(msg);
			}
		}).start();
		
	}
	class ScanInfo{
		public boolean isVirus;
		public String packageName;
		public String name;
	}
	/**
	 * 给iv_scanning设置旋转动画
	 */
	private void initAnimation() {
		// TODO Auto-generated method stub
		RotateAnimation rotateAnimation = 
				new RotateAnimation(0, 360,
									Animation.RELATIVE_TO_SELF, 0.5f, 
									Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnimation.setDuration(1000);
		//无限的旋转
		rotateAnimation.setRepeatCount(RotateAnimation.INFINITE);
		rotateAnimation.setFillAfter(true);
		iv_scanning.startAnimation(rotateAnimation);
	}

	private void initUI() {
		iv_scanning = (ImageView) findViewById(R.id.iv_scanning);
		tv_name = (TextView) findViewById(R.id.tv_name);
		pb_bar = (ProgressBar) findViewById(R.id.pb_bar);
		ll_add_text = (LinearLayout) findViewById(R.id.ll_add_text);
		
	}
}
