package com.example.nba.presentation.home

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nba.domain.model.Team
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val viewModel: HomeViewModel = mockk(relaxed = true)
    private val uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState())

    @Before
    fun setup() {
        every {
            viewModel.uiState
        } returns uiState
    }

    private fun setHomeScreen() {
        composeRule.setContent {
            HomeScreen(
                snackbarHostState = remember {
                    SnackbarHostState()
                },
                viewModel = viewModel,
                onTeamClick = {}
            )
        }
    }

    @Test
    fun shouldDisplayTeams() {
        val teams = listOf(
            Team(
                id = 1,
                fullName = "Los Angeles Lakers",
                city = "Los Angeles",
                conference = "West"
            ),
            Team(
                id = 2,
                fullName = "Boston Celtics",
                city = "Boston",
                conference = "East"
            )
        )

        every {
            viewModel.uiState
        } returns MutableStateFlow(
            HomeUiState(
                teams = teams,
                isRefreshing = false,
                error = null
            )
        )

        setHomeScreen()

        composeRule
            .onNodeWithText("Los Angeles Lakers")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Boston Celtics")
            .assertIsDisplayed()
    }

    @Test
    fun shouldDisplayLoadingWhenTeamsAreBeingLoaded() {
        uiState.value = HomeUiState(
            teams = emptyList(),
            isRefreshing = true,
            error = null
        )

        setHomeScreen()

        composeRule
            .onNodeWithTag("loading_content")
            .assertIsDisplayed()
    }

    @Test
    fun shouldDisplayErrorWhenLoadingTeamsFails() {
        uiState.value = HomeUiState(
            teams = emptyList(),
            isRefreshing = false,
            error = "Unable to load the teams"
        )

        setHomeScreen()

        composeRule
            .onNodeWithTag("error_content")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Unable to load the teams")
            .assertIsDisplayed()

        composeRule
            .onNodeWithText("Try again")
            .assertIsDisplayed()
    }


    @Test
    fun shouldDisplaySortDialogWhenSortButtonIsClicked() {
        setHomeScreen()

        composeRule
            .onNodeWithTag("sort_button")
            .assertIsDisplayed()
            .performClick()

        composeRule
            .onNodeWithText("Order teams by")
            .assertIsDisplayed()

        composeRule
            .onNodeWithTag("sort_option_conference")
            .assertIsDisplayed()
            .performClick()

        composeRule.onNodeWithTag("sort_button")
            .assertTextEquals("Conference")
    }

    @Test
    fun shouldSortTeamsByCity() {
        setHomeScreen()

        composeRule
            .onNodeWithTag("sort_button")
            .performClick()

        composeRule
            .onNodeWithTag("sort_option_city")
            .performClick()

        verify(exactly = 1) {
            viewModel.onSortChanged(TeamSort.CITY)
        }

        composeRule.onNodeWithTag("sort_button")
            .assertTextEquals("City")
    }

    @Test
    fun shouldSortTeamsByName() {
        setHomeScreen()

        composeRule
            .onNodeWithTag("sort_button")
            .performClick()

        composeRule
            .onNodeWithTag("sort_option_name")
            .performClick()

        verify(exactly = 1) {
            viewModel.onSortChanged(TeamSort.NAME)
        }
    }

    @Test
    fun shouldCallRefreshWhenRetryButtonIsClicked() {
        uiState.value = HomeUiState(
            teams = emptyList(),
            isRefreshing = false,
            error = "Unable to load the teams"
        )

        setHomeScreen()

        composeRule
            .onNodeWithText("Try again")
            .performClick()

        verify(exactly = 1) {
            viewModel.refresh()
        }
    }
}
