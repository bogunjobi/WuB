package mwn.bolfab.wakeupbuddy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Groups;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class AddContacts extends ListActivity {
	final int CONTACT_PICKER_RESULT = 1000;
	String[] emptyArray = {};
	public static HashMap<String, String> hashmap = new HashMap<String, String>();
	private static List<ContactData>contactList = new ArrayList<ContactData>();
	JSONParser jParser = new JSONParser();
	 
	final String groupTitle = Contacts.groupTitle;
	boolean groupExists = false;
	String gE = "false";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contacts);
		
	TextView tv = (TextView)findViewById(R.id.tell_friend);
	tv.setOnClickListener(tvListener);
	new PopulateContactList().execute();
	/*List<ContactData> grps = null;
	try {
		grps = new PopulateContactList().execute().get();
	} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ExecutionException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	if (grps!=null){
		String[] lv_arr = checkContacts(getName(grps));
		setListAdapter(new CheckboxAdapter(AddContacts.this, R.layout.check_list, lv_arr));
	}else {
			Toast.makeText(getApplicationContext(), "GRPS is null", Toast.LENGTH_SHORT).show();
	}*/
	
	getGroupID(groupTitle);
	Log.i("Group Exists", gE);
	
	//check if a local WuB group exists. If it does, then populate from db and do nothing, else create group
	
	if (!groupExists)		
			createGroup(); //create contact group to add new users
	
	
 	
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
	
	
	//auto-populate from database	
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
		
	
	/*private List<ContactData> getContactList(){
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
    }*/
	
	public String[] getName(List<ContactData> a){
		int size = a.size();
		String [] arr = new String[size];
		for (int i=0; i < size; i++){
			arr[i] = a.get(i).getName();
			//Log.i("ARRAY", arr[i]);
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
		File file = this.getFileStreamPath(Main.FILENAME);
	    
		//if file does not exist, don't bother performing the check.
		if (!file.exists())
			return strings;
		
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

	static class ContactData {
		String phone, name;
		
		private String getName(){
			//populate the global HashMap that holds contacts names and phone number 
			hashmap.put(name,phone);
			
			return name;
		}
		
	}
	
	
	 @SuppressWarnings("unchecked")
	public boolean onOptionsItemSelected(MenuItem item) {
		    FileOutputStream fos = null; 
		    ArrayList<String> selected = CheckboxAdapter.getSelectedString();
		    switch (item.getItemId()) {		
		    case R.id.action_cancel_contacts:
		    	  selected.clear();
		    	  
		    	  setListAdapter(new CheckboxAdapter(AddContacts.this, R.layout.empty_list, emptyArray));
			      AddContacts.this.finish();
			      break;
		    case R.id.action_add_contacts:
		    	String delimiter = ";";
		    	if (selected.size() > 0){	//if there are options selected
		    		
					try {
						//mode_append: this ensures that if the file already exists, 
						//new additions are appended and the file is not overwritten
						if (new AddContactDB().execute(selected).get())
							fos = openFileOutput(Main.FILENAME, Context.MODE_APPEND); 	
						
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();					
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
		    		setListAdapter(new CheckboxAdapter(AddContacts.this, R.layout.empty_list, emptyArray));
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
	 
	 private void openContactList() {
		 Intent it = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
     	startActivityForResult(it, CONTACT_PICKER_RESULT);
	 }
	 		@Override
	 public void onActivityResult(int requestCode, int resultCode, Intent data) {
	         if (resultCode == RESULT_OK) {
	             switch (requestCode){ 
	             	case CONTACT_PICKER_RESULT:
		                 // handle contact results
	             		Log.i("Warning", "Activity result is ok!");
	             		break;
	             	default:
	             		// gracefully handle failure
	             		Log.i("Warning", "Activity result not ok");
	             		break;
		         }
		     }
	       
	 }
	 
	 /**
	  * @author Bolu
	  * @category The purpose of this asynctask is to populate with users from the database
	  * 		  This is done for testing purposes.
	  */
	 
	 class AddContactDB extends AsyncTask<ArrayList<String>, String, Boolean>{
		String url_add_buddy = "http://wubuddy.hopto.org/add_buddy";
		private static final String TAG_SUCCESS = "success";
		JSONObject json;
		boolean addSuccessful = true;
		@Override
		protected Boolean doInBackground(ArrayList<String>... args) {
		           // Building Parameters
			   ArrayList<String> argument = args[0];
		       List<NameValuePair> params = new ArrayList<NameValuePair>();
		       
		       for (int i = 0; i <argument.size(); i++){
		    	String buddy = hashmap.get(argument.get(i));
		    	if (buddy == null)
		    		continue;
	           	params.add(new BasicNameValuePair("user_phone", Main.phoneNum));
	           	params.add(new BasicNameValuePair("buddy_phone", buddy));
	           	json = jParser.makeHttpRequest(url_add_buddy,
		                    "POST", params);
	           	  Log.d("Create Response", json.toString());
	           	int success;
				try {
					success = json.getInt(TAG_SUCCESS);
					if (success != 1) {
						addSuccessful = false;
						Toast.makeText(AddContacts.this, argument.get(i)+ " not added", Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}		 
		         
		       }
	            // check for success tag
		        return addSuccessful;
		        }
		
	 }	
	
	 		
	 class PopulateContactList extends AsyncTask<String, String, String>{
		 final String url_contacts = "http://wubuddy.hopto.org/.php";
		 private ProgressDialog pDialog;
		 JSONArray users = null;
		 private static final String TAG_SUCCESS = "success";
		 private static final String TAG_NAME = "name";
		 private static final String TAG_USER = "users";
		 private static final String TAG_PHONE = "phone";
		 
		 protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(AddContacts.this);
	            pDialog.setMessage("Loading contacts. Please wait...");
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(false);
	            pDialog.show();
	        }

		 
		 @Override
		 protected String doInBackground(String... args) {
			// Building Parameters
	            List<NameValuePair> params = new ArrayList<NameValuePair>();
	            //ArrayList<ContactData> userList = new ArrayList<ContactData>();
	            
	            // getting JSON string from URL
	            
	            //url to return 10 users
	            String url_topUsers = "http://wubuddy.hopto.org/get_limited_users.php";
				JSONObject json = jParser.makeHttpRequest(url_topUsers , "GET", params);
	 
	            // Check your log cat for JSON reponse
	            Log.d("All Products: ", json.toString());
	 
	            try {
	                // Checking for SUCCESS TAG
	                int success = json.getInt(TAG_SUCCESS);
	 
	                if (success == 1) {
	                    // products found
	                    // Getting Array of users
	                    users = json.getJSONArray(TAG_USER);
	 
	                    // looping through users
	                    for (int i = 0; i < users.length(); i++) {
	                    	ContactData data = new ContactData();
	                        JSONObject c = users.getJSONObject(i);	 
	                        // Storing each json item in variable
	                        String phone = c.getString(TAG_PHONE);
	                        String name = c.getString(TAG_NAME);
	 
	                       
	                        // adding each child node to ArrayList
	                        data.name = name;
	                        data.phone = phone;
	                        
	                         // adding ContactData to ArrayList
	                        contactList.add(data);
	                        
	                    }
	                    
	                    
	                } else {
	                	
	                    //launch phone contacts 
	                	openContactList();
	                	
	                }
	            } catch (JSONException e) {
	                e.printStackTrace();
	            }
	 

			 return null;
	}
		 
		 protected void onPostExecute(String s) {
	            // dismiss the dialog after getting all products
	            pDialog.dismiss();
	            // updating UI from Background Thread
	            runOnUiThread(new Runnable() {
	                public void run() {
	                    /**
	                     * Updating parsed JSON data into ListView
	                     * */
	                	
	                	String[] lv_arr = checkContacts(getName(contactList));
	                	
	                	
	                	setListAdapter(new CheckboxAdapter(AddContacts.this, R.layout.check_list, lv_arr));
	                	
	                    
	                 
	                }
	            });
	           
	        }
	 

	 
	}
}



	
	
	
	

