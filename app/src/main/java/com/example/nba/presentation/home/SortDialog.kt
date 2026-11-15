package com.example.nba.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

@Composable
fun SortDialog(
    onDismiss: () -> Unit,
    onSortSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Order teams by")
        },
        text = {
            Column {
                listOf("Name", "City", "Conference").forEach { option ->
                    TextButton(
                        modifier = Modifier.testTag(
                            "sort_option_${option.lowercase()}"
                        ),
                        onClick = {
                            onSortSelected(option)
                        }
                    ) {
                        Text(option)
                    }
                }
            }
        },
        confirmButton = {}
    )
}