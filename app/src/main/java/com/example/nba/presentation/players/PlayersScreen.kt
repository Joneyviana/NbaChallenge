
package com.example.nba.presentation.players

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun PlayersScreen(
    viewModel: PlayerViewModel = hiltViewModel()
) {
    val players = viewModel.players.collectAsLazyPagingItems()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        PlayersHeader()

        PlayersSearchField(
            query = searchQuery,
            onQueryChange = viewModel::onSearchQueryChanged
        )

        PlayersList(
            players = players,
            modifier = Modifier.weight(1f)
        )
    }
}