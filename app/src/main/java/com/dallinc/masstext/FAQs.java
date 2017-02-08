package com.dallinc.masstext;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;


public class FAQs extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqs);

        TextView emailView = (TextView) findViewById(R.id.emailLink);
        emailView.setLinksClickable(true);
        emailView.setText(Html.fromHtml("<a href=\"mailto:dallin.christensen+masstext2@gmail.com\">Email me (Dallin) directly</a>"));
        emailView.setMovementMethod(LinkMovementMethod.getInstance());

        TextView privacyPolicyLink = (TextView) findViewById(R.id.privacyPolicyLink);
        privacyPolicyLink.setLinksClickable(true);
        privacyPolicyLink.setText(Html.fromHtml("<a href=\"https://plus.google.com/+DallinChristensen1/posts/L5xXSRyktNA\">Click here to see it</a>"));
        privacyPolicyLink.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
