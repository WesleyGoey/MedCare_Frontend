package com.wesley.medcare.ui.view.Schedule

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wesley.medcare.R
import com.wesley.medcare.data.dto.Schedule.TimeDetailData
import com.wesley.medcare.ui.view.components.BackTopAppBar
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import com.wesley.medcare.ui.viewmodel.ScheduleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReminderView(
    navController: NavHostController = rememberNavController(),
    medicineViewModel: MedicineViewModel = viewModel(),
    scheduleViewModel: ScheduleViewModel = viewModel(),
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val medicines by medicineViewModel.medicines.collectAsState()
    val isLoading by scheduleViewModel.isLoading.collectAsState()
    val successMessage by scheduleViewModel.successMessage.collectAsState()
    val errorMessage by scheduleViewModel.errorMessage.collectAsState()

    var selectedMedicineId by remember { mutableIntStateOf(0) }
    var selectedMedicineName by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    var showDatePicker by remember { mutableStateOf(false) }
    var timeSlots by remember { mutableStateOf(listOf("08:00")) }
    var showTimePickerDialog by remember { mutableStateOf(false) }
    var selectedTimeIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(Unit) {
        medicineViewModel.getAllMedicines()
    }

    LaunchedEffect(successMessage) {
        successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            scheduleViewModel.clearMessages()
            onBack()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            scheduleViewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = {
            BackTopAppBar(title = "Back", onBack = { onBack() })
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 32.dp)
        ) {
            item {
                Column {
                    Text(text = "Add Reminder", fontSize = 26.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                    Text(text = "Create a new reminder for your medication", fontSize = 14.sp, color = Color(0xFF757575))
                }
            }

            // --- Medicine Selection Section ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Select Medication", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A2E), modifier = Modifier.padding(bottom = 12.dp))
                        medicines.forEach { medicine ->
                            val isSelected = selectedMedicineId == medicine.id
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (isSelected) Color(0xFFF0F4FF) else Color(0xFFF5F5F5))
                                    .border(width = if (isSelected) 1.5.dp else 0.dp, color = if (isSelected) Color(0xFF457AF9) else Color.Transparent, shape = RoundedCornerShape(20.dp))
                                    .clickable {
                                        selectedMedicineId = medicine.id
                                        selectedMedicineName = medicine.name
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp)).background(Color.White), contentAlignment = Alignment.Center) {
                                    Image(painter = painterResource(id = R.drawable.logo), contentDescription = null, modifier = Modifier.fillMaxSize())
                                }
                                Spacer(Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(medicine.name, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                                    Text(medicine.dosage, fontSize = 13.sp, color = Color(0xFF757575))
                                }
                                if (isSelected) Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF457AF9))
                            }
                        }
                    }
                }
            }

            // --- Date Picker Input Section ---
            item {
                Column {
                    Text("Start Date", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A2E))
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        shadowElevation = 2.dp
                    ) {
                        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")), color = Color(0xFF1A1A2E), fontWeight = FontWeight.Medium)
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF457AF9))
                        }
                    }
                }
            }

            // --- Reminder Times Section ---
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Reminder Times", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1A1A2E))
                        Text("Max 3 reminder times per day", fontSize = 12.sp, color = Color(0xFF757575), modifier = Modifier.padding(bottom = 16.dp))
                        timeSlots.forEachIndexed { index, time ->
                            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(48.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFFBC02D)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color.White)
                                }
                                Spacer(Modifier.width(12.dp))
                                Box(modifier = Modifier.weight(1f).height(48.dp).clip(RoundedCornerShape(14.dp)).background(Color(0xFFF5F7FA)).clickable {
                                    selectedTimeIndex = index
                                    showTimePickerDialog = true
                                }.padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
                                    Text(time, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                                }
                                if (timeSlots.size > 1) {
                                    IconButton(onClick = { timeSlots = timeSlots.filterIndexed { i, _ -> i != index } }) {
                                        Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Delete", tint = Color(0xFFE53935))
                                    }
                                }
                            }
                        }
                        if (timeSlots.size < 3) {
                            Button(
                                onClick = { selectedTimeIndex = -1; showTimePickerDialog = true },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF457AF9)),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.White)
                                Spacer(Modifier.width(8.dp))
                                Text("Add Time", fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }

            // --- Main Button ---
            item {
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        if (selectedMedicineId == 0) {
                            Toast.makeText(context, "Please select a medicine", Toast.LENGTH_SHORT).show()
                        } else {
                            val details = timeSlots.map { TimeDetailData(time = it) }
                            scheduleViewModel.createSchedule(
                                medicineId = selectedMedicineId,
                                medicineName = selectedMedicineName, // Pindahkan ke posisi kedua
                                startDate = selectedDate.toString(),
                                details = details
                            )
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(4.dp, RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF457AF9)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    else Text("Add Reminder", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }
    }

    // --- Time Picker Dialog ---
    if (showTimePickerDialog) {
        TimePickerDialog(
            initialTime = if (selectedTimeIndex >= 0) timeSlots[selectedTimeIndex] else "08:00",
            onConfirm = { time ->
                if (selectedTimeIndex >= 0) timeSlots = timeSlots.toMutableList().apply { set(selectedTimeIndex, time) }
                else timeSlots = timeSlots + time
                showTimePickerDialog = false
            },
            onDismiss = { showTimePickerDialog = false }
        )
    }

    // --- REVISI DATE PICKER DIALOG ---
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        selectedDate = java.time.Instant.ofEpochMilli(it).atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK", color = Color(0xFF457AF9), fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = Color(0xFF8A94A6)) }
            },
            colors = DatePickerDefaults.colors(containerColor = Color.White)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF8A94A6),
                    headlineContentColor = Color(0xFF1A1A2E),
                    selectedDayContainerColor = Color(0xFF457AF9),
                    selectedDayContentColor = Color.White,
                    disabledSelectedDayContainerColor = Color(0xFF457AF9).copy(alpha = 0.38f),
                    todayContentColor = Color(0xFF457AF9),
                    todayDateBorderColor = Color(0xFF457AF9),
                    dayContentColor = Color(0xFF1A1A2E),
                    disabledDayContentColor = Color(0xFF1A1A2E).copy(alpha = 0.38f),
                    weekdayContentColor = Color(0xFF8A94A6),
                    navigationContentColor = Color(0xFF1A1A2E),
                    yearContentColor = Color(0xFF1A1A2E),
                    currentYearContentColor = Color(0xFF457AF9),
                    selectedYearContainerColor = Color(0xFF457AF9),
                    selectedYearContentColor = Color.White,
                    dateTextFieldColors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color(0xFF1A1A2E),
                        unfocusedTextColor = Color(0xFF1A1A2E),
                        cursorColor = Color(0xFF457AF9),
                        focusedLabelColor = Color(0xFF457AF9),
                        unfocusedLabelColor = Color(0xFF8A94A6),
                        errorContainerColor = Color(0xFFFFF0F0),
                        errorTextColor = Color(0xFFFF5A5F),
                        errorCursorColor = Color(0xFFFF5A5F),
                        errorIndicatorColor = Color.Transparent,
                        errorLabelColor = Color(0xFFFF5A5F),
                        errorSupportingTextColor = Color(0xFFFF5A5F)
                    )
                )
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(initialTime: String, onConfirm: (String) -> Unit, onDismiss: () -> Unit) {
    val timeParts = initialTime.split(":")
    val state = rememberTimePickerState(initialHour = timeParts[0].toIntOrNull() ?: 8, initialMinute = timeParts[1].toIntOrNull() ?: 0, is24Hour = true)
    AlertDialog(onDismissRequest = onDismiss, properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(shape = RoundedCornerShape(28.dp), color = Color.White, tonalElevation = 6.dp, modifier = Modifier.width(IntrinsicSize.Min).padding(24.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                Text(text = "Select Time", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E), modifier = Modifier.align(Alignment.Start).padding(bottom = 20.dp))
                TimePicker(
                    state = state,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color(0xFFF5F7FA),
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = Color(0xFF1A1A2E),
                        selectorColor = Color(0xFF457AF9),
                        timeSelectorSelectedContainerColor = Color(0xFFECF1FF),
                        timeSelectorUnselectedContainerColor = Color(0xFFF5F7FA),
                        timeSelectorSelectedContentColor = Color(0xFF457AF9),
                        timeSelectorUnselectedContentColor = Color(0xFF1A1A2E)
                    )
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel", color = Color(0xFF8A94A6)) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { onConfirm(String.format("%02d:%02d", state.hour, state.minute)) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF457AF9), contentColor = Color.White), shape = RoundedCornerShape(12.dp)) {
                        Text("Set Time", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}