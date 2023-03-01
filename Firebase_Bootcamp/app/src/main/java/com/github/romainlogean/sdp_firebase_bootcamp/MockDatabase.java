package com.github.romainlogean.sdp_firebase_bootcamp;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class MockDatabase extends Database{

    private Map<String, String> db;

    public MockDatabase(){
        db = new HashMap<>();
    }

    @Override
    public CompletableFuture<String> get(String key) {
        String value = db.get(key);
        if (value == null) {
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(new NoSuchFieldException());
            return future;
        } else {
            return CompletableFuture.completedFuture(value);
        }
    }

    @Override
    public void set(String key, String val){
        db.put(key,val);
    }
}
