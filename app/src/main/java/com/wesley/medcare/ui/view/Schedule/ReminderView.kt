package com.wesley.medcare.ui.view.Schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wesley.medcare.ui.route.AppView

@Composable
fun ReminderView(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

    FloatingActionButton(
        onClick = {
            navController.navigate(AppView.AddReminderView.name)
        },
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(16.dp),
        containerColor = Color(0xFF2F93FF),
        contentColor = Color.White
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Medicine"
        )
    }
}
@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun ReminderViewPreview() {
    ReminderView()
}