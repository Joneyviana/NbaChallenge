package com.example.nba.presentation.navigation

import android.net.Uri

object AppDestinations {
    const val HOME = "home"
    const val PLAYERS = "players"
    const val GAMES = "games/{teamId}/{teamName}"

    fun games(teamId: Int, teamName: String) =
        "games/$teamId/${Uri.encode(teamName)}"
}