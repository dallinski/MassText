package com.dallinc.masstexter.helpers;

import com.dallinc.masstexter.models.Template;

import java.util.ArrayList;

/**
 * Created by dallin on 2/6/15.
 */
public abstract class Constants {
    public static final int MESSAGING_FRAGMENT_POS = 0;
    public static final int TEMPLATES_FRAGMENT_POS = 1;
    public static final int ABOUT_FRAGMENT_POS = 2;
    public static final String[] VARIABLE_OPTIONS = new String[]{"date", "day of the week", "time", "first name", "last name", "full name", "custom variable"};
    public static final String[] AUTO_VARIABLES = new String[]{"first name", "last name", "full name"};
    public static final String[] DAYS_OF_THE_WEEK = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    public static final String SENT = "SMS_SENT";
    public static final String ERROR = "ERROR";
    public static final String ACTION_SEND_SMS = "com.dallinc.masstexter.action.SEND_SMS";
    public static final String BROADCAST_SMS_RESULT= "com.dallinc.masstexter.broadcast.SMS_RESULT";
    public static final String BROADCAST_SENT_GROUP_MESSAGE= "com.dallinc.masstexter.broadcast.SENT_GROUP_MESSAGE";
    public static final String EXTRA_MESSAGE_ID= "com.dallinc.masstexter.extra.MESSAGE_ID";
    public static final String EXTRA_SEND_SMS_RESULT = "com.dallinc.masstexter.extra.SEND_SMS_RESULT";
    public static final String EXTRA_DELAY_MILLIS = "com.dallinc.masstexter.extra.DELAY_MILLIS";
    public static final String HAS_SEEN_EXAMPLE_TEMPLATE = "com.dallinc.masstexter.preference.HAS_SEEN_EXAMPLE_TEMPLATE";

    public static boolean contains(String[] vars, String var) {
        for(String v : vars) {
            if(var.equals(v)) {
                return true;
            }
        }
        return false;
    }

    public static final String EXAMPLE_TEMPLATE_1_TITLE = "Template Instructions";
    public static final String EXAMPLE_TEMPLATE_1_BODY = "Templates are really useful, but only if you know how to use them to their fullest.\n\nTemplates are mostly useful when you are sending the same basic message every day/week/month/etc.\n\nTemplates are saved so that you don't have to type up a new message each time.";
    public static final ArrayList<String> EXAMPLE_TEMPLATE_1_VARIABLES() {
        return new ArrayList<String>();
    }
    public static final Template getExample1() {
        return new Template(EXAMPLE_TEMPLATE_1_TITLE, EXAMPLE_TEMPLATE_1_BODY, EXAMPLE_TEMPLATE_1_VARIABLES());
    }
    public static final String EXAMPLE_TEMPLATE_2_TITLE = "How to use variables 1";
    public static final String EXAMPLE_TEMPLATE_2_BODY = "When you are editing a template (and your cursor is in the body field), a button will appear in the top right portion of the screen next to the save button.\n\nThat button is used to insert variables.\n\nPlace your cursor where you would like a variable, then hit the button and select a variable.\n\nVariables look like this (¬) once inserted.";
    public static final ArrayList<String> EXAMPLE_TEMPLATE_2_VARIABLES() {
        ArrayList<String> vars = new ArrayList<String>();
        vars.add("date");
        return vars;
    }
    public static final Template getExample2() {
        return new Template(EXAMPLE_TEMPLATE_2_TITLE, EXAMPLE_TEMPLATE_2_BODY, EXAMPLE_TEMPLATE_2_VARIABLES());
    }
    public static final String EXAMPLE_TEMPLATE_3_TITLE = "How to use variables 2";
    public static final String EXAMPLE_TEMPLATE_3_BODY = "You will \"fill in the blank\" when you send a message using that template.\n\nFor example: if I were to send a message using this template, I would be prompted to select a date to go here (¬) before I could send the message.";
    public static final ArrayList<String> EXAMPLE_TEMPLATE_3_VARIABLES() {
        ArrayList<String> vars = new ArrayList<String>();
        vars.add("date");
        return vars;
    }
    public static final Template getExample3() {
        return new Template(EXAMPLE_TEMPLATE_3_TITLE, EXAMPLE_TEMPLATE_3_BODY, EXAMPLE_TEMPLATE_3_VARIABLES());
    }
    public static final String EXAMPLE_TEMPLATE_4_TITLE = "Basketball Group";
    public static final String EXAMPLE_TEMPLATE_4_BODY = "Hey ¬, we're going to be playing basketball on ¬ at ¬. See you there at ¬!";
    public static final ArrayList<String> EXAMPLE_TEMPLATE_4_VARIABLES() {
        ArrayList<String> vars = new ArrayList<String>();
        vars.add("first name");
        vars.add("day of the week");
        vars.add("location");
        vars.add("time");
        return vars;
    }
    public static final Template getExample4() {
        return new Template(EXAMPLE_TEMPLATE_4_TITLE, EXAMPLE_TEMPLATE_4_BODY, EXAMPLE_TEMPLATE_4_VARIABLES());
    }
}
