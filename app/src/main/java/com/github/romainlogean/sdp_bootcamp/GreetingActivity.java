package com.github.romainlogean.sdp_bootcamp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GreetingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);

        String name = getIntent().getStringExtra("NAME");
        TextView greetingTextView = findViewById(R.id.greeting_text);
        greetingTextView.setText("Welcome in my app " + name + " !");
    }
}