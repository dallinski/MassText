package com.dallinc.masstexter;

/**
 * Created by dallin on 2/6/15.
 */
public abstract class Constants {
    public static final int MESSAGING_FRAGMENT_POS = 0;
    public static final int TEMPLATES_FRAGMENT_POS = 1;
    public static final int ABOUT_FRAGMENT_POS = 2;
    public static final String[] VARIABLE_OPTIONS = new String[]{"date", "day of the week", "time", "first name", "last name", "full name", "custom variable"};
    public static final String[] AUTO_VARIABLES = new String[]{"first name", "last name", "full name"};

    public static boolean contains(String[] vars, String var) {
        for(String v : vars) {
            if(var.equals(v)) {
                return true;
            }
        }
        return false;
    }
}
