package mwn.bolfab.wakeupbuddy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;



public class Contacts extends Activity  {
	final static String groupTitle = "WakeUpBuddy";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts);
		
		boolean contacts = getIntent().getBooleanExtra("contacts", false); 
		//intent from the main page. This is true if there were contacts added previously
		boolean addedContacts = getIntent().getBooleanExtra("contactsAdded", false);
		//intent from add contacts. This is true if contacts were newly added
	if (!(contacts || addedContacts)){		
		showDialog();
		
		
	} else{
		//retrieve contacts from database
		TextView tv =  (TextView)findViewById(R.id.textView1);
		tv.setVisibility(View.GONE);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader
					(getApplicationContext().openFileInput(Main.FILENAME)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (reader!=null){
			String [] result = readContacts(reader);
			ListView lv = (ListView)findViewById(R.id.listView1);
			lv.setAdapter(new ArrayAdapter<String>(Contacts.this, 
			android.R.layout.simple_list_item_1, result));
			lv.setOnItemClickListener(listListener);
		}else{
			Contacts.this.finish();
		}
        
	} 
}


	private OnItemClickListener listListener = new OnItemClickListener(){
	 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		 String cName = (String) parent.getItemAtPosition(position);
		 Intent listIntent = new Intent(Contacts.this, SendAlarm.class);
        listIntent.putExtra("cName", cName);
        //listIntent.putExtra("cNumber", phone);
        startActivity(listIntent);

	   }
	 };
	 
	
	
	public static String [] readContacts(BufferedReader r){
		StringBuilder sb = new StringBuilder();
        try{
            
            String line = null;
            while ((line = r.readLine()) != null) {
                sb.append(line).append("\n");
            }
            r.close();
        } catch(OutOfMemoryError om){
            om.printStackTrace();
        } catch(Exception ex){
            ex.printStackTrace();
        }
        String [] result = sb.toString().split(";");
        return result;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.contacts, menu);
		return true;
	}
	
	
	
	private void showDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(Contacts.this);
		builder.setCancelable(false);
		builder.setTitle("You currently have no contacts. Would you like to interact with test users?");
		builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
		 
		  public void onClick(DialogInterface dialog, int which) {
			  Intent myIntent = new Intent(Contacts.this, AddContacts.class); //open add contacts 
			  startActivity(myIntent);
			  dialog.dismiss();
		  }
		});
		builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
		  @Override
		  public void onClick(DialogInterface dialog, int which) {
		    dialog.dismiss();
		    Contacts.this.finish(); //go to the previous activity
		  }
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	 public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    case R.id.action_add_page:
		    	startActivity(new Intent(Contacts.this, AddContacts.class));
			      break;
		   
		    default:
		      break;
		    }

		    return true;
		  }
	
	
}
