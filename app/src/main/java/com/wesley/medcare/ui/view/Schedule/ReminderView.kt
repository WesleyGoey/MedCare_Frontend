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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
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
    val lifecycleOwner = LocalLifecycleOwner.current

    val reminders by viewModel.schedules.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val processingIds by viewModel.processingIds.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var pickedDate by remember { mutableStateOf(LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }

    val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH)

    val isToday = remember(pickedDate) { pickedDate.isEqual(LocalDate.now()) }
    val primaryBlue = Color(0xFF457AF9)
    val backgroundGray = Color(0xFFF5F7FA)

    // Load data awal saat aplikasi dibuka atau tanggal diganti
    DisposableEffect(lifecycleOwner, pickedDate) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.getSchedulesByDate(pickedDate.format(apiFormatter))
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Listener untuk pesan Toast
    LaunchedEffect(successMessage, errorMessage) {
        successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
            // REVISI: DI SINI TIDAK BOLEH ADA getSchedulesByDate()
        }
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }

    Scaffold(
        containerColor = backgroundGray,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppView.AddReminderView.name) },
                containerColor = primaryBlue,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.offset(y = 20.dp)
            ) { Icon(Icons.Default.Add, null, modifier = Modifier.size(28.dp)) }
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 20.dp)) {
            Spacer(modifier = Modifier.height(30.dp))
            Text("Reminders", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
            Spacer(modifier = Modifier.height(20.dp))

            DateSelectorCard(
                formattedDate = pickedDate.format(displayFormatter),
                onClick = { showDatePicker = true },
                themeColor = primaryBlue
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (isLoading && reminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryBlue)
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
                        val currentStatus = item.history?.firstOrNull()?.status ?: "PENDING"
                        val isProcessing = processingIds.contains(item.id)

                        ReminderTimelineItem(
                            time = item.time,
                            medicineName = item.medicine.name,
                            dosage = item.medicine.dosage,
                            status = currentStatus,
                            themeColor = primaryBlue,
                            isActionEnabled = isToday,
                            isLoadingAction = isProcessing,
                            onMarkAsTaken = {
                                viewModel.markAsTaken(item.id, pickedDate.format(apiFormatter))
                            },
                            onUndo = {
                                viewModel.undoMarkAsTaken(item.id, pickedDate.format(apiFormatter))
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
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        pickedDate = Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text("OK", fontWeight = FontWeight.Bold, color = primaryBlue) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("CANCEL", color = Color.Gray) }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
fun DateSelectorCard(formattedDate: String, onClick: () -> Unit, themeColor: Color) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.shadow(2.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(46.dp).clip(RoundedCornerShape(14.dp)).background(themeColor), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.CalendarToday, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Selected Date", fontSize = 12.sp, color = Color.Gray)
                Text(formattedDate, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
            }
            Icon(Icons.Default.KeyboardArrowDown, null, tint = themeColor, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun ReminderTimelineItem(
    time: String, medicineName: String, dosage: String, status: String,
    themeColor: Color, isActionEnabled: Boolean,
    isLoadingAction: Boolean,
    onMarkAsTaken: () -> Unit,
    onUndo: () -> Unit, onCardClick: () -> Unit
) {
    val isTaken = status == "DONE"
    val isMissed = status == "MISSED"
    val statusColor = when {
        isTaken -> Color(0xFF4CAF50)
        isMissed -> Color(0xFFEF5350)
        else -> themeColor
    }
    val statusText = when {
        isTaken -> "Taken"
        isMissed -> "Missed"
        else -> "Pending"
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 10.dp)) {
            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(statusColor.copy(alpha = 0.15f)).border(1.5.dp, statusColor, CircleShape), contentAlignment = Alignment.Center) {
                Icon(imageVector = if (isTaken) Icons.Default.Check else Icons.Default.Circle, null, tint = statusColor, modifier = Modifier.size(12.dp))
            }
            Box(modifier = Modifier.width(1.5.dp).height(140.dp).background(statusColor.copy(alpha = 0.2f)))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Card(
            modifier = Modifier.fillMaxWidth().clickable { onCardClick() }.padding(bottom = 8.dp).shadow(4.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(statusColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                            Icon(imageVector = if (isTaken) Icons.Default.CheckCircle else Icons.Default.AccessTime, null, tint = statusColor, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        val cleanTime = if (time.contains("T")) time.split("T")[1].substring(0, 5) else time
                        Text(cleanTime, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A2E))
                    }
                    Surface(color = statusColor.copy(alpha = 0.1f), shape = RoundedCornerShape(12.dp)) {
                        Text(statusText, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), fontSize = 13.sp, color = statusColor, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(medicineName, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                Text(dosage, fontSize = 15.sp, color = Color(0xFF757575))
                Spacer(modifier = Modifier.height(20.dp))

                if (isActionEnabled) {
                    if (isLoadingAction) {
                        Box(modifier = Modifier.fillMaxWidth().height(50.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = themeColor, strokeWidth = 2.dp)
                        }
                    } else {
                        if (isTaken) {
                            OutlinedButton(onClick = onUndo, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(16.dp), border = BorderStroke(1.dp, Color.LightGray)) {
                                Icon(Icons.Default.Refresh, null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Undo Mark", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(onClick = onMarkAsTaken, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = themeColor), shape = RoundedCornerShape(16.dp)) {
                                Text("Mark as Taken", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}