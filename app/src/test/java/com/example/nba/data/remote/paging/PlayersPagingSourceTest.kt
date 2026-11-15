package com.example.nba.data.remote.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.nba.data.remote.api.BallDontLieApi
import com.example.nba.data.remote.dto.ApiPage
import com.example.nba.data.remote.dto.PageMeta
import com.example.nba.data.remote.dto.PlayerDto
import com.example.nba.data.remote.dto.TeamDto
import com.example.nba.domain.model.Player
import com.example.nba.fixtures.PlayerTestFixtures.createPlayerDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import retrofit2.HttpException
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class PlayersPagingSourceTest {

    private lateinit var api: BallDontLieApi
    private lateinit var pagingSource: PlayersPagingSource

    private val query = "LeBron"
    private val pageSize = 25

    @BeforeEach
    fun setup() {
        api = mockk()
        pagingSource = PlayersPagingSource(
            api = api,
            query = query
        )
    }

    @Test
    fun `should load first page successfully`() = runTest {
        coEvery {
            api.searchPlayers(
                query = query,
                cursor = null,
                perPage = pageSize
            )
        } returns createPlayerResponse(nextCursor = 100L)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)

        result as PagingSource.LoadResult.Page

        assertEquals(1, result.data.size)
        assertEquals(100L, result.nextKey)
        assertNull(result.prevKey)

        coVerify(exactly = 1) {
            api.searchPlayers(query, null, pageSize)
        }
    }

    @Test
    fun `should use cursor when loading next page`() = runTest {
        coEvery {
            api.searchPlayers(
                query = query,
                cursor = 100L,
                perPage = pageSize
            )
        } returns createPlayerResponse(nextCursor = 200L)

        val result = pagingSource.load(
            PagingSource.LoadParams.Append(
                key = 100L,
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)

        result as PagingSource.LoadResult.Page

        assertEquals(200L, result.nextKey)

        coVerify(exactly = 1) {
            api.searchPlayers(query, 100L, pageSize)
        }
    }

    @Test
    fun `should return null next key when there are no more pages`() = runTest {
        coEvery {
            api.searchPlayers(query, null, pageSize)
        } returns createPlayerResponse(nextCursor = null)

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Page)

        result as PagingSource.LoadResult.Page

        assertNull(result.nextKey)
    }

    @Test
    fun `should return error when api throws IOException`() = runTest {
        val exception = IOException("Network error")

        coEvery {
            api.searchPlayers(query, null, pageSize)
        } throws exception

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals(
            exception,
            (result as PagingSource.LoadResult.Error).throwable
        )
    }

    @Test
    fun `should return error when api throws HttpException`() = runTest {
        val exception = mockk<HttpException>()

        coEvery {
            api.searchPlayers(query, null, pageSize)
        } throws exception

        val result = pagingSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = pageSize,
                placeholdersEnabled = false
            )
        )

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals(
            exception,
            (result as PagingSource.LoadResult.Error).throwable
        )
    }

    @Test
    fun `getRefreshKey should return null`() {
        val state = PagingState<Long, Player>(
            pages = emptyList(),
            anchorPosition = null,
            config = PagingConfig(pageSize = pageSize),
            leadingPlaceholderCount = 5
        )

        assertNull(pagingSource.getRefreshKey(state))
    }

    private fun createPlayerResponse(
        nextCursor: Long?
    ): ApiPage<PlayerDto> {
        return ApiPage(
            data = listOf(
                createPlayerDto()
            ),
            meta = PageMeta(next_cursor = nextCursor)
        )
    }
}


