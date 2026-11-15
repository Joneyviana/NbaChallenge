package com.example.nba.presentation.game


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.nba.domain.model.Game

@Composable
private fun GamesList(
    games: List<Game>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Home Name",
                modifier = Modifier.weight(1.4f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Home Score",
                modifier = Modifier.weight(0.8f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Visitor Name",
                modifier = Modifier.weight(1.4f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Visitor Score",
                modifier = Modifier.weight(0.8f),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalDivider()

        LazyColumn {
            items(
                items = games,
                key = { it.id }
            ) { game ->
                GamesItem(game)
            }
        }
    }
}