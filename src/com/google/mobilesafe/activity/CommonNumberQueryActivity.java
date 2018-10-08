package com.google.mobilesafe.activity;

import java.util.List;

import com.google.mobilesafe.R;
import com.google.mobilesafe.engine.CommonNumberDao;
import com.google.mobilesafe.engine.CommonNumberDao.Child;
import com.google.mobilesafe.engine.CommonNumberDao.Group;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberQueryActivity extends Activity {
	private ExpandableListView elv_common_number;
	private MyAdapter myAdapter;
	private List<Group> mGrouplist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
		initUI();
		initData();
	}

	/**
	 * 初始化数据， 对扩展的listView进行填充
	 */
	private void initData() {
		//需要获取数据库中的数据
		CommonNumberDao commonNumberDao = new CommonNumberDao();
		mGrouplist = commonNumberDao.getGroup();
		myAdapter = new MyAdapter();
		elv_common_number.setAdapter(myAdapter);
		elv_common_number.setOnChildClickListener(new OnChildClickListener() {
			
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// TODO Auto-generated method stub
				//当点击一个子集合的条目的时候， 那么就会打电话
				startCall(myAdapter.getChild(groupPosition, childPosition).number);
				return false;
			}
		});
	}
	protected void startCall(String phonenumber) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse("tel:"+phonenumber));
		startActivity(intent);
	}
	class MyAdapter extends BaseExpandableListAdapter{
		private TextView tv_name;
		private TextView tv_number;
		//获取组的大小
		@Override
		public int getGroupCount() {
			return mGrouplist.size();
		}
		//通过组的集合来获取子集合的大小
		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return mGrouplist.get(groupPosition).childlist.size();
		}
		//通过组在listView的位置获取组对象
		@Override
		public Group getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return mGrouplist.get(groupPosition);
		}

		@Override
		public Child getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return mGrouplist.get(groupPosition).childlist.get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			TextView textView = new TextView(getApplicationContext());
			textView.setText("          "+getGroup(groupPosition).name);
			textView.setTextColor(Color.RED);
			//表示是以像素为单位，20dp
			textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
			return textView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			View view = View.inflate(getApplicationContext(), R.layout.elv_child_view, null);
			tv_name = (TextView) view.findViewById(R.id.tv_name);
			tv_number = (TextView) view.findViewById(R.id.tv_number);
			tv_name.setText(getChild(groupPosition, childPosition).name);
			tv_number.setText(getChild(groupPosition, childPosition).number);
			
			return view;
		}
		//在特定的位置上是否选中子集合中的特定对象
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			
			return true;
		}
		
	}
	/**
	 * 初始化UI
	 */
	private void initUI() {
		elv_common_number = (ExpandableListView) findViewById(R.id.elv_common_number);
	}
}
