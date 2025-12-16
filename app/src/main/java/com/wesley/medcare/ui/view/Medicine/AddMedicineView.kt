// kotlin
package com.wesley.medcare.ui.view.Medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
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
import kotlinx.coroutines.launch
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import androidx.compose.runtime.collectAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineView(
    onBack: () -> Unit,
    viewModel: MedicineViewModel
) {
    val context = LocalContext.current

    // local form state
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Tablet") }

    val medicineTypes = listOf("Tablet", "Capsule", "Syrup", "Drops", "Ointment", "Patch", "Custom Type")

    // Observe ViewModel flows
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    // Snackbar host
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // React to success -> navigate back
    LaunchedEffect(successMessage) {
        successMessage?.let {
            onBack()
            viewModel.clearMessages()
        }
    }

    // React to errors -> show snackbar
    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(msg)
                viewModel.clearMessages()
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Basic Information Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Basic Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Medication Name") },
                        placeholder = { Text("e.g., Paracetamol") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5B9BD5),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { dosage = it },
                        label = { Text("Dosage") },
                        placeholder = { Text("e.g., 500mg") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5B9BD5),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = stock,
                            onValueChange = { stock = it },
                            label = { Text("Stock") },
                            placeholder = { Text("30") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF5B9BD5),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )

                        OutlinedTextField(
                            value = minStock,
                            onValueChange = { minStock = it },
                            label = { Text("Min. Stock") },
                            placeholder = { Text("5") },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF5B9BD5),
                                unfocusedBorderColor = Color(0xFFE0E0E0)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Medication Type Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Medication Type",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    medicineTypes.chunked(2).forEach { rowTypes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowTypes.forEach { type ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(24.dp))
                                        .background(
                                            if (selectedType == type) Color(0xFF5B9BD5)
                                            else Color(0xFFF0F0F0)
                                        )
                                        .clickable { selectedType = type }
                                        .border(
                                            width = if (selectedType == type) 2.dp else 0.dp,
                                            color = if (selectedType == type) Color(0xFF5B9BD5) else Color.Transparent,
                                            shape = RoundedCornerShape(24.dp)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type,
                                        color = if (selectedType == type) Color.White else Color.Gray,
                                        fontWeight = if (selectedType == type) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                            if (rowTypes.size < 2) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Notes Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Notes (Optional)",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("e.g., Take after meals") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5B9BD5),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Add Button
            Button(
                onClick = {
                    viewModel.addMedicine(
                        context = context,
                        name = name,
                        type = selectedType,
                        dosage = dosage,
                        stock = stock.toIntOrNull() ?: 0,
                        minStock = minStock.toIntOrNull() ?: 0,
                        notes = notes.ifBlank { null }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5B9BD5)),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading && name.isNotBlank() && dosage.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text(text = "Add Medication", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Snackbar host at the bottom
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
            SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(16.dp))
        }
    }
}
