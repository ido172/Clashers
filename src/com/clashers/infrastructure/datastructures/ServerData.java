package com.clashers.infrastructure.datastructures;

public class ServerData {

	private SongData songData;
	private GPSData gpsData;
	
	public ServerData(GPSData gpsData, SongData songData){
		this.songData = songData;
		this.gpsData = gpsData;
	}
	
	public ServerData() {
		this.songData = new SongData();
		this.gpsData = new GPSData();
	}

	public SongData getSongData() {
		return songData;
	}

	public GPSData getGpsData() {
		return gpsData;
	}

	public void setSongData(SongData songData) {
		this.songData = songData;
	}

	public void setGpsData(GPSData gpsData) {
		this.gpsData = gpsData;
	}
	
	public String toString(){
		return gpsData.toString() + songData.toString();
	}
}
