package com.example.nba.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.nba.data.mapper.toDomain
import com.example.nba.data.remote.api.BallDontLieApi
import com.example.nba.domain.model.Player
import retrofit2.HttpException
import java.io.IOException


class PlayersPagingSource(
    private val api: BallDontLieApi,
    private val query: String
) : PagingSource<Long, Player>() {

    override suspend fun load(
        params: LoadParams<Long>
    ): LoadResult<Long, Player> {
        return try {
            val response = api.searchPlayers(
                query = query,
                cursor = params.key,
                perPage = params.loadSize
            )

            LoadResult.Page(
                data = response.data.map { it.toDomain() },
                prevKey = null,
                nextKey = response.meta?.next_cursor
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(
        state: PagingState<Long, Player>
    ): Long? = null
}