package com.clashers.receivers;

import com.clashers.services.BGService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootCompletedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent startServiceIntent = new Intent(context, BGService.class);
        context.startService(startServiceIntent);
	}
}