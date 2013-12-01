package mwn.bolfab.wakeupbuddy;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.ListActivity;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Groups;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class AddContacts extends ListActivity {
	//public static int contactCount = 0;	//this maintains a count of all the contacts added by the user. Should be obtained dynamically in the onCreate() of the main function.

	
	final String groupTitle = "WakeUpBuddy";
	boolean groupExists = false;
	String gE = "false";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contacts);
		
	TextView tv = (TextView)findViewById(R.id.tell_friend);
	tv.setOnClickListener(tvListener);
	getGroupID(groupTitle);
	Log.i("Group Exists", gE);
	
	if (groupExists){		
		List<ContactData> grps = getContactList();
		String[] lv_arr = checkContacts(getName(grps));
		setListAdapter(new CheckboxAdapter(AddContacts.this, R.layout.check_list, lv_arr));
		
	} else {
		createGroup();
	}
}

	private OnClickListener tvListener = new OnClickListener() {			
		public void onClick(View v) {
			share(Main.TITLE, Main.SHARE_TEXT);
		}
	};
	
	
	public void share(String subject, String text) {
		final Intent share_intent = new Intent(Intent.ACTION_SEND);
		share_intent.setType("text/plain");
		share_intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		share_intent.putExtra(Intent.EXTRA_TEXT, text);
		startActivity(Intent.createChooser(share_intent, "Share With"));
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.add_contacts, menu);
		return true;
	}
	
	private String getGroupID(String groupTitle){
		
		Uri uri = Groups.CONTENT_URI;
	    String where = String.format("%s = ?", Groups.TITLE);
	    String[] whereParams = new String[]{groupTitle};
	    String[] selectColumns = {Groups._ID};
	    Cursor c = getContentResolver().query(
	            uri, 
	            selectColumns,
	            where, 
	            whereParams, 
	            null);

	    try{
	        if (c.moveToFirst()){
	        	groupExists = true;
	        	gE = "true";
	            return c.getString(0);  
	        }
	     return null;
	    }finally{
	        c.close();
	    }
	}
	
		
	private void createGroup(){
		groupExists = false;
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		ops.add(ContentProviderOperation
        		 .newInsert(ContactsContract.Groups.CONTENT_URI)
                 .withValue(ContactsContract.Groups.TITLE, groupTitle)
                 .withValue(ContactsContract.Groups.GROUP_VISIBLE, true)
                 .withValue(ContactsContract.Groups.ACCOUNT_NAME, groupTitle)
                 .withValue(ContactsContract.Groups.ACCOUNT_TYPE, groupTitle)
                 .build());
        
		try {

				getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

		    } catch (Exception e) {
		        Log.e("Error", e.toString());
		    }

		}
		/*ContentValues groupValues = new ContentValues();
	    ContentResolver cr = this.getContentResolver();
	    groupValues.put(ContactsContract.Groups.TITLE, groupTitle);
	    groupValues.put(ContactsContract.Groups.GROUP_VISIBLE, 1);
	    cr.insert(ContactsContract.Groups.CONTENT_URI, groupValues);*/
	
	
	private List<ContactData> getContactList(){
		List<ContactData>contactList = new ArrayList<ContactData>();

		String groupID = getGroupID(groupTitle);
		Uri groupURI = ContactsContract.Data.CONTENT_URI;
	    String[] projection = new String[]{
	     ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID,
	     ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
	     };
	    Log.d("groupID", ""+groupID);
	    Cursor c = getContentResolver().query(
                groupURI,
                projection,
                ContactsContract.CommonDataKinds.GroupMembership.GROUP_ROW_ID
                        + "=?", new String[]{groupID}, null);

        while (c.moveToNext()) {
            String id = c.getString(c.getColumnIndex
            		(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID));
            Cursor pCur = getContentResolver().query
            		(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[] {id}, null);

            while (pCur.moveToNext()) {
                ContactData data = new ContactData();
                data.name = pCur.getString(pCur.getColumnIndex
                		(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                data.phone = pCur.getString(pCur
                                .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contactList.add(data);
                groupExists = true;
            }

            pCur.close();

        }
        return contactList;
    }
	
	public String[] getName(List<ContactData> a){
		int size = a.size();
		String [] arr = new String[size];
		for (int i=0; i < size; i++){
			arr[i] = a.get(i).getName();
		}
		
		return arr;
	}
	
	/**
	 * 
	 * @author Bolu
	 * @category This functions checks if the contacts to be displayed have already been added.
	 * Very redundant code, but necessary to avoid dupes
	 * @return String [] that does not contain already added contacts
	 * 
	 * 
	 */
	
	String [] checkContacts(String [] strings) {
		//first retrieve stored contacts
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader
					(getApplicationContext().openFileInput(Main.FILENAME)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (reader != null){
			String [] contacts = RWFile.readContacts(reader);
			ArrayList<String> al_contacts = new ArrayList<String>(Arrays.asList(contacts));			
			ArrayList<String> al_strings = new ArrayList<String>(Arrays.asList(strings));
			al_strings.removeAll(al_contacts);
			String [] arr = al_strings.toArray(new String[al_strings.size()]);;
			return arr; 
		}
		//if we're unable to read the added contacts, just use the list of contacts as is
		return strings;
		
	}

	class ContactData {
		String phone, name;
		
		private String getName(){
			return name;
		}
	
	}
	
	
	 public boolean onOptionsItemSelected(MenuItem item) {
		    FileOutputStream fos = null; 
		    ArrayList<String> selected = CheckboxAdapter.getSelectedString();
		    switch (item.getItemId()) {		
		    case R.id.action_cancel_contacts:
		    	  selected.clear();
			      AddContacts.this.finish();
			      break;
		    case R.id.action_add_contacts:
		    	//ArrayList<String> selected = CheckboxAdapter.getSelectedString();
		    	String delimiter = ";";
		    	if (selected.size() > 0){	//if there are options selected
		    		
					try {
						//mode_append: this ensures that if the file already exists, 
						//new additions are appended and the file is not overwritten
						fos = openFileOutput(Main.FILENAME, Context.MODE_APPEND); 	
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
		    	}
				else{//if no options are selected, display a toast
					Toast.makeText(this, "No contacts selected", Toast.LENGTH_SHORT)
			          .show();
					break;
				}
		    		
		    	for (int i=0; i<selected.size();i++){
		    		try {
		    			fos.write(selected.get(i).getBytes());  //write the selected names into a file
		    			fos.write(delimiter.getBytes());
		    		} catch (IOException e) {
					// TODO Auto-generated catch block
		    			Log.e("Exception", e.toString());
		    		}
		    		
		    		Intent intent = new Intent(AddContacts.this, Contacts.class);
		    		intent.putExtra("contactsAdded", true);
		    		startActivity(intent); //open add contacts 
		    	}
		    	selected.clear();
		      break;
		    default:
		    	break;
		   }
		    try {
		    	if (fos != null)
		    		fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    return true;
		  } 
	}



	
	
	
	

