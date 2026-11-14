package com.example.nba.domain.usecase

import androidx.paging.PagingData
import com.example.nba.domain.model.Player
import com.example.nba.domain.repository.PlayersRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlayersUseCase @Inject constructor(
    private val repository: PlayersRepository
) {
    operator fun invoke(
        search: String?
    ): Flow<PagingData<Player>> {
        return repository.getPlayers(search)
    }
}