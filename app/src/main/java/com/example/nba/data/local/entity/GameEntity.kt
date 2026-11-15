package com.example.nba.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "games",
    indices = [Index("teamId")]
)
data class GameEntity(
    @PrimaryKey val id: Long,
    val teamId: Int,
    val date: String,
    val gameDate: LocalDate,
    val homeTeamName: String,
    val homeScore: Int,
    val visitorTeamName: String,
    val visitorScore: Int
)
