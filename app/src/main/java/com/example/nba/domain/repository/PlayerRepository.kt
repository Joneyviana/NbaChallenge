package com.example.nba.domain.repository

import androidx.paging.PagingData
import com.example.nba.domain.model.Player
import kotlinx.coroutines.flow.Flow

interface PlayersRepository {

    fun getPlayers(
        search: String?
    ): Flow<PagingData<Player>>
}