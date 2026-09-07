package com.uliana.plantcare.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FertilizerDialog(
    initialIntervalDays: Int,
    initialName: String,
    initialNotes: String,
    onDismiss: () -> Unit,
    onConfirm: (Int, String, String) -> Unit
) {
    var intervalText by remember { mutableStateOf(initialIntervalDays.toString()) }
    var name by remember { mutableStateOf(initialName) }
    var notes by remember { mutableStateOf(initialNotes) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("График удобрения") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Название удобрения") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = intervalText,
                    onValueChange = { intervalText = it },
                    label = { Text("Интервал, дней") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Заметки") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val interval = intervalText.toIntOrNull() ?: initialIntervalDays
                    onConfirm(interval.coerceAtLeast(1), name, notes)
                }
            ) { Text("Сохранить") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Отмена") }
        }
    )
}
