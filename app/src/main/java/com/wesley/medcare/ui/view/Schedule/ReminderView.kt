package com.wesley.medcare.ui.view.Schedule

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.wesley.medcare.ui.route.AppView
import com.wesley.medcare.ui.viewmodel.ScheduleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun ReminderView(
    navController: NavHostController,
    viewModel: ScheduleViewModel = viewModel()
) {
    val context = LocalContext.current
    val reminders by viewModel.schedules.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // State Tanggal
    var pickedDate by remember { mutableStateOf(LocalDate.now()) }
    val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy")

    // Fetch data otomatis saat tanggal berubah
    LaunchedEffect(pickedDate) {
        viewModel.getSchedulesByDate(pickedDate.format(apiFormatter))
    }

    // Dialog Tanggal
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            pickedDate = LocalDate.of(year, month + 1, dayOfMonth)
        },
        pickedDate.year, pickedDate.monthValue - 1, pickedDate.dayOfMonth
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppView.AddReminderView.name) },
                containerColor = Color(0xFF457AF9),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(30.dp))
            }
        },
        containerColor = Color(0xFFF5F7FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Jadwal Obat",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Card Pemilih Tanggal
            DateSelectorCard(
                formattedDate = pickedDate.format(displayFormatter),
                onClick = { datePickerDialog.show() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF457AF9))
                }
            } else if (reminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Tidak ada jadwal untuk tanggal ini", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(reminders, key = { it.id }) { item ->
                        ReminderTimelineItem(
                            time = item.time,
                            medicineName = item.medicine.name,
                            dosage = item.medicine.dosage,
                            onMarkAsTaken = {
                                viewModel.markAsTaken(item.id, pickedDate.format(apiFormatter))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DateSelectorCard(formattedDate: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF457AF9).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CalendarToday, null, tint = Color(0xFF457AF9), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Tanggal Terpilih", fontSize = 11.sp, color = Color.Gray)
                Text(formattedDate, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Default.KeyboardArrowDown, null, tint = Color.Gray)
        }
    }
}

@Composable
fun ReminderTimelineItem(
    time: String,
    medicineName: String,
    dosage: String,
    onMarkAsTaken: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // Garis Timeline Samping
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF457AF9).copy(alpha = 0.2f))
                    .border(2.dp, Color(0xFF457AF9), CircleShape)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(100.dp)
                    .background(Color(0xFF457AF9).copy(alpha = 0.1f))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, null, tint = Color(0xFF457AF9), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(time, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Text("Belum Diminum", fontSize = 12.sp, color = Color(0xFF457AF9))
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(medicineName, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Text(dosage, fontSize = 14.sp, color = Color.Gray)

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onMarkAsTaken,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF457AF9)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tandai Selesai")
                }
            }
        }
    }
}