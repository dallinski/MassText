package com.dallinc.masstexter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.dallinc.masstexter.ChangeLog;
import com.dallinc.masstexter.Group;
import com.dallinc.masstexter.GroupManage;
import com.dallinc.masstexter.MessageManage;
import com.dallinc.masstexter.Template;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends FragmentActivity {
    static ArrayList<Group> groups;
    static ArrayAdapter<String> groups_adapter;
    static ArrayList<Template> messages;
    static ArrayAdapter<String> messages_adapter;
    static SharedPreferences settings;
    Spinner groups_spinner;
    Spinner messages_spinner;
    String tempMessage;

    public static String[] groupToArray(ArrayList<Group> arrayList) {
        String[] arrstring = new String[arrayList.size()];
        int n = 0;
        while (n < arrayList.size()) {
            arrstring[n] = (Group)(arrayList.get(n)).name();
            ++n;
        }
        return arrstring;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     */
    public static int indexOfGroup(String string) {
        for (int i = 0; i < MainActivity.groups.size(); ++i) {
            if ((Group)(MainActivity.groups.get(i)).name().equalsIgnoreCase(string)) return i;
        }
        return -1;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     */
    public static int indexOfTemplate(String string) {
        for (int i = 0; i < MainActivity.messages.size(); ++i) {
            if ((Template)(MainActivity.messages.get(i)).name().equalsIgnoreCase(string)) return i;
        }
        return -1;
    }

    public static String[] messageToArray(ArrayList<Template> arrayList) {
        String[] arrstring = new String[arrayList.size()];
        int n = 0;
        while (n < arrayList.size()) {
            arrstring[n] = (Template)(arrayList.get(n)).name();
            ++n;
        }
        return arrstring;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     */
    public void fillInTheBlanks(View view) {
        if (MainActivity.messages.size() < 1) {
            Toast.makeText((Context)(this.getApplicationContext()), (CharSequence)("Please create a message!"), (int)(0)).show();
            return;
        }
        if (MainActivity.groups.size() < 1) {
            Toast.makeText((Context)(this.getApplicationContext()), (CharSequence)("Please create a group!"), (int)(0)).show();
            return;
        }
        this.tempMessage = (Template)(MainActivity.messages.get(this.messages_spinner.getSelectedItemPosition())).message();
        ArrayList arrayList = new ArrayList();
        String string = "";
        boolean bl = false;
        int n = 0;
        do {
            if (n >= this.tempMessage.length()) break;
            if (bl) {
                string = (String.valueOf((Object)(string)) + this.tempMessage.charAt(n));
            }
            if (this.tempMessage.charAt(n) == '%') {
                bl = (bl) ? (false) : (true);
                if (!(bl)) {
                    arrayList.add((Object)(string.substring(0, (-1 + string.length()))));
                    string = "";
                }
            }
            ++n;
        } while (true);
        Iterator iterator = arrayList.iterator();
        while (iterator.hasNext()) {
            this.inputField(view, (String)(iterator.next()));
        }
        if (this.tempMessage.contains((CharSequence)("%"))) return;
        this.sendMessageDialog(view);
    }

    public void goToGroupManagement(View view) {
        this.startActivity(new Intent(this.getApplicationContext(), (Class)(GroupManage.class)));
    }

    public void goToMessageManagement(View view) {
        this.startActivity(new Intent(this.getApplicationContext(), (Class)(MessageManage.class)));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     */
    public void initializeSpinners() {
        this.groups_spinner = (Spinner)(this.findViewById(2131296261));
        MainActivity.groups_adapter = new ArrayAdapter((Context)(this), 17367048);
        String[] arrstring = MainActivity.groupToArray(MainActivity.groups);
        int n = 0;
        do {
            if (n >= arrstring.length) break;
            MainActivity.groups_adapter.add((Object)(arrstring[n]));
            ++n;
        } while (true);
        MainActivity.groups_adapter.setDropDownViewResource(17367049);
        this.groups_spinner.setAdapter(MainActivity.groups_adapter);
        this.messages_spinner = (Spinner)(this.findViewById(2131296258));
        MainActivity.messages_adapter = new ArrayAdapter((Context)(this), 17367048);
        String[] arrstring2 = MainActivity.messageToArray(MainActivity.messages);
        for (int i = 0; i < arrstring2.length; ++i) {
            MainActivity.messages_adapter.add((Object)(arrstring2[i]));
        }
        MainActivity.messages_adapter.setDropDownViewResource(17367049);
        this.messages_spinner.setAdapter(MainActivity.messages_adapter);
        this.messages_spinner.setOnItemSelectedListener((AdapterView.OnItemSelectedListener)(new AdapterView.OnItemSelectedListener(){

            public void onItemSelected(AdapterView<?> adapterView22, View adapterView22, int n, long adapterView22) {
                MainActivity.this.setPreviewText((Template)(MainActivity.messages.get(n)).message());
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                MainActivity.this.setPreviewText("");
            }
        }));
    }

    public void inputField(View view, String string) {
        if (string.equals((Object)("time"))) {
            this.showTimePickerDialog(view);
            return;
        }
        if (string.equals((Object)("date"))) {
            this.showDatePickerDialog(view);
            return;
        }
        if (string.equals((Object)("first name"))) {
            this.tempMessage = this.tempMessage.replaceAll("%first name%", "`firstName");
            return;
        }
        if (string.equals((Object)("full name"))) {
            this.tempMessage = this.tempMessage.replaceAll("%full name%", "`fullName");
            return;
        }
        this.variablePickerDialog(view, string);
    }

    public String insertPersonalName(String string, String string2) {
        String string3 = this.nameFromNumber(string);
        String string4 = "";
        String[] arrstring = string3.split(" ");
        if (arrstring.length <= 0) return string2.replaceAll("`fullName", string3).replaceAll("`firstName", string4);
        string4 = arrstring[0];
        return string2.replaceAll("`fullName", string3).replaceAll("`firstName", string4);
    }

    public String nameFromNumber(String string) {
        String[] arrstring = new String[]{"display_name", "_id"};
        Uri uri = Uri.withAppendedPath((Uri)(ContactsContract.PhoneLookup.CONTENT_FILTER_URI), (String)(Uri.encode((String)(string))));
        Cursor cursor = this.getApplicationContext().getContentResolver().query(uri, arrstring, (String)(null), (String[])(null), (String)(null));
        if (!(cursor.moveToFirst())) return "noname noname";
        return cursor.getString(cursor.getColumnIndex("display_name"));
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(2130903040);
        MainActivity.settings = this.getPreferences(0);
        MainActivity.messages = new ArrayList();
        MainActivity.groups = new ArrayList();
        this.initializeSpinners();
        this.parseStoredData();
        ChangeLog changeLog = new ChangeLog((Context)(this));
        if (!(changeLog.firstRun())) return;
        changeLog.getLogDialog().show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.getMenuInflater().inflate(2131230720, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (!(menuItem.getTitle().toString().equals((Object)("Change Log")))) return true;
        new ChangeLog((Context)(this)).getFullLogDialog().show();
        return true;
    }

    public void parseStoredData() {
        Iterator iterator = MainActivity.settings.getAll().values().iterator();
        block0 : do {
            String[] arrstring;
            if (!(iterator.hasNext())) {
                if (MainActivity.messages.size() <= 0) return;
                this.setPreviewText((Template)(MainActivity.messages.get(0)).message());
                return;
            }
            if ((arrstring = (String)(iterator.next()).split("\t")).length == 2) {
                String string = arrstring[0];
                Template template = new Template(string, arrstring[1]);
                MainActivity.messages.add((Object)(template));
                MainActivity.messages_adapter.add((Object)(string));
                continue;
            }
            if (arrstring.length == 3) {
                String string = arrstring[0];
                String string2 = arrstring[1];
                String string3 = arrstring[2];
                boolean bl = false;
                int n = 0;
                do {
                    if (n >= MainActivity.groups.size()) {
                        if (bl) continue block0;
                        Group group = new Group(string);
                        group.add(string2, string3);
                        MainActivity.groups.add((Object)(group));
                        MainActivity.groups_adapter.add((Object)(string));
                        continue block0;
                    }
                    if ((Group)(MainActivity.groups.get(n)).name().equals((Object)(string))) {
                        bl = true;
                        (Group)(MainActivity.groups.get(n)).add(string2, string3);
                    }
                    ++n;
                } while (true);
            }
            System.out.println("found a bad stored preference");
        } while (true);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     */
    public void sendMessage() {
        var1_1 = (Group)(MainActivity.groups.get(this.groups_spinner.getSelectedItemPosition()));
        block0 : for (var2_2 = 0; var2_2 < var1_1.size(); ++var2_2) {
            var3_3 = var1_1.numberAt(var2_2);
            var4_4 = this.insertPersonalName(var3_3, this.tempMessage);
            var5_5 = SmsManager.getDefault();
            ** if (var4_4.length() > 160) goto lbl11
lbl9: // 1 sources:
            var5_5.sendTextMessage(var3_3, (String)(null), var4_4, (PendingIntent)(null), (PendingIntent)(null));
            ** GOTO lbl17
lbl11: // 1 sources:
            var8_7 = var5_5.divideMessage(var4_4);
            var9_8 = new ArrayList();
            var10_9 = 0;
            do lbl-1000: // 2 sources:
            {
                if (var10_9 < var8_7.size()) break block0;
                var5_5.sendMultipartTextMessage(var3_3, (String)(null), var8_7, var9_8, (ArrayList)(null));
lbl17: // 2 sources:
                var6_6 = new ContentValues();
                var6_6.put("address", var3_3);
                var6_6.put("body", var4_4);
                this.getContentResolver().insert(Uri.parse((String)("content://sms/sent")), var6_6);
                Toast.makeText((Context)(this.getApplicationContext()), (CharSequence)("Successfully sent message!"), (int)(1)).show();
                continue block0;
                break;
            } while (true);
        }
        return;
        {
            var9_8.add(var10_9, (Object)(PendingIntent.getBroadcast((Context)(this), (int)(0), (Intent)(new Intent("SMS_SENT")), (int)(0))));
            ++var10_9;
            ** while (true)
        }
    }

    public void sendMessageDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context)(this));
        builder.setTitle((CharSequence)("Message Preview"));
        TextView textView = new TextView(this.getApplicationContext());
        textView.setText((CharSequence)(this.tempMessage.replaceAll("`fullName", "\"full name\"").replaceAll("`firstName", "\"first name\"")));
        builder.setView((View)(textView));
        builder.setPositiveButton((CharSequence)("Send"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface2, int dialogInterface2) {
                MainActivity.this.sendMessage();
            }
        }));
        builder.setNegativeButton((CharSequence)("Cancel"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface, int n) {
                dialogInterface.cancel();
            }
        }));
        builder.show();
    }

    public void setPreviewText(String string) {
        TextView textView = (TextView)(this.findViewById(2131296267));
        textView.setBackgroundResource(2130837506);
        textView.setText((CharSequence)(string.replaceAll("[%*%]", "\"")));
    }

    public void showDatePickerDialog(View view) {
        new DatePickerFragment().show(this.getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View view) {
        new TimePickerFragment().show(this.getSupportFragmentManager(), "timePicker");
    }

    public void variablePickerDialog(View view, String string) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context)(this));
        builder.setTitle((CharSequence)((String.valueOf((char)(Character.toUpperCase((char)(string.charAt(0))))) + string.substring(1, string.length()))));
        EditText editText = new EditText((Context)(this));
        editText.setInputType(1);
        editText.setHint((CharSequence)(("some " + string)));
        builder.setView((View)(editText));
        builder.setPositiveButton((CharSequence)("Set"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface2, int dialogInterface2) {
                MainActivity.this.tempMessage = MainActivity.this.tempMessage.replaceFirst(("%" + string + "%"), editText.getText().toString());
                if (MainActivity.this.tempMessage.contains((CharSequence)("%"))) return;
                MainActivity.this.sendMessageDialog(view);
            }
        }));
        builder.setNegativeButton((CharSequence)("Cancel"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface, int n) {
                dialogInterface.cancel();
                MainActivity.this.tempMessage = "";
            }
        }));
        builder.show();
    }

    @SuppressLint(value={"ValidFragment", "SimpleDateFormat"})
    public class DatePickerFragment
    extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {
        public Dialog onCreateDialog(Bundle bundle) {
            Calendar calendar = Calendar.getInstance();
            int n = calendar.get(1);
            int n2 = calendar.get(2);
            int n3 = calendar.get(5);
            return new DatePickerDialog((Context)(this.getActivity()), (DatePickerDialog.OnDateSetListener)(this), n, n2, n3);
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         */
        public void onDateSet(DatePicker datePicker, int n, int n2, int n3) {
            String string = "";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM d, yyyy");
            try {
                String string2;
                Date date = simpleDateFormat.parse(("" + n2 + 1 + " " + n3 + ", " + n));
                string = (string2 = new SimpleDateFormat("MMM d, yyyy").format(date));
            }
            catch (ParseException var7_9) {
                var7_9.printStackTrace();
            }
            MainActivity.this.tempMessage = MainActivity.this.tempMessage.replaceFirst("%date%", string);
            if (MainActivity.this.tempMessage.contains((CharSequence)("%"))) return;
            MainActivity.this.sendMessageDialog((View)(datePicker));
        }
    }

    @SuppressLint(value={"ValidFragment"})
    public class TimePickerFragment
    extends DialogFragment
    implements TimePickerDialog.OnTimeSetListener {
        public Dialog onCreateDialog(Bundle bundle) {
            this.setRetainInstance(true);
            Calendar calendar = Calendar.getInstance();
            int n = calendar.get(11);
            int n2 = calendar.get(12);
            return new TimePickerDialog((Context)(this.getActivity()), (TimePickerDialog.OnTimeSetListener)(this), n, n2, DateFormat.is24HourFormat((Context)(this.getActivity())));
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         */
        public void onTimeSet(TimePicker timePicker, int n, int n2) {
            String string = (n > 12) ? (("" + n - 12)) : (("" + n));
            StringBuilder stringBuilder = new StringBuilder(String.valueOf((Object)((String.valueOf((Object)(string)) + ":"))));
            Object object = (n2 < 10) ? (("0" + n2)) : (Integer.valueOf((int)(n2)));
            StringBuilder stringBuilder2 = new StringBuilder(String.valueOf((Object)(stringBuilder.append(object).toString())));
            String string2 = (n > 11) ? (" PM") : (" AM");
            String string3 = stringBuilder2.append(string2).toString();
            MainActivity.this.tempMessage = MainActivity.this.tempMessage.replaceFirst("%time%", string3);
            if (MainActivity.this.tempMessage.contains((CharSequence)("%"))) return;
            MainActivity.this.sendMessageDialog((View)(timePicker));
        }
    }

}

