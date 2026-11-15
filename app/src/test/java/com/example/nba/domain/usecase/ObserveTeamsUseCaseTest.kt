package com.example.nba.domain.usecase

import com.example.nba.domain.repository.TeamsRepository
import com.example.nba.fixtures.TeamTestFixture
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ObserveTeamsUseCaseTest {

    private lateinit var repository: TeamsRepository
    private lateinit var useCase: ObserveTeamsUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        useCase = ObserveTeamsUseCase(repository)
    }

    @Test
    fun `should return teams from repository`() = runTest {
        // Arrange
        val teams = listOf(TeamTestFixture.createTeam())
        val flow = flowOf(teams)

        every { repository.observeTeams() } returns flow

        // Act
        val result = useCase()

        // Assert
        assertEquals(teams, result.first())

        verify(exactly = 1) {
            repository.observeTeams()
        }
    }

    @Test
    fun `should return empty list when repository has no teams`() = runTest {
        // Arrange
        every { repository.observeTeams() } returns flowOf(emptyList())

        // Act
        val result = useCase()

        // Assert
        assertTrue(result.first().isEmpty())
    }
}