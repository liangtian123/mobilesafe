package com.google.mobilesafe.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.telephony.SmsManager;

public class LocationService extends Service {
//	并且来获取这个丢失的手机的真实位置
	@SuppressLint("InlinedApi") @Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		 //获取经纬度的坐标
        //1,获取系统定位管理器，
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //2，以最优的方式来获得经纬度的坐标，三种方式获得的坐标可能在不同的
        //场景下会有不同的结果，也需要用到criteria,条件， 标准
        Criteria criteria = new Criteria();
        //允许花费
        criteria.setCostAllowed(true);
        //指定获取经纬度的精确度
        criteria.setAccuracy(Criteria.ACCURACY_HIGH);
        //通过定位管理器来获取最优的提供者，基站的，gps，网络的，
        String bestProvider = lm.getBestProvider(criteria, true);
        MyLocationListener myLocationListener = new MyLocationListener();
        lm.requestLocationUpdates(bestProvider, 0, 0, myLocationListener);
	}
	class MyLocationListener implements LocationListener{

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			double longitude = location.getLongitude();//获取经度
			double latitude = location.getLatitude();//获取纬度
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage("5556", null, "经度："+longitude+"纬度："+latitude, null, null);
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			
		}
		
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
}
