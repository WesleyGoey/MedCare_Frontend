// kotlin
package com.wesley.medcare.ui.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun BackTopAppBar(
    title: String = "Back",
    onBack: () -> Unit                                                                                                                                                                                                      
) {
    // Use a Surface + Row so we can control exact paddings/height.
    Surface(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
                .height(60.dp)
                .padding(start = 8.dp, top = 6.dp, bottom = 6.dp, end = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .size(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = null,
                    tint = Color(0xFF457AF9)
                )
            }

            Spacer(Modifier.width(4.dp))


            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF457AF9),
                modifier = Modifier
                    .padding(top = 2.dp)
                    .clickable(onClick = onBack)
            )
        }
    }
}
