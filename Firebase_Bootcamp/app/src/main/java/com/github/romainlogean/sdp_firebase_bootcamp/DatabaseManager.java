package com.github.romainlogean.sdp_firebase_bootcamp;

public class DatabaseManager {
    private static Database db = new FirebaseDatabaseAdapter();

    public static Database getDatabase() {
        return db;
    }

    public static void useMockDatabase() {
        db = new MockDatabase();
    }
}
