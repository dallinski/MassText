package com.dallinc.masstexter;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;

public class EditGroup extends Activity {
	private static final int REQUEST_PICK_CONTACT = 1001;
	private static final String TAG = "My tag";
	private Group group;
	String title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_group);
		
		title = getIntent().getStringExtra("title");
		TextView groupName = (TextView) findViewById(R.id.groupName);
		groupName.setText(title);
		initializeContacts();
		updateList();
	}
	
	public void initializeContacts(){
		for(int i=0; i< MainActivity.groups.size(); i++){
			Group tempGroup = MainActivity.groups.get(i);
			if(title.equals(tempGroup.name())){
				group = tempGroup;
				return;
			}
		}
	}
	
	public void updateList(){
		LinearLayout list = (LinearLayout) findViewById(R.id.groupList);
		list.removeAllViews();
		
		SharedPreferences.Editor editor = MainActivity.settings.edit();
		LayoutInflater layoutInflator = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			
		for(int i=0; i<group.size(); i++){
			View view = layoutInflator.inflate(R.layout.edit_group_list, null);
			QuickContactBadge qcb = (QuickContactBadge) view.findViewById(R.id.quickContactBadge1);
			qcb.assignContactFromPhone(group.get(i).number(), true);
			URItools uriTools = new URItools();
			uriTools.myMethod(group.get(i).number(), qcb, this);
			TextView nameView = (TextView) view.findViewById(R.id.contact_name);
			nameView.setText(group.get(i).name());
			TextView numView = (TextView) view.findViewById(R.id.contact_number);
			numView.setText(group.get(i).number());
			Button deleteButton = (Button) view.findViewById(R.id.delete_contact);
			final int pos = i;
			deleteButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					deleteContact(pos);
				}
			});
			list.addView(view);
			editor.putString("group" + title + i, title + "\t" + group.get(i).name() + "\t" + group.get(i).number());
			editor.commit();
		}
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.edit_group, menu);
//		return true;
//	}

	public void addContact(View v){		
		Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
		startActivityForResult(intent, REQUEST_PICK_CONTACT);
	}
	
	public void deleteContact(final int i){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Remove from group");
		builder.setMessage("Do you really want to remove " + group.get(i).name() + "?");
	    builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	SharedPreferences.Editor editor = MainActivity.settings.edit();
	        	editor.remove("group" + title + (group.size()-1));
	        	editor.commit();
	        	group.remove(i);
	        	updateList();
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});
		AlertDialog alert = builder.create();
	    alert.show();
	}
	
	public String[] contactsToArray(){
		String[] temp = new String[group.size()];
		for(int i=0; i<group.size(); i++){
			temp[i] = group.get(i).name();
		}
		return temp;
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(resultCode != Activity.RESULT_OK){
			Log.v("onActivityResult", "the result was not RESULT_OK");
			return;
		}
		
		Uri result = data.getData();
		Log.v(TAG, "Got a result: " + result.toString());

		// get the phone number id from the Uri
		String id = result.getLastPathSegment();

		// query the phone numbers for the selected phone number id
		Cursor c = getContentResolver().query(
		    Phone.CONTENT_URI, null,
		    Phone._ID + "=?",
		    new String[]{id}, null);

		int phoneIdx = c.getColumnIndex(Phone.NUMBER);
		int nameIdx = c.getColumnIndex(Phone.DISPLAY_NAME);

		if(c.getCount() == 1) { // contact has a single phone number
		    // get the only phone number
		    if(c.moveToFirst()) {
		        String phone = c.getString(phoneIdx);
		        String contactName = c.getString(nameIdx);
		        Log.v(TAG, "Got phone number: " + phone);

		        // do something with the phone number
		        SharedPreferences.Editor editor = MainActivity.settings.edit();
	        	editor.putString("group" + title + group.size(), title + "\t" + contactName + "\t" + phone);
	        	editor.commit();
	        	group.add(contactName, phone);
	        	updateList();

		    } else {
		        Log.w(TAG, "No results");
		    }
		}
	}
	
	
	//working code on version 1.0
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
//		
//		if (resultCode == Activity.RESULT_OK) {  
//	        Uri contactData = data.getData();  
//	        @SuppressWarnings("deprecation")
//			Cursor c =  managedQuery(contactData, null, null, null, null);  
//	        String contactId = "";
//	        String name = "";
//	        String number = "";
//	        if (c.moveToFirst()) {  
//	            name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));  
//	            contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
//	        }  
//	        if(name.length() < 1){
//	        	Toast.makeText(this, "No name listed for contact", Toast.LENGTH_SHORT).show();
//	        	return;
//	        }
//	        if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//		        Cursor c1 = getContentResolver().query(Data.CONTENT_URI, new String[] {Data._ID, Phone.NUMBER, Phone.TYPE, Phone.LABEL},
//		        	    Data.CONTACT_ID + "=?" + " AND "
//		        	    + Data.MIMETYPE + "='" + Phone.CONTENT_ITEM_TYPE + "'",
//		        	    new String[] {String.valueOf(contactId)}, null);
//		        c1.moveToFirst();
//		        number = c1.getString(1);
//		    }
//	        
//	        if(number.length() < 1){
//	        	Toast.makeText(this, "No phone number listed for contact", Toast.LENGTH_SHORT).show();
//	        	return;
//	        }
//	        else{
//	        	//SharedPreferences.Editor editor = MainActivity.settings.edit();
//	        	//editor.putString("group" + title + group.size(), title + "\t" + name + "\t" + number);
//	        	//editor.commit();
//	        	group.add(name, number);
//	        	updateList();
//	        }
//	    } 
//		else {  
//	        // gracefully handle failure  
//	        System.out.println("Warning: activity result not ok");  
//	    }  
//	} 
}
