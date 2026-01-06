package com.wesley.medcare.ui.view.Schedule

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.wesley.medcare.ui.route.AppView
import com.wesley.medcare.ui.viewmodel.ScheduleViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderView(
    navController: NavHostController,
    viewModel: ScheduleViewModel = viewModel()
) {
    val context = LocalContext.current
    val reminders by viewModel.schedules.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var pickedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)

    LaunchedEffect(Unit) {
        // This runs every time you navigate BACK to this screen
        viewModel.getSchedulesByDate(pickedDate.format(DateTimeFormatter.ISO_DATE))
    }

    LaunchedEffect(successMessage, errorMessage) {
        successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF5F7FA),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppView.AddReminderView.name) },
                containerColor = Color(0xFF457AF9),
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.offset(y = 20.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = "Reminders",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )

            Spacer(modifier = Modifier.height(20.dp))

            DateSelectorCard(
                formattedDate = pickedDate.format(displayFormatter),
                onClick = { showDatePicker = true }
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF457AF9))
                }
            } else if (reminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No medication for today", color = Color(0xFF757575))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(reminders, key = { it.id }) { item ->
                        // No need to manually override "MISSED" anymore!
                        // The server will now return nothing (null), so this defaults to "PENDING"
                        val currentStatus = item.history?.firstOrNull()?.status ?: "PENDING"

                        ReminderTimelineItem(
                            time = item.time,
                            medicineName = item.medicine.name,
                            dosage = item.medicine.dosage,
<<<<<<< HEAD
=======

                            // 3. Pass the FIX variable here
                            status = currentStatus,

                            themeColor = primaryBlue,
>>>>>>> fj
                            onMarkAsTaken = {
                                // Create the full timestamp: "2026-01-07T08:00:00"
                                val exactDateTime = "${pickedDate.format(apiFormatter)}T${item.time}:00"
                                viewModel.markAsTaken(item.id, exactDateTime)
                            },
                            onUndo = {
                                // Use the exact same format for Undo!
                                val exactDateTime = "${pickedDate.format(apiFormatter)}T${item.time}:00"
                                viewModel.undoMarkAsTaken(item.id, exactDateTime)
                            },
                            onCardClick = {
                                navController.navigate("${AppView.EditReminderView.name}/${item.scheduleId}")
                            }
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = pickedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            pickedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF457AF9))
                ) { Text("OK", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("CANCEL", color = Color(0xFF757575)) }
            },
            // Update warna dialog background agar konsisten
            colors = DatePickerDefaults.colors(containerColor = Color.White)
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    // --- Warna Utama Kalender ---
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

                    // --- WARNA INPUT KEYBOARD (TextField) ---
                    dateTextFieldColors = TextFieldDefaults.colors(
                        // Keadaan Normal
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color(0xFF1A1A2E),
                        unfocusedTextColor = Color(0xFF1A1A2E),
                        cursorColor = Color(0xFF457AF9),
                        focusedLabelColor = Color(0xFF457AF9),
                        unfocusedLabelColor = Color(0xFF8A94A6),

                        // Keadaan Error (Input tidak valid seperti 01/11/11111)
                        errorContainerColor = Color(0xFFFFF0F0), // Background merah sangat muda
                        errorTextColor = Color(0xFFFF5A5F),      // Teks merah MedCare
                        errorCursorColor = Color(0xFFFF5A5F),
                        errorIndicatorColor = Color.Transparent, // Menghapus garis bawah merah tebal
                        errorLabelColor = Color(0xFFFF5A5F),
                        errorSupportingTextColor = Color(0xFFFF5A5F)
                    )
                )
            )
        }
    }
}

@Composable
fun DateSelectorCard(formattedDate: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF457AF9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CalendarToday, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Selected Date", fontSize = 12.sp, color = Color.Gray)
                Text(formattedDate, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
            }
            Icon(Icons.Default.KeyboardArrowDown, null, tint = Color(0xFF457AF9), modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun ReminderTimelineItem(
    time: String,
    medicineName: String,
    dosage: String,
<<<<<<< HEAD
=======
    status: String, // <--- Add this parameter (e.g., "DONE", "PENDING", "MISSED")
    themeColor: Color,
>>>>>>> fj
    onMarkAsTaken: () -> Unit,
    onUndo: () -> Unit, // <--- Add Undo Callback
    onCardClick: () -> Unit
) {
    // Determine State
    val isTaken = status == "DONE"
    val isMissed = status == "MISSED"

    // Dynamic Colors
    val statusColor = when {
        isTaken -> Color(0xFF4CAF50) // Green
        isMissed -> Color(0xFFEF5350) // Red
        else -> themeColor // Blue
    }

    val statusText = when {
        isTaken -> "Taken"
        isMissed -> "Missed"
        else -> "Pending"
    }

    Row(modifier = Modifier.fillMaxWidth()) {
<<<<<<< HEAD
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(top = 10.dp)
        ) {
=======
        // Timeline Line (Left side)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
>>>>>>> fj
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
<<<<<<< HEAD
                    .border(1.5.dp, Color(0xFFD1D5DC), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, null, tint = Color(0xFFD1D5DC), modifier = Modifier.size(14.dp))
            }
            Box(
                modifier = Modifier
                    .width(1.5.dp)
                    .height(130.dp)
                    .background(Color(0xFFD1D5DC).copy(alpha = 0.4f))
=======
                    .background(statusColor.copy(alpha = 0.2f))
                    .border(2.dp, statusColor, CircleShape)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(130.dp) // Slightly taller to fit Undo button space
                    .background(statusColor.copy(alpha = 0.1f))
>>>>>>> fj
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

<<<<<<< HEAD
=======
        // Card Content
>>>>>>> fj
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCardClick() }
                .padding(bottom = 8.dp)
                .shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
<<<<<<< HEAD
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF457AF9).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccessTime, null, tint = Color(0xFF457AF9), modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        val cleanTime = if (time.contains("T")) time.split("T")[1].substring(0, 5) else time
                        Text(
                            text = cleanTime,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = Color(0xFF1A1A2E)
                        )
                    }

                    Surface(
                        color = Color(0xFFF3F4F6),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Pending",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 13.sp,
                            color = Color(0xFF757575),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = medicineName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A2E)
                )

                Text(
                    text = dosage,
                    fontSize = 15.sp,
                    color = Color(0xFF757575),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onMarkAsTaken,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF457AF9)),
                    shape = RoundedCornerShape(16.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text(
                        text = "Mark as Taken",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
=======
            Column(modifier = Modifier.padding(16.dp)) {
                // Header: Time and Status Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isTaken) Icons.Default.CheckCircle else Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        val formattedTime = if (time.contains("T")) time.split("T")[1].substring(0, 5) else time
                        Text(formattedTime, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    // Status Badge
                    Surface(
                        color = statusColor.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = statusText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            color = statusColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(medicineName, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1A2E))
                Text(dosage, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))

                // BUTTON LOGIC
                if (isTaken) {
                    // UNDO BUTTON (Outlined or Text Button)
                    OutlinedButton(
                        onClick = onUndo,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray),
                        border = BorderStroke(1.dp, Color.LightGray),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Undo")
                    }
                } else {
                    // MARK AS TAKEN BUTTON (Filled)
                    Button(
                        onClick = onMarkAsTaken,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Mark as Taken", fontWeight = FontWeight.Bold)
                    }
>>>>>>> fj
                }
            }
        }
    }
}