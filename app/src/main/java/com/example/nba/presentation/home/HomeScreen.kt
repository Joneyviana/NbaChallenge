package com.example.nba.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nba.domain.model.Team
import com.example.nba.presentation.components.ErrorContent
import com.example.nba.presentation.components.LoadingContent


@Composable
fun HomeScreen(
    snackbarHostState: SnackbarHostState,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var sortBy by remember { mutableStateOf("Name") }
    var showSortDialog by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf<Team?>(null) }

    LaunchedEffect(uiState.error) {
        if (uiState.teams.isNotEmpty()) {
            uiState.error?.let { error ->
                snackbarHostState.showSnackbar(error)
            }
        }
    }

    if (selectedTeam != null) {
        AlertDialog(
            onDismissRequest = { selectedTeam = null },
            title = {
                Text(selectedTeam!!.fullName)
            },
            text = {
                Text("Team details and paginated games will be loaded here.")
            },
            confirmButton = {
                TextButton(
                    onClick = { selectedTeam = null }
                ) {
                    Text("Back")
                }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Home",
                style = MaterialTheme.typography.headlineMedium
            )

            TextButton(
                modifier = Modifier.testTag("sort_button"),
                onClick = { showSortDialog = true }
            ) {
                Text(sortBy)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        ) {
            Text(
                "Name",
                Modifier.weight(2f),
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                "City",
                Modifier.weight(1.5f),
                style = MaterialTheme.typography.labelLarge
            )

            Text(
                "Conference",
                Modifier.weight(1.5f),
                style = MaterialTheme.typography.labelLarge
            )
        }

        HorizontalDivider()

        when {
            uiState.teams.isEmpty() && uiState.isRefreshing -> {
                LoadingContent()
            }

            uiState.teams.isEmpty() && uiState.error != null -> {
                ErrorContent(
                    message = uiState.error!!,
                    onRetry = viewModel::refresh
                )
            }

            else -> {
                TeamsList(
                    teams = uiState.teams,
                    onTeamClick = { selectedTeam = it }
                )
            }
        }
    }

    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            title = {
                Text("Order teams by")
            },
            text = {
                Column {
                    listOf("Name", "City", "Conference").forEach { option ->
                        TextButton(
                            modifier = Modifier.testTag("sort_option_${option.lowercase()}"),
                            onClick = {
                                sortBy = option

                                viewModel.onSortChanged(
                                    TeamSort.valueOf(option.uppercase())
                                )

                                showSortDialog = false
                            }
                        ) {
                            Text(option)
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun TeamsList(
    teams: List<Team>,
    onTeamClick: (Team) -> Unit
) {
    LazyColumn {
        items(teams) { team ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onTeamClick(team)
                    }
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    team.fullName,
                    Modifier.weight(2f)
                )

                Text(
                    team.city,
                    Modifier.weight(1.5f)
                )

                Text(
                    team.conference,
                    Modifier.weight(1.5f)
                )
            }

            HorizontalDivider()
        }
    }
}
