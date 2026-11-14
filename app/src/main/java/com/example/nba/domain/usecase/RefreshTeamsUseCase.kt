package com.example.nba.domain.usecase

import com.example.nba.domain.repository.TeamsRepository
import javax.inject.Inject

class RefreshTeamsUseCase @Inject constructor(private val repository: TeamsRepository) {
    suspend operator fun invoke() = repository.refreshTeams()
}
