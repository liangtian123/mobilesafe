package com.google.mobilesafe.activity;

import java.util.ArrayList;

import com.google.mobilesafe.R;
import com.google.mobilesafe.activity.AppManagerActivity.MyAdapter;
import com.google.mobilesafe.activity.AppManagerActivity.ViewHolder;
import com.google.mobilesafe.activity.AppManagerActivity.ViewTitleHolder;
import com.google.mobilesafe.db.domain.AppInfo;
import com.google.mobilesafe.db.domain.ProcessInfo;
import com.google.mobilesafe.engine.AppInfoProvider;
import com.google.mobilesafe.engine.ProcessInfoProvider;
import com.google.mobilesafe.utils.ContentValue;
import com.google.mobilesafe.utils.SpUtils;
import com.google.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;


public class ProgressManagerActivity extends Activity implements OnClickListener {
	private TextView tv_process_count,tv_memory_info;
	private ListView lv_process_list;
	private ArrayList<ProcessInfo> mSystemlist;
	private ArrayList<ProcessInfo> mCustomlist;
	private ArrayList<ProcessInfo> mProcessList;
	private MyAdapter mAdapter;

	private Handler mHandler=new Handler(){

		public void handleMessage(android.os.Message msg) {
			mAdapter = new MyAdapter();
			lv_process_list.setAdapter(mAdapter);
			//显示常驻悬浮框的文本，为了防止空指针异常，先进行判断是否为空，
			//当需要改变文本的状态的时候， 就在mCustomList的大小的时候，此时的listView在滚动， 
			//因此需要监听listView的滚动的状态
			if(tv_des!=null&&mCustomlist!=null){
				tv_des.setText("用户进程("+mCustomlist.size()+")");
			}
			
		};
	};
	private TextView tv_des;
	private Button bt_all;
	private Button bt_reverse;
	private Button bt_clear;
	private Button bt_setting;
	private int mProcessCount;
	private long mAvailSpace;
	private String mStrTotalSpace;
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			if(SpUtils.getBoolean(getApplicationContext(), ContentValue.SHOW_SYSTEM, false)){
				return mSystemlist.size()+mCustomlist.size()+2;
			}else{
				return mCustomlist.size()+1;
			}
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
		public ProcessInfo getItem(int position) { 
			// TODO Auto-generated method stub
			if(position==0||position==mCustomlist.size()+1){
				return null;
			}else{
				if(position<mCustomlist.size()+1){
				//说明这些位置的进程是属于客户端进程
					return mCustomlist.get(position-1);
				}else{
				//说明这些位置的进程是属于系统进程
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
						tv_title.setText("用户进程("+mCustomlist.size()+")");
					}else{
						tv_title.setText("系统进程("+mSystemlist.size()+")");
					}
					//将这个控件放到viewTitleHolder，作为他的属性控件用， 这样就只需要完成一次的加载， 节省内存的操作
					viewTitleHolder.title=tv_title;
					//将这个ViewHolder对象挂载到converView中
					convertView.setTag(viewTitleHolder);
				}else{
					viewTitleHolder = (ViewTitleHolder) convertView.getTag();
				}
				return convertView;
			}else{
				ViewHolder viewHolder=null;
				if(convertView==null){
					convertView=View.inflate(getApplicationContext(), R.layout.listview_process_item, null);
					//这里需要明确的是哪个view中的子控件
					ImageView iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
					TextView tv_memory_info = (TextView) convertView.findViewById(R.id.tv_memory_info);
					TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
					CheckBox cb_box = (CheckBox) convertView.findViewById(R.id.cb_box);
					viewHolder = new ViewHolder();
					viewHolder.icon=iv_icon;
					viewHolder.tv_memory_info=tv_memory_info;
					viewHolder.name=tv_name;
					viewHolder.cb_box=cb_box;
					//将这个ViewHolder对象挂载到converView中
					convertView.setTag(viewHolder);
				}else{
					viewHolder = (ViewHolder) convertView.getTag();
				}
				//然后通过这个对象，得到各个的控件，这样就可以节省内存
				viewHolder.icon.setBackgroundDrawable(getItem(position).icon);
				viewHolder.name.setText(getItem(position).name);
				//需要在控件上显示所占内存的大小
				String strMemSize = Formatter.formatFileSize(getApplicationContext(), getItem(position).memSize);
				viewHolder.tv_memory_info.setText(strMemSize);
				//由于本进程不能被选中， 因此需要将这个进程给隐藏
				if(getItem(position).packageName.equals(getPackageManager())){
					viewHolder.cb_box.setVisibility(View.GONE);
				}else{
					//如果不是本进程的话， 就可以显示这个checkbox了， 
					viewHolder.cb_box.setVisibility(View.VISIBLE);
				}
				viewHolder.cb_box.setChecked(getItem(position).isCheck);
				return convertView;
			}
		}
		
	}
	//创建静态的条目容器， 是因为了在加载一个条目中的子控件的时候， 可以减少在内存中加载的次数
		static class ViewHolder{
			public ImageView icon;
			public TextView tv_memory_info;
			public TextView name;
			public CheckBox cb_box;
		}
		static class ViewTitleHolder{
			public TextView title;
		}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_progress_manager);
		initUI();
		initTitleData();
		initListData();	
	}

	/**
	 * 初始化listView数据
	 */
	private void initListData() {
		// TODO Auto-generated method stub
		//通过id值找到常驻悬浮框，然后需要让他显示文本，
		tv_des = (TextView) findViewById(R.id.tv_des);
		//通过id值找到listView, 
		lv_process_list = (ListView) findViewById(R.id.lv_process_list);
		mSystemlist = new ArrayList<ProcessInfo>();
		mCustomlist = new ArrayList<ProcessInfo>();
		//在初始化列表的时候， 就开始监听listView的滚动状态
		lv_process_list.setOnScrollListener(new OnScrollListener() {
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
						tv_des.setText("系统进程("+mSystemlist.size()+")");
					}else{
						tv_des.setText("用户进程("+mCustomlist.size()+")");
					}
				}
			}
		});
		//当点中某个条目的时候，就弹出popupwindow，并来展示不同的效果
		lv_process_list.setOnItemClickListener(new OnItemClickListener() {

			private ProcessInfo mProcessInfo;

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//当点击某一个条目的时候， 需要获取这个条目的数据，
				//空实现， 因此：
				if(position==0||position==mCustomlist.size()+1){
					return;
				}else{
					if(position<mCustomlist.size()+1){
						mProcessInfo = mCustomlist.get(position-1); 
					}else{
						mProcessInfo = mSystemlist.get(position-mCustomlist.size()-2);
					}
					//当点击的时候， checkbox状态发生变化,并且是不是本应用
					if(mProcessInfo!=null){
						if(!mProcessInfo.packageName.equals(getPackageName())){
							//选中条目指向的对象和本应用的包名不一致,才需要去状态取反和设置单选框状态
							//状态取反
							mProcessInfo.isCheck=!mProcessInfo.isCheck;
							CheckBox cb_box = (CheckBox) view.findViewById(R.id.cb_box);
							cb_box.setChecked(mProcessInfo.isCheck);
						}
					}
				}
				
			}
		});
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
				mProcessList = ProcessInfoProvider.getPrecessInfo(getApplicationContext());
				for (ProcessInfo processInfo : mProcessList) {
					if(processInfo.isSystem){
						//说明是系统进程
						mSystemlist.add(processInfo);
					}else{
						//用户进程
						mCustomlist.add(processInfo);
					}
				}
				//需要放到listView中显示， 然而子线程中无法更新UI， 因此需要用到消息机制
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}
	/**
	 * 初始化标题数据
	 */
	private void initTitleData() {
		mProcessCount = ProcessInfoProvider.getProcessCount(getApplicationContext());
		tv_process_count.setText("进程总数："+mProcessCount); 
		mAvailSpace = ProcessInfoProvider.getAvailSpace(getApplicationContext());
		String strAvailSpace = Formatter.formatFileSize(getApplicationContext(), mAvailSpace);
		//总共的内存大小
		long totalSpace = ProcessInfoProvider.getTotalSpace(getApplicationContext());
		mStrTotalSpace = Formatter.formatFileSize(getApplicationContext(), totalSpace);
		tv_memory_info.setText("剩余/总共："+strAvailSpace+"/"+mStrTotalSpace);
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		tv_process_count = (TextView) findViewById(R.id.tv_process_count);
		tv_memory_info = (TextView) findViewById(R.id.tv_memory_info);
		lv_process_list = (ListView) findViewById(R.id.lv_process_list);
		tv_des = (TextView) findViewById(R.id.tv_des);
		bt_all = (Button) findViewById(R.id.bt_all);
		bt_reverse = (Button) findViewById(R.id.bt_reverse);
		bt_clear = (Button) findViewById(R.id.bt_clear);
		bt_setting = (Button) findViewById(R.id.bt_setting);
		bt_all.setOnClickListener(this);
		bt_reverse.setOnClickListener(this);
		bt_clear.setOnClickListener(this);
		bt_setting.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_all:
			Log.i("结果", "全选");
			selectAll();
			break;
		case R.id.bt_reverse:
			Log.i("结果", "反选");
			selectReverse();
			break;
		case R.id.bt_clear:
			Log.i("结果", "一键清理");
			clearAll();
			break;
		case R.id.bt_setting:
			Log.i("结果", "设置");
			setting();
			break;
		}
	}

	/**
	 * 点击按钮，跳转进入到设置界面
	 */
	private void setting() {
		Intent intent = new Intent(this, ProcessSettingActivity.class);
		startActivityForResult(intent, 0);
		//由于回退的时候， 需要回显结果， 因此有结果，并且在onactivityresult的方法中，通知适配器给更新
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	private void clearAll() {
		// TODO Auto-generated method stub
		//获取选中的进程
		//获取一个记录需要杀死的进程的集合
		ArrayList<ProcessInfo> killList = new ArrayList<ProcessInfo>();
		//先遍历用户应用， 如果用户应用被选中了， 那么就添加到需要杀死的进程中，当然本应用无法被选中
		for(ProcessInfo processInfo:mCustomlist){
			if(processInfo.packageName.equals(getPackageName())){
				continue;
			}else{
				if(processInfo.isCheck){
					//不能在集合循环过程中去移除集合中的对象
//					mCustomerList.remove(processInfo);
					killList.add(processInfo);
				}
			}
		}
		//遍历系统应用，如果被选中， 就添加到杀死集合中；
		for(ProcessInfo processInfo:mSystemlist){
			if(processInfo.isCheck){
				killList.add(processInfo);
			}
		}
		long totalReleaseSpace=0;
		//然后遍历这个杀死集合， 如果集合中包含有被选中的，那么就在用户应用和系统应用的集合中移除， 因此将进程杀死
		for(ProcessInfo processInfo:killList){
			if(mCustomlist.contains(processInfo)){
				mCustomlist.remove(processInfo);
			}
			if(mSystemlist.contains(processInfo)){
				mSystemlist.remove(processInfo);
			}
			//杀死记录在killList中的进程
			ProcessInfoProvider.killProcess(getApplicationContext(), processInfo);
			//释放空间的大小
			totalReleaseSpace+=processInfo.memSize;
			//集合发生改变的话， 需要告诉数据适配器
			if(mAdapter!=null){
				mAdapter.notifyDataSetChanged();
			}
		}	
		//当删除后， 进程的总数发生了变化， 并且释放了空间
		mProcessCount-=killList.size();
		//更新可用剩余空间
		mAvailSpace+=totalReleaseSpace;
		//将删除后的进程的总数和可用内存大小显示出来
		tv_process_count.setText("进程总数："+mProcessCount);
		tv_memory_info.setText("剩余/总共："+Formatter.formatFileSize(getApplicationContext(), mAvailSpace)+"/"+mStrTotalSpace);
		ToastUtils.show(getApplicationContext(), String.format("杀死了%d进程,释放了%s空间", killList.size(),Formatter.formatFileSize(getApplicationContext(), totalReleaseSpace)));
	}

	private void selectAll() {
		// TODO Auto-generated method stub
		for(ProcessInfo processInfo:mCustomlist){
			if(processInfo.packageName.equals(getPackageName())){
				continue;
			}else{
				processInfo.isCheck=true;
			}
		}
		for(ProcessInfo processInfo:mSystemlist){
				processInfo.isCheck=true;
		}
		//然后需要告诉数据适配器来刷新数据
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}
	private void selectReverse() {
		// TODO Auto-generated method stub
		for(ProcessInfo processInfo:mCustomlist){
			if(processInfo.packageName.equals(getPackageName())){
				continue;
			}else{
				processInfo.isCheck=!processInfo.isCheck;
			}
		}
		for(ProcessInfo processInfo:mSystemlist){
			processInfo.isCheck=!processInfo.isCheck;
		}
		//然后需要告诉数据适配器来刷新数据
		if(mAdapter!=null){
			mAdapter.notifyDataSetChanged();
		}
	}
}
