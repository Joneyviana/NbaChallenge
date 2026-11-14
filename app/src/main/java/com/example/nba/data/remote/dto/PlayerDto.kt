package com.example.nba.data.remote.dto

data class PlayerDto(
    val id: Int,
    val first_name: String,
    val last_name: String,
    val team: TeamSummaryDto
)
