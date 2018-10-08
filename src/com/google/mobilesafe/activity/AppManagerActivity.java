package com.google.mobilesafe.activity;

import java.text.Format;
import java.util.ArrayList;
import java.util.List;

import com.google.mobilesafe.R;
import com.google.mobilesafe.db.domain.AppInfo;
import com.google.mobilesafe.engine.AppInfoProvider;
import com.google.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

public class AppManagerActivity extends Activity implements OnClickListener{
	private ListView lv_app_list;
	private List<AppInfo> infoList;
	private ArrayList<AppInfo> mSystemlist;
	private ArrayList<AppInfo> mCustomlist;
	private TextView tv_des;
	private AppInfo mAppInfo;
	private PopupWindow mPopupWindow;
	private Handler mHanlder=new Handler(){
		public void handleMessage(android.os.Message msg) {
			MyAdapter mAdapter = new MyAdapter();
			lv_app_list.setAdapter(mAdapter);
			//显示常驻悬浮框的文本，为了防止空指针异常，先进行判断是否为空，
			//当需要改变文本的状态的时候， 就在mCustomList的大小的时候，此时的listView在滚动， 
			//因此需要监听listView的滚动的状态
			if(tv_des!=null&&mCustomlist!=null){
				tv_des.setText("用户应用("+mCustomlist.size()+")");
			}
			
		};
	};
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mSystemlist.size()+mCustomlist.size()+2;
		}
		//获取条目类型的总数，修改成两种，（纯文本，图片+文字）
		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return super.getViewTypeCount()+1;
		}
		//获取条目展示的类型， 由于不同的位置展示的不同的条目类型， 因此需要做个判断
		@Override
		public int getItemViewType(int position) {
			// TODO Auto-generated method stub
			//当加了纯文本的话， 就需要改变了这个位置了， 
			if(position==0||position==mCustomlist.size()+1){
				//返回0，表示纯文本的状态码
				return 0;
			}else{
				//返回1，表示图片+文字的状态码
				return 1;
			}
			
		}
		@Override
		public AppInfo getItem(int position) { 
			// TODO Auto-generated method stub
			if(position==0||position==mCustomlist.size()+1){
				return null;
			}else{
				if(position<mCustomlist.size()+1){
				//说明这些位置的应用是属于客户端应用
					return mCustomlist.get(position-1);
				}else{
				//说明这些位置的应用是属于系统应用
					return mSystemlist.get(position-mCustomlist.size()-2);
				}
			}
			
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			//那么这里就通过上面给定的状态码来判断不同的条目展示
			int viewType = getItemViewType(position);
			if(viewType==0){
				//说明是纯文本的
				ViewTitleHolder viewTitleHolder=null;
				if(convertView==null){
					convertView=View.inflate(getApplicationContext(), R.layout.listview_app_item_title, null);
					//这里需要明确的是哪个view中的子控件
					TextView tv_title = (TextView) convertView.findViewById(R.id.tv_title);
					//新建一个条目用于标题容器
					viewTitleHolder = new ViewTitleHolder();
					
					if(position==0){
						tv_title.setText("用户应用("+mCustomlist.size()+")");
					}else{
						tv_title.setText("系统应用("+mSystemlist.size()+")");
					}
					//将这个控件放到viewTitleHolder，作为他的属性控件用， 这样就只需要完成一次的加载， 节省内存的操作
					viewTitleHolder.title=tv_title;
					//将这个ViewHolder对象挂载到converView中
					convertView.setTag(viewTitleHolder);
				}else{
					viewTitleHolder = (ViewTitleHolder) convertView.getTag();
				}
			}else{
				ViewHolder viewHolder=null;
				if(convertView==null){
					convertView=View.inflate(getApplicationContext(), R.layout.listview_app_item, null);
					//这里需要明确的是哪个view中的子控件
					ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
					TextView tv_path = (TextView) convertView.findViewById(R.id.tv_path);
					TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
					viewHolder = new ViewHolder();
					viewHolder.icon=iv_icon;
					viewHolder.path=tv_path;
					viewHolder.name=tv_name;
					//将这个ViewHolder对象挂载到converView中
					convertView.setTag(viewHolder);
				}else{
					viewHolder = (ViewHolder) convertView.getTag();
				}
				//然后通过这个对象，得到各个的控件，这样就可以节省内存
				viewHolder.icon.setBackgroundDrawable(getItem(position).icon);
				viewHolder.name.setText(getItem(position).name);
				if (getItem(position).isSdCard) {
					//说明是安装在sdcard上的， 
					viewHolder.path.setText("sdcard应用");
				}else{
					viewHolder.path.setText("手机应用");
				}
			}
		    return convertView;
		}
		
	}
	//创建静态的条目容器， 是因为了在加载一个条目中的子控件的时候， 可以减少在内存中加载的次数
	static class ViewHolder{
		public ImageView icon;
		public TextView path;
		public TextView name;
	}
	static class ViewTitleHolder{
		public TextView title;
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		//设置布局
		setContentView(R.layout.activity_app_manager);
		initTitle();
		//获取系统中的数据，可能会是一个耗时的操作， 因此放到一个子线程
		initList();
	}

	private void initList() {
		//通过id值找到常驻悬浮框，然后需要让他显示文本，
		tv_des = (TextView) findViewById(R.id.tv_des);
		//通过id值找到listView, 
		lv_app_list = (ListView) findViewById(R.id.lv_app_list);
		mSystemlist = new ArrayList<AppInfo>();
		mCustomlist = new ArrayList<AppInfo>();
		//在初始化列表的时候， 就开始监听listView的滚动状态
		lv_app_list.setOnScrollListener(new OnScrollListener() {
			//在滚动状态发生变化的时候， 
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				
			}
			//表示在滚动的时候，
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				//AbsListView就是listView
				//firstVisibleItem第一个可见的条目
				//visibleItemCount当前一个屏幕的可见条目，
				//totalItemCount总条目数
				
				//同时为了防止空指针异常的出现， 需要判断一下：
				if(mCustomlist!=null&&mSystemlist!=null){
				//需要让这个常驻悬浮框发生变化， 那么在第一个Customlist完全看不到的情况下发生改变，因此：
					if(firstVisibleItem>=mCustomlist.size()+1){
						tv_des.setText("系统应用("+mSystemlist.size()+")");
					}else{
						tv_des.setText("用户应用("+mCustomlist.size()+")");
					}
				}
			}
		});
		//当点中某个条目的时候，就弹出popupwindow，并来展示不同的效果
		lv_app_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//当点击某一个条目的时候， 需要获取这个条目的数据，
				//因为在后面的操作（安装，卸载，分享）中需要用到这个数据
				//而且在点中标题的话， 不会弹出popupwindow，
				//空实现， 因此：
				if(position==0||position==mCustomlist.size()+1){
					return;
				}else{
					if(position<mCustomlist.size()+1){
						mAppInfo = mCustomlist.get(position-1); 
					}else{
						mAppInfo = mSystemlist.get(position-mCustomlist.size()-2);
					}
				}
				showPopupWindow(view);
			}
		});
	}


	protected void showPopupWindow(View view) {
		//需要定义在popupwindow中需要显示的内容
		View popupView = View.inflate(getApplicationContext(), R.layout.popupwindow_layout, null);
		TextView tv_uninstall = (TextView) popupView.findViewById(R.id.tv_uninstall);
		TextView tv_start = (TextView) popupView.findViewById(R.id.tv_start);
		TextView tv_share = (TextView) popupView.findViewById(R.id.tv_share);
		tv_uninstall.setOnClickListener(this);
		tv_start.setOnClickListener(this);
		tv_share.setOnClickListener(this);
		mPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
		//给这个popupwindow设置一个透明的背景，方便做回退
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
		//设置popupwindow的展示位置,这个popupwindow挂载到某个条目上，因此需要传入view对象
		mPopupWindow.showAsDropDown(view, 50, -view.getHeight());
		//但是这样弹出的话， 可能不会那么和谐， 用户觉得很突然，因此需要用一个动画来展示效果
		//创建一个淡入淡出的动画， 和缩放动画，并将位置停留在动画结束的位置
		AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
		alphaAnimation.setDuration(750);
		alphaAnimation.setFillAfter(true);
		ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 
							0, 1, 
							Animation.RELATIVE_TO_SELF, 0.5f, 
							Animation.RELATIVE_TO_SELF, 0.5f);
		alphaAnimation.setDuration(750);
		alphaAnimation.setFillAfter(true);
		//由于是一个都需要发生的动作， 因此需要将他们放到一个集合中
		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(alphaAnimation);
		animationSet.addAnimation(scaleAnimation);
		//然后将动画集合作用在这个popupwindow中的控件中popupView中就可以了
		popupView.startAnimation(animationSet);
		
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_uninstall:
			//点中这个，就跳转到卸载的界面,之前需要判断是否是系统应用，如果是， 不能卸载
			if(mAppInfo.isSystem){
				ToastUtils.show(getApplicationContext(), "此应用不能卸载");
			}else{
				Intent intent = new Intent("android.intent.action.DELETE");
				intent.addCategory("android.intent.category.DEFAULT");
				intent.setData(Uri.parse("package:"+mAppInfo.packageName));
				startActivity(intent);
			}
			break;
		case R.id.tv_start:
			//当点中这个控件的话， 那么就跳转到启动界面。
			//开启的话， 需要明确开启哪个， 因此需有包名
			PackageManager packageManager = getPackageManager();
			//通过launch去启动指定的包名，来开启应用，并且这个意图不能为空， 如果为空，弹出吐司告诉用户，此应用无法开启
			Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(mAppInfo.packageName);
			if(launchIntentForPackage!=null){
				startActivity(launchIntentForPackage);
			}else{
				ToastUtils.show(getApplicationContext(), "此应用不能开启");
			}
			break;
		case R.id.tv_share:
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT, "分享一个应用，应用名称为："+mAppInfo.name);
			intent.setType("text/plain");
			startActivity(intent);
			break;
		}
		//当点击完了之后，需要将这个popupwindow给隐藏
		if(mPopupWindow!=null){
			mPopupWindow.dismiss();
		}
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//当获取焦点的时候， 就获取集合的数据，并且在删除操作的时候，进行刷新动作，
		getData();
	}

	/**
	 * 通过引擎类获取集合的数据
	 */
	private void getData() {
		new Thread(new Runnable() {

			public void run() {
				infoList = AppInfoProvider.getAppInfoList(getApplicationContext());
				for (AppInfo appInfo : infoList) {
					if(appInfo.isSystem){
						//说明是系统应用
						mSystemlist.add(appInfo);
					}else{
						//用户应用
						mCustomlist.add(appInfo);
					}
				}
				//需要放到listView中显示， 然而子线程中无法更新UI， 因此需要用到消息机制
				mHanlder.sendEmptyMessage(0);
			}
		}).start();
	}
	/**
	 * 给这个模块添加标题
	 */
	private void initTitle() {
		// TODO Auto-generated method stub
		//获取磁盘可用的大小
		//需要获取磁盘和sdcard的路径
		String path = Environment.getDataDirectory().getAbsolutePath();
		String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
		//通过这个路径来获取磁盘可用的空间
		long memoryAvailSpace = getAvailSpace(path);
		long sdcardAvailSpace = getAvailSpace(sdPath);
		//获取sdcard的可用的大小
		//找到各自的控件， 并设置给该控件
		TextView tv_memory = (TextView) findViewById(R.id.tv_memory);
		TextView tv_sd_memory = (TextView) findViewById(R.id.tv_sd_memory);
		tv_memory.setText("磁盘可用:"+Formatter.formatFileSize(this, memoryAvailSpace));
		tv_sd_memory.setText("sd卡可用:"+Formatter.formatFileSize(this, sdcardAvailSpace));
	}

	/**
	 * @param path 磁盘和sdcard的路径
	 * @return 可用的区块的大小乘以区块的个数，等于区块的容量
	 */
	private long getAvailSpace(String path) {
		//获取可用磁盘的类
		StatFs statFs = new StatFs(path);
		//获取可用的区块的个数
		long count = statFs.getAvailableBlocks();
		//获取每个区块的大小
		long size = statFs.getBlockSize();
		//可用的区块的大小乘以区块的个数，等于区块的容量
		return size*count;
	}

}
