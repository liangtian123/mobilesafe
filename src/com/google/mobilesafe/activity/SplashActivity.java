package com.google.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.mobilesafe.R;
import com.google.mobilesafe.utils.ContentValue;
import com.google.mobilesafe.utils.SpUtils;
import com.google.mobilesafe.utils.StreamStringUtil;
import com.google.mobilesafe.utils.ToastUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SplashActivity extends Activity {

	private RelativeLayout rl_root;
    private TextView tv_version_name;
	private static final String TAG="SplashActivity";
	private static final int ENTER_HOME = 100;
	private static final int UPDATE_VERSION=101;
	private static final int URL_ERROR=102;
	private static final int IO_ERROR=103;
	private static final int JSON_ERROR=104;
	private String downloadUrl;
	private String versionDes;
	private String versionName;
	
	private int mVersionCode;
	@SuppressLint("HandlerLeak") 
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATE_VERSION:
				//弹出对话框，提示用户更新
				ToastUtils.show(getApplicationContext(),"弹出对话框，提示用户更新");
				showUpdateDialog();
				break;
			case ENTER_HOME:
				//进入主界面
				ToastUtils.show(getApplicationContext(),"进入主界面");
				enterHome();
				break;
			case URL_ERROR:
				//URL错误		
				ToastUtils.show(getApplicationContext(),"URL错误");
				enterHome();
				break;
			case IO_ERROR:
				//IO流异常
				ToastUtils.show(getApplicationContext(),"IO流异常");
				enterHome();
				break;
			case JSON_ERROR:
				//JSon异常
				ToastUtils.show(getApplicationContext(),"JSon异常");
				enterHome();
				break;
			}
		}
	};
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //去除当前activity头title
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        //首先需要初始化UI,对splash界面的版本名称进行更新
        //需要对版本号进行更新， 如果本地的版本高于从服务端获取的版本的话， 那么就不需要更新
        //否则需要对从服务端的版本进行下载， 然后安装到手机中
        	//首先需要初始化UI,对splash界面的版本名称进行更新,显示在界面的组件中
        	initUI();
        	//初始化动画，透明动画效果
        	initAnimation();
        	//调用这个方法，获取初始化数据,对splash界面的版本名称进行更新,显示在界面的组件中
        	initData();
        	//初始化db
        	initDB();
        	
        	//创建这个应用的快捷方式
        	if(!SpUtils.getBoolean(this, ContentValue.HAS_SHORTCUT, false)){
        		initShortCut();
        	}
        	
    }
	/**
	 * 创建这个应用的快捷方式，这里需要设置这个图标，名称，和创建了快捷方式后，跳转到这个应用中
	 */
	private void initShortCut(){
		
		Intent intent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
		
		//通过这个intent意图对象来维护这个快捷方式的图标， 名称
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher));
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "手机卫士第二季");
		//点击这个创建的快捷方式后，跳转到这个应用中
		//需要维护一个这样的一个意图。
		Intent shortCutIntent = new Intent("android.intent.action.HOME");
		shortCutIntent.addCategory("android.intent.category.DEFAULT");
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortCutIntent);
		//发送广播
		sendBroadcast(intent);
		//告知sp来开启这个快捷方式
		SpUtils.putBoolean(getApplicationContext(), ContentValue.HAS_SHORTCUT, true);
		
	}
	/**
	 * 初始化db
	 */
	private void initDB() {
		// TODO Auto-generated method stub
		//归属地的数据库
		initAddressDB("address.db");
		//常用电话号码的数据库
		initAddressDB("commonnum.db");
		//病毒数据库
		initAddressDB("antivirus.db");
		
	}
	
	/**
	 * 拷贝数据库到files文件下
	 * @param dbName 数据库的名称
	 */
	private void initAddressDB(String dbName) {
		// TODO Auto-generated method stub
		//在files文件夹下建立同名的dbName数据库文件
		File filesDir = getFilesDir();
		File file = new File(filesDir,dbName);
		//如果这个文件已经存在的话， 那么就直接结束这个方法
		if(file.exists()){
			return;
		}
		//输入流来读取第三方资产目录的文件
		InputStream input=null;
		FileOutputStream fos=null;
		try {
			input = getAssets().open(dbName);
			fos = new FileOutputStream(file);
			byte[] buffer=new byte[1024];
			int len=-1;
			while((len=input.read(buffer))!=-1){
				fos.write(buffer, 0, len);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(input!=null){
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(fos!=null){
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	private void initAnimation() {
		// TODO Auto-generated method stub
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.0f, 1.0f);
		alphaAnimation.setDuration(3000);
		rl_root.startAnimation(alphaAnimation);
	}
	/**
	 * 通过显示意图，跳转到主界面
	 */
	protected void enterHome() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
		startActivity(intent);
		//当开启一个新的界面， 将原来的导航界面关闭，（导航界面只可以见一次）
		finish();
	}
	/**
	 * 提示用户更新界面，
	 * 立即更新，下载apk，然后需要安装
	 * 稍后再说，进入主界面
	 * 取消事件监听，进入主界面，并且需要结束对话框
	 */
	protected void showUpdateDialog() {
		AlertDialog.Builder builder=new Builder(this);
		builder.setIcon(R.drawable.home_trojan);
		builder.setTitle("升级提醒");
		builder.setMessage(versionDes);
		//积极按钮，立即更新
		builder.setPositiveButton("立即更新", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//下载apk，apk的地址：downloadUrl
				downloadApk();
			}
		});
		//消极按钮， 
		builder.setNegativeButton("稍后再说", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				//取消对话框，跳转到主界面
				enterHome();
			}
		});
		//点击取消事件监听
		builder.setOnCancelListener(new OnCancelListener() {
			
			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				//即使用户点击取消，也需要让用户进入主界面
				enterHome();
				dialog.dismiss();
			}
		});
		//最后一定要记得show()出来
		builder.show();
	}
	/**
	 *下载apk，
	 *需要安排下载这个apk的位置，放到sdcard中
	 *用xutils工具了，来下载，
	 */
	protected void downloadApk() {
		// TODO Auto-generated method stub
		//apk下载地址的连接，设置apk的所在路径
		//下载到sdcard中，首先需要判断sdcard是否挂载
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			//说明已经挂载成功
			//获取sd路径
			String path=Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"mobilesafe74.apk";
			//发送请求，获取apk， 并放置到指定的位置中
			HttpUtils httpUtils = new HttpUtils();
			//发送请求， 传递参数（下载地址，下载应用放置地址）
			httpUtils.download(downloadUrl, path, new RequestCallBack<File>() {
				
				@Override
				public void onSuccess(ResponseInfo<File> responseFile) {
					// TODO Auto-generated method stub
					//下载成功(下载过后， 就放置在sd卡中apk)
					Log.i(TAG, "下载成功");
					File file = responseFile.result;
					//提示用户安装
					installApk(file);
				}
				//刚刚开始下载
				@Override
				public void onStart() {
					// TODO Auto-generated method stub
					Log.i(TAG, "刚刚开始下载");
					super.onStart();
				}
				//下载过程中的方法（下载的总大小，当前的下载位置，是否正在下载）
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					// TODO Auto-generated method stub
					Log.i(TAG, "正在下载。。。。。。");
					super.onLoading(total, current, isUploading);
				}
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					//下载失败
					Log.i(TAG, "下载失败");
				}
			});
		}
	}
	protected void installApk(File file) {
		// TODO Auto-generated method stub
		//安装apk， 需要进入一个系统界面， 定义好的，安装apk入口
		Intent intent = new Intent("android.intent.action.VIEW");
		//调用系统的应用，需要用隐式意图
		//文件作为数据源
		intent.addCategory("android.intent.category.DEFAULT");
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		startActivityForResult(intent, 0);
	}
	/* (non-Javadoc)
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 * 当跳转的这个页面取消后就会返回结果到splash界面，然后就会调用onActivityResult方法， 然后就跳转。
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		enterHome();
	}
	/**
	 * 初始化UI，alt shift J
	 */
	private void initUI() {
		tv_version_name = (TextView) findViewById(R.id.tv_version_name);
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}
	
    /**
	 * 获取初始化数据，对splash界面的版本名称进行更新,显示在界面的组件中
	 */
	private void initData() {
		// TODO Auto-generated method stub
		//获取应用版本的名称,对splash界面的版本名称进行更新,显示在界面的组件中
		tv_version_name.setText("版本名称："+getVersionName());
		//调用getVersionCode(),得到当前的版本号
		mVersionCode = getVersionCode();
		//需要从服务器中得到Json对象，然后需要获取服务器端的版本号,客户端发请求，服务端发响应
//		json中内容的包含：
		/*新版本的描述
		 * 更新版本的版本名称
		 * 服务器中apk的版本号
		 * 新版本的apk下载的地址http://10.0.2.2:33333/JSON.json
		 * */
		//这是就可以通过选定不选定设置中心的开启或者是关闭， 就可以做是否需要自动检查版本
		boolean open_update = SpUtils.getBoolean(getApplicationContext(), ContentValue.OPEN_UPDATE, false);
		ToastUtils.show(getApplicationContext(), "上次的选中状态"+open_update);
		if(open_update){
			//当上次选中的是开启的，那么就更新版本
			checkVersion();
		}else{
			//否则的话， 就不更新版本， 进入主界面
			//但是不能一下子就进入主界面， 需要睡眠一下
			//通过信息机制来做， ENTER_HOME来指向的消息
			mHandler.sendEmptyMessageDelayed(ENTER_HOME, 4000);
		}
	}
		
	/**
	 * 检测版本信息
	 */
	private void checkVersion() {
		//访问服务器端的数据， 他是一个耗时的操作，因此需要在子线程中完成
		new Thread(new Runnable() {
			private Message msg;
			@Override
			public void run() {
				String urlPath="http://10.0.2.2:33333/update.json";
				//记录当前的开始访问网络的时间
				long startTime = System.currentTimeMillis();
				try {
					//这里得到URl对象，
					URL url = new URL(urlPath);
					//通过url对象得到一个打开连接对象
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					//然后需要给这个对象设置一些参数， 像连接超时， 读取超时，相应码,请求方式
					connection.setConnectTimeout(2000);
					connection.setReadTimeout(2000);
					connection.setRequestMethod("GET");
					if(connection.getResponseCode()==200){//说明已经建立了连接
						InputStream inputStream = connection.getInputStream();
						//将流数据转换成字符串
						String jsonStr = StreamStringUtil.stream2String(inputStream);
						Log.i(TAG, jsonStr);
						//将json字符串变成json对象
						JSONObject json = new JSONObject(jsonStr);
						versionName = json.getString("versionName");
						versionDes=json.getString("versionDes");
						String versionCode = json.getString("versionCode");
						downloadUrl = json.getString("downloadUrl");
						Log.i(TAG, versionName);
						Log.i(TAG, versionDes);
						Log.i(TAG, versionCode);
						Log.i(TAG, downloadUrl);
						//需要判断本地的版本号和服务器的版本号大小
						if(mVersionCode<Integer.parseInt(versionCode)){
							//说明服务端版本高，需要提示用户更新
							Log.i(TAG, "服务端版本高，需要提示用户更新");
							msg = Message.obtain();
							msg.what=UPDATE_VERSION;
						}else{
							//说明本地版本高，不需要更新
							Log.i(TAG, "本地版本高，不需要更新");
							msg.what=ENTER_HOME;
						}
					}
				}catch (MalformedURLException e) {
					e.printStackTrace();
					msg.what=URL_ERROR;
				}catch (IOException e) {
					e.printStackTrace();
					msg.what=IO_ERROR;
				}catch (JSONException e) {
					e.printStackTrace();
					msg.what=JSON_ERROR;
				}finally{
					//制定睡眠的时间，请求网络的时长超过4s则不做处理
					//请求的网络时间小于4秒，强制让其睡眠满4秒，那么需要记录当前访问网络的时间
					//记录当前的开始访问网络的时间
					long endTime = System.currentTimeMillis();
					if((endTime-startTime)<2000){
						try {
							Thread.sleep(2000-(endTime-startTime));
							
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					mHandler.sendMessage(msg);
				}
			}
		}).start();
		
	}
	private int getVersionCode() {
		// TODO Auto-generated method stub
		//如何获取版本名称呢？需要通过PackageManager来获取
		PackageManager pm = getPackageManager();
		//通过这个包管理器，获取包名的指定基本信息，传0代表获取基本信息
		try {
			//获取包信息对象，这样就可以得到包所包含的所有的信息
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			Log.i(TAG, Integer.toString(packageInfo.versionCode));
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	/**
	 * 返回当前apk的版本名称,
	 * @return
	 */
	private String getVersionName() {
		//如何获取版本名称呢？需要通过PackageManager来获取
		PackageManager pm = getPackageManager();
		//通过这个包管理器，获取包名的指定基本信息，传0代表获取基本信息
		try {
			//获取包信息对象，这样就可以得到包所包含的所有的信息
			PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
			Log.i(TAG, packageInfo.versionName);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

    
}
