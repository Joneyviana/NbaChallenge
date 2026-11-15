package com.example.nba.data.mapper

import com.example.nba.data.local.entity.PlayerEntity
import com.example.nba.data.remote.dto.PlayerDto
import com.example.nba.domain.model.Player

fun PlayerDto.toDomain() = Player(
    id = id,
    firstName = first_name,
    lastName = last_name,
    teamFullName = team.full_name
)

fun PlayerDto.toEntity() = PlayerEntity(
    id = id,
    firstName = first_name,
    lastName = last_name,
    teamId = team.id,
    teamFullName = team.full_name
)


fun PlayerEntity.toDomain(): Player {
    return Player(
        id = id,
        firstName = firstName,
        lastName = lastName,
        teamFullName = teamFullName
    )
}