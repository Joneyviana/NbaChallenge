package com.example.nba.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun HomeHeader(
    sortBy: String,
    onSortClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            "Home",
            style = MaterialTheme.typography.headlineMedium
        )

        TextButton(
            modifier = Modifier.testTag("sort_button"),
            onClick = onSortClick
        ) {
            Text(sortBy)
        }
    }
}