package com.example.akllev.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.akllev.repository.ScheduleStore
import android.util.Log


class BootReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val list = ScheduleStore(ctx).getAll()
            Log.d("BootReceiver", "BOOT_COMPLETED, rescheduling count=${list.size}")
            ScheduleAlarmManager(ctx).scheduleAll(list)
        }
    }
}
