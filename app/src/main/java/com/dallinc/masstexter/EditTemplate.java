package com.dallinc.masstexter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class EditTemplate extends ActionBarActivity {

    Boolean disableInsertVariable = true;

    int cursor_position = 0;
    final String[] variables = new String[]{"date", "time", "first name", "last name", "full name", "custom variable"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_template);

        FloatingLabelEditText bodyInputField = (FloatingLabelEditText) findViewById(R.id.templateBodyInput);
        bodyInputField.getInputWidget().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                disableInsertVariable = !hasFocus;
                invalidateOptionsMenu();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_template, menu);
        menu.findItem(R.id.action_insert_variable).setVisible(!disableInsertVariable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_save:
                return true;
            case R.id.action_insert_variable:
                FloatingLabelEditText bodyInputField = (FloatingLabelEditText) findViewById(R.id.templateBodyInput);
                cursor_position = bodyInputField.getInputWidget().getSelectionEnd();
                selectVariable(bodyInputField.getContext());
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void insertVariable(String variable) {
        FloatingLabelEditText bodyInputField = (FloatingLabelEditText) findViewById(R.id.templateBodyInput);

        // variables are inserted with the unique identifying tag: "~!@variable_count%&$"
        // This character sequence was chosen arbitrarily in the hope that no one will ever manually enter it.
        int counter = 0;
        while(bodyInputField.getInputWidgetText().toString().contains("~!@" + variable + "_" + counter + "%&$")) {
            counter++;
        }
        variable = "~!@" + variable + "_" + counter + "%&$";

        String new_editable_body = bodyInputField.getInputWidgetText().replace(cursor_position, cursor_position, variable, 0, variable.length()).toString();
        bodyInputField.setInputWidgetText(new_editable_body);
        cursor_position += variable.length();
        bodyInputField.getInputWidget().setSelection(cursor_position);

        styleEditText();
    }

    private void styleEditText() {
        FloatingLabelEditText bodyInputField = (FloatingLabelEditText) findViewById(R.id.templateBodyInput);
        String template_text = bodyInputField.getInputWidgetText().toString();
        ArrayList<Integer[]> variable_indices = getVariableIndices(template_text);
        SpannableString spanText = new SpannableString(template_text);

        for(Integer[] indices : variable_indices) {
            spanText.setSpan(new AbsoluteSizeSpan(0), indices[0], indices[0] + 3, 0);
            spanText.setSpan(new AbsoluteSizeSpan(0), indices[1]-3, indices[1], 0);
            spanText.setSpan(new BackgroundColorSpan(getResources().getColor(R.color.colorPrimaryDark)), indices[0]+3, indices[1]-3, 0);
            spanText.setSpan(new ForegroundColorSpan(Color.WHITE), indices[0]+3, indices[1]-3, 0);
        }

        bodyInputField.setInputWidgetText(spanText, TextView.BufferType.SPANNABLE);
        bodyInputField.getInputWidget().setSelection(cursor_position);
    }

    private ArrayList<Integer[]> getVariableIndices(String text) {
        ArrayList<Integer[]> indicesList = new ArrayList<Integer[]>();

        Pattern p = Pattern.compile("~!@[a-zA-Z0-9 ]*_\\d+%&\\$");
        Matcher m = p.matcher(text);
        while (m.find()) {
            indicesList.add(new Integer[]{text.indexOf(m.group()), text.indexOf(m.group()) + m.group().length()});
        }

        return indicesList;
    }

    private void selectVariable(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insert Variable");

        builder.setItems(variables, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface, int n) {
                if(variables[n].equals("custom variable")) {
                    inputCustomVariable(context);
                    return;
                }
                insertVariable(variables[n]);
                return;
            }
        });
        builder.create().show();
    }

    private void inputCustomVariable(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insert Variable");
        final FloatingLabelEditText customVariable = new FloatingLabelEditText(this);
        customVariable.setLabelText("Custom Variable");
        customVariable.setLabelColor(getResources().getColor(R.color.colorPrimaryDark));
        customVariable.setLabelTextSize(18);
        customVariable.setInputWidgetTextSize(18);
        customVariable.setPadding(35, 0, 35, 0);
        builder.setView(customVariable);

        builder.setPositiveButton("Insert", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface, int n) {
                if (customVariable.getInputWidgetText().toString().length() < 1) {
                    // This should never happen
                    Toast.makeText(context, "You didn't write a variable name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String var_text = customVariable.getInputWidgetText().toString().replaceAll("[^a-zA-Z0-9 ]","");
                if(contains(variables, var_text)) {
                    Toast.makeText(context, "Input variable cannot be defined as a custom variable", Toast.LENGTH_SHORT).show();
                    return;
                }
                insertVariable(var_text);
                return;
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface, int n) {
                dialogInterface.dismiss();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);

        // After show (this is important specially if you have a list, a pager or other view that uses a adapter), clear the flags and set the soft input mode
        alertDialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        customVariable.addInputWidgetTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(s.length() > 0);
            }
        });
    }

    private boolean contains(String[] vars, String var) {
        for(String v : vars) {
            if(var.equals(v)) {
                return true;
            }
        }
        return false;
    }
}
