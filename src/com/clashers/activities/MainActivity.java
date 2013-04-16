package com.clashers.activities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.clashers.R;
import com.clashers.data.Data;
import com.clashers.infrastructure.ServiceManager;
import com.clashers.infrastructure.datastructures.SongData;
import com.clashers.infrastructure.datastructures.UserDataFromServer;
import com.clashers.pushnotifications.CommonUtilities;
import com.clashers.pushnotifications.WakeLocker;
import com.clashers.services.BGService;
import com.clashers.asynctasks.GCMSendMessage;
import com.clashers.asynctasks.GetNearUsers;
import com.clashers.asynctasks.interfaces.INearUsersParent;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.*;
import com.facebook.model.*;
import com.google.android.gcm.GCMRegistrar;

@SuppressLint("HandlerLeak")
public class MainActivity extends FragmentActivity implements INearUsersParent {

	public DisplayMetrics metrics = null;

	private ServiceManager smBG;

	private ArrayList<UserBox> users = null;

	private static MainActivity thisActivityInstance = null;

	public static MainActivity getActivityInsttance() {
		return thisActivityInstance;
	}

	private Button refreshBtn = null;

	private void GCMRegister() {
		// Make sure the device has the proper dependencies.
		GCMRegistrar.checkDevice(this);

		// Make sure the manifest was properly set - comment out this line
		// while developing the app, then uncomment it when it's ready.
		GCMRegistrar.checkManifest(this);

		// Get GCM registration id
		final String regId = GCMRegistrar.getRegistrationId(this);

		// Check if regid already presents
		if (regId.equals("")) {
			// Registration is not present, register now with GCM
			GCMRegistrar.register(this, Data.GCM_SENDER_ID);
		} else {
			// Device is already registered on GCM
			Log.d("GCM", "Already registered");
		}

		SharedPreferences settings = getSharedPreferences(Data.PREFS_FILE_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(Data.PREFS_GCM, regId);
		editor.commit();

		registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);

		thisActivityInstance = this;

		metrics = getResources().getDisplayMetrics();
		initServices();

		if (!isOnline()) {
			findViewById(R.id.no_connection_container).setVisibility(View.VISIBLE);
			Button retryBtn = (Button) findViewById(R.id.connection_retry_btn);
			retryBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (isOnline()) {
						GCMRegister();
						findViewById(R.id.no_connection_container).setVisibility(View.GONE);
						openFacebookConnection();
					}

				}
			});
		} else {
			GCMRegister();
			// start Facebook Login
			openFacebookConnection();
		}

		refreshBtn = (Button) findViewById(R.id.refresh_main);
		refreshBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				removeBoxes();
				getNearUsers();

			}
		});
	}

	private void removeBoxes() {
		if (users == null || users.size() < 1)
			return;

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		for (int i = 0; i < users.size(); i++) {
			ft.remove(users.get(i));
		}
		ft.commit();
	}

	private void openFacebookConnection() {
		Session.openActiveSession(this, true, new Session.StatusCallback() {

			// callback when session changes state
			@Override
			public void call(Session session, SessionState state, Exception exception) {
				if (session.isOpened()) {

					// make request to the /me API
					Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

						// callback after Graph API response with user
						// object
						@Override
						public void onCompleted(GraphUser user, Response response) {
							if (user != null) {
								SharedPreferences settings = getSharedPreferences(Data.PREFS_FILE_NAME, 0);
								SharedPreferences.Editor editor = settings.edit();

								// Set "hasLoggedIn" to true
								editor.putString(Data.PREFS_UID, user.getId());
								editor.putString(Data.PREFS_NAME, user.getName());
								// Commit the edits!
								editor.commit();

								Log.d("SUCCESS FACEBOOK LOGIN", "ID IS" + user.getId());
								initServices();
								getNearUsers();

							} else {
								Log.d("ERROR", "FAIL TO LOG IN");
							}
						}
					});
				}
			}
		});
	}

	private void getNearUsers() {
		new GetNearUsers(this).execute();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	private void initServices() {
		
		if (smBG == null) {
			smBG = new ServiceManager(this, BGService.class, new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// Receive message from service

				}
			});
		} else if (!smBG.isRunning()) {
			smBG.start();
		}
	}

	@Override
	public void UpdateUsersNearBy(ArrayList<UserDataFromServer> usersDataList) {
		if (usersDataList == null) {
			getNearUsers();

		} else if (usersDataList.size() > 0) {
			Collections.sort(usersDataList, new Comparator<UserDataFromServer>() {

				@Override
				public int compare(UserDataFromServer lhs, UserDataFromServer rhs) {
					if (lhs.getDistance() < rhs.getDistance()) {
						return -1;
					} else if (lhs.getDistance() == rhs.getDistance()) {
						return 0;
					} else {
						return 1;
					}
				}
			});
			findViewById(R.id.main_container).setVisibility(View.VISIBLE);
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			users = new ArrayList<UserBox>();
			UserDataFromServer currUserData = null;
			UserBox currUser = null;
			for (int i = 0; i < usersDataList.size(); i++) {
				currUser = new UserBox();
				currUserData = usersDataList.get(i);
				currUser.setSongData(new SongData(currUserData.getSong(), currUserData.getArtist(), currUserData
						.getAlbum(), "0"));
				currUser.setUID(currUserData.getUID());
				currUser.setUserName(currUserData.getUserName());
				currUser.setGCMID(currUserData.getGcmID());
				ft.add(R.id.users_container, currUser);
				currUser.setDistanceText(currUserData.getDistance() + "m");
				users.add(currUser);

			}
			ft.commit();
		}
	}

	public void imageClick(UserBox box) {
		Log.d("CLICK", "" + box.getUID() + "::" + box.getSongData().getTrack());

		new GCMSendMessage(box.getGCMID()).start();
		Intent myIntent = new Intent(this, PlayerActivity.class);
		myIntent.putExtra(Data.SQL_TAG_UID, box.getUID());
		myIntent.putExtra(Data.SQL_TAG_USER_NAME, box.getUserName());
		myIntent.putExtra(Data.SQL_TAG_SONG, box.getSongData().getTrack());
		myIntent.putExtra(Data.SQL_TAG_ARTIST, box.getSongData().getArtist());
		startActivity(myIntent);

	}

	private boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message depending upon your app
			 * requirement For now i am just displaying it on the screen
			 * */

			// Showing received message

			Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			WakeLocker.release();
		}
	};

	@Override
	protected void onStop() {
		unregisterReceiver(mHandleMessageReceiver);
		smBG.unbind();
		super.onStop();
	}
	
	@Override
	protected void onRestart() {
		registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
		smBG.start();
		super.onStop();
	}
	
}