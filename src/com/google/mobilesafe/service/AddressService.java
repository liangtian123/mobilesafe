package com.google.mobilesafe.service;

import javax.crypto.spec.IvParameterSpec;

import com.google.mobilesafe.R;
import com.google.mobilesafe.engine.AddressDao;
import com.google.mobilesafe.utils.ContentValue;
import com.google.mobilesafe.utils.SpUtils;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class AddressService extends Service {
	private TelephonyManager mTM;
	private MyPhoneStateListener mPhoneStateListener;
	private static final String tag = "AddressService";
	private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
	private WindowManager mWM;
	private View toast_view;
	private String mAddress;
	private TextView tv_toast;
	private int[] mDrawableIds;
	private int mScreenwidth;
	private int mScreenheight;
	private InnerOutCallReceiver innerOutCallReceiver;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			tv_toast.setText(mAddress);
		};
	};
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
  		
		mTM = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		mPhoneStateListener = new MyPhoneStateListener();
		mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		
		//动态的建立一个广播接受者用于接收打出电话的广播
		//首先需要给这个广播设置接收广播的类型
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
		//需要用一个类来继承BroadcastReceiver，得到这个对象，然后在注册这个广播。
		innerOutCallReceiver = new InnerOutCallReceiver();
		registerReceiver(innerOutCallReceiver, intentFilter);
	}
	class InnerOutCallReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			//当接收到广播的时候， 就获取这个值
			String phone = getResultData();
			//得到这个phone,在打电话的界面中显示出自定义的吐司
			showToast(phone);
		}
	}
	//需要复写PhoneStateListener的onCallStateChanged来监听电话的不同的状态
	class MyPhoneStateListener extends PhoneStateListener{
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				//当电话空闲的时候的状态
				//当处于空闲状态的时候，没有任何的操作，就需要把这个吐司给关闭
				Log.i(tag, "当电话空闲的时候的状态");
				if(mWM!=null&&toast_view!=null){
					//当view不为空的时候， 就移除
					mWM.removeView(toast_view);
				}
				
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				//当电话处于响铃的时候的状态，就出现一个自定义的吐司
				Log.i(tag, "当电话处于响铃的时候的状态");
				//展示自定义的控件
				showToast(incomingNumber);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				//当电话处于摘机的时候的状态
				Log.i(tag, "当电话处于摘机的时候的状态");
				break;
			}
		}
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * 显示自定义控件
	 * @param incomingNumber 对方打入的号码
	 */
	public void showToast(String incomingNumber) {
		// TODO Auto-generated method stub
		// XXX This should be changed to use a Dialog, with a Theme.Toast
        // defined that sets up the layout params appropriately.
//		Toast.makeText(context, "", duration).show();
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE 默认是可以触摸的，
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        params.format = PixelFormat.TRANSLUCENT;
//        params.windowAnimations = com.android.internal.R.style.Animation_Toast;
        //改为和电话一样的类型
        params.type = WindowManager.LayoutParams.TYPE_PHONE;
        //设置吐司的起始位置：左+上角
        params.gravity= Gravity.LEFT+Gravity.TOP;
        //这个v代表这个填充的控件
        toast_view = View.inflate(getApplicationContext(),R.layout.toast_view, null);
        //这个布局控件中的子控件
        tv_toast = (TextView) toast_view.findViewById(R.id.tv_toast);
        mWM = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mScreenwidth = mWM.getDefaultDisplay().getWidth();
        mScreenheight = mWM.getDefaultDisplay().getHeight();
      //当再次进来的时候，就显示上次的位置,指定这个参数的位置
        params.x = SpUtils.getInt(getApplicationContext(), ContentValue.LOCATION_X, 0);
        params.y = SpUtils.getInt(getApplicationContext(), ContentValue.LOCATION_Y, 0);
        
        //然后将这个布局文件转换成的view对象，设置到窗体中。
        mWM.addView(toast_view, params);
        //获取来电号码的归属地
        query(incomingNumber);
//        params.setTitle("Toast");
        //通过sp中的索引， 来给出吐司的一个样式
        int toast_style= SpUtils.getInt(getApplicationContext(), ContentValue.TOAST_STYLE, 0);
        mDrawableIds = new int[]{
        		R.drawable.call_locate_white,
        		R.drawable.call_locate_orange,
        		R.drawable.call_locate_blue,
        		R.drawable.call_locate_gray,
        		R.drawable.call_locate_green};
        tv_toast.setBackgroundResource(mDrawableIds[toast_style]);
        
        //这里让他有个拖拽的效果。
        toast_view.setOnTouchListener(new OnTouchListener() {
			
			private int startX;
			private int startY;
			private int endX;
			private int endY;

			@Override
			public boolean onTouch(View toast_view, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_MOVE:
					endX = (int) event.getRawX();
					endY = (int) event.getRawY();
					int disX=endX-startX;
					int disY=endY-startY;
					//获取偏移后的位置的
					params.x =params.x+disX;
					params.y =params.y+disY;
					//这里的偏移位置需要有一个容错的范围
					if(params.x<0||params.x>mScreenwidth-tv_toast.getWidth()||params.y<0||params.y>mScreenheight-22-tv_toast.getHeight()){
						return true;
					}
					//通过窗体的管理者来更新控件的布局
					mWM.updateViewLayout(toast_view, params); 
					//改变初始的位置
					startX = (int) event.getRawX();
					startY = (int) event.getRawY();
					break;
				case MotionEvent.ACTION_UP:
					//当手指离开的时候， 就记录离开时的这个控件的位置
					SpUtils.putInt(getApplicationContext(), ContentValue.LOCATION_X, params.x);
					SpUtils.putInt(getApplicationContext(), ContentValue.LOCATION_Y, params.y);
					break;
				}
				return true;
			}
		});
	}
	private void query(final String incomingNumber) {
		// TODO Auto-generated method stub
		//由于查询这个号码需要访问数据库，因此需要在子线程中完成
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				mAddress = AddressDao.getAddress(incomingNumber);
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//当关闭这个服务的时候， 这个监听电话的状态也需要关闭，
		//对象在用的时候， 为了防止空指针异常，保证不能为空， 因此需要判断， 是否为空， 
		Log.i(tag, "当吧服务杀死后， 这个电话的监听没有了监听");
		if(mTM!=null&&mPhoneStateListener!=null){
			mTM.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		}
		if(innerOutCallReceiver!=null){
			//当服务销毁的时候， 就取消注册
			unregisterReceiver(innerOutCallReceiver);
		}
		super.onDestroy();
	}
}
