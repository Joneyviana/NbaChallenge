package com.example.nba.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.nba.data.remote.api.BallDontLieApi
import com.example.nba.data.remote.paging.PlayersPagingSource
import com.example.nba.domain.model.Player
import com.example.nba.domain.repository.PlayersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PlayersRepositoryImpl @Inject constructor(
    private val api: BallDontLieApi
) : PlayersRepository {

    override fun getPlayers(search: String?): Flow<PagingData<Player>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                initialLoadSize = PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                PlayersPagingSource(
                    api = api,
                    query = search.orEmpty()
                )
            }
        ).flow
    }

    private companion object {
        const val PAGE_SIZE = 25
    }
}