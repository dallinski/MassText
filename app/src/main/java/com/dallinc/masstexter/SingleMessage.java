package com.dallinc.masstexter;

import com.orm.SugarRecord;

/**
 * Created by dallin on 2/7/15.
 */
public class SingleMessage extends SugarRecord<SingleMessage> {
    String phoneNumber;
    int deliveryAttempts;
    String successfullySentAt;
    GroupMessage groupMessage;

    public SingleMessage() {

    }

    public SingleMessage(String number, GroupMessage group) {
        phoneNumber = number;
        groupMessage = group;
        deliveryAttempts = 0;
    }

    private void sendMessage() {
        deliveryAttempts++;
    }
}
