package mwn.bolfab.wakeupbuddy;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SendAlarm extends Activity {
	 
	String chosenRingtone = null;
	TextView rTone = null;
	//private Object chosenRingtone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_alarm);
		String contactName = getIntent().getStringExtra("cName");
		String [] splitName = contactName.split(" ");
		Log.d("Received Contact", contactName);
		TextView name = (TextView)findViewById(R.id.contactName);
		TextView note = (TextView)findViewById(R.id.note);
		name.setText(contactName);
		note.setHint("Wake up, " + splitName[0] + "!");
		final TextView cTime = (TextView)findViewById(R.id.chooseTime);
		
		
		//Set alarm to current time by default. Display time in HH:MM am/pm format
		
		Calendar c = Calendar.getInstance();
		SimpleDateFormat fTime = new SimpleDateFormat("KK:mm a", Locale.US);
        String defaultTime = fTime.format(c.getTime());
		cTime.setText( "Alarm Time\n"+defaultTime);
	
		
		
		cTime.setOnClickListener(new OnClickListener(){
        public void onClick(View v) {
            // TODO Auto-generated method stub
            final Calendar mCurrentTime = Calendar.getInstance();
            int hour = mCurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mCurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker = new TimePickerDialog(SendAlarm.this, 
            		new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                	mCurrentTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                	mCurrentTime.set(Calendar.MINUTE, selectedMinute);
                    SimpleDateFormat mSDF = new SimpleDateFormat("hh:mm a", Locale.US);
                    String time = mSDF.format(mCurrentTime.getTime());

                    cTime.setText( "Alarm Time\n"+time);
                }
            }, hour, minute, false);//Yes 12 hour time
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();

        	}
		});
		
		//handle the display for ringtone
		rTone = (TextView)findViewById(R.id.ringtone);
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
	     if(alert == null){
	         // alert is null, using backup
	         alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	         if(alert == null){  //in the unlikely event that there's no default notification
	             // alert backup is null, using 2nd backup
	             alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);               
	         }
	     }
	     Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alert);
	     rTone.setText("Ringtone\n"+ r.getTitle(getApplicationContext())); //set default alarm
	     
	     
	     rTone.setOnClickListener(new OnClickListener(){
	    	 public void onClick(View v) {
	    		 Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
	    		 intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
	    		 intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Alarm Tone");
	    		 intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
	    		 SendAlarm.this.startActivityForResult(intent, 5);
	    	 }
	    });
	}
	
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent intent)
	 {
		
	     if (resultCode == Activity.RESULT_OK && requestCode == 5)
	     {
	          Uri uri = intent.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

	          if (uri != null)
	          {
	             this.chosenRingtone = uri.toString();
	    	     Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), uri);
	    	     rTone.setText("Ringtone\n"+r.getTitle(getApplicationContext()));
	          }
	          else
	          {
	              this.chosenRingtone = null;
	          }
	      }            
	  }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.send_alarm, menu);
		return true;
	}

	@Override
	  public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.action_cancel:
		      this.finish();
		      break;
	    case R.id.action_send:
	      Toast.makeText(this, "Send selected", Toast.LENGTH_SHORT)
	          .show();
	      break;

	    default:
	      break;
	    }

	    return true;
	  } 
}
