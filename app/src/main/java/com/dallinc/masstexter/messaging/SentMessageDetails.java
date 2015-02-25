package com.dallinc.masstexter.messaging;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.dallinc.masstexter.R;
import com.dallinc.masstexter.helpers.TextDrawable;
import com.dallinc.masstexter.models.GroupMessage;
import com.dallinc.masstexter.models.SingleMessage;

import java.util.List;

public class SentMessageDetails extends ActionBarActivity {
    private GroupMessage groupMessage;
    private List<SingleMessage> singleMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_message_details);

        TextView title = (TextView) findViewById(R.id.sentMessageAtTitle);

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            final long message_id = bundle.getLong("message_id");
            groupMessage = GroupMessage.findById(GroupMessage.class, message_id);
            singleMessages = SingleMessage.find(SingleMessage.class, "group_message = ?", Long.toString(message_id));
            setupRecipientsList();
            groupMessage.buildArrayListFromString();
            title.setText(groupMessage.sentAt);
            styleEditText();
        }

    }

    private void setupRecipientsList() {
        ListView listView = (ListView) findViewById(R.id.messageRecipientsListView);
        final MessageListAdapter adapter = new MessageListAdapter(getBaseContext(), singleMessages);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = (String) parent.getItemAtPosition(position);

            }
        });
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void styleEditText() {
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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.single_message_item, parent, false);
            TextView nameView = (TextView) rowView.findViewById(R.id.recipientName);
            TextView numberView = (TextView) rowView.findViewById(R.id.recipientNumber);
            ImageView imageView = (ImageView) rowView.findViewById(R.id.thumb);

            nameView.setText(objects.get(position).contactName);
            numberView.setText(objects.get(position).phoneNumber);

            TextView sentAtLabel = (TextView) rowView.findViewById(R.id.singleMessageSentAt);
            if(objects.get(position).successfullySentAt == null) {
                sentAtLabel.setText(objects.get(position).deliveryAttempts + " failed attempts");
                sentAtLabel.setTextColor(Color.RED);
            } else {
                sentAtLabel.setText(objects.get(position).successfullySentAt);
            }

            TextView errorMessage = (TextView) rowView.findViewById(R.id.errorMessage);
            if(objects.get(position).failureMessage != null) {
                errorMessage.setText(objects.get(position).failureMessage);
                errorMessage.setTextColor(Color.RED);
            }

            // TODO: set phone number label
            TextView numberLabel = (TextView) rowView.findViewById(R.id.numberLabel);
//            numberLabel.setText(label);

            // TODO: fix bug where it crashes if they scroll back and forth a lot
            // OUT OF MEMORY error - it keeps resetting the image
            if(objects.get(position).photoUriString != null) {
                Uri test = Uri.parse(objects.get(position).photoUriString);
                imageView.setImageURI(test);
            }

            return rowView;
        }

        private Uri getContactUriFromPhoneNumber(String phoneNumber) {
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

            return uri;
        }
    }
}
