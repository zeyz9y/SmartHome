// app/src/main/java/com/example/akllev/util/DeviceApi.kt
package com.example.akllev.util

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.example.akllev.alarm.HeatingActions
import com.example.akllev.service.BluetoothControlService

object DeviceApi {

    enum class Transport { BLUETOOTH, TCP }

    private const val PREFS = "device_api_prefs"
    private const val KEY_TRANSPORT = "transport"

    fun getTransport(ctx: Context): Transport {
        val idx = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getInt(KEY_TRANSPORT, Transport.BLUETOOTH.ordinal)
        return Transport.entries.getOrElse(idx) { Transport.BLUETOOTH }
    }

    fun setTransport(ctx: Context, transport: Transport) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_TRANSPORT, transport.ordinal)
            .apply()
    }

    // --- Komutlar Foreground Service üzerinden çalışıyor ---
    fun heatingOn(ctx: Context, targetTemp: Float?) {
        val i = Intent(ctx, BluetoothControlService::class.java).apply {
            action = HeatingActions.ACTION_HEATING_ON
            targetTemp?.let { putExtra(HeatingActions.EXTRA_TARGET_TEMP, it) }
        }
        ContextCompat.startForegroundService(ctx, i)
    }

    fun heatingOff(ctx: Context) {
        val i = Intent(ctx, BluetoothControlService::class.java).apply {
            action = HeatingActions.ACTION_HEATING_OFF
        }
        ContextCompat.startForegroundService(ctx, i)
    }
}
