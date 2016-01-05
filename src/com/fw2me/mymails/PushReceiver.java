package com.fw2me.mymails;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



public class PushReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{

		String msgType = "";
		String msgTitle = "";
		String message = "";
    String tickerText = "";
    
		if ( intent.getStringExtra("Message") != null )
			 message = intent.getStringExtra("Message");
		if ( intent.getStringExtra("MsgTitle") != null )
			msgTitle = intent.getStringExtra("MsgTitle");
		if ( intent.getStringExtra("MsgType") != null )
			msgType = intent.getStringExtra("MsgType");
		if ( intent.getStringExtra("tickerText") != null )
			tickerText = intent.getStringExtra("tickerText");
		Notification notification; 
		{
			// Create a test notification
			notification = new Notification();
			notification.icon = com.fw2me.mymails.R.drawable.ic_launcher;
			notification.tickerText = tickerText;
			notification.when = System.currentTimeMillis();
			// Sound + vibrate + light
			notification.defaults = Notification.DEFAULT_ALL;
			// Dismisses when pressed
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			// Create pending intent without a real intent
			notification.setLatestEventInfo(context, msgTitle, message, null);
			// Get notification manager
			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			// Show the notification
			mNotificationManager.notify(0, notification);
		}
	}
}