package com.example.nba.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nba.domain.model.Team


@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    onTeamClick: (Team) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var sortBy by remember { mutableStateOf("Name") }
    var showSortDialog by remember { mutableStateOf(false) }

    HandleHomeError(
        error = uiState.error,
        hasTeams = uiState.teams.isNotEmpty(),
        snackbarHostState = snackbarHostState
    )

    Column(Modifier.fillMaxSize()) {
        HomeHeader(
            sortBy = sortBy,
            onSortClick = { showSortDialog = true }
        )

        TeamsTableHeader()

        HorizontalDivider()

        TeamsContent(
            uiState = uiState,
            onRetry = viewModel::refresh,
            onTeamClick = onTeamClick
        )
    }

    if (showSortDialog) {
        SortDialog(
            onDismiss = { showSortDialog = false },
            onSortSelected = { option ->
                sortBy = option

                viewModel.onSortChanged(
                    TeamSort.valueOf(option.uppercase())
                )

                showSortDialog = false
            }
        )
    }
}


@Composable
private fun HandleHomeError(
    error: String?,
    hasTeams: Boolean,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(error) {
        if (hasTeams) {
            error?.let {
                snackbarHostState.showSnackbar(it)
            }
        }
    }
}