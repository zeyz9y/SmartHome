package com.example.akllev.ui.screens

import android.app.TimePickerDialog
import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.akllev.alarm.ScheduleAlarmManager
import com.example.akllev.model.HeatingSchedule
import com.example.akllev.repository.ScheduleStore
import com.example.akllev.ui.components.schedule.EditScheduleDialog
import com.example.akllev.ui.components.schedule.ScheduleCard
import com.example.akllev.ui.theme.spacing
import com.example.akllev.util.ExactAlarmPermission
import java.util.Calendar
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulesScreen(
    onNavigateBack: (() -> Unit)? = null
) {
    val ctx = LocalContext.current
    val store = remember { ScheduleStore(ctx) }
    var items by remember { mutableStateOf(store.getAll()) }
    var editing by remember { mutableStateOf<HeatingSchedule?>(null) }

    fun refresh() { items = store.getAll() }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Isıtma Zamanlayıcı") },
                navigationIcon = {
                    if (onNavigateBack != null) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    createScheduleDialog(ctx) { newItem ->
                        val hasExact = ExactAlarmPermission.ensure(ctx)
                        Log.d("SchedulesScreen", "createSchedule id=${newItem.id} exactPerm=$hasExact at=${Date()}")
                        store.upsert(newItem)
                        ScheduleAlarmManager(ctx).schedule(newItem)
                        refresh()
                        Toast.makeText(ctx, "Zamanlayıcı eklendi", Toast.LENGTH_SHORT).show()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) { Icon(Icons.Filled.Add, contentDescription = "Yeni Zamanlayıcı") }
        }
    ) { padding ->
        val tf = remember { DateFormat.getTimeFormat(ctx) }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(MaterialTheme.spacing.l),
            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.m)
        ) {
            if (items.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = MaterialTheme.spacing.xl * 2),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Henüz zamanlayıcı eklenmedi",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            items(items, key = { it.id }) { s ->
                val (nextStartMs, nextEndMs) = remember(
                    s.id, s.startHour, s.startMinute, s.endHour, s.endMinute, s.daysOfWeek, s.isEnabled
                ) { ScheduleAlarmManager(ctx).previewNextTimes(s) }
                val nextStart = nextStartMs?.let { tf.format(Date(it)) }
                val nextEnd   = nextEndMs?.let { tf.format(Date(it)) }

                ScheduleCard(
                    schedule = s,
                    nextStartText = nextStart,
                    nextEndText = nextEnd,
                    onToggle = { enabled ->
                        val updated = s.copy(isEnabled = enabled)
                        store.upsert(updated)
                        val mgr = ScheduleAlarmManager(ctx)
                        if (enabled) mgr.schedule(updated) else mgr.cancel(updated)
                        refresh()
                    },
                    onEdit = { editing = s },
                    onDelete = {
                        ScheduleAlarmManager(ctx).cancel(s)
                        store.delete(s.id)
                        refresh()
                    }
                )
            }
        }
    }

    editing?.let { current ->
        EditScheduleDialog(
            initialStartHour = current.startHour,
            initialStartMinute = current.startMinute,
            initialEndHour = current.endHour,
            initialEndMinute = current.endMinute,
            initialDays = current.daysOfWeek,
            initialTargetTemp = current.targetTemp,
            onDismiss = { editing = null },
            onSave = { sh, sm, eh, em, days, temp ->
                val mgr = ScheduleAlarmManager(ctx)
                mgr.cancel(current)
                val updated = current.copy(
                    startHour = sh, startMinute = sm,
                    endHour = eh, endMinute = em,
                    daysOfWeek = days,
                    targetTemp = temp
                )
                store.upsert(updated)
                mgr.schedule(updated)
                refresh()
                editing = null
                Toast.makeText(ctx, "Zamanlayıcı güncellendi", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

/* --- yardımcı: hızlı yaratım diyaloğu ekran sorumluluğunda kalsın --- */
private fun createScheduleDialog(
    ctx: Context,
    onCreate: (HeatingSchedule) -> Unit
) {
    val now = Calendar.getInstance()
    var sh = now.get(Calendar.HOUR_OF_DAY)
    var sm = now.get(Calendar.MINUTE)
    var eh = (sh + 1) % 24
    var em = sm

    TimePickerDialog(ctx, { _, h, m ->
        sh = h; sm = m
        TimePickerDialog(ctx, { _, h2, m2 ->
            eh = h2; em = m2
            val defaultDays = setOf(
                Calendar.MONDAY, Calendar.TUESDAY, Calendar.WEDNESDAY,
                Calendar.THURSDAY, Calendar.FRIDAY
            )
            onCreate(
                HeatingSchedule(
                    startHour = sh, startMinute = sm,
                    endHour = eh, endMinute = em,
                    daysOfWeek = defaultDays,
                    targetTemp = 22f
                )
            )
        }, eh, em, true).show()
    }, sh, sm, true).show()
}
