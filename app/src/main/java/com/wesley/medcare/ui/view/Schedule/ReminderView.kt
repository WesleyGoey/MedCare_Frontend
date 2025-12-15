package com.wesley.medcare.ui.view.Schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun ReminderView(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    // sample data for preview / demo
    val reminders = remember {
        listOf(
            ReminderItem(time = "8:00 AM", medicine = "Metformin", dose = "500mg", status = "Pending"),
            ReminderItem(time = "5:00 PM", medicine = "Metformin", dose = "500mg", status = "Pending"),
            ReminderItem(time = "8:00 PM", medicine = "Lisinopril", dose = "10mg", status = "Pending")
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("add_reminder") }, containerColor = Color(0xFF3B82F6)) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        },
        containerColor = Color(0xFFF5F7FA)
    ) { paddingValues: PaddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Reminders",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF111827)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Selected date card
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .clickable { navController.navigate("date_picker") }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF3B82F6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "calendar",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Selected Date", fontSize = 12.sp, color = Color(0xFF6B7280))
                        Text(text = "Tuesday, December 3, 2024", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }

                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = "dropdown",
                        tint = Color(0xFF9CA3AF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Timeline + reminder list
            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(items = reminders) { item ->
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                            // timeline column
                            Column(
                                modifier = Modifier.width(48.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                // circle icon
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .border(width = 2.dp, color = Color(0xFF3B82F6), shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AccessTime,
                                        contentDescription = null,
                                        tint = Color(0xFF3B82F6),
                                        modifier = Modifier.size(14.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // vertical connecting line
                                Box(
                                    modifier = Modifier
                                        .width(2.dp)
                                        .height(100.dp)
                                        .background(
                                            Brush.verticalGradient(colors = listOf(Color(0xFF3B82F6), Color(0xFF10B981)))
                                        )
                                )
                            }

                            // card with details
                            Card(
                                shape = RoundedCornerShape(14.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // icon area
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFFEEF2FF)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AccessTime,
                                            contentDescription = null,
                                            tint = Color(0xFF3B82F6)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = item.time, fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(text = item.medicine, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                        Text(text = item.dose, fontSize = 12.sp, color = Color(0xFF9CA3AF))
                                    }

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Column(horizontalAlignment = Alignment.End) {
                                        // status pill
                                        Surface(
                                            shape = RoundedCornerShape(20.dp),
                                            color = Color(0xFFEFF6FF)
                                        ) {
                                            Text(
                                                text = item.status,
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                                color = Color(0xFF374151),
                                                fontSize = 12.sp
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        Button(
                                            onClick = { /* mark as taken */ },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                                            shape = RoundedCornerShape(20.dp),
                                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                                        ) {
                                            Text(text = "Mark as Taken", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // bottom spacer so floating button doesn't overlap items
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}

// small model for preview/demo
private data class ReminderItem(val time: String, val medicine: String, val dose: String, val status: String)

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun ReminderViewPreview() {
    ReminderView()
}