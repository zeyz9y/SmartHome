package com.example.akllev.ui.components.schedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import java.util.Calendar

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DaysOfWeekChips(
    selected: Set<Int>,
    onToggleDay: (Int) -> Unit
) {
    val days = listOf(
        Calendar.MONDAY to "Pzt",
        Calendar.TUESDAY to "Sal",
        Calendar.WEDNESDAY to "Çar",
        Calendar.THURSDAY to "Per",
        Calendar.FRIDAY to "Cum",
        Calendar.SATURDAY to "Cmt",
        Calendar.SUNDAY to "Paz",
    )

    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEach { (dayConst, label) ->
            FilterChip(
                selected = dayConst in selected,
                onClick   = { onToggleDay(dayConst) },
                label     = { Text(label) }
            )
        }
    }
}
