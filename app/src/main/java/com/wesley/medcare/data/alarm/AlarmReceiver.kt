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
        val detailId = intent.getIntExtra("DETAIL_ID", -1)
        val notificationId = detailId

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medcare_reminder"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "MedCare Alarm", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        // Action Mark Taken
        val takenIntent = Intent(context, ActionReceiver::class.java).apply {
            action = "ACTION_TAKEN"
            putExtra("NOTIFICATION_ID", notificationId)
            putExtra("DETAIL_ID", detailId)
        }
        val takenPending = PendingIntent.getBroadcast(context, notificationId + 1, takenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        // Action Skip
        val skipIntent = Intent(context, ActionReceiver::class.java).apply {
            action = "ACTION_SKIP"
            putExtra("NOTIFICATION_ID", notificationId)
            putExtra("DETAIL_ID", detailId)
        }
        val skipPending = PendingIntent.getBroadcast(context, notificationId + 2, skipIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Waktunya Minum Obat!")
            .setContentText("Jangan lupa minum: $medicineName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setOngoing(true)
            .setSound(android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI)
            .addAction(0, "Mark Taken", takenPending)
            .addAction(0, "Skip", skipPending)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(notificationId, notification)
    }
}