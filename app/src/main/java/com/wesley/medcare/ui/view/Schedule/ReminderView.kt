package com.wesley.medcare.ui.view.Schedule

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import java.time.ZoneOffset
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

    // PERBAIKAN UTAMA: Gunakan LaunchedEffect agar tidak memicu fetch ulang setiap recomposition tab
    LaunchedEffect(pickedDate) {
        viewModel.getSchedulesByDate(pickedDate.format(apiFormatter))
    }

    // Hanya observe On_Resume jika benar-benar ingin sinkron data server saat buka app lagi
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Jangan dipaksa fetch di sini agar status lokal tidak hilang saat pindah tab
                // viewModel.getSchedulesByDate(pickedDate.format(apiFormatter))
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(successMessage, errorMessage) {
        successMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show(); viewModel.clearMessages() }
        errorMessage?.let { Toast.makeText(context, it, Toast.LENGTH_SHORT).show(); viewModel.clearMessages() }
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
                themeColor = Color(0xFF457AF9)
            )

            Spacer(modifier = Modifier.height(28.dp))

            if (isLoading && reminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFF457AF9))
                }
            } else if (reminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No medication for this day", color = Color(0xFF757575))
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    itemsIndexed(reminders, key = { _, item -> item.id }) { index, item ->
                        val currentStatus = item.history?.firstOrNull()?.status ?: "PENDING"
                        val isProcessing = processingIds.contains(item.id)

                        ReminderTimelineItem(
                            time = item.time,
                            medicineName = item.medicine.name,
                            dosage = item.medicine.dosage,
                            status = currentStatus,
                            themeColor = Color(0xFF457AF9),
                            isActionEnabled = isToday,
                            isLoadingAction = isProcessing,
                            isFirst = index == 0,
                            isLast = index == reminders.size - 1,
                            onMarkAsTaken = { viewModel.markAsTaken(item.id, pickedDate.format(apiFormatter)) },
                            onUndo = { viewModel.undoMarkAsTaken(item.id, pickedDate.format(apiFormatter)) },
                            onCardClick = { navController.navigate("${AppView.EditReminderView.name}/${item.scheduleId}") }
                        )
                    }
                }
            }
        }
    }

    if (showDatePicker) {
        val initialMillis = remember(pickedDate) { pickedDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli() }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { pickedDate = Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() }
                    showDatePicker = false
                }) { Text("OK", fontWeight = FontWeight.Bold, color = Color(0xFF457AF9)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("CANCEL", color = Color.Gray) }
            }
        ) { DatePicker(state = datePickerState) }
    }
}

// ... (Composables DateSelectorCard dan ReminderTimelineItem tetap sama seperti sebelumnya)

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
    isLoadingAction: Boolean, isFirst: Boolean, isLast: Boolean,
    onMarkAsTaken: () -> Unit, onUndo: () -> Unit, onCardClick: () -> Unit
) {
    val isTaken = status == "DONE"
    val isMissed = status == "MISSED"
    val statusColor = if (isTaken) Color(0xFF4CAF50) else if (isMissed) Color(0xFFEF5350) else themeColor
    val statusText = if (isTaken) "Taken" else if (isMissed) "Missed" else "Pending"

    val displayTime = remember(time) {
        try {
            val clean = if (time.contains("T")) time.split("T")[1] else time
            clean.substring(0, 5).replace(":", ".")
        } catch (e: Exception) { time }
    }

    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(32.dp)) {
            Box(modifier = Modifier.width(2.dp).weight(1f).background(if (isFirst) Color.Transparent else statusColor.copy(alpha = 0.2f)))
            Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(statusColor.copy(alpha = 0.15f)).border(1.5.dp, statusColor, CircleShape), contentAlignment = Alignment.Center) {
                Icon(if (isTaken) Icons.Default.Check else Icons.Default.Circle, null, tint = statusColor, modifier = Modifier.size(12.dp))
            }
            Box(modifier = Modifier.width(2.dp).weight(1f).background(if (isLast) Color.Transparent else statusColor.copy(alpha = 0.2f)))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(modifier = Modifier.padding(vertical = 8.dp).weight(1f)) {
            Card(modifier = Modifier.fillMaxWidth().clickable { onCardClick() }.shadow(4.dp, RoundedCornerShape(24.dp)), shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(statusColor.copy(alpha = 0.15f)), contentAlignment = Alignment.Center) {
                                Icon(if (isTaken) Icons.Default.CheckCircle else Icons.Default.AccessTime, null, tint = statusColor, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(displayTime, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1A1A2E))
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
                                    Text("Undo Mark", fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(onClick = onMarkAsTaken, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = themeColor, contentColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                                    Text("Mark as Taken", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}