package com.github.lgodier.webapibootcamp.cache

import androidx.room.*

@Database(entities = [Activity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDAO(): ActivityDAO
}