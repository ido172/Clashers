/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.clashers.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.clashers.R;
import com.clashers.infrastructure.AbstractService;
import com.clashers.infrastructure.datastructures.GPSData;

@SuppressLint("HandlerLeak")
/**
 * Location retriever.
 * I decided not to support address converter, because the server will do this
 */
public class LocationService extends AbstractService {
	private GPSData gpsData = new GPSData();
	private LocationManager mLocationManager;
	private Handler mHandler;
	private boolean mUseFine;
	private boolean mUseBoth;

	// Keys for maintaining UI states after rotation.
	//private static final String KEY_FINE = "use_fine";
	//private static final String KEY_BOTH = "use_both";
	// UI handler codes.
	private static final int UPDATE_LATLNG = 1;

	private static final int ONE_SECOND = 1000;
	private static final int ONE_METER = 1;
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	public static final int MSG_ADDRESS = 3;
	public static final int MSG_LATLNG = 4;
	public static final int MSG_GPS_NOT_ENABLED = 5;

	/**
	 * This sample demonstrates how to incorporate location based services in
	 * your app and process location updates. The app also shows how to convert
	 * lat/long coordinates to human-readable addresses.
	 */

	private void init() {
		mUseFine = false;
		mUseBoth = true;

		// Handler for updating text fields on the UI like the lat/long and
		// address.
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case UPDATE_LATLNG:
						gpsData = ((GPSData) msg.obj);
						send(Message.obtain(null, MSG_LATLNG, gpsData));
						break;
				}
			}
		};
		// Get a reference to the LocationManager object.
		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	}

	@Override
	public void onStartService() {
		init();
		tryToResumeLocationSystemService();
	}
	
	private void tryToResumeLocationSystemService(){
		// Check if the GPS setting is currently enabled on the device.
		// This verification should be done during onStart() because the system
		// calls this method
		// when the user returns to the activity, which ensures the desired
		// location provider is
		// enabled each time the activity resumes from the stopped state.
		this.serviceName = "LocationService";
		boolean gpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

		if (!gpsEnabled) {
			// SEND MESSAGE TO APP
			send(Message.obtain(null, MSG_GPS_NOT_ENABLED));
		} else {
			setup();
		}
	}

	@Override
	public void onDestroyService() {
		mLocationManager.removeUpdates(listener);
	}
	
	@Override
	public void onReceiveMessage(Message msg) {
		tryToResumeLocationSystemService();
	}

	// Set up fine and/or coarse location providers depending on whether the
	// fine provider or
	// both providers button is pressed.
	private void setup() {
		Location gpsLocation = null;
		Location networkLocation = null;
		mLocationManager.removeUpdates(listener);
		// Get fine location updates only.
		if (mUseFine) {
			// Request updates from just the fine (gps) provider.
			gpsLocation = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER, R.string.not_support_gps);
			// Update the UI immediately if a location is obtained.
			if (gpsLocation != null)
				updateUILocation(gpsLocation);
		} else if (mUseBoth) {
			// Request updates from both fine (gps) and coarse (network)
			// providers.
			gpsLocation = requestUpdatesFromProvider(LocationManager.GPS_PROVIDER, R.string.not_support_gps);
			networkLocation = requestUpdatesFromProvider(LocationManager.NETWORK_PROVIDER, R.string.not_support_network);

			// If both providers return last known locations, compare the two
			// and use the better
			// one to update the UI. If only one provider returns a location,
			// use it.
			if (gpsLocation != null && networkLocation != null) {
				updateUILocation(getBetterLocation(gpsLocation, networkLocation));
			} else if (gpsLocation != null) {
				updateUILocation(gpsLocation);
			} else if (networkLocation != null) {
				updateUILocation(networkLocation);
			}
		}
	}

	/**
	 * Method to register location updates with a desired location provider. If
	 * the requested provider is not available on the device, the app displays a
	 * Toast with a message referenced by a resource id.
	 * 
	 * @param provider Name of the requested provider.
	 * @param errorResId Resource id for the string message to be displayed if
	 *            the provider does not exist on the device.
	 * @return A previously returned {@link android.location.Location} from the
	 *         requested provider, if exists.
	 */
	private Location requestUpdatesFromProvider(final String provider, final int errorResId) {
		Location location = null;
		if (mLocationManager.isProviderEnabled(provider)) {
			mLocationManager.requestLocationUpdates(provider, ONE_SECOND, ONE_METER, listener);
			location = mLocationManager.getLastKnownLocation(provider);
		} else {
			Toast.makeText(this, errorResId, Toast.LENGTH_LONG).show();
		}
		return location;
	}

	private void updateUILocation(Location location) {
		// We're sending the update to a handler which then updates the UI with
		// the new
		// location.
		Message.obtain(mHandler, UPDATE_LATLNG, new GPSData(location.getLatitude(), location.getLongitude())).sendToTarget();
	}

	private final LocationListener listener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			// A new location update is received. Do something useful with it.
			// Update the UI with
			// the location update.
			updateUILocation(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	/**
	 * Determines whether one Location reading is better than the current
	 * Location fix. Code taken from
	 * http://developer.android.com/guide/topics/location
	 * /obtaining-user-location.html
	 * 
	 * @param newLocation The new Location that you want to evaluate
	 * @param currentBestLocation The current Location fix, to which you want to
	 *            compare the new one
	 * @return The better Location object based on recency and accuracy.
	 */
	protected Location getBetterLocation(Location newLocation, Location currentBestLocation) {
		if (currentBestLocation == null) {
			// A new location is always better than no location
			return newLocation;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = newLocation.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use
		// the new location
		// because the user has likely moved.
		if (isSignificantlyNewer) {
			return newLocation;
			// If the new location is more than two minutes older, it must be
			// worse
		} else if (isSignificantlyOlder) {
			return currentBestLocation;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (newLocation.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(newLocation.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and
		// accuracy
		if (isMoreAccurate) {
			return newLocation;
		} else if (isNewer && !isLessAccurate) {
			return newLocation;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return newLocation;
		}
		return currentBestLocation;
	}

	/** Checks whether two providers are the same */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}
}
