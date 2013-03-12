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

public class MainActivity extends FragmentActivity {
	
	static SharedPreferences settings;
	ArrayList<Template> messages;
	static ArrayList<Group> groups;
	Spinner groups_spinner;
	Spinner messages_spinner;
	ArrayAdapter<String> groups_adapter;
	ArrayAdapter<String> messages_adapter;
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
	}
	
	public String[] messageToArray(ArrayList<Template> templates){
		String[] temp = new String[templates.size()];
		for(int i=0; i<templates.size(); i++){
			temp[i] = templates.get(i).name();
		}
		return temp;
	}
	
	public String[] groupToArray(ArrayList<Group> g){
		String[] temp = new String[g.size()];
		for(int i=0; i<g.size(); i++){
			temp[i] = g.get(i).name();
		}
		return temp;
	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.activity_main, menu);
//		return true;
//	}
	
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
	
	public void addMessage(View v){
		addMessage(v, "", "");
	}
	public void addMessage(final View v, String title, String body){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("New Message Template");
	    
	    LinearLayout linLayout= new LinearLayout(this);
	    linLayout.setOrientation(1); //1 is for vertical orientation
	    final EditText inputTitle = new EditText(this); 
	    final EditText inputBody = new EditText(this);
	    inputTitle.setSingleLine();
	    inputTitle.setHint("Message Name");
	    inputTitle.setText(title);
	    inputBody.setHint("Message Body");
	    inputBody.setText(body);
	    linLayout.addView(inputTitle);
	    linLayout.addView(inputBody);
	    builder.setView(linLayout);
		
	    builder.setPositiveButton("Preview", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	if(inputTitle.getText().toString().length() < 1){
		    		addMessage(v,inputTitle.getText().toString(),inputBody.getText().toString());
		    		Toast.makeText(getApplicationContext(), "Input a name", Toast.LENGTH_SHORT).show();
		    	}
		    	else if(inputBody.getText().toString().length() < 1){
		    		addMessage(v,inputTitle.getText().toString(),inputBody.getText().toString());
		    		Toast.makeText(getApplicationContext(), "Input a message", Toast.LENGTH_SHORT).show();
		    	}
		    	else{
		    		templatePreview(v, inputTitle.getText().toString(),inputBody.getText().toString(), new Template("-1","-1"));
		    	}
		    }
		});
	    builder.setNeutralButton("Insert Variable", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	insertVariable(v, inputTitle.getText().toString(), inputBody.getText().toString(), new Template("-1","-1"), inputBody);
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
	
	public void insertVariable(final View v, final String title, final String body, final Template existingTemplate, final EditText myEditText){
		final boolean edit = !existingTemplate.name().equals("-1");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Insert Variable");
	    
	    final String[] options = new String[]{"date", "time", "first name", "full name"};
	    builder.setItems(options, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {
	        	if(edit){
	        		String textToInsert = "%" + options[item] + "%";
		        	int start = myEditText.getSelectionStart();
		        	int end = myEditText.getSelectionEnd();
		        	if(start == -1)
		        		start = 0;
		        	if(end == -1)
		        		end = 0;
		        	String withVar = myEditText.getText().replace(Math.min(start, end), Math.max(start, end), textToInsert, 0, textToInsert.length()).toString();
		        	editMessageDialog(v, title, withVar, existingTemplate);
		       	}
	        	else
	        		addMessage(v, title, body + "%" + options[item] + "%");
	        }
	    });
	    final EditText inputTitle = new EditText(this);
	    inputTitle.setSingleLine();
	    inputTitle.setHint("custom variable");
	    builder.setView(inputTitle);
		
	    builder.setPositiveButton("Insert", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {		    	
		    	if(edit){
		    		String textToInsert = "%" + inputTitle.getText().toString() + "%";
		        	int start = myEditText.getSelectionStart();
		        	int end = myEditText.getSelectionEnd();
		        	if(start == -1)
		        		start = 0;
		        	if(end == -1)
		        		end = 0;
		        	String withVar = myEditText.getText().replace(Math.min(start, end), Math.max(start, end), textToInsert, 0, textToInsert.length()).toString();
		        	editMessageDialog(v, title, withVar, existingTemplate);
		    	}
	        	else
	        		addMessage(v, title, body + "%" + inputTitle.getText().toString() + "%");
		    }
		});
	    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	if(edit)
	        		editMessageDialog(v, title, body, existingTemplate);
	        	else
	        		addMessage(v, title, body);
		    }
		});
	    AlertDialog alert = builder.create();	    
	    alert.show();
	}
	
	public void templatePreview(final View v, final String title, final String body, final Template existingTemplate) {
		final boolean edit = !existingTemplate.name().equals("-1");
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Template Preview (" + title + ")");
		
		TextView tv = new TextView(getApplicationContext());
		tv.setPadding(5, 0, 0, 5);
		String toDisplay = body.replaceAll("`fullName", "\"full name\"");
		toDisplay = toDisplay.replaceAll("`firstName", "\"first name\"");
		toDisplay = toDisplay.replaceAll("%", "\"");
		tv.setText(toDisplay);
		builder.setView(tv);

		// Set up the buttons
		builder.setPositiveButton(edit ? "Finish editing" : "Create", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	Template toAdd = new Template(title, body);
		    	if(edit){
		    		int removeindex = messages.indexOf(existingTemplate);
		    		messages.remove(removeindex);
		    		messages.add(removeindex, toAdd);
		    	}
		    	else{
		    		messages_adapter.add(title);
		    		messages.add(toAdd);
		    		SharedPreferences.Editor editor = settings.edit();
		    		editor.putString("message" + title, title + "\t" + body);
		    		editor.commit();
		    	}
		    }
		});
		builder.setNeutralButton("Edit", new DialogInterface.OnClickListener() { 
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	if(edit)
		    		editMessageDialog(v, title, body, existingTemplate);
		    	else
		    		addMessage(v, title, body);
		    }
		});
		builder.setNegativeButton(edit ? "Cancel" : "Trash", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		        dialog.cancel();
		    }
		});

		builder.show();
	}
	
	public void deleteMessage(View v){
		if(messages.size() < 1){
			Toast.makeText(this, "No messages to delete", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String[] options = messageToArray(messages);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Delete Message");
	    builder.setItems(options, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {
	        	SharedPreferences.Editor editor = settings.edit();
	        	editor.remove("message" + messages.get(item).name());
	        	editor.commit();
	        	messages_adapter.remove(messages.get(item).name());
	        	messages.remove(item);
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

	public void editMessage(final View v){
		if(messages.size() < 1){
			Toast.makeText(this, "No messages to edit", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String[] options = messageToArray(messages);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Edit Message");
	    builder.setItems(options, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {
	        	Template t = messages.get(item);
	        	editMessageDialog(v, t.name(), t.message(), t);
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
	
	public void editMessageDialog(final View v, final String title, final String body, final Template template){

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Edit Message Template");
	    
	    LinearLayout linLayout= new LinearLayout(this);
	    linLayout.setOrientation(1); //1 is for vertical orientation
	    final EditText inputTitle = new EditText(this); 
	    final EditText inputBody = new EditText(this);
	    inputTitle.setSingleLine();
	    inputTitle.setHint("Message Name");
	    inputTitle.setText(title);
	    inputBody.setHint("Message Body");
	    inputBody.setText(body);
	    linLayout.addView(inputTitle);
	    linLayout.addView(inputBody);
	    builder.setView(linLayout);
		
	    builder.setPositiveButton("Preview", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	if(inputTitle.getText().toString().length() < 1){
		    		editMessageDialog(v, inputTitle.getText().toString(), inputBody.getText().toString(), template);
		    		Toast.makeText(getApplicationContext(), "Input a name", Toast.LENGTH_SHORT).show();
		    	}
		    	else if(inputBody.getText().toString().length() < 1){
		    		editMessageDialog(v, inputTitle.getText().toString(), inputBody.getText().toString(), template);
		    		Toast.makeText(getApplicationContext(), "Input a message", Toast.LENGTH_SHORT).show();
		    	}
		    	else{
		    		templatePreview(v, inputTitle.getText().toString(),inputBody.getText().toString(), template);
		    	}
		    }
		});
	    builder.setNeutralButton("Insert Variable", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	insertVariable(v, inputTitle.getText().toString(), inputBody.getText().toString(), template, inputBody);
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
	
	public void addGroup(final View v){ 
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("New Group");
	    final EditText inputTitle = new EditText(this); 
	    inputTitle.setSingleLine();
	    inputTitle.setHint("Group name");
	    builder.setView(inputTitle);
		
	    builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	if(inputTitle.getText().toString().length() < 1){
		    		addGroup(v);
		    		Toast.makeText(getApplicationContext(), "Input a group name", Toast.LENGTH_SHORT).show();
		    		return;
		    	}
		    	Group newGroup = new Group(inputTitle.getText().toString());
		    	groups.add(newGroup);
		    	groups_adapter.add(inputTitle.getText().toString());
		    	Intent i = new Intent(getApplicationContext(), EditGroup.class);
		    	i.putExtra("title", inputTitle.getText().toString());
				startActivity(i);
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
	
	public void deleteGroup(View v){
		if(groups.size() < 1){
			Toast.makeText(this, "No groups to delete", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String[] options = groupToArray(groups);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Delete Group");
	    builder.setItems(options, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {
	        	SharedPreferences.Editor editor = settings.edit();
	        	for(int i=0; i<groups.get(item).size(); i++){
	        		editor.remove("group" + groups.get(item).name() + i);
	        		editor.commit();
	        	}
	        	groups_adapter.remove(groups.get(item).name());
	        	groups.remove(item);
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

	public void editGroup(View v){
		if(groups.size() < 1){
			Toast.makeText(this, "No groups to edit", Toast.LENGTH_SHORT).show();
			return;
		}
		
		String[] options = groupToArray(groups);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Edit Group");
	    builder.setItems(options, new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int item) {
	        	Group temp = groups.get(item);
	        	Intent i = new Intent(getApplicationContext(), EditGroup.class);
		    	i.putExtra("title", temp.name());
				startActivity(i);
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

}
