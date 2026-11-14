package com.example.nba.di

import android.content.Context
import com.example.nba.data.local.AppDatabase
import com.example.nba.data.local.DatabaseFactory
import com.example.nba.data.local.dao.GameDao
import com.example.nba.data.local.dao.TeamDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase {
        return DatabaseFactory.create(context)
    }

    @Provides
    @Singleton
    fun provideTeamDao(database: AppDatabase): TeamDao {
        return database.teamDao()
    }

    @Provides
    @Singleton
    fun provideGameDao(database: AppDatabase): GameDao {
        return database.gameDao()
    }
}
