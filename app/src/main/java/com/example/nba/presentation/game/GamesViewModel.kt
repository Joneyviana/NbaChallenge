package com.example.nba.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nba.domain.usecase.ObserveGamesUseCase
import com.example.nba.domain.usecase.RefreshGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException


@HiltViewModel
class GamesViewModel @Inject constructor(
    private val observeGamesUseCase: ObserveGamesUseCase,
    private val refreshGamesUseCase: RefreshGamesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GamesUiState())
    val uiState = _uiState.asStateFlow()

    private var currentTeamId: Int? = null
    private var observeJob: Job? = null

    fun loadGames(teamId: Int) {
        if (currentTeamId == teamId) return

        currentTeamId = teamId

        observeJob?.cancel()

        observeJob = viewModelScope.launch {
            observeGamesUseCase(teamId).collect { games ->
                _uiState.update {
                    it.copy(games = games)
                }
            }
        }

        refresh()
    }

    fun refresh() {
        val teamId = currentTeamId ?: return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRefreshing = true,
                    error = null
                )
            }

            try {
                refreshGamesUseCase(teamId)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(error = "Não foi possível atualizar os jogos")
                }
            } finally {
                _uiState.update {
                    it.copy(isRefreshing = false)
                }
            }
        }
    }
}