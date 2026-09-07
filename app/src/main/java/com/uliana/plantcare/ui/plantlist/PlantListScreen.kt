package com.uliana.plantcare.ui.plantlist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uliana.plantcare.ui.components.PlantCard
import com.uliana.plantcare.ui.LocationHolder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlantListScreen(
    viewModel: PlantListViewModel,
    onAddPlant: () -> Unit,
    onOpenPlant: (Long) -> Unit
) {
    val plants by viewModel.plants.collectAsState()
    val location = LocationHolder.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Мои растения") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddPlant) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить растение")
            }
        }
    ) { padding ->
        if (plants.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("Пока нет растений. Нажмите «+», чтобы добавить первое 🌱")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(plants, key = { it.id }) { plant ->
                    PlantCard(
                        plant = plant,
                        onClick = { onOpenPlant(plant.id) },
                        onWatered = { viewModel.markWatered(plant.id, location?.first, location?.second) }
                    )
                }
            }
        }
    }
}
