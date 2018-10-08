package com.google.mobilesafe.receiver;

import com.google.mobilesafe.engine.ProcessInfoProvider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class KillProcessReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ProcessInfoProvider.killAll(context);
	}

}
