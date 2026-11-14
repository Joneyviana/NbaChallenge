package com.example.nba.presentation.home

import com.example.nba.domain.model.Team

data class HomeUiState(
    val teams: List<Team> = emptyList(),
    val sortBy: TeamSort = TeamSort.NAME,
    val isRefreshing: Boolean = false,
    val error: String? = null
)

enum class TeamSort {
    NAME,
    CITY,
    CONFERENCE
}