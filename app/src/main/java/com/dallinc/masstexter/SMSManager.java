package com.dallinc.masstexter;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import contactpicker.Contact;

/**
 * Created by dallin on 2/16/15.
 */
public class SMSManager {

    protected static void sendGroupMessage(Context context, ArrayList<String> numbers, ArrayList<Contact> contacts, String masterMessage, ArrayList<String> variables) {
        System.out.println("body: " + masterMessage);
        System.out.println("variables: " + variables.toString());
        System.out.println("recipients: " + numbers);

        // TODO: store message in DB

        for(int i=0; i<numbers.size(); i++) {
            String number = numbers.get(i);
            String individualizedMessage = masterMessage;
            if(variables.size() > 0) {
                individualizedMessage = replaceNameVariables(individualizedMessage, variables, contacts.get(i));
            }

            SendSMS.startActionSendSMS(context, number, individualizedMessage);
        }

    }

    private static String replaceNameVariables(String message, ArrayList<String> variables, Contact contact) {
        // TODO: Make this more robust. Handle names that don't have spaces (only one name)

        String fullName = contact.getContactName();
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
}
