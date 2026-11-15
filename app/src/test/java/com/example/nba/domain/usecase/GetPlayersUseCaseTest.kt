package com.example.nba.domain.usecase

import org.junit.jupiter.api.Assertions.*
import androidx.paging.PagingData
import com.example.nba.domain.model.Player
import com.example.nba.domain.repository.PlayersRepository
import com.example.nba.fixtures.PlayerTestFixtures
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.bouncycastle.util.test.SimpleTest.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertSame

class GetPlayersUseCaseTest {

    private lateinit var repository: PlayersRepository
    private lateinit var getPlayersUseCase: GetPlayersUseCase

    @BeforeEach
    fun setup() {
        repository = mockk()
        getPlayersUseCase = GetPlayersUseCase(repository)
    }

    @Test
    fun `should call repository with search query`() = runTest {
        val pagingData = PagingData.empty<Player>()

        every {
            repository.getPlayers("LeBron")
        } returns flowOf(pagingData)

        val result = getPlayersUseCase("LeBron").first()

        assertSame(pagingData, result)

        verify(exactly = 1) {
            repository.getPlayers("LeBron")
        }
    }

    @Test
    fun `should call repository with null search`() = runTest {
        val pagingData = PagingData.empty<Player>()

        every {
            repository.getPlayers(null)
        } returns flowOf(pagingData)

        val result = getPlayersUseCase(null).first()

        assertSame(pagingData, result)

        verify(exactly = 1) {
            repository.getPlayers(null)
        }
    }
}