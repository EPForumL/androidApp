package com.github.romainlogean.sdp_bootcamp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = findViewById(R.id.mainGoButton);
        button.setOnClickListener(this::greet);
        Button button2 = findViewById(R.id.mapB);
        button2.setOnClickListener(this::map);
    }

    public void greet(View view){
        Intent intent = new Intent(view.getContext(), GreetingActivity.class);
        TextView nameTV = findViewById(R.id.mainName);
        String name = nameTV.getText().toString();
        intent.putExtra("NAME", name);
        view.getContext().startActivity(intent);
    }

    public void map(View view) {
        Intent intent = new Intent(view.getContext(), MainActivity3.class);
        view.getContext().startActivity(intent);
    }

}