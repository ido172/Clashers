package com.clashers.services;

import com.clashers.data.Data;
import com.clashers.infrastructure.AbstractService;
import com.clashers.infrastructure.ServiceCollection;
import com.clashers.infrastructure.ServiceManager;
import com.clashers.infrastructure.datastructures.GPSData;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

@SuppressLint("HandlerLeak")
public class BGService extends AbstractService {
	
	public static String USERID = null;
	public static String USERNAME = null;
	public static String GCMID = null;
	
	
	public static final int MSG_META = 0;
	public static final int MSG_SENDER = 1;
	public static final int MSG_RECEIVER = 2;
	private final static String metadataServiceName = "metadata";
	private final static String locationServiceName = "location";
	private final static String senderServiceName = "sender";
	private ServiceCollection serviceCollection;
	public static GPSData gpsData = new GPSData();

	@Override
	public void onStartService() {
		serviceCollection = new ServiceCollection();
		USERID = getUserId();
		if (USERID != null) {
			initServices();
		}
	}
	
	private String getUserId(){
		SharedPreferences settings = getSharedPreferences(Data.PREFS_FILE_NAME, 0);
		
		//init more params as well
		USERNAME = settings.getString(Data.PREFS_NAME, "");
		GCMID = settings.getString(Data.PREFS_GCM, "");
		
		return settings.getString(Data.PREFS_UID, null);
	}

	private void initServices() {
		Log.d("INIT BG SERVICE","INIT BG SERVICE");
		initMetaDataService();
		initLocationService();
		initSenderService();
		serviceCollection.StartAllServices();
	}
	private void initSenderService(){
		ServiceManager smSender = new ServiceManager(this, SenderService.class, new Handler());
		serviceCollection.addServiceManager(senderServiceName, smSender);
	}
	
	private void initLocationService() {
		ServiceManager smLocation = new ServiceManager(this, LocationService.class, new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// Receive message from service
				switch (msg.what) {
					case LocationService.MSG_ADDRESS:
						Toast.makeText(getBaseContext(), msg.obj.toString(), Toast.LENGTH_LONG).show();
						break;
					case LocationService.MSG_LATLNG:
						Toast.makeText(getBaseContext(), msg.obj.toString(), Toast.LENGTH_LONG).show();
						gpsData = (GPSData) msg.obj;
						try {
							serviceCollection.getService(senderServiceName).send(Message.obtain(null, SenderService.MSG_LOCATION_CHANGED, msg.obj));
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					case LocationService.MSG_GPS_NOT_ENABLED:
						// TODO
						break;
					case AbstractService.MSG_REGISTER_CLIENT:
						Toast.makeText(getBaseContext(), msg.obj.toString(), Toast.LENGTH_LONG).show();
						requestLocationFromService();
						break;
				}
			}
		});

		serviceCollection.addServiceManager(locationServiceName, smLocation);
	}

	private void requestLocationFromService() {
		serviceCollection.sendMessageToService(locationServiceName, Message.obtain(null, LocationService.MSG_LATLNG));
	}

	private void initMetaDataService() {
		ServiceManager smMetadata = new ServiceManager(this, MetadataService.class, new Handler() {
			
			@Override
			public void handleMessage(Message msg) {
				// Receive message from service
				switch (msg.what) {
					case MetadataService.MSG_METADATA_RECEIVED:
						Toast.makeText(getBaseContext(), msg.obj.toString(), Toast.LENGTH_LONG).show();
						try {
							serviceCollection.getService(senderServiceName).send(Message.obtain(null, SenderService.MSG_METADATA_CHANGED, msg.obj));
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						break;
					default:
						super.handleMessage(msg);
				}
			}
		});

		serviceCollection.addServiceManager(metadataServiceName, smMetadata);
	}
	


	@Override
	public void onDestroyService() {
		serviceCollection.StopAllServices();
	}

	@Override
	public void onReceiveMessage(Message msg) {
		switch (msg.what) {
			case MSG_META:
				doWhenMetaChanges();
				break;
			case MSG_SENDER:
				doWhenInfosSent();
				break;
			case MSG_RECEIVER:
				doWhenInfoReceived();
				break;
			default:
				throw new IllegalArgumentException();
		}

	}

	private void doWhenInfoReceived() {
		// TODO Auto-generated method stub
		if (checkIfUsersNearBy()) {
			increaseSendRate();
		}
	}

	private void increaseSendRate() {
		// TODO Auto-generated method stub

	}

	private boolean checkIfUsersNearBy() {
		// TODO Auto-generated method stub
		return false;
	}

	private void doWhenInfosSent() {
		// currently does nothing
	}

	private void doWhenMetaChanges() {
		sendInfo();
	}

	private void sendInfo() {
		// TODO Auto-generated method stub

	}

}
