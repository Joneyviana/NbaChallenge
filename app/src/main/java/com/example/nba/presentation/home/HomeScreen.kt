package com.example.nba.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private data class TeamRow(val name: String, val city: String, val conference: String)

@Composable
fun HomeScreen() {
    val teams = remember {
        listOf(
            TeamRow("Atlanta Hawks", "Atlanta", "East"),
            TeamRow("Boston Celtics", "Boston", "East"),
            TeamRow("Golden State Warriors", "Golden State", "West")
        )
    }
    var sortBy by remember { mutableStateOf("Name") }
    var showSortDialog by remember { mutableStateOf(false) }
    var selectedTeam by remember { mutableStateOf<TeamRow?>(null) }

    if (selectedTeam != null) {
        AlertDialog(
            onDismissRequest = { selectedTeam = null },
            title = { Text(selectedTeam!!.name) },
            text = { Text("Team details and paginated games will be loaded here.") },
            confirmButton = {
                TextButton(onClick = { selectedTeam = null }) { Text("Back") }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Home", style = MaterialTheme.typography.headlineMedium)
            TextButton(onClick = { showSortDialog = true }) { Text(sortBy) }
        }
        Row(Modifier.fillMaxWidth().padding(vertical = 12.dp)) {
            Text("Name", Modifier.weight(2f), style = MaterialTheme.typography.labelLarge)
            Text("City", Modifier.weight(1.5f), style = MaterialTheme.typography.labelLarge)
            Text("Conference", Modifier.weight(1.5f), style = MaterialTheme.typography.labelLarge)
        }
        HorizontalDivider()
        LazyColumn {
            items(teams.sortedBy {
                when (sortBy) {
                    "City" -> it.city
                    "Conference" -> it.conference
                    else -> it.name
                }
            }) { team ->
                Row(
                    Modifier.fillMaxWidth().clickable { selectedTeam = team }.padding(vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(team.name, Modifier.weight(2f))
                    Text(team.city, Modifier.weight(1.5f))
                    Text(team.conference, Modifier.weight(1.5f))
                }
                HorizontalDivider()
            }
        }
    }

    if (showSortDialog) {
        AlertDialog(
            onDismissRequest = { showSortDialog = false },
            title = { Text("Order teams by") },
            text = {
                Column {
                    listOf("Name", "City", "Conference").forEach { option ->
                        TextButton(onClick = {
                            sortBy = option
                            showSortDialog = false
                        }) { Text(option) }
                    }
                }
            },
            confirmButton = {}
        )
    }
}
