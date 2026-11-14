package com.example.nba.presentation.players

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PlayersScreen() {
    var query by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize()) {
        Text("Players", style = MaterialTheme.typography.headlineMedium)
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            label = { Text("Search players") },
            singleLine = true
        )
        Spacer(Modifier.height(12.dp))
        Text(
            if (query.isBlank()) "Search for an NBA player by name." else "Search will be connected to the API: $query",
            style = MaterialTheme.typography.bodyMedium
        )
        LazyColumn { }
    }
}
