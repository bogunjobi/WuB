package com.fab.quotes;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class Alarm extends Activity {
	private static long mSeconds;
	private static final int FIRST_TIME = 1000;
	static Toast mToast;
	private static final int DIALOG_SINGLE_CHOICE = 5;
	static PendingIntent sender;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alarm_controller);

		Button button = (Button) findViewById(R.id.start_repeating);
		button.setOnClickListener(mStartRepeatingListener);
		button = (Button) findViewById(R.id.stop_repeating);
		button.setOnClickListener(mStopRepeatingListener);
		View analog = (View) findViewById(R.id.analogClock1);
		analog.setOnClickListener(mAnalogListener);
	}

	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		showDialog(DIALOG_SINGLE_CHOICE);
	}

	protected Dialog onCreateDialog(int id) {
		// should add another option called slide show to this and probably
		// watch effect
		final String[] period_array = { "Slide Show (slow)", "Slide Show (medium)",
				"Slide Show (fast)", "Half Hour", "1 Hour", "3 Hours",
				"6 Hours", "12 Hours", "1 Day", "3 Days", "1 Week" };
		switch (id) {
		case DIALOG_SINGLE_CHOICE:
			return new AlertDialog.Builder(Alarm.this)
					.setIcon(R.drawable.app_logo)
					.setTitle(R.string.alert_dialog_single_choice)
					.setSingleChoiceItems(period_array, 0,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									mSeconds = getTime(whichButton);
									Toast.makeText(
											Alarm.this,
											"You selected: "
													+ period_array[whichButton],
											Toast.LENGTH_SHORT).show();
									/*   
									 * User clicked on a radio button do some
									 * stuff   
									 */
								}
							})
					.setPositiveButton(R.string.alert_dialog_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked Yes so do some stuff */
								}
							})   
					.setNegativeButton(R.string.alert_dialog_cancel,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									/* User clicked No so do some stuff */  
								}
							}).create();
		}
		return null;
	}

	private OnClickListener mStartRepeatingListener = new OnClickListener() {
		public void onClick(View v) {
			// When the alarm goes off, we want to broadcast an Intent to our
			// BroadcastReceiver. Here we make an Intent with an explicit class
			// name to have our own receiver (which has been published in
			// AndroidManifest.xml) instantiated and called, and then create an
			// IntentSender to have the intent executed as a broadcast.
			// Note that unlike above, this IntentSender is configured to
			// allow itself to be sent multiple times.
			Intent intent = new Intent(Alarm.this, RepeatingAlarm.class);
			sender = PendingIntent.getBroadcast(Alarm.this, 0, intent, 0);

			// We want the alarm to go off 30 seconds from now.
			long firstTime = SystemClock.elapsedRealtime();
			firstTime += FIRST_TIME;

			// Schedule the alarm!
			AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime,
					mSeconds, sender);

			// Tell the user about what we did.
			if (mToast != null) {
				mToast.cancel();
			}
			mToast = Toast.makeText(Alarm.this, R.string.repeating_scheduled,
					Toast.LENGTH_LONG);
			mToast.show();

			// play ringtone
			play();

		}
	};

	private OnClickListener mStopRepeatingListener = new OnClickListener() {
		public void onClick(View v) {
			// Create the same intent, and thus a matching IntentSender, for
			// the one that was scheduled.
			Intent intent = new Intent(Alarm.this, RepeatingAlarm.class);
			PendingIntent sender = PendingIntent.getBroadcast(Alarm.this, 0,
					intent, 0);

			// And cancel the alarm.
			// AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
			// am.cancel(sender);
			cancelAlarm(getApplicationContext(), sender);

			// mToast = Toast.makeText(Alarm.this,
			// R.string.repeating_unscheduled,
			// Toast.LENGTH_LONG);
			// mToast.show();

		}
	};

	private OnClickListener mAnalogListener = new OnClickListener() {
		public void onClick(View v) {
			showDialog(DIALOG_SINGLE_CHOICE);
		}
	};

	public void play() {
		Uri notification = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		Ringtone r = RingtoneManager.getRingtone(getApplicationContext(),
				notification);
		r.play();
	}

	public static long getTime(final int time) {
		switch (time) {
		case 0:
			return 15000;
		case 1:
			return 7500;
		case 2:
			return 3000;
		case 3:
			return 900000;
		case 4:
			return 1800000;
		case 5:
			return 5400000;
		case 6:
			return 10800000;
		case 7:
			return 21600000;
		case 8:
			return 43200000;
		case 9:
			return 129600000;
		case 10:
			return 302400000;
		}
		return 0;
	}

	public static void cancelAlarm(final Context context, final PendingIntent pi) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(ALARM_SERVICE);
		am.cancel(pi);
		// Tell the user about what we did.
		if (mToast != null) {
			mToast.cancel();
		}
		mToast = Toast.makeText(context, R.string.repeating_unscheduled,
				Toast.LENGTH_SHORT);
		mToast.show();
	}

}
