package com.example.cycondlife;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Button;
import android.view.View;
import android.view.View.OnClickListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class Combat extends Activity {

    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.combat);
        addListenerOnButton();
    }
    public void addListenerOnButton() {

        final Context context = this;

        button = (Button) findViewById(R.id.button1);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(context, MapsActivity.class);
                startActivity(intent);

            }

        });

    }

}