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
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.dallinc.masstexter.EditMessage;
import com.dallinc.masstexter.MainActivity;
import com.dallinc.masstexter.Template;
import java.util.ArrayList;

public class MessageManage
extends Activity {
    public void addMessage(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context)(this));
        builder.setTitle((CharSequence)("New Template"));
        EditText editText = new EditText((Context)(this));
        editText.setSingleLine();
        editText.setHint((CharSequence)("Template name"));
        builder.setView((View)(editText));
        builder.setPositiveButton((CharSequence)("Create"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface2, int dialogInterface2) {
                if (editText.getText().toString().length() < 1) {
                    MessageManage.this.addMessage(view);
                    Toast.makeText((Context)(MessageManage.this.getApplicationContext()), (CharSequence)("Input a template name"), (int)(0)).show();
                    return;
                }
                if (MainActivity.indexOfTemplate(editText.getText().toString()) != -1) {
                    MessageManage.this.addMessage(view);
                    Toast.makeText((Context)(MessageManage.this.getApplicationContext()), (CharSequence)(("The template \"" + editText.getText().toString() + "\" already exists!")), (int)(0)).show();
                    return;
                }
                Template template = new Template(editText.getText().toString(), "");
                MainActivity.messages.add((Object)(template));
                MainActivity.messages_adapter.add((Object)(editText.getText().toString()));
                MessageManage.this.update();
                Intent intent = new Intent(MessageManage.this.getApplicationContext(), (Class)(EditMessage.class));
                intent.putExtra("title", editText.getText().toString());
                MessageManage.this.startActivity(intent);
            }
        }));
        builder.setNegativeButton((CharSequence)("Cancel"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface, int n) {
                dialogInterface.cancel();
            }
        }));
        builder.create().show();
    }

    public void deleteMessage(int n) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context)(this));
        builder.setTitle((CharSequence)("Delete message"));
        builder.setMessage((CharSequence)(("Do you really want to delete " + (Template)(MainActivity.messages.get(n)).name() + "?")));
        builder.setPositiveButton((CharSequence)("Delete"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface2, int dialogInterface2) {
                SharedPreferences.Editor editor = MainActivity.settings.edit();
                editor.remove(("message" + (Template)(MainActivity.messages.get(n)).name()));
                editor.commit();
                MainActivity.messages_adapter.remove((Object)((Template)(MainActivity.messages.get(n)).name()));
                MainActivity.messages.remove(n);
                MessageManage.this.update();
            }
        }));
        builder.setNegativeButton((CharSequence)("Cancel"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface, int n) {
                dialogInterface.cancel();
            }
        }));
        builder.create().show();
    }

    public void editMessage(int n) {
        Intent intent = new Intent((Context)(this), (Class)(EditMessage.class));
        intent.putExtra("title", (Template)(MainActivity.messages.get(n)).name());
        this.startActivity(intent);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(2130903048);
        this.update();
    }

    public void update() {
        LinearLayout linearLayout = (LinearLayout)(this.findViewById(2131296286));
        linearLayout.removeAllViews();
        LayoutInflater layoutInflater = (LayoutInflater)(this.getApplicationContext().getSystemService("layout_inflater"));
        int n = 0;
        while (n < MainActivity.messages.size()) {
            int n2 = n++;
            View view = layoutInflater.inflate(2130903049, (ViewGroup)(null));
            view.setOnClickListener((View.OnClickListener)(new View.OnClickListener(){

                public void onClick(View view) {
                    MessageManage.this.editMessage(n2);
                }
            }));
            (TextView)(view.findViewById(2131296274)).setText((CharSequence)((Template)(MainActivity.messages.get(n)).name()));
            (Button)(view.findViewById(2131296276)).setOnClickListener((View.OnClickListener)(new View.OnClickListener(){

                public void onClick(View view) {
                    MessageManage.this.deleteMessage(n2);
                }
            }));
            linearLayout.addView(view);
        }
        return;
    }

}

