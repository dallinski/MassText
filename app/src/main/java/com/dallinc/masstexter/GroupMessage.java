package com.dallinc.masstexter;

import com.orm.SugarRecord;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;

/**
 * Created by dallin on 2/7/15.
 */
public class GroupMessage extends SugarRecord<GroupMessage> {
    String sentAt;
    String messageBody;

    public GroupMessage() {

    }

    public GroupMessage(String datetime, String body) {
        sentAt = datetime;
        messageBody = body;
    }

    public GroupMessage(String body) {
        sentAt = DateTime.now().toString();
        messageBody = body;
    }
}
