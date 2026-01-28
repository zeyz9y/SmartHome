@file:Suppress("SpellCheckingInspection")

package com.example.akllev.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.akllev.util.DeviceApi
import com.example.akllev.util.Notifier

class HeatingActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val scheduleId = intent.getStringExtra(HeatingActions.EXTRA_SCHEDULE_ID)
        val targetTemp = intent.getFloatExtra(HeatingActions.EXTRA_TARGET_TEMP, Float.NaN)
        val tempValue = if (targetTemp.isNaN()) null else targetTemp

        val notifier = Notifier(context)

        when (intent.action) {
            HeatingActions.ACTION_HEATING_ON -> {
                DeviceApi.heatingOn(context, tempValue)
                notifier.notifySystem(
                    "Isıtma Açıldı",
                    "Plan ID: $scheduleId — Hedef sıcaklık: ${tempValue ?: "-"}°C"
                )
            }
            HeatingActions.ACTION_HEATING_OFF -> {
                DeviceApi.heatingOff(context)
                notifier.notifySystem(
                    "Isıtma Kapatıldı",
                    "Plan ID: $scheduleId — Planlanan bitiş zamanı."
                )
            }
        }
    }
}
