package com.wesley.medcare.data.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(id: Int, time: String, medicineName: String) {
        val parts = time.split(":")
        if (parts.size < 2) return

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, parts[0].toInt())
            set(Calendar.MINUTE, parts[1].toInt())
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) add(Calendar.DATE, 1)
        }

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("MEDICINE_NAME", medicineName)
            putExtra("ALARM_ID", id)
            // Detail ID diasumsikan id/100 sesuai pola di BootReceiver kamu
            putExtra("DETAIL_ID", id / 100)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    fun cancel(id: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        alarmManager.cancel(pendingIntent)
    }
}