package com.wesley.medcare.ui.view.Medicine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AddMedicineView(
    onBack: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var minStock by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Basic Information", fontWeight = FontWeight.SemiBold)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Name e.g., Paracetamol") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dosage,
            onValueChange = { dosage = it },
            placeholder = { Text("Dosage e.g., 500mg") },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                placeholder = { Text("Stock") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = minStock,
                onValueChange = { minStock = it },
                placeholder = { Text("Min stock") },
                modifier = Modifier.weight(1f)
            )
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFEFF7FF)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "photo",
                        tint = Color(0xFF2F93FF)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text("Upload Photo (optional)", color = Color(0xFF2F93FF))
                }
            }
        }

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            placeholder = { Text("Notes (optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = { /* save action */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF2F93FF)
            )
        ) {
            Text("Add Medication", color = Color.White)
        }
    }
}
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddMedicinePreview() {
    AddMedicineView()
}
