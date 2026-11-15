package com.example.nba.presentation.players

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.performTextInput
import androidx.paging.PagingData
import com.example.nba.domain.usecase.GetPlayersUseCase
import junit.framework.TestCase.assertEquals
import androidx.compose.ui.test.onNodeWithText
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlayersScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var getPlayers: GetPlayersUseCase
    private lateinit var viewModel: PlayerViewModel

    @Before
    fun setup() {
        getPlayers = mockk()

        every { getPlayers(any()) } returns flowOf(
            PagingData.Companion.empty()
        )

        viewModel = PlayerViewModel(getPlayers)
    }

    @Test
    fun playersScreen_shouldDisplayTitle() {
        composeRule.setContent {
            PlayersScreen(viewModel = viewModel)
        }

        composeRule
            .onNodeWithText("Players")
            .assertIsDisplayed()
    }

    @Test
    fun playersScreen_shouldDisplaySearchField() {
        composeRule.setContent {
            PlayersScreen(viewModel = viewModel)
        }

        composeRule
            .onNode(hasSetTextAction())
            .assertIsDisplayed()
    }

    @Test
    fun playersScreen_shouldUpdateSearchQueryWhenTyping() {
        composeRule.setContent {
            PlayersScreen(viewModel = viewModel)
        }

        composeRule
            .onNode(hasSetTextAction())
            .performTextInput("LeBron")

        composeRule.runOnIdle {
            assertEquals(
                "LeBron",
                viewModel.searchQuery.value
            )
        }
    }
}