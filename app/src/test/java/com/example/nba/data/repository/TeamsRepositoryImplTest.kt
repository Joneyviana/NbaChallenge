package com.example.nba.data.repository

import com.example.nba.data.local.dao.TeamDao
import com.example.nba.data.remote.api.BallDontLieApi
import com.example.nba.data.remote.dto.ApiPage
import com.example.nba.fixtures.TeamTestFixture
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import kotlin.test.assertFailsWith

@OptIn(ExperimentalCoroutinesApi::class)
class TeamsRepositoryImplTest {

    private val api = mockk<BallDontLieApi>()
    private val dao = mockk<TeamDao>()
    private val repository = TeamsRepositoryImpl(api, dao)

    @Test
    fun `should observe teams mapping entities to domain`() = runTest {
        val entity = TeamTestFixture.createTeamEntity()
        val expected = TeamTestFixture.createTeam()

        every { dao.observeTeams() } returns flowOf(listOf(entity))

        val result = repository.observeTeams().first()

        assertEquals(listOf(expected), result)
        verify(exactly = 1) { dao.observeTeams() }
    }

    @Test
    fun `should refresh teams from api and save them in database`() = runTest {
        val dto = TeamTestFixture.createTeamDto()
        val expectedEntity = TeamTestFixture.createTeamEntity()

        coEvery { api.getTeams() } returns ApiPage(data = listOf(dto))
        coEvery { dao.upsertAll(listOf(expectedEntity)) } just Runs

        repository.refreshTeams()

        coVerify(exactly = 1) { api.getTeams() }
        coVerify(exactly = 1) {
            dao.upsertAll(listOf(expectedEntity))
        }
    }

    @Test
    fun `should propagate exception when api request fails`() = runTest {
        coEvery { api.getTeams() } throws IOException("Network error")

        assertFailsWith<IOException> {
            repository.refreshTeams()
        }

        coVerify(exactly = 0) { dao.upsertAll(any()) }
    }
}