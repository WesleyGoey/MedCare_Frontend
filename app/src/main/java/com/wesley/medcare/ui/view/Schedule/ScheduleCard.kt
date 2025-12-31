package com.wesley.medcare.ui.view.Schedule

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.wesley.medcare.data.dto.Schedule.DetailData

@Composable
fun ScheduleCard(
    medicineName: String,
    dosage: String,
    time: String,
    scheduleType: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon rounded square
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .shadow(6.dp, RoundedCornerShape(14.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(
                                Color(0xFF4DA1FF),
                                Color(0xFF2F93FF)
                            )
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccessTime,
                    contentDescription = "schedule icon",
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicineName,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF202630)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$dosage  •  $time",
                    color = Color(0xFF8D99A6),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF7FF)
                ) {
                    Text(
                        text = scheduleType,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color(0xFF2F93FF),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//private fun ScheduleCardPreview() {
//    ScheduleCard(
//        medicineName = "Metformin",
//        dosage = "500mg",
//        time = "08:00",
//        scheduleType = "Daily"
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//private fun ScheduleCardPreview_Weekly() {
//    ScheduleCard(
//        medicineName = "Aspirin",
//        dosage = "81mg",
//        time = "20:00",
//        scheduleType = "Weekly"
//    )
//}
//
