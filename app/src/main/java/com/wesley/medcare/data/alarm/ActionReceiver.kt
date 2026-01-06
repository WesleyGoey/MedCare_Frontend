package com.wesley.medcare.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
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

        // Gunakan GlobalScope atau CoroutineScope untuk tugas background singkat
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val container = AppContainer(context.applicationContext)
                val historyRepo = container.historyRepository
                val date = LocalDate.now().toString()

                when (action) {
                    "ACTION_TAKEN" -> {
                        val time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
                        val success = historyRepo.markAsTaken(detailId, date, time)

                        if (success) {
                            Log.d("ALARM_API", "Berhasil kirim ke Backend!")
                        } else {
                            Log.e("ALARM_API", "Gagal: Pastikan 'npm run dev' jalan & Wi-Fi sama.")
                        }
                    }
                    "ACTION_SKIP" -> {
                        val success = historyRepo.skipOccurrence(detailId, date)
                        if (success) Log.d("ALARM_API", "Skip berhasil dicatat")
                    }
                }
            } catch (e: Exception) {
                Log.e("ALARM_API", "Error koneksi: Server mungkin mati.")
            }
        }

        // Tutup notifikasi setelah tombol diklik
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.cancel(notificationId)
    }
}