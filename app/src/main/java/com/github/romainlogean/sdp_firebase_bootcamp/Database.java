package com.github.romainlogean.sdp_firebase_bootcamp;

import java.util.concurrent.CompletableFuture;

public abstract class Database {
    public abstract CompletableFuture<String> get(String key);
    public abstract void set(String key, String value);
}
