package com.clashers.asynctasks;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.clashers.data.Data;
import com.clashers.infrastructure.JSONParser;

import android.util.Log;

public class GCMSendMessage extends Thread {

	private String gcmTargetUser = null;

	// JSON parser class
	private JSONParser jsonParser;

	// url to update user status
	private static final String url_send_gcm = Data.SERVER_URL + Data.PHP_GCM_SEND_MESSAGE;
	
	public GCMSendMessage(String gcmTargetUser) {
		this.gcmTargetUser = gcmTargetUser;
		this.jsonParser = new JSONParser();
	}
	
	@Override
	public void run(){
		sendLogic();
	}

	private void sendLogic() {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("target", gcmTargetUser));
		params.add(new BasicNameValuePair("message", ""));


	
		Log.d("GCM", "send message to " + gcmTargetUser);
		// sending modified data through http request
		// Notice that update product url accepts POST method
		JSONObject json = jsonParser.makeHttpRequest(url_send_gcm, JSONParser.eMethodsSupported.POST, params);

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
}