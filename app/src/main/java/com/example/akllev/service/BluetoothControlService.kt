package com.example.akllev.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.akllev.R
import com.example.akllev.alarm.HeatingActions
import com.example.akllev.bt.BluetoothTransport
import com.example.akllev.util.Notifier
import kotlinx.coroutines.*

class BluetoothControlService : Service() {

    companion object {
        private const val CH_ID = "control"
        private const val FG_ID = 42
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    override fun onCreate() {
        super.onCreate()
        ensureChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Foreground'a geç (Android 8+ zorunlu)
        startForeground(FG_ID, buildOngoing("Komut gönderiliyor..."))

        scope.launch {
            try {
                val t = BluetoothTransport.get(this@BluetoothControlService)

                when (intent?.action) {
                    HeatingActions.ACTION_HEATING_ON -> {
                        val raw = intent.getFloatExtra(HeatingActions.EXTRA_TARGET_TEMP, Float.NaN)
                        val target = if (raw.isNaN()) null else raw

                        val connected = t.connectIfNeeded()
                        if (!connected) {
                            Notifier(this@BluetoothControlService)
                                .notifySystem("BT Bağlantı Yok", "Kayıtlı cihaza bağlanılamadı.")
                        } else {
                            t.heatingSet(true)
                            target?.let { t.writeTargetTemp(it) }
                            Notifier(this@BluetoothControlService)
                                .notifySystem("Isıtma Açıldı", "Servis → Bluetooth (LED=ON)")
                        }
                    }
                    HeatingActions.ACTION_HEATING_OFF -> {
                        val connected = t.connectIfNeeded()
                        if (!connected) {
                            Notifier(this@BluetoothControlService)
                                .notifySystem("BT Bağlantı Yok", "Kayıtlı cihaza bağlanılamadı.")
                        } else {
                            t.heatingSet(false)
                            Notifier(this@BluetoothControlService)
                                .notifySystem("Isıtma Kapatıldı", "Servis → Bluetooth (LED=OFF)")
                        }
                    }
                    else -> Log.w("CtrlService", "Bilinmeyen action: ${intent?.action}")
                }
            } catch (e: Exception) {
                Log.e("CtrlService", "Komut hatası", e)
                Notifier(this@BluetoothControlService)
                    .notifySystem("Komut Hatası", e.message ?: "Bilinmeyen hata")
            } finally {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf(startId)
            }
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (nm.getNotificationChannel(CH_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(CH_ID, "Cihaz Kontrol", NotificationManager.IMPORTANCE_LOW)
                )
            }
        }
    }

    private fun buildOngoing(text: String): Notification {
        return NotificationCompat.Builder(this, CH_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Akıllı Ev")
            .setContentText(text)
            .setOngoing(true)
            .build()
    }
}
