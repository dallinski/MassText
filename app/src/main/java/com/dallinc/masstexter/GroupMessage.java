package com.dallinc.masstexter;

import com.orm.SugarRecord;

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
}
