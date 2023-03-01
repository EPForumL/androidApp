package com.github.romainlogean.sdp_firebase_bootcamp;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.CompletableFuture;

public class FirebaseDatabaseAdapter extends Database {

    private final DatabaseReference databaseReference;

    public FirebaseDatabaseAdapter() {
        this.databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    public CompletableFuture<String> get(String key) {
        CompletableFuture<String> future = new CompletableFuture<>();

        databaseReference.child(key).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
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

        return future;
    }

    @Override
    public void set(String key, String value) {
        databaseReference.child(key).setValue(value);
    }
}
