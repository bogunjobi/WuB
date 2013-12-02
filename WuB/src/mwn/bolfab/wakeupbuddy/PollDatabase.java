package mwn.bolfab.wakeupbuddy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.AlarmClock;
import android.util.Log;
import android.widget.Toast;

public class PollDatabase extends Service {
	//private static final String TAG = "PollDatabase";
	JSONParser jParser = new JSONParser();
	private static String url_params = "http://wubuddy.hopto.org/user_buddy.php";
	private static String url_alarmSet = "http://wubuddy.hopto.org/get_alarm";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_ALARM = "alarmSet";
	private static final String TAG_TIME = "time";
	private static final String TAG_RINGTONE = "ringtone_id";
	private static final String TAG_SENDER = "user_phone";
	private static final String TAG_MESSAGE = "message";
	
	//test phone number
	String phoneNum = Main.phoneNum;
	
	public PollDatabase() {
	}
	
	public void onCreate() {
		super.onCreate();
		Toast.makeText(this, "Service Created", Toast.LENGTH_SHORT).show();
		
	}
	
	

	public int onStartCommand(Intent intent, int flags, int startID){
		
		//phoneNum = intent.getStringExtra("Phone");
		boolean alarmSet = false;
		try {
			alarmSet = new CheckAlarmSet().execute().get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (alarmSet){
			Log.i("AlarmSet", "true");
			ArrayList<HashMap<String, String>> alarmInfo = new ArrayList<HashMap<String, String>>();
			try {
				alarmInfo = new GetAlarmParams().execute().get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (alarmInfo!=null){
				String time = alarmInfo.get(0).get("time");
				Log.i("time", time);
				String tone = alarmInfo.get(1).get("ringtone_id");
				Log.i("ringtone", tone);
				String sender = alarmInfo.get(2).get("sender");
				Log.i("sender", sender);
				String message = alarmInfo.get(3).get("message");
				Log.i("message", message);
				new CreateAlarm().execute(time, tone, sender, message);
			}else {
				Log.i("alarmInfo", "null");
			}
		 
		}
		return Service.START_NOT_STICKY;
	}

	class CheckAlarmSet extends AsyncTask<String, String, Boolean>{
			protected Boolean doInBackground(String... args) {
				List<NameValuePair> params = new ArrayList<NameValuePair>();
				params.add(new BasicNameValuePair("phone", phoneNum));
				Log.i("Phone in AsyncCAS", phoneNum);
				// getting JSON string from URL
				JSONObject json = jParser.makeHttpRequest(url_alarmSet, "GET", params);
				
				// Check your log cat for JSON response
				Log.d("Alarm details: ", json.toString());

				try {
					// Checking for SUCCESS TAG
					int success = json.getInt(TAG_SUCCESS);
					String message = json.getString(TAG_MESSAGE);
					String [] msgString = message.split(":");
					String[] msgValue = msgString[1].split("\"");
					Log.i("Message", msgValue[1]);
					if (success == 1) {
						if (msgValue[1].equals("1"))
						return true;               
					}
				}catch (JSONException e){
					e.printStackTrace();
				}
				return false;	
			}				
	}
	
	class UpdateDB extends AsyncTask<String, String, Boolean>{
		protected Boolean doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("phone", phoneNum));
			params.add(new BasicNameValuePair("set_alarm", "0"));
			Log.i("Phone in AsyncUDB", phoneNum);
			String url_updateDB = "http://wubuddy.hopto.org/update_alarm_status.php";
			
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_updateDB , "POST", params);
			
			// Check your log cat for JSON response
			Log.d("Alarm details: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);
				if (success == 1) {
					return true;               
				}
			}catch (JSONException e){
				e.printStackTrace();
			}
			return false;
	
	}
		
}

	class CreateAlarm extends AsyncTask<String, String, String>{
		protected String doInBackground(String... args) {
			
			String time = args[0];
			//String ringtone = args[1];
			String sender = args[2];
			String message = args[3];
			String [] splitTime = time.split(":"); 
			// getting JSON string from URL
			Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
			alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			alarmIntent.putExtra(AlarmClock.EXTRA_MESSAGE, "Message: "+message+ " from sender: "+sender);
			alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, splitTime[0]);
			alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, splitTime[1]);
			alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
			//alarmIntent.putExtra(AlarmClock.EXTRA_RINGTONE, ringtone);
			startActivity(alarmIntent);
			return null;					
	}
			
}

	
	class GetAlarmParams extends AsyncTask<String, String, ArrayList<HashMap<String, String>>>{
		protected ArrayList<HashMap<String, String>> doInBackground(String... args) {
			ArrayList<HashMap<String, String>> alarmInfo = new ArrayList<HashMap<String, String>>();			
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			HashMap<String, String> map = new HashMap<String, String>();
				//alarmInfo.add(null);
			params.add(new BasicNameValuePair("phone", phoneNum));
			JSONObject jsonParser = jParser.makeHttpRequest(url_params, "GET", params);
			JSONObject alarm = null;
			try {
				JSONArray alarmArr = jsonParser.getJSONArray(TAG_ALARM);
				alarm = alarmArr.getJSONObject(0); 
				map.put(TAG_TIME, alarm.getString(TAG_TIME));
				map.put(TAG_RINGTONE, alarm.getString(TAG_RINGTONE));
				map.put(TAG_SENDER, alarm.getString(TAG_SENDER));
				map.put(TAG_MESSAGE, alarm.getString(TAG_MESSAGE));
				alarmInfo.add(map);
			} catch (JSONException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			return alarmInfo;
				
		}
			
	}
				
	
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO: Return the communication channel to the service.
		return null;
	} 
}
