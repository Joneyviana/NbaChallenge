package com.example.nba.data.mapper

import com.example.nba.data.local.entity.GameEntity
import com.example.nba.data.remote.dto.GameDto
import com.example.nba.domain.model.Game
import java.time.LocalDate

fun GameEntity.toDomain() = Game(
    id = id,
    homeTeamName = homeTeamName,
    homeTeamScore = homeScore,
    visitorTeamName = visitorTeamName,
    visitorTeamScore = visitorScore,
    gameDate = gameDate
)

fun GameDto.toEntity(teamId: Int) = GameEntity(
    id = id,
    teamId = teamId,
    homeTeamName = home_team.full_name,
    homeScore = home_team_score,
    visitorTeamName = visitor_team.full_name,
    visitorScore = visitor_team_score,
    date = date,
    gameDate = LocalDate.parse(date.substringBefore("T"))
)