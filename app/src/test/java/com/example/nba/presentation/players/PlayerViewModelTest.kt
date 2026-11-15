package com.example.nba.presentation.players

import androidx.paging.PagingData
import com.example.nba.domain.usecase.GetPlayersUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {

    private lateinit var dispatcher: TestDispatcher
    private lateinit var getPlayers: GetPlayersUseCase
    private lateinit var viewModel: PlayerViewModel

    @BeforeEach
    fun setup() {
        dispatcher = StandardTestDispatcher()
        Dispatchers.setMain(dispatcher)

        getPlayers = mockk()
        every { getPlayers(any()) } returns flowOf(PagingData.empty())

        viewModel = PlayerViewModel(getPlayers)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSearchQueryChanged should update searchQuery`() = runTest(dispatcher) {
        viewModel.onSearchQueryChanged("LeBron")

        assertEquals("LeBron", viewModel.searchQuery.value)
    }

    @Test
    fun `players should wait for debounce before searching`() = runTest(dispatcher) {
        val job = backgroundScope.launch {
            viewModel.players.collect { }
        }

        viewModel.onSearchQueryChanged("LeBron")

        advanceTimeBy(299)
        runCurrent()

        verify(exactly = 0) {
            getPlayers(any())
        }

        advanceTimeBy(1)
        runCurrent()

        verify(exactly = 1) {
            getPlayers("LeBron")
        }

        job.cancel()
    }

    @Test
    fun `players should pass null when search query is blank`() = runTest(dispatcher) {
        val job = backgroundScope.launch {
            viewModel.players.collect { }
        }

        advanceTimeBy(300)
        runCurrent()

        verify(exactly = 1) {
            getPlayers(null)
        }

        job.cancel()
    }

    @Test
    fun `players should search only the latest query after debounce`() =
        runTest(dispatcher) {
            val job = backgroundScope.launch {
                viewModel.players.collect { }
            }

            viewModel.onSearchQueryChanged("Le")
            advanceTimeBy(200)

            viewModel.onSearchQueryChanged("LeBron")
            advanceTimeBy(300)
            runCurrent()

            verify(exactly = 1) {
                getPlayers("LeBron")
            }

            verify(exactly = 0) {
                getPlayers("Le")
            }

            job.cancel()
        }
}