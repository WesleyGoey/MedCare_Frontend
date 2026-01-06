package com.wesley.medcare.ui.view.Schedule

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wesley.medcare.data.alarm.ActionReceiver

class AlarmAlertActivity : ComponentActivity() {
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val medicineName = intent.getStringExtra("MEDICINE_NAME") ?: "Obat"
        val alarmId = intent.getIntExtra("ALARM_ID", 0)
        val detailId = intent.getIntExtra("DETAIL_ID", -1)

        startAlarmSound()

        setContent {
            Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFFF0F7FF)) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("WAKTUNYA MINUM OBAT!", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(medicineName, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF202630))
                    Spacer(modifier = Modifier.height(60.dp))

                    Button(
                        onClick = {
                            // Kirim broadcast ke ActionReceiver untuk catat di Database
                            val takenIntent = Intent(this@AlarmAlertActivity, ActionReceiver::class.java).apply {
                                action = "ACTION_TAKEN"
                                putExtra("DETAIL_ID", detailId)
                                putExtra("NOTIFICATION_ID", alarmId)
                            }
                            sendBroadcast(takenIntent)

                            stopAndExit()
                        },
                        modifier = Modifier.fillMaxWidth().height(60.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F93FF))
                    ) {
                        Text("SAYA SUDAH MINUM", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    private fun startAlarmSound() {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        mediaPlayer = MediaPlayer().apply {
            setDataSource(this@AlarmAlertActivity, alarmUri)
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            isLooping = true
            prepare()
            start()
        }
    }

    private fun stopAndExit() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}