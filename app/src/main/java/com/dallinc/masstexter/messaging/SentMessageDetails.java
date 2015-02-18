package com.dallinc.masstexter.messaging;

import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dallinc.masstexter.R;
import com.dallinc.masstexter.helpers.TextDrawable;
import com.dallinc.masstexter.models.GroupMessage;
import com.dallinc.masstexter.models.SingleMessage;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.Card;

import java.util.List;

public class SentMessageDetails extends ActionBarActivity {
    private GroupMessage groupMessage;
    private List<SingleMessage> singleMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sent_message_details);

        TextView title = (TextView) findViewById(R.id.sentMessageAtTitle);
        final Card messageCard = (Card) findViewById(R.id.sentMessageBodyCard);
        final ButtonFlat showMessage = (ButtonFlat) findViewById(R.id.showMessageText);

        messageCard.setVisibility(View.GONE);
        showMessage.setRippleSpeed(30);

        showMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageCard.setVisibility(View.VISIBLE);
                showMessage.setVisibility(View.GONE);
            }
        });

        messageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                messageCard.setVisibility(View.GONE);
                showMessage.setVisibility(View.VISIBLE);
            }
        });

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
        for(SingleMessage single : singleMessages) {
            System.out.println(single.phoneNumber);
        }
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
}
