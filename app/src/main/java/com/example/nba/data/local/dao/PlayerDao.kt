package com.example.nba.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.nba.data.local.entity.PlayerEntity

@Dao
interface PlayerDao {

    @Query("""
        SELECT * FROM players
        ORDER BY firstName, lastName
    """)
    fun getPlayers(): PagingSource<Int, PlayerEntity>

    @Query("""
        SELECT * FROM players
        WHERE firstName LIKE '%' || :query || '%'
           OR lastName LIKE '%' || :query || '%'
        ORDER BY firstName, lastName
    """)
    suspend fun searchPlayersLocally(query: String): List<PlayerEntity>

    @Upsert
    suspend fun upsertAll(players: List<PlayerEntity>)

    @Query("SELECT COUNT(*) FROM players")
    suspend fun countPlayers(): Int

    @Query("DELETE FROM players")
    suspend fun deleteAll()
}