package com.clashers.activities;



import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import com.clashers.R;
import com.clashers.data.Data;
import com.clashers.infrastructure.YouTubeDisplayer;
import com.clashers.infrastructure.datastructures.SongData;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class PlayerActivity extends Activity{

	
	private Bitmap profileBitmap = null;
	private String UID = null;
	private SongData songData = null;
	private ImageView faceImage = null;
	
	YouTubeDisplayer nowListeningPlayer;
	YouTubeDisplayer[] historyPlayers;
	
	@Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.player_screen);
        
        // Add user Details
        Intent myIntent= getIntent();
        UID = myIntent.getStringExtra(Data.SQL_TAG_UID);
        TextView userName = (TextView)findViewById(R.id.textUserName);
        userName.setText(myIntent.getStringExtra(Data.SQL_TAG_USER_NAME));
        faceImage = (ImageView)findViewById(R.id.user_small_image);
		Thread trd = new Thread(new Runnable() {
			@Override
			public void run() {
				loadFacebookImage();
				
			}
		});
		trd.start();
		
		
		// init player
		songData = new SongData(myIntent.getStringExtra(Data.SQL_TAG_SONG),  myIntent.getStringExtra(Data.SQL_TAG_ARTIST), "", "");
		TextView songNameText = (TextView)findViewById(R.id.songName);
		songNameText .setText(songData.getTrack() + " - " + songData.getArtist()); 
        nowListeningPlayer = new YouTubeDisplayer(this, findViewById(R.id.video_player),songData);
        
          
    }
	
	
	private void loadFacebookImage() {
		
		URL newurl;
		try {
			newurl = new URL("http://graph.facebook.com/"+ UID + "/picture?type=small");
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
	
	
}
