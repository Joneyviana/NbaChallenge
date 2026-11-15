package com.example.nba.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.testing.asSnapshot
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.nba.data.local.AppDatabase
import com.example.nba.data.local.dao.PlayerDao
import com.example.nba.data.remote.api.BallDontLieApi
import com.example.nba.data.remote.dto.ApiPage
import com.example.nba.data.remote.dto.PlayerDto
import com.example.nba.data.remote.dto.TeamDto

import com.example.nba.data.local.entity.PlayerEntity
import com.example.nba.domain.model.Player

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.test.runTest

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

import java.io.IOException
import retrofit2.HttpException
import kotlin.test.assertFailsWith


@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalPagingApi::class)
class PlayersRepositoryImplTest {

    private lateinit var database: AppDatabase
    private lateinit var playerDao: PlayerDao
    private lateinit var api: BallDontLieApi
    private lateinit var repository: PlayersRepositoryImpl

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        playerDao = database.playerDao()
        api = mockk()

        repository = PlayersRepositoryImpl(
            database = database,
            api = api
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `should return local players when found`() = runTest {
        val entity = createPlayerEntity()
        playerDao.upsertAll(listOf(entity))

        val result = getPlayers("LeBron")

        assertEquals(1, result.size)
        assertEquals("LeBron", result.first().firstName)
        assertEquals("James", result.first().lastName)

        coVerify(exactly = 0) {
            api.searchPlayers(any(), any(), any())
        }
    }

    @Test
    fun `should fetch and persist players when local search is empty`() = runTest {
        coEvery {
            api.searchPlayers("LeBron", null, 25)
        } returns createApiResponse()

        val result = getPlayers("LeBron")

        assertEquals(1, result.size)
        assertEquals("LeBron", result.first().firstName)

        val persistedPlayers =
            playerDao.searchPlayersLocally("LeBron")

        assertEquals(1, persistedPlayers.size)
        assertEquals("James", persistedPlayers.first().lastName)

        coVerify(exactly = 1) {
            api.searchPlayers("LeBron", null, 25)
        }
    }

    @Test
    fun `should return empty result when API returns no players`() = runTest {
        coEvery {
            api.searchPlayers(any(), any(), any())
        } returns ApiPage(data = emptyList())

        val result = getPlayers("Unknown")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return empty result when IOException occurs`() = runTest {
        coEvery {
            api.searchPlayers(any(), any(), any())
        } throws IOException("No internet connection")

        val result = getPlayers("LeBron")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should return empty result when HttpException occurs`() = runTest {
        coEvery {
            api.searchPlayers(any(), any(), any())
        } throws mockk<HttpException>()

        val result = getPlayers("LeBron")

        assertTrue(result.isEmpty())
    }

    @Test
    fun `should propagate CancellationException`() = runTest {
        coEvery {
            api.searchPlayers("LeBron", null, PAGE_SIZE)
        } throws CancellationException("Search cancelled")

        assertFailsWith<CancellationException> {
            repository.getPlayers("LeBron").toList()
        }
    }

    private fun createPlayerEntity() = PlayerEntity(
        id = 1,
        firstName = "LeBron",
        lastName = "James",
        teamId = 14,
        teamFullName = "Los Angeles Lakers"
    )

    private fun createApiResponse() = ApiPage(
        data = listOf(
            PlayerDto(
                id = 1,
                first_name = "LeBron",
                last_name = "James",
                team = TeamDto(
                    id = 14,
                    full_name = "Los Angeles Lakers",
                    city = "Los Angeles",
                    conference = "West",
                    name = "Lakers"
                )
            )
        )
    )

    private suspend fun getPlayers(query: String) : List<Player> {
        return repository
            .getPlayers(query)
            .asSnapshot()
    }

    private companion object {
        private val PAGE_SIZE = 25
    }
}
