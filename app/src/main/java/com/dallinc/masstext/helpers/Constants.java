package com.dallinc.masstext.helpers;

import com.dallinc.masstext.models.Template;

import java.util.ArrayList;

/**
 * Created by dallin on 2/6/15.
 */
public abstract class Constants {
    public static final int MESSAGING_FRAGMENT_POS = 0;
    public static final int TEMPLATES_FRAGMENT_POS = 1;
    public static final String[] VARIABLE_OPTIONS = new String[]{"date", "day of the week", "time", "first name", "last name", "full name", "custom variable"};
    public static final String[] AUTO_VARIABLES = new String[]{"first name", "last name", "full name"};
    public static final String[] DAYS_OF_THE_WEEK = new String[]{"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};

    public static final String SENT = "SMS_SENT";
    public static final String ACTION_SEND_SMS = "com.dallinc.masstexter.action.SEND_SMS";
    public static final String BROADCAST_SMS_RESULT= "com.dallinc.masstexter.broadcast.SMS_RESULT";
    public static final String BROADCAST_SENT_GROUP_MESSAGE= "com.dallinc.masstexter.broadcast.SENT_GROUP_MESSAGE";
    public static final String BROADCAST_RELOAD_TEMPLATES= "com.dallinc.masstexter.broadcast.RELOAD_TEMPLATES";
    public static final String EXTRA_MESSAGE_ID= "com.dallinc.masstexter.extra.MESSAGE_ID";
    public static final String EXTRA_SEND_SMS_RESULT = "com.dallinc.masstexter.extra.SEND_SMS_RESULT";
    public static final String EXTRA_DELAY_MILLIS = "com.dallinc.masstexter.extra.DELAY_MILLIS";
    public static final String HAS_SEEN_EXAMPLE_TEMPLATE = "com.dallinc.masstexter.preference.HAS_SEEN_EXAMPLE_TEMPLATE";
    public static final String HAS_SEEN_CHANGE_LOG = "com.dallinc.masstexter.preference.HAS_SEEN_CHANGE_LOG";

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

    public static final String kagi_ichi = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCA";
    public static final String kagi_ni = "QEAjFeGsd6RX77UBcF3PBDizxv2hv9I4nEXW";
    public static final String kagi_san = "NL2uB39SHs/Dwmc+7Z62Sd4jSjjim7YpRFoRm7";
    public static final String kagi_shi = "NeUdPWFBBy4GJJsT2unDZ7OHHtUQTwdyHUItj";
    public static final String kagi_go = "cKzxvl3EjFkI7EYT20yTil1xKX+zGNkoFFWEcxnryXi";
    public static final String kagi_roku = "7Dy/PkLC5LczqxLTgQ8q42XH0yo95RfsiOUl3Qg";
    public static final String kagi_shichi = "7ea1SkBb014pnAkNytWSjFcmfmi8+a7LW6yxDbz/P";
    public static final String kagi_hachi = "75Q32Z0skZXiZYr0/Co/cgWlIfpjOeDlFt2ov";
    public static final String kagi_kyuu = "OZyy9m9aLNv2jk1NtA7SLra08kv76kIqzqbsC+Y";
    public static final String kagi_jyuu = "yz2FwcveCLUOlgYxov4PqlP/e7dZiUYodGQIDAQAB";

    public static final String kagi() {
        return kagi_ichi + kagi_ni + kagi_san + kagi_shi + kagi_go + kagi_roku + kagi_shichi + kagi_hachi + kagi_kyuu + kagi_jyuu;
    }

    public static final String DONATE_1_SKU = "1_dollar";
    public static final String DONATE_2_SKU = "2_dollars";
    public static final String DONATE_5_SKU = "5_dollars";
    public static final String DONATE_10_SKU = "10_dollars";
    public static final String DONATE_15_SKU = "15_dollars";
}
