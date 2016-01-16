package com.dallinc.masstext.helpers;

import java.util.Locale;

/**
 * Created by dallin on 2/6/15.
 */
public abstract class Constants {
    public static final int MESSAGING_FRAGMENT_POS = 0;
    public static final int TEMPLATES_FRAGMENT_POS = 1;

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

    public static boolean contains(String[] vars, String var, String[] englishVars) {
        for(String v : vars) {
            if(var.equals(v)) {
                return true;
            }
        }
        // Have to include this code to support any non-English locales that have templates saved
        // in English (from before the translation commit happened).
        if (!Locale.getDefault().getLanguage().equals("en"))
        {
            for(String v : englishVars) {
                if(var.equals(v)) {
                    return true;
                }
            }
        }
        return false;
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
