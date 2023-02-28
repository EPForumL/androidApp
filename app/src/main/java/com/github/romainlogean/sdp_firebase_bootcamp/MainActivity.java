package com.github.romainlogean.sdp_firebase_bootcamp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button get_button = (Button) findViewById(R.id.button_get);
        Button set_button = (Button) findViewById(R.id.button_set);

        get_button.setOnClickListener(this::get);
        set_button.setOnClickListener(this::set);
    }

    public void get(View view){
        String phone = ((TextView) findViewById(R.id.phone_textview)).getText().toString();

        CompletableFuture<String> future = new CompletableFuture<>();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

        db.child(phone).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    future.completeExceptionally(new NoSuchFieldException());
                } else {
                    future.complete(dataSnapshot.getValue(String.class));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                future.completeExceptionally(e);
            }
        });

        future.thenAccept(new Consumer<String>() {
            @Override
            public void accept(String s) {
                TextView emailView = (TextView) findViewById(R.id.email_textview);
                emailView.setText(s);
            }
        });

    }

    public void set(View view){
        String email = ((TextView) findViewById(R.id.email_textview)).getText().toString();
        String phone = ((TextView) findViewById(R.id.phone_textview)).getText().toString();

        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        db.child(phone).setValue(email);
    }

}