package com.google.mobilesafe.activity;

import com.google.mobilesafe.R;
import com.google.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EnterPsdActivity extends Activity {
	private TextView tv_app_name;
	private ImageView iv_app_icon;
	private EditText et_psd;
	private Button bt_submit;
	private String mPackageName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mPackageName = getIntent().getStringExtra("packageName");
		setContentView(R.layout.activit_enter_psd);
		initUI();
		initData();
	}

	private void initData() {
		//由于需要给这个布局的控件中添加内容， 因此需要用到包管理者
		PackageManager pm = getPackageManager();
		try {
			ApplicationInfo applicationInfo = pm.getApplicationInfo(mPackageName, 0);
			Drawable icon = applicationInfo.loadIcon(pm);
			iv_app_icon.setBackgroundDrawable(icon);
			tv_app_name.setText(applicationInfo.loadLabel(pm));
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		bt_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//得到edittext中的内容
				String strPsd = et_psd.getText().toString();
				if(!TextUtils.isEmpty(strPsd)){
					if(strPsd.equals("123")){
						//密码输入正确， 那么就解锁了， 进入到加锁的应用中， 并且告知看门狗不要在去监测该应用和解锁的应用， 因此需要发送一个广播
						//而且需要结束当前的界面
						Intent intent = new Intent("android.intent.action.SKIP");
						intent.putExtra("packageName", mPackageName);
						sendBroadcast(intent);
						finish();
					}else{
						ToastUtils.show(getApplicationContext(), "输入密码错误！");
					}
				}else{
					ToastUtils.show(getApplicationContext(), "请输入密码！");
				}
			}
		});
		
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		//通过隐式意图，跳转到桌面
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		startActivity(intent);
	}
	private void initUI() {
		tv_app_name = (TextView) findViewById(R.id.tv_app_name);
		iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);
		et_psd = (EditText) findViewById(R.id.et_psd);
		bt_submit = (Button) findViewById(R.id.bt_submit);
		
	}
	
}
