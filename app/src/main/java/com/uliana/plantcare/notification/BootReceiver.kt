package com.uliana.plantcare.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.uliana.plantcare.PlantCareApplication

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            (context.applicationContext as? PlantCareApplication)
        }
    }
}
