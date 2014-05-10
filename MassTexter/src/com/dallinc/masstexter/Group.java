package com.dallinc.masstexter;

import com.dallinc.masstexter.CustomContact;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.net.Uri;

public class Group {
    ArrayList<CustomContact> contacts;
    String groupName;

    public Group(String string) {
        this.groupName = string;
        this.contacts = new ArrayList();
    }

    public void add(String string, String string2) {
        this.contacts.add((Object)(new CustomContact(string, string2)));
        Collections.sort(this.contacts, (Comparator)(new customComparator()));
    }

    public void clear() {
        this.contacts.clear();
    }

    public CustomContact get(int n) {
        return (CustomContact)(this.contacts.get(n));
    }

    public String name() {
        return this.groupName;
    }

    public String nameAt(int n) {
        return (CustomContact)(this.contacts.get(n)).name();
    }

    public String numberAt(int n) {
        return (CustomContact)(this.contacts.get(n)).number();
    }

    public void remove(int n) {
        this.contacts.remove(n);
        Collections.sort(this.contacts, (Comparator)(new customComparator()));
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     */
    public void remove(String string) {
        int n = -1;
        for (int i = 0; i < this.contacts.size(); ++i) {
            if (!((CustomContact)(this.contacts.get(i)).number().equals((Object)(string)))) continue;
            n = i;
            break;
        }
        this.contacts.remove(n);
        Collections.sort(this.contacts, (Comparator)(new customComparator()));
    }

    public int size() {
        return this.contacts.size();
    }

    public String toString() {
        String string = "";
        int n = 0;
        while (n < this.contacts.size()) {
            string = (String.valueOf((Object)(string)) + (CustomContact)(this.contacts.get(n)).number() + ", ");
            ++n;
        }
        return string.substring(0, (-2 + string.length()));
    }

    class customComparator
    implements Comparator<CustomContact> {
        customComparator() {
        }

        public int compare(CustomContact customContact, CustomContact customContact2) {
            return customContact.name().compareTo(customContact2.name());
        }
    }

}

