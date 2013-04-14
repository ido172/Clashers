package com.clashers.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.util.Log;

import com.clashers.infrastructure.AbstractService;
import com.clashers.infrastructure.datastructures.SongData;

public class MetadataService extends AbstractService {
	// public static final String SERVICECMD =
	// "com.android.music.musicservicecommand";
	// public static final String CMDNAME = "command";
	// public static final String CMDTOGGLEPAUSE = "togglepause";
	// public static final String CMDSTOP = "stop";
	// public static final String CMDPAUSE = "pause";
	// public static final String CMDPREVIOUS = "previous";
	// public static final String CMDNEXT = "next";
	public final static int MSG_METADATA_RECEIVED = 0;
	public final static int MSG_METADATA_SEND_LATEST = 1;
	private SongData retrievedData = new SongData();

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String cmd = intent.getStringExtra("command");
			Log.d("mIntentReceiver.onReceive ", action + " / " + cmd);
			String track = intent.getStringExtra("track");
			String artist = intent.getStringExtra("artist");
			String album = intent.getStringExtra("album");
			String length = "0";
			if (isDataRetrievedHasSignificance(track, artist)) {
				retrievedData = new SongData(track, artist, album, length);
				Log.d("Clashers", retrievedData.toString());
				sendMessage();
			}
		}

		private boolean isDataRetrievedHasSignificance(String track,
				String artist) {
			return track != null && artist != null;
		}
	};

	private void sendMessage() {
		send(Message.obtain(null, MSG_METADATA_RECEIVED, retrievedData));
	}

	@Override
	public void onStartService() {
		this.serviceName = "MetadataService";
		IntentFilter filter = new IntentFilter();
		addActions(filter);
		registerReceiver(mReceiver, filter);
	}

	/**
	 * Found a detailed list in a website. Some of the filters were added by me.
	 * Wherever it was added, I marked it with a comment //added The website is:
	 * http
	 * ://bm2.googlecode.com/svn-history/r1180/trunk/android/src/org/bombusmod
	 * /BombusModService.java In addition, I searched for the most popular
	 * players on Android. Found this:
	 * http://www.techradar.com/news/phone-and-communications
	 * /mobile-phones/10-best-android-music-players-1114284 We support most of
	 * them. Added support for the N7Player
	 * 
	 * @param filter
	 */
	private void addActions(IntentFilter filter) {
		// Google player
		filter.addAction("com.android.music.playstatechanged");
		filter.addAction("com.android.music.playbackcomplete");
		filter.addAction("com.android.music.metachanged");

		// HTC Music
		filter.addAction("com.htc.music.playstatechanged");
		filter.addAction("com.htc.music.playbackcomplete");
		filter.addAction("com.htc.music.metachanged");

		// MIUI Player
		filter.addAction("com.miui.player.playstatechanged");
		filter.addAction("com.miui.player.playbackcomplete");
		filter.addAction("com.miui.player.metachanged");

		// TODO add support for N7

		// Real
		filter.addAction("com.real.IMP.playstatechanged");
		filter.addAction("com.real.IMP.playbackcomplete");
		filter.addAction("com.real.IMP.metachanged");

		// SEMC Music Player
		filter.addAction("com.sonyericsson.music.playbackcontrol.ACTION_TRACK_STARTED");
		filter.addAction("com.sonyericsson.music.playbackcontrol.ACTION_PAUSED");
		filter.addAction("com.sonyericsson.music.TRACK_COMPLETED");

		// rdio
		filter.addAction("com.rdio.android.metachanged");
		filter.addAction("com.rdio.android.playstatechanged");

		// Samsung Music Player
		filter.addAction("com.samsung.sec.android.MusicPlayer.playstatechanged");
		filter.addAction("com.samsung.sec.android.MusicPlayer.playbackcomplete");
		filter.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
		filter.addAction("com.sec.android.app.music.playstatechanged");
		filter.addAction("com.sec.android.app.music.playbackcomplete");
		filter.addAction("com.sec.android.app.music.metachanged");
		filter.addAction("com.samsung.music.metachanged");// added
		filter.addAction("com.samsung.music.playbackcomplete");// added
		filter.addAction("com.samsung.music.playstatechanged");// added
		filter.addAction("com.samsung.sec.metachanged");// added
		filter.addAction("com.samsung.sec.playbackcomplete");// added
		filter.addAction("com.samsung.sec.playstatechanged");// added
		filter.addAction("com.samsung.sec.android.metachanged");// added
		filter.addAction("com.samsung.sec.android.playbackcomplete");// added
		filter.addAction("com.samsung.sec.android.playstatechanged");// added
		filter.addAction("com.samsung.MusicPlayer.metachanged");// added
		filter.addAction("com.samsung.MusicPlayer.playstatechanged");// added
		filter.addAction("com.samsung.MusicPlayer.playbackcomplete");// added

		// Winamp
		filter.addAction("com.nullsoft.winamp.playstatechanged");
		filter.addAction("com.nullsoft.winamp.metachanged"); // Added
		filter.addAction("com.nullsoft.winamp.playbackcomplete");// added

		// Amazon
		filter.addAction("com.amazon.mp3.playstatechanged");

		// Rhapsody
		filter.addAction("com.rhapsody.playstatechanged");

		// PowerAmp
		filter.addAction("com.maxmpz.audioplayer.playstatechanged");
		// will be added any....
		// scrobblers detect for players (poweramp for example)
		// Last.fm
		filter.addAction("fm.last.android.metachanged");
		filter.addAction("fm.last.android.playbackpaused");
		filter.addAction("fm.last.android.playbackcomplete");

		// A simple last.fm scrobbler
		filter.addAction("com.adam.aslfms.notify.playstatechanged");

		// Scrobble Droid
		filter.addAction("net.jjc1138.android.scrobbler.action.MUSIC_STATUS");

		// Sony Ericsson
		filter.addAction("com.sonyericsson.music.TRACK_COMPLETED");// added
		filter.addAction("com.sonyericsson.music.metachanged");// added
		filter.addAction("com.sonyericsson.music.playbackcomplete");// added
		filter.addAction("com.sonyericsson.music.playstatechanged");// added

		// Apollo
		filter.addAction("com.andrew.apollo.metachanged");// added
		filter.addAction("com.andrew.apollo.playstatechanged");// added
	}

	@Override
	public void onDestroyService() {
	}

	@Override
	public void onReceiveMessage(Message msg) {
		switch (msg.what) {
		case MSG_METADATA_SEND_LATEST:
			sendMessage();
			break;
		}
	}
}