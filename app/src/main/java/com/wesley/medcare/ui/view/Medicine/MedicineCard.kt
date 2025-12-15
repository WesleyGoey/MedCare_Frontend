package com.wesley.medcare.ui.view.Medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MedicineCard(
    name: String,
    dosageText: String,
    pillsLeftText: String,
    scheduleTimes: List<String> = emptyList()
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Top row: icon + title + subtitle
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon rounded square
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .shadow(6.dp, RoundedCornerShape(14.dp))
                        .background(
                            brush = Brush.verticalGradient(listOf(Color(0xFF4DA1FF), Color(0xFF2F93FF))),
                            shape = RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = "med icon",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF202630)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "$dosageText  \u2022  $pillsLeftText",
                        color = Color(0xFF8D99A6),
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Divider(color = Color(0xFFF0F3F6), thickness = 1.dp)

            Spacer(modifier = Modifier.height(14.dp))

            // Schedule area
            if (scheduleTimes.isEmpty()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color(0xFF8D99A6)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("No schedule set", color = Color(0xFF8D99A6), fontSize = 15.sp)
                }
            } else {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = Color(0xFF2F93FF)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Daily Schedule",
                            color = Color(0xFF2F93FF),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        scheduleTimes.forEach { time ->
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color(0xFFEFF7FF),
                                shadowElevation = 0.dp,
                                modifier = Modifier
                                    .defaultMinSize(minHeight = 44.dp)
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFFD9E9FF),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = time,
                                        color = Color(0xFF2F93FF),
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MedicineCardPreview_WithSchedule() {
    MedicineCard(
        name = "Metformin",
        dosageText = "500mg",
        pillsLeftText = "28 pills left",
        scheduleTimes = listOf("08:00", "17:00")
    )
}

@Preview(showBackground = true)
@Composable
private fun MedicineCardPreview_NoSchedule() {
    MedicineCard(
        name = "Aspirin",
        dosageText = "81mg",
        pillsLeftText = "3 pills left",
        scheduleTimes = emptyList()
    )
}
