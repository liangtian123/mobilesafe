package com.google.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.IvParameterSpec;

import com.google.mobilesafe.R;
import com.google.mobilesafe.db.AppLockDao;
import com.google.mobilesafe.db.domain.AppInfo;
import com.google.mobilesafe.engine.AppInfoProvider;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class AppLockActivity extends Activity {
	private Button bt_unlock,bt_lock;
	private LinearLayout ll_unlock,ll_lock;
	private TextView tv_unlock,tv_lock;
	private ListView lv_lock,lv_unlock;
	private List<AppInfo> mAppInfoList, mLockList,mUnLockList;
	private MyAdapter mLockAdapter,mUnlockAdapter;
	private TranslateAnimation mTranslateAnimation;
	private AppLockDao mAppLockDao;
	private Handler mHandler=new Handler(){

		public void handleMessage(android.os.Message msg) {
			//传入一个true , 说明是加锁的
			mLockAdapter = new MyAdapter(true);
			lv_lock.setAdapter(mLockAdapter);
			//传入一个false , 说明是未加锁的
			mUnlockAdapter = new MyAdapter(false);
			lv_unlock.setAdapter(mUnlockAdapter);//开始这里错误，原因写成了lv_lock 
			//这里需要在已加锁未加锁的应用中名称的改变
			
		};
	};
	
	class MyAdapter extends BaseAdapter{
		//由于都是一个适配器的样式， 因此可以共有同一个，但是需要用是否加锁来区分
		private boolean isLock;
		
		public MyAdapter(boolean isLock) {
			// TODO Auto-generated constructor stub
			this.isLock=isLock;
		}
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			if(isLock){
				tv_lock.setText("加锁应用："+mLockList.size());
				return mLockList.size();
			}else{
				tv_unlock.setText("未加锁应用："+mUnLockList.size());
				return mUnLockList.size();
			}
		}

		@Override
		public AppInfo getItem(int position) {
			if(isLock){
				return mLockList.get(position);
			}else{
				return mUnLockList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder=null;
			// TODO Auto-generated method stub
			if(convertView==null){
				viewHolder = new ViewHolder();
				convertView=View.inflate(getApplicationContext(), R.layout.listview_islock_view, null);
				 viewHolder.iv_icon= (ImageView) convertView.findViewById(R.id.iv_icon);
				 viewHolder.tv_name=(TextView) convertView.findViewById(R.id.tv_name);
				 viewHolder.iv_lock=(ImageView) convertView.findViewById(R.id.iv_lock);
				 convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			//然后需要将这个内容显示在这些子控件中
			final AppInfo appInfo = getItem(position);
			//设置一个临时的变量，这样就可以定义为final了， 不然的话， 设置为final的变量又再次赋值，是会报错的
			final View animationView=convertView;
			viewHolder.iv_icon.setBackgroundDrawable(appInfo.icon);
			viewHolder.tv_name.setText(appInfo.name);
			//后面的未加锁和加锁需要区分
			if(isLock){
				viewHolder.iv_lock.setBackgroundResource(R.drawable.lock);
			}else{
				viewHolder.iv_lock.setBackgroundResource(R.drawable.unlock);
			}
			//点击这个iv_lock设置点击事件，，如果是点击未加锁的， 并且把把这个应用添加到加锁集合中， 未加锁的集合就删除一条应用，
			viewHolder.iv_lock.setOnClickListener(new OnClickListener() {
				

				@Override
				public void onClick(View v) {
					//执行动画
					animationView.startAnimation(mTranslateAnimation);
					mTranslateAnimation.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
						@Override
						public void onAnimationEnd(Animation animation) {
							if(isLock){
								mLockList.remove(appInfo);
								mUnLockList.add(appInfo);
								mAppLockDao.delete(appInfo.packageName);
								mLockAdapter.notifyDataSetChanged();
							}else{
//								如果是点击未加锁的， 并且把把这个应用添加到加锁集合中， 未加锁的集合就删除一条应用
								mLockList.add(appInfo);
								mUnLockList.remove(appInfo);
								mAppLockDao.insert(appInfo.packageName);
								//错误， 这里开始是写mLockAdapter，但是发生变化的是mUnlockAdapter，因此需要将mUnlockAdapter修改，
								mUnlockAdapter.notifyDataSetChanged();
							}
						}
					});
				}
			});
			return convertView;
		}
	}
	static class ViewHolder{
		public ImageView iv_icon,iv_lock;
		public TextView tv_name;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock);
		//初始化UI
		initUI();
		//初始化数据
		initData();
		//初始化动画
		initAnimation();
	}

	private void initAnimation() {
		mTranslateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 1, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0);
		mTranslateAnimation.setDuration(500); 
		
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		new Thread(new Runnable() {
			
			

			@Override
			public void run() {
				// TODO Auto-generated method stub
				//需要获取所有的应用的包名
				
				mAppInfoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				
				//区分已加锁和未加锁的应用
				
				mLockList = new ArrayList<AppInfo>();
				
				mUnLockList = new ArrayList<AppInfo>();
				
				mAppLockDao = AppLockDao.getInstance(getApplicationContext());
				List<String> lockPackageList = mAppLockDao.findAll();
				
				//然后通过遍历所有的应用的包名， 如果其中是已加锁的那么就发动已加锁的集合中
				for(AppInfo appInfo:mAppInfoList){
					if(lockPackageList.contains(appInfo.name)){
						mLockList.add(appInfo);
					}else{
						mUnLockList.add(appInfo);
					}
				}
				//告知主线程更新Ui，那么就可以维护数据了
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		bt_unlock = (Button) findViewById(R.id.bt_unlock);
		bt_lock = (Button) findViewById(R.id.bt_lock);
		
		ll_unlock = (LinearLayout) findViewById(R.id.ll_unlock);
		ll_lock = (LinearLayout) findViewById(R.id.ll_lock);
		
		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		tv_lock = (TextView) findViewById(R.id.tv_lock);
		
		lv_lock = (ListView) findViewById(R.id.lv_lock);
		lv_unlock = (ListView) findViewById(R.id.lv_unlock);
		bt_lock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//当点击已加锁， 那么显示已加锁的listView, 不显示未加锁的，
				ll_lock.setVisibility(View.VISIBLE);
				ll_unlock.setVisibility(View.GONE);
				//并且该按钮的背景图片发生改变
				bt_unlock.setBackgroundResource(R.drawable.tab_left_default);
				bt_lock.setBackgroundResource(R.drawable.tab_right_pressed);
			}
		});
		bt_unlock.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//当点击未加锁， 那么显示未加锁的listView, 不显示已加锁的，
				ll_lock.setVisibility(View.GONE);
				ll_unlock.setVisibility(View.VISIBLE);
				//并且该按钮的背景图片发生改变
				bt_unlock.setBackgroundResource(R.drawable.tab_left_pressed);
				bt_lock.setBackgroundResource(R.drawable.tab_right_default);
			}
		});
	}
}
