package mwn.bolfab.wakeupbuddy;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.IntentService;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.provider.AlarmClock;
import android.util.Log;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class SetAlarms extends IntentService {
	// TODO: Rename actions, choose action names that describe tasks that this
	
	public SetAlarms() {
		super("SetAlarms");
	}
	private static final String TAG_ALARMSET = "alarmSet";
	private static final String TAG_PHONE = "phone";
	private static final String TAG_SUCCESS = "success";


	@Override
	protected void onHandleIntent(Intent intent) {
		UpdateField();
		String time = intent.getStringExtra("time");
		String ringtone = intent.getStringExtra("ringtone");
		String sender = intent.getStringExtra("sender");
		String message = intent.getStringExtra("message");
		String [] splitTime = time.split(":");
		Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
		alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		alarmIntent.putExtra(AlarmClock.EXTRA_MESSAGE, message);
		//Calendar calendar = Calendar.getInstance();
		//calendar.add(Calendar.MINUTE, 1);
		alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, splitTime[0]);
		alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, splitTime[1]);
		alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
		
		getApplicationContext().startActivity(alarmIntent);
		displayAlarm(sender, time);
		
	}
	
	public void UpdateField(){
		
		String alarmSet = "1";
	        
			// Building Parameters
	        List<NameValuePair> params = new ArrayList<NameValuePair>();
	        params.add(new BasicNameValuePair(TAG_ALARMSET, alarmSet));
	        params.add(new BasicNameValuePair(TAG_PHONE, Main.phoneNum));
	      
	        String url_updateField = "http://wubuddy.hopto.org/ . php";
			// sending modified data through http request
	        // Notice that update product url accepts POST method
	        JSONParser jsonParser = new JSONParser();
	        JSONObject json = jsonParser.makeHttpRequest(url_updateField ,
	                "POST", params);

	        // check json success tag
	        try {
	            int success = json.getInt(TAG_SUCCESS);

	            if (success == 1) {
	                Log.i("Updating DB", "successful!");
	              
	            } else {
	                // failed to update product
	            }
	        } catch (JSONException e) {
	            e.printStackTrace();
	        }
		
	}
	
	public void displayAlarm(String sender, String time){
		AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
		builder.setCancelable(false);
		builder.setTitle("Alarm Received");
		builder.setMessage("Alarm received from "+sender+" scheduled for "+time);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
		 
		  public void onClick(DialogInterface dialog, int which) {
			  dialog.dismiss();
		  }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	

	
}
