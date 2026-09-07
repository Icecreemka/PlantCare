package com.uliana.plantcare.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.uliana.plantcare.MainActivity
import com.uliana.plantcare.R
import android.app.PendingIntent
import android.content.Intent

object NotificationHelper {
    const val CHANNEL_WATERING = "channel_watering"
    const val CHANNEL_FERTILIZING = "channel_fertilizing"
    const val CHANNEL_GENERAL_CHECK = "channel_general_check"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_WATERING, "Полив", NotificationManager.IMPORTANCE_DEFAULT)
        )
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_FERTILIZING, "Удобрение", NotificationManager.IMPORTANCE_DEFAULT)
        )
        manager.createNotificationChannel(
            NotificationChannel(CHANNEL_GENERAL_CHECK, "Проверка растений", NotificationManager.IMPORTANCE_LOW)
        )
    }

    fun showNotification(
        context: Context,
        channelId: String,
        notificationId: Int,
        title: String,
        text: String
    ) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, notificationId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_plant_notification)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (androidx.core.content.ContextCompat.checkSelfPermission(
                context, android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
        ) {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        }
    }
}
