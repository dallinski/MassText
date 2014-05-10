package com.dallinc.masstexter;

public class Template {
    String groupName;
    String message;

    public Template(String string, String string2) {
        this.groupName = string;
        this.message = string2;
    }

    public String message() {
        return this.message;
    }

    public String name() {
        return this.groupName;
    }
}

