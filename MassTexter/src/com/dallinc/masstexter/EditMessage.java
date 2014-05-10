package com.dallinc.masstexter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import com.dallinc.masstexter.MainActivity;
import com.dallinc.masstexter.Template;
import java.lang.CharSequence;
import java.util.ArrayList;

public class EditMessage
extends Activity {
    String title;

    public void insertVariable(View view) {
        EditText editText = (EditText)(this.findViewById(2131296278));
        editText.getText().toString();
        AlertDialog.Builder builder = new AlertDialog.Builder((Context)(this));
        builder.setTitle((CharSequence)("Insert Variable"));
        String[] arrstring = new String[]{"date", "time", "first name", "full name"};
        builder.setItems((CharSequence[])(arrstring), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface, int n) {
                String string = ("%" + arrstring[n] + "%");
                int n2 = editText.getSelectionStart();
                int n3 = editText.getSelectionEnd();
                if (n2 == -1) {
                    n2 = 0;
                }
                if (n3 == -1) {
                    n3 = 0;
                }
                String string2 = editText.getText().replace(Math.min((int)(n2), (int)(n3)), Math.max((int)(n2), (int)(n3)), (CharSequence)(string), 0, string.length()).toString();
                editText.setText((CharSequence)(string2));
                editText.setSelection((n3 + string.length()));
            }
        }));
        EditText editText2 = new EditText((Context)(this));
        editText2.setSingleLine();
        editText2.setHint((CharSequence)("custom variable"));
        builder.setView((View)(editText2));
        builder.setPositiveButton((CharSequence)("Insert"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface2, int dialogInterface2) {
                if (editText2.getText().toString().length() < 1) {
                    Toast.makeText((Context)(EditMessage.this.getApplicationContext()), (CharSequence)("You didn't write a variable name!"), (int)(0)).show();
                    return;
                }
                String string = ("%" + editText2.getText().toString() + "%");
                int n = editText.getSelectionStart();
                int n2 = editText.getSelectionEnd();
                if (n == -1) {
                    n = 0;
                }
                if (n2 == -1) {
                    n2 = 0;
                }
                String string2 = editText.getText().replace(Math.min((int)(n), (int)(n2)), Math.max((int)(n), (int)(n2)), (CharSequence)(string), 0, string.length()).toString();
                editText.setText((CharSequence)(string2));
                editText.setSelection((n2 + string.length()));
            }
        }));
        builder.setNegativeButton((CharSequence)("Cancel"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface, int n) {
                dialogInterface.dismiss();
            }
        }));
        builder.create().show();
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(2130903044);
        this.title = this.getIntent().getStringExtra("title");
        (EditText)(this.findViewById(2131296277)).setText((CharSequence)(this.title));
        int n = MainActivity.indexOfTemplate(this.title);
        EditText editText = (EditText)(this.findViewById(2131296278));
        if (n == -1) return;
        editText.setText((CharSequence)((Template)(MainActivity.messages.get(n)).message()));
    }

    public void save(View view) {
        EditText editText = (EditText)(this.findViewById(2131296277));
        EditText editText2 = (EditText)(this.findViewById(2131296278));
        if (this.title.equals((Object)(""))) {
            if (editText.getText().equals((Object)(""))) {
                Toast.makeText((Context)(this), (CharSequence)("Enter a template name!"), (int)(0)).show();
                return;
            }
            if (editText2.getText().equals((Object)(""))) {
                Toast.makeText((Context)(this), (CharSequence)("Add text to template!"), (int)(0)).show();
                return;
            }
            String string = editText.getText().toString();
            String string2 = editText2.getText().toString();
            if (MainActivity.indexOfTemplate(string) != -1) {
                Toast.makeText((Context)(this), (CharSequence)("A template with this title already exists!"), (int)(0)).show();
                return;
            }
            Template template = new Template(string, string2);
            MainActivity.messages_adapter.add((Object)(string));
            MainActivity.messages.add((Object)(template));
            SharedPreferences.Editor editor = MainActivity.settings.edit();
            editor.putString(("message" + string), (String.valueOf((Object)(string)) + "\t" + string2));
            editor.commit();
            this.finish();
            return;
        }
        String string = editText.getText().toString();
        String string3 = editText2.getText().toString();
        Template template = new Template(string, string3);
        int n = MainActivity.indexOfTemplate(string);
        MainActivity.messages.remove(n);
        MainActivity.messages.add(n, (Object)(template));
        SharedPreferences.Editor editor = MainActivity.settings.edit();
        editor.remove(("message" + this.title));
        editor.putString(("message" + string), (String.valueOf((Object)(string)) + "\t" + string3));
        editor.commit();
        this.finish();
    }

}

