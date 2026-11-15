
package com.example.nba.presentation.game

import androidx.lifecycle.SavedStateHandle
import com.example.nba.domain.model.Game
import com.example.nba.domain.usecase.ObserveGamesUseCase
import com.example.nba.domain.usecase.RefreshGamesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException
import java.time.LocalDate
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GamesViewModelTest {

    companion object {
        private const val TEAM_ID = 14
    }

    private val observeGamesUseCase: ObserveGamesUseCase = mockk()
    private val refreshGamesUseCase: RefreshGamesUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var gamesFlow: MutableStateFlow<List<Game>>

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        gamesFlow = MutableStateFlow(emptyList())

        every {
            observeGamesUseCase(TEAM_ID)
        } returns gamesFlow

        coEvery {
            refreshGamesUseCase(TEAM_ID)
        } returns Unit
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should observe games`() = runTest(testDispatcher) {
        val games = listOf(createGame())

        gamesFlow.value = games

        val viewModel = createViewModel()

        advanceUntilIdle()

        assertEquals(games, viewModel.uiState.value.games)

        verify(exactly = 1) {
            observeGamesUseCase(TEAM_ID)
        }
    }

    @Test
    fun `should refresh games when view model is initialized`() =
        runTest(testDispatcher) {

            val viewModel = createViewModel()

            advanceUntilIdle()

            coVerify(exactly = 1) {
                refreshGamesUseCase(TEAM_ID)
            }

            assertFalse(viewModel.uiState.value.isRefreshing)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `should display refreshing state while refresh is running`() =
        runTest(testDispatcher) {

            val deferred = CompletableDeferred<Unit>()

            coEvery {
                refreshGamesUseCase(TEAM_ID)
            } coAnswers {
                deferred.await()
            }

            val viewModel = createViewModel()

            runCurrent()

            assertTrue(viewModel.uiState.value.isRefreshing)

            deferred.complete(Unit)

            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isRefreshing)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `should update games when flow emits new values`() =
        runTest(testDispatcher) {

            val viewModel = createViewModel()

            advanceUntilIdle()

            val games = listOf(
                createGame(),
                createGame(
                    id = 2,
                    homeTeamName = "Golden State Warriors",
                    visitorTeamName = "Chicago Bulls",
                    homeTeamScore = 120,
                    visitorTeamScore = 115
                )
            )

            gamesFlow.value = games

            advanceUntilIdle()

            assertEquals(games, viewModel.uiState.value.games)
        }

    @Test
    fun `should display error when refresh fails`() =
        runTest(testDispatcher) {

            coEvery {
                refreshGamesUseCase(TEAM_ID)
            } throws IOException("Network error")

            val viewModel = createViewModel()

            advanceUntilIdle()

            assertEquals(
                "Unable to update the games",
                viewModel.uiState.value.error
            )

            assertFalse(viewModel.uiState.value.isRefreshing)
        }

    @Test
    fun `should preserve cached games when refresh fails`() =
        runTest(testDispatcher) {

            val games = listOf(createGame())
            gamesFlow.value = games

            coEvery {
                refreshGamesUseCase(TEAM_ID)
            } throws IOException("Network error")

            val viewModel = createViewModel()

            advanceUntilIdle()

            assertEquals(games, viewModel.uiState.value.games)

            assertEquals(
                "Unable to update the games",
                viewModel.uiState.value.error
            )

            assertFalse(viewModel.uiState.value.isRefreshing)
        }

    @Test
    fun `should clear previous error when refresh starts`() =
        runTest(testDispatcher) {

            val deferred = CompletableDeferred<Unit>()

            coEvery {
                refreshGamesUseCase(TEAM_ID)
            } throws IOException("Network error")

            val viewModel = createViewModel()

            advanceUntilIdle()

            assertEquals(
                "Unable to update the games",
                viewModel.uiState.value.error
            )

            coEvery {
                refreshGamesUseCase(TEAM_ID)
            } coAnswers {
                deferred.await()
            }

            viewModel.refresh()

            runCurrent()

            assertNull(viewModel.uiState.value.error)
            assertTrue(viewModel.uiState.value.isRefreshing)

            deferred.complete(Unit)

            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isRefreshing)
            assertNull(viewModel.uiState.value.error)
        }

    @Test
    fun `should retry refresh when refresh is called`() =
        runTest(testDispatcher) {

            val viewModel = createViewModel()

            advanceUntilIdle()

            viewModel.refresh()

            advanceUntilIdle()

            coVerify(exactly = 2) {
                refreshGamesUseCase(TEAM_ID)
            }
        }

    @Test
    fun `should propagate cancellation`() = runTest(testDispatcher) {
        coEvery {
            refreshGamesUseCase(TEAM_ID)
        } throws CancellationException()

        val viewModel = createViewModel()

        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    private fun createViewModel() = GamesViewModel(
        savedStateHandle = SavedStateHandle(
            mapOf("teamId" to TEAM_ID)
        ),
        observeGamesUseCase = observeGamesUseCase,
        refreshGamesUseCase = refreshGamesUseCase
    )

    private fun createGame(
        id: Long = 1,
        homeTeamName: String = "Los Angeles Lakers",
        visitorTeamName: String = "Boston Celtics",
        homeTeamScore: Int? = 110,
        visitorTeamScore: Int? = 105
    ) = Game(
        id = id,
        homeTeamName = homeTeamName,
        homeTeamScore = homeTeamScore,
        visitorTeamName = visitorTeamName,
        visitorTeamScore = visitorTeamScore,
        gameDate = LocalDate.of(2026, 11, 10)
    )
}
