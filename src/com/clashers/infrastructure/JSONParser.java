package com.clashers.infrastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

	private static InputStream is = null;
	private static JSONObject jObj = null;
	private static String json = "";

	public enum eMethodsSupported {
		POST, GET
	}

	/**
	 * function get json from url by making HTTP POST or GET mehtod
	 * @param url
	 * @param method
	 * @param params
	 * @return
	 */
	public JSONObject makeHttpRequest(String url, eMethodsSupported method, List<NameValuePair> params) {

		// Making HTTP request
		try {
			switch (method) {
				case POST:
					// request method is POST
					// defaultHttpClient
					HttpPost httpPost = new HttpPost(url);
					httpPost.setEntity(new UrlEncodedFormEntity(params));
					handleMethod(httpPost);
					break;

				case GET:
					// request method is GET
					String paramString = URLEncodedUtils.format(params, "utf-8");
					url += "?" + paramString;
					HttpGet httpGet = new HttpGet(url);
					handleMethod(httpGet);
					break;

			}
		} catch (Exception e) {
			Log.d("JSON Parser", "JSONParser - " + e.getMessage());
			e.printStackTrace();
		}

		readFromServer();
		createJSONObject();

		// return JSON String
		return jObj;

	}

	/**
	 * try parse the string to a JSON object
	 */
	private void createJSONObject() {
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.d("JSON Parser", "Error parsing data " + e.toString());
		}
	}

	/**
	 * Read what the server returns
	 */
	private void readFromServer() {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.d("JSON Parser", "Error converting result " + e.toString());
		}
	}

	/**
	 * 
	 * @param httpMethod - the method to be sent to server
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	private void handleMethod(HttpRequestBase httpMethod) throws ClientProtocolException, IOException {
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse;
		httpResponse = httpClient.execute(httpMethod);
		HttpEntity httpEntity = httpResponse.getEntity();
		is = httpEntity.getContent();
	}
}