package com.clashers.infrastructure.datastructures;

public class SongData {
	private final String track;
	private final String artist;
	private final String album;
	private final String songTime;
	private final static String UNKOWN = "unknown";

	public SongData() {
		this.track = UNKOWN;
		this.artist = UNKOWN;
		this.album = UNKOWN;
		this.songTime = UNKOWN;
	}
	
	public SongData(String name, String artist, String album, String songTime){
		this.track = name;
		this.artist = artist;
		this.album = album;
		this.songTime = songTime;
	}

	public String getTrack() {
		return track;
	}

	public String getArtist() {
		return artist;
	}

	public String getAlbum() {
		return album;
	}

	public String getSongTime() {
		return songTime;
	}
	
	public String toString(){
		return artist + ":" + album + ":" + track + ":" + songTime;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;
        
        SongData ds = (SongData) obj;
		return ds.artist.equals(this.artist) && ds.album.equals(this.album) && ds.track.equals(this.track);
	}
	
	/**
	 * returns if the data was already set, and thus has significance in sending it.
	 * @return
	 */
	public boolean isEmpty(){
		return this.album != UNKOWN || this.artist != UNKOWN || this.track != UNKOWN || this.songTime!= UNKOWN;
	}
}