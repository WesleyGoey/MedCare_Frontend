package com.wesley.medcare.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.wesley.medcare.data.container.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val detailId = intent.getIntExtra("DETAIL_ID", -1)
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val container = AppContainer(context.applicationContext)
                val historyRepo = container.historyRepository
                val date = LocalDate.now().toString()

                if (action == "ACTION_TAKEN") {
                    val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                    historyRepo.markAsTaken(detailId, date, time)
                }
            } catch (e: Exception) {
                Log.e("ALARM_API", "Error: ${e.message}")
            }
        }

        // Tutup notifikasi tray
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.cancel(notificationId)
    }
}