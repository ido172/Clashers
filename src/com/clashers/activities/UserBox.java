package com.clashers.activities;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.clashers.R;
import com.clashers.infrastructure.datastructures.SongData;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class UserBox extends Fragment {
	
	private String UID = null;
	private String GCMID = null;
	public String getGCMID() {
		return GCMID;
	}

	public void setGCMID(String gCMID) {
		GCMID = gCMID;
	}
	private String userName = null;
	private SongData songData = null;
	private ImageView faceImage = null;
	public ImageView getFaceImage() {
		return faceImage;
	}

	public void setFaceImage(ImageView faceImage) {
		this.faceImage = faceImage;
	}
	private Bitmap profileBitmap = null;
	
	private UserBox thisUserBox = null;
	
	private TextView distanceLabel = null;
	
	private String distanceText = null;
	
	public void setDistanceText(String distance) {
		distanceText = distance;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		
		thisUserBox = this;
		
		View mainView = inflater.inflate(R.layout.user_box, container, false);
		
		faceImage = (ImageView) mainView.findViewById(R.id.face_image);
		setImageClick();
		distanceLabel = (TextView) mainView.findViewById(R.id.distance_label);
		distanceLabel.setText(distanceText);
		
		Thread trd = new Thread(new Runnable() {
			
			@Override
			public void run() {
				loadFacebookImage();
				
			}
		});
		trd.start();
		return mainView;
	}
	private void setImageClick() {
		faceImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivity.getActivityInsttance().imageClick(thisUserBox);
				
			}
		});	
	}
	
	private void loadFacebookImage() {
		
		URL newurl;
		try {
			newurl = new URL("http://graph.facebook.com/"+ UID + "/picture?type=large");
			profileBitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
			MainActivity.getActivityInsttance().runOnUiThread(new Runnable() {
				float dpi = MainActivity.getActivityInsttance().metrics.density;
				@Override
				public void run() {
					faceImage.getLayoutParams().width = (int)(profileBitmap.getWidth() * dpi);
					faceImage.getLayoutParams().height = (int)(profileBitmap.getHeight() * dpi);
					faceImage.setImageBitmap(profileBitmap);
					
					
				}
			});
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 	

	}
	
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUID() {
		return UID;
	}
	public void setUID(String uID) {
		UID = uID;
	}
	
	public SongData getSongData() {
		return songData;
	}
	public void setSongData(SongData songData) {
		this.songData = songData;
	}	
}
