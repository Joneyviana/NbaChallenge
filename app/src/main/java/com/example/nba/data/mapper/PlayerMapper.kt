package com.example.nba.data.mapper

import com.example.nba.data.remote.dto.PlayerDto
import com.example.nba.domain.model.Player

fun PlayerDto.toDomain() = Player(
    firstName = first_name,
    lastName = last_name,
    team = team.toEntity().toDomain()
)