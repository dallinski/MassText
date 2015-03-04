package com.dallinc.masstexter.messaging;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import android.widget.Toast;

import com.dallinc.masstexter.R;
import com.dallinc.masstexter.helpers.Constants;
import com.dallinc.masstexter.helpers.TextDrawable;
import com.dallinc.masstexter.models.GroupMessage;
import com.dallinc.masstexter.models.SingleMessage;

import java.util.List;

public class SentMessageDetails extends ActionBarActivity {
    private GroupMessage groupMessage;
    private List<SingleMessage> singleMessages;
    private BroadcastReceiver receiver;

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(Constants.BROADCAST_SMS_RESULT));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_message_details);

        TextView title = (TextView) findViewById(R.id.sentMessageAtTitle);

        Bundle bundle = getIntent().getExtras();
        if(bundle == null) {
            Toast.makeText(getBaseContext(), "Unexpected error. No message details found.", Toast.LENGTH_SHORT).show();
            finish();
        }

        final long message_id = bundle.getLong("message_id");
        groupMessage = GroupMessage.findById(GroupMessage.class, message_id);
        singleMessages = SingleMessage.find(SingleMessage.class, "group_message = ?", Long.toString(message_id));

        // Setup Recipients List
        ListView listView = (ListView) findViewById(R.id.messageRecipientsListView);
        final MessageListAdapter adapter = new MessageListAdapter(getBaseContext(), singleMessages);
        listView.setAdapter(adapter);

        groupMessage.buildArrayListFromString();
        title.setText(groupMessage.sentAt);
        styleMessageText();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String result = intent.getStringExtra(Constants.EXTRA_SEND_SMS_RESULT);
                long id = intent.getLongExtra(Constants.EXTRA_MESSAGE_ID, -1);
                int delay = intent.getIntExtra(Constants.EXTRA_DELAY_MILLIS, 0);
                if(result.equals("success")) {
                    Toast.makeText(getBaseContext(), "Successfully sent message!", Toast.LENGTH_SHORT).show();
                } else if(result.equals("failure") && delay >= 60000) {
                    // Only show the toast for the last failure (because there might be a ton)
                    Toast.makeText(getBaseContext(), "Delivery attempt: " + result, Toast.LENGTH_SHORT).show();
                }
                adapter.updateSingleMessage(id);
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sent_message_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        if (id == R.id.action_resend_to_all) {
//            return true;
//        } else if (id == R.id.action_resend_to_failed) {
//            return true;
//        } else if (id == R.id.action_send_new_to_all) {
//            return true;
//        } else if (id == R.id.action_send_this_message_to_others) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void styleMessageText() {
        TextView messageText = (TextView) findViewById(R.id.sentMessageBodyText);
        String template_text = groupMessage.messageBody;
        SpannableString spanText = new SpannableString(template_text);

        int starting_pos = 0;
        int variable_idx = 0;
        while(starting_pos != -1) {
            int idx = template_text.indexOf("¬", starting_pos);
            if(idx == -1) {
                break;
            }

            String variable = groupMessage.variables.get(variable_idx);

            Rect bounds = new Rect();
            Paint textPaint = messageText.getPaint();
            textPaint.getTextBounds(variable, 0, variable.length(), bounds);
            int width = bounds.width();

            TextDrawable d = new TextDrawable(this);
            d.setText(variable);
            d.setTextColor(getResources().getColor(R.color.colorAccent));
            d.setTextSize(14);
            d.setTextAlign(Layout.Alignment.ALIGN_CENTER);
            d.setBounds(3, 0, width+6, (int)(messageText.getTextSize()));

            spanText.setSpan(new ImageSpan(d, ImageSpan.ALIGN_BASELINE), idx, idx+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            starting_pos = idx+1;
            variable_idx++;
        }
        messageText.setText(spanText, TextView.BufferType.SPANNABLE);
    }

    private class MessageListAdapter extends ArrayAdapter<SingleMessage> {
        private final Context context;
        private final List<SingleMessage> objects;

        public MessageListAdapter(Context context, List<SingleMessage> objects) {
            super(context, R.layout.single_message_item, objects);
            this.context = context;
            this.objects = objects;
        }

        public void updateSingleMessage(long id) {
            for(int i=0; i<objects.size(); i++) {
                if(id == objects.get(i).getId()) {
                    updateSingleMessage(i);
                    return;
                }
            }
            Log.e(Constants.ERROR, "Failed to update UI for message with id: " + id);
        }

        private void updateSingleMessage(int i) {
            SingleMessage updatedMessage = SingleMessage.findById(SingleMessage.class, objects.get(i).getId());
            objects.set(i, updatedMessage);
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            final SingleMessage sentMessage = objects.get(position);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.single_message_item, parent, false);
            TextView nameView = (TextView) rowView.findViewById(R.id.recipientName);
            TextView numberView = (TextView) rowView.findViewById(R.id.recipientNumber);

            QuickContactBadge quickContactBadge = (QuickContactBadge) rowView.findViewById(R.id.contactBadge);
            quickContactBadge.assignContactFromPhone(sentMessage.phoneNumber, true);

            nameView.setText(sentMessage.contactName);
            numberView.setText(sentMessage.phoneNumber);

            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setTitle("Resend Message");
                    builder.setMessage("Would you like to resend this message to " + sentMessage.contactName + "?");
                    builder.setPositiveButton("Resend", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int n) {
                            sentMessage.sendMessage(v.getContext(), 1);
                            dialogInterface.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialogInterface, int n) {
                            dialogInterface.dismiss();
                        }
                    });
                    final AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            });

            TextView sentAtLabel = (TextView) rowView.findViewById(R.id.singleMessageSentAt);
            if(sentMessage.successfullySentAt == null) {
                if(sentMessage.failureMessage != null) {
                    sentAtLabel.setText(sentMessage.deliveryAttempts + " failed attempts");
                    sentAtLabel.setTextColor(Color.RED);
                } else {
                    sentAtLabel.setText("Pending delivery");
                }

            } else {
                sentAtLabel.setText(sentMessage.successfullySentAt);
            }

            TextView errorMessage = (TextView) rowView.findViewById(R.id.errorMessage);
            if(sentMessage.failureMessage != null) {
                errorMessage.setText(sentMessage.failureMessage);
                errorMessage.setTextColor(Color.RED);
            }

            // TODO: set phone number label
            TextView numberLabel = (TextView) rowView.findViewById(R.id.numberLabel);
//            numberLabel.setText(label);

            // TODO: fix bug where it crashes if they scroll back and forth a lot
            // OUT OF MEMORY error - it keeps resetting the image
            if(sentMessage.photoUriString != null) {
                Uri photoUri = Uri.parse(sentMessage.photoUriString);
                quickContactBadge.setImageURI(photoUri);
            }

            return rowView;
        }

        private Uri getContactUriFromPhoneNumber(String phoneNumber) {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            return uri;
        }
    }
}
