package com.example.nba.data.repository

import com.example.nba.data.local.dao.GameDao
import com.example.nba.data.mapper.toDomain
import com.example.nba.data.mapper.toEntity
import com.example.nba.data.remote.api.BallDontLieApi
import com.example.nba.domain.model.Game
import com.example.nba.domain.repository.GamesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GamesRepositoryImpl @Inject constructor(
    private val api: BallDontLieApi,
    private val dao: GameDao
) : GamesRepository {

    override fun observeGames(teamId: Int): Flow<List<Game>> =
        dao.observeGames(teamId).map { games ->
            games.map { it.toDomain() }
        }

    override suspend fun refreshGames(teamId: Int) {
        val remoteGames = api.getGames(
            teamId,
            cursor = null,
            perPage = PAGE_SIZE
        ).data

        dao.upsertAll(
            remoteGames.map { it.toEntity(teamId) }
        )
    }

    private companion object {
        const val PAGE_SIZE = 25
    }
}