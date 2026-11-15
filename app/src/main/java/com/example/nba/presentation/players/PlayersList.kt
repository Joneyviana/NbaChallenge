package com.example.nba.presentation.players

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.nba.domain.model.Player
import com.example.nba.presentation.components.EmptyContent
import com.example.nba.presentation.components.ErrorContent
import com.example.nba.presentation.components.LoadingContent

@Composable
fun PlayersList(
    players: LazyPagingItems<Player>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp)
    ) {
        when {
            players.itemCount > 0 -> {
                items(
                    count = players.itemCount,
                    key = players.itemKey { it.id }
                ) { index ->
                    players[index]?.let { player ->
                        PlayerItem(player)
                    }
                }
            }

            players.loadState.refresh is LoadState.Loading -> {
                item {
                    LoadingContent()
                }
            }

            players.loadState.refresh is LoadState.Error -> {
                item {
                    ErrorContent(
                        message = "Não foi possível carregar os jogadores",
                        onRetry = players::retry
                    )
                }
            }

            else -> {
                item {
                    EmptyContent()
                }
            }
        }

        AppendLoadingItem(players)
    }
}

private fun LazyListScope.AppendLoadingItem(
    players: LazyPagingItems<Player>
) {
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
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Não foi possível carregar mais jogadores. Tentar novamente")
                }
            }
        }

        is LoadState.NotLoading -> Unit
    }
}