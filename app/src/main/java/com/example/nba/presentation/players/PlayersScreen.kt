package com.example.nba.presentation.players

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.nba.presentation.components.EmptyContent
import com.example.nba.presentation.components.ErrorContent
import com.example.nba.presentation.components.LoadingContent
import com.example.nba.presentation.home.HomeViewModel

@Composable
fun PlayersScreen(viewModel: PlayerViewModel = hiltViewModel()) {
    val players = viewModel.players.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize()) {
        Text("Players", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            label = { Text("Search players") },
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        Text(
            if (query.isBlank()) "Search for an NBA player by name." else "Search will be connected to the API: $query",
            style = MaterialTheme.typography.bodyMedium
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (val refresh = players.loadState.refresh) {
                is LoadState.Loading -> {
                    item {
                        LoadingContent()
                    }
                }

                is LoadState.Error -> {
                    item {
                        ErrorContent(
                            message = "Não foi possível carregar os jogadores",
                            onRetry = players::retry
                        )
                    }
                }

                is LoadState.NotLoading -> {
                    if (players.itemCount == 0) {
                        item {
                            EmptyContent()
                        }
                    } else {
                        items(
                            count = players.itemCount
                        ) { index ->
                            players[index]?.let { player ->
                                PlayerItem(player)
                            }
                        }
                    }
                }
            }

            when (val append = players.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        LoadingContent()
                    }
                }

                is LoadState.Error -> {
                    item {
                        ErrorContent(
                            message = "Não foi possível carregar mais jogadores",
                            onRetry = players::retry
                        )
                    }
                }

                is LoadState.NotLoading -> Unit
            }
        }
    }
}