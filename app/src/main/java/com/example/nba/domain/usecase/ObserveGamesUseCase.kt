package com.example.nba.domain.usecase

import com.example.nba.domain.model.Game
import com.example.nba.domain.repository.GamesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveGamesUseCase @Inject constructor(
    private val repository: GamesRepository
) {

    operator fun invoke(teamId: Int): Flow<List<Game>> {
        return repository.observeGames(teamId)
    }
}