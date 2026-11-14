package com.example.nba.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "games")
data class GameEntity(
    @PrimaryKey val id: Long,
    val teamId: Int,
    val date: String,
    val homeTeamName: String,
    val homeScore: Int,
    val visitorTeamName: String,
    val visitorScore: Int
)
