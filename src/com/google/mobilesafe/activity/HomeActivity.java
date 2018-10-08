package com.google.mobilesafe.activity;


import com.google.mobilesafe.R;
import com.google.mobilesafe.utils.ContentValue;
import com.google.mobilesafe.utils.Md5Util;
import com.google.mobilesafe.utils.SpUtils;
import com.google.mobilesafe.utils.ToastUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends Activity {
	private GridView gv_home;
	private String[] mTitleArrs;
	private int[] mDrawableIds;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		//调用初始化UI方法
		initUI();
		//调用初始化数据方法
		initData();
	}

	/**
	 * 初始化数据方法,让gridView中有条目
	 * 类似于listView，因此首先需要有数据， 然后通过inflate将布局文件转换成控件显示
	 */
	private void initData() {
		mTitleArrs = new String[]{"手机防盗","通信卫士","软件管理",
									"进程管理","流量统计","手机杀毒",
									"缓存清理","高级工具","设置中心"};
		mDrawableIds = new int[]{R.drawable.home_safe,R.drawable.home_callmsgsafe,R.drawable.home_apps,
									R.drawable.home_taskmanager,R.drawable.home_netmanager,R.drawable.home_trojan,
									R.drawable.home_sysoptimize,R.drawable.home_tools,R.drawable.home_settings};
		//将适配器的数据设置到gridView中
		gv_home.setAdapter(new MyAdapter());
		//当用户点击某个图标的话， 那么就跳转到相应界面中
		gv_home.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				switch (position) {
				case 0:
					//点了手机防盗
					ToastUtils.show(getApplicationContext(), "进入手机防盗界面");
					//点击这个条目，就进入到密码验证对话框
					showDialog();
					break;
				case 1:
					//点了通信卫士，跳转到一个界面，
					startActivity(new Intent(getApplicationContext(),BlackNumberActivity.class));
					break;
				case 2:
					//点了软件管理，跳转到一个界面，
					startActivity(new Intent(getApplicationContext(),AppManagerActivity.class));
					break;
				case 3:
					//点了软件管理，跳转到一个界面，
					startActivity(new Intent(getApplicationContext(),ProgressManagerActivity.class));
					break;
				case 5:
					//点了软件管理，跳转到一个界面，
					startActivity(new Intent(getApplicationContext(),AntiVirusActivity.class));
					break;
				case 7:
					//点了手机防盗
					ToastUtils.show(getApplicationContext(), "进入高级工具");
					startActivity(new Intent(getApplicationContext(), AToolActivity.class));
					break;
				case 8:
					//点了设置中心，
					ToastUtils.show(getApplicationContext(), "进入设置中心");
					Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
					startActivity(intent);
					break;
				
				}
			}

			/**
			 * 创建一个对话框，来显示密码，并输入
			 */
			private void showDialog() {
				//需要创建一个制造对话框器
				//首先需要判断本地是否有存储密码
				String psd = SpUtils.getString(getApplicationContext(), ContentValue.MOBILE_SAVE_PSD, "");
				//如果密码为空
				if(TextUtils.isEmpty(psd)){
					//开始需要初始设置密码对话框，               依据看是否是已经设置密码了， 如果设置了， 那么就直接进入到
					showSetPsdDialog();
				}else{
					//确认密码对话框
					showConfirmPsdDialog(psd);
				}
				//确认密码对话框中，那么就需要将这个密码保存，然后回显
			}
		});
	}
	/**
	 * 
	 */
	protected void showConfirmPsdDialog(final String psd) {
		// TODO Auto-generated method stub
		//需要一个对话框生成器，这样可以自定义对话框
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		//将布局文件填充成一个view对象
		View view = View.inflate(this, R.layout.dialog_confirm_psd, null);
		//然后设置到对话框中
//		dialog.setView(view);
		dialog.setView(view, 0, 0, 0, 0);
		
		//找到对话框所关心的控件
		final EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cansel = (Button) view.findViewById(R.id.bt_cansel);
		//需要给按钮设置点击事件,确定按钮
		bt_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//首先密码不能为空， 
				String confirm_psd = et_confirm_psd.getText().toString().trim();
				//当密码已经加密了， 因此要比对的话， 需要把输入的密码也要加密， 才能比对
				confirm_psd=Md5Util.exeMd5(confirm_psd);
				if(!TextUtils.isEmpty(confirm_psd)){
					//当确认的密码和保存的密码相同， 那么就进入下个界面
					if(psd.equals(confirm_psd)){
						//如果密码相同，那么就进入设置完成界面
						Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
						startActivity(intent);
					}else{
						//如果密码不相同，弹个吐司
						ToastUtils.show(getApplicationContext(), "确认密码错误");
					}
				}else{
					//如果其中有个密码为空
					ToastUtils.show(getApplicationContext(), "确认密码为空");
				}
				dialog.dismiss();
			}
		});
		bt_cansel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//然后需要关闭对话框
				dialog.dismiss();
			}
		});
		//一定要记住show出来
		dialog.show();
	}

	/**
	 * 开始需要初始设置密码对话框，
	 */
	protected void showSetPsdDialog() {
		// TODO Auto-generated method stub
		//需要一个对话框生成器，这样可以自定义对话框
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		//将布局文件填充成一个view对象
		View view = View.inflate(this, R.layout.dialog_set_psd, null);
		//然后设置到对话框中
//		dialog.setView(view);
		dialog.setView(view, 0, 0, 0, 0);
		
		//找到对话框所关心的控件
		final EditText et_set_psd = (EditText) view.findViewById(R.id.et_set_psd);
		final EditText et_confirm_psd = (EditText) view.findViewById(R.id.et_confirm_psd);
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cansel = (Button) view.findViewById(R.id.bt_cansel);
		//需要给按钮设置点击事件,确定按钮
		bt_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//首先密码不能为空， 
				String set_psd = et_set_psd.getText().toString().trim();
				String confirm_psd = et_confirm_psd.getText().toString().trim();
				
				if((!TextUtils.isEmpty(set_psd))&&(!TextUtils.isEmpty(confirm_psd))){
					if(set_psd.equals(confirm_psd)){
						//如果密码相同，那么就进入下个界面
						Intent intent = new Intent(getApplicationContext(),SetupOverActivity.class);
						startActivity(intent);
						//把这个密码进行md5加密
						set_psd = Md5Util.exeMd5(set_psd);
						//当键入了密码并且和确认密码一致的话， 那么就把这个新的加密的密码保存到sharedpreference中。
						SpUtils.putString(getApplicationContext(),ContentValue.MOBILE_SAVE_PSD, set_psd);
					}else{
						//如果密码不相同，弹个吐司
						ToastUtils.show(getApplicationContext(),"确认密码错误");
					}
				}else{
					//如果其中有个密码为空
					ToastUtils.show(getApplicationContext(), "请输入密码");
				}
				dialog.dismiss();
			}
		});
		bt_cansel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//然后需要关闭对话框
				dialog.dismiss();
			}
		});
		//一定要记住show出来
		dialog.show();
	}
	/**
	 * 初始化UI
	 */
	private void initUI() {
		gv_home = (GridView) findViewById(R.id.gv_home);
	}
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mTitleArrs.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mTitleArrs[position];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(getApplicationContext(), R.layout.gridview_item, null);
			//然后需要给每个条目设置图标，标题
			ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
			TextView tv_title = (TextView) view.findViewById(R.id.tv_title);
			//获取每个位置的图片和标题
			String title = mTitleArrs[position];
			int drawableId = mDrawableIds[position];
			iv_icon.setBackgroundResource(drawableId);
			tv_title.setText(title);
			return view;
		}
		
	}
}

