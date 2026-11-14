package com.example.nba.domain.model

data class Player(
    val firstName: String,
    val lastName: String,
    val team: Team
){
    fun getTeamName(): String {
        return team.fullName
    }
}