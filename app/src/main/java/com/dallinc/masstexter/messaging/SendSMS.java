package com.dallinc.masstexter.messaging;

import android.app.Activity;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsManager;
import android.util.Log;

import com.dallinc.masstexter.helpers.Constants;
import com.dallinc.masstexter.models.SingleMessage;

import java.util.ArrayList;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class SendSMS extends IntentService {
    private LocalBroadcastManager broadcaster;

    @Override
    public void onCreate() {
        super.onCreate();
        broadcaster = LocalBroadcastManager.getInstance(this);
    }

    public void sendResult(long id, String message, int delay) {
        Intent intent = new Intent(Constants.BROADCAST_SMS_RESULT);
        intent.putExtra(Constants.EXTRA_MESSAGE_ID, id);
        intent.putExtra(Constants.EXTRA_SEND_SMS_RESULT, message);
        intent.putExtra(Constants.EXTRA_DELAY_MILLIS, delay);
        broadcaster.sendBroadcast(intent);
    }

    /**
     * Starts this service to perform action SendSMS with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSendSMS(Context context, long messageId, int delay) {
        Intent intent = new Intent(context, SendSMS.class);
        intent.setAction(Constants.ACTION_SEND_SMS);
        intent.putExtra(Constants.EXTRA_MESSAGE_ID, messageId);
        intent.putExtra(Constants.EXTRA_DELAY_MILLIS, delay);
        context.startService(intent);
    }

    public SendSMS() {
        super("SendSMS");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (Constants.ACTION_SEND_SMS.equals(action)) {
                long messageId = intent.getLongExtra(Constants.EXTRA_MESSAGE_ID, -1);
                int delay = intent.getIntExtra(Constants.EXTRA_DELAY_MILLIS, 1);
                SingleMessage singleMessage = SingleMessage.findById(SingleMessage.class, messageId);
                if(singleMessage != null) {
                    handleActionSendSMS(singleMessage, delay);
                } else {
                    Log.e(Constants.ERROR, "Couldn't find the message with id: " + messageId);
                }
            }
        }
    }

    /**
     * Handle action SendSMS in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSendSMS(SingleMessage singleMessage, int delay) {
//        http://stackoverflow.com/questions/16643391/how-to-check-for-successful-multi-part-sms-send
        sendLongSmsMessage4(getApplicationContext(), singleMessage, delay);
    }

    private void sendLongSmsMessage4(Context context, final SingleMessage singleMessage, final int delay) {
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
                        singleMessage.clearFailureMessage();
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
                    Log.i("SendSMS", "All message part responses received, unregistering message Id: " + singleMessage.getId());
                    context.unregisterReceiver(this);

                    if (singleMessage.isFailed()) {
                        Log.d("SendSMS", "SMS Failure for message id: " + singleMessage.getId());
                        sendResult(singleMessage.getId(), "failure", delay);
                        if(delay < 60000) { // retry sending until the delay is greater than a minute
                            SystemClock.sleep(500 + delay);
                            int newDelay = 2 * delay; // double the length of the delay each failure
                            singleMessage.sendMessage(context, newDelay);
                        }
                    } else {
                        Log.d("SendSMS", "SMS Success for message id: " + singleMessage.getId());
                        singleMessage.setAsSent();
                        sendResult(singleMessage.getId(), "success", 0);
                    }
                }
            }
        };

        context.registerReceiver(broadcastReceiver, new IntentFilter(Constants.SENT + singleMessage.getId()));

        for (int i = 0; i < messageParts.size(); i++) {
            Intent sentIntent = new Intent(Constants.SENT + singleMessage.getId());
            pendingIntents.add(PendingIntent.getBroadcast(context, 0, sentIntent, 0));
        }

        Log.i("SendSMS", "About to send multi-part message Id: " + singleMessage.getId());
        smsManager.sendMultipartTextMessage(singleMessage.phoneNumber, null, messageParts, pendingIntents, null);
    }
}
