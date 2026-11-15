package com.example.nba.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nba.data.local.entity.GameEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("""
        SELECT * FROM games
        WHERE teamId = :teamId
        ORDER BY gameDate DESC
    """)
    fun observeGames(teamId: Int): Flow<List<GameEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(games: List<GameEntity>)

    @Query("DELETE FROM games WHERE teamId = :teamId")
    suspend fun deleteByTeam(teamId: Int)
}
