package com.wesley.medcare

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.wesley.medcare.ui.route.AppRoute
import com.wesley.medcare.ui.theme.MedCareTheme

class MainActivity : ComponentActivity() {

    // Helper untuk meminta izin notifikasi secara modern
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            // Jika user menolak, beritahu bahwa alarm tidak akan muncul
            Toast.makeText(this, "Izin notifikasi ditolak. Alarm tidak akan muncul.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Panggil fungsi cek izin saat aplikasi dibuka
        checkNotificationPermission()

        enableEdgeToEdge()
        setContent {
            MedCareTheme {
                AppRoute()
            }
        }
    }

    private fun checkNotificationPermission() {
        // Izin POST_NOTIFICATIONS hanya diperlukan untuk Android 13 (API 33) ke atas
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Izin sudah diberikan, tidak perlu melakukan apa-apa
                }
                else -> {
                    // Minta izin ke user
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MedCareTheme {
        Greeting("Android")
    }
}