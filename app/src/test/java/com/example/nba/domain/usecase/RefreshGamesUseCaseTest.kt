package com.example.nba.domain.usecase


import com.example.nba.domain.repository.GamesRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RefreshGamesUseCaseTest {

    private val repository: GamesRepository = mockk()

    private val useCase = RefreshGamesUseCase(repository)

    @Test
    fun `should refresh games using repository`() = runTest {
        // Arrange
        val teamId = 14

        coEvery {
            repository.refreshGames(teamId)
        } returns Unit

        // Act
        useCase(teamId)

        // Assert
        coVerify(exactly = 1) {
            repository.refreshGames(teamId)
        }
    }

    @Test
    fun `should propagate exception when repository fails`() = runTest {
        // Arrange
        val teamId = 14
        val exception = RuntimeException("Failed to refresh games")

        coEvery {
            repository.refreshGames(teamId)
        } throws exception

        // Act / Assert
        val result = assertFailsWith<RuntimeException> {
            useCase(teamId)
        }

        assertEquals("Failed to refresh games", result.message)
    }
}
