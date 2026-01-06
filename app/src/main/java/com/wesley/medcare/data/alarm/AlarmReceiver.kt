package com.wesley.medcare.data.alarm

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.wesley.medcare.ui.view.Schedule.AlarmAlertActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val medicineName = intent.getStringExtra("MEDICINE_NAME") ?: "Obat"
        val alarmId = intent.getIntExtra("ALARM_ID", 0)
        val detailId = intent.getIntExtra("DETAIL_ID", -1)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "medcare_reminder_channel"
        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()

            val channel = NotificationChannel(channelId, "Pengingat Obat", NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(alarmSound, audioAttributes)
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 1. Intent untuk Tombol di Tray
        val takenIntent = Intent(context, ActionReceiver::class.java).apply {
            action = "ACTION_TAKEN"
            putExtra("DETAIL_ID", detailId)
            putExtra("NOTIFICATION_ID", alarmId)
        }
        val takenPendingIntent = PendingIntent.getBroadcast(
            context, alarmId, takenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 2. Intent untuk Full Screen Activity
        val fullScreenIntent = Intent(context, AlarmAlertActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
            putExtra("MEDICINE_NAME", medicineName)
            putExtra("ALARM_ID", alarmId)
            putExtra("DETAIL_ID", detailId)
        }
        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, alarmId, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Waktunya Minum Obat!")
            .setContentText("Saatnya minum obat: $medicineName")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .setOngoing(true)
            .setAutoCancel(true)
            .setSound(alarmSound)
            .addAction(android.R.drawable.ic_menu_save, "SUDAH DIMINUM", takenPendingIntent)
            .build()

        notificationManager.notify(alarmId, notification)
    }
}