package com.example.akllev.ui.components.schedule

import android.app.TimePickerDialog
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun EditScheduleDialog(
    initialStartHour: Int,
    initialStartMinute: Int,
    initialEndHour: Int,
    initialEndMinute: Int,
    initialDays: Set<Int>,
    initialTargetTemp: Float?,
    onDismiss: () -> Unit,
    onSave: (startH: Int, startM: Int, endH: Int, endM: Int, days: Set<Int>, targetTemp: Float) -> Unit
) {
    val ctx = LocalContext.current

    var startHour by remember { mutableIntStateOf(initialStartHour) }
    var startMinute by remember { mutableIntStateOf(initialStartMinute) }
    var endHour by remember { mutableIntStateOf(initialEndHour) }
    var endMinute by remember { mutableIntStateOf(initialEndMinute) }
    var days by remember { mutableStateOf(initialDays.toSet()) }
    var targetTemp by remember { mutableFloatStateOf(initialTargetTemp ?: 22f) }

    fun pickStart() {
        TimePickerDialog(ctx, { _, h, m -> startHour = h; startMinute = m }, startHour, startMinute, true).show()
    }
    fun pickEnd() {
        TimePickerDialog(ctx, { _, h, m -> endHour = h; endMinute = m }, endHour, endMinute, true).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Zamanlayıcıyı Düzenle") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { pickStart() }) {
                        Text("Başlangıç: %02d:%02d".format(startHour, startMinute))
                    }
                    OutlinedButton(onClick = { pickEnd() }) {
                        Text("Bitiş: %02d:%02d".format(endHour, endMinute))
                    }
                }

                Text("Günler")
                DaysOfWeekChips(
                    selected = days,
                    onToggleDay = { day ->
                        days = if (day in days) days - day else days + day
                    }
                )

                Text("Hedef Sıcaklık: ${"%.0f".format(targetTemp)}°C")
                Slider(
                    value = targetTemp,
                    onValueChange = { targetTemp = it },
                    valueRange = 16f..28f,
                    steps = (28 - 16) - 1
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val sameTime = (startHour == endHour && startMinute == endMinute)
                if (sameTime) {
                    Toast.makeText(ctx, "Başlangıç ve bitiş aynı olamaz.", Toast.LENGTH_SHORT).show()
                    return@TextButton
                }
                onSave(startHour, startMinute, endHour, endMinute, days.toSet(), targetTemp)
            }) { Text("Kaydet") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("İptal") }
        }
    )
}
