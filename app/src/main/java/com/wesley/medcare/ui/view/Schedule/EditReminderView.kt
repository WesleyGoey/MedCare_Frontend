package com.wesley.medcare.ui.view.Schedule

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.wesley.medcare.data.dto.Schedule.TimeDetailData
import com.wesley.medcare.ui.viewmodel.ScheduleViewModel
import java.time.LocalDate
import kotlin.toString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReminderView(
    navController: NavHostController,
    scheduleId: Int,
    viewModel: ScheduleViewModel = viewModel()
) {
    val context = LocalContext.current
    val scheduleDetails by viewModel.editingScheduleDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // State lokal untuk form
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var medicineId by remember { mutableIntStateOf(0) }
    var timeSlots by remember { mutableStateOf(listOf<String>()) }
    var startDate by remember { mutableStateOf("") }

    // State Dialog
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var selectedTimeIndex by remember { mutableIntStateOf(-1) }

    // Fetch data saat pertama kali dibuka
    LaunchedEffect(scheduleId) {
        viewModel.getScheduleById(scheduleId)
    }

    // Sinkronisasi data dari API ke State Lokal
    LaunchedEffect(scheduleDetails) {
        if (scheduleDetails.isNotEmpty()) {
            val firstDetail = scheduleDetails.first()
            medicineName = firstDetail.medicine.name
            dosage = firstDetail.medicine.dosage
            medicineId = firstDetail.medicine.id
            startDate = firstDetail.schedule.startDate
            timeSlots = scheduleDetails.map { it.time.substring(0, 5) }
        }
    }

    // Handle Pesan Sukses/Error
    LaunchedEffect(successMessage) {
        successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Reminder", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(Modifier.height(8.dp))
                Text("Update reminder information", color = Color.Gray, fontSize = 14.sp)
            }

            // Section Medication
            item {
                SectionTitle("Medication")
                Spacer(Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F7FF)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = Color.White
                        ) {
                            Icon(
                                imageVector = Icons.Default.MedicalServices,
                                contentDescription = null,
                                modifier = Modifier.padding(10.dp),
                                tint = Color(0xFFE57373)
                            )
                        }
                        Spacer(Modifier.width(16.dp))
                        Column {
                            Text(medicineName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF202630))
                            Text(dosage, color = Color.Gray, fontSize = 14.sp)
                        }
                    }
                }
            }

            // Header Waktu (Schedule Time + Add Time)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Schedule Time", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF202630))
                    if (timeSlots.size < 3) {
                        TextButton(onClick = {
                            selectedTimeIndex = -1
                            showTimePickerDialog = true
                        }) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Add Time")
                        }
                    }
                }
            }

            // List Kartu Jam (Identik dengan AddReminderView)
            items(timeSlots) { time ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedTimeIndex = timeSlots.indexOf(time)
                            showTimePickerDialog = true
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF7FF), // Biru muda sesuai screenshot
                    border = BorderStroke(1.dp, Color(0xFFD9E9FF))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccessTime,
                                contentDescription = null,
                                tint = Color(0xFF2F93FF) // Biru ikon
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = time,
                                fontSize = 16.sp,
                                color = Color(0xFF202630),
                                fontWeight = FontWeight.Medium
                            )
                        }
                        IconButton(
                            onClick = { timeSlots = timeSlots.filter { it != time } },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFE53935) // Merah ikon
                            )
                        }
                    }
                }
            }

            // Tombol Simpan & Hapus
            item {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.updateSchedule(
                            scheduleId = scheduleId,
                            medicineId = medicineId,
                            startDate = startDate,
                            details = timeSlots.map { TimeDetailData(time = it) }
                        )
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F93FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.deleteSchedule(
                            scheduleId = scheduleId,
                            dateToRefresh = LocalDate.now().toString()
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE53935)),
                    border = BorderStroke(1.dp, Color(0xFFE53935))
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Delete Reminder", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(30.dp))
            }
        }
    }

    // TimePickerDialog Logic
    if (showTimePickerDialog) {
        TimePickerDialog(
            initialTime = if (selectedTimeIndex >= 0) timeSlots[selectedTimeIndex] else "08:00",
            onConfirm = { time ->
                if (selectedTimeIndex >= 0) {
                    timeSlots = timeSlots.toMutableList().apply { set(selectedTimeIndex, time) }
                } else {
                    timeSlots = timeSlots + time
                }
                showTimePickerDialog = false
            },
            onDismiss = { showTimePickerDialog = false }
        )
    }
}
@Composable
fun SectionTitle(title: String) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF202630))
}
