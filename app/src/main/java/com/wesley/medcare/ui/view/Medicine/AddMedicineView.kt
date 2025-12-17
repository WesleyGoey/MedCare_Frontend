package com.wesley.medcare.ui.view.Medicine

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.wesley.medcare.ui.view.components.BackTopAppBar
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineView(
    navController: NavHostController,
    viewModel: MedicineViewModel = viewModel()
) {
    val context = LocalContext.current
    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("") }
    var minStockText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Tablet") }
    var notes by remember { mutableStateOf("") }

    val medicineTypes = listOf("Tablet", "Capsule", "Syrup", "Drops", "Ointment", "Patch", "Custom Type")

    LaunchedEffect(Unit) {
        // Reset state setiap kali view dibuka
        viewModel.resetState()
    }

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = { BackTopAppBar(onBack = { navController.navigateUp() }, title = "Add Medication") },
        content = { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("Add a new medication to your list", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                }

                // Basic Information Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Basic Information", style = MaterialTheme.typography.titleMedium)

                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                placeholder = { Text("e.g., Paracetamol") },
                                label = { Text("Medication Name") },
                                singleLine = true,
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = dosage,
                                onValueChange = { dosage = it },
                                placeholder = { Text("e.g., 500mg") },
                                label = { Text("Dosage") },
                                singleLine = true,
                                enabled = !isLoading,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedTextField(
                                    value = stockText,
                                    onValueChange = { stockText = it.filter { ch -> ch.isDigit() } },
                                    placeholder = { Text("30") },
                                    label = { Text("Stock") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    enabled = !isLoading,
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = minStockText,
                                    onValueChange = { minStockText = it.filter { ch -> ch.isDigit() } },
                                    placeholder = { Text("5") },
                                    label = { Text("Min. Stock") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    enabled = !isLoading,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }

                // Medicine Type list
                item {
                    Text("Medication Type", style = MaterialTheme.typography.titleSmall)
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        medicineTypes.forEach { type ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedType = type },
                                shape = RoundedCornerShape(12.dp),
                                colors = if (selectedType == type)
                                    CardDefaults.cardColors(containerColor = Color(0xFF2F93FF))
                                else
                                    CardDefaults.cardColors(containerColor = Color(0xFFF5F7FA))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = type,
                                        color = if (selectedType == type) Color.White else Color(0xFF111827)
                                    )
                                    Spacer(Modifier.weight(1f))
                                    if (selectedType == type) {
                                        Icon(Icons.Filled.ArrowBack, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                // Notes (optional)
                item {
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("e.g., Take after meals") },
                        label = { Text("Notes (Optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        enabled = !isLoading,
                        colors = TextFieldDefaults.colors()
                    )
                }

                // Photo upload (optional)
                item {
                    Text("Medication Photo (Optional)", style = MaterialTheme.typography.titleSmall)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clickable {
                                Toast.makeText(context, "Photo picker not implemented in this stub", Toast.LENGTH_SHORT).show()
                            },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Outlined.CameraAlt, contentDescription = null, tint = Color(0xFF2F93FF), modifier = Modifier.size(36.dp))
                                Spacer(Modifier.height(6.dp))
                                Text("Upload Photo", color = Color(0xFF2F93FF))
                                Text("Tap to select medication photo", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Add Medication button
                item {
                    val stock = stockText.toIntOrNull()
                    val minStock = minStockText.toIntOrNull()
                    val formValid = name.isNotBlank() && dosage.isNotBlank() && stock != null && minStock != null

                    Button(
                        onClick = {
                            if (!formValid) {
                                Toast.makeText(context, "Please fill all required fields", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            viewModel.addMedicine(
                                name = name.trim(),
                                type = selectedType,
                                dosage = dosage.trim(),
                                stock = stock!!,
                                minStock = minStock!!,
                                notes = if (notes.isBlank()) null else notes.trim()
                            )
                        },
                        enabled = !isLoading && formValid,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2F93FF)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Add Medication", color = Color.White)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    )
}
