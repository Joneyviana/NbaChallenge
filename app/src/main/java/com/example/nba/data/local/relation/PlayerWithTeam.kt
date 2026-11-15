package com.example.nba.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.nba.data.local.entity.PlayerEntity
import com.example.nba.data.local.entity.TeamEntity

data class PlayerWithTeam(
    @Embedded
    val player: PlayerEntity,

    @Relation(
        parentColumn = "teamId",
        entityColumn = "id"
    )
    val team: TeamEntity
)