package com.wesley.medcare.ui.view.Medicine

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.wesley.medcare.ui.view.components.BackTopAppBar
import com.wesley.medcare.ui.viewmodel.MedicineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineView(
    navController: NavController,
    viewModel: MedicineViewModel = viewModel()
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    val medicineName by viewModel.medicineName.collectAsState()
    val dosage by viewModel.dosage.collectAsState()
    val stock by viewModel.stock.collectAsState()
    val minStock by viewModel.minStock.collectAsState()
    val selectedType by viewModel.medicineType.collectAsState()
    val notes by viewModel.notes.collectAsState()

    // State untuk kontrol custom type
    var isCustomSelected by remember { mutableStateOf(false) }
    var customTypeText by remember { mutableStateOf("") }

    var showStockInfo by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val medicineTypes = listOf("Tablet", "Capsule", "Syrup", "Drops", "Ointment", "Patch", "Custom Type")

    LaunchedEffect(Unit) { viewModel.clearForm() }

    LaunchedEffect(successMessage) {
        if (!successMessage.isNullOrEmpty()) {
            Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show()
            viewModel.resetSuccessMessage()
            navController.previousBackStackEntry?.savedStateHandle?.set("refreshMedicines", true)
            navController.popBackStack()
        }
    }

    LaunchedEffect(errorMessage) {
        if (!errorMessage.isNullOrEmpty()) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = { BackTopAppBar(title = "Back", onBack = { navController.popBackStack() }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            Text(text = "Add Medication", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
            Text(text = "Enter medication information", fontSize = 14.sp, color = Color(0xFF5F6368), modifier = Modifier.padding(bottom = 16.dp))

            // --- Basic Information Card ---
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Basic Information", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E), modifier = Modifier.padding(bottom = 16.dp))

                    Text(text = "Medication Name", fontSize = 14.sp, color = Color(0xFF5F6368), modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = medicineName,
                        onValueChange = { viewModel.setMedicineName(it) },
                        placeholder = { Text("e.g., Paracetamol", color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color(0xFF1A1A2E),
                            unfocusedTextColor = Color(0xFF1A1A2E),
                            cursorColor = Color(0xFF457AF9)
                        )
                    )

                    Text(text = "Dosage", fontSize = 14.sp, color = Color(0xFF5F6368), modifier = Modifier.padding(bottom = 8.dp))
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = { viewModel.setDosage(it) },
                        placeholder = { Text("e.g., 500mg", color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color(0xFF1A1A2E),
                            unfocusedTextColor = Color(0xFF1A1A2E),
                            cursorColor = Color(0xFF457AF9)
                        )
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Stock", fontSize = 14.sp, color = Color(0xFF5F6368), modifier = Modifier.padding(bottom = 8.dp))
                            OutlinedTextField(
                                value = stock?.toString() ?: "",
                                onValueChange = { viewModel.setStock(it.toIntOrNull()) },
                                placeholder = { Text("30", color = Color(0xFF9CA3AF)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF5F5F5),
                                    unfocusedContainerColor = Color(0xFFF5F5F5),
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = Color(0xFF1A1A2E),
                                    unfocusedTextColor = Color(0xFF1A1A2E),
                                    cursorColor = Color(0xFF457AF9)
                                )
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                                Text(text = "Min. Stock", fontSize = 14.sp, color = Color(0xFF5F6368))
                                Box {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "Info",
                                        tint = Color(0xFF457AF9),
                                        modifier = Modifier.size(20.dp).padding(start = 4.dp).clickable { showStockInfo = !showStockInfo }
                                    )
                                    if (showStockInfo) {
                                        Popup(
                                            alignment = Alignment.TopStart,
                                            offset = IntOffset(x = -255, y = 220),
                                            onDismissRequest = { showStockInfo = false }
                                        ) {
                                            Card(
                                                colors = CardDefaults.cardColors(containerColor = Color(0xFF457AF9)),
                                                shape = RoundedCornerShape(16.dp),
                                                modifier = Modifier.width(170.dp),
                                                elevation = CardDefaults.cardElevation(8.dp)
                                            ) {
                                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                                                    Icon(Icons.Default.Info, null, tint = Color.White, modifier = Modifier.size(16.dp).padding(top = 2.dp))
                                                    Spacer(Modifier.width(8.dp))
                                                    Text(
                                                        text = "When stock reaches this number, you'll receive a low stock alert notification.",
                                                        color = Color.White,
                                                        fontSize = 12.sp,
                                                        lineHeight = 16.sp,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            OutlinedTextField(
                                value = minStock?.toString() ?: "",
                                onValueChange = { viewModel.setMinStock(it.toIntOrNull()) },
                                placeholder = { Text("5", color = Color(0xFF9CA3AF)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF5F5F5),
                                    unfocusedContainerColor = Color(0xFFF5F5F5),
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent,
                                    focusedTextColor = Color(0xFF1A1A2E),
                                    unfocusedTextColor = Color(0xFF1A1A2E),
                                    cursorColor = Color(0xFF457AF9)
                                )
                            )
                        }
                    }
                }
            }

            // --- Medication Type Card ---
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Medication Type", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E), modifier = Modifier.padding(bottom = 12.dp))
                    medicineTypes.forEach { type ->
                        val isSelected = if (type == "Custom Type") isCustomSelected else (selectedType == type && !isCustomSelected)

                        Row(
                            modifier = Modifier.fillMaxWidth().clickable {
                                if (type == "Custom Type") {
                                    isCustomSelected = true
                                    viewModel.setMedicineType(customTypeText)
                                } else {
                                    isCustomSelected = false
                                    customTypeText = ""
                                    viewModel.setMedicineType(type)
                                }
                            }
                                .background(if (isSelected) Color(0xFF457AF9) else Color(0xFFF5F5F5), shape = RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = type, fontSize = 14.sp, color = if (isSelected) Color.White else Color(0xFF1A1A2E))
                            if (isSelected) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (isCustomSelected) {
                        OutlinedTextField(
                            value = customTypeText,
                            onValueChange = {
                                customTypeText = it
                                viewModel.setMedicineType(it)
                            },
                            placeholder = { Text("Enter custom medication type", color = Color(0xFF9CA3AF)) },
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF5F5F5),
                                unfocusedContainerColor = Color(0xFFF5F5F5),
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color(0xFF1A1A2E),
                                unfocusedTextColor = Color(0xFF1A1A2E),
                                cursorColor = Color(0xFF457AF9)
                            )
                        )
                    }
                }
            }

            // --- Notes Section ---
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                        Text(text = "Notes", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A1A2E))
                        Text(text = " (Optional)", fontSize = 14.sp, color = Color(0xFF5F6368))
                    }
                    OutlinedTextField(
                        value = notes ?: "",
                        onValueChange = { viewModel.setNotes(it) },
                        placeholder = { Text("e.g., Take after meals", color = Color(0xFF9CA3AF)) },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color(0xFF1A1A2E),
                            unfocusedTextColor = Color(0xFF1A1A2E),
                            cursorColor = Color(0xFF457AF9)
                        )
                    )
                }
            }

            Button(
                onClick = {
                    when {
                        medicineName.isBlank() -> Toast.makeText(context, "Medicine name is required", Toast.LENGTH_SHORT).show()
                        dosage.isBlank() -> Toast.makeText(context, "Dosage is required", Toast.LENGTH_SHORT).show()
                        stock == null -> Toast.makeText(context, "Please enter a valid stock", Toast.LENGTH_SHORT).show()
                        minStock == null -> Toast.makeText(context, "Please enter a valid minimum stock", Toast.LENGTH_SHORT).show()
                        isCustomSelected && customTypeText.isBlank() -> Toast.makeText(context, "Please enter custom type name", Toast.LENGTH_SHORT).show()
                        else -> viewModel.addMedicine()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF457AF9))
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                else Text(text = "Add Medicine", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
        }
    }
}