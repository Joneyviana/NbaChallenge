package com.example.nba.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.nba.data.local.entity.PlayerRemoteKeys

@Dao
interface PlayerRemoteKeysDao {

    @Query("SELECT * FROM player_remote_keys WHERE id = 1")
    suspend fun getRemoteKeys(): PlayerRemoteKeys?

    @Upsert
    suspend fun upsert(remoteKeys: PlayerRemoteKeys)

    @Query("DELETE FROM player_remote_keys")
    suspend fun clear()
}