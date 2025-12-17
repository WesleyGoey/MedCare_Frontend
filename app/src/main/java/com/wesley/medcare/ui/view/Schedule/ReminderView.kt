package com.wesley.medcare.ui.view.Schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wesley.medcare.ui.route.AppView
import com.wesley.medcare.ui.viewmodel.ScheduleViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun ReminderView(
    navController: NavHostController = rememberNavController(),
    viewModel: ScheduleViewModel = viewModel()
) {
    val reminders by viewModel.schedules.collectAsState()
    val selectedDate by viewModel.selectedSchedule.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getAllSchedules()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Title
            Text(
                text = "Reminders Today",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Date Selector
//            DateSelectorCard(
//                selectedDate = selectedDate,
//                onDateClick = { }
//            )

            Spacer(modifier = Modifier.height(16.dp))

            // Reminder List
            if (reminders.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No reminders for this date",
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reminders, key = { it.id }) { reminder ->
                        ReminderCard(
                            time = reminder.time,
                            medicineName = reminder.medicine.name,
                            dosage = reminder.medicine.dosage,
                            status = "false",
                            onMarkAsTaken = {
//                                viewModel.markReminderAsTaken(reminder.id)
                            }
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = { navController.navigate(AppView.AddReminderView.name) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF2F93FF),
            contentColor = Color.White
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Reminder"
            )
        }
    }
}

@Composable
private fun DateSelectorCard(
    selectedDate: LocalDate,
    onDateClick: () -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onDateClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2F93FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarToday,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Selected Date",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Text(
                    text = selectedDate.format(formatter),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = Color(0xFF2F93FF)
            )
        }
    }
}

@Composable
private fun ReminderCard(
    time: String,
    medicineName: String,
    dosage: String,
    status: String,
    onMarkAsTaken: () -> Unit
) {
    val isPending = status.equals("Pending", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2F93FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = time,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )

                    // Status Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isPending) Color(0xFFE8E8E8) else Color(0xFFE8F5E9)
                    ) {
                        Text(
                            text = status,
                            fontSize = 12.sp,
                            color = if (isPending) Color.Gray else Color(0xFF4CAF50),
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = medicineName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = dosage,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            // Mark as Taken Button (only show if pending)
            if (isPending) {
                Button(
                    onClick = onMarkAsTaken,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2F93FF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Mark as Taken")
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ReminderViewPreview() {
    ReminderView()
}