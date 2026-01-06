package com.wesley.medcare

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.wesley.medcare.ui.route.AppRoute
import com.wesley.medcare.ui.theme.MedCareTheme

class MainActivity : ComponentActivity() {

    // Launcher untuk izin notifikasi (Android 13+)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Toast.makeText(this, "Notifikasi diaktifkan", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Izin ditolak. Anda tidak akan menerima pengingat obat.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Cek Izin Notifikasi
        checkNotificationPermission()

        // 2. Cek Izin Exact Alarm (Penting agar alarm tidak telat)
        checkExactAlarmPermission()

        enableEdgeToEdge()
        setContent {
            MedCareTheme {
                AppRoute()
            }
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                // Mengarahkan user ke pengaturan sistem untuk mengizinkan Alarm Tepat Waktu
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                }
                Toast.makeText(this, "Mohon izinkan 'Alarm Tepat Waktu' agar pengingat berfungsi.", Toast.LENGTH_LONG).show()
            }
        }
    }
}