package com.example.nba.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.nba.domain.model.Team

@Composable
fun TeamsList(
    teams: List<Team>,
    onTeamClick: (Team) -> Unit
) {
    LazyColumn {
        items(teams) { team ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onTeamClick(team)
                    }
                    .padding(vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    team.fullName,
                    Modifier.weight(2f)
                )

                Text(
                    team.city,
                    Modifier.weight(1.5f)
                )

                Text(
                    team.conference,
                    Modifier.weight(1.5f)
                )
            }

            HorizontalDivider()
        }
    }
}
