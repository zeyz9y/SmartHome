package com.example.akllev.ui.components.dashboard

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.akllev.ui.components.common.InfoCard

@SuppressLint("DefaultLocale")
@Composable
fun DashboardTopCards(
    temperature: Number?,   // Float? / Double? fark etmez
    humidity:    Number?,
    energy:      String,
    modifier: Modifier = Modifier
) {
    val tempStr = temperature?.toFloat()?.let { String.format("%.1f°C", it) } ?: "--"
    val humStr  = humidity   ?.toFloat()?.let { String.format("%.1f%%", it) } ?: "--"

    Row(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        InfoCard(
            icon = Icons.Default.Thermostat,
            value = tempStr,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        InfoCard(
            icon = Icons.Default.WaterDrop,
            value = humStr,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
        InfoCard(
            icon = Icons.Default.Bolt,
            value = energy,
            color = Color.White,
            modifier = Modifier.weight(1f)
        )
    }
}
