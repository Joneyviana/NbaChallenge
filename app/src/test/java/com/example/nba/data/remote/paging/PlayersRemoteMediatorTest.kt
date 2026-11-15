package com.example.nba.data.remote.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.MediatorResult
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.nba.data.local.AppDatabase
import com.example.nba.data.local.dao.PlayerDao
import com.example.nba.data.local.dao.PlayerRemoteKeysDao
import com.example.nba.data.local.entity.PlayerEntity
import com.example.nba.data.local.entity.PlayerRemoteKeys
import com.example.nba.data.remote.api.BallDontLieApi
import com.example.nba.data.remote.dto.ApiPage
import com.example.nba.data.remote.dto.PageMeta
import com.example.nba.data.remote.dto.PlayerDto
import com.example.nba.data.remote.dto.TeamDto
import com.example.nba.fixtures.PlayerTestFixtures.createPlayerDto
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import retrofit2.HttpException
import java.io.IOException
import kotlin.test.assertEquals
import kotlin.test.assertTrue


@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalPagingApi::class)
class PlayersRemoteMediatorTest {

    private lateinit var database: AppDatabase
    private lateinit var playerDao: PlayerDao
    private lateinit var remoteKeysDao: PlayerRemoteKeysDao
    private lateinit var api: BallDontLieApi
    private lateinit var mediator: PlayersRemoteMediator

    private val pageSize = 25

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        playerDao = database.playerDao()
        remoteKeysDao = database.playerRemoteKeysDao()
        api = mockk()

        mediator = PlayersRemoteMediator(
            database = database,
            api = api
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun createPagingState() = PagingState<Int, PlayerEntity>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = pageSize),
        leadingPlaceholderCount = 3
    )

    @Test
    fun `refresh should fetch first page`() = runTest {
        coEvery {
            api.searchPlayers("", null, pageSize)
        } returns createPlayerResponse(nextCursor = 100L)

        val result = mediator.load(
            LoadType.REFRESH,
            createPagingState()
        )

        assertTrue(result is MediatorResult.Success)

        assertEquals(
            100L,
            remoteKeysDao.getRemoteKeys()?.nextCursor
        )

        coVerify(exactly = 1) {
            api.searchPlayers("", null, pageSize)
        }
    }

    @Test
    fun `append should use next cursor`() = runTest {
        remoteKeysDao.upsert(
            PlayerRemoteKeys(nextCursor = 100L)
        )

        coEvery {
            api.searchPlayers("", 100L, pageSize)
        } returns createPlayerResponse(nextCursor = 200L)

        val result = mediator.load(
            LoadType.APPEND,
            createPagingState()
        )

        assertTrue(result is MediatorResult.Success)

        assertEquals(
            200L,
            remoteKeysDao.getRemoteKeys()?.nextCursor
        )

        coVerify(exactly = 1) {
            api.searchPlayers("", 100L, pageSize)
        }
    }

    @Test
    fun `append without remote key should finish pagination`() = runTest {
        val result = mediator.load(
            LoadType.APPEND,
            createPagingState()
        )

        assertTrue(result is MediatorResult.Success)
        assertTrue(result.endOfPaginationReached)

        coVerify(exactly = 0) {
            api.searchPlayers(any(), any(), any())
        }
    }

    @Test
    fun `prepend should finish pagination`() = runTest {
        val result = mediator.load(
            LoadType.PREPEND,
            createPagingState()
        )

        assertTrue(result is MediatorResult.Success)
        assertTrue(result.endOfPaginationReached)

        coVerify(exactly = 0) {
            api.searchPlayers(any(), any(), any())
        }
    }

    @Test
    fun `refresh without next cursor should finish pagination`() = runTest {
        coEvery {
            api.searchPlayers("", null, pageSize)
        } returns createPlayerResponse(nextCursor = null)

        val result = mediator.load(
            LoadType.REFRESH,
            createPagingState()
        )

        assertTrue(result is MediatorResult.Success)
        assertTrue(result.endOfPaginationReached)
    }

    @Test
    fun `should return error when api throws IOException`() = runTest {
        val exception = IOException("Network error")

        coEvery {
            api.searchPlayers("", null, pageSize)
        } throws exception

        val result = mediator.load(
            LoadType.REFRESH,
            createPagingState()
        )

        assertTrue(result is MediatorResult.Error)
        assertEquals(
            exception,
            (result as MediatorResult.Error).throwable
        )
    }

    @Test
    fun `should return error when api throws HttpException`() = runTest {
        val exception = mockk<HttpException>()

        coEvery {
            api.searchPlayers("", null, pageSize)
        } throws exception

        val result = mediator.load(
            LoadType.REFRESH,
            createPagingState()
        )

        assertTrue(result is MediatorResult.Error)
        assertEquals(
            exception,
            (result as MediatorResult.Error).throwable
        )
    }

    @Test
    fun `initialize should launch refresh when database is empty`() = runTest {
        val result = mediator.initialize()

        assertEquals(
            RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH,
            result
        )
    }

    @Test
    fun `initialize should skip refresh when database has players`() = runTest {
        playerDao.upsertAll(
            listOf(
                PlayerEntity(
                    id = 1,
                    firstName = "LeBron",
                    lastName = "James",
                    teamFullName = "Los Angeles Lakers",
                    teamId = 15
                )
            )
        )

        val result = mediator.initialize()

        assertEquals(
            RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH,
            result
        )
    }

    private fun createPlayerResponse(
        nextCursor: Long?
    ): ApiPage<PlayerDto> {
        return ApiPage(
            data = listOf(
                createPlayerDto()
            ),
            meta = PageMeta(
                next_cursor = nextCursor
            )
        )
    }
}
