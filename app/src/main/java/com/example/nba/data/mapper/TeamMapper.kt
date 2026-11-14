package com.example.nba.data.mapper

import com.example.nba.data.local.entity.TeamEntity
import com.example.nba.data.remote.dto.TeamDto
import com.example.nba.domain.model.Team

fun TeamDto.toEntity() = TeamEntity(
    id = id,
    fullName = full_name,
    city = city,
    conference = conference,
    name = name
)

fun TeamEntity.toDomain() = Team(
    id = id,
    fullName = fullName,
    city = city,
    conference = conference
)
