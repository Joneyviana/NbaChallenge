package com.example.nba.domain.repository

import com.example.nba.domain.model.Team
import kotlinx.coroutines.flow.Flow

interface TeamsRepository {
    fun observeTeams(): Flow<List<Team>>
    suspend fun refreshTeams()
}
