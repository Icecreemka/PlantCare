package com.uliana.plantcare.notification.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.uliana.plantcare.PlantCareApplication
import com.uliana.plantcare.notification.NotificationHelper
import java.time.LocalDate

class WateringFertilizerWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as PlantCareApplication
        val repo = app.plantRepository
        val today = LocalDate.now()

        val plants = repo.getAllPlantsSnapshot()
        plants.forEach { plant ->
            if (!plant.nextWateringDate.isAfter(today)) {
                NotificationHelper.showNotification(
                    context = applicationContext,
                    channelId = NotificationHelper.CHANNEL_WATERING,
                    notificationId = ("water_" + plant.id).hashCode(),
                    title = "Пора полить: ${plant.name}",
                    text = "Расчётная дата полива: ${plant.nextWateringDate}"
                )
            }
        }

        val schedules = repo.getAllActiveFertilizerSchedules()
        schedules.forEach { schedule ->
            if (!schedule.nextFertilizeDate.isAfter(today)) {
                val plantName = plants.find { it.id == schedule.plantId }?.name ?: "растение"
                NotificationHelper.showNotification(
                    context = applicationContext,
                    channelId = NotificationHelper.CHANNEL_FERTILIZING,
                    notificationId = ("fert_" + schedule.id).hashCode(),
                    title = "Пора подкормить: $plantName",
                    text = schedule.fertilizerName.ifBlank { "По графику: каждые ${schedule.intervalDays} дн." }
                )
            }
        }

        return Result.success()
    }
}
