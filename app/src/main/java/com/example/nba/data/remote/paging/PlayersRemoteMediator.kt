package com.example.nba.data.remote.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.nba.data.local.AppDatabase
import com.example.nba.data.local.entity.PlayerEntity
import com.example.nba.data.local.entity.PlayerRemoteKeys
import com.example.nba.data.mapper.toEntity
import com.example.nba.data.remote.api.BallDontLieApi
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class PlayersRemoteMediator(
    private val database: AppDatabase,
    private val api: BallDontLieApi
) : RemoteMediator<Int, PlayerEntity>() {

    private val playerDao = database.playerDao()
    private val remoteKeysDao = database.playerRemoteKeysDao()

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PlayerEntity>
    ): MediatorResult {

        return try {
            val cursor = when (loadType) {
                LoadType.REFRESH -> null

                LoadType.PREPEND -> {
                    return MediatorResult.Success(
                        endOfPaginationReached = true
                    )
                }

                LoadType.APPEND -> {
                    val remoteKeys = remoteKeysDao.getRemoteKeys()

                    remoteKeys?.nextCursor
                        ?: return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                }
            }

            val response = api.searchPlayers(
                query = "",
                cursor = cursor,
                perPage = state.config.pageSize
            )

            val players = response.data.map { it.toEntity() }
            val nextCursor = response.meta?.next_cursor

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    remoteKeysDao.clear()
                }

                playerDao.upsertAll(players)

                remoteKeysDao.upsert(
                    PlayerRemoteKeys(
                        nextCursor = nextCursor
                    )
                )
            }

            MediatorResult.Success(
                endOfPaginationReached = nextCursor == null
            )

        } catch (exception: IOException) {
            MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            MediatorResult.Error(exception)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return if (playerDao.countPlayers() == 0) {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }
}