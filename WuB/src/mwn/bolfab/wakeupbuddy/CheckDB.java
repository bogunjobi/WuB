/**
 * 
 */
package mwn.bolfab.wakeupbuddy;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * @author ogunjobj
 *
 */

//check if alarmSet? If true, retrieve  info and set alarm. For the query, make sure to find the alarmSent field where phoneNo == wahever
public class CheckDB extends Service {

	boolean alarmSet = false;
	HttpClient client = new DefaultHttpClient();
	
	JSONParser jParser = new JSONParser();
	private static String url_params = "link";
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public void onCreate(){
		super.onCreate();
		alarmSet = alarmSet();
		if (alarmSet){
			
			//SetAlarm(time, ringtone_id, sender, message);
		}
		Calendar cal = Calendar.getInstance();
		Intent intent = new Intent(this, CheckDB.class);
		PendingIntent pintent = PendingIntent.getService(this, 0, intent, 0);
		AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		// Start every 60 seconds
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 60*1000, pintent); 
	}

	public int onStartCommand(Intent intent, int flags, int startID){
		return Service.START_NOT_STICKY;
	}
	
	 void connectDB(String link, List<NameValuePair> Al) throws ClientProtocolException, IOException{
		HttpPost httppost = new HttpPost(link);
		httppost.setEntity(new UrlEncodedFormEntity(Al));
		HttpResponse resp = client.execute(httppost);
		HttpEntity entity = resp.getEntity();
		InputStream is = entity.getContent();
		
		//String data = EntityUtils.toString(entity);
		//JSONObject jObj = new JSONObject(data);
	}

	public boolean alarmSet(){
		String link = "link.php";
		List <NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("phone", Main.USERNUMBER));
		try {
			connectDB(link, nameValuePairs);
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		HttpPost httppost = new HttpPost("//link.php");
		try {
			HttpResponse resp = client.execute(httppost);
			HttpEntity entity = resp.getEntity();
			String data = EntityUtils.toString(entity);
			JSONObject jObj = new JSONObject(data);
			if (jObj.toString().equals("true"))
				return true;
			else
				return false;			 
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	public ArrayList<String> retrieveAlarm(){
		JSONArray arr = null;
		ArrayList<String> alarm = null;
		final String TAG_SUCCESS = "success";
		final String ALARMTIME = "time";
		final String SENDERNAME = "name";
		final String RINGTONE_ID = "ringtone_id";
		final String MESSAGE = "message";
		
		 List<NameValuePair> params = new ArrayList<NameValuePair>();
         // getting JSON string from URL
         JSONObject json = jParser.makeHttpRequest(url_params, "GET", params);

         // Check your log cat for JSON response
         Log.d("Alarm Parameters: ", json.toString());

         try {
             // Checking for SUCCESS TAG
             int success = json.getInt(TAG_SUCCESS);
             if (success == 1) {
                 // row found
                 //products = json.getJSONArray(TAG_PRODUCTS);

		
		return alarm;
	}
	
}
