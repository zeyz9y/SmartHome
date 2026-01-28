package com.example.akllev.ui.components.schedule

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.akllev.model.HeatingSchedule

@Composable
fun ScheduleCard(
    schedule: HeatingSchedule,
    nextStartText: String?, // ekranda formatlanıp veriliyor
    nextEndText: String?,
    onToggle: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "%02d:%02d — %02d:%02d".format(
                        schedule.startHour, schedule.startMinute,
                        schedule.endHour, schedule.endMinute
                    ),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Switch(checked = schedule.isEnabled, onCheckedChange = onToggle)
            }

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Günler: " + schedule.daysOfWeek.map { dayShort(it) }.joinToString(" "),
                    style = MaterialTheme.typography.bodyMedium
                )
                schedule.targetTemp?.let {
                    Text("Hedef: ${"%.0f".format(it)}°C", style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "Sonraki başlangıç: " + (nextStartText ?: "—"),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                "Sonraki bitiş: " + (nextEndText ?: "—"),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onEdit) { Text("Düzenle") }
                Spacer(Modifier.width(4.dp))
                TextButton(onClick = onDelete) { Text("Sil") }
            }
        }
    }
}

private fun dayShort(d: Int) = when (d) {
    java.util.Calendar.MONDAY -> "Pzt"
    java.util.Calendar.TUESDAY -> "Sal"
    java.util.Calendar.WEDNESDAY -> "Çar"
    java.util.Calendar.THURSDAY -> "Per"
    java.util.Calendar.FRIDAY -> "Cum"
    java.util.Calendar.SATURDAY -> "Cmt"
    java.util.Calendar.SUNDAY -> "Paz"
    else -> d.toString()
}
