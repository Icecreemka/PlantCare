package com.uliana.plantcare

import android.app.Application
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.uliana.plantcare.data.local.AppDatabase
import com.uliana.plantcare.data.repository.PlantRepository
import com.uliana.plantcare.data.repository.WeatherRepository
import com.uliana.plantcare.notification.NotificationHelper
import com.uliana.plantcare.notification.workers.CheckReminderWorker
import com.uliana.plantcare.notification.workers.WateringFertilizerWorker
import java.util.concurrent.TimeUnit

class PlantCareApplication : Application() {

    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
    val weatherRepository: WeatherRepository by lazy { WeatherRepository() }
    val plantRepository: PlantRepository by lazy {
        PlantRepository(
            plantDao = database.plantDao(),
            fertilizerDao = database.fertilizerDao(),
            careEventDao = database.careEventDao(),
            weatherRepository = weatherRepository
        )
    }

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createChannels(this)
        scheduleBackgroundWork()
    }

    private fun scheduleBackgroundWork() {
        val workManager = WorkManager.getInstance(this)

        val wateringRequest = PeriodicWorkRequestBuilder<WateringFertilizerWorker>(24, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "watering_fertilizer_check",
            ExistingPeriodicWorkPolicy.KEEP,
            wateringRequest
        )

        val checkRequest = PeriodicWorkRequestBuilder<CheckReminderWorker>(60, TimeUnit.HOURS)
            .build()
        workManager.enqueueUniquePeriodicWork(
            "general_check_reminder",
            ExistingPeriodicWorkPolicy.KEEP,
            checkRequest
        )
    }
}
