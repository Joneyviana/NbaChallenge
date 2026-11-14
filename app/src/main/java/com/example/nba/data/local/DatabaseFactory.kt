package com.example.nba.data.local

import android.content.Context
import androidx.room.Room

object DatabaseFactory {
    fun create(context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "nba.db")
            .fallbackToDestructiveMigration()
            .build()
}
