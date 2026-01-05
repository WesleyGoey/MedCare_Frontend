package com.wesley.medcare.ui.view.Schedule

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import com.wesley.medcare.ui.viewmodel.ScheduleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditReminderView(
    navController: NavHostController,
    scheduleId: Int,
    viewModel: ScheduleViewModel = viewModel(),
    medicineViewModel: MedicineViewModel = viewModel()
) {
    val context = LocalContext.current
    val scheduleDetails by viewModel.editingScheduleDetails.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // State lokal untuk form
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var medicineId by remember { mutableIntStateOf(0) }
    var timeSlots by remember { mutableStateOf(mutableListOf<String>()) }
    var startDate by remember { mutableStateOf("") }

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
            // Ambil waktu format HH:mm
            timeSlots = scheduleDetails.map { it.time.substring(0, 5) }.toMutableList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Reminder", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color(0xFF457AF9))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text("Update reminder information", color = Color.Gray, fontSize = 14.sp)

            // Section Medication
            SectionTitle("Medication")
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F9FF)),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(50.dp),
                        shape = CircleShape,
                        color = Color.White
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = Color(0xFFE57373)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(medicineName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(dosage, color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            // Section Reminder Times
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                Column {
                    SectionTitle("Reminder Times")
                    Text("${timeSlots.size} reminder times per day", color = Color.Gray, fontSize = 12.sp)
                }
                Text("(Max 3)", color = Color(0xFF457AF9), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            // List of Times
            timeSlots.forEachIndexed { index, time ->
                TimeRow(
                    label = "Time ${index + 1}",
                    time = time,
                    onDelete = {
                        val newList = timeSlots.toMutableList()
                        newList.removeAt(index)
                        timeSlots = newList
                    },
                    onTimeClick = {
                        // Logic Show TimePickerDialog
                    }
                )
            }

            // Add Time Button
            if (timeSlots.size < 3) {
                OutlinedButton(
                    onClick = { /* Logic Add Time */ },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF457AF9)),
                    border = BorderStroke(1.dp, Color(0xFF457AF9))
                ) {
                    Icon(Icons.Default.Add, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Add Time")
                }
            }

            Spacer(Modifier.height(20.dp))

            // Action Buttons
            Button(
                onClick = {
                    viewModel.updateSchedule(
                        scheduleId = scheduleId,
                        medicineId = medicineId,
                        startDate = startDate,
                        details = timeSlots.map { TimeDetailData(it) }
                    )
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF457AF9))
            ) {
                Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { viewModel.deleteSchedule(scheduleId) },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFE57373)),
                border = BorderStroke(1.dp, Color(0xFFE57373))
            ) {
                Icon(Icons.Default.Delete, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Delete Reminder", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF202630))
}

@Composable
fun TimeRow(label: String, time: String, onDelete: () -> Unit, onTimeClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFBC02D).copy(alpha = 0.2f)
            ) {
                Icon(Icons.Default.AccessTime, null, modifier = Modifier.padding(16.dp), tint = Color(0xFFFBC02D))
            }

            Surface(
                modifier = Modifier.weight(1f).height(56.dp).clickable { onTimeClick() },
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF5F5F5)
            ) {
                Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(time, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(56.dp).background(Color(0xFFFFEBEE), RoundedCornerShape(12.dp))
            ) {
                Icon(Icons.Default.Delete, null, tint = Color(0xFFE57373))
            }
        }
    }
}