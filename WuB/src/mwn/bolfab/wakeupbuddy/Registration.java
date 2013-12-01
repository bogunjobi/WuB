package mwn.bolfab.wakeupbuddy;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Registration extends Activity {
	static FileOutputStream fos = null;
	static final String CONTACTINFO = "selfInfo";
	
	EditText username;
    EditText phone;
	String userName = null;
	//String phoneNum = null;
	
	JSONParser jsonParser = new JSONParser();
	private static String url_registration = "http://wubuddy.hopto.org/create_user.php";
	public static final String TAG_SUCCESS = "success";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration);
		SharedPreferences pref = getSharedPreferences("ActivityPREF", Context.MODE_PRIVATE);
	    if(pref.getBoolean("activity_executed", false)){
	        Intent intent = new Intent(this, Main.class);
	        startActivity(intent);
	        finish();
	    } else {
	        Editor ed = pref.edit();
	        ed.putBoolean("activity_executed", true);
	        ed.commit();
	              
	        //store user info
	        Button bn = (Button)findViewById(R.id.button1);
	        username = (EditText)findViewById(R.id.username);
	        phone = (EditText)findViewById(R.id.phone);
	        phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
	
	        bn.setOnClickListener(new View.OnClickListener() {			
			@Override
				public void onClick(View v) {
				//start an async task to create user entry
				new RegisterNewUser().execute();
				}			
			
	        });
	    }
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.registration, menu);
		return true;
	}
	
	 ProgressDialog pDialog;
	class RegisterNewUser extends AsyncTask<String, String, String>{
		
		 protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(Registration.this);
	            pDialog.setMessage("Registering User..");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(true);
	            pDialog.show();
	        }
	 
		@Override
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			
			
	        			
	        String userName = username.getText().toString();
	        String phoneNum = phone.getText().toString();
			Log.d("what it is", userName + phoneNum);
            params.add(new BasicNameValuePair("name", userName));
            params.add(new BasicNameValuePair("phone", phoneNum));            
           
            JSONObject json = jsonParser.makeHttpRequest(url_registration,
                    "POST", params);
            Log.d("Create Response", json.toString());
            
            // check for success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // successfully registered user
                	SharedPreferences preferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                	SharedPreferences.Editor editor = preferences.edit();
                	editor.putString("Name",userName);
                	editor.putString("Phone", phoneNum);
                	editor.commit();
                	Intent i = new Intent(getApplicationContext(), Main.class);
                    startActivity(i);
 
                    // closing this screen
                    finish();
                } else {
                    // failed to create user
                	Toast.makeText(Registration.this, "Wahala Dey", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
			return null;
		}
		
		protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
        }

		
	}

}
