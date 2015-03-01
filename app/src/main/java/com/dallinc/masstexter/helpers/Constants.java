package com.dallinc.masstexter.helpers;

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

    public static boolean contains(String[] vars, String var) {
        for(String v : vars) {
            if(var.equals(v)) {
                return true;
            }
        }
        return false;
    }
}
