package mwn.bolfab.wakeupbuddy;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.content.Intent;
import android.provider.AlarmClock;

public class SetAlarm {

	public SetAlarm(Calendar datetime, int ringtone_id, String sender, String message) {
		 	GregorianCalendar cal = new GregorianCalendar();
		    cal.setTimeInMillis(System.currentTimeMillis());
		    day = 
		    hour = cal.get(Calendar.HOUR_OF_DAY);
		    minute = cal.get(Calendar.MINUTE);

		    Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
		    i.putExtra(AlarmClock.EXTRA_HOUR, hour + Integer.parseInt(etHour.getText().toString()));
		    i.putExtra(AlarmClock.EXTRA_MINUTES, minute + Integer.parseInt(etMinute.getText().toString()));
		    i.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
		    startActivity(i);
	}

}
