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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.wesley.medcare.R
import com.wesley.medcare.data.dto.Schedule.TimeDetailData
import com.wesley.medcare.ui.view.components.BackTopAppBar
import com.wesley.medcare.ui.viewmodel.ScheduleViewModel
import java.time.LocalDate

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

    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var medicineId by remember { mutableIntStateOf(0) }
    var timeSlots by remember { mutableStateOf(listOf<String>()) }
    var startDate by remember { mutableStateOf("") }

    var showTimePickerDialog by remember { mutableStateOf(false) }
    var selectedTimeIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(scheduleId) {
        viewModel.getScheduleById(scheduleId)
    }

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

    LaunchedEffect(successMessage) {
        successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
            navController.popBackStack()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        topBar = { BackTopAppBar(title = "Back", onBack = { navController.popBackStack() }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 32.dp)
        ) {
            item {
                Column {
                    Text(
                        "Edit Reminder",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A2E)
                    )
                    Text("Update reminder information", fontSize = 14.sp, color = Color(0xFF757575))
                }
            }

            // --- Section Medication ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Medication",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color(0xFF1A1A2E),
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color(0xFFF0F4FF))
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White), contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.logo),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(
                                    medicineName,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1A1A2E),
                                    fontSize = 18.sp
                                )
                                Text(dosage, fontSize = 14.sp, color = Color(0xFF757575))
                            }
                        }
                    }
                }
            }

            // --- Section Reminder Times ---
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(24.dp)),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Column {
                            Text(
                                "Reminder Times",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF1A1A2E)
                            )
                            Text(
                                "${timeSlots.size} reminder times per day (Max 3)",
                                fontSize = 12.sp,
                                color = Color(0xFF757575)
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        timeSlots.forEachIndexed { index, time ->
                            Text(
                                "Time ${index + 1}",
                                fontSize = 13.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFFBC02D)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AccessTime, null, tint = Color.White)
                                }
                                Spacer(Modifier.width(12.dp))
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(Color(0xFFF5F7FA))
                                        .clickable {
                                            selectedTimeIndex = index
                                            showTimePickerDialog = true
                                        }
                                        .padding(horizontal = 16.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        time,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1A1A2E)
                                    )
                                }
                                Spacer(Modifier.width(12.dp))
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFFFF5A5F))
                                        .clickable {
                                            timeSlots =
                                                timeSlots.filterIndexed { i, _ -> i != index }
                                        }, contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        if (timeSlots.size < 3) {
                            Button(
                                onClick = { selectedTimeIndex = -1; showTimePickerDialog = true },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(
                                        0xFF457AF9
                                    ), contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    null,
                                    modifier = Modifier.size(18.dp)
                                ); Spacer(Modifier.width(8.dp)); Text("Add Time")
                            }
                        }
                    }
                }
            }

            // --- Action Buttons ---
            item {
                Spacer(Modifier.height(10.dp))
                Button(
                    onClick = {
                        viewModel.updateSchedule(
                            scheduleId,
                            medicineId,
                            medicineName,
                            startDate,
                            timeSlots.map { TimeDetailData(time = it) })
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF457AF9)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isLoading) CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    else Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        viewModel.deleteSchedule(
                            scheduleId,
                            medicineId,
                            LocalDate.now().toString()
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5A5F)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5A5F))
                ) {
                    Icon(Icons.Default.Delete, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Delete Reminder", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showTimePickerDialog) {
        CustomTimePickerDialog(
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTimePickerDialog(
    initialTime: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val timeParts = initialTime.split(":")
    val state = rememberTimePickerState(
        initialHour = timeParts[0].toIntOrNull() ?: 8,
        initialMinute = timeParts[1].toIntOrNull() ?: 0,
        is24Hour = true
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Text(
                    text = "Select Time",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 20.dp)
                )

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = Color(0xFF8A94A6))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onConfirm(
                                String.format(
                                    "%02d:%02d",
                                    state.hour,
                                    state.minute
                                )
                            )
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF457AF9),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Set Time", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}