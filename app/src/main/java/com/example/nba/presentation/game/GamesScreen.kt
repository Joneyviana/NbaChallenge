package com.example.nba.presentation.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.nba.domain.model.Game
import com.example.nba.presentation.components.EmptyContent
import com.example.nba.presentation.components.ErrorContent
import com.example.nba.presentation.components.LoadingContent


@Composable
fun GameScreen(
    teamName: String,
    onBackClick: () -> Unit,
    viewModel: GamesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = onBackClick) {
                Text("< Home")
            }

            Text(
                text = teamName,
                style = MaterialTheme.typography.titleLarge
            )
        }

        when {
            uiState.games.isEmpty() && uiState.isRefreshing -> {
                LoadingContent(
                    modifier = Modifier.fillMaxSize()
                )
            }

            uiState.games.isEmpty() && uiState.error != null -> {
                ErrorContent(
                    message = uiState.error!!,
                    onRetry = viewModel::refresh,
                    modifier = Modifier.fillMaxSize()
                )
            }

            uiState.games.isEmpty() -> {
                EmptyContent(
                    message = "Nenhum jogo encontrado",
                    modifier = Modifier.fillMaxSize()
                )
            }

            else -> {
                GamesList(
                    games = uiState.games,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}