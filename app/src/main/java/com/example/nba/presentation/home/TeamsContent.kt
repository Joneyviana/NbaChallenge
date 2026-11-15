package com.example.nba.presentation.home

import androidx.compose.runtime.Composable
import com.example.nba.domain.model.Team
import com.example.nba.presentation.components.ErrorContent
import com.example.nba.presentation.components.LoadingContent

@Composable
fun TeamsContent(
    uiState: HomeUiState,
    onRetry: () -> Unit,
    onTeamClick: (Team) -> Unit
) {
    when {
        uiState.teams.isEmpty() && uiState.isRefreshing -> {
            LoadingContent()
        }

        uiState.teams.isEmpty() && uiState.error != null -> {
            ErrorContent(
                message = uiState.error,
                onRetry = onRetry
            )
        }

        else -> {
            TeamsList(
                teams = uiState.teams,
                onTeamClick = onTeamClick
            )
        }
    }
}