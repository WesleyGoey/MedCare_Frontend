package com.wesley.medcare.ui.view.Medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.wesley.medcare.ui.view.components.BackTopAppBar
import com.wesley.medcare.ui.viewmodel.MedicineViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineView(
    navController: NavHostController,
    viewModel: MedicineViewModel = viewModel()
) {
    val name by viewModel.medicineName.collectAsState()
    val dosage by viewModel.dosage.collectAsState()
    val stock by viewModel.stock.collectAsState()
    val minStock by viewModel.minStock.collectAsState()
    val medType by viewModel.medicineType.collectAsState()
    val notes by viewModel.notes.collectAsState()

    val isLoading by viewModel.isLoading.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var stockText by remember { mutableStateOf(stock?.toString() ?: "") }
    var minStockText by remember { mutableStateOf(minStock?.toString() ?: "") }

    // keep local strings in sync when ViewModel updates (e.g., editing existing medication)
    LaunchedEffect(stock) {
        if (stock != null && stockText != stock.toString()) stockText = stock.toString()
        if (stock == null && stockText.isNotEmpty()) stockText = ""
    }
    LaunchedEffect(minStock) {
        if (minStock != null && minStockText != minStock.toString()) minStockText = minStock.toString()
        if (minStock == null && minStockText.isNotEmpty()) minStockText = ""
    }

    LaunchedEffect(Unit) {
        viewModel.resetState()
    }

    LaunchedEffect(successMessage) {
        if (!successMessage.isNullOrEmpty()) {
            navController.previousBackStackEntry?.savedStateHandle?.set("refreshMedicines", true)
            viewModel.resetSuccessMessage()
            navController.popBackStack()
        }
    }

    val medicineTypes =
        listOf("Tablet", "Capsule", "Syrup", "Drops", "Ointment", "Patch", "Custom Type")
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(errorMessage) {
        errorMessage?.let { msg ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(msg)
                viewModel.resetState()
            }
        }
    }
    Scaffold(
        topBar = {
            BackTopAppBar(onBack = { navController.navigateUp() })
        }
    ) { paddingValues ->
        // Kotlin
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Basic Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2C3E50)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        TextField(
                            value = name,
                            onValueChange = { viewModel.setMedicineName(it) },
                            placeholder = { Text("e.g., Paracetamol", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF3F5F7),
                                unfocusedContainerColor = Color(0xFFF3F5F7),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color(0xFF2F93FF)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        TextField(
                            value = dosage,
                            onValueChange = { viewModel.setDosage(it) },
                            placeholder = { Text("e.g., 500mg", color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF3F5F7),
                                unfocusedContainerColor = Color(0xFFF3F5F7),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color(0xFF2F93FF)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            TextField(
                                value = stockText,
                                onValueChange = { new ->
                                    val filtered = new.filter { it.isDigit() }
                                    stockText = filtered
                                    viewModel.setStock(filtered.toIntOrNull())
                                },
                                placeholder = { Text("30", color = Color.Gray)},
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF3F5F7),
                                    unfocusedContainerColor = Color(0xFFF3F5F7),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    cursorColor = Color(0xFF2F93FF)
                                )
                            )

                            TextField(
                                value = minStockText,
                                onValueChange = { new ->
                                    val filtered = new.filter { it.isDigit() }
                                    minStockText = filtered
                                    viewModel.setMinStock(filtered.toIntOrNull())
                                },
                                placeholder = { Text("5", color = Color.Gray) },
                                singleLine = true,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color(0xFFF3F5F7),
                                    unfocusedContainerColor = Color(0xFFF3F5F7),
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = Color.Black,
                                    unfocusedTextColor = Color.Black,
                                    cursorColor = Color(0xFF2F93FF)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Medication Type (single column pills with check)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Medication Type",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2C3E50)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Column {
                            medicineTypes.forEach { t ->
                                val selected = medType == t
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .height(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (selected) Color(0xFF2F93FF) else Color(0xFFF3F5F7))
                                        .clickable { viewModel.setMedicineType(t) }
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = t,
                                        color = if (selected) Color.White else Color(0xFF4B4B4B),
                                        fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                                        modifier = Modifier.weight(1f)
                                    )
                                    if (selected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Notes Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Notes (Optional)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF2C3E50)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        TextField(
                            value = notes ?: "",
                            onValueChange = { viewModel.setNotes(it) },
                            placeholder = { Text("e.g., Take after meals", color = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFFF3F5F7),
                                unfocusedContainerColor = Color(0xFFF3F5F7),
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color(0xFF2F93FF)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Gradient Add button (uses viewModel.addMedicine)
                val enabled = !isLoading && name.isNotBlank() && dosage.isNotBlank()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            brush = if (enabled) Brush.horizontalGradient(listOf(Color(0xFF4F8FFB), Color(0xFF2563EB))) else Brush.horizontalGradient(listOf(Color(0xFFBFCFEF), Color(0xFF9FAFE0)))
                        )
                        .clickable(enabled = enabled) {
                            if (enabled) viewModel.addMedicine()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Add Medication", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }

            // Snackbar host pinned to bottom
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
                SnackbarHost(hostState = snackbarHostState, modifier = Modifier.padding(16.dp))
            }
        }
    }
}
@Preview
@Composable
fun AddMedicineViewPreview() {
    AddMedicineView(navController = NavHostController(LocalContext.current))
}

