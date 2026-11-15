package com.example.nba.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_remote_keys")
data class PlayerRemoteKeys(
    @PrimaryKey
    val id: Int = 1,
    val nextCursor: Long?
)