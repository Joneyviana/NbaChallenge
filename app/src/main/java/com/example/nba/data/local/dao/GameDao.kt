package com.example.nba.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.nba.data.local.entity.GameEntity

@Dao
interface GameDao {
    @Query("SELECT * FROM games WHERE teamId = :teamId ORDER BY date DESC")
    fun pagingSource(teamId: Int): PagingSource<Int, GameEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(games: List<GameEntity>)

    @Query("DELETE FROM games WHERE teamId = :teamId")
    suspend fun clearForTeam(teamId: Int)
}
