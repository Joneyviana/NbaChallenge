package com.example.nba.presentation.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.nba.presentation.game.GameScreen
import com.example.nba.presentation.home.HomeScreen
import com.example.nba.presentation.players.PlayersScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {

    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME
    ) {
        composable(AppDestinations.HOME) {
            HomeScreen(
                snackbarHostState = snackbarHostState,
                onTeamClick = { team ->
                    navController.navigate(
                        AppDestinations.games(team.id, team.fullName)
                    )
                }
            )
        }

        composable(AppDestinations.PLAYERS) {
            PlayersScreen()
        }

        composable(
            route = AppDestinations.GAMES,
            arguments = listOf(
                navArgument("teamId") {
                    type = NavType.IntType
                },
                navArgument("teamName") {
                    type = NavType.StringType
                }
            )
        ) {
            val teamName = it.arguments?.getString("teamName").orEmpty()

            GameScreen(
                teamName = teamName,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}