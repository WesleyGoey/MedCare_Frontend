package com.wesley.medcare.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.wesley.medcare.data.container.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val detailId = intent.getIntExtra("DETAIL_ID", -1)
        val notificationId = intent.getIntExtra("NOTIFICATION_ID", 0)

        if (detailId == -1) return

        if (action == "ACTION_TAKEN") {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val container = AppContainer(context.applicationContext)
                    val historyRepo = container.historyRepository
                    val medicineRepo = container.medicineRepository

                    // Gunakan Waktu Jakarta
                    val jakartaZone = ZoneId.of("Asia/Jakarta")
                    val now = LocalDateTime.now(jakartaZone)

                    // Format tanggal harus sinkron dengan ViewModel: YYYY-MM-DDT00:00:00
                    val dateFormatted = now.toLocalDate().toString() + "T00:00:00"
                    val timeTaken = now.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"))

                    val isSuccess = historyRepo.markAsTaken(detailId, dateFormatted, timeTaken)

                    if (isSuccess) {
                        Log.d("ALARM_ACTION", "Berhasil Mark As Taken untuk ID: $detailId")
                        // Cek Stok Obat
                        val response = medicineRepo.getAllMedicines()
                        response?.data?.forEach { medicine ->
                            if (medicine.stock <= medicine.minStock) {
                                NotificationHelper(context).showLowStockNotification(
                                    medicine.name,
                                    medicine.stock
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ALARM_ACTION", "Error updating history: ${e.message}")
                }
            }
        }

        // Hapus notifikasi dari tray
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        manager.cancel(notificationId)
    }
}