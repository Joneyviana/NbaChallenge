package com.example.nba.domain.usecase

import com.example.nba.domain.repository.GamesRepository
import javax.inject.Inject

class RefreshGamesUseCase @Inject constructor(
    private val repository: GamesRepository
) {

    suspend operator fun invoke(teamId: Int) {
        repository.refreshGames(teamId)
    }
}