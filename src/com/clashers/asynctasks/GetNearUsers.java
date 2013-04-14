package com.clashers.asynctasks;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.clashers.asynctasks.interfaces.INearUsersParent;
import com.clashers.data.Data;
import com.clashers.infrastructure.JSONParser;
import com.clashers.infrastructure.datastructures.UserDataFromServer;
import com.clashers.services.BGService;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Background Async Task to Load all near users by making HTTP Request
 * */
public class GetNearUsers extends AsyncTask<String, String, String> {

	// Parent
	private INearUsersParent parentActivity;

	// Creating JSON Parser object
	private JSONParser jParser = new JSONParser();

	// url to get all near users
	private static String url_near_users = Data.SERVER_URL
			+ Data.PHP_GET_NEAR_USERS;

	// users JSONArray
	private JSONArray users;
	private ArrayList<UserDataFromServer> usersList;

	public GetNearUsers(INearUsersParent parent) {
		super();
		parentActivity = parent;
	}

	/**
	 * getting All near users from url
	 * */
	@Override
	protected String doInBackground(String... arg) {
		// Building Parameters
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(Data.SQL_TAG_UID, BGService.USERID));

		// getting JSON string from URL
		JSONObject json = jParser.makeHttpRequest(url_near_users,
				JSONParser.eMethodsSupported.GET, params);


		try {

			Log.d("All Users: ", json.toString());

			// Checking for SUCCESS TAG
			int success = json.getInt(Data.SQL_TAG_SUCCESS);

			if (success == 1) {
				usersFound(json);
			} else {
				usersWereNotFound();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Near users were found
	 * 
	 * @param json
	 * @throws JSONException
	 */
	private void usersFound(JSONObject json) throws JSONException {
		usersList = new ArrayList<UserDataFromServer>();
		// Getting Array of Users
		users = json.getJSONArray(Data.SQL_TAG_USERS);

		// looping through All Users
		for (int i = 0; i < users.length(); i++) {
			JSONObject c = users.getJSONObject(i);

			usersList.add(new UserDataFromServer(c.getString(Data.SQL_TAG_UID), c.getString(Data.SQL_TAG_GCM_ID), c.getString(Data.SQL_TAG_USER_NAME),
					c.getString(Data.SQL_TAG_SONG), c
							.getString(Data.SQL_TAG_ARTIST), c
							.getString(Data.SQL_TAG_ALBUM), c
							.getString(Data.SQL_TAG_LONGITUDE), c
							.getString(Data.SQL_TAG_LATITUDE)));
		}
	}

	private void usersWereNotFound() {
		// no users found
	}

	/**
	 * After completing background task Dismiss the progress dialog
	 * **/
	protected void onPostExecute(String file_url) {
		parentActivity.UpdateUsersNearBy(usersList);
	}
}