package com.ynov.spookify.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey val trackId: String,
    val playedAt: Long,
)
