package com.example.nba.di

import com.example.nba.data.repository.PlayersRepositoryImpl
import com.example.nba.data.repository.TeamsRepositoryImpl
import com.example.nba.domain.repository.PlayersRepository
import com.example.nba.domain.repository.TeamsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTeamsRepository(
        impl: TeamsRepositoryImpl,
    ): TeamsRepository

    @Binds
    @Singleton
    abstract fun bindPlayersRepository(
        impl: PlayersRepositoryImpl,
    ): PlayersRepository
}
