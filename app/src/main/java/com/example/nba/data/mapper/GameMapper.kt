package com.example.nba.data.mapper

import com.example.nba.data.local.entity.GameEntity
import com.example.nba.data.remote.dto.GameDto
import com.example.nba.domain.model.Game

fun GameEntity.toDomain() = Game(
    id = id,
    homeTeamName = homeTeamName,
    homeTeamScore = homeTeamScore,
    visitorTeamName = visitorTeamName,
    visitorTeamScore = visitorTeamScore
)

fun GameDto.toEntity(teamId: Int) = GameEntity(
    id = id,
    teamId = teamId,
    homeTeamName = home_team.full_name,
    homeTeamScore = home_team_score,
    visitorTeamName = visitor_team.full_name,
    visitorTeamScore = visitor_team_score
)