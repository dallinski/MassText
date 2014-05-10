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
import com.dallinc.masstexter.EditGroup;
import com.dallinc.masstexter.Group;
import com.dallinc.masstexter.MainActivity;
import java.util.ArrayList;

public class GroupManage
extends Activity {
    public void addGroup(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context)(this));
        builder.setTitle((CharSequence)("New Group"));
        EditText editText = new EditText((Context)(this));
        editText.setSingleLine();
        editText.setHint((CharSequence)("Group name"));
        builder.setView((View)(editText));
        builder.setPositiveButton((CharSequence)("Create"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface2, int dialogInterface2) {
                if (editText.getText().toString().length() < 1) {
                    GroupManage.this.addGroup(view);
                    Toast.makeText((Context)(GroupManage.this.getApplicationContext()), (CharSequence)("Input a group name"), (int)(0)).show();
                    return;
                }
                if (MainActivity.indexOfGroup(editText.getText().toString()) != -1) {
                    GroupManage.this.addGroup(view);
                    Toast.makeText((Context)(GroupManage.this.getApplicationContext()), (CharSequence)(("The group \"" + editText.getText().toString() + "\" already exists!")), (int)(0)).show();
                    return;
                }
                Group group = new Group(editText.getText().toString());
                MainActivity.groups.add((Object)(group));
                MainActivity.groups_adapter.add((Object)(editText.getText().toString()));
                GroupManage.this.update();
                Intent intent = new Intent(GroupManage.this.getApplicationContext(), (Class)(EditGroup.class));
                intent.putExtra("title", editText.getText().toString());
                GroupManage.this.startActivity(intent);
            }
        }));
        builder.setNegativeButton((CharSequence)("Cancel"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface, int n) {
                dialogInterface.cancel();
            }
        }));
        builder.create().show();
    }

    public void deleteGroup(int n) {
        AlertDialog.Builder builder = new AlertDialog.Builder((Context)(this));
        builder.setTitle((CharSequence)("Delete group"));
        builder.setMessage((CharSequence)(("Do you really want to delete " + (Group)(MainActivity.groups.get(n)).name() + "?")));
        builder.setPositiveButton((CharSequence)("Delete"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface, int n) {
                SharedPreferences.Editor editor = MainActivity.settings.edit();
                int n2 = 0;
                do {
                    if (n2 >= (Group)(MainActivity.groups.get(n)).size()) {
                        MainActivity.groups_adapter.remove((Object)((Group)(MainActivity.groups.get(n)).name()));
                        MainActivity.groups.remove(n);
                        GroupManage.this.update();
                        return;
                    }
                    editor.remove(("group" + (Group)(MainActivity.groups.get(n)).name() + n2));
                    editor.commit();
                    ++n2;
                } while (true);
            }
        }));
        builder.setNegativeButton((CharSequence)("Cancel"), (DialogInterface.OnClickListener)(new DialogInterface.OnClickListener(){

            public void onClick(DialogInterface dialogInterface, int n) {
                dialogInterface.cancel();
            }
        }));
        builder.create().show();
    }

    public void editGroup(int n) {
        Group group = (Group)(MainActivity.groups.get(n));
        Intent intent = new Intent((Context)(this), (Class)(EditGroup.class));
        intent.putExtra("title", group.name());
        this.startActivity(intent);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(2130903045);
        this.update();
    }

    public void update() {
        LinearLayout linearLayout = (LinearLayout)(this.findViewById(2131296282));
        linearLayout.removeAllViews();
        LayoutInflater layoutInflater = (LayoutInflater)(this.getApplicationContext().getSystemService("layout_inflater"));
        int n = 0;
        while (n < MainActivity.groups.size()) {
            int n2 = n++;
            View view = layoutInflater.inflate(2130903046, (ViewGroup)(null));
            view.setOnClickListener((View.OnClickListener)(new View.OnClickListener(){

                public void onClick(View view) {
                    GroupManage.this.editGroup(n2);
                }
            }));
            (TextView)(view.findViewById(2131296274)).setText((CharSequence)((Group)(MainActivity.groups.get(n)).name()));
            (Button)(view.findViewById(2131296276)).setOnClickListener((View.OnClickListener)(new View.OnClickListener(){

                public void onClick(View view) {
                    GroupManage.this.deleteGroup(n2);
                }
            }));
            linearLayout.addView(view);
        }
        return;
    }

}

