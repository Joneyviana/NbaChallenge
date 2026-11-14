package com.example.nba.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teams")
data class TeamEntity(
    @PrimaryKey val id: Int,
    val fullName: String,
    val city: String,
    val conference: String,
    val name: String
)
