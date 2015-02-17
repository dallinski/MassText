package com.dallinc.masstexter;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
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
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.marvinlabs.widget.floatinglabel.edittext.FloatingLabelEditText;

import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Iterator;

import contactpicker.Contact;
import contactpicker.ContactManager;
import contactpicker.FlowLayout;


public class Compose extends ActionBarActivity {
    final int REQUEST_CODE = 100;
    boolean repeatCheck = false;
    int i = 0;
    ArrayList<Contact> contactsShareDetail;
    ArrayList<String> contactsSharePhone;
    ArrayList<String> variables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        final ButtonRectangle sendMessageBtn = (ButtonRectangle) findViewById(R.id.sendMessage);
        sendMessageBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        final ButtonRectangle finalizeBtn = (ButtonRectangle) findViewById(R.id.finalizeMessage);
        finalizeBtn.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        contactsShareDetail = new ArrayList<Contact>();
        contactsSharePhone = new ArrayList<String>();
        variables = new ArrayList<String>();
        ScrollViewWithMaxHeight maxHeightScrollView = (ScrollViewWithMaxHeight) findViewById(R.id.maxHeightScrollView);
        maxHeightScrollView.setMaxHeight((int) (90 * getResources().getDisplayMetrics().density));

        final FloatingLabelEditText editText = (FloatingLabelEditText) findViewById(R.id.composeBody);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            final Template template = Template.findById(Template.class, bundle.getLong("template_id"));
            template.buildArrayListFromString();
            variables = template.variables;
            setSendButtonVisibility();
            editText.setInputWidgetText(template.body);
            styleEditText();

            editText.addInputWidgetTextChangedListener(new TextWatcher() {
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
                        variables.remove(before_variable_count - 1);
                        before_variable_count--;
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    setSendButtonVisibility();
                }
            });
        }

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getInputWidgetText().toString().length() < 1) {
                    Toast.makeText(getBaseContext(), "You cannot send an empty message", Toast.LENGTH_SHORT).show();
                    return;
                } else if (contactsSharePhone.size() < 1) {
                    Toast.makeText(getBaseContext(), "You must specify at least one recipient", Toast.LENGTH_SHORT).show();
                    return;
                }
                System.out.println("body: " + editText.getInputWidgetText().toString());
                System.out.println("variables: " + variables.toString());
                System.out.println("recipients: " + contactsSharePhone);
            }
        });

        finalizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillInVariables();
            }
        });

        setSendButtonVisibility();
    }

    private void setSendButtonVisibility() {
        ButtonRectangle sendMessageBtn = (ButtonRectangle) findViewById(R.id.sendMessage);
        ButtonRectangle finalizeBtn = (ButtonRectangle) findViewById(R.id.finalizeMessage);

        sendMessageBtn.setVisibility(unsetVariableExists() ? View.GONE : View.VISIBLE);
        finalizeBtn.setVisibility(unsetVariableExists() ? View.VISIBLE : View.GONE);
    }

    private boolean unsetVariableExists() {
        for(String var : variables) {
            if(var.length() == 0) {
                continue; // I don't know why, but there is sometimes a blank variable
            }
            if(!Constants.contains(Constants.AUTO_VARIABLES, var)) {
                return true;
            }
        }
        return false;
    }

    private void fillInVariables() {
        for(String var : variables) {
            if(!Constants.contains(Constants.AUTO_VARIABLES, var)) {
                switch (var) {
                    case "time":
                        showTimePickerDialog();
                        return;
                    case "date":
                        showDatePickerDialog();
                        return;
                    case "day of the week":
                        showDayOfTheWeekPickerDialog();
                        return;
                    default:
                        showCustomVariablePickerDialog(var);
                        return;
                }
            }
        }
    }

    private void replaceVariable(String replacement) {
        final FloatingLabelEditText editText = (FloatingLabelEditText) findViewById(R.id.composeBody);
        for(int i=0; i<variables.size(); i++) {
            if(!Constants.contains(Constants.AUTO_VARIABLES, variables.get(i))) {
                String original = editText.getInputWidgetText().toString();
                int var_pos = nthOccurrence(original, '¬', i);
                Editable widgetText = editText.getInputWidgetText();
                widgetText.replace(var_pos, var_pos+1, replacement);
                editText.setInputWidgetText(widgetText.toString());
                styleEditText();
                fillInVariables();
                return;
            }
        }
    }

    private static int nthOccurrence(String str, char c, int n) {
        int pos = str.indexOf(c, 0);
        while (n-- > 0 && pos != -1)
            pos = str.indexOf(c, pos+1);
        return pos;
    }

    private void showTimePickerDialog() {
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                LocalTime localTime = new LocalTime().withHourOfDay(hourOfDay).withMinuteOfHour(minute);
                DateTimeFormatter fmt = DateTimeFormat.forPattern("h:mm a");
                String str = localTime.toString(fmt);
                replaceVariable(str);
            }
        };
        TimePickerFragment timePickerFragment = TimePickerFragment.withCustomListener(timeSetListener);
        timePickerFragment.show(this.getSupportFragmentManager(), "timePicker");
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear = monthOfYear + 1; // make it one-indexed instead of zero-indexed
                LocalDate date = new LocalDate().withYear(year).withMonthOfYear(monthOfYear).withDayOfMonth(dayOfMonth);
                DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM d, yyyy");
                String str = date.toString(fmt);
                replaceVariable(str);
            }
        };
        DatePickerFragment datePickerFragment = DatePickerFragment.withCustomListener(dateSetListener);
        datePickerFragment.show(this.getSupportFragmentManager(), "datePicker");
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

    private void showCustomVariablePickerDialog(String var_name) {
        CustomVariablePickerFragment.OnMyDialogResult onMyDialogResult = new CustomVariablePickerFragment.OnMyDialogResult() {
            @Override
            public void finish(String result) {
                replaceVariable(result);
            }
        };
        CustomVariablePickerFragment customVariablePickerFragment = CustomVariablePickerFragment.withCustomListener(onMyDialogResult, var_name);
        customVariablePickerFragment.show(this.getSupportFragmentManager(), "customPicker");
    }

    private void showDayOfTheWeekPickerDialog() {
        DayOfWeekPickerFragment.OnMyDialogResult onMyDialogResult = new DayOfWeekPickerFragment.OnMyDialogResult() {
            @Override
            public void finish(String result) {
                replaceVariable(result);
            }
        };
        DayOfWeekPickerFragment dayOfWeekPickerFragment = DayOfWeekPickerFragment.withCustomListener(onMyDialogResult);
        dayOfWeekPickerFragment.show(this.getSupportFragmentManager(), "dayOfWeekPicker");
    }

    private void styleEditText() {
        FloatingLabelEditText bodyInputField = (FloatingLabelEditText) findViewById(R.id.composeBody);
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
            bodyInputField.getInputWidget().setSelection(bodyInputField.getInputWidget().getSelectionEnd());
        } catch(IndexOutOfBoundsException e) {
            bodyInputField.getInputWidget().setSelection(bodyInputField.getInputWidgetText().length());
        }

    }

    private void clear() {
        contactsShareDetail = new ArrayList<Contact>();
        contactsSharePhone = new ArrayList<String>();

        FlowLayout chipsBoxLayout = (FlowLayout)findViewById(R.id.chips_box_layout);
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

                                FlowLayout chipsBoxLayout = (FlowLayout)findViewById(R.id.chips_box_layout);
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

                                FlowLayout chipsBoxLayout = (FlowLayout)findViewById(R.id.chips_box_layout);
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
