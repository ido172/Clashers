package com.clashers.infrastructure;

import com.clashers.asynctasks.YouTubeSearch;
import com.clashers.asynctasks.interfaces.IYouTubeSearchParent;
import com.clashers.infrastructure.datastructures.SongData;

import android.app.Activity;
import android.view.View;

public class YouTubeDisplayer implements IYouTubeSearchParent{
	
	private View videoContainer;	
	private Activity parentActivity;
	private YouTubeSearch yts; 
	
	
	public YouTubeDisplayer(Activity parentActivity, View videoContainer, SongData songData) {
		this.videoContainer = videoContainer;
		this.parentActivity = parentActivity;
		yts = new YouTubeSearch(this, songData.getTrack() + " " + songData.getArtist());
		yts.execute();
	}

	@Override
	public void setSongYouTubeURL(String srcURL) {
		VideoPlayerManger videoPlayerManager = new VideoPlayerManger(parentActivity, videoContainer, srcURL);
		videoPlayerManager.init();
	}

}
