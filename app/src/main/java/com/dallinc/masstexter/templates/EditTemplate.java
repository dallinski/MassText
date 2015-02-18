package com.dallinc.masstexter.templates;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.dallinc.masstexter.helpers.Constants;
import com.dallinc.masstexter.R;
import com.dallinc.masstexter.helpers.TextDrawable;
import com.dallinc.masstexter.models.Template;
import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;

import java.util.ArrayList;


public class EditTemplate extends ActionBarActivity {

    Boolean disableInsertVariable = true;
    ArrayList<String> variables = new ArrayList<String>();
    Template existingTemplate;

    int cursor_position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_template);

        final FloatingLabelEditText bodyInputField = (FloatingLabelEditText) findViewById(R.id.templateBodyInput);
        bodyInputField.getInputWidget().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                disableInsertVariable = !hasFocus;
                invalidateOptionsMenu();
            }
        });

        bodyInputField.addInputWidgetTextChangedListener(new TextWatcher() {
            String before_text = "";
            int before_variable_count = 0;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                before_text = s.toString();
                before_variable_count = variableInstances(s.toString(), start+count);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int after_variable_count = variableInstances(s.toString(), start+count);
                while(after_variable_count < before_variable_count) {
                    // Remove the appropriate variable(s)
                    variables.remove(before_variable_count-1);
                    before_variable_count--;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            long template_id = bundle.getLong("template_id");
            existingTemplate = Template.findById(Template.class, template_id);
            existingTemplate.buildArrayListFromString();
            FloatingLabelEditText titleInputField = (FloatingLabelEditText) findViewById(R.id.templateTitleInput);
            titleInputField.setInputWidgetText(existingTemplate.title);
            bodyInputField.setInputWidgetText(existingTemplate.body);
            variables = existingTemplate.variables;
            styleEditText();
        } else {
            // A new template.
            // automatically focus on the title and show keyboard for quick entry
            findViewById(R.id.templateTitleInput).requestFocus();
        }
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
                return saveTemplate();
            case R.id.action_insert_variable:
                FloatingLabelEditText bodyInputField = (FloatingLabelEditText) findViewById(R.id.templateBodyInput);
                cursor_position = bodyInputField.getInputWidget().getSelectionEnd();
                selectVariable(bodyInputField.getContext());
                return true;
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                upIntent.putExtra("opened_tab", Constants.TEMPLATES_FRAGMENT_POS);
                NavUtils.navigateUpTo(this, upIntent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean saveTemplate() {
        FloatingLabelEditText title = (FloatingLabelEditText) findViewById(R.id.templateTitleInput);
        FloatingLabelEditText body = (FloatingLabelEditText) findViewById(R.id.templateBodyInput);
        String title_text = title.getInputWidgetText().toString();
        if(title_text.length() < 1) {
            Toast.makeText(getBaseContext(), "You cannot save without a title.", Toast.LENGTH_SHORT).show();
            return false;
        }
        String body_text = body.getInputWidgetText().toString();
        if(body_text.length() < 1) {
            Toast.makeText(getBaseContext(), "You cannot save without body text.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(existingTemplate != null) {
            existingTemplate.title = title_text;
            existingTemplate.body = body_text;
            existingTemplate.variables = variables;
            existingTemplate.save();
        } else {
            Template template = new Template(title_text, body_text, variables);
            template.save();
        }
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        upIntent.putExtra("opened_tab", Constants.TEMPLATES_FRAGMENT_POS);
        NavUtils.navigateUpTo(this, upIntent);
        return true;
    }

    private void insertVariable(String variable) {
        FloatingLabelEditText bodyInputField = (FloatingLabelEditText) findViewById(R.id.templateBodyInput);
        int variable_position = variableInstances(bodyInputField.getInputWidgetText().toString(), cursor_position);
        String new_editable_body = bodyInputField.getInputWidgetText().replace(cursor_position, cursor_position, "¬", 0, 1).toString();
        variables.add(variable_position, variable);
        bodyInputField.setInputWidgetText(new_editable_body);
        cursor_position += 1;
        bodyInputField.getInputWidget().setSelection(cursor_position);

        styleEditText();
    }

    private int variableInstances(String s, int end_position) {
        int count = 0;
        for(int i=0; i<end_position; i++ ) {
            if(s.charAt(i) == '¬') {
                count++;
            }
        }
        return count;
    }

    private void styleEditText() {
        FloatingLabelEditText bodyInputField = (FloatingLabelEditText) findViewById(R.id.templateBodyInput);
        String template_text = bodyInputField.getInputWidgetText().toString();
        SpannableString spanText = new SpannableString(template_text);

        int starting_pos = 0;
        int variable_idx = 0;
        while(starting_pos != -1) {
            int idx = template_text.indexOf("¬", starting_pos);
            if(idx == -1) {
                break;
            }

            String variable = variables.get(variable_idx);

            Rect bounds = new Rect();
            Paint textPaint = bodyInputField.getInputWidget().getPaint();
            textPaint.getTextBounds(variable, 0, variable.length(), bounds);
            int width = bounds.width();

            TextDrawable d = new TextDrawable(this);
            d.setText(variable);
            d.setTextColor(getResources().getColor(R.color.colorAccent));
            d.setTextSize(20);
            d.setTextAlign(Layout.Alignment.ALIGN_CENTER);
            d.setBounds(3, 0, width+6, (int)(bodyInputField.getInputWidget().getTextSize()));

            spanText.setSpan(new ImageSpan(d, ImageSpan.ALIGN_BASELINE), idx, idx+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            starting_pos = idx+1;
            variable_idx++;
        }

        bodyInputField.setInputWidgetText(spanText, TextView.BufferType.SPANNABLE);
        try{
            bodyInputField.getInputWidget().setSelection(cursor_position);
        } catch(IndexOutOfBoundsException e) {
            bodyInputField.getInputWidget().setSelection(bodyInputField.getInputWidgetText().length());
        }

    }

    private void selectVariable(final Context context) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insert Variable");

        builder.setItems(Constants.VARIABLE_OPTIONS, new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface, int n) {
                if(Constants.VARIABLE_OPTIONS[n].equals("custom variable")) {
                    inputCustomVariable(context);
                    return;
                }
                insertVariable(Constants.VARIABLE_OPTIONS[n]);
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
                if(Constants.contains(Constants.VARIABLE_OPTIONS, var_text)) {
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
}
