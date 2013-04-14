package com.clashers.services;

import java.util.Timer;
import java.util.TimerTask;

import com.clashers.infrastructure.AbstractService;

import android.os.Message;

public class LocalService extends AbstractService {
	public static final int MSG_INCREMENT = 1;
	public static final int MSG_COUNTER = 2;

	private Timer timer = new Timer();
	private int counter = 0, incrementby = 1;

	@Override
	public void onStartService() {
		this.serviceName = "LocalService";
		// Increment counter and send to activity every 250ms
		timer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				try {
					counter += incrementby;
					send(Message.obtain(null, MSG_COUNTER, counter, 0));
				} catch (Throwable t) {
				}
			}
		}, 0, 2500L);
	}

	public void onStopService() {
		if (timer != null) {
			timer.cancel();
		}
		counter = 0;
	}

	@Override
	public void onReceiveMessage(Message msg) {
		if (msg.what == MSG_INCREMENT) {
			incrementby = msg.arg1;
		}
	}

	@Override
	public void onDestroyService() {
		// TODO Auto-generated method stub

	}
}