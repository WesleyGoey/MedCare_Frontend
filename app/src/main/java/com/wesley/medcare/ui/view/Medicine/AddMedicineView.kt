// Kotlin
package com.wesley.medcare.ui.view.Medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.wesley.medcare.data.dto.Medicine.MedicineData

@Composable
fun AddMedicineView(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    onSave: (MedicineData) -> Unit = {}
) {
    val scroll = rememberScrollState()

    var medName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    val types = listOf("Tablet", "Capsule", "Syrup", "Drops", "Ointment", "Patch", "Custom Type")
    var selectedType by remember { mutableStateOf(types.first()) }
    var notes by remember { mutableStateOf("") }
    var photoSelected by remember { mutableStateOf(false) }

    val cardColor = Color(0xFFFFFFFF) // explicit surface color (no MaterialTheme)
    val pageBg = Color(0xFFF3F6F9)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(pageBg)
            .verticalScroll(scroll)
            .padding(horizontal = 16.dp)
            .padding(top = 12.dp, bottom = 24.dp)
    ) {
        // Basic Information Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 4.dp,
            color = cardColor,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Basic Information", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = medName,
                    onValueChange = { medName = it },
                    label = { Text("Medication Name") },
                    placeholder = { Text("e.g., Paracetamol") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = dosage,
                    onValueChange = { dosage = it },
                    label = { Text("Dosage") },
                    placeholder = { Text("e.g., 500mg") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock") },
                        placeholder = { Text("30") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = minStock,
                        onValueChange = { minStock = it },
                        label = { Text("Min. Stock") },
                        placeholder = { Text("5") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Medication Type Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 4.dp,
            color = cardColor,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Medication Type", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    types.forEach { type ->
                        val selected = type == selectedType
                        val background = if (selected) {
                            Brush.horizontalGradient(listOf(Color(0xFF2F8AF7), Color(0xFF56B5FF)))
                        } else {
                            Brush.verticalGradient(listOf(Color(0xFFF2F2F2), Color(0xFFF2F2F2)))
                        }
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .padding(vertical = 4.dp)
                                .height(44.dp)
                                .clip(RoundedCornerShape(22.dp))
                                .background(background)
                                .clickable { selectedType = type }
                                .padding(horizontal = 20.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = type,
                                    color = if (selected) Color.White else Color.Black,
                                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                                )
                                if (selected) {
                                    Spacer(Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Notes Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 4.dp,
            color = cardColor,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Notes (Optional)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes") },
                    placeholder = { Text("e.g., Take after meals") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Photo Card
        Surface(
            shape = RoundedCornerShape(12.dp),
            shadowElevation = 4.dp,
            color = cardColor,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Medication Photo (Optional)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF7FBFF))
                        .clickable { photoSelected = !photoSelected },
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Upload",
                            tint = Color(0xFF2F8AF7),
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(if (photoSelected) "Photo Selected" else "Upload Photo", color = Color(0xFF2F8AF7))
                        Spacer(Modifier.height(4.dp))
                        Text("Tap to select medication photo", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        // Add Medication Button (explicit color usage)
        Button(
            onClick = { /* handle add */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2F8AF7),
                contentColor = Color.White
            )
        ) {
            Text("Add Medication", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun AddMedicinePreview() {
    AddMedicineView(
        onBack = { }
    )
}
