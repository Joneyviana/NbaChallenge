package com.example.nba.data.remote.dto

data class GameDto(
    val id: Long,
    val date: String,
    val home_team_score: Int,
    val visitor_team_score: Int,
    val home_team: TeamSummaryDto,
    val visitor_team: TeamSummaryDto
)

data class TeamSummaryDto(
    val id: Int,
    val full_name: String
)
