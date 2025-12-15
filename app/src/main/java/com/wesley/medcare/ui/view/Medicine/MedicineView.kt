package com.wesley.medcare.ui.view.Medicine

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController

@Composable
fun MedicineView(
    navController: NavHostController
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Medicine") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("AddMedicineView") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add medicine")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Medicine list placeholder")
            // TODO: list medicines here
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun MedicineViewPreview() {
    val navController = rememberNavController()
    MedicineView(navController)
}
