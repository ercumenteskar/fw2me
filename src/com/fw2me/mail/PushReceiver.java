package com.fw2me.mail;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;



public class PushReceiver extends BroadcastReceiver
{
	@Override
	public void onReceive(Context context, Intent intent)
	{
		String msgType = "";
		String msgTitle = "";
		String message = "";
    String tickerText = "";
    //
		if ( intent.getStringExtra("Message") != null )
			 message = intent.getStringExtra("Message");
		if ( intent.getStringExtra("MsgTitle") != null )
			msgTitle = intent.getStringExtra("MsgTitle");
		if ( intent.getStringExtra("MsgType") != null )
			msgType = intent.getStringExtra("MsgType");
		if ( intent.getStringExtra("tickerText") != null )
			tickerText = intent.getStringExtra("tickerText");
		if ( message.equals("") )
		  return;
		NotificationCompat.Builder mBuilder =
		    new NotificationCompat.Builder(context)
		    .setSmallIcon(R.drawable.ic_launcher_sb)
		    .setContentTitle("My notification")
		    .setContentText("Hello World!");
		NotificationManager mNotificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    
	// notificationID allows you to update the notification later on.
	  mNotificationManager.notify(0, mBuilder.build());
		
		return;
//		Notification notification; 
//		{
//			// Create a test notification
//			notification = new Notification();
//			notification.icon = R.drawable.ic_launcher;
//			notification.tickerText = tickerText;
//			notification.when = System.currentTimeMillis();
//			// Sound + vibrate + light
//			notification.defaults = Notification.DEFAULT_ALL;
//			// Dismisses when pressed
//			notification.flags = Notification.FLAG_AUTO_CANCEL;
//			// Create pending intent without a real intent
//			notification.setLatestEventInfo(context, msgTitle, message, null);
//			// Get notification manager
//			NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//			// Show the notification
//			mNotificationManager.notify(0, notification);
//		}
	}
}