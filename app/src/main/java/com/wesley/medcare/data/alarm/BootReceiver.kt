package com.wesley.medcare.data.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.wesley.medcare.data.container.AppContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON") {

            val container = AppContainer(context.applicationContext)
            val repository = container.scheduleRepository
            val scheduler = AlarmScheduler(context)

            CoroutineScope(Dispatchers.IO).launch {
                val today = LocalDate.now().toString()
                val response = repository.getScheduleWithDetailsByDate(today)

                response?.data?.forEach { detail ->
                    // Menggunakan medicineId * 100 sebagai ID unik
                    scheduler.schedule(
                        id = detail.id,
                        time = detail.time,
                        medicineName = detail.medicine.name
                    )
                }
            }
        }
    }
}