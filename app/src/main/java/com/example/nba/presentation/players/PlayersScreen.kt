
package com.example.nba.presentation.players

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.example.nba.presentation.components.EmptyContent
import com.example.nba.presentation.components.ErrorContent
import com.example.nba.presentation.components.LoadingContent

@Composable
fun PlayersScreen(viewModel: PlayerViewModel = hiltViewModel()) {
    val players = viewModel.players.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(Modifier.fillMaxSize()) {
        Text(
            "Players",
            style = MaterialTheme.typography.headlineMedium,
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                viewModel.onSearchQueryChanged(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            label = { Text("Search players") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search,
                autoCorrectEnabled = false,
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                },
            ),
        )

        Spacer(Modifier.height(12.dp))

        Text(
            if (searchQuery.isBlank()) {
                "Search for an NBA player by name."
            } else {
                "Searching for: $searchQuery"
            },
            style = MaterialTheme.typography.bodyMedium,
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            when {
                // Prioriza os dados disponíveis no Room
                players.itemCount > 0 -> {
                    items(
                        count = players.itemCount,
                        key = players.itemKey { it.id },
                    ) { index ->
                        players[index]?.let { player ->
                            PlayerItem(player)
                        }
                    }
                }

                // Sem dados e carregando
                players.loadState.refresh is LoadState.Loading -> {
                    item {
                        LoadingContent()
                    }
                }

                // Sem dados e com erro
                players.loadState.refresh is LoadState.Error -> {
                    item {
                        ErrorContent(
                            message = "Não foi possível carregar os jogadores",
                            onRetry = players::retry,
                        )
                    }
                }

                // Sem dados e sem erro
                else -> {
                    item {
                        EmptyContent()
                    }
                }
            }

            // Carregamento da próxima página
            when (players.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        LoadingContent()
                    }
                }

                is LoadState.Error -> {
                    item {
                        TextButton(
                            onClick = players::retry,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Text(
                                "Não foi possível carregar mais jogadores. Tentar novamente",
                            )
                        }
                    }
                }

                is LoadState.NotLoading -> Unit
            }
        }
    }
}
