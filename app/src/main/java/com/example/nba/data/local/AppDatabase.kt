package com.example.nba.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.nba.data.local.dao.GameDao
import com.example.nba.data.local.dao.TeamDao
import com.example.nba.data.local.entity.GameEntity
import com.example.nba.data.local.entity.TeamEntity

@Database(
    entities = [TeamEntity::class, GameEntity::class],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun teamDao(): TeamDao
    abstract fun gameDao(): GameDao
}
