package com.example.nba.presentation.game

import com.example.nba.domain.model.Game

data class GamesUiState(
    val games: List<Game> = emptyList(),
    val isRefreshing: Boolean = false,
    val error: String? = null
)