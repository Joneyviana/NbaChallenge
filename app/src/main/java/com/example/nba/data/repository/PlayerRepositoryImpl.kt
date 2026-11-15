package com.example.nba.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.example.nba.data.local.AppDatabase
import com.example.nba.data.mapper.toDomain
import com.example.nba.data.mapper.toEntity
import com.example.nba.data.remote.api.BallDontLieApi
import com.example.nba.data.remote.paging.PlayersRemoteMediator
import com.example.nba.domain.model.Player
import com.example.nba.domain.repository.PlayersRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


class PlayersRepositoryImpl @Inject constructor(
    private val database: AppDatabase,
    private val api: BallDontLieApi
) : PlayersRepository {

    private val playerDao = database.playerDao()

    override fun getPlayers(search: String?): Flow<PagingData<Player>> {
        return if (search.isNullOrBlank()) {
            getCachedPlayers()
        } else {
            searchPlayers(search)
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun getCachedPlayers(): Flow<PagingData<Player>> {
        return Pager(
            config = pagingConfig,
            remoteMediator = PlayersRemoteMediator(
                database = database,
                api = api
            ),
            pagingSourceFactory = {
                playerDao.getPlayers()
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    private fun searchPlayers(query: String): Flow<PagingData<Player>> = flow {
        val localPlayers = playerDao.searchPlayersLocally(query)

        if (localPlayers.isNotEmpty()) {
            emit(
                PagingData.from(
                    localPlayers.map { it.toDomain() }
                )
            )
            return@flow
        }

        val response = try {
            api.searchPlayers(
                query = query,
                cursor = null,
                perPage = PAGE_SIZE
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            emit(PagingData.empty())
            return@flow
        } catch (e: HttpException) {
            emit(PagingData.empty())
            return@flow
        }

        val players = response.data.map { it.toEntity() }

        database.withTransaction {
            playerDao.upsertAll(players)
        }

        emit(
            PagingData.from(
                players.map { it.toDomain() }
            )
        )
    }

    private companion object {
        const val PAGE_SIZE = 25

        val pagingConfig = PagingConfig(
            pageSize = PAGE_SIZE,
            initialLoadSize = PAGE_SIZE,
            enablePlaceholders = false
        )
    }
}