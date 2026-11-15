package com.example.nba.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey
    val id: Int,
    val firstName: String,
    val lastName: String,
    val teamId: Int,
    val teamFullName: String
)