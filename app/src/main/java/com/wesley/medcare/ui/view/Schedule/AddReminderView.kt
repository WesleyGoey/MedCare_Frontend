package com.wesley.medcare.ui.view.Schedule

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.navigation.compose.rememberNavController
import com.wesley.medcare.data.dto.Schedule.TimeDetailData
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

    // State Variables
    var selectedMedicineId by remember { mutableIntStateOf(0) }
    var selectedMedicineName by remember { mutableStateOf("") }
    var showMedicineDropdown by remember { mutableStateOf(false) }
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
        topBar = {
            TopAppBar(
                title = { Text("Add Reminder", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { onBack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF202630)
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Medicine Selector
            item {
                Column {
                    Text("Medicine", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF202630))
                    Spacer(Modifier.height(8.dp))
                    Box {
                        OutlinedButton(
                            onClick = { showMedicineDropdown = !showMedicineDropdown },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFF0F7FF))
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    if (selectedMedicineName.isEmpty()) "Select medicine" else selectedMedicineName,
                                    color = if (selectedMedicineName.isEmpty()) Color.Gray else Color(0xFF202630)
                                )
                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color(0xFF2F93FF))
                            }
                        }
                        DropdownMenu(
                            expanded = showMedicineDropdown,
                            onDismissRequest = { showMedicineDropdown = false },
                            modifier = Modifier.fillMaxWidth(0.9f)
                        ) {
                            medicines.forEach { medicine ->
                                DropdownMenuItem(
                                    text = { Text("${medicine.name} (${medicine.dosage})") },
                                    onClick = {
                                        selectedMedicineId = medicine.id
                                        selectedMedicineName = medicine.name
                                        showMedicineDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Start Date Selector
            item {
                Column {
                    Text("Start Date", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF202630))
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { showDatePicker = true },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFFF0F7FF))
                    ) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                selectedDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                                color = Color(0xFF202630)
                            )
                            Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF2F93FF))
                        }
                    }
                }
            }

            // Time Slots Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Schedule Time", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color(0xFF202630))
                    TextButton(onClick = { selectedTimeIndex = -1; showTimePickerDialog = true }) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Add Time")
                    }
                }
            }

            // Time Slots List
            items(timeSlots) { time ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedTimeIndex = timeSlots.indexOf(time)
                            showTimePickerDialog = true
                        },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF7FF),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD9E9FF))
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AccessTime, contentDescription = null, tint = Color(0xFF2F93FF))
                            Spacer(Modifier.width(12.dp))
                            Text(time, fontSize = 16.sp, color = Color(0xFF202630), fontWeight = FontWeight.Medium)
                        }
                        IconButton(onClick = { timeSlots = timeSlots.filter { it != time } }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFE53935))
                        }
                    }
                }
            }

            // Save Button
            item {
                Spacer(Modifier.height(16.dp))
                Button(
                    onClick = {
                        when {
                            selectedMedicineId == 0 -> Toast.makeText(context, "Please select a medicine", Toast.LENGTH_SHORT).show()
                            timeSlots.isEmpty() -> Toast.makeText(context, "Please add at least one time", Toast.LENGTH_SHORT).show()
                            else -> {
                                val details = timeSlots.map { TimeDetailData(time = it) }
                                // PERBAIKAN: Menghapus scheduleType dari parameter call
                                scheduleViewModel.createSchedule(
                                    medicineId = selectedMedicineId,
                                    startDate = selectedDate.toString(),
                                    details = details,
                                    medicineName = selectedMedicineName
                                )
                            }
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F93FF)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Save Reminder", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Dialogs (DatePicker & TimePicker tetap sama)
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

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            selectedDate = java.time.Instant.ofEpochMilli(it)
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF2F93FF))
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) { Text("Cancel") }
            }
        ) {
            // PERBAIKAN: Menambahkan colors agar header dan seleksi berwarna biru
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    titleContentColor = Color(0xFF202630),
                    headlineContentColor = Color(0xFF202630),
                    weekdayContentColor = Color(0xFF202630),
                    currentYearContentColor = Color(0xFF2F93FF),
                    selectedYearContentColor = Color.White,
                    selectedYearContainerColor = Color(0xFF2F93FF),
                    dayContentColor = Color(0xFF202630),
                    selectedDayContentColor = Color.White,
                    selectedDayContainerColor = Color(0xFF2F93FF),
                    todayContentColor = Color(0xFF2F93FF),
                    todayDateBorderColor = Color(0xFF2F93FF)
                )
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val timeParts = initialTime.split(":")
    val hour = timeParts[0].toIntOrNull() ?: 8
    val minute = timeParts[1].toIntOrNull() ?: 0

    val state = rememberTimePickerState(
        initialHour = hour,
        initialMinute = minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Time",
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )

                // PERBAIKAN: Custom warna dial dan selector menjadi biru
                TimePicker(
                    state = state,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color(0xFFF0F7FF),
                        selectorColor = Color(0xFF2F93FF), // Jarum Jam Biru
                        containerColor = Color.White,
                        periodSelectorSelectedContainerColor = Color(0xFFD9E9FF),
                        timeSelectorSelectedContainerColor = Color(0xFFD9E9FF),
                        timeSelectorSelectedContentColor = Color(0xFF2F93FF)
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color.Gray)
                    }
                    TextButton(
                        onClick = {
                            val formattedTime = String.format("%02d:%02d", state.hour, state.minute)
                            onConfirm(formattedTime)
                        }
                    ) {
                        Text("OK", color = Color(0xFF2F93FF), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}