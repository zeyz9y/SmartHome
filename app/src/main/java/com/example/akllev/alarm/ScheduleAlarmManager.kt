@file:Suppress("SpellCheckingInspection")

package com.example.akllev.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.akllev.model.HeatingSchedule
import java.util.Calendar
import kotlin.math.absoluteValue
import android.os.Build
import android.util.Log
import java.util.Date


object HeatingActions {
    const val ACTION_HEATING_ON  = "com.example.akllev.HEATING_ON"
    const val ACTION_HEATING_OFF = "com.example.akllev.HEATING_OFF"
    const val EXTRA_SCHEDULE_ID  = "schedule_id"
    const val EXTRA_TARGET_TEMP  = "target_temp"
}

class ScheduleAlarmManager(private val ctx: Context) {
    private val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun previewNextTimes(s: HeatingSchedule): Pair<Long?, Long?> {
        // Zamanlayıcı kapalıysa ya da gün seçili değilse null döndür
        if (!s.isEnabled || s.daysOfWeek.isEmpty()) return null to null
        val start = nextOccurrence(s, true).timeInMillis
        val end   = nextOccurrence(s, false).timeInMillis
        return start to end
    }


    fun schedule(schedule: HeatingSchedule) {
        if (!schedule.isEnabled) { cancel(schedule); return }

        // Başlangıç alarmı: targetTemp'i ilet
        setExactAt(
            nextOccurrence(schedule, isStart = true),
            HeatingActions.ACTION_HEATING_ON,
            schedule.id,
            1,
            schedule.targetTemp                      // <-- geç
        )
        // Bitiş alarmı: targetTemp gerekmiyor
        setExactAt(
            nextOccurrence(schedule, isStart = false),
            HeatingActions.ACTION_HEATING_OFF,
            schedule.id,
            2,
            null                                     // <-- boş
        )
    }



    fun scheduleAll(list: List<HeatingSchedule>) = list.forEach { schedule(it) }

    fun cancel(schedule: HeatingSchedule) {
        listOf(1, 2).forEach { salt ->
            val pi = pending(schedule.id,
                if (salt == 1) HeatingActions.ACTION_HEATING_ON else HeatingActions.ACTION_HEATING_OFF,
                salt
            )
            am.cancel(pi)
        }
    }



    private fun setExactAt(
        cal: Calendar,
        action: String,
        id: String,
        salt: Int,
        targetTemp: Float?
    ) {
        val t = cal.timeInMillis
        val canExact = if (Build.VERSION.SDK_INT >= 31) am.canScheduleExactAlarms() else true
        Log.d("ScheduleAlarmMgr", "schedule ${action.substringAfterLast('.')} at ${Date(t)} id=$id exact=$canExact")

        val pi = pending(id, action, salt, targetTemp)   // <-- temp'i geçir
        if (Build.VERSION.SDK_INT >= 31 && !canExact) {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, pi)
            Log.d("ScheduleAlarmMgr", "-> used setAndAllowWhileIdle (fallback)")
        } else {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, pi)
            Log.d("ScheduleAlarmMgr", "-> used setExactAndAllowWhileIdle")
        }
    }



    private fun pending(
        id: String,
        action: String,
        salt: Int,
        targetTemp: Float? = null      // <-- YENİ parametre
    ): PendingIntent {
        val intent = Intent(ctx, HeatingActionReceiver::class.java).apply {
            this.action = action
            putExtra(HeatingActions.EXTRA_SCHEDULE_ID, id)
            // Sadece ON için anlamlı; OFF'ta null gelir, sorun değil
            targetTemp?.let { putExtra(HeatingActions.EXTRA_TARGET_TEMP, it) }
        }
        val req = (id.hashCode() * 31 + action.hashCode() + salt).absoluteValue
        return PendingIntent.getBroadcast(
            ctx, req, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }



    private fun nextOccurrence(s: HeatingSchedule, isStart: Boolean): Calendar {
        val now = Calendar.getInstance()
        val h = if (isStart) s.startHour else s.endHour
        val m = if (isStart) s.startMinute else s.endMinute
        Log.d("ScheduleAlarmMgr", "nextOccurrence(isStart=$isStart) now=${Date(now.timeInMillis)} target=$h:$m days=${s.daysOfWeek}")

        for (d in 0..7) {
            val c = Calendar.getInstance().apply {
                timeInMillis = now.timeInMillis
                set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                add(Calendar.DAY_OF_YEAR, d)
            }
            val dow = c.get(Calendar.DAY_OF_WEEK)
            if (dow in s.daysOfWeek) {
                c.set(Calendar.HOUR_OF_DAY, h)
                c.set(Calendar.MINUTE, m)
                if (c.after(now)) {
                    Log.d("ScheduleAlarmMgr", "-> picked ${Date(c.timeInMillis)} (DOW=$dow, d=$d)")
                    return c
                }
            }
        }
        return Calendar.getInstance().apply { add(Calendar.MINUTE, 1) }
    }
}
