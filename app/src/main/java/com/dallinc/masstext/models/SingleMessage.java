package com.dallinc.masstext.models;

import android.content.Context;
import android.net.Uri;

import com.dallinc.masstext.R;
import com.dallinc.masstext.messaging.SendSMS;
import com.orm.SugarRecord;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by dallin on 2/7/15.
 */
public class SingleMessage extends SugarRecord {
    public String phoneNumber;
    public int deliveryAttempts;
    public String successfullySentAt;
    public GroupMessage groupMessage;
    public String contactName;
    public String photoUriString;
    public String failureMessage;
    //TODO: public String numberLabel;

    public SingleMessage() {

    }

    public SingleMessage(String number, String name, GroupMessage group) {
        phoneNumber = number;
        contactName = name;
        groupMessage = group;
        deliveryAttempts = 0;
    }

    public void setPhotoUri(Uri uri) {
        photoUriString = uri.toString();
    }

    public void sendMessage(Context context, int delay) {
        deliveryAttempts++;
        save();
        SendSMS.startActionSendSMS(context, this.getId(), delay);
    }

    public String individualizedMessage(Context c) {
        groupMessage.buildArrayListFromString();
        if(groupMessage.variables.size() > 0) {
            return replaceNameVariables(groupMessage.messageBody, groupMessage.variables, c);
        }
        return groupMessage.messageBody;
    }

    private String replaceNameVariables(String message, ArrayList<String> variables, Context c) {
        String fullName;
        String[] parts;
        if(contactName == null) {
            fullName = "";
            parts = new String[]{"", ""};
        } else {
            fullName = contactName;
            if(fullName.contains(" ")) {
                parts = fullName.split(" ");
            } else {
                parts = new String[]{fullName};
            }
        }

        for(String variable : variables) {
            if (variable.equals(c.getString(R.string.var_first_name))) {
                message = message.replaceFirst("¬", parts[0]);
            }
            else if (variable.equals(c.getString(R.string.var_last_name))) {
                message = message.replaceFirst("¬", parts[parts.length-1]);
            }
            else if (variable.equals(c.getString(R.string.var_full_name))) {
                message = message.replaceFirst("¬", fullName);
            }
            else if (!Locale.getDefault().getLanguage().equals("en")) {
                if (variable.equals("first name")) {
                    message = message.replaceFirst("¬", parts[0]);
                }
                else if (variable.equals("last name")) {
                    message = message.replaceFirst("¬", parts[parts.length-1]);
                }
                else if (variable.equals("full name")) {
                    message = message.replaceFirst("¬", fullName);
                }
            }
        }

        return message;
    }

    public boolean isFailed() {
        return failureMessage != null;
    }

    public void setAsSent() {
        failureMessage = null;
        DateTime dateTime = DateTime.now();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM d, yyyy - h:mm a");
        successfullySentAt = dateTime.toString(fmt);
        save();
    }

    public void clearFailureMessage() {
        failureMessage = null;
        save();
    }

    public void fail(String message) {
        failureMessage = message;
        save();
    }
}
