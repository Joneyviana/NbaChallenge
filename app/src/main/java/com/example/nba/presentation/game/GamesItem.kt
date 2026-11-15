package com.example.nba.presentation.game

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nba.domain.model.Game

@Composable
fun GamesItem(game: Game) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = game.homeTeamName,
            modifier = Modifier.weight(1.4f),
            textAlign = TextAlign.Center
        )

        Text(
            text = game.homeTeamScore?.toString() ?: "-",
            modifier = Modifier.weight(0.8f),
            textAlign = TextAlign.Center
        )

        Text(
            text = game.visitorTeamName,
            modifier = Modifier.weight(1.4f),
            textAlign = TextAlign.Center
        )

        Text(
            text = game.visitorTeamScore?.toString() ?: "-",
            modifier = Modifier.weight(0.8f),
            textAlign = TextAlign.Center
        )
    }
}