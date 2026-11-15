package com.example.nba.presentation.game

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nba.domain.model.Game
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate


@RunWith(AndroidJUnit4::class)
class GameScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var viewModel: GamesViewModel
    private lateinit var uiState: MutableStateFlow<GamesUiState>

    @Before
    fun setup() {
        viewModel = mockk(relaxed = true)
        uiState = MutableStateFlow(GamesUiState())

        every { viewModel.uiState } returns uiState
    }

    private fun setScreen(
        teamName: String = "Los Angeles Lakers",
        onBackClick: () -> Unit = {}
    ) {
        composeRule.setContent {
            MaterialTheme {
                GameScreen(
                    teamName = teamName,
                    onBackClick = onBackClick,
                    viewModel = viewModel
                )
            }
        }
    }

    @Test
    fun shouldDisplayTeamName() {
        setScreen()

        composeRule
            .onNodeWithText("Los Angeles Lakers")
            .assertIsDisplayed()
    }

    @Test
    fun shouldDisplayGamesWhenLoadingIsFinished() {
        uiState.value = GamesUiState(
            games = listOf(
                createGame()
            ),
            isRefreshing = false
        )

        setScreen()

        composeRule
            .onNodeWithText("Boston Celtics")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("110")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("105")
            .assertIsDisplayed()
    }

    @Test
    fun shouldDisplayMultipleGames() {
        uiState.value = GamesUiState(
            games = listOf(
                createGame(id = 1),
                createGame(
                    id = 2,
                    homeTeamName = "Golden State Warriors",
                    visitorTeamName = "Chicago Bulls",
                    homeTeamScore = 120,
                    visitorTeamScore = 115
                )
            )
        )

        setScreen()

        composeRule
            .onNodeWithText("Boston Celtics")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Chicago Bulls")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("120")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("115")
            .assertIsDisplayed()
    }

    @Test
    fun shouldDisplayColumnHeaders() {
        uiState.value = GamesUiState(
            games = listOf(createGame())
        )

        setScreen()

        composeRule.onNodeWithText("Home Name").assertIsDisplayed()
        composeRule.onNodeWithText("Home Score").assertIsDisplayed()
        composeRule.onNodeWithText("Visitor Name").assertIsDisplayed()
        composeRule.onNodeWithText("Visitor Score").assertIsDisplayed()
    }

    @Test
    fun shouldDisplayLoadingContentWhenRefreshingWithoutGames() {
        uiState.value = GamesUiState(
            games = emptyList(),
            isRefreshing = true
        )

        setScreen()

        composeRule
            .onNodeWithTag("loading_content")
            .assertIsDisplayed()
    }

    @Test
    fun shouldDisplayErrorContentWhenGamesAreEmpty() {
        uiState.value = GamesUiState(
            games = emptyList(),
            isRefreshing = false,
            error = "Unable to load the games"
        )

        setScreen()

        composeRule
            .onNodeWithTag("error_content")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Unable to load the games")
            .assertIsDisplayed()
    }

    @Test
    fun shouldRetryWhenErrorRetryButtonIsClicked() {
        uiState.value = GamesUiState(
            games = emptyList(),
            isRefreshing = false,
            error = "Unable to load the games"
        )

        setScreen()

        composeRule
            .onNodeWithTag("error_content")
            .assertIsDisplayed()

        // O botão de retry deve estar dentro do ErrorContent.
        // Se possível, adicione um testTag específico a ele.
        composeRule
            .onNodeWithTag("retry_button")
            .performClick()

        verify(exactly = 1) {
            viewModel.refresh()
        }
    }

    @Test
    fun shouldDisplayEmptyContentWhenThereAreNoGamesAndNoError() {
        uiState.value = GamesUiState(
            games = emptyList(),
            isRefreshing = false,
            error = null
        )

        setScreen()

        composeRule
            .onNodeWithText("Nenhum jogo encontrado")
            .assertIsDisplayed()
    }

    @Test
    fun shouldCallBackWhenBackButtonIsClicked() {
        var backClicked = false

        setScreen(
            onBackClick = {
                backClicked = true
            }
        )

        composeRule
            .onNodeWithText("< Home")
            .performClick()

        assertTrue(backClicked)
    }

    @Test
    fun shouldDisplayGamesEvenWhenRefreshFails() {
        uiState.value = GamesUiState(
            games = listOf(createGame()),
            isRefreshing = false,
            error = "Unable to load the games"
        )

        setScreen()

        composeRule
            .onNodeWithText("Boston Celtics")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Unable to update the games")
            .assertDoesNotExist()
    }

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
