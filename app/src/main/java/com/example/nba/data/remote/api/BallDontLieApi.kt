package com.example.nba.data.remote.api

import com.example.nba.data.remote.dto.ApiPage
import com.example.nba.data.remote.dto.GameDto
import com.example.nba.data.remote.dto.PlayerDto
import com.example.nba.data.remote.dto.TeamDto
import retrofit2.http.GET
import retrofit2.http.Query

interface BallDontLieApi {
    @GET("nba/v1/teams")
    suspend fun getTeams(): ApiPage<TeamDto>

    @GET("nba/v1/games")
    suspend fun getGames(
        @Query("team_ids[]") teamId: Int,
        @Query("cursor") cursor: Long?,
        @Query("per_page") perPage: Int = 25
    ): ApiPage<GameDto>

    @GET("nba/v1/players")
    suspend fun searchPlayers(
        @Query("search") query: String,
        @Query("cursor") cursor: Long?,
        @Query("per_page") perPage: Int = 25
    ): ApiPage<PlayerDto>
}
