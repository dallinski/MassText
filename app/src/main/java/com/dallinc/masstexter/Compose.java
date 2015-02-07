package com.dallinc.masstexter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;

import contactpicker.Contact;
import contactpicker.ContactManager;
import contactpicker.FlowLayout;


public class Compose extends ActionBarActivity {
    final int REQUEST_CODE = 100;
    boolean repeatCheck = false;
    int i = 0;
    FlowLayout chipsBoxLayout;
    ArrayList<Contact> contactsShareDetail;
    ArrayList<String> contactsSharePhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            Template template = Template.findById(Template.class, bundle.getLong("template_id"));
            Toast.makeText(getBaseContext(), "Stub: Create message (template): " + template.title, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getBaseContext(), "Stub: Create message (quick compose)", Toast.LENGTH_SHORT).show();
        }

        chipsBoxLayout = (FlowLayout)findViewById(R.id.chips_box_layout);
        contactsShareDetail = new ArrayList<Contact>();
        contactsSharePhone = new ArrayList<String>();
    }

    public void clear() {
        contactsShareDetail = new ArrayList<Contact>();
        contactsSharePhone = new ArrayList<String>();
        chipsBoxLayout.removeAllViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_select_recipients) {
            clear();
            Intent contactPicker = new Intent(getBaseContext(), ContactManager.class);
            startActivityForResult(contactPicker, REQUEST_CODE);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK) {
                if(data.hasExtra(Contact.CONTACTS_DATA)) {

                    FlowLayout.LayoutParams params = new FlowLayout.LayoutParams(FlowLayout.LayoutParams.WRAP_CONTENT, FlowLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(2, 2, 2, 2);

                    ArrayList<Contact> contacts = data.getParcelableArrayListExtra(Contact.CONTACTS_DATA);
                    if(contacts != null) {
                        i += 1;
                        if (i == 2) {
                            repeatCheck = true;
                        }

                        Iterator<Contact> iterContacts = contacts.iterator();
                        while (iterContacts.hasNext()) {
                            Contact contact = iterContacts.next();
                            if(!repeatCheck)
                            {
                                contactsShareDetail.add(contact);
                                contactsSharePhone.add(contact.getContactNumber());
                                TextView t = new TextView(getBaseContext());
                                t.setLayoutParams(params);
                                t.setTextSize(16f);
                                t.setPadding(3, 3, 3, 3);
                                t.setText(contact.getContactName());
                                t.setTextColor(Color.WHITE);
                                // t.setBackgroundColor(Color.BLUE);
                                t.setBackgroundResource(R.drawable.chips_bg);
                                chipsBoxLayout.addView(t);
                            }
                            else if(repeatCheck && !contactsSharePhone.contains(contact.getContactNumber()))
                            {
                                contactsShareDetail.add(contact);
                                contactsSharePhone.add(contact.getContactNumber());
                                TextView t = new TextView(getBaseContext());
                                t.setLayoutParams(params);
                                t.setTextSize(16f);
                                t.setPadding(3, 3, 3, 3);
                                t.setText(contact.getContactName());
                                t.setTextColor(Color.WHITE);
                                // t.setBackgroundColor(Color.BLUE);
                                t.setBackgroundResource(R.drawable.chips_bg);
                                chipsBoxLayout.addView(t);
                            }

                        }
                    }
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
