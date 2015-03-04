package com.dallinc.masstexter;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

import com.gc.materialdesign.views.ButtonRectangle;


public class Donate extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        ButtonRectangle donateButton = (ButtonRectangle) findViewById(R.id.buttonDonate);
        donateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
