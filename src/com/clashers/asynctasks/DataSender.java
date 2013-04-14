package com.clashers.asynctasks;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.clashers.data.Data;
import com.clashers.infrastructure.JSONParser;
import com.clashers.infrastructure.datastructures.GPSData;
import com.clashers.infrastructure.datastructures.ServerData;
import com.clashers.infrastructure.datastructures.SongData;
import com.clashers.services.BGService;

import android.util.Log;

public class DataSender extends Thread {

	private SongData songData;
	private GPSData gpsData;

	// JSON parser class
	private JSONParser jsonParser;

	// url to update user status
	private static final String url_update_user = Data.SERVER_URL + Data.PHP_UPDATE_DATA;
	
	public DataSender(ServerData serverData) {
		setServerDataToBeSent(serverData);
		this.jsonParser = new JSONParser();
	}
	
	@Override
	public void run(){
		sendLogic();
	}

	private void sendLogic() {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(Data.SQL_TAG_UID, BGService.USERID));
		params.add(new BasicNameValuePair(Data.SQL_TAG_GCM_ID, BGService.GCMID));
		params.add(new BasicNameValuePair(Data.SQL_TAG_USER_NAME, BGService.USERNAME));
		params.add(new BasicNameValuePair(Data.SQL_TAG_SONG, songData.getTrack()));
		params.add(new BasicNameValuePair(Data.SQL_TAG_ARTIST, songData.getArtist()));
		params.add(new BasicNameValuePair(Data.SQL_TAG_ALBUM, songData.getAlbum()));
		params.add(new BasicNameValuePair(Data.SQL_TAG_SONG_TIME, songData.getSongTime()));
		params.add(new BasicNameValuePair(Data.SQL_TAG_LATITUDE, Double.toString(gpsData.getLatitude())));
		params.add(new BasicNameValuePair(Data.SQL_TAG_LONGITUDE, Double.toString(gpsData.getLongitude())));

		String tempValue;
		for (int i = 0; i < params.size(); i++) {
			tempValue = (params.get(i).getValue() != null)?params.get(i).getValue():"null";
			Log.d("param:" + i, tempValue);
		}
		// sending modified data through http request
		// Notice that update product url accepts POST method
		JSONObject json = jsonParser.makeHttpRequest(url_update_user, JSONParser.eMethodsSupported.POST, params);

		// check json success tag
		try {
			int success = json.getInt(Data.SQL_TAG_SUCCESS);

			if (success == 1) {
				Log.d("update success!!!", "update success!!!");
			} else {
				// failed to update product
			}
		} catch (Exception e) {
			Log.d("update FAIL!!!", "update FAIL!!!");
			e.printStackTrace();
		}
	}

	/**
	 * sets the serverData to be sent
	 * 
	 * @param serverData
	 */
	private void setServerDataToBeSent(ServerData serverData) {
		this.gpsData = serverData.getGpsData();
		this.songData = serverData.getSongData();
	}

}