package com.example.nba.presentation.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TeamsTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
    ) {
        Text(
            "Name",
            Modifier.weight(2f),
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            "City",
            Modifier.weight(1.5f),
            style = MaterialTheme.typography.labelLarge
        )

        Text(
            "Conference",
            Modifier.weight(1.5f),
            style = MaterialTheme.typography.labelLarge
        )
    }
}