package com.example.nba.data.repository

import com.example.nba.data.local.dao.TeamDao
import com.example.nba.data.mapper.toDomain
import com.example.nba.data.mapper.toEntity
import com.example.nba.data.remote.api.BallDontLieApi
import com.example.nba.domain.model.Team
import com.example.nba.domain.repository.TeamsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TeamsRepositoryImpl(
    private val api: BallDontLieApi,
    private val dao: TeamDao
) : TeamsRepository {
    override fun observeTeams(): Flow<List<Team>> =
        dao.observeTeams().map { teams -> teams.map { it.toDomain() } }

    override suspend fun refreshTeams() {
        val remoteTeams = api.getTeams().data
        dao.upsertAll(remoteTeams.map { it.toEntity() })
    }
}
