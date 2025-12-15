package com.wesley.medcare.ui.view.Medicine

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun MedicineCard(
//    icon: Int,
//    description: String,
//    value: String
) {
    Card(
        modifier = Modifier
            .size(width = 105.dp, height = 135.dp)
    ) {

    }
}

@Preview
@Composable
private fun MedicineCardPreview() {
    MedicineCard(
//        icon = R.drawable.icon_humidity,
//        description = "HUMIDITY",
//        value = "49%"
    )
}