package com.example.akllev.util

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

object ExactAlarmPermission {
    /** true => izni var; false => ayar sayfasını açar ve false döner */
    fun ensure(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < 31) return true
        val am = context.getSystemService(AlarmManager::class.java)
        val can = am.canScheduleExactAlarms()
        if (can) return true

        // Kullanıcıyı özel izin sayfasına götür
        val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            // Bazı cihazlarda NEW_TASK gerekli
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
        return false
    }
}
