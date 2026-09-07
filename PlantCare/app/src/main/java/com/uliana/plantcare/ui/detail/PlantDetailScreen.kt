package com.uliana.plantcare.ui.detail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.uliana.plantcare.data.local.entity.CareEventType
import com.uliana.plantcare.ui.LocationHolder
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantDetailScreen(
    viewModel: PlantDetailViewModel,
    onBack: () -> Unit
) {
    val plant by viewModel.plant.collectAsState()
    val schedule by viewModel.fertilizerSchedule.collectAsState()
    val history by viewModel.careHistory.collectAsState()
    val location = LocationHolder.current

    var notesDraft by remember(plant?.id) { mutableStateOf(plant?.wateringNotes ?: "") }
    var showFertilizerDialog by remember { mutableStateOf(false) }
    var showRepotDialog by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val currentPlant = plant ?: return

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentPlant.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Text("←", modifier = Modifier.padding(start = 12.dp)) }
                },
                actions = {
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Удалить растение")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                if (currentPlant.photoUri != null) {
                    AsyncImage(
                        model = currentPlant.photoUri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp))
                    )
                }
            }

            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Полив", style = MaterialTheme.typography.titleMedium)
                        Text("Категория: ${currentPlant.category.displayName}")
                        Text("Горшок: ⌀${currentPlant.potDiameterCm} см, высота ${currentPlant.potHeightCm} см")
                        Text("Последний полив: ${currentPlant.lastWateredDate.format(dateFormatter)}")
                        Text(
                            "Следующий полив: ${currentPlant.nextWateringDate.format(dateFormatter)}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text("Текущий интервал: ${"%.1f".format(currentPlant.currentIntervalDays)} дн. (база ${"%.1f".format(currentPlant.baseWateringIntervalDays)} дн.)")

                        Spacer(modifier = Modifier.height(4.dp))
                        Button(
                            onClick = { viewModel.markWatered(location?.first, location?.second) },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Отметить полив сегодня") }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.reportDryness(true) },
                                modifier = Modifier.weight(1f)
                            ) { Text("Высохло раньше") }
                            OutlinedButton(
                                onClick = { viewModel.reportDryness(false) },
                                modifier = Modifier.weight(1f)
                            ) { Text("Ещё влажно") }
                        }

                        OutlinedButton(
                            onClick = { viewModel.markChecked() },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Отметить проверку сегодня") }
                    }
                }
            }

            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Удобрение", style = MaterialTheme.typography.titleMedium)
                        if (schedule == null) {
                            Text("График удобрения не задан")
                            Button(onClick = { showFertilizerDialog = true }) { Text("Задать график") }
                        } else {
                            val s = schedule!!
                            Text("Удобрение: ${s.fertilizerName.ifBlank { "не указано" }}")
                            Text("Интервал: каждые ${s.intervalDays} дн.")
                            Text("Последняя подкормка: ${s.lastFertilizedDate.format(dateFormatter)}")
                            Text("Следующая подкормка: ${s.nextFertilizeDate.format(dateFormatter)}")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { viewModel.markFertilized(s) }) { Text("Подкормил(а) сегодня") }
                                OutlinedButton(onClick = { showFertilizerDialog = true }) { Text("Изменить график") }
                            }
                        }
                    }
                }
            }

            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Особенности полива", style = MaterialTheme.typography.titleMedium)
                        OutlinedTextField(
                            value = notesDraft,
                            onValueChange = { notesDraft = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            label = { Text("Заметки") }
                        )
                        Button(
                            onClick = { viewModel.updateWateringNotes(notesDraft) },
                            modifier = Modifier.align(androidx.compose.ui.Alignment.End)
                        ) { Text("Сохранить заметку") }
                    }
                }
            }

            item {
                Card(shape = RoundedCornerShape(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Пересадка", style = MaterialTheme.typography.titleMedium)
                        Text(
                            currentPlant.lastRepotDate?.let { "Последняя пересадка: ${it.format(dateFormatter)}" }
                                ?: "Дата последней пересадки не указана"
                        )
                        OutlinedButton(onClick = { showRepotDialog = true }) { Text("Отметить пересадку сегодня") }
                    }
                }
            }

            item {
                Text("История ухода", style = MaterialTheme.typography.titleMedium)
            }

            items(history) { event ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    Text(event.date.format(dateFormatter), modifier = Modifier.width(100.dp))
                    Text(careEventLabel(event.type))
                }
            }
        }
    }

    if (showFertilizerDialog) {
        FertilizerDialog(
            initialIntervalDays = schedule?.intervalDays ?: 14,
            initialName = schedule?.fertilizerName ?: "",
            initialNotes = schedule?.notes ?: "",
            onDismiss = { showFertilizerDialog = false },
            onConfirm = { interval, fertName, fertNotes ->
                viewModel.setFertilizerSchedule(interval, fertName, fertNotes)
                showFertilizerDialog = false
            }
        )
    }

    if (showRepotDialog) {
        AlertDialog(
            onDismissRequest = { showRepotDialog = false },
            title = { Text("Отметить пересадку") },
            text = { Text("Записать сегодняшнюю дату как дату последней пересадки?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateRepotDate(java.time.LocalDate.now())
                    showRepotDialog = false
                }) { Text("Да") }
            },
            dismissButton = { TextButton(onClick = { showRepotDialog = false }) { Text("Отмена") } }
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Удалить растение?") },
            text = { Text("Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(onClick = { viewModel.deletePlant(onBack) }) { Text("Удалить") }
            },
            dismissButton = { TextButton(onClick = { showDeleteConfirm = false }) { Text("Отмена") } }
        )
    }
}

private fun careEventLabel(type: CareEventType): String = when (type) {
    CareEventType.WATERING -> "💧 Полив"
    CareEventType.FERTILIZING -> "🌱 Удобрение"
    CareEventType.CHECK -> "👀 Проверка"
    CareEventType.REPOT -> "🪴 Пересадка"
    CareEventType.NOTE -> "📝 Заметка"
    CareEventType.DRIED_EARLY -> "⏱ Высохло раньше прогноза"
    CareEventType.STILL_MOIST -> "⏳ Ещё влажно на дату прогноза"
}
