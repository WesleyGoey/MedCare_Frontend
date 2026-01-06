package com.wesley.medcare.data.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra("MEDICINE_NAME") ?: "Obat"
        val alarmId = intent.getIntExtra("ALARM_ID", 0)
        // Kita butuh detailId agar ActionReceiver tahu jadwal mana yang diupdate ke API
        val detailId = intent.getIntExtra("DETAIL_ID", -1)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medcare_reminder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Pengingat Obat", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Intent untuk tombol "Sudah Diminum"
        val takenIntent = Intent(context, ActionReceiver::class.java).apply {
            action = "ACTION_TAKEN"
            putExtra("DETAIL_ID", detailId)
            putExtra("NOTIFICATION_ID", alarmId)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context, alarmId, takenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Waktunya Minum Obat!")
            .setContentText("Saatnya minum obat: $medicineName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            // Menghubungkan tombol ke ActionReceiver
            .addAction(android.R.drawable.ic_menu_save, "SUDAH DIMINUM", takenPendingIntent)
            .build()

        notificationManager.notify(alarmId, notification)
    }
}