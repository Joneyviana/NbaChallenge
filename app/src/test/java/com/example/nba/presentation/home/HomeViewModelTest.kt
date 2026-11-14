package com.example.nba.presentation.home

import com.example.nba.domain.model.Team
import com.example.nba.domain.usecase.ObserveTeamsUseCase
import com.example.nba.domain.usecase.RefreshTeamsUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue


@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val observeTeams: ObserveTeamsUseCase = mockk()
    private val refreshTeams: RefreshTeamsUseCase = mockk()

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var teamsFlow: MutableStateFlow<List<Team>>

    private val lakers = createTeam(
        "Lakers", "Los Angeles", "West"
    )

    private val celtics = createTeam(
        "Celtics", "Boston", "East"
    )

    private val teams = listOf(lakers, celtics)

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        teamsFlow = MutableStateFlow(emptyList())

        every { observeTeams() } returns teamsFlow
        coEvery { refreshTeams() } returns Unit
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `should observe teams`() = runTest(testDispatcher) {
        teamsFlow.value = teams

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(teams.map { it.fullName }.sorted(),
            viewModel.uiState.value.teams.map { it.fullName })
    }

    @Test
    fun `should sort teams by name`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        teamsFlow.value = teams
        advanceUntilIdle()

        viewModel.onSortChanged(TeamSort.NAME)

        assertEquals(
            listOf("Celtics", "Lakers"),
            viewModel.uiState.value.teams.map { it.fullName }
        )
    }

    @Test
    fun `should sort teams by city`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        teamsFlow.value = teams
        advanceUntilIdle()

        viewModel.onSortChanged(TeamSort.CITY)

        assertEquals(
            listOf("Boston", "Los Angeles"),
            viewModel.uiState.value.teams.map { it.city }
        )
    }

    @Test
    fun `should sort teams by conference`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        teamsFlow.value = teams
        advanceUntilIdle()

        viewModel.onSortChanged(TeamSort.CONFERENCE)

        assertEquals(
            listOf("East", "West"),
            viewModel.uiState.value.teams.map { it.conference }
        )
    }

    @Test
    fun `should update teams when flow emits new values`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            teamsFlow.value = listOf(lakers)
            advanceUntilIdle()

            assertEquals(
                listOf(lakers),
                viewModel.uiState.value.teams
            )
        }

    @Test
    fun `should finish refreshing successfully`() =
        runTest(testDispatcher) {
            val viewModel = createViewModel()
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isRefreshing)
            assertNull(viewModel.uiState.value.error)

            coVerify(exactly = 1) { refreshTeams() }
        }

    @Test
    fun `should keep local teams when refresh fails`() =
        runTest(testDispatcher) {
            teamsFlow.value = teams
            coEvery { refreshTeams() } throws IOException()

            val viewModel = createViewModel()
            advanceUntilIdle()

            assertEquals(teams.map { it.fullName }.sorted(),
                viewModel.uiState.value.teams.map { it.fullName })
            assertEquals(
                "Erro ao atualizar os times",
                viewModel.uiState.value.error
            )
            assertFalse(viewModel.uiState.value.isRefreshing)
        }

    @Test
    fun `should keep refreshing state while request is running`() =
        runTest(testDispatcher) {
            val deferred = CompletableDeferred<Unit>()

            coEvery { refreshTeams() } coAnswers {
                deferred.await()
            }

            val viewModel = createViewModel()
            runCurrent()

            assertTrue(viewModel.uiState.value.isRefreshing)

            deferred.complete(Unit)
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isRefreshing)
        }

    @Test
    fun `should propagate cancellation`() = runTest(testDispatcher) {
        coEvery { refreshTeams() } throws CancellationException()

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
        assertFalse(viewModel.uiState.value.isRefreshing)
    }

    private fun createViewModel() =
        HomeViewModel(observeTeams, refreshTeams)

    private fun createTeam(
        fullName: String,
        city: String,
        conference: String
    ): Team = mockk {
        every { this@mockk.fullName } returns fullName
        every { this@mockk.city } returns city
        every { this@mockk.conference } returns conference
    }
}
