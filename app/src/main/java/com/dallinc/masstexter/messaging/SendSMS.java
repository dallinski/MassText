package com.dallinc.masstexter.messaging;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.telephony.SmsManager;
import android.util.Log;

import com.dallinc.masstexter.models.SingleMessage;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class SendSMS extends IntentService {
    private final String LOG_TAG = "SendSMS";
    private final String SENT = "SMS_SENT";
    private final String DELIVERED = "SMS_DELIVERED";
    private final String ERROR = "ERROR";
    private static final String ACTION_SEND_SMS = "com.dallinc.masstexter.action.SEND_SMS";

    private static final String EXTRA_MESSAGE_ID= "com.dallinc.masstexter.extra.MESSAGE_ID";
    private static final String EXTRA_PERSONALIZED_MESSAGE = "com.dallinc.masstexter.extra.PERSONALIZED_MESSAGE";

    /**
     * Starts this service to perform action SendSMS with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSendSMS(Context context, long messageId) {
        Intent intent = new Intent(context, SendSMS.class);
        intent.setAction(ACTION_SEND_SMS);
        intent.putExtra(EXTRA_MESSAGE_ID, messageId);
        context.startService(intent);
    }

    public SendSMS() {
        super("SendSMS");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND_SMS.equals(action)) {
                long messageId = intent.getLongExtra(EXTRA_MESSAGE_ID, -1);
                SingleMessage singleMessage = SingleMessage.findById(SingleMessage.class, messageId);
                if(singleMessage != null) {
                    handleActionSendSMS(singleMessage);
                } else {
                    Log.e(ERROR, "Couldn't find the message with id: " + messageId);
                }
            }
        }
    }

    /**
     * Handle action SendSMS in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSendSMS(SingleMessage singleMessage) {
        SystemClock.sleep(1000); // sleep for 1 second (in an attempt to not overload the port)

//        http://stackoverflow.com/questions/16643391/how-to-check-for-successful-multi-part-sms-send
        sendLongSmsMessage4(getApplicationContext(), singleMessage);
    }

    private void sendLongSmsMessage4(Context context, final SingleMessage singleMessage) {
        SmsManager smsManager = SmsManager.getDefault();
        final ArrayList<String> messageParts = smsManager.divideMessage(singleMessage.individualizedMessage());
        ArrayList<PendingIntent> pendingIntents = new ArrayList<PendingIntent>(messageParts.size());

        // Receive when each part of the SMS has been sent
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            int nMsgParts = messageParts.size();
            @Override
            public void onReceive(Context context, Intent intent) {
                // We need to make all the parts succeed before we say we have succeeded.
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        singleMessage.fail("Error - Generic failure");
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        singleMessage.fail("Error - No Service");
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        singleMessage.fail("Error - Null PDU");
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        singleMessage.fail("Error - Radio off");
                        break;
                }

                nMsgParts--;
                if (nMsgParts <= 0) {
                    // Stop us from getting any other broadcasts (may be for other messages)
                    Log.i(LOG_TAG, "All message part resoponses received, unregistering message Id: " + singleMessage.getId());
                    context.unregisterReceiver(this);

                    if (singleMessage.isFailed()) {
                        Log.d(LOG_TAG, "SMS Failure for message id: " + singleMessage.getId());
                    } else {
                        Log.d(LOG_TAG, "SMS Success for message id: " + singleMessage.getId());
                        singleMessage.setAsSent();
                    }
                }
            }
        };

        context.registerReceiver(broadcastReceiver, new IntentFilter(SENT + singleMessage.getId()));

        for (int i = 0; i < messageParts.size(); i++) {
            Intent sentIntent = new Intent(SENT + singleMessage.getId());
            pendingIntents.add(PendingIntent.getBroadcast(context, 0, sentIntent, 0));
        }

        Log.i(LOG_TAG, "About to send multi-part message Id: " + singleMessage.getId());
        smsManager.sendMultipartTextMessage(singleMessage.phoneNumber, null, messageParts, pendingIntents, null);
    }
}
