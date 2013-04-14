package com.clashers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.clashers.R;
import com.clashers.R.drawable;
import com.clashers.R.string;
import com.clashers.activities.MainActivity;
import com.clashers.data.Data;
import com.clashers.pushnotifications.CommonUtilities;
import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(Data.GCM_SENDER_ID);
       }

    /**
     * Method called on device registered
     **/
    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.d(TAG, "Device registered: regId = " + registrationId);
        CommonUtilities.displayMessage(context, "Your device registred with GCM");
       
    }

    /**
     * Method called on device un registred
     * */
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.d(TAG, "Device unregistered");
        CommonUtilities.displayMessage(context, getString(R.string.gcm_unregistered));
        
    }

    /**
     * Method called on Receiving a new message
     * */
    @Override
    protected void onMessage(Context context, Intent intent) {
        Log.d(TAG, "Received message");
        String message = intent.getExtras().getString("price");
        
        CommonUtilities.displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on receiving a deleted message
     * */
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.d(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        CommonUtilities.displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    /**
     * Method called on Error
     * */
    @Override
    public void onError(Context context, String errorId) {
        Log.d(TAG, "Received error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.d(TAG, "Received recoverable error: " + errorId);
        CommonUtilities.displayMessage(context, getString(R.string.gcm_recoverable_error,
                errorId));
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private void generateNotification(Context context, String message) {
    	
    	
    	NotificationCompat.Builder mBuilder =
    	        new NotificationCompat.Builder(context)
    	        .setSmallIcon(R.drawable.notification_icon)
    	        .setContentTitle("My notification")
    	        .setContentText("Hello World!");
    	// Creates an explicit intent for an Activity in your app
    	Intent resultIntent = new Intent(context, MainActivity.class);

    	// The stack builder object will contain an artificial back stack for the
    	// started Activity.
    	// This ensures that navigating backward from the Activity leads out of
    	// your application to the Home screen.
    	TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
    	// Adds the back stack for the Intent (but not the Intent itself)
    	stackBuilder.addParentStack(MainActivity.class);
    	// Adds the Intent that starts the Activity to the top of the stack
    	stackBuilder.addNextIntent(resultIntent);
    	PendingIntent resultPendingIntent =
    	        stackBuilder.getPendingIntent(
    	            0,
    	            PendingIntent.FLAG_UPDATE_CURRENT
    	        );
    	mBuilder.setContentIntent(resultPendingIntent);
    	
    	
    	NotificationManager mNotificationManager =
    	    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    	// mId allows you to update the notification later on.
    	mNotificationManager.notify(0, mBuilder.build());     


		
    }

}
