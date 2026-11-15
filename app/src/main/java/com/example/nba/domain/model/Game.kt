package com.example.nba.domain.model

import java.time.LocalDate

data class Game(
    val id: Long,
    val gameDate: LocalDate,
    val homeTeamName: String,
    val homeTeamScore: Int?,
    val visitorTeamName: String,
    val visitorTeamScore: Int?
)