package com.subspace.android;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.subspace.redemption.ConnectActivity;
import com.subspace.redemption.MainMenuActivity;
import com.subspace.redemption.R;

public class NetworkServiceNotification {
	
	private NotificationManager mNM;
	private Notification notification;
	private Context context;
	
	// Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = R.string.network_service_started;
    
	public NetworkServiceNotification(Context serviceContext)
	{
		context = serviceContext;
		mNM = (NotificationManager)context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		setupNotification();
		reset();
	}
	
    private void setupNotification() {
        // Set the icon, scrolling text and timestamp
        notification = new Notification(R.drawable.icon, "",System.currentTimeMillis());        
        notification.flags = Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;              
    }
	
	public void reset()
    {
    	Intent mainMenuNotificationIntent = new Intent(context, MainMenuActivity.class);
        mainMenuNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);        
        mainMenuNotificationIntent.addFlags(Notification.FLAG_ONGOING_EVENT);
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent mainMenuContentIntent = PendingIntent.getActivity(context, 0,
        		mainMenuNotificationIntent, 0);
        
        notification.contentIntent = mainMenuContentIntent;
        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(context, "Subspace Mobile", "Disconnected", mainMenuContentIntent);
        // Send the notification.
        mNM.notify(NOTIFICATION, notification);    	
    }
    
	public void connected(String zoneName) {
    	
        Intent connectNotificationIntent = new Intent(context, ConnectActivity.class);
        connectNotificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);        
        connectNotificationIntent.addFlags(Notification.FLAG_ONGOING_EVENT);
		// TODO Auto-generated method stub
        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent connectContentIntent = PendingIntent.getActivity(context, 0,
        		connectNotificationIntent, 0);
        
        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(context, zoneName,
        		"Connected",
        		connectContentIntent);
        
     // Send the notification.
        mNM.notify(NOTIFICATION, notification);
	}

	public void cancel() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION);
	}

	
}
