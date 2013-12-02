package mwn.bolfab.wakeupbuddy;

import java.io.File;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Main extends Activity {
	
	// UI elements
	Button table[][] = new Button[2][2];
	TextView tv = null;
	LinearLayout layout = null;
	
	// Fixed texts
	public static final String FILENAME = "WUB_Contacts";
	public static String phoneNum;
	static final String TITLE = "Share Alarm Note";
	static final String SHARE_TEXT = "To view this message, check out this awesome app "
			+ "on the play store: " + Settings.LINK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState); 
		setContentView(R.layout.main);
		SharedPreferences prefs = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
		String userInfo[] = {prefs.getString("Name", null), prefs.getString("Phone", null)};
		phoneNum = userInfo[1];
		
		
		//start service
		while  (phoneNum == null); //wait until phone Number is populated
		Intent i = new Intent(Main.this, PollDatabase.class);
		Calendar cal = Calendar.getInstance();
		PendingIntent pintent = PendingIntent.getService(Main.this, 0, i, 0);
		AlarmManager alarm = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		// Start every 60 seconds
		alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 60*1000, pintent); 
		i.putExtra("Phone", phoneNum);
		startService(i);
	
		
		// find UI elements defined in xml
		layout = (LinearLayout) findViewById(R.id.LinearLayout1);
		table[0][0] = (Button) this.findViewById(R.id.b00);
		table[0][1] = (Button) this.findViewById(R.id.b01);
		table[1][0] = (Button) this.findViewById(R.id.b10);
		table[1][1] = (Button) this.findViewById(R.id.b11);
		
		// TODO: assign OnClickListeners to table buttons
		table[0][0].setOnClickListener(myhandler);
		table[0][1].setOnClickListener(myhandler);
		table[1][0].setOnClickListener(myhandler);
		table[1][1].setOnClickListener(myhandler);
		
		TextView tv = (TextView)findViewById(R.id.tv_welcome);
		tv.setText("Welcome, " + userInfo[0] +"!");

	}

	// TODO: assign actions for listeners when activated
 private OnClickListener myhandler = new OnClickListener() {			
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.b00:
				Intent i = new Intent(Main.this, Contacts.class);
				i.putExtra("contacts", fileExistence(FILENAME));
				startActivity(i);
				break;
			case R.id.b01:
				Intent its = new Intent(Main.this, SettingsActivity.class);
				startActivity(its);
				break;
			case R.id.b10:
				Intent intent = new Intent(Main.this, AddContacts.class);
				startActivity(intent);
				break;
			case R.id.b11:
				Intent it = new Intent(Main.this, Help.class);
				startActivity(it);
				break;
			}
		}
	};
	
	//this file checks if any contacts have been added previously
	public boolean fileExistence(String fname){
	    File file = Main.this.getFileStreamPath(fname);
	    //file.delete();
	    return file.exists();
	}
	@Override
	protected void onResume() {
		super.onResume();
		if (Settings.can_play == Settings.PLAY_NOW) {
			Intent i = new Intent(this, SplashScreen.class);
			startActivity(i);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case R.id.rate:
			Uri address = Uri.parse(Settings.LINK);
			startActivity(new Intent(Intent.ACTION_VIEW, address));
			break;
		case R.id.settings:
			startActivity(new Intent(this, Settings.class));
			return true;
		case R.id.about:
			startActivity(new Intent(this, About.class));
			return true;
		case R.id.help:
			startActivity(new Intent(this, Help.class));
			return true;
		case R.id.share:
			share(TITLE, SHARE_TEXT);
			return true;
		}
		return false;
	}

	public void share(String subject, String text) {
		final Intent share_intent = new Intent(Intent.ACTION_SEND);
		share_intent.setType("text/plain");
		share_intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		share_intent.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(Intent.createChooser(share_intent, "Share With"));
	}

/*	public String [] userInfo(){
		//Context c = Main.this;
		BufferedReader reader = null;
		String [] contact = null;
		try {
			reader = new BufferedReader(new InputStreamReader
					(getApplicationContext().openFileInput(Registration.CONTACTINFO)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		if (reader != null){
			contact = RWFile.readContacts(reader);
			return contact; //return phone number
	} 
		return null;
	}*/
}
