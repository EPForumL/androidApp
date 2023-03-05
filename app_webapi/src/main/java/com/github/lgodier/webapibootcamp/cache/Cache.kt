package com.github.lgodier.webapibootcamp.cache

import androidx.room.*

@Entity
data class Activity(
    @PrimaryKey val aid: Int,
    @ColumnInfo(name = "description") val description: String?,
)

@Dao
interface ActivityDAO {

    @Query("SELECT COUNT(*) FROM activity")
    fun count() : Int

    @Insert
    fun insertAll(vararg users: Activity)

    @Query("SELECT * FROM activity WHERE aid = :actId")
    fun loadById(actId: Int): Activity

}

@Database(entities = [Activity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun activityDAO(): ActivityDAO
}