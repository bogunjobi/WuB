package mwn.bolfab.wakeupbuddy;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;    
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.widget.RemoteViews;
import android.widget.Toast;

public class Widget extends AppWidgetProvider {

	private static final String MESSAGE_DELETED = "WuBuddy Widget Deleted";
	//static String[] quote_array;
	//static String[] famous_array;
	static int count; 
	static Resources res;
	static String input;  
	static RemoteViews updateViews;
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {

		super.onUpdate(context, appWidgetManager, appWidgetIds);

		Intent intent = new Intent(context, Main.class);
		Intent intent01 = new Intent(context, Settings.class);
		Intent intent02 = new Intent(context, Main.class);
		PendingIntent pending = PendingIntent
				.getActivity(context, 0, intent, 0);
		PendingIntent pending01 = PendingIntent
				.getActivity(context, 0, intent01, 0);
		PendingIntent pending02 = PendingIntent
				.getActivity(context, 0, intent02, 0);

		updateViews = new RemoteViews(context.getPackageName(),
				R.layout.widget);
		
		updateViews.setTextViewText(R.id.read_more, "WuBuddy alarm time");
		//generate widget text input
		input = getPeriodicText(context);
		updateViews.setTextViewText(R.id.widget_text, input);
		//REMOVE THIS CODE WHEN DONE....
				
		updateViews.setOnClickPendingIntent(R.id.widget_text, pending);
		updateViews.setOnClickPendingIntent(R.id.logoView, pending01);
		updateViews.setOnClickPendingIntent(R.id.read_more, pending02);
		appWidgetManager.updateAppWidget(appWidgetIds, updateViews);

	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {   
		super.onDeleted(context, appWidgetIds);
		Toast.makeText(context, MESSAGE_DELETED, Toast.LENGTH_SHORT).show();
	}
		
	public static String getAlarmTime (Context context) {
	  String time = "coming soon...";	
		return time;
	}
	
}
