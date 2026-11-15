package com.example.nba.domain.usecase


import com.example.nba.domain.model.Game
import com.example.nba.domain.repository.GamesRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class ObserveGamesUseCaseTest {

    private val repository: GamesRepository = mockk()

    private val useCase = ObserveGamesUseCase(repository)

    @Test
    fun `should return games from repository`() = runTest {
        // Arrange
        val games = listOf(
            Game(
                id = 1L,
                homeTeamName = "Los Angeles Lakers",
                homeTeamScore = 110,
                visitorTeamName = "Boston Celtics",
                visitorTeamScore = 105,
                gameDate = LocalDate.of(2026, 11, 10)
            )
        )

        every { repository.observeGames(14) } returns flowOf(games)

        // Act
        val result = useCase(14).single()

        // Assert
        assertEquals(games, result)
        verify(exactly = 1) { repository.observeGames(14) }
    }

    @Test
    fun `should return empty list when repository has no games`() = runTest {
        // Arrange
        every { repository.observeGames(14) } returns flowOf(emptyList())

        // Act
        val result = useCase(14).single()

        // Assert
        assertEquals(emptyList(), result)
        verify(exactly = 1) { repository.observeGames(14) }
    }
}
