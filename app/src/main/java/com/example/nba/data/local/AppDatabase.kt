package com.example.nba.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nba.data.local.dao.GameDao
import com.example.nba.data.local.dao.PlayerDao
import com.example.nba.data.local.dao.PlayerRemoteKeysDao
import com.example.nba.data.local.dao.TeamDao
import com.example.nba.data.local.entity.GameEntity
import com.example.nba.data.local.entity.TeamEntity
import com.example.nba.data.local.entity.PlayerEntity
import com.example.nba.data.local.entity.PlayerRemoteKeys


@Database(
    entities = [
        TeamEntity::class,
        GameEntity::class,
        PlayerEntity::class,
        PlayerRemoteKeys::class],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao
    abstract fun gameDao(): GameDao
    abstract fun playerDao(): PlayerDao
    abstract fun playerRemoteKeysDao(): PlayerRemoteKeysDao
}
