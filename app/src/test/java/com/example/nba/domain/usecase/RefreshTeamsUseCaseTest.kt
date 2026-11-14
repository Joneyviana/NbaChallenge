package com.example.nba.domain.usecase

import com.example.nba.domain.repository.TeamsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RefreshTeamsUseCaseTest {
    private val repository = mockk<TeamsRepository>()
    private val useCase = RefreshTeamsUseCase(repository)

    @Test
    fun `invoke refreshes teams repository`() = runTest {
        coEvery { repository.refreshTeams() } returns Unit

        useCase()

        coVerify(exactly = 1) { repository.refreshTeams() }
    }
}
