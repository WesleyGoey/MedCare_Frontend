package com.wesley.medcare.ui.view.History

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

// Color Palette
private val Teal500 = Color(0xFF2FB6A3)
private val TealBorder = Color(0xFF2FB6A3).copy(alpha = 0.25f)
private val TealLightBg = Color(0xFFF0F9F8)

private val Red500 = Color(0xFFFF5A5F)
private val RedBorder = Color(0xFFFF5A5F).copy(alpha = 0.25f)
private val RedLightBg = Color(0xFFFFF5F5)

private val Yellow500 = Color(0xFFFFC107)
private val GrayText = Color(0xFF8A94A6)
private val DarkText = Color(0xFF1A1A2E)
private val GrayBackground = Color(0xFFF5F7FA)

@Composable
fun HistoryView(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground),
        // Padding horizontal 24.dp agar sejajar dengan Medicine/ProfileView
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Column(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    text = "History",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Text(
                    text = "This Week",
                    fontSize = 16.sp,
                    color = GrayText
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(
                    icon = Icons.Default.TrendingUp,
                    iconColor = Teal500,
                    borderColor = TealBorder,
                    value = "85%",
                    label = "Compliance",
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    icon = Icons.Default.DateRange,
                    iconColor = Red500,
                    borderColor = RedBorder,
                    value = "2",
                    label = "Missed",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            WeeklyComplianceCard()
        }

        item {
            RecentActivityCard()
        }
    }
}

@Composable
fun SummaryCard(
    icon: ImageVector,
    iconColor: Color,
    borderColor: Color,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        // Elevasi dikembalikan ke 2.dp sesuai permintaan awal
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = iconColor
            )
            Text(
                text = label,
                fontSize = 14.sp,
                color = GrayText
            )
        }
    }
}

@Composable
fun WeeklyComplianceCard() {
    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    val values = listOf(100, 75, 100, 50, 100, 75, 100)

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0).copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Weekly Compliance",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                days.forEachIndexed { index, day ->
                    ComplianceBar(day = day, percentage = values[index])
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = Teal500, label = "90-100%")
                Spacer(modifier = Modifier.width(12.dp))
                LegendItem(color = Yellow500, label = "70-89%")
                Spacer(modifier = Modifier.width(12.dp))
                LegendItem(color = Red500, label = "Below 70%")
            }
        }
    }
}

@Composable
fun ComplianceBar(day: String, percentage: Int) {
    val barColor = when {
        percentage >= 90 -> Teal500
        percentage >= 70 -> Yellow500
        else -> Red500
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .width(22.dp)
                .weight(if (percentage == 0) 0.01f else percentage / 100f)
                .clip(RoundedCornerShape(6.dp))
                .background(barColor)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = day,
            fontSize = 12.sp,
            color = GrayText
        )
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = GrayText
        )
    }
}

@Composable
fun RecentActivityCard() {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0).copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Recent Activity",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            ActivityItem(time = "Today - 12:00 PM", medicine = "Lisinopril 10mg", isTaken = true)
            Spacer(modifier = Modifier.height(12.dp))
            ActivityItem(time = "Today - 8:00 AM", medicine = "Aspirin 81mg", isTaken = true)
            Spacer(modifier = Modifier.height(12.dp))
            ActivityItem(time = "Yesterday - 8:00 PM", medicine = "Vitamin D 1000 IU", isTaken = false)
            Spacer(modifier = Modifier.height(12.dp))
            ActivityItem(time = "Yesterday - 2:00 PM", medicine = "Metformin 500mg", isTaken = true)
        }
    }
}

@Composable
fun ActivityItem(time: String, medicine: String, isTaken: Boolean) {
    val bgColor = if (isTaken) TealLightBg else RedLightBg
    val borderColor = if (isTaken) TealBorder else RedBorder
    val iconColor = if (isTaken) Teal500 else Red500
    val icon = if (isTaken) Icons.Default.Check else Icons.Default.Close

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = time, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = DarkText)
            Text(text = medicine, fontSize = 14.sp, color = GrayText)
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(iconColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}