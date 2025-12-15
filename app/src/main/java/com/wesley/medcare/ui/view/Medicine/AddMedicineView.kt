package com.wesley.medcare.ui.view.Medicine

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.wesley.medcare.data.dto.Medicine.MedicineData

@Composable
fun AddMedicineView(onBack: () -> Unit = {}) {
    val scrollState = rememberScrollState()

    // Form state
    val nameState = remember { mutableStateOf("") }
    val dosageState = remember { mutableStateOf("") }
    val stockState = remember { mutableStateOf("") }
    val minStockState = remember { mutableStateOf("") }
    val notesState = remember { mutableStateOf("") }
    val selectedType = remember { mutableStateOf("Tablet") }

    val types = listOf("Tablet", "Capsule", "Syrup", "Drops", "Ointment", "Patch", "Custom Type")

    // Shared textfield colors — borderless look with light container

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF3F6F9))
    ) {
        // Header replaced TopAppBar - simple back + title inside scrollable content
        Column(
            modifier = Modifier
                .verticalScroll(scrollState)
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(text = "Add Medication", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Add a new medication to your list", fontSize = 13.sp, color = Color(0xFF9E9E9E))
            }

            // Basic Information Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 6.dp,
                color = Color.White,
                tonalElevation = 2.dp,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Basic Information", fontWeight = FontWeight.SemiBold)
                    Text("Medication Name", fontSize = 13.sp, color = Color(0xFF6C6C6C))
                    TextField(
                        value = nameState.value,
                        onValueChange = { nameState.value = it },
                        placeholder = { Text("e.g., Paracetamol", color = Color(0xFF9E9E9E)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor =  Color(0xFFF7F8FA),
                            unfocusedContainerColor = Color(0xFFF7F8FA),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Text("Dosage", fontSize = 13.sp, color = Color(0xFF6C6C6C))
                    TextField(
                        value = dosageState.value,
                        onValueChange = { dosageState.value = it },
                        placeholder = { Text("e.g., 500mg", color = Color(0xFF9E9E9E)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor =  Color(0xFFF7F8FA),
                            unfocusedContainerColor = Color(0xFFF7F8FA),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Stock", fontSize = 13.sp, color = Color(0xFF6C6C6C))
                            TextField(
                                value = stockState.value,
                                onValueChange = { stockState.value = it },
                                placeholder = { Text("30", color = Color(0xFF9E9E9E)) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor =  Color(0xFFF7F8FA),
                                    unfocusedContainerColor = Color(0xFFF7F8FA),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    cursorColor = Color.Black
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Min. Stock", fontSize = 13.sp, color = Color(0xFF6C6C6C))
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(RoundedCornerShape(18.dp))
                                        .background(Color(0xFFDEEAFE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("i", fontSize = 10.sp, color = Color(0xFF2B8AF7))
                                }
                            }
                            TextField(
                                value = minStockState.value,
                                onValueChange = { minStockState.value = it },
                                placeholder = { Text("5", color = Color(0xFF9E9E9E)) },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor =  Color(0xFFF7F8FA),
                                    unfocusedContainerColor = Color(0xFFF7F8FA),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    disabledIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    cursorColor = Color.Black
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
            }

            // Medication Type Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 6.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Medication Type", fontWeight = FontWeight.SemiBold)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        types.forEach { type ->
                            val isSelected = selectedType.value == type
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) Brush.horizontalGradient(
                                            listOf(Color(0xFF4B9BFF), Color(0xFF0B6CF6))
                                        ) else Brush.linearGradient(listOf(Color(0xFFF7F8FA), Color(0xFFF7F8FA)))
                                    )
                                    .border(
                                        BorderStroke(
                                            if (isSelected) 0.dp else 1.dp,
                                            if (isSelected) Color.Transparent else Color(0xFFE6E6E6)
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { selectedType.value = type },
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = type,
                                        color = if (isSelected) Color.White else Color(0xFF4B4B4B),
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Notes Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 6.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Notes (Optional)", fontWeight = FontWeight.SemiBold)
                    TextField(
                        value = notesState.value,
                        onValueChange = { notesState.value = it },
                        placeholder = { Text("e.g., Take after meals", color = Color(0xFF9E9E9E)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor =  Color(0xFFF7F8FA),
                            unfocusedContainerColor = Color(0xFFF7F8FA),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent,
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                }
            }

            // Photo upload card
            Surface(
                shape = RoundedCornerShape(12.dp),
                shadowElevation = 6.dp,
                color = Color.White,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Medication Photo (Optional)", fontWeight = FontWeight.SemiBold)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, Color(0xFF4B9BFF), RoundedCornerShape(12.dp))
                            .background(Color(0xFFEFF7FF))
                            .clickable { /* open picker */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF2B8AF7)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.CameraAlt, contentDescription = "Camera", tint = Color.White)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Upload Photo", color = Color(0xFF2B8AF7), fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("Tap to select medication photo", color = Color(0xFF9E9E9E), fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Add Medication Button
            Button(
                onClick = { /* save action */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                content = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF4B9BFF), Color(0xFF0B6CF6))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "Add Medication", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddMedicinePreview() {
    AddMedicineView(
        onBack = { }
    )
}
