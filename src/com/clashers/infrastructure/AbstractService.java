package com.clashers.infrastructure;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public abstract class AbstractService extends Service {
	public static final int MSG_REGISTER_CLIENT = 9991;
	public static final int MSG_UNREGISTER_CLIENT = 9992;

	// set from extended calsses
	protected String serviceName = "MyService";

	// Keeps track of all current registered clients.
	ArrayList<Messenger> mClients = new ArrayList<Messenger>();

	// Target we publish for clients to send messages to IncomingHandler.
	final Messenger mMessenger = new Messenger(new IncomingHandler());

	// Handler of incoming messages from clients.
	@SuppressLint("HandlerLeak")
	private class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_REGISTER_CLIENT:
				Log.i("MyService", "Client registered: " + msg.replyTo);
				if (!mClients.contains(msg.replyTo)) {
					mClients.add(msg.replyTo);
				}
				send(Message.obtain(null, MSG_REGISTER_CLIENT, serviceName
						+ " service is binded"));
				break;
			case MSG_UNREGISTER_CLIENT:
				Log.i("MyService", "Client un-registered: " + msg.replyTo);
				mClients.remove(msg.replyTo);
				break;
			default:
				// super.handleMessage(msg);
				onReceiveMessage(msg);
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();

		onStartService();

		Log.i(serviceName, "Service Started.");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(serviceName, "Received start id " + startId + ": " + intent);
		// run until explicitly stopped.
		return START_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessenger.getBinder();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		onDestroyService();

		Log.i(serviceName, "Service Stopped.");
	}

	protected void send(Message msg) {
		for (int i = mClients.size() - 1; i >= 0; i--) {
			try {
				Log.i(serviceName, "Sending message to clients: " + msg);
				Message msgNew = new Message();
				msgNew.copyFrom(msg);
				mClients.get(i).send(msgNew);
			} catch (RemoteException e) {
				// The client is dead. Remove it from the list; we are going
				// through the list from back to front so this is safe to do
				// inside the loop.
				Log.e(serviceName, "Client is dead. Removing from list: " + i);
				mClients.remove(i);
			}
		}
	}

	/**
	 * called within onCreate of the service
	 */
	public abstract void onStartService();

	/**
	 * called within onDestroy of the service
	 */
	public abstract void onDestroyService();

	/**
	 * handles the message arriving
	 */
	public abstract void onReceiveMessage(Message msg);
}