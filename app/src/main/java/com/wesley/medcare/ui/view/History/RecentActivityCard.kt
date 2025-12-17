package com.wesley.medcare.ui.view.History

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

private val Teal500 = Color(0xFF00C897)
private val Teal100 = Color(0xFFE0F7FA)
private val Red500 = Color(0xFFFF5252)
private val Red100 = Color(0xFFFFEBEE)
private val GrayText = Color(0xFF757575)
private val DarkText = Color(0xFF212121)

@Composable
fun RecentActivityCard(time: String, medicine: String, isTaken: Boolean) {
    val backgroundColor = if (isTaken) Teal100 else Red100
    val contentColor = if (isTaken) Teal500 else Red500
    val icon = if (isTaken) Icons.Default.Check else Icons.Default.Close
    val statusText = if (isTaken) "Taken" else "Missed"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = time,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = DarkText
            )
            Text(
                text = medicine,
                style = MaterialTheme.typography.bodyMedium,
                color = GrayText
            )
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(contentColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = statusText,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRecentActivityCard() {
    MaterialTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            RecentActivityCard(
                time = "Today - 12:00 PM",
                medicine = "Lisinopril 10mg",
                isTaken = true
            )
        }
    }
}