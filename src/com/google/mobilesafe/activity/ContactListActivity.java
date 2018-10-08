package com.google.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;

import com.google.mobilesafe.R;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ContactListActivity extends Activity {
	private static final String tag = "ContactListActivity";
	//这里需要定义一个集合来接收定义的map对象
	ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
	private MyAdapter mAdapter;
	protected Handler mHandler=new Handler(){

		public void handleMessage(android.os.Message msg) {
			//创建自己定义的适配器对象
			mAdapter = new MyAdapter();
			//将联系人显示在listview中
			lv_contact.setAdapter(mAdapter);
		};
	};
	private ListView lv_contact;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		//初始化UI
		initUI();
		//初始化联系人的数据
		initData();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		// TODO Auto-generated method stub
		//由于查询数据库是一个耗时的操作， 因此需要放到一个子线程中做：
		new Thread(new Runnable() {
			
			@Override
			public void run() {
			// TODO Auto-generated method stub
			//联系人的界面显示所有的联系人的名字和号码,通过上下文来得到内容解析器
			ContentResolver contentResolver = getContentResolver();
			//首先需要有uri，这里牵涉到多表的查询，因此需要获取联系人的id来获取
			String uri_str="content://com.android.contacts/raw_contacts";
			//然后变成uri
			Uri uri = Uri.parse(uri_str);
			//获取游标
			Cursor cursor = contentResolver.query(uri, new String[]{"contact_id"}, null, null, null);
			//遍历游标来获取id
			while(cursor.moveToNext()){
				//获取每个联系人的id
				String id = cursor.getString(0);
//					Log.i(tag, id);
				//然后通过外键产生的多表视图， 来得到联系人的数据和mimeType(用来指定数据的类型是什么)
				Cursor indexCursor = 
						contentResolver.query(Uri.parse("content://com.android.contacts/data"), 
							new String[]{"data1","mimetype"}, 
							"raw_contact_id=?", 
							new String[]{id}, null);
				//需要将获取到的联系人的信息放到map中， 因此需要定义个map集合
				HashMap<String,String> hashMap = new HashMap<String,String>();
				//然后需要遍历这个游标
				while(indexCursor.moveToNext()){
					String data = indexCursor.getString(0);
					String mimetype = indexCursor.getString(1);
//						Log.i(tag, data);
//						Log.i(tag, mimetype);

					if("vnd.android.cursor.item/phone_v2".equals(mimetype)){
						//说明获取的数据data是电话号码
						hashMap.put("phone",data);
					}else if("vnd.android.cursor.item/name".equals(mimetype)){
						//说明获取的数据data是联系人名称
						hashMap.put("name",data);
					}
				}
				//获取到了这个对象后， 就把这个对象map的一个元素放到list集合中,此处会有三个联系人
				list.add(hashMap);
				//用完游标后， 需要关闭资源
				indexCursor.close();
			}
			//打印验证结果，成功，只有发送消息了， 那么这里就会运行到，为什么？？？？？？？？？
				Log.i(tag, list.toString());
			//一定要记住这步，关闭游标， 资源
			cursor.close();
			//子线程中的数据集合已经封装好了之后， 那么需要交给主线程，设置给适配器。
			//通过验证，在子线程中如果不将数据发送到主线程的话， 这个集合中的值是得不到的，
			//发送一个空消息到主线程
			mHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	/**
	 * 初始化UI
	 */
	private void initUI() {
		lv_contact = (ListView) findViewById(R.id.lv_contact);
		//首先用adapter设置到listView中，那么适配器需要有数据，集合list，因此需要先得到联系人
		//数据，这里由于是其他的应用程序（联系人的应用）， 因此需要内容提供者来作为中间人，
		//我们通过点击解析者来获取。
		//需要给这个listView控件来设置点击事件
		lv_contact.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				//点中某个条目回显出来
				if(mAdapter!=null){
					//通过这个item获取map集合
					HashMap<String,String> map = mAdapter.getItem(position);
					//将点击的联系人的电话号码返回到edittext中
					String phone = map.get("phone");
					Intent intent = new Intent();
					intent.putExtra("phone", phone);
					setResult(0,intent);
					//当点击的时候， 就需要关闭这个界面
					finish();
				}
			}
		});
	}
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public HashMap<String,String> getItem(int position) {
			// TODO Auto-generated method stub
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view=null;
			if(convertView==null){
				 view = View.inflate(getApplicationContext(), R.layout.listview_content_item, null);
			}else{
				view=convertView;
			}
			//获取map集合
			HashMap<String,String> map = list.get(position);
			//得到map集合中的联系人的信息
			String name = map.get("name");
			String phone = map.get("phone");
			//将信息放到view控件中
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			tv_name.setText(name);
			tv_phone.setText(phone);
			return view;
		}
		
	}
}
