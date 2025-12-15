package com.wesley.medcare.ui.view.Schedule

import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun ReminderView(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {

}

@Composable
@Preview(showBackground = true, showSystemUi = true)
private fun ReminderViewPreview() {
    ReminderView()
}