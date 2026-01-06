package com.wesley.medcare.ui.view.Home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.wesley.medcare.data.dto.Medicine.MedicineDataWithSchedule
import com.wesley.medcare.data.dto.Schedule.DetailData
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import com.wesley.medcare.ui.viewmodel.ScheduleViewModel
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun HomeView(
    navController: NavHostController,
    medicineVM: MedicineViewModel = viewModel(),
    scheduleVM: ScheduleViewModel = viewModel()
) {
    val medicines by medicineVM.medicines.collectAsState()
    val schedules by scheduleVM.schedules.collectAsState()

    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    val displayDate = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH))

    // Palette Warna
    val primaryBlue = Color(0xFF457AF9)
    val backgroundGray = Color(0xFFF5F7FA)
    val lowStockRed = Color(0xFFFF5A5F)
    val successGreen = Color(0xFF2FB6A3)
    val textDark = Color(0xFF2B2F38)
    val textGray = Color(0xFF8A94A6)

    LaunchedEffect(Unit) {
        medicineVM.getAllMedicines()
        scheduleVM.getSchedulesByDate(today)
    }

    Scaffold(containerColor = Color.White) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            // Padding bawah dikurangi (dari 100.dp ke 20.dp) agar tidak terlalu kosong
            contentPadding = PaddingValues(top = 30.dp, bottom = 20.dp)
        ) {
            // --- HEADER ---
            item {
                Column {
                    Text("Good Morning!", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = textDark)
                    Text(displayDate, fontSize = 16.sp, color = textGray)
                }
            }

            // --- UPCOMING MEDICATION ---
            val currentTime = LocalTime.now()
            val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

            val upcoming = schedules
                .filter {
                    val scheduleTime = LocalTime.parse(it.time, timeFormatter)
                    (it.history?.firstOrNull()?.status ?: "PENDING") == "PENDING" && scheduleTime.isAfter(currentTime)
                }
                .minByOrNull { LocalTime.parse(it.time, timeFormatter) }

            upcoming?.let {
                item {
                    Text("Upcoming Medication", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
                    Spacer(modifier = Modifier.height(12.dp))
                    UpcomingCard(it, primaryBlue, lowStockRed)
                }
            }

            // --- LOW STOCK ALERTS ---
            val lowStockItems = medicines.filter { it.stock <= it.minStock }
            if (lowStockItems.isNotEmpty()) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        lowStockItems.forEach { medicine ->
                            LowStockCard(medicine, lowStockRed)
                        }
                    }
                }
            }

            // --- TODAY'S SCHEDULE ---
            item {
                Text("Today's Schedule", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = textDark)
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = backgroundGray),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        if (schedules.isEmpty()) {
                            Text("No medication for today", color = textGray)
                        } else {
                            val sortedSchedules = schedules.sortedBy { LocalTime.parse(it.time, timeFormatter) }

                            sortedSchedules.forEach { item ->
                                val isDone = (item.history?.firstOrNull()?.status ?: "PENDING") == "DONE"

                                // Jika Done -> Hijau, Jika Belum -> Abu-abu
                                val accentColor = if (isDone) successGreen else textGray

                                ScheduleSummaryItem(item, isDone, accentColor)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ... (Sub-komponen UpcomingCard, LowStockCard, dan ScheduleSummaryItem tetap sama seperti sebelumnya)

@Composable
fun UpcomingCard(schedule: DetailData, themeColor: Color, alertColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFF5F7FA)),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(themeColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Notifications, null, tint = Color.White, modifier = Modifier.size(28.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(schedule.medicine.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color(0xFF2B2F38))
                Text(schedule.medicine.dosage, color = Color(0xFF8A94A6))
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AccessTime, null, tint = alertColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        "Due soon • ${schedule.time}",
                        color = alertColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun LowStockCard(medicine: MedicineDataWithSchedule, color: Color) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        border = BorderStroke(1.dp, color.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Warning, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("Low Stock Alert", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF2B2F38))
                Text("${medicine.name} - Only ${medicine.stock} pills left", color = Color(0xFF8A94A6), fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun ScheduleSummaryItem(item: DetailData, isDone: Boolean, accentColor: Color) {
    // Latar belakang hijau sangat tipis (30% dari BFEAE3) jika sudah selesai
    val backgroundColor = if (isDone) Color(0xFFBFEAE3).copy(alpha = 0.3f) else Color.White

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = backgroundColor,
        border = BorderStroke(1.dp, if (isDone) accentColor.copy(alpha = 0.2f) else Color(0xFFF5F7FA))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(accentColor, RoundedCornerShape(2.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${item.time} - ${item.medicine.name}",
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp,
                color = Color(0xFF2B2F38)
            )
            if (isDone) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}