package com.wesley.medcare.ui.view.History

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController


private val Teal500 = Color(0xFF00C897)
private val Teal100 = Color(0xFFE0F7FA)
private val Yellow500 = Color(0xFFFFC107)
private val Red500 = Color(0xFFFF5252)
private val Red100 = Color(0xFFFFEBEE)
private val GrayText = Color(0xFF757575)
private val DarkText = Color(0xFF212121)
private val GrayBackground = Color(0xFFF5F5F5)

@Composable
fun HistoryView(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GrayBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = DarkText
                )
                Text(
                    text = "This Week",
                    style = MaterialTheme.typography.bodyLarge,
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
                    value = "85%",
                    label = "Compliance",
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    icon = Icons.Default.DateRange,
                    iconColor = Red500,
                    value = "2",
                    label = "Missed Doses",
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
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = iconColor
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
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
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Weekly Compliance",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
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
                Spacer(modifier = Modifier.width(16.dp))
                LegendItem(color = Yellow500, label = "70-89%")
                Spacer(modifier = Modifier.width(16.dp))
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
                .width(24.dp)
                .weight(if (percentage == 0) 0.01f else percentage / 100f)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(barColor)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = day,
            style = MaterialTheme.typography.bodySmall,
            color = GrayText
        )
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = GrayText
        )
    }
}

@Composable
fun RecentActivityCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Recent Activity",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = DarkText,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            RecentActivityCard(
                time = "Today - 12:00 PM",
                medicine = "Lisinopril 10mg",
                isTaken = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            RecentActivityCard(
                time = "Today - 8:00 AM",
                medicine = "Aspirin 81mg",
                isTaken = true
            )
            Spacer(modifier = Modifier.height(12.dp))
            RecentActivityCard(
                time = "Yesterday - 8:00 PM",
                medicine = "Vitamin D 1000 IU",
                isTaken = false
            )
        }
    }
}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun HistoryPreview() {
    HistoryView()
}