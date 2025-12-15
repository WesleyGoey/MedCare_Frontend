package com.wesley.medcare.ui.view.Medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wesley.medcare.ui.viewmodel.MedicineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineView(
    onBack: () -> Unit = {},
    viewModel: MedicineViewModel = viewModel()
) {
    var nameState by remember { mutableStateOf("") }
    var dosageState by remember { mutableStateOf("") }
    var stockState by remember { mutableStateOf("") }
    var minStockState by remember { mutableStateOf("") }
    var notesState by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf("Tablet") }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val addSuccess by viewModel.addSuccess.collectAsState()

    LaunchedEffect(addSuccess) {
        if (addSuccess) {
            viewModel.resetAddSuccess()
            onBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Medicine") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            OutlinedTextField(
                value = nameState,
                onValueChange = { nameState = it },
                label = { Text("Medicine Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = dosageState,
                onValueChange = { dosageState = it },
                label = { Text("Dosage") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = stockState,
                onValueChange = { stockState = it },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = minStockState,
                onValueChange = { minStockState = it },
                label = { Text("Minimum Stock") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = notesState,
                onValueChange = { notesState = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(text = "Type: $selectedType")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Tablet", "Capsule", "Syrup", "Injection").forEach { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Button(
                onClick = {
                    viewModel.saveMedicineForm(
                        name = nameState,
                        dosage = dosageState,
                        stockStr = stockState,
                        minStockStr = minStockState,
                        type = selectedType,
                        notes = notesState
                    )
                },
                enabled = !isLoading && nameState.isNotBlank() && dosageState.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color(0xFF6200EA), Color(0xFF03DAC5))
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White)
                    } else {
                        Text("Add Medicine", color = Color.White)
                    }
                }
            }
        }
    }
}
