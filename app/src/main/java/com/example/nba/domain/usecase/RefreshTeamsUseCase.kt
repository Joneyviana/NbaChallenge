package com.example.nba.domain.usecase

import com.example.nba.domain.repository.TeamsRepository

class RefreshTeamsUseCase(private val repository: TeamsRepository) {
    suspend operator fun invoke() = repository.refreshTeams()
}
