package com.example.nba.domain.repository

import com.example.nba.domain.model.Game
import kotlinx.coroutines.flow.Flow

interface GamesRepository {

    fun observeGames(teamId: Int): Flow<List<Game>>

    suspend fun refreshGames(teamId: Int)
}