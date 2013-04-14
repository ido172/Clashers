package com.clashers.infrastructure.datastructures;

import com.clashers.services.BGService;

import android.location.Location;

public class UserDataFromServer {
	private final String uid;
	private final String gcmID;
	private final String userName;
	private final String song;
	private final String artist;
	private final String album;
	private final String longtitude;
	private final String latitude;

	public UserDataFromServer(String uid, String gcmID, String userName, String song, String artist, String album, String longtitude, String latitude) {
		this.uid = uid;
		this.gcmID = gcmID;
		this.userName = userName;
		this.song = song;
		this.artist = artist;
		this.album = album;
		this.longtitude = longtitude;
		this.latitude = latitude;		
	}

	public String getGcmID() {
		return gcmID;
	}

	public float getDistance() {
		float[] results = new float[3] ;
		try {
		Location.distanceBetween(BGService.gpsData.getLatitude(), BGService.gpsData.getLongitude(), Float.parseFloat(latitude), Float.parseFloat(longtitude), results);
		} catch (IllegalArgumentException e) {
			return -1;
		}
		return results[0];
	}
	
	public String getUID() {
		return uid;
	}
	
	public String getUserName() {
		return userName;
	}

	public String getSong() {
		return song;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	@Override
	public String toString() {
		return String.format("UID: %s, Song: song, Artist: %s, Album: %s", uid, song, artist, album);
	}

	public String getLatitude() {
		return latitude;
	}

	public String getLongtitude() {
		return longtitude;
	}
}
