package com.github.lgodier.webapibootcamp.cache

import androidx.room.*

@Entity
data class Activity(
    @PrimaryKey val aid: Int,
    @ColumnInfo(name = "description") val description: String?,
)