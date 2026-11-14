package com.example.nba.domain.usecase

import com.example.nba.domain.model.Team
import com.example.nba.domain.repository.TeamsRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObserveTeamsUseCase @Inject constructor(
    private val repository: TeamsRepository
) {

    operator fun invoke(): Flow<List<Team>> {
        return repository.observeTeams()
    }
}