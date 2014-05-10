package com.dallinc.masstexter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import com.dallinc.masstexter.CustomContact;
import com.dallinc.masstexter.Group;
import com.dallinc.masstexter.MainActivity;
import com.dallinc.masstexter.URItools;
import java.util.ArrayList;

public class EditGroup extends Activity {
	private static final int REQUEST_PICK_CONTACT = 1001;
	private static final String TAG = "My tag";
	private Group group;
	String title;

    public void addContact(View view) {
        this.startActivityForResult(new Intent("android.intent.action.PICK", ContactsContract.CommonDataKinds.Phone.CONTENT_URI), 1001);
    }

    public String[] contactsToArray() {
        String[] arrstring = new String[this.group.size()];
        int n = 0;
        while (n < this.group.size()) {
            arrstring[n] = this.group.get(n).name();
            ++n;
        }
        return arrstring;
    }

    public void deleteContact(int n) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context)(this));
        builder.setTitle((CharSequence)("Remove from group"));
        builder.setMessage((CharSequence)(("Do you really want to remove " + this.group.get(n).name() + "?")));
        builder.setPositiveButton((CharSequence)("Remove"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface2, int dialogInterface2) {
                SharedPreferences.Editor editor = MainActivity.settings.edit();
                editor.remove(("group" + EditGroup.this.title + -1 + EditGroup.this.group.size()));
                editor.commit();
                EditGroup.this.group.remove(n);
                EditGroup.this.updateList();
            }
        }));
        builder.setNegativeButton((CharSequence)("Cancel"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface, int n) {
                dialogInterface.cancel();
            }
        }));
        builder.create().show();
    }

    public void initializeContacts() {
        int n = 0;
        do {
            Group group;
            if (n >= MainActivity.groups.size()) {
                this.group = new Group("New Group");
                return;
            }
            if (this.title.equals((Object)((group = (Group)(MainActivity.groups.get(n))).name()))) {
                this.group = group;
                return;
            }
            ++n;
        } while (true);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     */
    protected void onActivityResult(int n, int n2, Intent intent) {
        if (n2 != -1) {
            Log.v((String)("onActivityResult"), (String)("the result was not RESULT_OK"));
            return;
        }
        Uri uri = intent.getData();
        Log.v((String)("My tag"), (String)(("Got a result: " + uri.toString())));
        String string = uri.getLastPathSegment();
        Cursor cursor = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, (String[])(null), "_id=?", new String[]{string}, (String)(null));
        int n3 = cursor.getColumnIndex("data1");
        int n4 = cursor.getColumnIndex("display_name");
        if (cursor.getCount() != 1) return;
        if (cursor.moveToFirst()) {
            String string2 = cursor.getString(n3);
            String string3 = cursor.getString(n4);
            Log.v((String)("My tag"), (String)(("Got phone number: " + string2)));
            SharedPreferences.Editor editor = MainActivity.settings.edit();
            editor.putString(("group" + this.title + this.group.size()), (String.valueOf((Object)(this.title)) + "\t" + string3 + "\t" + string2));
            editor.commit();
            this.group.add(string3, string2);
            this.updateList();
            return;
        }
        Log.w((String)("My tag"), (String)("No results"));
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(2130903042);
        this.title = this.getIntent().getStringExtra("title");
        (TextView)(this.findViewById(2131296269)).setText((CharSequence)(this.title));
        this.initializeContacts();
        this.updateList();
    }

    public void updateList() {
        LinearLayout linearLayout = (LinearLayout)(this.findViewById(2131296270));
        linearLayout.removeAllViews();
        SharedPreferences.Editor editor = MainActivity.settings.edit();
        LayoutInflater layoutInflater = (LayoutInflater)(this.getApplicationContext().getSystemService("layout_inflater"));
        int n = 0;
        while (n < this.group.size()) {
            View view = layoutInflater.inflate(2130903043, (ViewGroup)(null));
            QuickContactBadge quickContactBadge = (QuickContactBadge)(view.findViewById(2131296273));
            quickContactBadge.assignContactFromPhone(this.group.get(n).number(), true);
            new URItools().setContactUri(this.group.get(n).number(), quickContactBadge, (Context)(this));
            (TextView)(view.findViewById(2131296274)).setText((CharSequence)(this.group.get(n).name()));
            (TextView)(view.findViewById(2131296275)).setText((CharSequence)(this.group.get(n).number()));
            (Button)(view.findViewById(2131296276)).setOnClickListener((View.OnClickListener)(new View.OnClickListener(){

                public void onClick(View view) {
                    EditGroup.this.deleteContact(n);
                }
            }));
            linearLayout.addView(view);
            editor.putString(("group" + this.title + n), (String.valueOf((Object)(this.title)) + "\t" + this.group.get(n).name() + "\t" + this.group.get(n).number()));
            editor.commit();
            ++n;
        }
        return;
    }

}

