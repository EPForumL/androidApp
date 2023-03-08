package com.github.lgodier.webapibootcamp.cache

import androidx.room.*

@Dao
interface ActivityDAO {

    @Query("SELECT COUNT(*) FROM activity")
    fun count() : Int

    @Insert
    fun insertAll(vararg users: Activity)

    @Query("SELECT * FROM activity WHERE aid = :actId")
    fun loadById(actId: Int): Activity

}