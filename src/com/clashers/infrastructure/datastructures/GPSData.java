package com.clashers.infrastructure.datastructures;

/**
 * Represents GPS information.
 * 
 * @author Bar
 * 
 */
public class GPSData {

	private final double latitude;
	private final double longitude;
	public final static double UNKNOWN_COORDINATE = -1;

	public GPSData() {
		latitude = UNKNOWN_COORDINATE;
		longitude = UNKNOWN_COORDINATE;
	}

	public GPSData(double latitude, double longtitue) {
		this.latitude = latitude;
		this.longitude = longtitue;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	
	public String toString(){
		return latitude + ", " + longitude;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != getClass())
            return false;
        
        GPSData gps = (GPSData) obj;
		return gps.latitude == (this.latitude) && gps.longitude == (this.longitude);
	}
	
	/**
	 * returns if the GPSData was already set, and includes information.
	 * @return
	 */
	public boolean isEmpty() {
		return latitude != UNKNOWN_COORDINATE && longitude != UNKNOWN_COORDINATE; 
	}
}