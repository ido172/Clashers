package com.clashers.infrastructure;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import com.clashers.R;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.URLUtil;
import android.widget.ImageButton;
import android.widget.VideoView;

public class VideoPlayerManger {
	
	private static final String TAG = "VideoPlayer";

	private VideoView mVideoView;
	private ImageButton mPlay;
	private ImageButton mPause;
	private ImageButton mReset;
	private ImageButton mStop;
	private String videoSrc = null;
	private String finalVideoPath = null;
	
	private Activity parentActivity;
	
	public VideoPlayerManger(Activity parentActivity, View i_parentView, String i_videoSRC) {
		videoSrc = i_videoSRC;
		this.parentActivity = parentActivity;
		mVideoView = (VideoView) parentActivity.findViewById(R.id.video_view);		
	}
	
	public void init() {	

		mPlay = (ImageButton) parentActivity.findViewById(R.id.play);
		mPause = (ImageButton) parentActivity.findViewById(R.id.pause);
		mReset = (ImageButton) parentActivity.findViewById(R.id.reset);
		mStop = (ImageButton) parentActivity.findViewById(R.id.stop);

		mPlay.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				playVideo();
				
			}
		});
		mPause.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (mVideoView != null) {
					mVideoView.pause();
					
				}
			}
		});
		mReset.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (mVideoView != null) {
					mVideoView.seekTo(0);
				}
			}
		});
		mStop.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {
				if (mVideoView != null) {
					stopVideo();
				}
			}
		});
		
		
		playVideo();
		
	}

	public void playVideo() {
		parentActivity.runOnUiThread(new Runnable(){
			public void run() {
				loadVideo();
			}
		});
	}
	
	public void stopVideo() {
		mVideoView.stopPlayback();
	}
	
	public void loadVideo() {
		try {
			Log.d(TAG, "src: " + videoSrc);
			if (videoSrc == null || videoSrc.length() == 0) {


			} else {
				// If the path has not changed, just start the media player
				if (mVideoView != null && mVideoView.getBufferPercentage() == 100) {
					mVideoView.start();
					mVideoView.requestFocus();
					return;
				}
				Thread trd = new Thread(new Runnable() {
					
					@Override
					public void run() {
						try {
							getDataSource(videoSrc);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				trd.start();
			}
		} catch (Exception e) {
			Log.e(TAG, "error: " + e.getMessage(), e);
			if (mVideoView != null) {
				mVideoView.stopPlayback();
			}
		}
	}

	private void getDataSource(String path) throws IOException {
		if (!URLUtil.isNetworkUrl(path)) {
			finalVideoPath = path;
		} else {
			URL url = new URL(path);
			URLConnection cn = url.openConnection();
			cn.connect();
			InputStream stream = cn.getInputStream();
			if (stream == null)
				throw new RuntimeException("stream is null");
			File temp = File.createTempFile("mediaplayertmp", "dat");
			temp.deleteOnExit();
			String tempPath = temp.getAbsolutePath();
			FileOutputStream out = new FileOutputStream(temp);
			byte buf[] = new byte[128];
			do {
				int numread = stream.read(buf);
				if (numread <= 0)
					break;
				out.write(buf, 0, numread);
			} while (true);
			try {
				stream.close();
				out.close();
			} catch (IOException ex) {
				Log.e(TAG, "error: " + ex.getMessage(), ex);
			}
			finalVideoPath =  tempPath;
		}
		parentActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mVideoView.setVideoPath(finalVideoPath);
				mVideoView.requestFocus();
				mVideoView.start();
			}
		});
	}
}
