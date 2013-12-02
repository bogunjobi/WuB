package mwn.bolfab.wakeupbuddy;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
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
	TextView name;
	TextView note;
	TextView cTime; 
	String contactName = null;
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_USERPHONE = "user_phone";
    private static final String TAG_BUDDYNAME = "buddy_name";
    private static final String TAG_BUDDYPHONE = "buddy_phone";
    private static final String TAG_MSG = "message"; 
    private static final String TAG_TIME = "time";
    private static final String TAG_TONE = "ringtone_id";
    private static final String url_alarm = "http://wubuddy.hopto.org/set_alarm_time";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_alarm);
		contactName = getIntent().getStringExtra("cName");
		String [] splitName = contactName.split(" ");
		Log.d("Received Contact", contactName);
		name = (TextView)findViewById(R.id.contactName);
		note = (TextView)findViewById(R.id.note);
		name.setText(contactName);
		note.setHint("Wake up, " + splitName[0] + "!");
		cTime = (TextView)findViewById(R.id.chooseTime);
		
		
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
                	//SimpleDateFormat mSDF = new SimpleDateFormat("HH:mm");
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
	    	Log.i("Time", cTime.getText().toString());
	    	String [] time = cTime.getText().toString().split("\n");
	    	SimpleDateFormat parseFormat = new SimpleDateFormat("HH:mm");
	    	SimpleDateFormat dispFormat = new SimpleDateFormat("hh:mm a");
	    	String parsedTime = null;
	    	try {
				Date time24 = dispFormat.parse(time[1]);
				parsedTime = parseFormat.format(time24);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	String msg = note.getText().toString();
	    	Log.i("ParsedTime", parsedTime);
	    	//NOTE: Passing ringtone name, not uri!!!
	    	new SendAlarmTask().execute(Main.phoneNum, contactName, AddContacts.hashmap.get(contactName), parsedTime, 
	    			rTone.getText().toString(), msg);
	      
	      break;

	    default:
	      break;
	    }

	    return true;
	  }
	
	
	

	private class SendAlarmTask extends AsyncTask<String, String, String> {
	    ProgressDialog pDialog;
		 JSONParser jsonParser = new JSONParser();
	        /**
	         * Before starting background thread Show Progress Dialog
	         * */
	        @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(SendAlarm.this);
	            pDialog.setMessage("Sending Alarm ...");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(true);
	            pDialog.show();
	        }
	        
	       protected String doInBackground(String... params) {
	    	   String senderNumber = params[0];
	    	   String senderName = params[1];
	    	   String receiverNumber = params[2];
	    	   String time = params[3];
	    	   String tone = params[4];
	    	   String msg = params[5];
	    	   List<NameValuePair> args = new ArrayList<NameValuePair>();
	    	   args.add(new BasicNameValuePair(TAG_USERPHONE, senderNumber));
	    	   args.add(new BasicNameValuePair(TAG_BUDDYNAME, senderName));
	    	   args.add(new BasicNameValuePair(TAG_BUDDYPHONE, receiverNumber));
	    	   args.add(new BasicNameValuePair(TAG_TIME, time));
	    	   args.add(new BasicNameValuePair(TAG_TONE, tone));
	    	   args.add(new BasicNameValuePair(TAG_MSG, msg));
	    	   JSONObject json = jsonParser.makeHttpRequest(url_alarm, "POST", args);
	    	   int success;
            try {
                 success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // successfully updated
                    finish();
                } else {
                	Toast.makeText(SendAlarm.this, "WARNING: Alarm not sent", Toast.LENGTH_SHORT)
      	          .show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }            

			return null;	
			}
	        
	        protected void onPostExecute(String file_url) {
	            // dismiss the dialog once product updated
	            pDialog.dismiss();
	        }
	}
	
}
