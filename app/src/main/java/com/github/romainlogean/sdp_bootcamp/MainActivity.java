package com.github.romainlogean.sdp_bootcamp;

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

        /*Button options = findViewById(R.id.optionsButton);
        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainerOptions, DisplayFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // whatever name ?
                        .commit();
            }
        });*/


        // add fragment to main view
        /*getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainerOptions, new DisplayFragment())
                .commit();*/
    }

    public void greet(View view){
        Intent intent = new Intent(view.getContext(), GreetingActivity.class);
        TextView nameTV = findViewById(R.id.mainName);
        String name = nameTV.getText().toString();
        intent.putExtra("NAME", name);
        view.getContext().startActivity(intent);
    }

    public void toggleOptions(View view){
        Intent intent = new Intent(view.getContext(), GreetingActivity.class);
        TextView nameTV = findViewById(R.id.mainName);
        String name = nameTV.getText().toString();
        intent.putExtra("NAME", name);
        view.getContext().startActivity(intent);
    }
}