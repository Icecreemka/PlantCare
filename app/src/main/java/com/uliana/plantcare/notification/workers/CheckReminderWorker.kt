package com.uliana.plantcare.notification.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.uliana.plantcare.PlantCareApplication
import com.uliana.plantcare.notification.NotificationHelper

class CheckReminderWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val app = applicationContext as PlantCareApplication
        val hasPlants = app.plantRepository.getAllPlantsSnapshot().isNotEmpty()
        if (hasPlants) {
            NotificationHelper.showNotification(
                context = applicationContext,
                channelId = NotificationHelper.CHANNEL_GENERAL_CHECK,
                notificationId = 9001,
                title = "Проверьте ваши растения 🌿",
                text = "Загляните к цветам — всё ли в порядке с землёй и листьями?"
            )
        }
        return Result.success()
    }
}
