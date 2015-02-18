package com.dallinc.masstexter.messaging;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.SystemClock;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class SendSMS extends IntentService {
    private static final String ACTION_SEND_SMS = "com.dallinc.masstexter.action.SEND_SMS";

    private static final String EXTRA_PHONE_NUMBER = "com.dallinc.masstexter.extra.PHONE_NUMBER";
    private static final String EXTRA_MESSAGE = "com.dallinc.masstexter.extra.MESSAGE";

    /**
     * Starts this service to perform action SendSMS with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSendSMS(Context context, String phoneNumber, String message) {
        Intent intent = new Intent(context, SendSMS.class);
        intent.setAction(ACTION_SEND_SMS);
        intent.putExtra(EXTRA_PHONE_NUMBER, phoneNumber);
        intent.putExtra(EXTRA_MESSAGE, message);
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
                final String phoneNumber = intent.getStringExtra(EXTRA_PHONE_NUMBER);
                final String message = intent.getStringExtra(EXTRA_MESSAGE);
                handleActionSendSMS(phoneNumber, message);
            }
        }
    }

    /**
     * Handle action SendSMS in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSendSMS(String phoneNumber, String message) {
        SystemClock.sleep(3000); // sleep for 3 seconds (Just for testing purposes)
        System.out.println(phoneNumber + ": " + message);
    }
}
