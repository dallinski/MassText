package com.dallinc.masstexter;

import java.text.*;
import java.util.*;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.annotation.SuppressLint;
import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;

public class MainActivity extends FragmentActivity {
	
	static SharedPreferences settings;
	static ArrayList<Template> messages;
	static ArrayList<Group> groups;
	Spinner groups_spinner;
	Spinner messages_spinner;
	static ArrayAdapter<String> groups_adapter;
	static ArrayAdapter<String> messages_adapter;
	String tempMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		settings = getPreferences(MODE_PRIVATE);
		
		messages = new ArrayList<Template>();
		groups = new ArrayList<Group>();
		initializeSpinners();
		parseStoredData();
		
		ChangeLog cl = new ChangeLog(this);
	    if (cl.firstRun())
	        cl.getLogDialog().show();
	}
	
	public void parseStoredData(){
		Map<String, ?> allSettings = settings.getAll();
		Collection<?> ar = allSettings.values();
		
		for(Object o: ar){
			String s = (String) o;
			String[] values = s.split("\t");
			if(values.length == 2){
				//this is a message
				String messageTitle = values[0];
				String messageBody = values[1];
				Template tempTemplate = new Template(messageTitle, messageBody);
				messages.add(tempTemplate);
				messages_adapter.add(messageTitle);
			}
			else if(values.length == 3){
				//this is a contact in a group
				String groupName = values[0];
				String contactName = values[1];
				String contactNumber = values[2];
				boolean alreadyStored = false;
				for(int i=0; i<groups.size(); i++){
					if(groups.get(i).name().equals(groupName)){
						alreadyStored = true;
						groups.get(i).add(contactName, contactNumber);
					}
				}
				if(!alreadyStored){
					Group tempGroup = new Group(groupName);
					tempGroup.add(contactName, contactNumber);
					groups.add(tempGroup);
					groups_adapter.add(groupName);
				}
			}
			else{
				System.out.println("found a bad stored preference");
			}
		}
		if(messages.size() > 0){
			setPreviewText(messages.get(0).message());
		}
	}
	
	public void initializeSpinners(){
		groups_spinner = (Spinner) findViewById(R.id.groups_spinner);
		groups_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		String[] names = groupToArray(groups);
		for(int i=0; i<names.length; i++){
			groups_adapter.add(names[i]);
		}
		groups_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		groups_spinner.setAdapter(groups_adapter);
		
		messages_spinner = (Spinner) findViewById(R.id.messages_spinner);
		messages_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		String[] messagenames = messageToArray(messages);
		for(int i=0; i<messagenames.length; i++){
			messages_adapter.add(messagenames[i]);
		}
		messages_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		messages_spinner.setAdapter(messages_adapter);
		
		messages_spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		        setPreviewText(messages.get(position).message());
		    }

		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    	setPreviewText("");
		    }

		});
	}
	
	public static String[] messageToArray(ArrayList<Template> templates){
		String[] temp = new String[templates.size()];
		for(int i=0; i<templates.size(); i++){
			temp[i] = templates.get(i).name();
		}
		return temp;
	}
	
	public static String[] groupToArray(ArrayList<Group> g){
		String[] temp = new String[g.size()];
		for(int i=0; i<g.size(); i++){
			temp[i] = g.get(i).name();
		}
		return temp;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getTitle().toString().equals("Change Log")){
			ChangeLog cl = new ChangeLog(this);
			cl.getFullLogDialog().show();
		}
		else{
			
		}
		return true;
	}
	
	@SuppressLint("ValidFragment")
	public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			setRetainInstance(true);
			final Calendar c = Calendar.getInstance();
	        int hour = c.get(Calendar.HOUR_OF_DAY);
	        int minute = c.get(Calendar.MINUTE);

	        // Create a new instance of TimePickerDialog and return it
	        return new TimePickerDialog(getActivity(), this, hour, minute,
	                DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			String time = (hourOfDay > 12) ? "" + (hourOfDay-12): "" + hourOfDay;
			time += ":";
			time += (minute < 10) ? "0" + minute : minute;
			time += (hourOfDay > 11) ? " PM" : " AM";
			tempMessage = tempMessage.replaceFirst("%time%", time);
			if(!tempMessage.contains("%"))
	    		sendMessageDialog(view);
		}
	}
	
	public void showTimePickerDialog(View v) {
	    DialogFragment newFragment = new TimePickerFragment();
	    newFragment.show(getSupportFragmentManager(), "timePicker");
	}
	
	@SuppressLint({ "ValidFragment", "SimpleDateFormat" })
	public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			
			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			String tempDate = "";
			SimpleDateFormat sdf = new SimpleDateFormat("MM d, yyyy");
			try {
				Date d = sdf.parse("" + (month+1) + " " + day + ", " + year);
				SimpleDateFormat sdfout = new SimpleDateFormat("MMM d, yyyy");
				tempDate = sdfout.format(d);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			tempMessage = tempMessage.replaceFirst("%date%", tempDate);
			if(!tempMessage.contains("%"))
	    		sendMessageDialog(view);
		}
	}
	
	public void showDatePickerDialog(View v) {
	    DialogFragment newFragment = new DatePickerFragment();
	    newFragment.show(getSupportFragmentManager(), "datePicker");
	}
	
	
	public void fillInTheBlanks(View v){
		if(messages.size() < 1){
			Toast.makeText(getApplicationContext(), "Please create a message!", Toast.LENGTH_SHORT).show();
			return;
		}
		else if(groups.size() < 1){
			Toast.makeText(getApplicationContext(), "Please create a group!", Toast.LENGTH_SHORT).show();
			return;
		}
		tempMessage = messages.get(messages_spinner.getSelectedItemPosition()).message();
		ArrayList<String> blanks = new ArrayList<String>();
		String variableName = "";
		boolean isVariable = false;
		for(int i=0; i<tempMessage.length(); i++){
			if(isVariable)
				variableName += tempMessage.charAt(i);
			if(tempMessage.charAt(i) == '%'){
				isVariable = !isVariable;
				if(!isVariable){
					variableName = variableName.substring(0, variableName.length()-1);
					blanks.add(variableName);
					variableName = "";
				}
			}
		}
		for(String var: blanks){
			inputField(v, var);
		}
		if(!tempMessage.contains("%"))
    		sendMessageDialog(v);
	}
	
	public void inputField(View v, String variableName){
		if(variableName.equals("time")){
			showTimePickerDialog(v);
		}
		else if(variableName.equals("date")){
			showDatePickerDialog(v);
		}
		else if(variableName.equals("first name")){
			tempMessage = tempMessage.replaceAll("%first name%", "`firstName");
		}
		else if(variableName.equals("full name")){
			tempMessage = tempMessage.replaceAll("%full name%", "`fullName");
		}
		else{
			variablePickerDialog(v, variableName);
		}
	}
	
	public void variablePickerDialog(final View v, final String variableName) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		String properName = Character.toUpperCase(variableName.charAt(0)) + 
						variableName.substring(1, variableName.length());
		builder.setTitle(properName);

		final EditText input = new EditText(this);
		// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
		input.setInputType(InputType.TYPE_CLASS_TEXT);
		input.setHint("some " + variableName);
		//input.requestFocus();******************************************************************
		builder.setView(input);

		builder.setPositiveButton("Set", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	tempMessage = tempMessage.replaceFirst("%"+variableName+"%", input.getText().toString());
		    	if(!tempMessage.contains("%"))
		    		sendMessageDialog(v);
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		        tempMessage = "";
		    }
		});

		builder.show();
	}
	
	public void sendMessageDialog(final View v) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Message Preview");
		
		TextView tv = new TextView(getApplicationContext());
		String toDisplay = tempMessage.replaceAll("`fullName", "\"full name\"");
		toDisplay = toDisplay.replaceAll("`firstName", "\"first name\"");
		tv.setText(toDisplay);
		builder.setView(tv);

		// Set up the buttons
		builder.setPositiveButton("Send", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	sendMessage();
		    }
		});
		builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});

		builder.show();
	}
	
	public void sendMessage(){
		Group tempGroup = groups.get(groups_spinner.getSelectedItemPosition());
		for(int i=0; i<tempGroup.size(); i++){
			String phoneNum = tempGroup.numberAt(i);
			String messageToSend = insertPersonalName(phoneNum, tempMessage);
			
			SmsManager smsmanage = SmsManager.getDefault();
			
			if(messageToSend.length() > 160){
				ArrayList<String> parts = smsmanage.divideMessage(messageToSend);
				ArrayList<PendingIntent> sentIntents = new ArrayList<PendingIntent>();
				for(int j=0; j<parts.size(); j++){
					sentIntents.add(j, PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0));
				}
				smsmanage.sendMultipartTextMessage(phoneNum, null, parts, sentIntents, null);
			}
			else{
				smsmanage.sendTextMessage(phoneNum, null, messageToSend, null, null);
			}
			
			ContentValues values = new ContentValues();
			values.put("address", phoneNum);
			values.put("body", messageToSend);
			getContentResolver().insert(Uri.parse("content://sms/sent"), values);
			
			Toast.makeText(getApplicationContext(), "Successfully sent message!", Toast.LENGTH_LONG).show();
		}
	}
	
	public String insertPersonalName(String phoneNumber, String messageToSend){
		String temp = messageToSend;
		String fullname = nameFromNumber(phoneNumber);
		String firstname = "";
		String[] nameparts = fullname.split(" ");
		if(nameparts.length > 0)
			firstname = nameparts[0];
		temp = temp.replaceAll("`fullName", fullname);
		temp = temp.replaceAll("`firstName", firstname);
		return temp;
	}
	
	public String nameFromNumber(String phoneNumber){
		// define the columns I want the query to return
		String[] projection = new String[] {
		        ContactsContract.PhoneLookup.DISPLAY_NAME,
		        ContactsContract.PhoneLookup._ID};

		// encode the phone number and build the filter URI
		Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

		// query time
		Cursor cursor = getApplicationContext().getContentResolver().query(contactUri, projection, null, null, null);

		if (cursor.moveToFirst())
		    return cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
		else
			return "noname noname";
	}
	
	public void goToGroupManagement(View v){
		Intent i = new Intent(getApplicationContext(), GroupManage.class);
		startActivity(i);
	}
	
	public void goToMessageManagement(View v){
		Intent i = new Intent(getApplicationContext(), MessageManage.class);
		startActivity(i);
	}
	
	public static int indexOfTemplate(String templateName){
		for(int i=0; i<messages.size(); i++){
			if(messages.get(i).name().equalsIgnoreCase(templateName))
				return i;
		}
		return -1;
	}
	
	public static int indexOfGroup(String name){
		for(int i=0; i<groups.size(); i++){
			if(groups.get(i).name().equalsIgnoreCase(name))
				return i;
		}
		return -1;
	}
	
	public void setPreviewText(String s){
		TextView preview = (TextView) findViewById(R.id.preview_message);
		preview.setBackgroundResource(R.drawable.edittext_rounded_corners);
		
		String toDisplay = s.replaceAll("[%*%]", "\"");
		
		preview.setText(toDisplay);
	}

}
