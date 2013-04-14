package com.clashers.services;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.clashers.asynctasks.DataSender;
import com.clashers.infrastructure.AbstractService;
import com.clashers.infrastructure.datastructures.GPSData;
import com.clashers.infrastructure.datastructures.ServerData;
import com.clashers.infrastructure.datastructures.SongData;

public class SenderService extends AbstractService{

	public static final int MSG_SET_TIME_ROUND_OF_DELIVERY = 0;
	public static final int MSG_METADATA_CHANGED = 1;
	public static final int MSG_LOCATION_CHANGED = 2;
	private ServerData serverData;
	private long sendRoundTime = 5000;
	private Handler delaySendHandler = null;

	@Override
	public void onStartService() {
		this.serverData = new ServerData();
		delaySendHandler = new Handler();
		delaySendHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				Log.d("Clashers","Sending to server: " + serverData.toString());
				DataSender dataSender = new DataSender(serverData);
				dataSender.start();
				delaySendHandler.postDelayed(this, sendRoundTime);
				Log.i("Clashers", "DataSent");
			}
		}, this.sendRoundTime);
		
	}

	@Override
	public void onDestroyService() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReceiveMessage(Message msg) {
		switch (msg.what) {
			case MSG_SET_TIME_ROUND_OF_DELIVERY:
				this.sendRoundTime = (Long) msg.obj;
				break;
			case MSG_METADATA_CHANGED:
				this.serverData.setSongData((SongData) msg.obj);
				break;
			case MSG_LOCATION_CHANGED:
				this.serverData.setGpsData((GPSData) msg.obj);
				break;
		}
	}
}
