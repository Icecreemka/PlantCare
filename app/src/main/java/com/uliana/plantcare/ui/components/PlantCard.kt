package com.uliana.plantcare.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.uliana.plantcare.data.local.entity.Plant
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Composable
fun PlantCard(
    plant: Plant,
    onClick: () -> Unit,
    onWatered: () -> Unit,
    modifier: Modifier = Modifier
) {
    val today = LocalDate.now()
    val daysLeft = ChronoUnit.DAYS.between(today, plant.nextWateringDate)
    val isOverdue = daysLeft < 0

    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (plant.photoUri != null) {
                AsyncImage(
                    model = plant.photoUri,
                    contentDescription = plant.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.LocalFlorist, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(plant.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(plant.category.displayName, style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = when {
                        isOverdue -> "Пора полить! (просрочено на ${-daysLeft} дн.)"
                        daysLeft == 0L -> "Полить сегодня"
                        else -> "Полив через $daysLeft дн."
                    },
                    color = if (isOverdue) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            IconButton(onClick = onWatered) {
                Icon(Icons.Filled.WaterDrop, contentDescription = "Отметить полив")
            }
        }
    }
}
