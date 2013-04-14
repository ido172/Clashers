package com.clashers.asynctasks;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.clashers.asynctasks.interfaces.IYouTubeSearchParent;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

public class YouTubeSearch extends AsyncTask<String, String, String> {

	public static final String ERROR = "ERROR";
	// YouTUbe data API query - returns only mobile supported links
	private static final String YOUTUBE = "http://gdata.youtube.com/feeds/api/videos?fields=entry[link/@rel='http://gdata.youtube.com/schemas/2007%23mobile'](media:group(media:content[@type='video/3gpp']))&max-results=50&q=";
	private ArrayList<String> youTubeXMlFormat = null;
	private int youTubeXMlFormatCounter = 0;
	private String sourceURL = null;
	private String searchQueryString = null;
	private IYouTubeSearchParent parentActivity;

	public YouTubeSearch(IYouTubeSearchParent parent, String searchQuery) {
		super();
		parentActivity = parent;
		searchQueryString = searchQuery;
		youTubeXMlFormat = new ArrayList<String>();
		youTubeXMlFormat.add("feed");
		youTubeXMlFormat.add("entry");
		youTubeXMlFormat.add("media:group");
		youTubeXMlFormat.add("media:content");
		resetSearch();
	}

	private void resetSearch() {
		youTubeXMlFormatCounter = 0;
	}

	/**
	 * Invoked on the UI thread before the task is executed. This step is
	 * normally used to setup the task, for instance by showing a progress bar
	 * in the user interface.
	 * 
	 * Send a notification to the activity telling that we are searching for the
	 * song on YouTube
	 */
	@Override
	protected void onPreExecute() {
	}

	/**
	 * invoked on the UI thread after a call to publishProgress(Progress...).
	 * The timing of the execution is undefined. This method is used to display
	 * any form of progress in the user interface while the background
	 * computation is still executing. For instance, it can be used to animate a
	 * progress bar or show logs in a text field.
	 * 
	 * @param params - what is the status of loading
	 */
	@Override
	protected void onProgressUpdate(String... params) {
		// here the textview will be updated
	}

	/**
	 * invoked on the background thread immediately after onPreExecute()
	 * finishes executing. This step is used to perform background computation
	 * that can take a long time. The parameters of the asynchronous task are
	 * passed to this step. The result of the computation must be returned by
	 * this step and will be passed back to the last step.
	 * 
	 * Searches YouTube data API with a given Artist name and song name and
	 * returns a link to a 3gp file (mobile supported)
	 * 
	 * @param artistName
	 * @param songName
	 * @return a source link as a string
	 */
	@Override
	protected String doInBackground(String... params) {
		resetSearch();
		String urlString = "";
		HttpURLConnection urlConnection = null;
		try {
			urlString = URLEncoder.encode(searchQueryString, "UTF-8");
			URL url = new URL(YOUTUBE + urlString);
			InputStream in = null;
			urlConnection = (HttpURLConnection) url.openConnection();
			in = new BufferedInputStream(urlConnection.getInputStream());
			findSourceURL(parseXML(in), youTubeXMlFormat.get(youTubeXMlFormatCounter),
					youTubeXMlFormat.get(++youTubeXMlFormatCounter));
		} catch (UnsupportedEncodingException e2) {
			sourceURL = ERROR;
			e2.printStackTrace();
		} catch (IOException e1) {
			sourceURL = ERROR;
			e1.printStackTrace();
			sourceURL = ERROR;
		} catch (XmlPullParserException e) {
			sourceURL = ERROR;
		} finally {
			if (urlConnection != null)
				urlConnection.disconnect();
		}
		return null;
	}

	/**
	 * invoked on the UI thread after the background computation finishes. The
	 * result of the background computation is passed to this step as a
	 * parameter.
	 */
	@Override
	protected void onPostExecute(String file_url) {
		if (sourceURL != null && !sourceURL.equals(ERROR)) {
			Log.d("result", sourceURL);
			parentActivity.setSongYouTubeURL(sourceURL);

		}
	}

	/**
	 * Getting XML object from InputStream object
	 * 
	 * @param in InputStream
	 * @return XmlPullParser object
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private XmlPullParser parseXML(InputStream in) throws XmlPullParserException, IOException {
		XmlPullParser parser = Xml.newPullParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
		parser.setInput(in, null);
		parser.nextTag();
		return parser;
	}

	/**
	 * XML parsing related function.
	 * @param parser
	 * @param parName
	 * @param destName
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void findSourceURL(XmlPullParser parser, String parName, String destName) throws XmlPullParserException,
			IOException {
		parser.require(XmlPullParser.START_TAG, null, parName);
		while (parser.next() != XmlPullParser.END_TAG) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			if (parser.getName().equals(destName)) {
				if (destName.equals("media:content")) {
					if (parser.getAttributeValue(5).toString().equals("6")) {
						sourceURL = parser.getAttributeValue(0);
						break;
					} else {
						skip(parser);
					}
				} else {
					findSourceURL(parser, youTubeXMlFormat.get(youTubeXMlFormatCounter),
							youTubeXMlFormat.get(++youTubeXMlFormatCounter));
					break;
				}
			} else {
				skip(parser);
			}
		}

	}

	/**
	 * XML parsing related function.
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
		if (parser.getEventType() != XmlPullParser.START_TAG) {
			throw new IllegalStateException();
		}
		int depth = 1;
		while (depth != 0) {
			switch (parser.next()) {
				case XmlPullParser.END_TAG:
					depth--;
					break;
				case XmlPullParser.START_TAG:
					depth++;
					break;
			}
		}
	}

}
