package com.google.mobilesafe.test;

import java.util.ArrayList;
import java.util.Random;

import com.google.mobilesafe.db.BlackNumberDao;
import com.google.mobilesafe.db.domain.BlackNumberInfo;

import android.test.AndroidTestCase;

public class Test extends AndroidTestCase {
	public void insert(){
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		for(int i=0;i<100;i++){
			if(i<10){
				//i在个位数的时候
				dao.insert("1851580111"+i, 1+new Random().nextInt(3)+"");
			}else{
				//i在两位数的时候
				dao.insert("185158011"+i, 1+new Random().nextInt(3)+"");
			}
		}
	}
	public void delete(){
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.delete("110");
	}
	public void update(){
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		dao.update("120","2");
	}
	public void findAll(){
		BlackNumberDao dao = BlackNumberDao.getInstance(getContext());
		ArrayList<BlackNumberInfo> list=dao.findAll();
	}
}
