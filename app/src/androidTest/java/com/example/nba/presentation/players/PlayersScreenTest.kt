package com.example.nba.presentation.players

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.nba.domain.model.Player
import com.example.nba.domain.usecase.GetPlayersUseCase
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class PlayersScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private lateinit var getPlayers: GetPlayersUseCase
    private lateinit var viewModel: PlayerViewModel

    @Before
    fun setup() {
        getPlayers = mockk()

        every {
            getPlayers(any())
        } returns flowOf(PagingData.empty())

        viewModel = PlayerViewModel(getPlayers)
    }

    private fun setPlayersScreen(
        pagingFlow: Flow<PagingData<Player>> = flowOf(PagingData.empty())
    ) {
        every {
            getPlayers(null)
        } returns pagingFlow

        composeRule.setContent {
            PlayersScreen(viewModel = viewModel)
        }
    }

    private fun createPagingFlow(
        loadAction: suspend (
            PagingSource.LoadParams<Int>
        ) -> PagingSource.LoadResult<Int, Player>
    ): Flow<PagingData<Player>> {
        return Pager(
            config = PagingConfig(pageSize = 25),
            pagingSourceFactory = {
                object : PagingSource<Int, Player>() {

                    override suspend fun load(
                        params: LoadParams<Int>
                    ): LoadResult<Int, Player> {
                        return loadAction(params)
                    }

                    override fun getRefreshKey(
                        state: PagingState<Int, Player>
                    ): Int? = null
                }
            }
        ).flow
    }

    private fun waitForTag(tag: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule
                .onAllNodesWithTag(tag)
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun playersScreen_shouldDisplayTitle() {
        setPlayersScreen()

        composeRule
            .onNodeWithText("Players")
            .assertIsDisplayed()
    }

    @Test
    fun playersScreen_shouldDisplaySearchField() {
        setPlayersScreen()

        composeRule
            .onNode(hasSetTextAction())
            .assertIsDisplayed()
    }

    @Test
    fun playersScreen_shouldUpdateSearchQueryWhenTyping() {
        setPlayersScreen()

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

    @Test
    fun playersScreen_shouldDisplayLoadingWhenPlayersAreBeingLoaded() {
        val pagingFlow = createPagingFlow {
            awaitCancellation()
        }

        setPlayersScreen(pagingFlow)

        waitForTag("loading_content")

        composeRule
            .onNodeWithTag("loading_content")
            .assertIsDisplayed()
    }

    @Test
    fun playersScreen_shouldDisplayErrorWhenLoadingPlayersFails() {
        val pagingFlow = createPagingFlow {
            PagingSource.LoadResult.Error(
                IOException("Network error")
            )
        }

        setPlayersScreen(pagingFlow)

        waitForTag("error_content")

        composeRule
            .onNodeWithTag("error_content")
            .assertIsDisplayed()
    }
}
