package com.example.nba.data.repository

import org.junit.jupiter.api.Assertions.*


import com.example.nba.data.local.dao.GameDao
import com.example.nba.data.local.entity.GameEntity
import com.example.nba.data.remote.api.BallDontLieApi
import com.example.nba.data.remote.dto.ApiPage
import com.example.nba.data.remote.dto.GameDto
import com.example.nba.data.remote.dto.TeamSummaryDto
import com.example.nba.domain.model.Game
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GamesRepositoryImplTest {

    private val api: BallDontLieApi = mockk()
    private val dao: GameDao = mockk()

    private val repository = GamesRepositoryImpl(api, dao)

    private companion object {
        const val TEAM_ID = 14
    }

    @Test
    fun `should observe games from database`() = runTest {
        // Arrange
        val entities = listOf(
            GameEntity(
                id = 1L,
                teamId = TEAM_ID,
                date = "2026-11-10T00:00:00.000Z",
                gameDate = LocalDate.of(2026, 11, 10),
                homeTeamName = "Los Angeles Lakers",
                homeScore = 110,
                visitorTeamName = "Boston Celtics",
                visitorScore = 105
            )
        )

        every { dao.observeGames(TEAM_ID) } returns flowOf(entities)

        // Act
        val result = repository.observeGames(TEAM_ID)

        // Assert
        assertEquals(
            listOf(
                Game(
                    id = 1L,
                    homeTeamName = "Los Angeles Lakers",
                    homeTeamScore = 110,
                    visitorTeamName = "Boston Celtics",
                    visitorTeamScore = 105,
                    gameDate = LocalDate.of(2026, 11, 10)
                )
            ),
            result.single()
        )

        verify(exactly = 1) { dao.observeGames(TEAM_ID) }
    }

    @Test
    fun `should refresh games from api and save them in database`() = runTest {
        // Arrange
        val remoteGames = listOf(
            GameDto(
                id = 1L,
                date = "2026-11-10T00:00:00.000Z",
                home_team_score = 110,
                visitor_team_score = 105,
                home_team = TeamSummaryDto(
                    id = 14,
                    full_name = "Los Angeles Lakers"
                ),
                visitor_team = TeamSummaryDto(
                    id = 2,
                    full_name = "Boston Celtics"
                )
            ),
            GameDto(
                id = 2L,
                date = "2026-11-11T00:00:00.000Z",
                home_team_score = 98,
                visitor_team_score = 102,
                home_team = TeamSummaryDto(
                    id = 14,
                    full_name = "Los Angeles Lakers"
                ),
                visitor_team = TeamSummaryDto(
                    id = 3,
                    full_name = "Chicago Bulls"
                )
            )
        )

        coEvery {
            api.getGames(
                teamId = TEAM_ID,
                cursor = null,
                perPage = 25
            )
        } returns ApiPage(data = remoteGames)

        coEvery { dao.upsertAll(any()) } returns Unit

        // Act
        repository.refreshGames(TEAM_ID)

        // Assert
        coVerify(exactly = 1) {
            api.getGames(
                teamId = TEAM_ID,
                cursor = null,
                perPage = 25
            )
        }

        coVerify(exactly = 1) {
            dao.upsertAll(
                listOf(
                    GameEntity(
                        id = 1L,
                        teamId = TEAM_ID,
                        date = "2026-11-10T00:00:00.000Z",
                        gameDate = LocalDate.of(2026, 11, 10),
                        homeTeamName = "Los Angeles Lakers",
                        homeScore = 110,
                        visitorTeamName = "Boston Celtics",
                        visitorScore = 105
                    ),
                    GameEntity(
                        id = 2L,
                        teamId = TEAM_ID,
                        date = "2026-11-11T00:00:00.000Z",
                        gameDate = LocalDate.of(2026, 11, 11),
                        homeTeamName = "Los Angeles Lakers",
                        homeScore = 98,
                        visitorTeamName = "Chicago Bulls",
                        visitorScore = 102
                    )
                )
            )
        }
    }

    @Test
    fun `should save empty list when api returns no games`() = runTest {
        // Arrange
        coEvery {
            api.getGames(TEAM_ID, cursor = null, perPage = 25)
        } returns ApiPage(data = emptyList())

        coEvery { dao.upsertAll(emptyList()) } returns Unit

        // Act
        repository.refreshGames(TEAM_ID)

        // Assert
        coVerify(exactly = 1) {
            dao.upsertAll(emptyList())
        }
    }

    @Test
    fun `should propagate api exception when refreshing games`() = runTest {
        // Arrange
        val exception = RuntimeException("API unavailable")

        coEvery {
            api.getGames(TEAM_ID, cursor = null, perPage = 25)
        } throws exception

        // Act / Assert
        val result = assertFailsWith<RuntimeException> {
            repository.refreshGames(TEAM_ID)
        }

        assertEquals("API unavailable", result.message)

        coVerify(exactly = 0) { dao.upsertAll(any()) }
    }

    @Test
    fun `should propagate dao exception when saving games`() = runTest {
        // Arrange
        val remoteGames = listOf(
            GameDto(
                id = 1L,
                date = "2026-11-10T00:00:00.000Z",
                home_team_score = 110,
                visitor_team_score = 105,
                home_team = TeamSummaryDto(14, "Los Angeles Lakers"),
                visitor_team = TeamSummaryDto(2, "Boston Celtics")
            )
        )

        coEvery {
            api.getGames(TEAM_ID, cursor = null, perPage = 25)
        } returns ApiPage(data = remoteGames)

        coEvery { dao.upsertAll(any()) } throws RuntimeException("Database error")

        // Act / Assert
        val result = assertFailsWith<RuntimeException> {
            repository.refreshGames(TEAM_ID)
        }

        assertEquals("Database error", result.message)
    }
}
