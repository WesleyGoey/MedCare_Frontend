package com.wesley.medcare.ui.view.Schedule

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
    var showDatePicker by remember { mutableStateOf(false) } // State untuk dialog baru

    val apiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val displayFormatter = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy", Locale.ENGLISH)

    val primaryBlue = Color(0xFF457AF9) // Tema Biru Utama
    val backgroundGray = Color(0xFFF5F7FA)

    LaunchedEffect(pickedDate) {
        viewModel.getSchedulesByDate(pickedDate.format(apiFormatter))
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(AppView.AddReminderView.name) },
                containerColor = primaryBlue,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(30.dp))
            }
        },
        containerColor = backgroundGray
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Medication Schedule",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A2E)
            )
            Spacer(modifier = Modifier.height(20.dp))

            // Selector tanggal dengan warna tema
            DateSelectorCard(
                formattedDate = pickedDate.format(displayFormatter),
                onClick = { showDatePicker = true },
                themeColor = primaryBlue
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = primaryBlue)
                }
            } else if (reminders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No medication for today", color = Color.Gray)
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
                            themeColor = primaryBlue,
                            onMarkAsTaken = {
                                viewModel.markAsTaken(item.id, pickedDate.format(apiFormatter))
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

    // Modal Date Picker yang warnanya bisa disesuaikan (Menghilangkan warna hijau)
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
                            pickedDate = Instant.ofEpochMilli(it)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                        }
                        showDatePicker = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = primaryBlue)
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDatePicker = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray)
                ) { Text("CANCEL") }
            }
        ) {
            DatePicker(
                state = datePickerState,
                colors = DatePickerDefaults.colors(
                    containerColor = Color.White,
                    titleContentColor = primaryBlue,
                    headlineContentColor = primaryBlue,
                    selectedDayContainerColor = primaryBlue,
                    selectedDayContentColor = Color.White,
                    todayContentColor = primaryBlue,
                    todayDateBorderColor = primaryBlue,
                    currentYearContentColor = primaryBlue,
                    selectedYearContainerColor = primaryBlue,
                    selectedYearContentColor = Color.White
                )
            )
        }
    }
}

@Composable
fun DateSelectorCard(formattedDate: String, onClick: () -> Unit, themeColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(themeColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CalendarToday, null, tint = themeColor, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Selected Date", fontSize = 11.sp, color = Color.Gray)
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
    themeColor: Color,
    onMarkAsTaken: () -> Unit,
    onCardClick: () -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .clip(CircleShape)
                    .background(themeColor.copy(alpha = 0.2f))
                    .border(2.dp, themeColor, CircleShape)
            )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(115.dp) // Disesuaikan sedikit lebih panjang
                    .background(themeColor.copy(alpha = 0.1f))
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onCardClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AccessTime, null, tint = themeColor, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        val formattedTime = if (time.contains("T")) time.split("T")[1].substring(0, 5) else time
                        Text(formattedTime, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Text("Pending", fontSize = 12.sp, color = themeColor, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(medicineName, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1A1A2E))
                Text(dosage, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onMarkAsTaken,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = themeColor),
                    shape = RoundedCornerShape(12.dp) // Lebih membulat agar modern
                ) {
                    Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Mark as Taken", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}