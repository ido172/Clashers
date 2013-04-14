package com.clashers.services;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.clashers.asynctasks.DataSender;
import com.clashers.asynctasks.GetNearUsers;
import com.clashers.asynctasks.interfaces.INearUsersParent;
import com.clashers.infrastructure.AbstractService;
import com.clashers.infrastructure.datastructures.ServerData;
import com.clashers.infrastructure.datastructures.UserDataFromServer;

public class DataSenderRecieverService extends AbstractService implements INearUsersParent {

	public static final int MSG_SET_TIME_ROUND_OF_DELIVERY = 0;
	public static final int MSG_SET_TIME_ROUND_OF_RECEIPIENT = 1;
	public static final int MSG_SET_SERVER_DATA = 2;
	public static final int MSG_GET_USERS_NEAR_BY = 3;
	private long sendRoundTime = 5000; // 5 sec
	private long receiveRoundTime = 5000;
	private Handler delaySendHandler = null;
	private Handler delayRecieveHandler = null;
	private ServerData serverData;
	DataSenderRecieverService serviceRef = this;

	@Override
	/**
	 * Starts the data sender thread
	 */
	public void onStartService() {
		this.serverData = new ServerData();
		delaySendHandler = new Handler();
		delaySendHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				DataSender dataSender = new DataSender(serverData);
				dataSender.start();
				delaySendHandler.postDelayed(this, sendRoundTime);
				Log.i("Clashers", "DataSent");
			}
		}, this.sendRoundTime);

		delayRecieveHandler = new Handler();
		delayRecieveHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				GetNearUsers getNearUsers = new GetNearUsers(serviceRef);
				getNearUsers.execute();
				delaySendHandler.postDelayed(this, receiveRoundTime);
				Log.i("Clashers", "DataReceived");
			}
		}, this.receiveRoundTime);
	}

	@Override
	public void onReceiveMessage(Message msg) {
		switch (msg.what) {
			case MSG_SET_TIME_ROUND_OF_DELIVERY:
				this.sendRoundTime = (Long) msg.obj;
				break;
			case MSG_SET_TIME_ROUND_OF_RECEIPIENT:
				this.receiveRoundTime = (Long) msg.obj;
				break;
			case MSG_SET_SERVER_DATA:
				this.serverData = (ServerData) msg.obj;
				new DataSender(serverData).start();
				break;
		}
	}

	@Override
	public void onDestroyService() {
		// TODO Auto-generated method stub

	}

	@Override
	public void UpdateUsersNearBy(ArrayList<UserDataFromServer> usersList) {
		send(Message.obtain(null, MSG_GET_USERS_NEAR_BY, usersList));
		
	}
}