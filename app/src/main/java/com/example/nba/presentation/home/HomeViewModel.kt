package com.example.nba.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nba.domain.model.Team
import com.example.nba.domain.usecase.ObserveTeamsUseCase
import com.example.nba.domain.usecase.RefreshTeamsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.sortedBy
import kotlinx.coroutines.flow.catch
import kotlin.coroutines.cancellation.CancellationException



@HiltViewModel
class HomeViewModel @Inject constructor(
    private val observeTeams: ObserveTeamsUseCase,
    private val refreshTeams: RefreshTeamsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        startObservingTeams()
        refresh()
    }

    private fun startObservingTeams() {
        viewModelScope.launch {
            observeTeams()
                .catch {
                    updateError("Error loading teams")
                }
                .collect { teams ->
                    _uiState.update { state ->
                        state.copy(
                            teams = sortTeams(teams, state.sortBy)
                        )
                    }
                }
        }
    }

    fun onSortChanged(sortBy: TeamSort) {
        _uiState.update { state ->
            state.copy(
                sortBy = sortBy,
                teams = sortTeams(state.teams, sortBy)
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isRefreshing = true, error = null)
            }

            try {
                refreshTeams()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                updateError("Erro ao atualizar os times")
            } finally {
                _uiState.update {
                    it.copy(isRefreshing = false)
                }
            }
        }
    }

    private fun updateError(message: String) {
        _uiState.update {
            it.copy(error = message)
        }
    }

    private fun sortTeams(
        teams: List<Team>,
        sortBy: TeamSort
    ): List<Team> = when (sortBy) {
        TeamSort.NAME -> teams.sortedBy { it.fullName }
        TeamSort.CITY -> teams.sortedBy { it.city }
        TeamSort.CONFERENCE -> teams.sortedBy { it.conference }
    }
}