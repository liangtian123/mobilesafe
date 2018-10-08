package com.google.mobilesafe.activity;

import java.util.ArrayList;

import com.google.mobilesafe.R;
import com.google.mobilesafe.db.BlackNumberDao;
import com.google.mobilesafe.db.domain.BlackNumberInfo;
import com.google.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class BlackNumberActivity extends Activity {
	private Button bt_add;
	private ListView lv_blacknumber;
	private BlackNumberDao mDao;
	private ArrayList<BlackNumberInfo> mList;
	private MyAdapter myAdapter;
	private int mode=1;
	private boolean mIsLoad=false;
	private int mCount;
	private Handler mHandler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			
			if(myAdapter==null){
				myAdapter = new MyAdapter();
				lv_blacknumber.setAdapter(myAdapter);
			}else{
				myAdapter.notifyDataSetChanged();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_blacknumber);
		initUI();
		initData();
	}
	class MyAdapter extends BaseAdapter{

		private TextView tv_phone;
		private TextView tv_mode;
		private ImageView iv_delete;
		private ViewHolder viewHolder;

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public BlackNumberInfo getItem(int position) {
			// TODO Auto-generated method stub
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
//			View view=null;
			if(convertView==null){
				convertView=View.inflate(getApplicationContext(), R.layout.listview_blacknumber_item, null);
				viewHolder = new ViewHolder();
				viewHolder.phone=(TextView) convertView.findViewById(R.id.tv_phone);
				viewHolder.mode=(TextView) convertView.findViewById(R.id.tv_mode);
				viewHolder.delete=(ImageView) convertView.findViewById(R.id.iv_delete);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			//获取list中的内容blackNumberInfo对象
			BlackNumberInfo blackNumberInfo = mList.get(position);
			//然后将blackNumberInfo对象中的属性设置到各个控件。
			viewHolder.phone.setText(blackNumberInfo.getPhone());
			//由于得到的mode是不一样的， 因此需要用switch来判断下。
			int mode=Integer.parseInt(blackNumberInfo.getMode());
			switch (mode) {
			case 1:
				viewHolder.mode.setText("拦截短信");
				break;
			case 2:
				viewHolder.mode.setText("拦截电话");
				break;
			case 3:
				viewHolder.mode.setText("拦截所有");
				break;
			}
			//给这个垃圾桶设置点击事件，当点击的时候， 就删除这条数据在数据库中，
			viewHolder.delete.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//此处需要注意的是：一定是先在数据库中将这个条目删除，　然后再把在集合的删除，　
					//原因： 如果先把集合中的删除了， 那么在删除数据库的话， 就会错位， 到了最后的话， 就会
					//因为集合中少了一个， 所以就查不到了， 因此包空指针异常
					//在数据库中删除
					mDao.delete(mList.get(position).getPhone());
					//把这条信息从集合中删除
					mList.remove(position);
					//通知数据适配器修改数据
					if(myAdapter!=null){
						myAdapter.notifyDataSetChanged();
					}
				}
			});
			return convertView;
		}
		
	}
	static class ViewHolder{
		private TextView phone;
		private TextView mode;
		private ImageView delete;
	}
	/**
	 * 初始化数据，这步很重要， 如果没有这步的话， 那么就找不到这个集合， 那么listView就显示不了了
	 */
	private void initData() {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				//得到这个操作数据库的对象
				mDao = BlackNumberDao.getInstance(getApplicationContext());
				//通过这个对象，调用这个方法， 就可以得到这个集合了
				mList = mDao.findAll(0);
				mCount = mDao.getCount();
				//然后通过消息机制来告知主线程可以使用包含数据的集合了
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}
	
	/**
	 * //自定义对话框
	 */
	private void showDialog() {
		//由于对话框和activity绑定的，因此需要用this。
		Builder builder = new AlertDialog.Builder(this);
		//由于需要自定义一个对话框，因此需要创建一个自定义的，
		final AlertDialog dialog = builder.create();
		View view = View.inflate(getApplicationContext(), R.layout.dialog_add_blacknumber, null);
		dialog.setView(view, 0, 0, 0, 0);
		
		//这里需要添加到数据库中，得到各自的控件
		final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cansel = (Button) view.findViewById(R.id.bt_cansel);
		//监听选中的单选框
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.rb_sms:
					//说明选中了拦截短信
					mode=1;
					break;
				case R.id.rb_phone:
					//说明选中了拦截电话
					mode=2;
					break;
				case R.id.rb_all:
					//说明选中了拦截短信和电话
					mode=3;
					break;
				}
			}
		});
		bt_submit.setOnClickListener(new OnClickListener() {
			

			@Override
			public void onClick(View v) {
				//将电话号码插入到数据库中
				String phone = et_phone.getText().toString();
				if(!TextUtils.isEmpty(phone)){
					
					ToastUtils.show(getApplicationContext(), "准备插入一条数据！！");
					mDao=BlackNumberDao.getInstance(getApplicationContext());
					mDao.insert(phone,mode+"");
					//需要和集合中的数据同步,首先将数据手动的添加到javaBean中
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
					mList = mDao.findAll();
					ToastUtils.show(getApplicationContext(), "查询到的总数："+mList.size());
					blackNumberInfo.setPhone(phone);
					blackNumberInfo.setMode(mode+"");
					//每次加入集合都加到集合的顶部
					mList.add(0,blackNumberInfo);
					//当适配器中的数据也需要更新
					if(myAdapter!=null){
						myAdapter.notifyDataSetChanged();
					}
				}else{
					ToastUtils.show(getApplicationContext(), "请输入拦截号码");
				}
				//需要将对话框隐藏对话框
				dialog.dismiss();
			}
		});
		//当对话框中按下取消键的时候
		bt_cansel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		//这个对话框需要关闭， 当容易忘的东西的时候， 需要先把他给写出来， 然后再在上面写
		dialog.show();
	}
	private void initUI() {
		bt_add = (Button) findViewById(R.id.bt_add);
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
		//当点击这个添加按钮的时候， 就弹出一个自定义的对话框，处理添加数据到数据库中的操作， 
		//这里添加数据库的同时， 也需要让集合同步添加一条这样的数据，  不然的话， 就会有这么一条的差
		//同时需要告诉设置数据的适配器， 告诉他数据发生了更新，
		//当数据插入更新后， 需要把这个对话框隐藏。取消的时候， 也把这个对话框隐藏
		bt_add.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//弹出一个自定义的对话框，
				showDialog();
			}
		});
		//由于需要开始只显示20条数据，因此需要在listview滚动的时候，监听他的状态
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {
			//防止重复加载的变量，
			//如果当前加载mIsLoad为true,本次加载完毕后，再将mIsLoad改为false；
			//如果下一次加载需要去做执行的时候，会判断上述mIsLoad的变量，是否为false,如果为true，就需要等到上一次加载完成后， 
			//将其值改为false后再去加载
			
			//滚动过程中状态发生改变调用的方法
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if(mList!=null){
					if((scrollState==OnScrollListener.SCROLL_STATE_IDLE)&&
							(lv_blacknumber.getLastVisiblePosition()>=mList.size()-1)&&!mIsLoad){
						//加载下一页的数据,当这个当前的条目数大于当前的集合大小的时候，才继续加载更多
						if(mCount>mList.size()){
							
							new Thread(new Runnable() {
								
								@Override
								public void run() {
									
									mDao = BlackNumberDao.getInstance(getApplicationContext());
									ArrayList<BlackNumberInfo> moreData = mDao.findAll(mList.size());
									mList.addAll(moreData);
									mHandler.sendEmptyMessage(0);
								}
							}).start();
						}
					}
				}
				
			}
			//滚动过程中调用的方法
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
			}
		});
	}
}
