package com.dallinc.masstexter.models;

import android.content.Context;
import android.net.Uri;

import com.dallinc.masstexter.messaging.SendSMS;
import com.orm.SugarRecord;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

/**
 * Created by dallin on 2/7/15.
 */
public class SingleMessage extends SugarRecord<SingleMessage> {
    public String phoneNumber;
    public int deliveryAttempts;
    public String successfullySentAt;
    public GroupMessage groupMessage;
    public String contactName;
    public String photoUriString;

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

    public void sendMessage(Context context) {
        deliveryAttempts++;
        SendSMS.startActionSendSMS(context, phoneNumber, individualizedMessage());
    }

    private String individualizedMessage() {
        groupMessage.buildArrayListFromString();
        if(groupMessage.variables.size() > 0) {
            return replaceNameVariables(groupMessage.messageBody, groupMessage.variables);
        }
        return groupMessage.messageBody;
    }

    private String replaceNameVariables(String message, ArrayList<String> variables) {
        // TODO: Make this more robust. Handle names that don't have spaces (only one name)

        String fullName = contactName;
        String[] parts = fullName.split(" ");

        for(String variable : variables) {
            switch (variable) {
                case "first name":
                    message = message.replaceFirst("¬", parts[0]);
                    break;
                case "last name":
                    message = message.replaceFirst("¬", parts[parts.length-1]);
                    break;
                case "full name":
                    message = message.replaceFirst("¬", fullName);
                    break;
            }
        }

        return message;
    }

    private void setAsSent() {
        DateTime dateTime = DateTime.now();
        DateTimeFormatter fmt = DateTimeFormat.forPattern("MMM d, yyyy - h:mm a");
        successfullySentAt = dateTime.toString(fmt);
    }
}
